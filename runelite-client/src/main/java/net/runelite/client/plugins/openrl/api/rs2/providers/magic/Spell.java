package net.runelite.client.plugins.openrl.api.rs2.providers.magic;

import net.runelite.api.annotations.Component;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2NPC;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Player;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2TileItem;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2TileObject;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Item;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.api.Interactable;

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

	default void castOn(Interactable interactable)
	{
		if (interactable instanceof RS2Item)
		{
			RS2Magic.cast(this, (RS2Item) interactable);
			return;
		}

		if (interactable instanceof RS2NPC)
		{
			RS2Magic.cast(this, (RS2NPC) interactable);
			return;
		}

		if (interactable instanceof RS2Player)
		{
			RS2Magic.cast(this, (RS2Player) interactable);
			return;
		}

		if (interactable instanceof RS2TileItem)
		{
			RS2Magic.cast(this, (RS2TileItem) interactable);
			return;
		}

		if (interactable instanceof RS2TileObject)
		{
			RS2Magic.cast(this, (RS2TileObject) interactable);
		}
	}
}