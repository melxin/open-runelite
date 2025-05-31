package net.runelite.client.plugins.openrl.api.rs2.providers.widgets.minigames;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.runelite.api.Quest;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayer;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.plugins.openrl.api.commons.Predicates;
import net.runelite.client.plugins.openrl.api.game.Skills;
import net.runelite.client.plugins.openrl.api.game.Vars;
import net.runelite.client.plugins.openrl.api.rs2.providers.client.RS2ClientScript;
import net.runelite.client.plugins.openrl.api.rs2.providers.entities.RS2Players;
import net.runelite.client.plugins.openrl.api.rs2.providers.quests.RS2Quests;
import net.runelite.client.plugins.openrl.api.rs2.providers.tabs.RS2Tabs;
import net.runelite.client.plugins.openrl.api.rs2.providers.tabs.Tab;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.RS2Widgets;
import net.runelite.client.plugins.openrl.api.rs2.providers.worlds.RS2Worlds;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Widget;

@Slf4j
public class RS2Minigames
{
	private static final Supplier<RS2Widget> MINIGAMES_TAB_BUTTON = () -> RS2Widgets.getWidget(InterfaceID.SideChannels.TAB_3);
	private static final Supplier<RS2Widget> MINIGAMES_DESTINATION = () -> RS2Widgets.getWidget(InterfaceID.Grouping.CURRENTGAME);
	private static final Supplier<RS2Widget> MINIGAMES_TELEPORT_BUTTON = () -> RS2Widgets.getWidget(InterfaceID.Grouping.TELEPORT);

	private static final Set<Quest> NMZ_QUESTS = Set.of(
		Quest.THE_ASCENT_OF_ARCEUUS,
		Quest.CONTACT,
		Quest.THE_CORSAIR_CURSE,
		Quest.THE_DEPTHS_OF_DESPAIR,
		Quest.DESERT_TREASURE_I,
		Quest.DRAGON_SLAYER_I,
		Quest.DREAM_MENTOR,
		Quest.FAIRYTALE_I__GROWING_PAINS,
		Quest.FAMILY_CREST,
		Quest.FIGHT_ARENA,
		Quest.THE_FREMENNIK_ISLES,
		Quest.GETTING_AHEAD,
		Quest.THE_GRAND_TREE,
		Quest.THE_GREAT_BRAIN_ROBBERY,
		Quest.GRIM_TALES,
		Quest.HAUNTED_MINE,
		Quest.HOLY_GRAIL,
		Quest.HORROR_FROM_THE_DEEP,
		Quest.IN_SEARCH_OF_THE_MYREQUE,
		Quest.LEGENDS_QUEST,
		Quest.LOST_CITY,
		Quest.LUNAR_DIPLOMACY,
		Quest.MONKEY_MADNESS_I,
		Quest.MOUNTAIN_DAUGHTER,
		Quest.MY_ARMS_BIG_ADVENTURE,
		Quest.ONE_SMALL_FAVOUR,
		Quest.RECIPE_FOR_DISASTER,
		Quest.ROVING_ELVES,
		Quest.SHADOW_OF_THE_STORM,
		Quest.SHILO_VILLAGE,
		Quest.SONG_OF_THE_ELVES,
		Quest.TALE_OF_THE_RIGHTEOUS,
		Quest.TREE_GNOME_VILLAGE,
		Quest.TROLL_ROMANCE,
		Quest.TROLL_STRONGHOLD,
		Quest.VAMPYRE_SLAYER,
		Quest.WHAT_LIES_BELOW,
		Quest.WITCHS_HOUSE
	);

	public static boolean canTeleport()
	{
		return getLastMinigameTeleportUsage().plus(20, ChronoUnit.MINUTES).isBefore(Instant.now());
	}

	public static void teleport(Destination destination)
	{
		if (!canTeleport())
		{
			log.warn("Tried to minigame teleport, but it's on cooldown.");
			return;
		}

		final RS2Widget minigamesTeleportButton = MINIGAMES_TELEPORT_BUTTON.get();
		final List<Integer> teleportGraphics = List.of(800, 802, 803, 804);
		if (isOpen() && minigamesTeleportButton != null)
		{
			if (Destination.getCurrent() != destination)
			{
				RS2ClientScript.runScript(124, destination.index);
				return;
			}

			if (teleportGraphics.contains(RS2Players.getLocal().getGraphic()))
			{
				return;
			}

			final RS2Widget button = minigamesTeleportButton.getChild(destination.index);
			if (RS2Widgets.isVisible(button))
			{
				button.interact(Predicates.textContains("Teleport to"));
			}
		}
		else
		{
			open();
		}
	}

	public static boolean open()
	{
		if (!isTabOpen())
		{
			RS2Tabs.open(Tab.CLAN_CHAT);
			return false;
		}

		if (!isOpen())
		{
			final RS2Widget widget = MINIGAMES_TAB_BUTTON.get();
			if (RS2Widgets.isVisible(widget))
			{
				widget.interact("Grouping");
				return false;
			}
		}

		return isOpen();
	}

	public static boolean isOpen()
	{
		return RS2Widgets.isVisible(MINIGAMES_TELEPORT_BUTTON.get());
	}

	public static boolean isTabOpen()
	{
		return RS2Tabs.isOpen(Tab.CLAN_CHAT);
	}

	public static Instant getLastMinigameTeleportUsage()
	{
		return Instant.ofEpochSecond(Vars.getVarp(VarPlayer.LAST_MINIGAME_TELEPORT) * 60L);
	}

	@Getter
	@AllArgsConstructor
	public enum Destination
	{
		BARBARIAN_ASSAULT(1, "Barbarian Assault", new WorldPoint(2531, 3577, 0), false),
		BOUNTY_HUNTER(2, "Bounty Hunter", null, true),
		BLAST_FURNACE(3, "Blast Furnace", new WorldPoint(2933, 10183, 0), true),
		BURTHORPE_GAMES_ROOM(4, "Burthorpe Games Room", new WorldPoint(2208, 4938, 0), true),
		CASTLE_WARS(5, "Castle Wars", new WorldPoint(2439, 3092, 0), false),
		CLAN_WARS(6, "Clan Wars", new WorldPoint(3151, 3636, 0), false),
		DAGANNOTH_KINGS(7, "Dagannoth Kings", null, true),
		FISHING_TRAWLER(8, "Fishing Trawler", new WorldPoint(2658, 3158, 0), true),
		GIANTS_FOUNDARY(9, "Giants' Foundry", new WorldPoint(3361, 3147, 0), true),
		GOD_WARS(10, "God Wars", null, true),
		GUARDIANS_OF_THE_RIFT(11, "Guardians of the Rift", new WorldPoint(3616, 9478, 0), true),
		LAST_MAN_STANDING(12, "Last Man Standing", new WorldPoint(3149, 3635, 0), false),
		MAGE_TRAINING_ARENA(13, "Mage Training Arena", null, true),
		MASTERING_MIXOLOGY(14, "Mastering Mixology", null, true),
		NIGHTMARE_ZONE(15, "Nightmare Zone", new WorldPoint(2611, 3121, 0), true),
		PEST_CONTROL(16, "Pest Control", new WorldPoint(2653, 2655, 0), true),
		PLAYER_OWNED_HOUSES(17, "Player Owned Houses", null, false),
		RAT_PITS(18, "Rat Pits", new WorldPoint(3263, 3406, 0), true),
		ROYAL_TITANS(19, "Royal Titans", null, true),
		SHADES_OF_MORTTON(20, "Shades of Mort'ton", new WorldPoint(3500, 3300, 0), true),
		SHIELD_OF_ARRAV(21, "Shield of Arrav", null, true),
		SHOOTING_STARS(22, "Shooting Stars", null, true),
		SORCERESS_GARDEN(23, "Sorceress Garden", null, true),
		SOUL_WARS(24, "Soul Wars", new WorldPoint(2209, 2857, 0), true),
		THEATRE_OF_BLOOD(25, "Theatre of Blood", null, true),
		TITHE_FARM(26, "Tithe Farm", new WorldPoint(1793, 3501, 0), true),
		TOMBS_OF_AMASCUT(27, "Tombs of Amascut", null, true),
		TROUBLE_BREWING(28, "Trouble Brewing", new WorldPoint(3811, 3021, 0), true),
		TZHAAR_FIGHT_PIT(29, "TzHaar Fight Pit", new WorldPoint(2402, 5181, 0), true),
		VOLCANIC_MINE(30, "Volcanic Mine", null, true),
		NONE(-1, "None", null, false);

		private final int index;
		private final String name;
		private final WorldPoint location;
		private final boolean members;

		public boolean canUse()
		{
			if (!hasDestination())
			{
				return false;
			}

			if (members && !RS2Worlds.inMembersWorld())
			{
				return false;
			}

			switch (this)
			{
				case BURTHORPE_GAMES_ROOM:
				case CASTLE_WARS:
				case CLAN_WARS:
				case LAST_MAN_STANDING:
				case SOUL_WARS:
				case TZHAAR_FIGHT_PIT:
				case GIANTS_FOUNDARY:
					return true;
				case BARBARIAN_ASSAULT:
					return Vars.getBit(3251) >= 1;
				case BLAST_FURNACE:
					return Vars.getBit(575) >= 1;
				case FISHING_TRAWLER:
					return Skills.getLevel(Skill.FISHING) >= 15;
				case GUARDIANS_OF_THE_RIFT:
					return RS2Quests.isFinished(Quest.TEMPLE_OF_THE_EYE);
				case NIGHTMARE_ZONE:
					return NMZ_QUESTS.stream().filter(RS2Quests::isFinished).count() >= 5;
				case PEST_CONTROL:
					return RS2Players.getLocal().getCombatLevel() >= 40;
				case RAT_PITS:
					return RS2Quests.isFinished(Quest.RATCATCHERS);
				case SHADES_OF_MORTTON:
					return RS2Quests.isFinished(Quest.SHADES_OF_MORTTON);
				case TROUBLE_BREWING:
					return RS2Quests.isFinished(Quest.CABIN_FEVER) && Skills.getLevel(Skill.COOKING) >= 40;
				case TITHE_FARM:
					//return Skills.getLevel(Skill.FARMING) >= 34 && (Vars.getBit(Varbits.KOUREND_FAVOR_HOSIDIUS) / 10) >= 100;
					return false;
				case BOUNTY_HUNTER:
				case MAGE_TRAINING_ARENA:
				case MASTERING_MIXOLOGY:
				case ROYAL_TITANS:
				case SORCERESS_GARDEN:
				case TOMBS_OF_AMASCUT:
					return false;
			}
			return false;
		}

		public boolean hasDestination()
		{
			return location != null;
		}

		public static Destination getCurrent()
		{
			final RS2Widget selectedTeleport = MINIGAMES_DESTINATION.get();
			if (RS2Widgets.isVisible(selectedTeleport))
			{
				return byName(selectedTeleport.getText());
			}

			return NONE;
		}

		public static Destination byName(String name)
		{
			return Arrays.stream(values())
				.filter(x -> x.getName().equals(name))
				.findFirst()
				.orElse(NONE);
		}
	}
}