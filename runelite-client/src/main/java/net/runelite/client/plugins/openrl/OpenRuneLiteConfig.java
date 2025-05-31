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
package net.runelite.client.plugins.openrl;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.openrl.external.OPRLExternalPluginManager;
import net.runelite.client.plugins.openrl.api.managers.InteractionManager;

@ConfigGroup(OpenRuneLiteConfig.GROUP)
public interface OpenRuneLiteConfig extends Config
{
	String GROUP = "openrl";

	@ConfigItem(
		keyName = "overlayEnabled",
		name = "Enable overlay",
		description = "Enable's overlay.",
		position = 0
	)
	default boolean overlayEnabled()
	{
		return false;
	}

	@ConfigItem(
		keyName = "overlayColor",
		name = "Overlay color",
		description = "The overlay color.",
		position = 1
	)
	default Color overlayColor()
	{
		return Color.RED;
	}

	@ConfigSection(
		name = "Interaction",
		position = 2,
		description = ""
	)
	String interaction = "Interaction";

	@ConfigItem(
		keyName = "interactionMethod",
		name = "Interaction setting",
		description = "The automated interaction to use.",
		position = 3,
		section = interaction
	)
	default InteractionManager.InteractMethod interactionMethod()
	{
		return InteractionManager.InteractMethod.INVOKE;
	}

	@ConfigItem(
		keyName = "disableMouse",
		name = "Disable manual input",
		description = "Disables manual input",
		position = 4,
		section = interaction
		//hidden = true
	)
	default boolean disableMouse()
	{
		return false;
	}

	@ConfigItem(
		keyName = "printMenuActions",
		name = "Print menu actions",
		description = "Print menu actions in console.",
		position = 5,
		section = interaction
	)
	default boolean printMenuActions()
	{
		return false;
	}

	@ConfigItem(
		keyName = "naturalMouse",
		name = "Natural mouse",
		description = "Uses the 'natural mouse' algorithm to move and click",
		position = 6,
		section = interaction,
		hidden = true
	)
	default boolean naturalMouse()
	{
		return false;
	}

	@ConfigItem(
		keyName = "alwaysRightClickMenu",
		name = "Always right-click menu",
		description = "Right-click open the menu instead of direct left-click if applicable and interaction setting is set to 'Mouse Events'.",
		position = 7,
		section = interaction,
		hidden = true
	)
	default boolean alwaysRightClickMenu()
	{
		return false;
	}

	@ConfigItem(
		keyName = "mousePositionOverlay",
		name = "Mouse position overlay",
		description = "Overlay's the mouse position.",
		position = 8,
		section = interaction
	)
	default boolean mousePositionOverlay()
	{
		return false;
	}

	@ConfigSection(
		name = "Data collection",
		position = 9,
		description = ""
	)
	String dataCollection = "Data collection";

	@ConfigItem(
		keyName = "disableSession",
		name = "Disable session (online users)",
		description = "Do not send your online status & your client mode to the backend.",
		position = 10,
		section = dataCollection
	)
	default boolean disableSession()
	{
		return false;
	}

	@ConfigItem(
		keyName = "disableLinkBrowser",
		name = "Disable link browser",
		description = "Do not open any link e.g wiki.",
		position = 11,
		section = dataCollection
	)
	default boolean disableLinkBrowser()
	{
		return false;
	}

	@ConfigSection(
		name = "Misc",
		position = 12,
		description = ""
	)
	String misc = "Misc";

	@ConfigItem(
		keyName = "disableGroups",
		name = "Disable Groups/Party",
		description = "Disables the Party functionality (needs a restart).",
		position = 13,
		section = misc
	)
	default boolean disableGroups()
	{
		return true;
	}

	@ConfigItem(
		keyName = "neverLogout",
		name = "Never logout",
		description = "Do not logout until 6 hour timer.",
		position = 14,
		section = misc
	)
	default boolean neverLogout()
	{
		return false;
	}

	@ConfigItem(
		keyName = "skipWelcomeScreen",
		name = "Skip welcome screen",
		description = "Skip the login welcome screen.",
		position = 15,
		section = misc
	)
	default boolean skipWelcomeScreen()
	{
		return false;
	}

	@ConfigSection(
		name = "External",
		position = 16,
		description = ""
	)
	String external = "External";

	@ConfigItem(
		keyName = "externalRepos",
		name = "",
		description = "",
		position = 17,
		section = external,
		hidden = true
	)
	default String getExternalRepositories()
	{
		return OPRLExternalPluginManager.DEFAULT_PLUGIN_REPOS;
	}

	@ConfigItem(
		keyName = "externalRepos",
		name = "",
		description = "",
		position = 18,
		section = external,
		hidden = true
	)
	void setExternalRepositories(String val);

	@ConfigItem(
		keyName = "warning",
		name = "",
		description = "",
		position = 19,
		section = external,
		hidden = true
	)
	default boolean warning()
	{
		return true;
	}
}