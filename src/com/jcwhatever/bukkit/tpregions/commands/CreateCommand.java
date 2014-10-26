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

import com.jcwhatever.bukkit.generic.regions.RegionSelection;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException.CommandSenderType;
import com.jcwhatever.bukkit.tpregions.TPRegions;
import com.jcwhatever.bukkit.tpregions.regions.TPRegion;
import com.jcwhatever.bukkit.tpregions.regions.TPRegionManager;

@ICommandInfo(
		command="create", 
		staticParams = {"regionName"},
		usage = "/tpr create <regionName>",
		description="Create a teleport region or portal. Note that portals must be flat vertically or horizontally.")

public class CreateCommand extends AbstractCommand {
	
	
	@Override
	public void execute(CommandSender sender, CommandArguments args)
	        throws InvalidValueException, InvalidCommandSenderException {
		
	    InvalidCommandSenderException.check(sender, CommandSenderType.PLAYER,
	            "Console cannot select region.");
		
		if (!isWorldEditInstalled(sender))
			return; // finish
		
		String regionName = args.getName("regionName", 32);
				
		TPRegionManager regionManager = TPRegions.getInstance().getRegionManager(); 
		
		TPRegion region = regionManager.getRegion(regionName);
		if (region != null) {
			tellError(sender, "There is already a teleport region or portal with the name '{0}'.", regionName);
			return; // finish
		}
		
		RegionSelection sel = getWorldEditSelection((Player)sender);
		if (sel == null)
			return; // finish
		
		
		region = regionManager.createRegion(regionName, sel.getP1(), sel.getP2());
			
		if (region == null) {
			tellError(sender, "Failed to create a new region.");
			return; // finish
		}
		
		tellSuccess(sender, "New teleport region named '{0}' created. Set the destination using '/tpr set ?'", regionName);
	}
	
}
