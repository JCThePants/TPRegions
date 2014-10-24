package com.jcwhatever.bukkit.tpregions.regions;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface ITPDestination {
	
	public boolean isEnabled();
	
	public Location getDestination(TPRegion sender, Player p, float yaw);
	
}
