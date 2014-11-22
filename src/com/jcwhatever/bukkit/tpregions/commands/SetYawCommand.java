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
import com.jcwhatever.bukkit.generic.commands.CommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.tpregions.TPRegions;
import com.jcwhatever.bukkit.tpregions.regions.TPRegion;
import com.jcwhatever.bukkit.tpregions.regions.TPRegionManager;
import org.bukkit.command.CommandSender;

@CommandInfo(
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



