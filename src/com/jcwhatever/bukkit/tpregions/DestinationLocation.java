package com.jcwhatever.bukkit.tpregions;

import com.jcwhatever.bukkit.generic.utils.TextUtils;
import com.jcwhatever.bukkit.tpregions.regions.ITPDestination;
import com.jcwhatever.bukkit.tpregions.regions.TPRegion;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class DestinationLocation extends Location implements ITPDestination {
	
	public static DestinationLocation from(Location location) {
		return new DestinationLocation(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}
	
	public DestinationLocation(World world, double x, double y, double z) {
		super(world, x, y, z);
	}
	
	public DestinationLocation(World world, double x, double y, double z, float yaw, float pitch) {
		super(world, x, y, z, yaw, pitch);
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Location getDestination(TPRegion sender, Player p, float yaw) {
		return new Location(this.getWorld(), this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
	}
	
	@Override
	public String toString() {
		return TextUtils.formatLocation(this, true);
	}

}
