package net.runelite.client.plugins.openrl.external;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.BasePluginLoader;
import org.pf4j.PluginClassLoader;
import org.pf4j.PluginClasspath;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;
import java.nio.file.Path;

@Slf4j
class OPRLExternalBasePluginLoader extends BasePluginLoader
{
	OPRLExternalBasePluginLoader(PluginManager pluginManager, PluginClasspath pluginClasspath)
	{
		super(pluginManager, pluginClasspath);
	}

	@Override
	protected PluginClassLoader createPluginClassLoader(Path pluginPath, PluginDescriptor pluginDescriptor)
	{
		return new OPRLExternalClassLoader(pluginManager, pluginDescriptor, getClass().getClassLoader());
	}
}