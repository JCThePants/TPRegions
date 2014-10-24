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
		command="del", 
		staticParams={"regionName"},
		usage="/tpr del <regionName>",
		description="Delete a teleport region or portal.")

public class DelCommand extends AbstractCommand {

	@Override
	public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {
		
		String regionName = args.getName("regionName", 32);
		
		TPRegionManager regionManager = TPRegions.getInstance().getRegionManager(); 
		
		TPRegion region = regionManager.getRegion(regionName);
		if (region == null) {
			tellError(sender, "A teleport region or portal with the name '{0}' was not found.", regionName);
			return; // finish
		}
		
		if (!regionManager.delete(regionName)) {
			tellError(sender, "Failed to delete region or portal named '{0}'.", regionName);
		}
		
		tellSuccess(sender, "Teleport region/portal '{0}' deleted.", regionName);
	}
	
}

