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

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.CommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator;
import com.jcwhatever.bukkit.generic.utils.text.TextUtils.FormatTemplate;
import com.jcwhatever.bukkit.tpregions.Lang;
import com.jcwhatever.bukkit.tpregions.TPRegions;
import com.jcwhatever.bukkit.tpregions.regions.RegionType;
import com.jcwhatever.bukkit.tpregions.regions.TPRegion;

import org.bukkit.command.CommandSender;

import java.util.List;

@CommandInfo(
		parent="list",
		command="regions",
		staticParams={"page=1"},
		usage="/tpr list regions [page]",
		description="List all teleport regions.")

public class RegionsSubCommand extends AbstractCommand {

	@Localizable static final String _PAGINATOR_TITLE = "Teleport Regions/Portals";
	@Localizable static final String _LIST_ITEM_DESCRIPTION = "Type: {0: type}, destination: {1: destination name}";
	@Localizable static final String _LABEL_NOT_SET = "not set";

	@Override
	public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

		int page = args.getInteger("page");

		ChatPaginator pagin = new ChatPaginator(TPRegions.getPlugin(), 6, Lang.get(_PAGINATOR_TITLE));

		List<TPRegion> regions = TPRegions.getRegionManager().getRegions();

		for (TPRegion region : regions) {
			if (region == null || region.getType() != RegionType.REGION)
				continue;

			pagin.add(region.getName(),
					Lang.get(_LIST_ITEM_DESCRIPTION, region.getType().name(),
							(region.getDestination() != null ? region.getDestination() : Lang.get(_LABEL_NOT_SET))));
		}

		pagin.show(sender, page, FormatTemplate.LIST_ITEM_DESCRIPTION);
	}
}
