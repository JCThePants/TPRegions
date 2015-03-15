/* This file is part of TPRegions for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.jcwhatever.bukkit.tpregions.regions;

import com.jcwhatever.bukkit.tpregions.DestinationLocation;
import com.jcwhatever.bukkit.tpregions.ITPDestination;
import com.jcwhatever.bukkit.tpregions.TPRegions;
import com.jcwhatever.bukkit.tpregions.Teleporter;
import com.jcwhatever.nucleus.regions.Region;
import com.jcwhatever.nucleus.regions.data.RegionShape;
import com.jcwhatever.nucleus.regions.data.RegionShape.Flatness;
import com.jcwhatever.nucleus.regions.data.RegionShape.FlatnessPosition;
import com.jcwhatever.nucleus.regions.data.RegionShape.ShapeDirection;
import com.jcwhatever.nucleus.regions.options.EnterRegionReason;
import com.jcwhatever.nucleus.regions.options.LeaveRegionReason;
import com.jcwhatever.nucleus.regions.selection.IRegionSelection;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.LocationUtils;
import com.jcwhatever.nucleus.utils.MetaKey;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Scheduler;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * A teleport region.
 *
 * <p>If the sending {@link ITPDestination} is a {@link IRegionSelection}, the player
 * is teleported to the coordinates that are the same as the {@link TPRegion} coordinates, relative to
 * the regions center.</p>
 *
 * <p>Where player Yaw is modified, the players yaw is rotated and the coordinates are rotated
 * around the regions center point.</p>
 */
public class TPRegion extends Region implements ITPDestination {

    public static final MetaKey<TPRegion> REGION_META_KEY = new MetaKey<TPRegion>(TPRegion.class);

    private ITPDestination _destination;
    private Set<UUID> _received;
    private boolean _isEnabled = true;
    private float _yaw = 0.0F;
    private Set<BlockState> _portalBlocks = new HashSet<BlockState>(25);
    private Map<Integer, Integer> _widthXMap;
    private Map<Integer, Integer> _widthZMap;
    private Map<Integer, Integer> _heightMaxXMap;
    private Map<Integer, Integer> _heightMaxZMap;

    /**
     * Constructor.
     *
     * @param name      The regions name.
     * @param dataNode  The regions data node.
     */
    public TPRegion(String name, IDataNode dataNode) {
        super(TPRegions.getPlugin(), name, dataNode);

        PreCon.notNull(dataNode);

        _received = new HashSet<>(5);

        setMeta(REGION_META_KEY, this);
    }

    /**
     * Initializes the region.
     *
     * <p>Used to load all regions before initializing. Regions may be
     * interdependent, so they all must be loaded first.</p>
     *
     * @param regionManager  The parent region manager.
     */
    public void init(TPRegionManager regionManager) {

        //noinspection ConstantConditions
        _isEnabled = getDataNode().getBoolean("enabled", _isEnabled);
        _yaw = (float) getDataNode().getDouble("yaw", _yaw);

        String dest = getDataNode().getString("destination");

        if (dest != null) {

            Location destLoc = getDataNode().getLocation("destination", null);

            // Destination is location
            _destination = destLoc != null
                    ? DestinationLocation.from(destLoc)
                    : regionManager.getRegion(dest);
        }

        updatePlayerWatcher();

        if (_isEnabled)
            openPortal();
    }

    /**
     * Determine if the region represents a visible portal or is
     * an invisible region.
     */
    public RegionType getType() {
        return getShape().getPosition() != FlatnessPosition.NONE &&
                getShape().getFlatness() == Flatness.FLAT
                ? RegionType.PORTAL
                : RegionType.REGION;
    }

    @Override
    public boolean isEnabled() {
        return _isEnabled;
    }

    /**
     * Set the enabled state.
     *
     * @param isEnabled  True to enable teleport.
     */
    public void setIsEnabled(boolean isEnabled) {
        _isEnabled = isEnabled;

        //noinspection ConstantConditions
        getDataNode().set("enabled", isEnabled);
        getDataNode().save();

        updatePlayerWatcher();
    }

    /**
     * Get the yaw angle adjustment on outgoing teleports.
     */
    public float getYaw() {
        return _yaw;
    }

    /**
     * Set the yaw angle adjustment on outgoing teleports.
     *
     * @param yaw  The yaw angle.
     */
    public void setYaw(float yaw) {
        _yaw = yaw % 360;

        //noinspection ConstantConditions
        getDataNode().set("yaw", _yaw);
        getDataNode().save();
    }

    /**
     * Get the teleport destination.
     */
    @Nullable
    public ITPDestination getDestination () {
        return _destination;
    }

    /**
     * Set the teleport destination.
     *
     * @param destination  The teleport destination.
     */
    public void setDestination(@Nullable ITPDestination destination) {

        if (getDataNode() == null)
            throw new AssertionError();

        if (destination instanceof TPRegion) {
            TPRegion region = (TPRegion)destination;
            _destination = destination;
            getDataNode().set("destination", region.getName());
        }
        else if (destination instanceof Location) {
            _destination = destination;
            getDataNode().set("destination", destination);
        }
        else {
            getDataNode().remove("destination");
        }

        getDataNode().save();

        updatePlayerWatcher();
    }

    @Override
    public void teleport(@Nullable ITPDestination sender, Entity entity, float yaw) {
        PreCon.notNull(entity);

        final UUID id = entity.getUniqueId();

        if (_received.contains(id) || sender == this)
            return;

        Location destination = getDestination(sender, entity, yaw);
        if (destination == null)
            return;

        _received.add(id);

        Teleporter.teleport(entity, destination);
    }

    public void openPortal() {

        if (getType() != RegionType.PORTAL)
            return;

        final World world = getWorld();

        if (world == null ||
                getShape().getFlatness() != Flatness.FLAT) {
            return;
        }

        _portalBlocks.clear();


        // delay ensures portal blocks don't disappear when the server first starts
        Scheduler.runTaskLater(TPRegions.getPlugin(), 10, new Runnable() {
            @Override
            public void run() {

                final Material portalMaterial = getShape().getPosition() == FlatnessPosition.VERTICAL
                        ? Material.PORTAL
                        : Material.ENDER_PORTAL;

                _widthXMap = new HashMap<>(getYHeight() + 1, 1.0f);
                _widthZMap = new HashMap<>(getYHeight() + 1, 1.0f);
                _heightMaxXMap = new HashMap<>(getXWidth() + 1, 1.0f);
                _heightMaxZMap = new HashMap<>(getZWidth() + 1, 1.0f);

                for (int y = getYEnd(); y >= getYStart(); y--) {

                    ReadWidthMode xMode = ReadWidthMode.NONE;
                    int minX = Integer.MAX_VALUE;
                    int maxX = Integer.MIN_VALUE;
                    int minXY = Integer.MAX_VALUE;
                    int maxXY = Integer.MIN_VALUE;

                    for (int x = getXStart(); x <= getXEnd(); x++) {

                        ReadWidthMode zMode = ReadWidthMode.NONE;
                        int minZ = Integer.MAX_VALUE;
                        int maxZ = Integer.MIN_VALUE;
                        int minZY = Integer.MAX_VALUE;
                        int maxZY = Integer.MIN_VALUE;

                        for (int z = getZStart(); z <= getZEnd(); z++) {
                            Block block = world.getBlockAt(x, y, z);

                            Material material = block.getType();

                            // check for valid portal location
                            if (material == Material.AIR ||
                                    material == Material.PORTAL ||
                                    material == Material.ENDER_PORTAL) {

                                switch (xMode) {
                                    case NONE:
                                        minX = x;
                                        xMode = ReadWidthMode.STARTED;
                                        break;
                                    case STARTED:
                                        maxX = x;
                                        break;
                                }

                                switch (zMode) {
                                    case NONE:
                                        minZ = z;
                                        zMode = ReadWidthMode.STARTED;
                                        break;
                                    case STARTED:
                                        maxZ = z;
                                        break;
                                }

                                minXY = Math.min(minXY, y);
                                maxXY = Math.max(maxXY, y);

                                minZY = Math.min(minZY, y);
                                maxZY = Math.max(maxZY, y);

                                // record min and max height on Z axis
                                _heightMaxZMap.put(z, maxZY);
                            }

                            // check for valid location to insert a portal block
                            if (material == Material.AIR ||
                                    (material == Material.PORTAL && portalMaterial == Material.ENDER_PORTAL) ||
                                    (material == Material.ENDER_PORTAL && portalMaterial == Material.PORTAL)) {

                                block.setType(Material.GLOWSTONE);
                                _portalBlocks.add(block.getState());
                            }
                        }

                        // record width on Z axis for current Y height.
                        if (zMode == ReadWidthMode.STARTED) {
                            int widthZ = Math.abs(maxZ - minZ);
                            _widthZMap.put(y, widthZ / 2);
                        }

                        // record min and max height on X axis
                        _heightMaxXMap.put(x, maxXY);
                    }

                    // record width on X axis for current Y height.
                    if (xMode == ReadWidthMode.STARTED) {
                        int widthX = Math.abs(maxX - minX);
                        _widthXMap.put(y, widthX / 2);
                    }
                }

                for (BlockState block : _portalBlocks) {
                    block.setType(portalMaterial);

                    if (getShape().getDirection() == ShapeDirection.WEST_EAST) {
                        block.setRawData((byte) 2);
                    }
                    else {
                        block.setRawData((byte) 0);
                    }

                    block.update(true);
                }
            }
        });
    }

    // Remove the portal blocks
    public void closePortal() {

        if (getType() != RegionType.PORTAL)
            return;

        World world = getWorld();

        if (world == null)
            return;

        _portalBlocks.clear();

        for (int y = getYEnd(); y >= getYStart(); y--) {
            for (int x = getXStart(); x <= getXEnd(); x++) {
                for (int z = getZStart(); z <= getZEnd(); z++) {

                    Block block = world.getBlockAt(x, y, z);

                    if (block.getType() == Material.PORTAL ||
                            block.getType() == Material.ENDER_PORTAL) {

                        BlockState state = block.getState();
                        state.setType(Material.AIR);
                        state.update(true);
                    }
                }
            }
        }

        refreshChunks();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    protected boolean canDoPlayerEnter(Player p, EnterRegionReason reason) {
        return _isEnabled && !p.hasMetadata("NPC");
    }

    @Override
    protected void onPlayerEnter(Player p, EnterRegionReason reason) {

        if (!(_destination == null || !_destination.isEnabled()) &&
                !_received.contains(p.getUniqueId())) {
            _destination.teleport(this, p, _yaw);
        }
        _received.remove(p.getUniqueId());
    }

    @Override
    protected void onPlayerLeave(Player p, LeaveRegionReason reason) {
        // remove from received so if player re-enters the region they
        // can be teleported.
        _received.remove(p.getUniqueId());
    }

    @Override
    protected void onDispose() {
        closePortal();
    }

    // update the player watcher state and open or close the portal
    private void updatePlayerWatcher() {
        boolean isPlayerWatcher = _destination != null && _destination.isEnabled() && _isEnabled;

        setEventListener(isPlayerWatcher);

        if (isPlayerWatcher) {
            openPortal();
        }
        else {
            closePortal();
        }
    }

    // calculate a destination location
    @Nullable
    private Location getDestination(@Nullable ITPDestination sender, Entity entity, float yaw) {
        PreCon.notNull(sender);
        PreCon.notNull(entity);

        Location rootLocation = getLocation(entity);

        if (sender instanceof IRegionSelection) {
            IRegionSelection region = (IRegionSelection)sender;

            Location localCenter = getCenter();
            if (localCenter == null)
                return null;

            Location senderCenter = region.getCenter();
            if (senderCenter == null)
                return null;

            Location pLoc;

            if (Float.compare(yaw, 0F) == 0) {
                pLoc = rootLocation;
                pLoc.setYaw(yaw);
            }
            else {
                pLoc = LocationUtils.rotate(senderCenter, rootLocation, 0.0D, yaw, 0.0D);
            }

            double x = pLoc.getX() - senderCenter.getX();
            double z = pLoc.getZ() - senderCenter.getZ();

            // get height from bottom of portal to entity location
            double height = rootLocation.getY() - region.getYStart();
            double dy = Math.min(getYStart() + height, getYStart() + getYHeight() - 2);

            if (getType() == RegionType.PORTAL) {

                // adjust for possibly smaller destination portal

                dy = adjustOffset(dy, x, _heightMaxXMap);
                dy = adjustOffset(dy, z, _heightMaxZMap);

                if (getShape() == RegionShape.FLAT_NORTH_SOUTH) {
                    z = 0D;
                    x = adjustX(x, dy);
                } else if (getShape() == RegionShape.FLAT_WEST_EAST) {
                    x = 0D;
                    z = adjustZ(z, dy);
                } else if (getShape() == RegionShape.FLAT_HORIZONTAL) {
                    x = adjustX(x, dy);
                    z = adjustZ(z, dy);
                }
            }

            double dx = localCenter.getX() + x;
            double dz = localCenter.getZ() + z;

            Location location = new Location(getWorld(), dx, dy, dz, pLoc.getYaw(), pLoc.getPitch());

            // put player on the ground if the player is not flying
            if (!(entity instanceof Player) || !((Player) entity).isFlying()) {
                location = LocationUtils.findSurfaceBelow(location);
                if (location != null)
                    location.add(0, 1, 0);
            }

            return location;
        }
        else {

            Location center = getCenter();
            if (center == null)
                return null;

            Location entityLocation = entity.getLocation(rootLocation);

            return new Location(getWorld(), center.getX(), getYStart(), center.getZ(),
                    entityLocation.getYaw() + yaw, entityLocation.getPitch());
        }
    }

    private double adjustX(double x, double y) {
        x = adjustOffset(x, y, _widthXMap);
        x = adjustOffset(x, y + 1, _widthXMap);
        x = adjustOffset(x, y + 2, _widthXMap);

        return x;
    }

    private double adjustZ(double z, double y) {
        z = adjustOffset(z, y, _widthZMap);
        z = adjustOffset(z, y + 1, _widthZMap);
        z = adjustOffset(z, y + 2, _widthZMap);

        return z;
    }

    private double adjustOffset(double width, double y, Map<Integer, Integer> maxMap) {

        Integer max = maxMap.get((int) y);

        if (max != null && Math.abs(width) > max) {

            return width >= 0
                    ? max
                    : -max;
        }

        return width;
    }

    private Location getLocation(Entity entity) {
        while (entity.getVehicle() != null) {
            entity = entity.getVehicle();
        }
        return entity.getLocation();
    }

    private enum ReadWidthMode {
        NONE,
        STARTED
    }
}
