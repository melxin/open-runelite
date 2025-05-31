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
package net.runelite.client.plugins.openrl.plugins.test;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(OpenRuneLiteTestPluginConfig.GROUP)
public interface OpenRuneLiteTestPluginConfig extends Config
{
	String GROUP = "TEST";

	@ConfigSection(
		name = "Ground items",
		position = 0,
		description = ""
	)
	String groundItemsSection = "Ground items";

	@ConfigItem(
		keyName = "groundItemActions",
		name = "Ground item actions",
		description = "Logs the nearest ground item actions.",
		position = 1,
		section = groundItemsSection
	)
	default boolean nearestGroundItemActions()
	{
		return false;
	}

	@ConfigSection(
		name = "Interact",
		position = 2,
		description = ""
	)
	String interactSection = "Interact";

	@ConfigItem(
		keyName = "sceneEntity",
		name = "Scene entity",
		description = "The scene entity id or name.",
		position = 3,
		section = interactSection
	)
	default String sceneEntity()
	{
		return "Tree";
	}

	@ConfigItem(
		keyName = "sceneEntityAction",
		name = "Scene entity action",
		description = "The scene entity index or name.",
		position = 4,
		section = interactSection
	)
	default String sceneEntityAction()
	{
		return "0";
	}

	@ConfigItem(
		keyName = "interactSceneEntity",
		name = "Interact scene entity",
		description = "Interact with the scene entity.",
		position = 5,
		section = interactSection
	)
	default boolean interactSceneEntity()
	{
		return false;
	}

	@ConfigSection(
		name = "Inventory",
		position = 6,
		description = ""
	)
	String inventorySection = "Inventory";

	@ConfigItem(
		keyName = "inventoryitem",
		name = "Item",
		description = "The inventory item id or name.",
		position = 7,
		section = inventorySection
	)
	default String inventoryItem()
	{
		return "Bones";
	}

	@ConfigItem(
		keyName = "inventoryItemAction",
		name = "Item action",
		description = "The inventory item action index or name.",
		position = 8,
		section = inventorySection
	)
	default String inventoryItemAction()
	{
		return "0";
	}

	@ConfigItem(
		keyName = "interactInventoryItem",
		name = "Interact",
		description = "Interact with the inventory item.",
		position = 9,
		section = inventorySection
	)
	default boolean interactInventoryItem()
	{
		return false;
	}
}