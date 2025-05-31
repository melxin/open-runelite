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
package net.runelite.client.plugins.openrl.plugins.walker;

import java.awt.Button;
import java.util.Set;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.poh.HousePortal;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.poh.JewelryBox;

@ConfigGroup(WalkerConfig.GROUP)
public interface WalkerConfig extends Config
{
	String GROUP = "Walker";

	@ConfigItem(
		keyName = "testWalkerMenuOptions",
		name = "Test menu options",
		description = "Add test menu options to test walker.",
		position = 0
	)
	default boolean testWalkerMenuOptions()
	{
		return false;
	}

	@ConfigItem(
		keyName = "pathOverlay",
		name = "Path overlay",
		description = "Enables the current path overlay.",
		position = 1
	)
	default boolean pathOverlay()
	{
		return false;
	}

	@ConfigItem(
		keyName = "collisionMapOverlay",
		name = "Collision map overlay",
		description = "Enables the collision map overlay.",
		position = 2
	)
	default boolean collisionMapOverlay()
	{
		return false;
	}

	@ConfigItem(
		keyName = "resetPath",
		name = "Reset path",
		description = "Reset the current path.",
		position = 3
	)
	default Button resetPathButton()
	{
		return new Button();
	}

	@ConfigSection(
		name = "Pathfinder/Regions",
		position = 4,
		description = ""
	)
	String pathfinderSection = "Pathfinder/Regions";

	@ConfigItem(
		keyName = "useTransports",
		name = "Use transports",
		description = "Include transport nodes when calculating paths",
		position = 5,
		section = pathfinderSection
	)
	default boolean useTransports()
	{
		return true;
	}

	@ConfigItem(
		keyName = "minTileDistance",
		name = "Min step distance",
		description = "",
		position = 6,
		section = pathfinderSection
	)
	default int minStepDistance()
	{
		return 7;
	}

	@ConfigItem(
		keyName = "maxTileDistance",
		name = "Max step distance",
		description = "",
		position = 7,
		section = pathfinderSection
	)
	default int maxStepDistance()
	{
		return 14;
	}

	@ConfigItem(
		keyName = "useTeleports",
		name = "Use teleports",
		description = "Include teleportation when calculating paths",
		position = 8,
		section = pathfinderSection
	)
	default boolean useTeleports()
	{
		return true;
	}

	@ConfigItem(
		keyName = "avoidWilderness",
		name = "Avoid Wilderness",
		description = "Avoids walking in the wilderness if the destination is not in the wildy",
		position = 9,
		section = pathfinderSection
	)
	default boolean avoidWilderness()
	{
		return false;
	}

	@ConfigItem(
		keyName = "loadClientCollisionData",
		name = "Load client collision data",
		description = "Loads collision map packaged with client",
		position = 10,
		section = pathfinderSection
	)
	default Button download()
	{
		return new Button();
	}

	@ConfigItem(
		keyName = "localCollisionData",
		name = "Load custom collision data",
		description = "Loads custom collision map from .openosrs directory. The file should be named 'regions'",
		position = 11,
		section = pathfinderSection
	)
	default Button reload()
	{
		return new Button();
	}

	@ConfigItem(
		keyName = "usePoh",
		name = "Use POH",
		description = "",
		position = 12,
		section = pathfinderSection
	)
	default boolean usePoh()
	{
		return false;
	}

	@ConfigItem(
		keyName = "hasMountedGlory",
		name = "Mounted Glory",
		description = "",
		position = 13,
		section = pathfinderSection
	)
	default boolean hasMountedGlory()
	{
		return false;
	}

	@ConfigItem(
		keyName = "hasMountedDigsitePendant",
		name = "Mounted Digsite Pendant",
		description = "",
		position = 14,
		section = pathfinderSection
	)
	default boolean hasMountedDigsitePendant()
	{
		return false;
	}

	@ConfigItem(
		keyName = "hasMountedMythicalCape",
		name = "Mounted Mythical Cape",
		description = "",
		position = 15,
		section = pathfinderSection
	)
	default boolean hasMountedMythicalCape()
	{
		return false;
	}

	@ConfigItem(
		keyName = "hasMountedXericsTalisman",
		name = "Mounted Xerics Talisman",
		description = "",
		position = 16,
		section = pathfinderSection
	)
	default boolean hasMountedXericsTalisman()
	{
		return false;
	}

	@ConfigItem(
		keyName = "hasJewelryBox",
		name = "Jewelry Box",
		description = "",
		position = 17,
		section = pathfinderSection
	)
	default JewelryBox hasJewelryBox()
	{
		return JewelryBox.NONE;
	}

	@ConfigItem(
		keyName = "useEquipmentJewellery",
		name = "Use jewellery from equipment",
		description = "",
		position = 18,
		section = pathfinderSection
	)
	default boolean useEquipmentJewellery()
	{
		return true;
	}

	@ConfigItem(
		keyName = "useEquipmentTeleports",
		name = "Use teleports from equipment",
		description = "",
		position = 19,
		section = pathfinderSection
	)
	default boolean useEquipmentTeleports()
	{
		return true;
	}

	@ConfigItem(
		keyName = "useMinigameTeleports",
		name = "Use minigames teleports",
		description = "",
		position = 20,
		section = pathfinderSection
	)
	default boolean useMinigameTeleports()
	{
		return true;
	}

	@ConfigItem(
		keyName = "useCharterShips",
		name = "Use charter ships",
		description = "",
		position = 21,
		section = pathfinderSection
	)
	default boolean useCharterShips()
	{
		return true;
	}

	@ConfigItem(
		keyName = "housePortals",
		name = "House Portals",
		description = "",
		position = 22,
		section = pathfinderSection
	)
	default Set<HousePortal> housePortals()
	{
		return Set.of();
	}
}