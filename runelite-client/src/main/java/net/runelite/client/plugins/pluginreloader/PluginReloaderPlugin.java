/*
 * Copyright (c) 2026, Orvian
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.pluginreloader;

import com.google.inject.Inject;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.client.Notifier;
import java.awt.TrayIcon;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

@Slf4j
@PluginDescriptor(
	name = "Plugin Reloader",
	description = "Reload side-loaded plugins",
	developerPlugin = true
)
public class PluginReloaderPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private PluginReloaderConfig config;

	@Inject
	private PluginManager pluginManager;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private ConfigManager configManager;

	@Inject
	private Notifier notifier;

	@Inject
	private ClientToolbar clientToolbar;

	private NavigationButton button;
	private PluginReloaderPanel panel;

	@Provides
	PluginReloaderConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PluginReloaderConfig.class);
	}

	@Override
	protected void startUp()
	{
		panel = new PluginReloaderPanel(this, config);
		BufferedImage icon = ImageUtil.loadImageResource(getClass(), "pluginreloader_icon.png");

		button = NavigationButton.builder()
			.tooltip("Plugin Reloader")
			.icon(icon)
			.priority(10)
			.panel(panel)
			.build();

		clientToolbar.addNavigation(button);
	}

	@Override
	protected void shutDown()
	{
		if (button != null)
		{
			clientToolbar.removeNavigation(button);
			button = null;
		}
		panel = null;
	}

	void reloadPlugins(String input, boolean autoEnable, Consumer<String> statusCallback)
	{
		List<String> selectedPlugins = parseSelectedPlugins(input);
		List<String> validPlugins = validatePlugins(selectedPlugins);
		List<String> invalidPlugins = new ArrayList<>(selectedPlugins);
		invalidPlugins.removeAll(validPlugins);

		if (validPlugins.isEmpty())
		{
			sendChatMessage("No valid plugins found to reload.");
			if (!invalidPlugins.isEmpty())
			{
				sendChatMessage("Not found: " + String.join(", ", invalidPlugins));
			}
			SwingUtilities.invokeLater(() -> statusCallback.accept("✗ No valid plugins found"));
			return;
		}

		// Build status message with partial success info
		final int validCount = validPlugins.size();
		final int invalidCount = invalidPlugins.size();
		final String reloadMsg = "Reloading " + validCount + " plugin" + (validCount > 1 ? "s" : "")
			+ (invalidCount > 0 ? " (" + invalidCount + " invalid)" : "");
		sendChatMessage(reloadMsg + ": " + String.join(", ", validPlugins));
		SwingUtilities.invokeLater(() -> statusCallback.accept(reloadMsg + "..."));

		// Stop old instances before reload
		for (Plugin p : new ArrayList<>(pluginManager.getPlugins()))
		{
			String simpleName = p.getClass().getSimpleName().toLowerCase();
			boolean isSelected = validPlugins.stream().anyMatch(v -> v.replace(".jar", "").equalsIgnoreCase(simpleName.replace("plugin", "")));
			if (isSelected && pluginManager.isPluginActive(p))
			{
				try
				{
					pluginManager.stopPlugin(p);
				}
				catch (Exception e)
				{
					log.warn("Could not stop plugin: {}", p.getClass().getSimpleName());
				}
			}
		}

		// Enable/disable plugins in config before reload
		if (autoEnable)
		{
			enablePluginsInConfig(validPlugins);
		}
		else
		{
			disablePluginsInConfig(validPlugins);
		}

		pluginManager.reloadSideLoaded(validPlugins, () ->
		{
			log.debug("Reload callback executed, autoEnable: {}", autoEnable);
			SwingUtilities.invokeLater(() ->
			{
				if (autoEnable)
				{
					// Ensure reloaded plugins are started
					for (Plugin p : pluginManager.getPlugins())
					{
						String simpleName = p.getClass().getSimpleName().toLowerCase();
						boolean isSelected = validPlugins.stream().anyMatch(v -> v.replace(".jar", "").equalsIgnoreCase(simpleName.replace("plugin", "")));
						log.debug("Checking plugin: {}, isSelected: {}, isActive: {}", simpleName, isSelected, pluginManager.isPluginActive(p));
						if (isSelected && !pluginManager.isPluginActive(p))
						{
							try
							{
								pluginManager.setPluginEnabled(p, true);
								pluginManager.startPlugin(p);
								log.debug("Started plugin: {}", simpleName);
							}
							catch (Exception e)
							{
								log.warn("Could not start plugin: {}", p.getClass().getSimpleName());
							}
						}
					}
					final String successMsg = "✓ Reloaded " + validCount + " plugin" + (validCount > 1 ? "s" : "")
						+ (invalidCount > 0 ? " (" + invalidCount + " invalid)" : "")
						+ (autoEnable ? " & started" : "");
					sendChatMessage(successMsg + ": " + String.join(", ", validPlugins));
					notifier.notify(successMsg, TrayIcon.MessageType.INFO);
					statusCallback.accept(successMsg);
				}
				else
				{
					final String successMsg = "✓ Reloaded " + validCount + " plugin" + (validCount > 1 ? "s" : "")
						+ (invalidCount > 0 ? " (" + invalidCount + " invalid)" : "");
					sendChatMessage(successMsg + ": " + String.join(", ", validPlugins));
					notifier.notify(successMsg, TrayIcon.MessageType.INFO);
					statusCallback.accept(successMsg);
				}
			});
		});
	}

	private List<String> parseSelectedPlugins(String input)
	{
		if (input == null || input.trim().isEmpty())
		{
			return List.of();
		}

		return Arrays.stream(input.split(","))
			.map(String::trim)
			.map(s -> s.replaceAll("[\\[\\]]", "")) // Remove square brackets
			.filter(s -> !s.isEmpty())
			.collect(Collectors.toList());
	}

	private List<String> validatePlugins(List<String> pluginNames)
	{
		List<String> validPlugins = new ArrayList<>();
		Path sideloadedPluginsDir = Paths.get(System.getProperty("user.home"), ".runelite", "sideloaded-plugins");

		for (String pluginName : pluginNames)
		{
			Path pluginPath = sideloadedPluginsDir.resolve(pluginName);
			if (Files.exists(pluginPath))
			{
				validPlugins.add(pluginName);
			}
			else
			{
				log.warn("Plugin file not found: {}", pluginPath);
			}
		}

		return validPlugins;
	}

	private void enablePluginsInConfig(List<String> pluginNames)
	{
		for (String pluginName : pluginNames)
		{
			String className = pluginName.replace(".jar", "") + "plugin";
			try
			{
				configManager.setConfiguration("runelite", className, String.valueOf(true));
			}
			catch (Exception e)
			{
				log.warn("Could not enable plugin in config: {}", className);
			}
		}
	}

	private void disablePluginsInConfig(List<String> pluginNames)
	{
		for (String pluginName : pluginNames)
		{
			String className = pluginName.replace(".jar", "") + "plugin";
			try
			{
				configManager.setConfiguration("runelite", className, String.valueOf(false));
			}
			catch (Exception e)
			{
				log.warn("Could not disable plugin in config: {}", className);
			}
		}
	}

	private void sendChatMessage(String message)
	{
		final ChatMessageBuilder chatMessageBuilder = new ChatMessageBuilder()
			.append(ChatColorType.HIGHLIGHT)
			.append(message)
			.append(ChatColorType.NORMAL);

		chatMessageManager.queue(QueuedMessage.builder()
			.type(ChatMessageType.CONSOLE)
			.runeLiteFormattedMessage(chatMessageBuilder.build())
			.build());
	}
}
