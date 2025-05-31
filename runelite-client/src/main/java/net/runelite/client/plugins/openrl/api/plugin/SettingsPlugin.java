package net.runelite.client.plugins.openrl.api.plugin;

import net.runelite.client.config.Config;
import net.runelite.client.plugins.Plugin;

public abstract class SettingsPlugin extends LoopedPlugin
{
	public abstract Config getConfig();

	public abstract String getPluginName();

	public abstract String getPluginDescription();

	public abstract String[] getPluginTags();
}
