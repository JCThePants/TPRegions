package com.jcwhatever.bukkit.tpregions.commands.list;

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator.PaginatorTemplate;
import com.jcwhatever.bukkit.generic.utils.TextUtils.FormatTemplate;
import com.jcwhatever.bukkit.tpregions.TPRegions;
import com.jcwhatever.bukkit.tpregions.regions.RegionType;
import com.jcwhatever.bukkit.tpregions.regions.TPRegion;
import com.jcwhatever.bukkit.tpregions.regions.TPRegionManager;
import org.bukkit.command.CommandSender;

import java.util.List;

@ICommandInfo(
		parent="list",
		command="regions", 
		staticParams={"page=1"},
		usage="/tpr list regions [page]",
		description="List all teleport regions.")

public class RegionsSubCommand extends AbstractCommand {

	@Override
	public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {
		
		int page = args.getInt("page");
		
		TPRegionManager regionManager = TPRegions.getInstance().getRegionManager();
		
		ChatPaginator pagin = new ChatPaginator(TPRegions.getInstance(), 6, PaginatorTemplate.HEADER, PaginatorTemplate.FOOTER, "Teleport Regions");
		
		List<TPRegion> regions = regionManager.getRegions();
		
		for (TPRegion region : regions) {
			if (region == null || region.getType() != RegionType.REGION)
				continue;
			
			pagin.add(region.getName(), "Destination: " + (region.getDestination() != null ? region.getDestination() : "none"));
		}
		
		pagin.show(sender, page, FormatTemplate.ITEM_DESCRIPTION);
	}
}
