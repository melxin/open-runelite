package net.runelite.client.plugins.openrl.api.rs2.magic;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import net.runelite.api.MenuAction;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.client.plugins.openrl.api.game.Vars;
import net.runelite.client.plugins.openrl.api.rs2.entities.RS2NPC;
import net.runelite.client.plugins.openrl.api.rs2.entities.RS2Player;
import net.runelite.client.plugins.openrl.api.rs2.entities.RS2TileItem;
import net.runelite.client.plugins.openrl.api.rs2.entities.RS2TileObject;
import net.runelite.client.plugins.openrl.api.rs2.items.RS2Item;
import net.runelite.client.plugins.openrl.api.rs2.widgets.RS2Widget;
import net.runelite.client.plugins.openrl.api.rs2.widgets.RS2Widgets;

public class RS2Magic
{
	private static final int AUTOCAST_VARP = 108;

	public static boolean isAutoCasting()
	{
		return Vars.getVarp(VarPlayerID.LASTCASTSPELL) != 0;
	}

	public static boolean isSpellSelected(Spell spell)
	{
		RS2Widget widget = RS2Widgets.getWidget(spell.getInterfaceId());
		if (widget != null)
		{
			return widget.getBorderType() == 2;
		}

		return false;
	}

	public static void cast(Spell spell, RS2Item target)
	{
		selectSpell(spell);
		//target.interact(0, MenuAction.WIDGET_TARGET_ON_WIDGET);
		target.interact("Cast");
	}

	public static void cast(Spell spell, RS2NPC target)
	{
		selectSpell(spell);
		target.interact(MenuAction.WIDGET_TARGET_ON_NPC);
	}

	public static void cast(Spell spell, RS2Player target)
	{
		selectSpell(spell);
		target.interact(MenuAction.WIDGET_TARGET_ON_PLAYER);
	}

	public static void cast(Spell spell, RS2TileItem target)
	{
		selectSpell(spell);
		target.interact(MenuAction.WIDGET_TARGET_ON_GROUND_ITEM);
	}

	public static void cast(Spell spell, RS2TileObject target)
	{
		selectSpell(spell);
		target.interact(MenuAction.WIDGET_TARGET_ON_GAME_OBJECT);
	}

	public static void selectSpell(Spell spell)
	{
		final RS2Widget widget = RS2Widgets.getWidget(spell.getInterfaceId());
		if (widget != null)
		{
			widget.interact("Cast");
		}
		/*if (widget != null)
		{
			Static.getClient().setSelectedSpellWidget(widget.getId());
			Static.getClient().setSelectedSpellChildIndex(-1);
			Static.getClient().setSpellSelected(true);

			Static.getClient().setSelectedSpellActionName("Cast");
			Static.getClient().setSelectedSpellName("<col=" + Integer.toHexString(65280) + widget.getName() + "<col=" + Integer.toHexString(16777215) + ">");
		}*/
	}

	public static void cast(Spell spell)
	{
		cast(spell, 0);
	}

	public static void cast(Spell spell, int actionIndex)
	{
		final RS2Widget widget = RS2Widgets.getWidget(spell.getInterfaceId());
		if (widget != null)
		{
			widget.interact(actionIndex);
		}
	}

	/*public static void cast(Spell spell, int actionIndex, int opcode)
	{
		RS2Widget widget = RS2Widgets.get(spell.getWidget());
		if (widget != null)
		{
			widget.interact(actionIndex, opcode);
		}
	}*/

	public static Instant getLastHomeTeleportUsage()
	{
		return Instant.ofEpochSecond(Vars.getVarp(VarPlayerID.AIDE_TELE_TIMER) * 60L);
	}

	public static boolean isHomeTeleportOnCooldown()
	{
		return !getLastHomeTeleportUsage().plus(30, ChronoUnit.MINUTES).isAfter(Instant.now());
	}
}