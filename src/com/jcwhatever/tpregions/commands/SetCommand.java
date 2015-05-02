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

import com.jcwhatever.tpregions.DestinationLocation;
import com.jcwhatever.tpregions.Lang;
import com.jcwhatever.tpregions.TPRegions;
import com.jcwhatever.tpregions.regions.TPRegion;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
        command="set",
        staticParams={"regionName", "destination=$location"},
        description="Sets the destination of a TPRegion to the players current location or the specified region.",

        paramDescriptions = {
                "regionName= The name of the region to set a destination.",
                "destination= The name of the destination region to teleport player to or leave blank to use" +
                        "your current location as the teleport location."})

public class SetCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _NOT_FOUND =
            "Failed to find a teleport region named '{0: region name}'.";

    @Localizable static final String _SET_LOCATION =
            "Teleport region '{0: region name}' destination set to location:";

    @Localizable static final String _SET_REGION =
            "Teleport region '{0: region name}' destination set to region '{1: destination name}'.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        String regionName = args.getString("regionName");

        TPRegion region = TPRegions.getRegionManager().getRegion(regionName);
        if (region == null)
            throw new CommandException(Lang.get(_NOT_FOUND, regionName));

        // Portal Location
        if (args.getString("destination").equals("$location")) {

            CommandException.checkNotConsole(getPlugin(), this, sender);

            Player p = (Player)sender;
            Location location = p.getLocation();

            region.setDestination(DestinationLocation.from(location));

            tellSuccess(sender, Lang.get(_SET_LOCATION, regionName));
            tell(sender, TextUtils.formatLocation(location, true));
        }
        // Region Destination
        else {

            String name = args.getName("destination", 32);

            TPRegion destination = TPRegions.getRegionManager().getRegion(name);
            if (destination == null)
                throw new CommandException(Lang.get(_NOT_FOUND, name));

            region.setDestination(destination);

            tellSuccess(sender, Lang.get(_SET_REGION, region.getName(), destination.getName()));
        }
    }

}

