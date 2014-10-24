package com.jcwhatever.bukkit.tpregions.commands;

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.tpregions.TPRegions;
import com.jcwhatever.bukkit.tpregions.regions.TPRegion;
import com.jcwhatever.bukkit.tpregions.regions.TPRegionManager;
import org.bukkit.command.CommandSender;

@ICommandInfo(
		command="setyaw", 
		staticParams={"regionName", "yaw"},
		usage="/tpr setyaw <regionName> <yaw>",
		description="Make adjustments to the players yaw angle when teleported.")

public class SetYawCommand extends AbstractCommand {

	@Override
	public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {
		
		String regionName = args.getName("regionName", 32);
		float yaw = args.getFloat("yaw");
		
		TPRegionManager regionManager = TPRegions.getInstance().getRegionManager(); 
		
		TPRegion region = regionManager.getRegion(regionName);
		if (region == null) {
			tellError(sender, "A teleport region with the name '" + regionName + "' was not found.");
			return; // finish
		}
		
		region.setYaw(yaw);
		
		tellSuccess(sender, "Teleport region '" + regionName + "' yaw adjustment set to " + yaw + " degrees.");
	}
	
}



