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

import com.jcwhatever.bukkit.tpregions.commands.CreateCommand;
import com.jcwhatever.bukkit.tpregions.commands.DelCommand;
import com.jcwhatever.bukkit.tpregions.commands.DisableCommand;
import com.jcwhatever.bukkit.tpregions.commands.EnableCommand;
import com.jcwhatever.bukkit.tpregions.commands.SetCommand;
import com.jcwhatever.bukkit.tpregions.commands.SetYawCommand;
import com.jcwhatever.bukkit.tpregions.commands.list.ListCommand;
import com.jcwhatever.bukkit.tpregions.regions.TPRegionManager;
import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.NucleusPlugin;
import com.jcwhatever.nucleus.managed.scripting.IEvaluatedScript;
import com.jcwhatever.nucleus.managed.scripting.IScriptApi;
import com.jcwhatever.nucleus.managed.scripting.SimpleScriptApi;
import com.jcwhatever.nucleus.managed.scripting.SimpleScriptApi.IApiObjectCreator;
import com.jcwhatever.nucleus.mixins.IDisposable;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

/**
 * Teleport Regions Bukkit plugin.
 */
public class TPRegions  extends NucleusPlugin {

    private static TPRegions _instance;

    private TPRegionManager _regionManager;
    private IScriptApi _scriptApi;

    /**
     * Get the current plugin instance.
     */
    public static TPRegions getPlugin() {
        return _instance;
    }

    /**
     * Get the region manager.
     */
    public static TPRegionManager getRegionManager() {
        return _instance._regionManager;
    }

    @Override
    public String getChatPrefix() {
        return ChatColor.GRAY + "[TPR] " + ChatColor.RESET;
    }

    @Override
    public String getConsolePrefix() {
        return "[TPR] ";
    }

    @Override
    protected void onEnablePlugin() {

        _instance = this;

        _regionManager = new TPRegionManager(getDataNode().getNode("regions"));

        registerCommand(CreateCommand.class);
        registerCommand(DelCommand.class);
        registerCommand(DisableCommand.class);
        registerCommand(EnableCommand.class);
        registerCommand(ListCommand.class);
        registerCommand(SetCommand.class);
        registerCommand(SetYawCommand.class);

        registerEventListeners(new BukkitEventListener());

        _scriptApi = new SimpleScriptApi(this, "tpregions", new IApiObjectCreator() {
            @Override
            public IDisposable create(Plugin plugin, IEvaluatedScript script) {
                return new ScriptApi();
            }
        });

        Nucleus.getScriptApiRepo().registerApi(_scriptApi);
    }

    @Override
    protected void onDisablePlugin() {
        _regionManager.dispose();

        Nucleus.getScriptApiRepo().unregisterApi(_scriptApi);

        _instance = null;
    }
}

