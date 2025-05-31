package net.runelite.client.plugins.openrl.api.rs2.magic;

import java.util.Arrays;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.annotations.Component;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.plugins.openrl.api.game.Vars;
import net.runelite.client.plugins.openrl.api.rs2.items.RS2Equipment;
import net.runelite.client.plugins.openrl.api.rs2.items.RS2Inventory;
import net.runelite.client.plugins.openrl.api.game.Skills;

public enum SpellBook
{
	STANDARD(0),
	ANCIENT(1),
	LUNAR(2),
	NECROMANCY(3);

	private static final int SPELLBOOK_VARBIT = 4070;

	private final int varbitValue;

	SpellBook(int varbitValue)
	{
		this.varbitValue = varbitValue;
	}

	public static SpellBook getCurrent()
	{
		return Arrays.stream(values()).filter(x -> Vars.getBit(SPELLBOOK_VARBIT) == x.varbitValue)
			.findFirst().orElse(null);
	}

	public enum Standard implements Spell
	{
		// Teleport spells
		HOME_TELEPORT(
			0,
			InterfaceID.MagicSpellbook.TELEPORT_HOME_STANDARD,
			false
		),
		VARROCK_TELEPORT(
			25,
			InterfaceID.MagicSpellbook.VARROCK_TELEPORT,
			false,
			new RuneRequirement(3, Rune.AIR),
			new RuneRequirement(1, Rune.FIRE),
			new RuneRequirement(1, Rune.LAW)
		),
		LUMBRIDGE_TELEPORT(
			31,
			InterfaceID.MagicSpellbook.LUMBRIDGE_TELEPORT,
			false,
			new RuneRequirement(3, Rune.AIR),
			new RuneRequirement(1, Rune.EARTH),
			new RuneRequirement(1, Rune.LAW)
		),
		FALADOR_TELEPORT(
			37,
			InterfaceID.MagicSpellbook.FALADOR_TELEPORT,
			false,
			new RuneRequirement(3, Rune.AIR),
			new RuneRequirement(1, Rune.WATER),
			new RuneRequirement(1, Rune.LAW)
		),
		TELEPORT_TO_HOUSE(
			40,
			InterfaceID.MagicSpellbook.TELEPORT_TO_YOUR_HOUSE,
			true,
			new RuneRequirement(1, Rune.AIR),
			new RuneRequirement(1, Rune.EARTH),
			new RuneRequirement(1, Rune.LAW)
		),
		CAMELOT_TELEPORT(
			45,
			InterfaceID.MagicSpellbook.CAMELOT_TELEPORT,
			true,
			new RuneRequirement(5, Rune.AIR),
			new RuneRequirement(1, Rune.LAW)
		),
		TELEPORT_TO_KOUREND(
			48,
			InterfaceID.MagicSpellbook.KOUREND_TELEPORT,
			true,
			new RuneRequirement(2, Rune.LAW),
			new RuneRequirement(1, Rune.WATER),
			new RuneRequirement(1, Rune.FIRE)
		),
		ARDOUGNE_TELEPORT(
			51,
			InterfaceID.MagicSpellbook.ARDOUGNE_TELEPORT,
			true,
			new RuneRequirement(2, Rune.WATER),
			new RuneRequirement(2, Rune.LAW)
		),
		CIVITAS_ILLA_FORTIS_TELEPORT(
			54,
			InterfaceID.MagicSpellbook.FORTIS_TELEPORT,
			true,
			new RuneRequirement(2, Rune.LAW),
			new RuneRequirement(1, Rune.EARTH),
			new RuneRequirement(1, Rune.FIRE)
		),
		WATCHTOWER_TELEPORT(
			58,
			InterfaceID.MagicSpellbook.WATCHTOWER_TELEPORT,
			true,
			new RuneRequirement(2, Rune.EARTH),
			new RuneRequirement(2, Rune.LAW)
		),
		TROLLHEIM_TELEPORT(
			61,
			InterfaceID.MagicSpellbook.TROLLHEIM_TELEPORT,
			true,
			new RuneRequirement(2, Rune.FIRE),
			new RuneRequirement(2, Rune.LAW)
		),
		TELEPORT_TO_APE_ATOLL(
			64,
			InterfaceID.MagicSpellbook.TELEPORT_APE_ATOLL_DUNGEON,
			true,
			new RuneRequirement(2, Rune.FIRE),
			new RuneRequirement(2, Rune.WATER),
			new RuneRequirement(2, Rune.LAW)
		),
		TELEOTHER_LUMBRIDGE(
			74,
			InterfaceID.MagicSpellbook.TELEOTHER_LUMBRIDGE,
			true,
			new RuneRequirement(1, Rune.EARTH),
			new RuneRequirement(1, Rune.LAW),
			new RuneRequirement(1, Rune.SOUL)
		),
		TELEOTHER_FALADOR(
			82,
			InterfaceID.MagicSpellbook.TELEOTHER_FALADOR,
			true,
			new RuneRequirement(1, Rune.WATER),
			new RuneRequirement(1, Rune.LAW),
			new RuneRequirement(1, Rune.SOUL)
		),
		TELEPORT_TO_BOUNTY_TARGET(
			85,
			InterfaceID.MagicSpellbook.BOUNTY_TARGET,
			true,
			new RuneRequirement(1, Rune.CHAOS),
			new RuneRequirement(1, Rune.DEATH),
			new RuneRequirement(1, Rune.LAW)
		),
		TELEOTHER_CAMELOT(
			90,
			InterfaceID.MagicSpellbook.TELEOTHER_CAMELOT,
			true,
			new RuneRequirement(1, Rune.LAW),
			new RuneRequirement(2, Rune.SOUL)
		),

		// Strike spells
		WIND_STRIKE(
			1,
			InterfaceID.MagicSpellbook.WIND_STRIKE,
			false,
			new RuneRequirement(1, Rune.AIR),
			new RuneRequirement(1, Rune.MIND)
		),
		WATER_STRIKE(
			5,
			InterfaceID.MagicSpellbook.WATER_STRIKE,
			false,
			new RuneRequirement(1, Rune.AIR),
			new RuneRequirement(1, Rune.WATER),
			new RuneRequirement(1, Rune.MIND)
		),
		EARTH_STRIKE(
			9,
			InterfaceID.MagicSpellbook.EARTH_STRIKE,
			false,
			new RuneRequirement(1, Rune.AIR),
			new RuneRequirement(2, Rune.EARTH),
			new RuneRequirement(1, Rune.MIND)
		),
		FIRE_STRIKE(
			13,
			InterfaceID.MagicSpellbook.FIRE_STRIKE,
			false,
			new RuneRequirement(2, Rune.AIR),
			new RuneRequirement(3, Rune.FIRE),
			new RuneRequirement(1, Rune.MIND)
		),

		// Bolt spells
		WIND_BOLT(
			17,
			InterfaceID.MagicSpellbook.WIND_BOLT,
			false,
			new RuneRequirement(2, Rune.AIR),
			new RuneRequirement(1, Rune.CHAOS)
		),
		WATER_BOLT(
			23,
			InterfaceID.MagicSpellbook.WATER_BOLT,
			false,
			new RuneRequirement(2, Rune.AIR),
			new RuneRequirement(2, Rune.WATER),
			new RuneRequirement(1, Rune.CHAOS)
		),
		EARTH_BOLT(
			29,
			InterfaceID.MagicSpellbook.EARTH_BOLT,
			false,
			new RuneRequirement(2, Rune.AIR),
			new RuneRequirement(3, Rune.EARTH),
			new RuneRequirement(1, Rune.CHAOS)
		),
		FIRE_BOLT(
			35,
			InterfaceID.MagicSpellbook.FIRE_BOLT,
			false,
			new RuneRequirement(3, Rune.AIR),
			new RuneRequirement(4, Rune.FIRE),
			new RuneRequirement(1, Rune.CHAOS)
		),

		// Blast spells
		WIND_BLAST(
			41,
			InterfaceID.MagicSpellbook.WIND_BLAST,
			false,
			new RuneRequirement(3, Rune.AIR),
			new RuneRequirement(1, Rune.DEATH)
		),
		WATER_BLAST(
			47,
			InterfaceID.MagicSpellbook.WATER_BLAST,
			false,
			new RuneRequirement(3, Rune.AIR),
			new RuneRequirement(3, Rune.WATER),
			new RuneRequirement(1, Rune.DEATH)
		),
		EARTH_BLAST(
			53,
			InterfaceID.MagicSpellbook.EARTH_BLAST,
			false,
			new RuneRequirement(3, Rune.AIR),
			new RuneRequirement(4, Rune.EARTH),
			new RuneRequirement(1, Rune.DEATH)
		),
		FIRE_BLAST(
			59,
			InterfaceID.MagicSpellbook.FIRE_BLAST,
			false,
			new RuneRequirement(4, Rune.AIR),
			new RuneRequirement(5, Rune.FIRE),
			new RuneRequirement(1, Rune.DEATH)
		),

		// Wave spells
		WIND_WAVE(
			62,
			InterfaceID.MagicSpellbook.WIND_WAVE,
			true,
			new RuneRequirement(5, Rune.AIR),
			new RuneRequirement(1, Rune.BLOOD)
		),
		WATER_WAVE(
			65,
			InterfaceID.MagicSpellbook.WATER_WAVE,
			true,
			new RuneRequirement(5, Rune.AIR),
			new RuneRequirement(7, Rune.WATER),
			new RuneRequirement(1, Rune.BLOOD)
		),
		EARTH_WAVE(
			70,
			InterfaceID.MagicSpellbook.EARTH_WAVE,
			true,
			new RuneRequirement(5, Rune.AIR),
			new RuneRequirement(7, Rune.EARTH),
			new RuneRequirement(1, Rune.BLOOD)
		),
		FIRE_WAVE(
			75,
			InterfaceID.MagicSpellbook.FIRE_WAVE,
			true,
			new RuneRequirement(5, Rune.AIR),
			new RuneRequirement(7, Rune.FIRE),
			new RuneRequirement(1, Rune.BLOOD)
		),

		// Surge spells
		WIND_SURGE(
			81,
			InterfaceID.MagicSpellbook.WIND_SURGE,
			true,
			new RuneRequirement(7, Rune.AIR),
			new RuneRequirement(1, Rune.WRATH)
		),
		WATER_SURGE(
			85,
			InterfaceID.MagicSpellbook.WATER_SURGE,
			true,
			new RuneRequirement(7, Rune.AIR),
			new RuneRequirement(10, Rune.WATER),
			new RuneRequirement(1, Rune.WRATH)
		),
		EARTH_SURGE(
			90,
			InterfaceID.MagicSpellbook.EARTH_SURGE,
			true,
			new RuneRequirement(7, Rune.AIR),
			new RuneRequirement(10, Rune.EARTH),
			new RuneRequirement(1, Rune.WRATH)
		),
		FIRE_SURGE(
			95,
			InterfaceID.MagicSpellbook.FIRE_SURGE,
			true,
			new RuneRequirement(7, Rune.AIR),
			new RuneRequirement(10, Rune.FIRE),
			new RuneRequirement(1, Rune.WRATH)
		),

		// God spells
		SARADOMIN_STRIKE(
			60,
			InterfaceID.MagicSpellbook.SARADOMIN_STRIKE,
			true,
			new RuneRequirement(4, Rune.AIR),
			new RuneRequirement(2, Rune.FIRE),
			new RuneRequirement(2, Rune.BLOOD)
		),
		CLAWS_OF_GUTHIX(
			60,
			InterfaceID.MagicSpellbook.CLAWS_OF_GUTHIX,
			true,
			new RuneRequirement(4, Rune.AIR),
			new RuneRequirement(1, Rune.FIRE),
			new RuneRequirement(2, Rune.BLOOD)
		),
		FLAMES_OF_ZAMORAK(
			60,
			InterfaceID.MagicSpellbook.FLAMES_OF_ZAMORAK,
			true,
			new RuneRequirement(1, Rune.AIR),
			new RuneRequirement(4, Rune.FIRE),
			new RuneRequirement(2, Rune.BLOOD)
		),

		// Other combat spells
		CRUMBLE_UNDEAD(
			39,
			InterfaceID.MagicSpellbook.CRUMBLE_UNDEAD,
			false,
			new RuneRequirement(2, Rune.AIR),
			new RuneRequirement(2, Rune.EARTH),
			new RuneRequirement(1, Rune.CHAOS)
		),
		IBAN_BLAST(
			50,
			InterfaceID.MagicSpellbook.IBAN_BLAST,
			true,
			new RuneRequirement(5, Rune.FIRE),
			new RuneRequirement(1, Rune.DEATH)
		),
		MAGIC_DART(
			50,
			InterfaceID.MagicSpellbook.MAGIC_DART,
			true,
			new RuneRequirement(1, Rune.DEATH),
			new RuneRequirement(4, Rune.MIND)
		),

		// Curse spells
		CONFUSE(
			3,
			InterfaceID.MagicSpellbook.CONFUSE,
			false,
			new RuneRequirement(2, Rune.EARTH),
			new RuneRequirement(3, Rune.WATER),
			new RuneRequirement(1, Rune.BODY)
		),
		WEAKEN(
			11,
			InterfaceID.MagicSpellbook.WEAKEN,
			false,
			new RuneRequirement(2, Rune.EARTH),
			new RuneRequirement(3, Rune.WATER),
			new RuneRequirement(1, Rune.BODY)
		),
		CURSE(
			19,
			InterfaceID.MagicSpellbook.CURSE,
			false,
			new RuneRequirement(3, Rune.EARTH),
			new RuneRequirement(2, Rune.WATER),
			new RuneRequirement(1, Rune.BODY)
		),
		BIND(
			20,
			InterfaceID.MagicSpellbook.BIND,
			false,
			new RuneRequirement(3, Rune.EARTH),
			new RuneRequirement(3, Rune.WATER),
			new RuneRequirement(2, Rune.NATURE)
		),
		SNARE(
			50,
			InterfaceID.MagicSpellbook.SNARE,
			false,
			new RuneRequirement(4, Rune.EARTH),
			new RuneRequirement(4, Rune.WATER),
			new RuneRequirement(3, Rune.NATURE)
		),
		VULNERABILITY(
			66,
			InterfaceID.MagicSpellbook.VULNERABILITY,
			true,
			new RuneRequirement(5, Rune.EARTH),
			new RuneRequirement(5, Rune.WATER),
			new RuneRequirement(1, Rune.SOUL)
		),
		ENFEEBLE(
			73,
			InterfaceID.MagicSpellbook.ENFEEBLE,
			true,
			new RuneRequirement(8, Rune.EARTH),
			new RuneRequirement(8, Rune.WATER),
			new RuneRequirement(1, Rune.SOUL)
		),
		ENTANGLE(
			79,
			InterfaceID.MagicSpellbook.ENTANGLE,
			true,
			new RuneRequirement(5, Rune.EARTH),
			new RuneRequirement(5, Rune.WATER),
			new RuneRequirement(4, Rune.NATURE)
		),
		STUN(
			80,
			InterfaceID.MagicSpellbook.STUN,
			true,
			new RuneRequirement(12, Rune.EARTH),
			new RuneRequirement(12, Rune.WATER),
			new RuneRequirement(1, Rune.SOUL)
		),
		TELE_BLOCK(
			85,
			InterfaceID.MagicSpellbook.TELEPORT_BLOCK,
			false,
			new RuneRequirement(1, Rune.CHAOS),
			new RuneRequirement(1, Rune.DEATH),
			new RuneRequirement(1, Rune.LAW)
		),

		// Support spells
		CHARGE(
			80,
			InterfaceID.MagicSpellbook.CHARGE,
			true,
			new RuneRequirement(3, Rune.AIR),
			new RuneRequirement(3, Rune.FIRE),
			new RuneRequirement(3, Rune.BLOOD)
		),

		// Utility spells
		BONES_TO_BANANAS(
			15,
			InterfaceID.MagicSpellbook.BONES_BANANAS,
			false,
			new RuneRequirement(2, Rune.EARTH),
			new RuneRequirement(2, Rune.WATER),
			new RuneRequirement(1, Rune.NATURE)
		),
		LOW_LEVEL_ALCHEMY(
			21,
			InterfaceID.MagicSpellbook.LOW_ALCHEMY,
			false,
			new RuneRequirement(3, Rune.FIRE),
			new RuneRequirement(1, Rune.NATURE)
		),
		SUPERHEAT_ITEM(
			43,
			InterfaceID.MagicSpellbook.SUPERHEAT,
			false,
			new RuneRequirement(4, Rune.FIRE),
			new RuneRequirement(1, Rune.NATURE)
		),
		HIGH_LEVEL_ALCHEMY(
			55,
			InterfaceID.MagicSpellbook.HIGH_ALCHEMY,
			false,
			new RuneRequirement(5, Rune.FIRE),
			new RuneRequirement(1, Rune.NATURE)
		),
		BONES_TO_PEACHES(
			60,
			InterfaceID.MagicSpellbook.BONES_PEACHES,
			true,
			new RuneRequirement(2, Rune.EARTH),
			new RuneRequirement(4, Rune.WATER),
			new RuneRequirement(2, Rune.NATURE)
		),

		// Enchantment spells
		LVL_1_ENCHANT(
			7,
			InterfaceID.MagicSpellbook.ENCHANT_1,
			false,
			new RuneRequirement(1, Rune.WATER),
			new RuneRequirement(1, Rune.COSMIC)
		),
		LVL_2_ENCHANT(
			27,
			InterfaceID.MagicSpellbook.ENCHANT_2,
			false,
			new RuneRequirement(3, Rune.AIR),
			new RuneRequirement(1, Rune.COSMIC)
		),
		LVL_3_ENCHANT(
			49,
			InterfaceID.MagicSpellbook.ENCHANT_3,
			false,
			new RuneRequirement(5, Rune.FIRE),
			new RuneRequirement(1, Rune.COSMIC)
		),
		CHARGE_WATER_ORB(
			56,
			InterfaceID.MagicSpellbook.CHARGE_WATER_ORB,
			true,
			new RuneRequirement(30, Rune.WATER),
			new RuneRequirement(3, Rune.COSMIC)
		),
		LVL_4_ENCHANT(
			57,
			InterfaceID.MagicSpellbook.ENCHANT_4,
			false,
			new RuneRequirement(10, Rune.EARTH),
			new RuneRequirement(1, Rune.COSMIC)
		),
		CHARGE_EARTH_ORB(
			60,
			InterfaceID.MagicSpellbook.CHARGE_EARTH_ORB,
			true,
			new RuneRequirement(30, Rune.EARTH),
			new RuneRequirement(3, Rune.COSMIC)
		),
		CHARGE_FIRE_ORB(
			63,
			InterfaceID.MagicSpellbook.CHARGE_FIRE_ORB,
			true,
			new RuneRequirement(30, Rune.FIRE),
			new RuneRequirement(3, Rune.COSMIC)
		),
		CHARGE_AIR_ORB(
			66,
			InterfaceID.MagicSpellbook.CHARGE_AIR_ORB,
			true,
			new RuneRequirement(30, Rune.AIR),
			new RuneRequirement(3, Rune.COSMIC)
		),
		LVL_5_ENCHANT(
			68,
			InterfaceID.MagicSpellbook.ENCHANT_5,
			true,
			new RuneRequirement(15, Rune.EARTH),
			new RuneRequirement(15, Rune.WATER),
			new RuneRequirement(1, Rune.COSMIC)
		),
		LVL_6_ENCHANT(
			87,
			InterfaceID.MagicSpellbook.ENCHANT_6,
			true,
			new RuneRequirement(20, Rune.EARTH),
			new RuneRequirement(20, Rune.FIRE),
			new RuneRequirement(1, Rune.COSMIC)
		),
		LVL_7_ENCHANT(
			93,
			InterfaceID.MagicSpellbook.ENCHANT_7,
			true,
			new RuneRequirement(20, Rune.BLOOD),
			new RuneRequirement(20, Rune.SOUL),
			new RuneRequirement(1, Rune.COSMIC)
		),

		// Other spells
		TELEKINETIC_GRAB(
			31,
			InterfaceID.MagicSpellbook.TELEGRAB,
			false,
			new RuneRequirement(1, Rune.AIR),
			new RuneRequirement(1, Rune.LAW)
		),
		;

		private final int level;
		private final int interfaceId;
		private final boolean members;
		private final RuneRequirement[] requirements;

		Standard(int level, @Component int interfaceId, boolean members, RuneRequirement... requirements)
		{
			this.level = level;
			this.interfaceId = interfaceId;
			this.members = members;
			this.requirements = requirements;
		}

		@Override
		public int getLevel()
		{
			return level;
		}

		@Override
		public int getInterfaceId()
		{
			return interfaceId;
		}

		@Override
		public boolean canCast()
		{
			if (getCurrent() != STANDARD)
			{
				return false;
			}

			/*if (members && !Worlds.inMembersWorld())
			{
				return false;
			}*/

			if (this == HOME_TELEPORT)
			{
				return RS2Magic.isHomeTeleportOnCooldown();
			}

			if (level > Skills.getLevel(Skill.MAGIC) || level > Skills.getBoostedLevel(Skill.MAGIC))
			{
				return false;
			}

			if (this == ARDOUGNE_TELEPORT && Vars.getVarp(165) < 30)
			{
				return false;
			}

			if (this == TROLLHEIM_TELEPORT && Vars.getVarp(335) < 110)
			{
				return false;
			}

			return haveEquipment() && haveItem() && haveRunesAvailable();
		}

		public boolean haveRunesAvailable()
		{
			for (RuneRequirement req : requirements)
			{
				if (!req.meetsRequirements())
				{
					return false;
				}
			}

			return true;
		}

		public boolean haveEquipment()
		{
			switch (this)
			{
				case IBAN_BLAST:
					return RS2Equipment.contains(ItemID.IBANS_STAFF, ItemID.IBANS_STAFF_1410, ItemID.IBANS_STAFF_U);
				case MAGIC_DART:
					return RS2Equipment.contains(ItemID.SLAYERS_STAFF_E, ItemID.SLAYERS_STAFF, ItemID.STAFF_OF_THE_DEAD, ItemID.STAFF_OF_THE_DEAD_23613, ItemID.TOXIC_STAFF_OF_THE_DEAD, ItemID.STAFF_OF_LIGHT, ItemID.STAFF_OF_BALANCE);
				case SARADOMIN_STRIKE:
					return RS2Equipment.contains(ItemID.SARADOMIN_STAFF, ItemID.STAFF_OF_LIGHT);
				case FLAMES_OF_ZAMORAK:
					return RS2Equipment.contains(ItemID.ZAMORAK_STAFF, ItemID.STAFF_OF_THE_DEAD, ItemID.STAFF_OF_THE_DEAD_23613, ItemID.TOXIC_STAFF_OF_THE_DEAD);
				case CLAWS_OF_GUTHIX:
					return RS2Equipment.contains(ItemID.GUTHIX_STAFF, ItemID.VOID_KNIGHT_MACE, ItemID.STAFF_OF_BALANCE);
				default:
					return true;
			}
		}

		public boolean haveItem()
		{
			switch (this)
			{
				case TELEPORT_TO_APE_ATOLL:
					return RS2Inventory.contains(ItemID.BANANA);
				case CHARGE_AIR_ORB:
				case CHARGE_WATER_ORB:
				case CHARGE_EARTH_ORB:
				case CHARGE_FIRE_ORB:
					return RS2Inventory.contains(ItemID.UNPOWERED_ORB);
				default:
					return true;
			}
		}
	}

	public enum Ancient implements Spell
	{
		// Teleport spells
		EDGEVILLE_HOME_TELEPORT(
			0,
			InterfaceID.MagicSpellbook.TELEPORT_HOME_STANDARD
		),
		PADDEWWA_TELEPORT(
			54,
			InterfaceID.MagicSpellbook.TELEPORT_HOME_STANDARD,
			new RuneRequirement(1, Rune.AIR),
			new RuneRequirement(1, Rune.FIRE),
			new RuneRequirement(2, Rune.LAW)
		),
		SENNTISTEN_TELEPORT(
			60,
			InterfaceID.MagicSpellbook.TELEPORT_HOME_STANDARD,
			new RuneRequirement(2, Rune.LAW),
			new RuneRequirement(1, Rune.SOUL)
		),
		KHARYRLL_TELEPORT(
			66,
			InterfaceID.MagicSpellbook.TELEPORT_HOME_STANDARD,
			new RuneRequirement(2, Rune.LAW),
			new RuneRequirement(1, Rune.BLOOD)
		),
		LASSAR_TELEPORT(
			72,
			InterfaceID.MagicSpellbook.TELEPORT_HOME_STANDARD,
			new RuneRequirement(4, Rune.WATER),
			new RuneRequirement(2, Rune.LAW)
		),
		DAREEYAK_TELEPORT(
			78,
			InterfaceID.MagicSpellbook.TELEPORT_HOME_STANDARD,
			new RuneRequirement(2, Rune.AIR),
			new RuneRequirement(3, Rune.FIRE),
			new RuneRequirement(2, Rune.LAW)
		),
		CARRALLANGER_TELEPORT(
			84,
			InterfaceID.MagicSpellbook.TELEPORT_HOME_STANDARD,
			new RuneRequirement(2, Rune.LAW),
			new RuneRequirement(2, Rune.SOUL)
		),
		BOUNTY_TARGET_TELEPORT(
			85,
			InterfaceID.MagicSpellbook.BOUNTY_TARGET,
			new RuneRequirement(1, Rune.CHAOS),
			new RuneRequirement(1, Rune.DEATH),
			new RuneRequirement(1, Rune.LAW)
		),
		ANNAKARL_TELEPORT(
			90,
			InterfaceID.MagicSpellbook.TELEPORT_HOME_STANDARD,
			new RuneRequirement(2, Rune.LAW),
			new RuneRequirement(2, Rune.BLOOD)
		),
		GHORROCK_TELEPORT(
			96,
			InterfaceID.MagicSpellbook.TELE_GHORROCK,
			new RuneRequirement(8, Rune.WATER),
			new RuneRequirement(2, Rune.LAW)
		),

		// Rush Spells
		SMOKE_RUSH(
			50,
			InterfaceID.MagicSpellbook.SMOKE_RUSH,
			new RuneRequirement(1, Rune.AIR),
			new RuneRequirement(1, Rune.FIRE),
			new RuneRequirement(2, Rune.CHAOS),
			new RuneRequirement(2, Rune.DEATH)
		),
		SHADOW_RUSH(
			52,
			InterfaceID.MagicSpellbook.SHADOW_RUSH,
			new RuneRequirement(1, Rune.AIR),
			new RuneRequirement(2, Rune.CHAOS),
			new RuneRequirement(2, Rune.DEATH),
			new RuneRequirement(1, Rune.SOUL)
		),
		BLOOD_RUSH(
			56,
			InterfaceID.MagicSpellbook.BLOOD_RUSH,
			new RuneRequirement(2, Rune.CHAOS),
			new RuneRequirement(2, Rune.DEATH),
			new RuneRequirement(1, Rune.BLOOD)
		),
		ICE_RUSH(
			58,
			InterfaceID.MagicSpellbook.ICE_RUSH,
			new RuneRequirement(2, Rune.WATER),
			new RuneRequirement(2, Rune.CHAOS),
			new RuneRequirement(2, Rune.DEATH)
		),

		// Burst Spells
		SMOKE_BURST(
			62,
			InterfaceID.MagicSpellbook.SMOKE_BURST,
			new RuneRequirement(2, Rune.AIR),
			new RuneRequirement(2, Rune.FIRE),
			new RuneRequirement(4, Rune.CHAOS),
			new RuneRequirement(2, Rune.DEATH)
		),
		SHADOW_BURST(
			64,
			InterfaceID.MagicSpellbook.SHADOW_BURST,
			new RuneRequirement(1, Rune.AIR),
			new RuneRequirement(4, Rune.CHAOS),
			new RuneRequirement(2, Rune.DEATH),
			new RuneRequirement(2, Rune.SOUL)
		),
		BLOOD_BURST(
			68,
			InterfaceID.MagicSpellbook.BLOOD_BURST,
			new RuneRequirement(2, Rune.CHAOS),
			new RuneRequirement(4, Rune.DEATH),
			new RuneRequirement(2, Rune.BLOOD)
		),
		ICE_BURST(
			70,
			InterfaceID.MagicSpellbook.ICE_BURST,
			new RuneRequirement(4, Rune.WATER),
			new RuneRequirement(4, Rune.CHAOS),
			new RuneRequirement(2, Rune.DEATH)
		),

		// Blitz Spells
		SMOKE_BLITZ(
			74,
			InterfaceID.MagicSpellbook.SMOKE_BLITZ,
			new RuneRequirement(2, Rune.AIR),
			new RuneRequirement(2, Rune.FIRE),
			new RuneRequirement(2, Rune.DEATH),
			new RuneRequirement(2, Rune.BLOOD)
		),
		SHADOW_BLITZ(
			76,
			InterfaceID.MagicSpellbook.SHADOW_BLITZ,
			new RuneRequirement(2, Rune.AIR),
			new RuneRequirement(2, Rune.DEATH),
			new RuneRequirement(2, Rune.BLOOD),
			new RuneRequirement(2, Rune.SOUL)
		),
		BLOOD_BLITZ(
			80,
			InterfaceID.MagicSpellbook.BLOOD_BLITZ,
			new RuneRequirement(2, Rune.DEATH),
			new RuneRequirement(4, Rune.BLOOD)
		),
		ICE_BLITZ(
			82,
			InterfaceID.MagicSpellbook.ICE_BLITZ,
			new RuneRequirement(3, Rune.WATER),
			new RuneRequirement(2, Rune.DEATH),
			new RuneRequirement(2, Rune.BLOOD)
		),

		// Barrage Spells
		SMOKE_BARRAGE(
			86,
			InterfaceID.MagicSpellbook.SMOKE_BARRAGE,
			new RuneRequirement(4, Rune.AIR),
			new RuneRequirement(4, Rune.FIRE),
			new RuneRequirement(4, Rune.DEATH),
			new RuneRequirement(2, Rune.BLOOD)
		),
		SHADOW_BARRAGE(
			88,
			InterfaceID.MagicSpellbook.SHADOW_BARRAGE,
			new RuneRequirement(4, Rune.AIR),
			new RuneRequirement(4, Rune.DEATH),
			new RuneRequirement(2, Rune.BLOOD),
			new RuneRequirement(3, Rune.SOUL)
		),
		BLOOD_BARRAGE(
			92,
			InterfaceID.MagicSpellbook.BLOOD_BARRAGE,
			new RuneRequirement(4, Rune.DEATH),
			new RuneRequirement(4, Rune.BLOOD),
			new RuneRequirement(1, Rune.SOUL)
		),
		ICE_BARRAGE(
			94,
			InterfaceID.MagicSpellbook.ICE_BARRAGE,
			new RuneRequirement(6, Rune.WATER),
			new RuneRequirement(4, Rune.DEATH),
			new RuneRequirement(2, Rune.BLOOD)
		);

		private final int level;
		private final int interfaceId;
		private final RuneRequirement[] requirements;

		Ancient(int level, @Component int interfaceId, RuneRequirement... requirements)
		{
			this.level = level;
			this.interfaceId = interfaceId;
			this.requirements = requirements;
		}

		@Override
		public int getLevel()
		{
			return level;
		}

		@Override
		public int getInterfaceId()
		{
			return interfaceId;
		}

		public boolean canCast()
		{
			if (getCurrent() != ANCIENT)
			{
				return false;
			}

			/*if (!Worlds.inMembersWorld())
			{
				return false;
			}*/

			if (this == EDGEVILLE_HOME_TELEPORT)
			{
				return RS2Magic.isHomeTeleportOnCooldown();
			}

			if (level > Skills.getLevel(Skill.MAGIC) || level > Skills.getBoostedLevel(Skill.MAGIC))
			{
				return false;
			}

			return haveRunesAvailable();
		}

		public boolean haveRunesAvailable()
		{
			for (RuneRequirement req : requirements)
			{
				if (!req.meetsRequirements())
				{
					return false;
				}
			}

			return true;
		}
	}

	public enum Lunar implements Spell
	{
		// Teleport spells
		LUNAR_HOME_TELEPORT(
			0,
			InterfaceID.MagicSpellbook.TELEPORT_HOME_LUNAR
		),
		MOONCLAN_TELEPORT(
			69,
			InterfaceID.MagicSpellbook.TELE_MOONCLAN,
			new RuneRequirement(2, Rune.EARTH),
			new RuneRequirement(2, Rune.ASTRAL),
			new RuneRequirement(1, Rune.LAW)
		),
		TELE_GROUP_MOONCLAN(
			70,
			InterfaceID.MagicSpellbook.TELE_GROUP_MOONCLAN,
			new RuneRequirement(4, Rune.EARTH),
			new RuneRequirement(2, Rune.ASTRAL),
			new RuneRequirement(1, Rune.LAW)
		),
		OURANIA_TELEPORT(
			71,
			InterfaceID.MagicSpellbook.OURANIA_TELEPORT,
			new RuneRequirement(6, Rune.EARTH),
			new RuneRequirement(2, Rune.ASTRAL),
			new RuneRequirement(1, Rune.LAW)
		),
		WATERBIRTH_TELEPORT(
			72,
			InterfaceID.MagicSpellbook.TELE_WATERBIRTH,
			new RuneRequirement(1, Rune.WATER),
			new RuneRequirement(2, Rune.ASTRAL),
			new RuneRequirement(1, Rune.LAW)
		),
		TELE_GROUP_WATERBIRTH(
			73,
			InterfaceID.MagicSpellbook.TELE_GROUP_WATERBIRTH,
			new RuneRequirement(5, Rune.WATER),
			new RuneRequirement(2, Rune.ASTRAL),
			new RuneRequirement(1, Rune.LAW)
		),
		BARBARIAN_TELEPORT(
			75,
			InterfaceID.MagicSpellbook.TELE_BARB_OUT,
			new RuneRequirement(3, Rune.FIRE),
			new RuneRequirement(2, Rune.ASTRAL),
			new RuneRequirement(2, Rune.LAW)
		),
		TELE_GROUP_BARBARIAN(
			76,
			InterfaceID.MagicSpellbook.TELE_GROUP_BARBARIAN,
			new RuneRequirement(6, Rune.FIRE),
			new RuneRequirement(2, Rune.ASTRAL),
			new RuneRequirement(2, Rune.LAW)
		),
		KHAZARD_TELEPORT(
			78,
			InterfaceID.MagicSpellbook.TELE_KHAZARD,
			new RuneRequirement(4, Rune.WATER),
			new RuneRequirement(2, Rune.ASTRAL),
			new RuneRequirement(2, Rune.LAW)
		),
		TELE_GROUP_KHAZARD(
			79,
			InterfaceID.MagicSpellbook.TELE_GROUP_KHAZARD,
			new RuneRequirement(8, Rune.WATER),
			new RuneRequirement(2, Rune.ASTRAL),
			new RuneRequirement(2, Rune.LAW)
		),
		FISHING_GUILD_TELEPORT(
			85,
			InterfaceID.MagicSpellbook.TELE_FISH,
			new RuneRequirement(10, Rune.WATER),
			new RuneRequirement(3, Rune.ASTRAL),
			new RuneRequirement(3, Rune.LAW)
		),
		TELE_GROUP_FISHING_GUILD(
			86,
			InterfaceID.MagicSpellbook.TELE_GROUP_FISHING_GUILD,
			new RuneRequirement(14, Rune.WATER),
			new RuneRequirement(3, Rune.ASTRAL),
			new RuneRequirement(3, Rune.LAW)
		),
		CATHERBY_TELEPORT(
			87,
			InterfaceID.MagicSpellbook.TELE_CATHER,
			new RuneRequirement(10, Rune.WATER),
			new RuneRequirement(3, Rune.ASTRAL),
			new RuneRequirement(3, Rune.LAW)
		),
		TELE_GROUP_CATHERBY(
			88,
			InterfaceID.MagicSpellbook.TELE_GROUP_CATHERBY,
			new RuneRequirement(15, Rune.WATER),
			new RuneRequirement(3, Rune.ASTRAL),
			new RuneRequirement(3, Rune.LAW)
		),
		ICE_PLATEAU_TELEPORT(
			89,
			InterfaceID.MagicSpellbook.TELEPORT_HOME_STANDARD,
			new RuneRequirement(8, Rune.WATER),
			new RuneRequirement(3, Rune.ASTRAL),
			new RuneRequirement(3, Rune.LAW)
		),
		TELE_GROUP_ICE_PLATEAU(
			90,
			InterfaceID.MagicSpellbook.TELEPORT_HOME_STANDARD,
			new RuneRequirement(16, Rune.WATER),
			new RuneRequirement(3, Rune.ASTRAL),
			new RuneRequirement(3, Rune.LAW)
		),

		// Combat spells
		MONSTER_EXAMINE(
			66,
			InterfaceID.MagicSpellbook.MONSTER_EXAMINE,
			new RuneRequirement(1, Rune.MIND),
			new RuneRequirement(1, Rune.COSMIC),
			new RuneRequirement(1, Rune.ASTRAL)
		),
		CURE_OTHER(
			66,
			InterfaceID.MagicSpellbook.CURE_OTHER,
			new RuneRequirement(10, Rune.EARTH),
			new RuneRequirement(1, Rune.ASTRAL),
			new RuneRequirement(1, Rune.LAW)
		),
		CURE_ME(
			66,
			InterfaceID.MagicSpellbook.CURE_ME,
			new RuneRequirement(2, Rune.COSMIC),
			new RuneRequirement(2, Rune.ASTRAL),
			new RuneRequirement(1, Rune.LAW)
		),
		CURE_GROUP(
			66,
			InterfaceID.MagicSpellbook.CURE_GROUP,
			new RuneRequirement(2, Rune.COSMIC),
			new RuneRequirement(2, Rune.ASTRAL),
			new RuneRequirement(2, Rune.LAW)
		),
		STAT_SPY(
			66,
			InterfaceID.MagicSpellbook.STAT_SPY,
			new RuneRequirement(5, Rune.BODY),
			new RuneRequirement(1, Rune.COSMIC),
			new RuneRequirement(2, Rune.ASTRAL)
		),
		DREAM(
			66,
			InterfaceID.MagicSpellbook.DREAM,
			new RuneRequirement(5, Rune.BODY),
			new RuneRequirement(1, Rune.COSMIC),
			new RuneRequirement(2, Rune.ASTRAL)
		),
		STAT_RESTORE_POT_SHARE(
			66,
			InterfaceID.MagicSpellbook.REST_POT_SHARE,
			new RuneRequirement(10, Rune.WATER),
			new RuneRequirement(10, Rune.EARTH),
			new RuneRequirement(2, Rune.ASTRAL)
		),
		BOOST_POTION_SHARE(
			66,
			InterfaceID.MagicSpellbook.STREN_POT_SHARE,
			new RuneRequirement(10, Rune.WATER),
			new RuneRequirement(12, Rune.EARTH),
			new RuneRequirement(3, Rune.ASTRAL)
		),
		ENERGY_TRANSFER(
			66,
			InterfaceID.MagicSpellbook.ENERGY_TRANS,
			new RuneRequirement(3, Rune.ASTRAL),
			new RuneRequirement(1, Rune.NATURE),
			new RuneRequirement(2, Rune.LAW)
		),
		HEAL_OTHER(
			66,
			InterfaceID.MagicSpellbook.HEAL_OTHER,
			new RuneRequirement(3, Rune.ASTRAL),
			new RuneRequirement(3, Rune.LAW),
			new RuneRequirement(1, Rune.BLOOD)
		),
		VENGEANCE_OTHER(
			66,
			InterfaceID.MagicSpellbook.VENGEANCE_OTHER,
			new RuneRequirement(10, Rune.EARTH),
			new RuneRequirement(3, Rune.ASTRAL),
			new RuneRequirement(2, Rune.DEATH)
		),
		VENGEANCE(
			66,
			InterfaceID.MagicSpellbook.VENGEANCE,
			new RuneRequirement(10, Rune.EARTH),
			new RuneRequirement(4, Rune.ASTRAL),
			new RuneRequirement(2, Rune.DEATH)
		),
		HEAL_GROUP(
			66,
			InterfaceID.MagicSpellbook.HEAL_GROUP,
			new RuneRequirement(4, Rune.ASTRAL),
			new RuneRequirement(6, Rune.LAW),
			new RuneRequirement(3, Rune.BLOOD)
		),

		// Utility spells
		BAKE_PIE(
			66,
			InterfaceID.MagicSpellbook.BAKE_PIE,
			new RuneRequirement(4, Rune.WATER),
			new RuneRequirement(5, Rune.FIRE),
			new RuneRequirement(1, Rune.ASTRAL)
		),
		GEOMANCY(
			66,
			InterfaceID.MagicSpellbook.GEOMANCY,
			new RuneRequirement(8, Rune.EARTH),
			new RuneRequirement(3, Rune.ASTRAL),
			new RuneRequirement(3, Rune.NATURE)
		),
		CURE_PLANT(
			66,
			InterfaceID.MagicSpellbook.CURE_PLANT,
			new RuneRequirement(8, Rune.EARTH),
			new RuneRequirement(1, Rune.ASTRAL)
		),
		NPC_CONTACT(
			66,
			InterfaceID.MagicSpellbook.NPC_CONTACT,
			new RuneRequirement(2, Rune.AIR),
			new RuneRequirement(1, Rune.COSMIC),
			new RuneRequirement(1, Rune.ASTRAL)
		),
		HUMIDIFY(
			66,
			InterfaceID.MagicSpellbook.HUMIDIFY,
			new RuneRequirement(3, Rune.WATER),
			new RuneRequirement(1, Rune.FIRE),
			new RuneRequirement(1, Rune.ASTRAL)
		),
		HUNTER_KIT(
			66,
			InterfaceID.MagicSpellbook.HUNTER_KIT,
			new RuneRequirement(2, Rune.EARTH),
			new RuneRequirement(2, Rune.ASTRAL)
		),
		SPIN_FLAX(
			66,
			InterfaceID.MagicSpellbook.SPIN_FLAX,
			new RuneRequirement(5, Rune.AIR),
			new RuneRequirement(1, Rune.ASTRAL),
			new RuneRequirement(2, Rune.NATURE)
		),
		SUPERGLASS_MAKE(
			66,
			InterfaceID.MagicSpellbook.SUPERGLASS,
			new RuneRequirement(10, Rune.AIR),
			new RuneRequirement(6, Rune.FIRE),
			new RuneRequirement(2, Rune.ASTRAL)
		),
		TAN_LEATHER(
			66,
			InterfaceID.MagicSpellbook.TAN_LEATHER,
			new RuneRequirement(5, Rune.FIRE),
			new RuneRequirement(2, Rune.ASTRAL),
			new RuneRequirement(1, Rune.NATURE)
		),
		STRING_JEWELLERY(
			66,
			InterfaceID.MagicSpellbook.STRING_JEWEL,
			new RuneRequirement(10, Rune.EARTH),
			new RuneRequirement(5, Rune.WATER),
			new RuneRequirement(2, Rune.ASTRAL)
		),
		MAGIC_IMBUE(
			66,
			InterfaceID.MagicSpellbook.MAGIC_IMBUE,
			new RuneRequirement(7, Rune.WATER),
			new RuneRequirement(7, Rune.FIRE),
			new RuneRequirement(2, Rune.ASTRAL)
		),
		FERTILE_SOIL(
			66,
			InterfaceID.MagicSpellbook.FERTILE_SOIL,
			new RuneRequirement(15, Rune.EARTH),
			new RuneRequirement(3, Rune.ASTRAL),
			new RuneRequirement(2, Rune.NATURE)
		),
		PLANK_MAKE(
			66,
			InterfaceID.MagicSpellbook.PLANK_MAKE,
			new RuneRequirement(15, Rune.EARTH),
			new RuneRequirement(2, Rune.ASTRAL),
			new RuneRequirement(1, Rune.NATURE)
		),
		RECHARGE_DRAGONSTONE(
			66,
			InterfaceID.MagicSpellbook.RECHARGE_DRAGONSTONE,
			new RuneRequirement(4, Rune.WATER),
			new RuneRequirement(1, Rune.ASTRAL),
			new RuneRequirement(1, Rune.SOUL)
		),
		SPELLBOOK_SWAP(
			66,
			InterfaceID.MagicSpellbook.SPELLBOOK_SWAP,
			new RuneRequirement(2, Rune.COSMIC),
			new RuneRequirement(3, Rune.ASTRAL),
			new RuneRequirement(1, Rune.LAW)
		),
		;

		private final int level;
		private final int interfaceId;
		private final RuneRequirement[] requirements;

		Lunar(int level, @Component int interfaceId, RuneRequirement... requirements)
		{
			this.level = level;
			this.interfaceId = interfaceId;
			this.requirements = requirements;
		}

		@Override
		public int getLevel()
		{
			return level;
		}

		@Override
		@Component
		public int getInterfaceId()
		{
			return interfaceId;
		}

		public boolean canCast()
		{
			if (getCurrent() != LUNAR)
			{
				return false;
			}

			/*if (!Worlds.inMembersWorld())
			{
				return false;
			}*/

			if (this == LUNAR_HOME_TELEPORT)
			{
				return RS2Magic.isHomeTeleportOnCooldown();
			}

			if (level > Skills.getLevel(Skill.MAGIC) || level > Skills.getBoostedLevel(Skill.MAGIC))
			{
				return false;
			}

			return haveRunesAvailable();
		}

		public boolean haveRunesAvailable()
		{
			for (RuneRequirement req : requirements)
			{
				if (!req.meetsRequirements())
				{
					return false;
				}
			}

			return true;
		}
	}

	public enum Necromancy implements Spell
	{
		// Teleport spells
		ARCEUUS_HOME_TELEPORT(
			1,
			InterfaceID.MagicSpellbook.TELEPORT_HOME_ARCEUUS
		),
		ARCEUUS_LIBRARY_TELEPORT(
			6,
			InterfaceID.MagicSpellbook.TELEPORT_ARCEUUS_LIBRARY,
			new RuneRequirement(2, Rune.EARTH),
			new RuneRequirement(1, Rune.LAW)
		),
		DRAYNOR_MANOR_TELEPORT(
			17,
			InterfaceID.MagicSpellbook.TELEPORT_DRAYNOR_MANOR,
			new RuneRequirement(1, Rune.EARTH),
			new RuneRequirement(1, Rune.WATER),
			new RuneRequirement(1, Rune.LAW)
		),
		BATTLEFRONT_TELEPORT(
			23,
			InterfaceID.MagicSpellbook.TELEPORT_BATTLEFRONT,
			new RuneRequirement(1, Rune.EARTH),
			new RuneRequirement(1, Rune.FIRE),
			new RuneRequirement(1, Rune.LAW)
		),
		MIND_ALTAR_TELEPORT(
			28,
			InterfaceID.MagicSpellbook.TELEPORT_MIND_ALTAR,
			new RuneRequirement(2, Rune.MIND),
			new RuneRequirement(1, Rune.LAW)
		),
		RESPAWN_TELEPORT(
			34,
			InterfaceID.MagicSpellbook.TELEPORT_RESPAWN,
			new RuneRequirement(1, Rune.SOUL),
			new RuneRequirement(1, Rune.LAW)
		),
		SALVE_GRAVEYARD_TELEPORT(
			40,
			InterfaceID.MagicSpellbook.TELEPORT_SALVE_GRAVEYARD,
			new RuneRequirement(2, Rune.SOUL),
			new RuneRequirement(1, Rune.LAW)
		),
		FENKENSTRAINS_CASTLE_TELEPORT(
			48,
			InterfaceID.MagicSpellbook.TELEPORT_FENKENSTRAIN_CASTLE,
			new RuneRequirement(1, Rune.EARTH),
			new RuneRequirement(1, Rune.SOUL),
			new RuneRequirement(1, Rune.LAW)
		),
		WEST_ARDOUGNE_TELEPORT(
			61,
			InterfaceID.MagicSpellbook.TELEPORT_WEST_ARDOUGNE,
			new RuneRequirement(2, Rune.SOUL),
			new RuneRequirement(2, Rune.LAW)
		),
		HARMONY_ISLAND_TELEPORT(
			65,
			InterfaceID.MagicSpellbook.TELEPORT_HARMONY_ISLAND,
			new RuneRequirement(1, Rune.NATURE),
			new RuneRequirement(1, Rune.SOUL),
			new RuneRequirement(1, Rune.LAW)
		),
		CEMETERY_TELEPORT(
			71,
			InterfaceID.MagicSpellbook.TELEPORT_CEMETERY,
			new RuneRequirement(1, Rune.BLOOD),
			new RuneRequirement(1, Rune.SOUL),
			new RuneRequirement(1, Rune.LAW)
		),
		BARROWS_TELEPORT(
			83,
			InterfaceID.MagicSpellbook.TELEPORT_BARROWS,
			new RuneRequirement(1, Rune.BLOOD),
			new RuneRequirement(2, Rune.SOUL),
			new RuneRequirement(2, Rune.LAW)
		),
		APE_ATOLL_TELEPORT(
			90,
			InterfaceID.MagicSpellbook.APE_TELEPORT,
			new RuneRequirement(2, Rune.BLOOD),
			new RuneRequirement(2, Rune.SOUL),
			new RuneRequirement(2, Rune.LAW)
		),

		// Combat spells
		GHOSTLY_GRASP(
			35,
			InterfaceID.MagicSpellbook.GHOSTLY_GRASP,
			new RuneRequirement(4, Rune.AIR),
			new RuneRequirement(1, Rune.CHAOS)
		),
		SKELETAL_GRASP(
			56,
			InterfaceID.MagicSpellbook.SKELETAL_GRASP,
			new RuneRequirement(8, Rune.EARTH),
			new RuneRequirement(1, Rune.DEATH)
		),
		UNDEAD_GRASP(
			79,
			InterfaceID.MagicSpellbook.UNDEAD_GRASP,
			new RuneRequirement(12, Rune.FIRE),
			new RuneRequirement(1, Rune.BLOOD)
		),
		INFERIOR_DEMONBANE(
			44,
			InterfaceID.MagicSpellbook.INFERIOR_DEMONBANE,
			new RuneRequirement(4, Rune.FIRE),
			new RuneRequirement(1, Rune.CHAOS)
		),
		SUPERIOR_DEMONBANE(
			62,
			InterfaceID.MagicSpellbook.SUPERIOR_DEMONBANE,
			new RuneRequirement(8, Rune.FIRE),
			new RuneRequirement(1, Rune.SOUL)
		),
		DARK_DEMONBANE(
			82,
			InterfaceID.MagicSpellbook.DARK_DEMONBANE,
			new RuneRequirement(12, Rune.FIRE),
			new RuneRequirement(2, Rune.SOUL)
		),
		LESSER_CORRUPTION(
			64,
			InterfaceID.MagicSpellbook.LESSER_CORRUPTION,
			new RuneRequirement(1, Rune.DEATH),
			new RuneRequirement(2, Rune.SOUL)
		),
		GREATER_CORRUPTION(
			85,
			InterfaceID.MagicSpellbook.GREATER_CORRUPTION,
			new RuneRequirement(1, Rune.BLOOD),
			new RuneRequirement(3, Rune.SOUL)
		),
		RESURRECT_LESSER_GHOST(
			38,
			InterfaceID.MagicSpellbook.RESURRECT_LESSER_GHOST,
			new RuneRequirement(10, Rune.AIR),
			new RuneRequirement(1, Rune.COSMIC),
			new RuneRequirement(5, Rune.MIND)
		),
		RESURRECT_LESSER_SKELETON(
			38,
			InterfaceID.MagicSpellbook.RESURRECT_LESSER_SKELETON,
			new RuneRequirement(10, Rune.AIR),
			new RuneRequirement(1, Rune.COSMIC),
			new RuneRequirement(5, Rune.MIND)
		),
		RESURRECT_LESSER_ZOMBIE(
			38,
			InterfaceID.MagicSpellbook.RESURRECT_LESSER_ZOMBIE,
			new RuneRequirement(10, Rune.AIR),
			new RuneRequirement(1, Rune.COSMIC),
			new RuneRequirement(5, Rune.MIND)
		),
		RESURRECT_SUPERIOR_GHOST(
			57,
			InterfaceID.MagicSpellbook.RESURRECT_SUPERIOR_GHOST,
			new RuneRequirement(10, Rune.EARTH),
			new RuneRequirement(1, Rune.COSMIC),
			new RuneRequirement(5, Rune.DEATH)
		),
		RESURRECT_SUPERIOR_SKELETON(
			57,
			InterfaceID.MagicSpellbook.RESURRECT_SUPERIOR_SKELETON,
			new RuneRequirement(10, Rune.EARTH),
			new RuneRequirement(1, Rune.COSMIC),
			new RuneRequirement(5, Rune.DEATH)
		),
		RESURRECT_SUPERIOR_ZOMBIE(
			57,
			InterfaceID.MagicSpellbook.RESURRECT_SUPERIOR_ZOMBIE,
			new RuneRequirement(10, Rune.EARTH),
			new RuneRequirement(1, Rune.COSMIC),
			new RuneRequirement(5, Rune.DEATH)
		),
		RESURRECT_GREATER_GHOST(
			76,
			InterfaceID.MagicSpellbook.RESURRECT_GREATER_GHOST,
			new RuneRequirement(10, Rune.FIRE),
			new RuneRequirement(1, Rune.COSMIC),
			new RuneRequirement(5, Rune.BLOOD)
		),
		RESURRECT_GREATER_SKELETON(
			76,
			InterfaceID.MagicSpellbook.RESURRECT_GREATER_SKELETON,
			new RuneRequirement(10, Rune.FIRE),
			new RuneRequirement(1, Rune.COSMIC),
			new RuneRequirement(5, Rune.BLOOD)
		),
		RESURRECT_GREATER_ZOMBIE(
			76,
			InterfaceID.MagicSpellbook.RESURRECT_GREATER_ZOMBIE,
			new RuneRequirement(10, Rune.FIRE),
			new RuneRequirement(1, Rune.COSMIC),
			new RuneRequirement(5, Rune.BLOOD)
		),
		DARK_LURE(
			50,
			InterfaceID.MagicSpellbook.DARK_LURE,
			new RuneRequirement(1, Rune.DEATH),
			new RuneRequirement(1, Rune.NATURE)
		),
		MARK_OF_DARKNESS(
			59,
			InterfaceID.MagicSpellbook.MARK_OF_DARKNESS,
			new RuneRequirement(1, Rune.COSMIC),
			new RuneRequirement(1, Rune.SOUL)
		),
		WARD_OF_ARCEUUS(
			73,
			InterfaceID.MagicSpellbook.WARD_OF_ARCEUUS,
			new RuneRequirement(1, Rune.COSMIC),
			new RuneRequirement(2, Rune.NATURE),
			new RuneRequirement(4, Rune.SOUL)
		),

		// Utility spells
		BASIC_REANIMATION(
			16,
			InterfaceID.MagicSpellbook.REANIMATION_BASIC,
			new RuneRequirement(4, Rune.BODY),
			new RuneRequirement(2, Rune.NATURE)
		),
		ADEPT_REANIMATION(
			41,
			InterfaceID.MagicSpellbook.REANIMATION_ADEPT,
			new RuneRequirement(4, Rune.BODY),
			new RuneRequirement(3, Rune.NATURE),
			new RuneRequirement(1, Rune.SOUL)
		),
		EXPERT_REANIMATION(
			72,
			InterfaceID.MagicSpellbook.REANIMATION_EXPERT,
			new RuneRequirement(1, Rune.BLOOD),
			new RuneRequirement(3, Rune.NATURE),
			new RuneRequirement(2, Rune.SOUL)
		),
		MASTER_REANIMATION(
			90,
			InterfaceID.MagicSpellbook.REANIMATION_MASTER,
			new RuneRequirement(2, Rune.BLOOD),
			new RuneRequirement(4, Rune.NATURE),
			new RuneRequirement(4, Rune.SOUL)
		),
		DEMONIC_OFFERING(
			84,
			InterfaceID.MagicSpellbook.DEMONIC_OFFERING,
			new RuneRequirement(1, Rune.SOUL),
			new RuneRequirement(1, Rune.WRATH)
		),
		SINISTER_OFFERING(
			92,
			InterfaceID.MagicSpellbook.SINISTER_OFFERING,
			new RuneRequirement(1, Rune.BLOOD),
			new RuneRequirement(1, Rune.WRATH)
		),
		SHADOW_VEIL(
			47,
			InterfaceID.MagicSpellbook.SHADOW_VEIL,
			new RuneRequirement(5, Rune.EARTH),
			new RuneRequirement(5, Rune.FIRE),
			new RuneRequirement(5, Rune.COSMIC)
		),
		VILE_VIGOUR(
			66,
			InterfaceID.MagicSpellbook.VILE_VIGOUR,
			new RuneRequirement(3, Rune.AIR),
			new RuneRequirement(1, Rune.SOUL)
		),
		DEGRIME(
			70,
			InterfaceID.MagicSpellbook.DEGRIME,
			new RuneRequirement(4, Rune.EARTH),
			new RuneRequirement(2, Rune.NATURE)
		),
		RESURRECT_CROPS(
			78,
			InterfaceID.MagicSpellbook.RESURRECT_CROPS,
			new RuneRequirement(25, Rune.EARTH),
			new RuneRequirement(8, Rune.BLOOD),
			new RuneRequirement(12, Rune.NATURE),
			new RuneRequirement(8, Rune.SOUL)
		),
		DEATH_CHARGE(
			80,
			InterfaceID.MagicSpellbook.DEATH_CHARGE,
			new RuneRequirement(1, Rune.BLOOD),
			new RuneRequirement(1, Rune.DEATH),
			new RuneRequirement(1, Rune.SOUL)
		),
		;

		private final int level;
		private final int interfaceId;
		private final RuneRequirement[] requirements;

		Necromancy(int level, @Component int interfaceId, RuneRequirement... requirements)
		{
			this.level = level;
			this.interfaceId = interfaceId;
			this.requirements = requirements;
		}

		@Override
		public int getLevel()
		{
			return level;
		}

		@Override
		public int getInterfaceId()
		{
			return interfaceId;
		}

		public boolean canCast()
		{
			if (getCurrent() != NECROMANCY)
			{
				return false;
			}

			/*if (!Worlds.inMembersWorld())
			{
				return false;
			}*/

			if (this == ARCEUUS_HOME_TELEPORT)
			{
				return RS2Magic.isHomeTeleportOnCooldown();
			}

			if (level > Skills.getLevel(Skill.MAGIC) || level > Skills.getBoostedLevel(Skill.MAGIC))
			{
				return false;
			}

			return haveRunesAvailable();
		}

		public boolean haveRunesAvailable()
		{
			for (RuneRequirement req : requirements)
			{
				if (!req.meetsRequirements())
				{
					return false;
				}
			}

			return true;
		}
	}
}