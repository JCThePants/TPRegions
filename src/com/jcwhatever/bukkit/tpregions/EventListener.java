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
