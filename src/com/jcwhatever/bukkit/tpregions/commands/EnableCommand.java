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
		command="enable", 
		staticParams={"regionName"},
		usage="/tpr enable <regionName>",
		description="Enables a teleport region or portal.")

public class EnableCommand extends AbstractCommand {

	@Override
	public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {
		
		String regionName = args.getName("regionName", 32);
		
		TPRegionManager regionManager = TPRegions.getInstance().getRegionManager(); 
		
		TPRegion region = regionManager.getRegion(regionName);
		if (region == null) {
			tellError(sender, "A teleport region or portal with the name '{0}' was not found.", regionName);
			return; // finish
		}
		
		region.setIsEnabled(true);
		
		tellEnabled(sender, "Teleport region '{0}' {e}.", true, regionName);
	}
	
}


