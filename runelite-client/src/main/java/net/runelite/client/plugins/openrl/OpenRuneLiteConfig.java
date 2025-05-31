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

@ConfigGroup(OpenRuneLiteConfig.GROUP)
public interface OpenRuneLiteConfig extends Config
{
	String GROUP = "openrl";

	@ConfigItem(
		keyName = "overlayEnabled",
		name = "Enable overlay",
		description = "Whether to Enable overlay.",
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

	@ConfigItem(
		keyName = "printMenuActions",
		name = "Print menu actions",
		description = "Whether to print menu actions in console.",
		position = 2
	)
	default boolean printMenuActions()
	{
		return false;
	}

	@ConfigItem(
		keyName = "interactionMethod",
		name = "Interaction setting",
		description = "The interaction to use.",
		position = 3
	)
	default InteractMethod interactionMethod()
	{
		return InteractMethod.NATURAL_MOUSE;
	}
}
