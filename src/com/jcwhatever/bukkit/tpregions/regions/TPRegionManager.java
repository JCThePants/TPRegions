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

import com.jcwhatever.generic.GenericsLib;
import com.jcwhatever.generic.mixins.IDisposable;
import com.jcwhatever.generic.regions.IRegion;
import com.jcwhatever.generic.storage.DataBatchOperation;
import com.jcwhatever.generic.storage.IDataNode;
import com.jcwhatever.generic.utils.PreCon;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Manages teleport regions.
 */
public class TPRegionManager implements IDisposable {

    private final IDataNode _dataNode;
    private final Map<String, TPRegion> _regionMap = new HashMap<>(50);

    private boolean _isDisposed;

    /**
     * Constructor.
     *
     * @param dataNode  The managers data node.
     */
    public TPRegionManager(IDataNode dataNode) {
        PreCon.notNull(dataNode);

        _dataNode = dataNode;
        loadRegions();
    }

    /**
     * Get a {@code TPRegion} by name.
     *
     * @param name  The name of the region.
     *
     * @return  Null if not found.
     */
    @Nullable
    public TPRegion getRegion(String name) {
        PreCon.notNullOrEmpty(name);

        return _regionMap.get(name.toLowerCase());
    }

    /**
     * Get all {@code TPRegion}'s.
     */
    public List<TPRegion> getRegions() {
        return new ArrayList<TPRegion>(_regionMap.values());
    }

    /**
     * Get {@code TPRegions} by type.
     *
     * @param type  The type to search for.
     */
    public List<TPRegion> getRegions(RegionType type) {
        PreCon.notNull(type);
        PreCon.isValid(type != RegionType.NONE, "RegionType cannot be NONE.");

        List<TPRegion> regions = new ArrayList<>(20);

        for (TPRegion region : getRegions()) {
            if (region.getType() == type)
                regions.add(region);
        }

        return regions;
    }

    /**
     * Get the {@code TPRegion} at the specified location.
     *
     * @param location  The location to check.
     *
     * @return  The found {@code TPRegion} or null if not found.
     */
    @Nullable
    public TPRegion getRegionAt(Location location) {
        PreCon.notNull(location);

        List<IRegion> regions = GenericsLib.getRegionManager().getRegions(location);

        for (IRegion region : regions) {
            if (region.getRegionClass().equals(TPRegion.class)) {
                return getRegion(region.getName());
            }
        }

        return null;
    }

    /**
     * Create a new teleport region.
     *
     * @param name  The name of the region.
     * @param p1    The p1 cuboid coordinates.
     * @param p2    The p2 cuboid coordinates.
     *
     * @return  The created region or Null if a region with the name already exists.
     */
    @Nullable
    public TPRegion add(String name, final Location p1, final Location p2) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(p1);
        PreCon.notNull(p2);

        if (_regionMap.containsKey(name.toLowerCase()))
            return null;

        IDataNode settings = _dataNode.getNode(name);

        final TPRegion region = new TPRegion(name, settings);

        settings.runBatchOperation(new DataBatchOperation() {

            @Override
            public void run(IDataNode dataNode) {
                region.setCoords(p1, p2);
            }
        });

        _regionMap.put(region.getSearchName(), region);

        region.init(this);

        return region;
    }

    /**
     * Remove a region by name.
     *
     * @param name  The name of the region.
     *
     * @return  True if the region was found and removed.
     */
    public boolean remove(String name) {
        PreCon.notNullOrEmpty(name);

        TPRegion region = getRegion(name);
        if (region == null)
            return false;

        _dataNode.set(region.getName(), null);
        _dataNode.saveAsync(null);

        region.dispose();
        _regionMap.remove(region.getSearchName());

        List<TPRegion> regions = getRegions();

        for (TPRegion r : regions) {
            if (r.getDestination() != null && r.getDestination() == region) {
                r.setDestination(null);
            }
        }

        return true;
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        List<TPRegion> regions = getRegions();

        for (TPRegion region : regions) {
            region.dispose();
        }

        _isDisposed = true;
    }

    // load regions from data node
    private void loadRegions() {

        Set<String> regionNames = _dataNode.getSubNodeNames();
        if (regionNames == null || regionNames.isEmpty())
            return;


        for (String regionName : regionNames) {
            loadRegion(regionName, _dataNode.getNode(regionName));
        }

        for (TPRegion region : _regionMap.values()) {
            region.init(this);
        }
    }

    // load a single region
    private void loadRegion(String name, IDataNode settings) {

        TPRegion region = new TPRegion(name, settings);

        _regionMap.put(region.getSearchName(), region);
    }
}
