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


package com.jcwhatever.tpregions.commands;

import com.jcwhatever.tpregions.Lang;
import com.jcwhatever.tpregions.TPRegions;
import com.jcwhatever.tpregions.regions.TPRegion;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;

import org.bukkit.command.CommandSender;

@CommandInfo(
        command="setyaw",
        staticParams={"regionName", "yaw"},
        description="Make adjustments to the players yaw angle when teleported from the specified region.",

        paramDescriptions = {
                "regionName= The name of the region to set yaw adjustment on.",
                "yaw= The yaw adjustment in degrees. Should be a multiple of 90. Range is 0-270."})

public class SetYawCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _NOT_FOUND =
            "A teleport region with the name '{0: region name}' was not found.";

    @Localizable static final String _SUCCESS =
            "Teleport region '{0: region name}' yaw adjustment set to {1: yaw} degrees.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        String regionName = args.getString("regionName");
        float yaw = args.getFloat("yaw");

        TPRegion region = TPRegions.getRegionManager().getRegion(regionName);
        if (region == null)
            throw new CommandException(Lang.get(_NOT_FOUND, regionName));

        region.setYaw(yaw);

        tellSuccess(sender, Lang.get(_SUCCESS, regionName, yaw));
    }
}



