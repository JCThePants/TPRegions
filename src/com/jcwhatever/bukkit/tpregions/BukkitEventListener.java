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

import com.jcwhatever.bukkit.tpregions.regions.TPRegion;

import org.bukkit.Location;
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

import javax.annotation.Nullable;

public class BukkitEventListener implements Listener {

	// prevent mob spawning inside teleport regions.
	@EventHandler
	private void onCreatureSpawn(CreatureSpawnEvent event) {

		if (event.getSpawnReason() != SpawnReason.NETHER_PORTAL)
			return;

		TPRegion region = getRegion(event.getLocation());
		if (region == null)
			return;

		event.setCancelled(true);
	}

	// prevent block physics inside teleport regions.
	@EventHandler(priority=EventPriority.HIGH)
	private void onBlockPhysics(BlockPhysicsEvent event) {

		TPRegion region = getRegion(event.getBlock().getLocation());
		if (region == null)
			return;

		event.setCancelled(true);
	}

	// prevent normal nether portal events in teleport regions.
	@EventHandler(priority=EventPriority.HIGH)
	private void onPlayerPortal(PlayerPortalEvent event) {

		TPRegion region = getRegion(event.getFrom());
		if (region != null && region.isEnabled())
			event.setCancelled(true);
	}

	// prevent damaging teleport region blocks.
	@EventHandler
	private void onBlockDamage(BlockDamageEvent event) {

		TPRegion region = getRegion(event.getBlock().getLocation());
		if (region != null && region.isEnabled())
			event.setCancelled(true);
	}

	// prevent breaking teleport region blocks.
	@EventHandler
	private void onBlockBreakEvent(BlockBreakEvent event) {

		TPRegion region = getRegion(event.getBlock().getLocation());
		if (region != null && region.isEnabled())
			event.setCancelled(true);
	}

	// prevent explosion damage to and near teleport region blocks.
	@EventHandler
	private void onEntityExplode(EntityExplodeEvent event) {

		for (Block block : event.blockList()) {
			TPRegion region = getRegion(block.getLocation());

			if (region != null && region.isEnabled()) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@Nullable
	private TPRegion getRegion(Location location) {
		return TPRegions.getRegionManager().getRegionAt(location);
	}
}
