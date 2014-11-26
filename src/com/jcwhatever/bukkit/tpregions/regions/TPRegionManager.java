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

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.performance.SingleCache;
import com.jcwhatever.bukkit.generic.regions.IRegion;
import com.jcwhatever.bukkit.generic.storage.BatchOperation;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public class TPRegionManager {
	
	private static TPRegionManager _instance;
	
	public static TPRegionManager get() {
		return _instance;
	}

	private Map<String, TPRegion> _regionMap = new HashMap<>(50);
	private IDataNode _settings;
	private SingleCache<RegionType, List<TPRegion>> _regionsByTypeCache = new SingleCache<RegionType, List<TPRegion>>();

	public TPRegionManager(IDataNode settings) {
		_instance = this;
		_settings = settings;
		loadRegions();		
	}

	public TPRegion getRegion(String name) {
		return _regionMap.get(name.toLowerCase());
	}

	public List<TPRegion> getRegions() {
		return new ArrayList<TPRegion>(_regionMap.values());
	}
	
	public List<TPRegion> getRegions(RegionType type) {
		if (_regionsByTypeCache.keyEquals(type))
			return new ArrayList<TPRegion>(_regionsByTypeCache.getValue());
		
		List<TPRegion> regions = new ArrayList<>(20);
		
		for (TPRegion region : getRegions()) {
			if (region.getType() == type)
				regions.add(region);
		}
		
		_regionsByTypeCache.set(type, new ArrayList<TPRegion>(regions));
		
		return regions;
	}

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
	
	
	
	public TPRegion createRegion(String name, final Location p1, final Location p2) {
		
		IDataNode settings = _settings.getNode(name);
				
		final TPRegion region = new TPRegion(name, settings);
		
		settings.runBatchOperation(new BatchOperation() {

			@Override
			public void run(IDataNode dataNode) {
				region.setCoords(p1, p2);
			}
		});

		_regionMap.put(region.getSearchName(), region);
		_regionsByTypeCache.reset();
		
		region.init(this);

		return region;
	}


	public boolean delete(String name) {

		TPRegion region = getRegion(name);
		if (region == null)
			return false;

		_settings.set(region.getName(), null);
        _settings.saveAsync(null);

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

	private void loadRegions() {

		Set<String> regionNames = _settings.getSubNodeNames();
		if (regionNames == null || regionNames.isEmpty())
			return;


		for (String regionName : regionNames) {
			loadRegion(regionName, _settings.getNode(regionName));
		}

		for (TPRegion region : _regionMap.values()) {
			region.init(this);
		}
	}


	private void loadRegion(String name, IDataNode settings) {
		
		TPRegion region = new TPRegion(name, settings);
		
		_regionMap.put(region.getSearchName(), region);
	}

	
}
