package com.jcwhatever.bukkit.tpregions.commands;

import com.jcwhatever.bukkit.generic.commands.AbstractCommandHandler;
import com.jcwhatever.bukkit.tpregions.TPRegions;
import com.jcwhatever.bukkit.tpregions.commands.list.ListCommand;

public class CommandHandler extends AbstractCommandHandler {

	public CommandHandler() {
		super(TPRegions.getInstance());
	}

	@Override
	protected void registerCommands() {
		this.registerCommand(CreateCommand.class);
		this.registerCommand(DelCommand.class);
		this.registerCommand(DisableCommand.class);
		this.registerCommand(EnableCommand.class);
		this.registerCommand(ListCommand.class);
		this.registerCommand(SetCommand.class);
		this.registerCommand(SetYawCommand.class);
	}

}