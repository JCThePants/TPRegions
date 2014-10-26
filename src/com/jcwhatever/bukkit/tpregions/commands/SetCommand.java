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


package com.jcwhatever.bukkit.tpregions.commands;

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException.CommandSenderType;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import com.jcwhatever.bukkit.tpregions.DestinationLocation;
import com.jcwhatever.bukkit.tpregions.TPRegions;
import com.jcwhatever.bukkit.tpregions.regions.ITPDestination;
import com.jcwhatever.bukkit.tpregions.regions.TPRegion;
import com.jcwhatever.bukkit.tpregions.regions.TPRegionManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ICommandInfo(
		command="set", 
		staticParams={"regionName", "destName=$location"},
		usage="/tpr set <regionName> [destName]",
		description="Sets the destination of a TPRegion to the players location or the specified region. Specify 'null' for <destName> if you want to clear the destination.")

public class SetCommand extends AbstractCommand {

	@Override
	public void execute(CommandSender sender, CommandArguments args)
	        throws InvalidValueException, InvalidCommandSenderException {

		String regionName = args.getName("regionName", 32);
		

		TPRegionManager regionManager = TPRegions.getInstance().getRegionManager(); 

		TPRegion region = regionManager.getRegion(regionName);
		if (region == null) {
			tellError(sender, "Failed to find a teleport region named '{0}'.", regionName);
			return; // finish
		}
		
		// Portal Location
		if (args.getString("destName").equals("$location")) {
			
		    InvalidCommandSenderException.check(sender, CommandSenderType.PLAYER,
		            "Console has no location.");
			
			Player p = (Player)sender;
			Location location = p.getLocation();
			
			region.setDestination(DestinationLocation.from(location));
			
			tellSuccess(sender, "Teleport region '{0}' destination set to location:");
			tell(sender, TextUtils.formatLocation(location, true));
			return; // finish
		}
		// Region Destination
		else {
			
			
			String destName = args.getName("destName", 32);
			
			
			if (destName.equalsIgnoreCase("null")) {
				region.setDestination(null);
				tellSuccess(sender, "Teleport region '{0}' destination set to nothing.", destName);
				return; // finish
			}
						
			TPRegion destination = regionManager.getRegion(destName);

			if (destination == null) {
				tellError(sender, "Failed to find a teleport region named '{0}' to set as destination.", destName);
				return; // finish
			}

			if (!(destination instanceof ITPDestination)) {
				tellError(sender, "Region '{0}' cannot be used as a destination.", destName);
				return; // finish
			}

			region.setDestination((ITPDestination)destination);
			
			tellSuccess(sender, "Teleport region '{0}' destination set to region '{1}'.", region.getName(), destination.getName());
		}
	}

}

