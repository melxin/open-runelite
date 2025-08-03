/*
 * Copyright (c) 2022, Melxin <https://github.com/melxin/>
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
package net.runelite.client.plugins.openrl.plugins.castlewars;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("castlewars")
public interface CastleWarsConfig extends Config
{
	@ConfigItem(
		name = "Mirror Mode Compatibility?",
		keyName = "mirrorMode",
		description = "Should we show the overlay on Mirror Mode?",
		position = 0
	)
	default boolean mirrorMode()
	{
		return false;
	}

	@ConfigItem(
		keyName = "hidePlayerOptions",
		name = "Quick use",
		description = "Hide player options when using explosive,tinderbox or bucket",
		position = 1
	)
	default boolean hidePlayerOptions()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hideFlagOptions",
		name = "Quick flag grab",
		description = "Hide npc & player options if the flag is on the same location for quick grab.",
		position = 2
	)
	default boolean hideFlagOptions()
	{
		return true;
	}

	@ConfigItem(
		keyName = "displayOpenTunnels",
		name = "Display open tunnels",
		description = "Display which tunnel underground is open.",
		position = 3
	)
	default boolean displayOpenTunnels()
	{
		return true;
	}

	@ConfigItem(
		keyName = "barricadeHighlight",
		name = "Barricade highlight",
		description = "Set highlight on barricades for each team color.",
		position = 4
	)
	default boolean barricadeHighlight()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hexColorSaradominBarricade",
		name = "Saradomin barricade highlight color",
		description = "Color of saradomin barricade highlight.",
		position = 5
	)
	default Color getSaradominHighlightColor()
	{
		return Color.BLUE;
	}

	@ConfigItem(
		keyName = "hexColorZamorakBarricade",
		name = "Zamorak barricade highlight color",
		description = "Color of zamorak barricade highlight.",
		position = 6
	)
	default Color getZamorakHighlightColor()
	{
		return Color.RED;
	}

	@ConfigItem(
		keyName = "rocksHighlight",
		name = "Underground rocks highlight",
		description = "Highlight underground rocks.",
		position = 7
	)
	default boolean rocksHighlight()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hexColorRocks",
		name = "Rocks highlight color",
		description = "Color of underground rocks highlight.",
		position = 8
	)
	default Color getRocksHighlightColor()
	{
		return Color.GRAY;
	}

	@ConfigItem(
		keyName = "useTindTimer",
		name = "Tind timer",
		description = "Use timer for lit barricades.",
		position = 9
	)
	default boolean useTindTimer()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hexColorLit",
		name = "Tind timer color",
		description = "Color of tind timer.",
		position = 10
	)
	default Color getLitColor()
	{
		return Color.YELLOW;
	}

	@ConfigItem(
		keyName = "hexColorEmpty",
		name = "Tind timer nearly finished color",
		description = "Color of timer when almost finished.",
		position = 11
	)
	default Color getEmptyColor()
	{
		return Color.RED;
	}

	@ConfigItem(
		keyName = "doorsHighlight",
		name = "Doors highlight",
		description = "Highlight doors.",
		position = 12
	)
	default boolean doorsHighlight()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hexColorOpenDoors",
		name = "Open door highlight color",
		description = "Color of open door highlight.",
		position = 13
	)
	default Color getOpenDoorsHighlightColor()
	{
		return Color.GREEN;
	}

	@ConfigItem(
		keyName = "hexColorClosedDoors",
		name = "Closed door highlight color",
		description = "Color of closed doors highlight.",
		position = 14
	)
	default Color getClosedDoorsHighlightColor()
	{
		return Color.RED;
	}

	@ConfigItem(
		keyName = "tapHighlight",
		name = "Tap highlight",
		description = "Highlight tap.",
		position = 15
	)
	default boolean tapHighlight()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hexColorTap",
		name = "Tap highlight color",
		description = "Color of tap.",
		position = 16
	)
	default Color getTapHighlightColor()
	{
		return Color.blue;
	}

	@ConfigItem(
		keyName = "groundItemHighlight",
		name = "GroundItem Highlight",
		description = "Highlight GroundItems, Tinderbox/Bucket.",
		position = 17
	)
	default boolean groundItemHighlight()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hexColorGroundItems",
		name = "GroundItems highlight color",
		description = "Color of GroundItems.",
		position = 18
	)
	default Color getGroundItemHighlightColor()
	{
		return Color.magenta;
	}

	@ConfigItem(
		keyName = "caveHighlight",
		name = "cave Highlight",
		description = "Highlight underground caves.",
		position = 19
	)
	default boolean caveHighlight()
	{
		return true;
	}

	@ConfigItem(
		keyName = "caveHighlightAsCircle",
		name = "cave Highlight circle",
		description = "Highlight as circle instead of convexhull.",
		position = 20
	)
	default boolean caveHighlightAsCircle()
	{
		return false;
	}

	@ConfigItem(
		keyName = "hexColorCaves",
		name = "Cave highlight color",
		description = "Color of underground caves.",
		position = 21
	)
	default Color getCaveHighlightColor()
	{
		return Color.darkGray;
	}

	@ConfigItem(
		keyName = "hideCastPlayers",
		name = "Hide cast players",
		description = "Hide player menu's from same team upon casting.",
		position = 22
	)
	default boolean hideCastPlayers()
	{
		return true;
	}

	@ConfigItem(
		keyName = "autoJoin",
		name = "Auto join game",
		description = "Auto skip the do you want to join dialogue.",
		position = 23
	)
	default boolean autoJoin()
	{
		return false;
	}

	@ConfigItem(
		keyName = "Surrender",
		name = "Surrender",
		description = "Surrender/teleport at low hitpoints by invoking 'Surrender' action on the cape.",
		position = 24
	)
	default boolean surrender()
	{
		return false;
	}
}