package com.jcwhatever.bukkit.tpregions;

import com.jcwhatever.bukkit.generic.GenericsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;

import com.jcwhatever.bukkit.tpregions.commands.CommandHandler;
import com.jcwhatever.bukkit.tpregions.regions.TPRegionManager;

public class TPRegions  extends GenericsPlugin {

	private static TPRegions _instance;
	
	private TPRegionManager _regionManager;
	
	public static TPRegions getInstance() {
		return _instance;
	}
	
	public TPRegions() {
		super();
	}
	
	@Override
	protected void init() {
		_instance = this;
	}

    @Override
    protected void onEnablePlugin() {
        registerListeners();

        _regionManager = new TPRegionManager(getSettings().getNode("regions"));
    }

    @Override
    protected void onDisablePlugin() {

    }

    @Override
	public String getChatPrefix() {
		return ChatColor.GRAY + "[TPR] " + ChatColor.RESET;
	}

	@Override
	public String getConsolePrefix() {
		return "[TPR] ";
	}

	
	public TPRegionManager getRegionManager() {
		return _regionManager;
	}
	
	private void registerListeners() {
        CommandHandler handler = new CommandHandler();
        getCommand("tpr").setExecutor(handler);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new EventListener(), this);
    }
	

}

