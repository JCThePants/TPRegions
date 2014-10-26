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


package com.jcwhatever.bukkit.tpregions;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import com.jcwhatever.bukkit.tpregions.regions.TPRegion;
import com.jcwhatever.bukkit.tpregions.regions.TPRegionManager;

public class EventListener implements Listener {
	
    
    @EventHandler(priority=EventPriority.NORMAL)
    private void onCreatureSpawn(CreatureSpawnEvent event) {
        
        if (event.getSpawnReason() != SpawnReason.NETHER_PORTAL)
            return;
        
        TPRegionManager regionManager = TPRegionManager.get();
        TPRegion region = regionManager.getRegionAt(event.getLocation());
        if (region == null)
            return;
        
        event.setCancelled(true);
    }
    
	@EventHandler(priority=EventPriority.HIGH)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        
		TPRegionManager regionManager = TPRegionManager.get();
		TPRegion region = regionManager.getRegionAt(event.getBlock().getLocation());
		if (region == null)
			return;
		
		event.setCancelled(true);
    }
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerPortal(PlayerPortalEvent event) {
		TPRegion region = TPRegionManager.get().getRegionAt(event.getFrom());
		
		if (region != null && region.isEnabled())
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockDamage(BlockDamageEvent event) {
		TPRegion region = TPRegionManager.get().getRegionAt(event.getBlock().getLocation());
		
		if (region != null && region.isEnabled())
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent event) {
		TPRegion region = TPRegionManager.get().getRegionAt(event.getBlock().getLocation());
		
		if (region != null && region.isEnabled())
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		
		for (Block block : event.blockList()) {
			TPRegion region = TPRegionManager.get().getRegionAt(block.getLocation());
			
			if (region != null && region.isEnabled()) {
				event.setCancelled(true);
				return;
			}
		}
		
	}
}
