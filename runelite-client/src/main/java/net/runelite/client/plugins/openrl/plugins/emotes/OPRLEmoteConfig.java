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
package net.runelite.client.plugins.openrl.plugins.emotes;

import java.awt.Button;
import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.plugins.openrl.api.rs2.providers.emotes.EmoteID;

@ConfigGroup(OPRLEmoteConfig.GROUP)
public interface OPRLEmoteConfig extends Config
{
	String GROUP = "OPRLEmote";

	@ConfigItem(
		keyName = "overlay",
		name = "Overlay",
		description = "Draw overlay.",
		position = 0
	)
	default boolean overlayEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "overlayColor",
		name = "Overlay color",
		description = "The overlay color if 'Overlay' is enabled.",
		position = 1
	)
	default Color overlayColor()
	{
		return Color.GREEN;
	}

	@ConfigItem(
		keyName = "emote",
		name = "Emote",
		description = "The emote to loop if 'Loop emote' is enabled.",
		position = 2
	)
	default EmoteID emoteId()
	{
		return EmoteID.ANGRY;
	}

	@ConfigItem(
		keyName = "start",
		name = "Start/Stop",
		description = "Start/Stop button",
		position = 3
	)
	default Button startStopButton()
	{
		return new Button();
	}
}