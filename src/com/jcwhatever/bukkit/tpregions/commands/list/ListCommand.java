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


package com.jcwhatever.bukkit.tpregions.commands.list;

import com.jcwhatever.bukkit.tpregions.Lang;
import com.jcwhatever.bukkit.tpregions.TPRegions;
import com.jcwhatever.bukkit.tpregions.regions.TPRegion;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.messaging.ChatPaginator;
import com.jcwhatever.nucleus.utils.text.TextUtils.FormatTemplate;

import org.bukkit.command.CommandSender;

import java.util.List;

@CommandInfo(
        command="list",
        staticParams={"page=1"},
        floatingParams = {"search=" },
        description="List all regions and portals.",

        paramDescriptions = {
                "page= {PAGE}",
                "search= Optional. Specify a search filter."
        })

public class ListCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _PAGINATOR_TITLE =
            "Teleport Regions/Portals";

    @Localizable static final String _LIST_ITEM_DESCRIPTION =
            "Type: {0: type}, destination: {1: destination name}";

    @Localizable static final String _LABEL_NOT_SET = "not set";

    public ListCommand () {
        super();

        registerCommand(PortalsSubCommand.class);
        registerCommand(RegionsSubCommand.class);
    }

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        int page = args.getInteger("page");

        ChatPaginator pagin = new ChatPaginator(TPRegions.getPlugin(), 6, Lang.get(_PAGINATOR_TITLE));

        List<TPRegion> regions = TPRegions.getRegionManager().getRegions();

        for (TPRegion region : regions) {
            if (region == null)
                continue;
            pagin.add(region.getName(),
                    Lang.get(_LIST_ITEM_DESCRIPTION, region.getType().name(),
                            (region.getDestination() != null ? region.getDestination() : Lang.get(_LABEL_NOT_SET))));
        }

        if (!args.isDefaultValue("search"))
            pagin.setSearchTerm(args.getString("search"));

        pagin.show(sender, page, FormatTemplate.LIST_ITEM_DESCRIPTION);
    }
}


