package net.runelite.client.plugins.openrl.api.rs2.magic;

import java.util.Arrays;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.plugins.openrl.api.rs2.items.RS2Equipment;
import net.runelite.client.plugins.openrl.api.rs2.items.RS2Inventory;
import net.runelite.client.plugins.openrl.api.rs2.items.RS2Item;

public enum Rune
{
	AIR(ItemID.AIRRUNE, "Air", "Smoke", "Mist", "Dust"),
	EARTH(ItemID.EARTHRUNE, "Earth", "Lava", "Mud", "Dust"),
	FIRE(ItemID.FIRERUNE, "Fire", "Lava", "Smoke", "Steam"),
	WATER(ItemID.WATERRUNE, "Water", "Mud", "Steam", "Mist"),
	MIND(ItemID.MINDRUNE, "Mind"),
	BODY(ItemID.BODYRUNE, "Body"),
	COSMIC(ItemID.COSMICRUNE, "Cosmic"),
	CHAOS(ItemID.CHAOSRUNE, "Chaos"),
	NATURE(ItemID.NATURERUNE, "Nature"),
	LAW(ItemID.LAWRUNE, "Law"),
	DEATH(ItemID.DEATHRUNE, "Death"),
	ASTRAL(ItemID.ASTRALRUNE, "Astral"),
	BLOOD(ItemID.BLOODRUNE, "Blood"),
	SOUL(ItemID.SOULRUNE, "Soul"),
	WRATH(ItemID.WRATHRUNE, "Wrath");

	private final int runeId;
	private final String[] runeNames;

	Rune(int runeId, String... runeNames)
	{
		this.runeId = runeId;
		this.runeNames = runeNames;
	}

	public String[] getRuneNames()
	{
		return runeNames;
	}

	public int getRuneId()
	{
		return runeId;
	}

	public int getQuantity()
	{
		if (isStaffEquipped() || isTomeEquipped())
		{
			return Integer.MAX_VALUE;
		}

		RS2Item rune = RS2Inventory.getFirst(x -> x.getName() != null && x.getName().contains("rune") &&
			Arrays.stream(runeNames)
				.anyMatch(name -> x.getId() == runeId || x.getName().contains(name)));
		if (rune == null)
		{
			return RunePouch.getQuantity(this);
		}

		return rune.getQuantity() + RunePouch.getQuantity(this);
	}

	private boolean isStaffEquipped()
	{
		return RS2Equipment.contains(x -> x.getName() != null
			&& x.getName().toLowerCase().contains("staff")
			&& Arrays.stream(runeNames).anyMatch(n -> x.getName().toLowerCase().contains(n.toLowerCase())));
	}

	private boolean isTomeEquipped()
	{
		return RS2Equipment.contains(x -> x.getName() != null
			&& x.getName().startsWith("Tome of")
			&& !x.getName().endsWith("(empty")
			&& Arrays.stream(runeNames).anyMatch(n -> x.getName().toLowerCase().contains(n.toLowerCase())));
	}
}
