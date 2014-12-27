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

import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.language.Localizable;
import com.jcwhatever.bukkit.tpregions.Lang;
import com.jcwhatever.bukkit.tpregions.TPRegions;
import com.jcwhatever.bukkit.tpregions.regions.TPRegion;

import org.bukkit.command.CommandSender;

@CommandInfo(
        command="disable",
        staticParams={"regionName"},
        description="Disables a teleport region or portal.",

        paramDescriptions = {
                "regionName= The name of the region to disable."})

public class DisableCommand extends AbstractCommand {

    @Localizable static final String _NOT_FOUND = "A teleport region or portal with the name '{0: region name}' was not found.";
    @Localizable static final String _DISABLED = "Teleport region '{0: region name}' {RED}Disabled.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        String regionName = args.getName("regionName", 32);

        TPRegion region = TPRegions.getRegionManager().getRegion(regionName);
        if (region == null) {
            tellError(sender, Lang.get(_NOT_FOUND, regionName));
            return; // finish
        }

        region.setIsEnabled(false);

        tellSuccess(sender, Lang.get(_DISABLED, regionName));
    }
}



