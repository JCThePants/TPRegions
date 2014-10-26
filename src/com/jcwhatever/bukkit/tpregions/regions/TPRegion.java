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

import com.jcwhatever.bukkit.generic.player.collections.PlayerSet;
import com.jcwhatever.bukkit.generic.regions.Region;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.tpregions.DestinationLocation;
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

public class TPRegion extends Region implements ITPDestination {

	private ITPDestination _destination;
	private Set<Player> _received;
	private boolean _isEnabled = true;
	private float _yaw = 0.0F;
	Set<BlockState> _portalBlocks = new HashSet<BlockState>();
	
	public TPRegion(String name, IDataNode settings) {
		super(TPRegions.getInstance(), name, settings);
		
		_received = new PlayerSet();
	}
	
	public void init(TPRegionManager regionManager) {
		
		_isEnabled = _dataNode.getBoolean("enabled", _isEnabled);
		_yaw = (float) _dataNode.getDouble("yaw", _yaw);
		
		String dest = _dataNode.getString("destination");
		
		if (dest != null) {
			
			Location destLoc = _dataNode.getLocation("destination", null);
			
			// Destination is location
			if (destLoc != null) {
				_destination = DestinationLocation.from(destLoc);
			}
			// Destination is region
			else {
				TPRegion region = regionManager.getRegion(dest);
				if (region instanceof ITPDestination) {
					ITPDestination destination = (ITPDestination)region;
					_destination = destination;
				}	
			}
		}
		
		updatePlayerWatcher();
	}

	public ITPDestination getDestination () {
		return _destination;
	}
	
	private void sendPlayer(TPRegion sender, final Player p, float yaw) {
		
		final Location destination = _destination.getDestination(sender, p, yaw);
		if (destination == null)
			return;
		
		if (_destination != null)
			_received.add(p);
		
		final GameMode gm = p.getGameMode();
		final Vector v = p.getVelocity();
		
		Bukkit.getScheduler().runTaskLater(TPRegions.getInstance(), new Runnable() {

			@Override
			public void run() {
				p.teleport(destination);
				p.setGameMode(gm);
				p.setVelocity(v);
			}
			
		}, 1);
		
	}

	public void setDestination(ITPDestination destination) {
		if (destination instanceof TPRegion) {
			TPRegion region = (TPRegion)destination;
			_destination = destination;
			_dataNode.set("destination", region.getName());
		}
		else if (destination instanceof Location) {
			_destination = destination;
			_dataNode.set("destination", (Location)destination);
		}
		else {
			_dataNode.remove("destination");
		}
		
		_dataNode.saveAsync(null);
		
		updatePlayerWatcher();
	}
	
	@Override
	public boolean canDoPlayerEnter(Player p) {
		if (_destination == null || !_destination.isEnabled())
			return false;
		
		return _isEnabled && !_received.contains(p);
	}
	
	public boolean canDoPlayerLeave(Player p) {
		return _destination != null;
	}

	@Override
	protected void onPlayerEnter (Player p) {
		sendPlayer(this, p, _yaw);
	}
	
	@Override
	protected void onPlayerLeave (Player p) {
		_received.remove(p);
	}
	
	public boolean isEnabled() {
		return _isEnabled;
	}
	
	public float getYaw() {
		return _yaw;
	}

	public void setIsEnabled(boolean isEnabled) {
		_isEnabled = isEnabled;
		_dataNode.set("enabled", isEnabled);
		_dataNode.saveAsync(null);
		
		updatePlayerWatcher();
	}
	
	public void setYaw(float yaw) {
		_yaw = yaw % 360;
		_dataNode.set("yaw", _yaw);
		_dataNode.saveAsync(null);
	}

	
	/*
	 * ITPDestination implementation
	 */
	
	@Override
	public Location getDestination(TPRegion sender, Player p, float yaw) {
		if (sender == null || p == null)
			return null;

		Location pLoc = p.getLocation();

		double x = pLoc.getX() - sender.getXStart();
		double y = pLoc.getY() - sender.getYStart();
		double z = pLoc.getZ() - sender.getZStart();

		double dx = getXStart() + x;
		double dy = getYStart() + y;
		double dz = getZStart() + z;

		float dyaw = (pLoc.getYaw() + _yaw + yaw) % 360; 

		return new Location(getWorld(), dx, dy, dz, dyaw, pLoc.getPitch());
	}

	public RegionType getType() {
		return isFlatHorizontal() || isFlatVertical() ? RegionType.PORTAL : RegionType.REGION;
	}
	
	public String toString() {
		return getName();
	}
	
	public boolean canBePortal() {
		return isFlatHorizontal() || isFlatVertical();
	}
	
	private void openPortal() {
		
		 if (getType() != RegionType.PORTAL)
			 return;
		
		World world = getWorld();

		if (world == null || ! canBePortal())
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
	
	private void updatePlayerWatcher() {
		boolean isPlayerWatcher = _destination != null && _destination.isEnabled() && _isEnabled;
		
		setIsPlayerWatcher(isPlayerWatcher);	
		
		if (isPlayerWatcher) {
			openPortal();
		}
		else {
			closePortal();
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		closePortal();
	}
	
}
