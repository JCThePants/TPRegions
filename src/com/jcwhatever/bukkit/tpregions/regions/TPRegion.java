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

import com.jcwhatever.nucleus.regions.Region;
import com.jcwhatever.nucleus.regions.selection.IRegionSelection;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.LocationUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.bukkit.tpregions.DestinationLocation;
import com.jcwhatever.bukkit.tpregions.ITPDestination;
import com.jcwhatever.bukkit.tpregions.TPRegions;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * A teleport region.
 *
 * <p>If the sending {@code ITPDestination} is a {@code IRegionSelection}, the player
 * is teleported to the coordinates that are the same as the {@code TPRegion} coordinates, relative to
 * the regions center.</p>
 *
 * <p>Where player Yaw is modified, the players yaw is rotated and the coordinates are rotated
 * around the regions center point.</p>
 */
public class TPRegion extends Region implements ITPDestination {

    private ITPDestination _destination;
    private Set<UUID> _received;
    private boolean _isEnabled = true;
    private float _yaw = 0.0F;
    Set<BlockState> _portalBlocks = new HashSet<BlockState>(100);

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
    }

    /**
     * Determine if the region represents a visible portal or is
     * an invisible region.
     */
    public RegionType getType() {
        return isFlatHorizontal() || isFlatVertical() ? RegionType.PORTAL : RegionType.REGION;
    }

    /**
     * Determine if the region is enabled.
     */
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


    /**
     * Send a player to the {@code TPRegion} destination.
     *
     * @param sender  The {@code ITPDestination} that the player is being received from.
     * @param p       The player to teleport.
     * @param yaw     The desired yaw adjustment.
     */
    @Override
    public void teleport(@Nullable ITPDestination sender, final Player p, float yaw) {
        PreCon.notNull(p);

        if (_received.contains(p.getUniqueId()) && sender == this)
            return;

        final Location destination = getDestination(sender, p, yaw);
        if (destination == null)
            return;

        _received.add(p.getUniqueId());

        final GameMode gm = p.getGameMode();
        final Vector v = p.getVelocity();

        Bukkit.getScheduler().runTaskLater(TPRegions.getPlugin(), new Runnable() {

            @Override
            public void run() {
                p.teleport(destination);
                p.setGameMode(gm);
                p.setVelocity(v);
            }

        }, 1);

    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Determine if the region can handle a player entering.
     */
    @Override
    protected boolean canDoPlayerEnter(Player p, EnterRegionReason reason) {
        return _isEnabled;
    }

    /**
     * Called when a player enters the region and {@code canDoPlayerEnter}
     * returns true.
     */
    @Override
    protected void onPlayerEnter (Player p, EnterRegionReason reason) {

        if (!(_destination == null || !_destination.isEnabled()) &&
                !_received.contains(p.getUniqueId())) {
            _destination.teleport(this, p, _yaw);
        }
        _received.remove(p.getUniqueId());
    }

    /**
     * Called when a player leaves the region and {@code canDoPlayerLeave}
     * returns true.
     */
    @Override
    protected void onPlayerLeave (Player p, LeaveRegionReason reason) {
        // remove from received so if player re-enters the region they
        // can be teleported.
        _received.remove(p.getUniqueId());
    }

    @Override
    protected void onDispose() {
        closePortal();
    }

    private void openPortal() {

        if (getType() != RegionType.PORTAL)
            return;

        World world = getWorld();

        if (world == null || !(isFlatHorizontal() || isFlatVertical()))
            return;

        _portalBlocks.clear();

        final Material portalMaterial = isFlatVertical() ? Material.PORTAL : Material.ENDER_PORTAL;

        for (int y = getYEnd(); y >= getYStart(); y--) {
            for (int x = getXStart(); x <= getXEnd(); x++) {
                for (int z = getZStart(); z <= getZEnd(); z++) {
                    Block block = world.getBlockAt(x, y, z);

                    if (block.getType() == Material.AIR ||
                            (block.getType() == Material.PORTAL && portalMaterial == Material.ENDER_PORTAL) ||
                            (block.getType() == Material.ENDER_PORTAL && portalMaterial == Material.PORTAL)) {
                        block.setType(Material.GLOWSTONE);
                        _portalBlocks.add(block.getState());

                    }
                }
            }
        }

        for (BlockState block : _portalBlocks) {
            block.setType(portalMaterial);
        }

        for (BlockState block : _portalBlocks) {
            block.update(true);
        }
    }

    // Remove the portal blocks
    private void closePortal() {

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
    private Location getDestination(@Nullable ITPDestination sender, Player p, float yaw) {
        PreCon.notNull(sender);
        PreCon.notNull(p);

        if (sender instanceof IRegionSelection) {
            IRegionSelection region = (IRegionSelection)sender;

            Location localCenter = getCenter();
            if (localCenter == null)
                return null;

            Location senderCenter = region.getCenter();

            Location pLoc = Float.compare(yaw, 0F) == 0
                    ? p.getLocation()
                    : LocationUtils.rotate(senderCenter, p.getLocation(), 0.0D, yaw, 0.0D);

            double x = pLoc.getX() - senderCenter.getX();
            double y = pLoc.getY() - senderCenter.getY();
            double z = pLoc.getZ() - senderCenter.getZ();

            double dx = localCenter.getX() + x;
            double dy = localCenter.getY() + y;
            double dz = localCenter.getZ() + z;

            return new Location(getWorld(), dx, dy, dz, pLoc.getYaw(), pLoc.getPitch());
        }
        else {

            Location center = getCenter();
            if (center == null)
                return null;

            return new Location(getWorld(), center.getX(), getYStart(), center.getZ(),
                    p.getLocation().getYaw() + yaw, p.getLocation().getPitch());
        }
    }
}
