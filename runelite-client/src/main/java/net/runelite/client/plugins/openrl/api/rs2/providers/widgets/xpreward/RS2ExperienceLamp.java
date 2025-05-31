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
package net.runelite.client.plugins.openrl.api.rs2.providers.widgets.xpreward;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.RS2ItemQuery;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.RS2Widgets;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Item;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Widget;

@Slf4j
public class RS2ExperienceLamp
{
	private static final Supplier<RS2Widget> CONFIRM_BUTTON = () -> RS2Widgets.getWidget(InterfaceID.Xpreward.CONFIRM);

	/**
	 * Get the xp reward lamp item from inventory.
	 *
	 * @return the xp reward lamp item
	 */
	@Nullable
	public static RS2Item getItem()
	{
		return RS2ItemQuery.inventoryQuery()
			.nameEquals(
				"Lamp",
				"Champion's lamp",
				"Lamp of the gatherer",
				"Antique lamp",
				"Ancient lamp",
				"Blessed lamp",
				"Combat lamp",
				"Dreamy lamp",
				"Dusty lamp",
				"Lamp of knowledge",
				"Magic lamp"
			)
			.first();
	}

	/**
	 * Contains xp reward lamp in inventory.
	 *
	 * @return true if in inventory, false otherwise
	 */
	public static boolean inInventory()
	{
		return getItem() != null;
	}

	/**
	 * Checks if the xp reward lamp interface is open.
	 *
	 * @return true if confirm button is visible, false otherwise
	 */
	public static boolean isOpen()
	{
		return RS2Widgets.isVisible(CONFIRM_BUTTON.get());
	}

	/**
	 * Open the xp reward lamp same as {@link #rub()}
	 */
	public static void open()
	{
		rub();
	}

	/**
	 * Rub the xp reward lamp same as {@link #open()}
	 */
	public static void rub()
	{
		final RS2Item lamp = getItem();
		if (lamp == null)
		{
			return;
		}
		lamp.interact("Rub");
	}

	/**
	 * Select the xp reward skill.
	 *
	 * @param skill
	 */
	public static void chooseSkill(Skill skill)
	{
		final RS2Widget xpRewardSkill = RS2Widgets.getWidget(skill.getInterfaceId());
		if (xpRewardSkill != null)
		{
			xpRewardSkill.interact(skill.getAction());
		}
	}

	/**
	 * Checks if the correct xp reward skill is selected
	 *
	 * @param skill
	 * @return true if the correct xp reward skill is selected, false otherwise
	 */
	public static boolean isSkillSelected(Skill skill)
	{
		final RS2Widget confirmButton = CONFIRM_BUTTON.get();
		if (confirmButton == null)
		{
			return false;
		}

		final RS2Widget[] children = confirmButton.getChildren();
		for (int i = 0; i < children.length; i++)
		{
			final RS2Widget child = children[i];
			if (child == null || child.getText() == null)
			{
				continue;
			}
			if (child.getText().equals("Confirm: " + skill.getAction()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Confirm
	 */
	public static void confirm()
	{
		final RS2Widget confirmButton = CONFIRM_BUTTON.get();
		if (confirmButton != null)
		{
			confirmButton.interact(0);
		}
	}

	/**
	 * The xp reward skill/stat
	 */
	@Getter
	@RequiredArgsConstructor
	public enum Skill
	{
		ATTACK("Attack", InterfaceID.Xpreward.ATTACK),
		STRENGTH("Strength", InterfaceID.Xpreward.STRENGTH),
		RANGED("Ranged", InterfaceID.Xpreward.RANGED),
		MAGIC("Magic", InterfaceID.Xpreward.MAGIC),
		DEFENCE("Defence", InterfaceID.Xpreward.DEFENCE),
		SAILING("Sailing", InterfaceID.Xpreward.SAILING),
		HITPOINTS("Hitpoints", InterfaceID.Xpreward.HITPOINTS),
		PRAYER("Prayer", InterfaceID.Xpreward.PRAYER),
		AGILITY("Agility", InterfaceID.Xpreward.AGILITY),
		HERBLORE("Herblore", InterfaceID.Xpreward.HERBLORE),
		THIEVING("Thieving", InterfaceID.Xpreward.THIEVING),
		CRAFTING("Crafting", InterfaceID.Xpreward.CRAFTING),
		RUNECRAFT("Runecraft", InterfaceID.Xpreward.RUNECRAFT),
		SLAYER("Slayer", InterfaceID.Xpreward.SLAYER),
		FARMING("Farming", InterfaceID.Xpreward.FARMING),
		MINING("Mining", InterfaceID.Xpreward.MINING),
		SMITHING("Smithing", InterfaceID.Xpreward.SMITHING),
		FISHING("Fishing", InterfaceID.Xpreward.FISHING),
		COOKING("Cooking", InterfaceID.Xpreward.COOKING),
		FIREMAKING("Firemaking", InterfaceID.Xpreward.FIREMAKING),
		WOODCUTTING("Woodcutting", InterfaceID.Xpreward.WOODCUTTING),
		FLETCHING("Fletching", InterfaceID.Xpreward.FLETCHING),
		CONSTRUCTION("Construction", InterfaceID.Xpreward.CONSTRUCTION),
		HUNTER("Hunter", InterfaceID.Xpreward.HUNTER);

		private final String action;
		private final int interfaceId;
	}
}