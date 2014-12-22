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
import com.jcwhatever.bukkit.generic.commands.exceptions.CommandException;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.regions.selection.IRegionSelection;
import com.jcwhatever.bukkit.tpregions.Lang;
import com.jcwhatever.bukkit.tpregions.TPRegions;
import com.jcwhatever.bukkit.tpregions.regions.TPRegion;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
        command="create",
        staticParams = {"regionName"},
        description="Create a teleport region or portal from your current region selection. " +
                "Note that portals must be flat vertically or horizontally.",

        paramDescriptions = {
                "regionName= The name of the region. {NAME}"})

public class CreateCommand extends AbstractCommand {

    @Localizable static final String _ALREADY_EXISTS = "There is already a teleport region or portal with the " +
            "name '{0 : region name}'.";
    @Localizable static final String _FAILED = "Failed to create a new region.";
    @Localizable static final String _SUCCESS = "New teleport region named '{0: region name}' created. " +
            "Set the destination using '/tpr set ?'";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        CommandException.assertNotConsole(this, sender);

        String regionName = args.getName("regionName", 32);

        TPRegion region = TPRegions.getRegionManager().getRegion(regionName);
        if (region != null) {
            tellError(sender, Lang.get(_ALREADY_EXISTS, regionName));
            return; // finish
        }

        IRegionSelection sel = getRegionSelection((Player) sender);
        if (sel == null)
            return; // finish

        region = TPRegions.getRegionManager().add(regionName, sel.getP1(), sel.getP2());

        if (region == null) {
            tellError(sender, Lang.get(_FAILED));
            return; // finish
        }

        tellSuccess(sender, Lang.get(_SUCCESS, regionName));
    }
}
