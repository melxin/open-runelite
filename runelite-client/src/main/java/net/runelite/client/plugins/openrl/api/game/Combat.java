package net.runelite.client.plugins.openrl.api.game;

import java.util.function.Predicate;
import java.util.function.Supplier;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.plugins.openrl.api.commons.Predicates;
import net.runelite.client.plugins.openrl.api.rs2.providers.entities.RS2NPCs;
import net.runelite.client.plugins.openrl.api.rs2.providers.entities.RS2Players;
import net.runelite.client.plugins.openrl.api.rs2.providers.tabs.RS2Tabs;
import net.runelite.client.plugins.openrl.api.rs2.providers.tabs.Tab;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.RS2Widgets;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2NPC;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Widget;

// @TODO FIX
public class Combat
{
	private static final Supplier<RS2Widget> SPEC_BUTTON = () -> RS2Widgets.getWidget(InterfaceID.CombatInterface.SP_ATTACKBAR);

	private static final int VENOM_THRESHOLD = 1000000;

	public static boolean isRetaliating()
	{
		return Vars.getVarp(VarPlayerID.OPTION_NODEF) == 0;
	}

	public static boolean isPoisoned()
	{
		return Vars.getVarp(VarPlayerID.POISON) > 0;
	}

	public static boolean isVenomed()
	{
		return Vars.getVarp(VarPlayerID.POISON) >= VENOM_THRESHOLD;
	}

	public static boolean isSpecEnabled()
	{
		return Vars.getVarp(VarPlayerID.SA_ATTACK) == 1;
	}

	public static int getSpecEnergy()
	{
		return Vars.getVarp(VarPlayerID.SA_ENERGY) / 10;
	}

	public static boolean isAntifired()
	{
		return Vars.getBit(VarbitID.ANTIFIRE_POTION) > 0;
	}

	public static boolean isSuperAntifired()
	{
		return Vars.getBit(VarbitID.SUPER_ANTIFIRE_POTION) > 0;
	}

	public static void toggleSpec()
	{
		if (isSpecEnabled())
		{
			return;
		}

		final RS2Widget spec = SPEC_BUTTON.get();
		if (spec != null)
		{
			if (RS2Tabs.isOpen(Tab.COMBAT))
			{
				RS2Tabs.open(Tab.COMBAT);
			}

			spec.interact(0);
		}
	}

	/*public static void setAttackStyle(AttackStyle attackStyle)
	{
		if (attackStyle.widgetInfo == null)
		{
			return;
		}

		final RS2Widget widget = RS2Widgets.get(attackStyle.widgetInfo);
		if (widget != null)
		{
			widget.interact(0);
		}
	}

	public static AttackStyle getAttackStyle()
	{
		return AttackStyle.fromIndex(Vars.getVarp(43));
	}*/

	public static RS2NPC getAttackableNPC(int... ids)
	{
		return getAttackableNPC(Predicates.idEquals(ids));
	}

	public static RS2NPC getAttackableNPC(String... names)
	{
		return getAttackableNPC(Predicates.nameContains(names));
	}

	public static RS2NPC getAttackableNPC(Predicate<RS2NPC> filter)
	{
		final Player local = RS2Players.getLocal();
		RS2NPC attackingMe = RS2NPCs.getNearest(x -> x.hasAction("Attack") && RS2Players.getNearest(p -> p.getInteracting() != null
			&& p.getInteracting().equals(x)) == null && x.getInteracting() != null && x.getInteracting().equals(local)
			&& filter.test(x));
		if (attackingMe != null)
		{
			return attackingMe;
		}

		return RS2NPCs.getNearest(x -> x.hasAction("Attack") && RS2Players.getNearest(p -> p.getInteracting() != null
			&& p.getInteracting().equals(x)) == null && x.getInteracting() == null && filter.test(x));
	}

	public static int getCurrentHealth()
	{
		return Skills.getBoostedLevel(Skill.HITPOINTS);
	}

	public static int getMissingHealth()
	{
		return Skills.getLevel(Skill.HITPOINTS) - Skills.getBoostedLevel(Skill.HITPOINTS);
	}

	public static double getHealthPercent()
	{
		return ((double) getCurrentHealth() / Skills.getLevel(Skill.HITPOINTS)) * 100;
	}

	/*public static WeaponStyle getCurrentWeaponStyle()
	{
		Item weapon = Equipment.fromSlot(EquipmentInventorySlot.WEAPON);

		if (weapon == null)
		{
			return WeaponStyle.MELEE;
		}
		else
		{
			return WeaponMap.StyleMap.getOrDefault(weapon.getId(), WeaponStyle.MELEE);
		}
	}

	public enum AttackStyle
	{
		FIRST(0, WidgetInfo.COMBAT_STYLE_ONE),
		SECOND(1, WidgetInfo.COMBAT_STYLE_TWO),
		THIRD(2, WidgetInfo.COMBAT_STYLE_THREE),
		FOURTH(3, WidgetInfo.COMBAT_STYLE_FOUR),
		SPELLS(4, WidgetInfo.COMBAT_SPELL_BOX),
		SPELLS_DEFENSIVE(4, WidgetInfo.COMBAT_DEFENSIVE_SPELL_BOX),
		UNKNOWN(-1, null);

		private final int index;
		private final WidgetInfo widgetInfo;

		AttackStyle(int index, WidgetInfo widgetInfo)
		{
			this.index = index;
			this.widgetInfo = widgetInfo;
		}

		public int getIndex()
		{
			return index;
		}

		public WidgetInfo getWidgetInfo()
		{
			return widgetInfo;
		}

		public static AttackStyle fromIndex(int index)
		{
			return Arrays.stream(values()).filter(x -> x.index == index)
				.findFirst()
				.orElse(UNKNOWN);
		}
	}*/
}