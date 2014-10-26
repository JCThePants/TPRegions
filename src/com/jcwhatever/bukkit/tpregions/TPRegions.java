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

        _regionManager = new TPRegionManager(getDataNode().getNode("regions"));
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

