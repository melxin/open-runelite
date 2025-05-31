package net.runelite.client.plugins.openrl.external;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.JarPluginLoader;
import org.pf4j.PluginClassLoader;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;
import java.nio.file.Path;

@Slf4j
class OPRLExternalJarPluginLoader extends JarPluginLoader
{
	public OPRLExternalJarPluginLoader(PluginManager pluginManager)
	{
		super(pluginManager);
	}

	@Override
	public ClassLoader loadPlugin(Path pluginPath, PluginDescriptor pluginDescriptor)
	{
		final PluginClassLoader pluginClassLoader = new OPRLExternalClassLoader(pluginManager, pluginDescriptor, getClass().getClassLoader());
		pluginClassLoader.addFile(pluginPath.toFile());

		return pluginClassLoader;
	}
}