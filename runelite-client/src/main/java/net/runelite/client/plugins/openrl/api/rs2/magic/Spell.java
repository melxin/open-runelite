package net.runelite.client.plugins.openrl.api.rs2.magic;

import net.runelite.api.annotations.Component;
import net.runelite.client.plugins.openrl.api.rs2.entities.RS2NPC;
import net.runelite.client.plugins.openrl.api.rs2.entities.RS2Player;
import net.runelite.client.plugins.openrl.api.rs2.entities.RS2TileItem;
import net.runelite.client.plugins.openrl.api.rs2.entities.RS2TileObject;
import net.runelite.client.plugins.openrl.api.rs2.items.RS2Item;

public interface Spell
{
	int getLevel();

	@Component
	int getInterfaceId();

	boolean canCast();

	default void cast()
	{
		RS2Magic.cast(this);
	}

	default void castOn(RS2Item item)
	{
		RS2Magic.cast(this, item);
	}

	default void castOn(RS2NPC npc)
	{
		RS2Magic.cast(this, npc);
	}

	default void castOn(RS2Player player)
	{
		RS2Magic.cast(this, player);
	}

	default void castOn(RS2TileItem tileItem)
	{
		RS2Magic.cast(this, tileItem);
	}

	default void castOn(RS2TileObject tileObject)
	{
		RS2Magic.cast(this, tileObject);
	}
}
