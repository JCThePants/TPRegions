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
        command="del",
        staticParams={"regionName"},
        description="Remove a teleport region or portal.",

        paramDescriptions = {
                "regionName= The name of the region to remove."})

public class DelCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _NOT_FOUND = "A teleport region or portal with the name '{0: region name}' was not found.";
    @Localizable static final String _FAILED = "Failed to delete region or portal named '{0: region name}'.";
    @Localizable static final String _SUCCESS = "Teleport region/portal '{0: region name}' deleted.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        String regionName = args.getString("regionName");

        TPRegion region = TPRegions.getRegionManager().getRegion(regionName);
        if (region == null)
            throw new CommandException(Lang.get(_NOT_FOUND, regionName));

        if (!TPRegions.getRegionManager().remove(regionName))
            throw new CommandException(Lang.get(_FAILED, regionName));

        tellSuccess(sender, Lang.get(_SUCCESS, regionName));
    }
}

