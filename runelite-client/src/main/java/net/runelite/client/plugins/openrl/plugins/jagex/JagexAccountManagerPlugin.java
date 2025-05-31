/*
 * Copyright (c) 2025, Melxin <https://github.com/melxin>
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
package net.runelite.client.plugins.openrl.plugins.jagex;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.openrl.plugins.jagex.ui.JagexAccountManagerPanel;
import net.runelite.client.plugins.openrl.plugins.jagex.ui.LegacyAccountManagerPanel;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

@PluginDescriptor(
	name = "Open RuneLite Jagex account manager",
	description = "Manage Jagex accounts",
	tags = {"jagex", "account", "launcher", "manager", "oauth"},
	enabledByDefault = false
)
@Slf4j
public class JagexAccountManagerPlugin extends Plugin
{
	@Inject
	private EventBus eventBus;
	@Inject
	private ClientToolbar clientToolbar;
	private JagexAccountManagerPanel jagexAccountManagerPanel;
	private NavigationButton navButton;

	private LegacyAccountManagerPanel legacyAccountManagerPanel;
	private NavigationButton legacyAccountManagerNavButton;

	@Inject
	protected JagexAccountManagerPluginConfig config;

	@Provides
	private JagexAccountManagerPluginConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(JagexAccountManagerPluginConfig.class);
	}

	@Subscribe
	protected void onConfigChanged(ConfigChanged configChanged)
	{
		if (!configChanged.getGroup().equals(JagexAccountManagerPluginConfig.GROUP))
		{
			return;
		}

		// Legacy
		if (configChanged.getKey().equals("enableLegacyAccountManagerPanel"))
		{
			if (config.enableLegacyAccountManagerPanel())
			{
				eventBus.register(legacyAccountManagerPanel);
				clientToolbar.addNavigation(legacyAccountManagerNavButton);
				return;
			}
			clientToolbar.removeNavigation(legacyAccountManagerNavButton);
			eventBus.unregister(legacyAccountManagerPanel);
		}
	}

	@Override
	protected void startUp() throws IOException
	{
		jagexAccountManagerPanel = new JagexAccountManagerPanel(config);

		eventBus.register(jagexAccountManagerPanel);

		final BufferedImage icon = ImageUtil.rotateImage(ImageUtil.outlineImage(ImageUtil.loadImageResource(getClass(), "/openrl.png"), Color.RED, true), 80);

		navButton = NavigationButton.builder()
			.tooltip("Jagex account manager")
			.icon(icon)
			.priority(-1)
			.panel(jagexAccountManagerPanel)
			.build();

		clientToolbar.addNavigation(navButton);

		// Legacy
		legacyAccountManagerPanel = new LegacyAccountManagerPanel(config);
		legacyAccountManagerNavButton = NavigationButton.builder()
			.tooltip("Legacy account manager")
			.icon(icon)
			.priority(-2)
			.panel(legacyAccountManagerPanel)
			.build();
		if (config.enableLegacyAccountManagerPanel())
		{
			eventBus.register(legacyAccountManagerPanel);
			clientToolbar.addNavigation(legacyAccountManagerNavButton);
		}
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navButton);
		eventBus.unregister(jagexAccountManagerPanel);

		// Legacy
		clientToolbar.removeNavigation(legacyAccountManagerNavButton);
		eventBus.unregister(legacyAccountManagerPanel);
	}
}