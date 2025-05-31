package net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import net.runelite.api.GameState;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.Quest;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.commons.Time;
import net.runelite.client.plugins.openrl.api.game.Game;
import net.runelite.client.plugins.openrl.api.game.GameThread;
import net.runelite.client.plugins.openrl.api.game.Vars;
import net.runelite.client.plugins.openrl.api.input.Keyboard;
import net.runelite.client.plugins.openrl.api.managers.RegionManager;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.Teleport;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.TeleportItem;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.TeleportSpell;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.poh.HousePortal;
import net.runelite.client.plugins.openrl.api.rs2.providers.entities.RS2Players;
import net.runelite.client.plugins.openrl.api.rs2.providers.entities.RS2TileObjects;
import net.runelite.client.plugins.openrl.api.rs2.providers.items.RS2Equipment;
import net.runelite.client.plugins.openrl.api.rs2.providers.items.RS2Inventory;
import net.runelite.client.plugins.openrl.api.rs2.providers.magic.RS2Magic;
import net.runelite.client.plugins.openrl.api.rs2.providers.magic.Spell;
import net.runelite.client.plugins.openrl.api.rs2.providers.magic.SpellBook;
import net.runelite.client.plugins.openrl.api.rs2.providers.quests.RS2Quests;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.RS2Widgets;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.dialog.RS2Dialog;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.minigames.RS2Minigames;
import net.runelite.client.plugins.openrl.api.rs2.providers.worlds.RS2Worlds;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Item;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2TileObject;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Widget;
import static net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.MovementConstants.AMULET_OF_GLORY;
import static net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.MovementConstants.ARDY_CLOAK;
import static net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.MovementConstants.BURNING_AMULET;
import static net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.MovementConstants.COMBAT_BRACELET;
import static net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.MovementConstants.DIGSITE_PENDANT;
import static net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.MovementConstants.GAMES_NECKLACE;
import static net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.MovementConstants.NECKLACE_OF_PASSAGE;
import static net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.MovementConstants.RING_OF_DUELING;
import static net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.MovementConstants.RING_OF_WEALTH;
import static net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.MovementConstants.SKILLS_NECKLACE;
import static net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.MovementConstants.SLAYER_RING;
import static net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.MovementConstants.XERICS_TALISMAN;
import static net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.poh.JewelryBox.BASIC;
import static net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.poh.JewelryBox.FANCY;
import static net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.poh.JewelryBox.ORNATE;

public class TeleportLoader
{
	private static Pattern WILDY_PATTERN = Pattern.compile("Okay, teleport to level [\\d,]* Wilderness\\.");
	private static List<Teleport> LAST_TELEPORT_LIST = new ArrayList<>();

	public static List<Teleport> buildTeleports()
	{
		final List<Teleport> teleports = new ArrayList<>();
		teleports.addAll(LAST_TELEPORT_LIST);
		teleports.addAll(buildTimedTeleports());
		return teleports;
	}

	private static List<Teleport> buildTimedTeleports()
	{
		return GameThread.invokeLater(() ->
		{
			final List<Teleport> teleports = new ArrayList<>();
			if (RS2Worlds.inMembersWorld())
			{
				if (Game.getWildyLevel() == 0)
				{
					// Minigames
					if (RegionManager.useMinigameTeleports() && RS2Minigames.canTeleport())
					{
						for (RS2Minigames.Destination tp : RS2Minigames.Destination.values())
						{
							if (tp.canUse())
							{
								teleports.add(new Teleport(tp.getLocation(), 2, () -> RS2Minigames.teleport(tp)));
							}
						}
					}
				}
			}

			if (Game.getWildyLevel() <= 20)
			{
				for (TeleportSpell teleportSpell : TeleportSpell.values())
				{
					if (!teleportSpell.canCast() || teleportSpell.getPoint() == null)
					{
						continue;
					}

					if (teleportSpell.getPoint().distanceTo(RS2Players.getLocal().getWorldLocation()) > 50)
					{
						teleports.add(new Teleport(teleportSpell.getPoint(), 5, () ->
						{
							final Spell spell = teleportSpell.getSpell();
							if (teleportSpell == TeleportSpell.TELEPORT_TO_HOUSE)
							{
								// Tele to outside
								final RS2Widget widget = RS2Widgets.getWidget(spell.getInterfaceId());
								if (widget == null)
								{
									return;
								}
								widget.interact(1);
							}
							else
							{
								RS2Magic.cast(spell);
							}
						}));
					}
				}
			}

			return teleports;
		});
	}

	public static void refreshTeleports()
	{
		GameThread.invoke(() ->
		{
			final List<Teleport> teleports = new ArrayList<>();
			if (RS2Worlds.inMembersWorld())
			{
				// One click teleport items
				for (TeleportItem tele : TeleportItem.values())
				{
					if (tele.canUse() && tele.getDestination().distanceTo(RS2Players.getLocal().getWorldLocation()) > 20)
					{
						switch (tele)
						{
							case ROYAL_SEED_POD:
								if (Game.getWildyLevel() <= 30)
								{
									teleports.add(itemTeleport(tele));
								}
							default:
								if (Game.getWildyLevel() <= 20)
								{
									teleports.add(itemTeleport(tele));
								}
						}
					}
				}

				if (Game.getWildyLevel() <= 20)
				{
					if (ringOfDueling())
					{
						teleports.add(new Teleport(new WorldPoint(3315, 3235, 0), 6,
							() -> jewelryTeleport("PvP Arena", RING_OF_DUELING)));
						teleports.add(new Teleport(new WorldPoint(2440, 3090, 0), 2,
							() -> jewelryTeleport("Castle Wars", RING_OF_DUELING)));
						teleports.add(new Teleport(new WorldPoint(3151, 3635, 0), 2,
							() -> jewelryTeleport("Ferox Enclave", RING_OF_DUELING)));
					}

					if (gamesNecklace())
					{
						teleports.add(new Teleport(new WorldPoint(2898, 3553, 0), 2,
							() -> jewelryTeleport("Burthorpe", GAMES_NECKLACE)));
						teleports.add(new Teleport(new WorldPoint(2520, 3571, 0), 6,
							() -> jewelryTeleport("Barbarian Outpost", GAMES_NECKLACE)));
						teleports.add(new Teleport(new WorldPoint(2964, 4382, 2), 2,
							() -> jewelryTeleport("Corporeal Beast", GAMES_NECKLACE)));
						teleports.add(new Teleport(new WorldPoint(3244, 9501, 2), 2,
							() -> jewelryTeleport("Tears of Guthix", GAMES_NECKLACE)));
						teleports.add(new Teleport(new WorldPoint(1624, 3938, 0), 1,
							() -> jewelryTeleport("Wintertodt Camp", GAMES_NECKLACE)));
					}

					if (necklaceOfPassage())
					{
						teleports.add(new Teleport(new WorldPoint(3114, 3179, 0), 2,
							() -> jewelryTeleport("Wizards' Tower", NECKLACE_OF_PASSAGE)));
						teleports.add(new Teleport(new WorldPoint(2430, 3348, 0), 2,
							() -> jewelryTeleport("The Outpost", NECKLACE_OF_PASSAGE)));
						teleports.add(new Teleport(new WorldPoint(3405, 3157, 0), 2,
							() -> jewelryTeleport("Eagle's Eyrie", NECKLACE_OF_PASSAGE)));
					}

					if (xericsTalisman())
					{
						teleports.add(new Teleport(new WorldPoint(1576, 3530, 0), 6,
							() -> jewelryTeleport("Xeric's Lookout", XERICS_TALISMAN)));
						teleports.add(new Teleport(new WorldPoint(1752, 3566, 0), 6,
							() -> jewelryTeleport("Xeric's Glade", XERICS_TALISMAN)));
						teleports.add(new Teleport(new WorldPoint(1504, 3817, 0), 6,
							() -> jewelryTeleport("Xeric's Inferno", XERICS_TALISMAN)));
						/*if (Quests.isFinished(Quest.ARCHITECTURAL_ALLIANCE))
						{
							teleports.add(new Teleport(new WorldPoint(1640, 3674, 0), 6,
														() -> jewelryTeleport("Xeric's Heart", XERICS_TALISMAN)));
						}*/
					}

					if (digsitePendant())
					{
						teleports.add(new Teleport(new WorldPoint(3341, 3445, 0), 6,
							() -> jewelryTeleport("Digsite", DIGSITE_PENDANT)));
						teleports.add(new Teleport(new WorldPoint(3764, 3869, 1), 6,
							() -> jewelryTeleport("Fossil Island", DIGSITE_PENDANT)));
						if (RS2Quests.isFinished(Quest.DRAGON_SLAYER_II))
						{
							teleports.add(new Teleport(new WorldPoint(3549, 10456, 0), 6,
								() -> jewelryTeleport("Lithkren", DIGSITE_PENDANT)));
						}
					}
				}

				if (Game.getWildyLevel() <= 30)
				{
					if (combatBracelet())
					{
						teleports.add(new Teleport(new WorldPoint(2882, 3548, 0), 2,
							() -> jewelryTeleport("Warriors' Guild", COMBAT_BRACELET)));
						teleports.add(new Teleport(new WorldPoint(3191, 3367, 0), 2,
							() -> jewelryTeleport("Champions' Guild", COMBAT_BRACELET)));
						teleports.add(new Teleport(new WorldPoint(3052, 3488, 0), 2,
							() -> jewelryTeleport("Monastery", COMBAT_BRACELET)));
						teleports.add(new Teleport(new WorldPoint(2655, 3441, 0), 2,
							() -> jewelryTeleport("Ranging Guild", COMBAT_BRACELET)));
					}

					if (skillsNecklace())
					{
						teleports.add(new Teleport(new WorldPoint(2611, 3390, 0), 6,
							() -> jewelryPopupTeleport("Fishing Guild", SKILLS_NECKLACE)));
						teleports.add(new Teleport(new WorldPoint(3050, 9763, 0), 6,
							() -> jewelryPopupTeleport("Mining Guild", SKILLS_NECKLACE)));
						teleports.add(new Teleport(new WorldPoint(2933, 3295, 0), 6,
							() -> jewelryPopupTeleport("Crafting Guild", SKILLS_NECKLACE)));
						teleports.add(new Teleport(new WorldPoint(3143, 3440, 0), 6,
							() -> jewelryPopupTeleport("Cooking Guild", SKILLS_NECKLACE)));
						teleports.add(new Teleport(new WorldPoint(1662, 3505, 0), 6,
							() -> jewelryPopupTeleport("Woodcutting Guild", SKILLS_NECKLACE)));
						teleports.add(new Teleport(new WorldPoint(1249, 3718, 0), 6,
							() -> jewelryPopupTeleport("Farming Guild", SKILLS_NECKLACE)));
					}

					if (ringOfWealth())
					{
						teleports.add(new Teleport(new WorldPoint(3163, 3478, 0), 2,
							() -> jewelryTeleport("Grand Exchange", RING_OF_WEALTH)));
						teleports.add(new Teleport(new WorldPoint(2996, 3375, 0), 2,
							() -> jewelryTeleport("Falador", RING_OF_WEALTH)));

						if (RS2Quests.isFinished(Quest.THRONE_OF_MISCELLANIA))
						{
							teleports.add(new Teleport(new WorldPoint(2538, 3863, 0), 2,
								() -> jewelryTeleport("Miscellania", RING_OF_WEALTH)));
						}
						if (RS2Quests.isFinished(Quest.BETWEEN_A_ROCK))
						{
							teleports.add(new Teleport(new WorldPoint(2828, 10166, 0), 2,
								() -> jewelryTeleport("Miscellania", RING_OF_WEALTH)));
						}
					}

					if (amuletOfGlory())
					{
						teleports.add(new Teleport(new WorldPoint(3087, 3496, 0), 0,
							() -> jewelryTeleport("Edgeville", AMULET_OF_GLORY)));
						teleports.add(new Teleport(new WorldPoint(2918, 3176, 0), 0,
							() -> jewelryTeleport("Karamja", AMULET_OF_GLORY)));
						teleports.add(new Teleport(new WorldPoint(3105, 3251, 0), 0,
							() -> jewelryTeleport("Draynor Village", AMULET_OF_GLORY)));
						teleports.add(new Teleport(new WorldPoint(3293, 3163, 0), 0,
							() -> jewelryTeleport("Al Kharid", AMULET_OF_GLORY)));
					}

					if (burningAmulet())
					{
						teleports.add(new Teleport(new WorldPoint(3235, 3636, 0), 5,
							() -> jewelryWildernessTeleport("Chaos Temple", BURNING_AMULET)));
						teleports.add(new Teleport(new WorldPoint(3038, 3651, 0), 5,
							() -> jewelryWildernessTeleport("Bandit Camp", BURNING_AMULET)));
						teleports.add(new Teleport(new WorldPoint(3028, 3842, 0), 5,
							() -> jewelryWildernessTeleport("Lava Maze", BURNING_AMULET)));
					}

					if (slayerRing())
					{
						teleports.add(new Teleport(new WorldPoint(2432, 3423, 0), 2,
							() -> slayerRingTeleport("Stronghold Slayer Cave", SLAYER_RING)));
						teleports.add(new Teleport(new WorldPoint(3422, 3537, 0), 2,
							() -> slayerRingTeleport("Slayer Tower", SLAYER_RING)));
						teleports.add(new Teleport(new WorldPoint(2802, 10000, 0), 2,
							() -> slayerRingTeleport("Fremennik Slayer Dungeon", SLAYER_RING)));
						teleports.add(new Teleport(new WorldPoint(3185, 4601, 0), 2,
							() -> slayerRingTeleport("Tarn's Lair", SLAYER_RING)));
						if (RS2Quests.isFinished(Quest.MOURNINGS_END_PART_II))
						{
							teleports.add(new Teleport(new WorldPoint(2028, 4636, 0), 2,
								() -> slayerRingTeleport("Dark Beasts", SLAYER_RING)));
						}
					}

					if (ardyCloak())
					{
						teleports.add(new Teleport(new WorldPoint(2606, 3221, 0), 3,
							() -> equipmentTeleport("Monastery Teleport", "Kandarin Monastery", ARDY_CLOAK)));
					}
				}

				if (RegionManager.usePoh() && (canEnterHouse() || RS2TileObjects.getNearest(ObjectID.PORTAL_4525) != null))
				{
					if (RegionManager.hasMountedGlory())
					{
						teleports.add(mountedPohTeleport(new WorldPoint(3087, 3496, 0), ObjectID.AMULET_OF_GLORY, "Edgeville"));
						teleports.add(mountedPohTeleport(new WorldPoint(2918, 3176, 0), ObjectID.AMULET_OF_GLORY, "Karamja"));
						teleports.add(mountedPohTeleport(new WorldPoint(3105, 3251, 0), ObjectID.AMULET_OF_GLORY, "Draynor Village"));
						teleports.add(mountedPohTeleport(new WorldPoint(3293, 3163, 0), ObjectID.AMULET_OF_GLORY, "Al Kharid"));
					}

					if (RegionManager.hasMountedDigsitePendant())
					{
						teleports.add(pohDigsitePendantTeleport(new WorldPoint(3341, 3445, 0), 1));
						teleports.add(pohDigsitePendantTeleport(new WorldPoint(3766, 3870, 1), 2));
						if (RS2Quests.isFinished(Quest.DRAGON_SLAYER_II))
						{
							teleports.add(pohDigsitePendantTeleport(new WorldPoint(3549, 10456, 0), 3));
						}
					}

					switch (RegionManager.hasJewelryBox())
					{
						case ORNATE:
							if (RS2Quests.isFinished(Quest.THRONE_OF_MISCELLANIA))
							{
								teleports.add(pohWidgetTeleport(new WorldPoint(2538, 3863, 0), 'j'));
							}
							teleports.add(pohWidgetTeleport(new WorldPoint(3163, 3478, 0), 'k'));
							teleports.add(pohWidgetTeleport(new WorldPoint(2996, 3375, 0), 'l'));
							if (RS2Quests.isFinished(Quest.BETWEEN_A_ROCK))
							{
								teleports.add(pohWidgetTeleport(new WorldPoint(2828, 10166, 0), 'm'));
							}
							teleports.add(pohWidgetTeleport(new WorldPoint(3087, 3496, 0), 'n'));
							teleports.add(pohWidgetTeleport(new WorldPoint(2918, 3176, 0), 'o'));
							teleports.add(pohWidgetTeleport(new WorldPoint(3105, 3251, 0), 'p'));
							teleports.add(pohWidgetTeleport(new WorldPoint(3293, 3163, 0), 'q'));
						case FANCY:
							teleports.add(pohWidgetTeleport(new WorldPoint(2882, 3548, 0), '9'));
							teleports.add(pohWidgetTeleport(new WorldPoint(3191, 3367, 0), 'a'));
							teleports.add(pohWidgetTeleport(new WorldPoint(3052, 3488, 0), 'b'));
							teleports.add(pohWidgetTeleport(new WorldPoint(2655, 3441, 0), 'c'));
							teleports.add(pohWidgetTeleport(new WorldPoint(2611, 3390, 0), 'd'));
							teleports.add(pohWidgetTeleport(new WorldPoint(3050, 9763, 0), 'e'));
							teleports.add(pohWidgetTeleport(new WorldPoint(2933, 3295, 0), 'f'));
							teleports.add(pohWidgetTeleport(new WorldPoint(3143, 3440, 0), 'g'));
							teleports.add(pohWidgetTeleport(new WorldPoint(1662, 3505, 0), 'h'));
							teleports.add(pohWidgetTeleport(new WorldPoint(1249, 3718, 0), 'i'));
						case BASIC:
							teleports.add(pohWidgetTeleport(new WorldPoint(3315, 3235, 0), '1'));
							teleports.add(pohWidgetTeleport(new WorldPoint(2440, 3090, 0), '2'));
							teleports.add(pohWidgetTeleport(new WorldPoint(3151, 3635, 0), '3'));
							teleports.add(pohWidgetTeleport(new WorldPoint(2898, 3553, 0), '4'));
							teleports.add(pohWidgetTeleport(new WorldPoint(2520, 3571, 0), '5'));
							teleports.add(pohWidgetTeleport(new WorldPoint(2964, 4382, 2), '6'));
							teleports.add(pohWidgetTeleport(new WorldPoint(3244, 9501, 2), '7'));
							teleports.add(pohWidgetTeleport(new WorldPoint(1624, 3938, 0), '8'));
							break;
						default:
					}

					//nexus portal
					final List<Teleport> nexusTeleports = getNexusTeleports();
					teleports.addAll(nexusTeleports);

					//normal house portals (remove duplicate teleports)
					RegionManager.getHousePortals().stream().
						filter(housePortal -> nexusTeleports.stream().
							noneMatch(teleport -> teleport.getDestination().equals(housePortal.getDestination()))).
						forEach(housePortal -> teleports.add(pohPortalTeleport(housePortal)));
				}
			}

			LAST_TELEPORT_LIST.clear();
			LAST_TELEPORT_LIST.addAll(teleports);
		});
	}

	public static boolean canEnterHouse()
	{
		return RS2Inventory.contains(ItemID.TELEPORT_TO_HOUSE) || TeleportSpell.TELEPORT_TO_HOUSE.canCast();
	}

	public static void enterHouse()
	{
		if (TeleportSpell.TELEPORT_TO_HOUSE.canCast())
		{
			SpellBook.Standard.TELEPORT_TO_HOUSE.cast();
			return;
		}

		final RS2Item teleTab = RS2Inventory.getFirst(ItemID.TELEPORT_TO_HOUSE);
		if (teleTab != null)
		{
			teleTab.interact("Break");
		}
	}

	public static void jewelryTeleport(String target, int... ids)
	{
		final RS2Item inv = RS2Inventory.getFirst(ids);

		if (inv != null)
		{
			if (!RS2Dialog.isViewingOptions())
			{
				inv.interact("Rub");
				Time.sleepTicksUntil(RS2Dialog::isViewingOptions, 2);
				return;
			}
			RS2Dialog.chooseOption(target);
			return;
		}

		if (!RegionManager.useEquipmentJewellery())
		{
			return;
		}

		final RS2Item equipped = RS2Equipment.getFirst(ids);
		if (equipped != null)
		{
			equipped.interact(target);
		}
	}

	public static void equipmentTeleport(String invTarget, String equipTarget, int... ids)
	{
		final RS2Item inv = RS2Inventory.getFirst(x -> x.hasAction(invTarget) && Arrays.stream(ids).anyMatch(id -> id == x.getId()));

		if (inv != null)
		{
			inv.interact(invTarget);
			return;
		}

		if (!RegionManager.useEquipmentTeleports())
		{
			return;
		}

		final RS2Item equipped = RS2Equipment.getFirst(x -> x.hasAction(equipTarget) && Arrays.stream(ids).anyMatch(id -> id == x.getId()));
		if (equipped != null)
		{
			equipped.interact(equipTarget);
		}
	}

	public static Teleport pohPortalTeleport(HousePortal housePortal)
	{
		return new Teleport(housePortal.getDestination(), 10, () ->
		{
			if (!RS2Players.getLocal().isIdle() || Static.getClient().getGameState() == GameState.LOADING)
			{
				return;
			}

			final RS2TileObject portal = RS2TileObjects.getNearest(housePortal.getPortalName());
			if (portal != null)
			{
				portal.interact("Enter", "Varrock", "Seers' Village", "Watchtower");
				return;
			}

			enterHouse();
		});
	}

	public static List<Teleport> getNexusTeleports()
	{
		final List<Teleport> result = new ArrayList<>();
		int[] varbitArray = {
			6672, 6673, 6674, 6675, 6676, 6677, 6678, 6679, 6680,
			6681, 6682, 6683, 6684, 6685, 6686, 6568, 6569, 6582,
			10092, 10093, 10094, 10095, 10096, 10097, 10098,
			10099, 10100, 10101, 10102, 10103
		};

		for (int varbit : varbitArray)
		{
			int id = Vars.getBit(varbit);
			switch (id)
			{
				case 0:
				{
					break;
				}
				case 1:
				{
					result.add(pohNexusTeleport(HousePortal.VARROCK));
					break;
				}
				case 2:
				{
					result.add(pohNexusTeleport(HousePortal.LUMBRIDGE));
					break;
				}
				case 3:
				{
					result.add(pohNexusTeleport(HousePortal.FALADOR));
					break;
				}
				case 4:
				{
					result.add(pohNexusTeleport(HousePortal.CAMELOT));
					break;
				}
				case 5:
				{
					result.add(pohNexusTeleport(HousePortal.EAST_ARDOUGNE));
					break;
				}
				case 6:
				{
					result.add(pohNexusTeleport(HousePortal.WATCHTOWER));
					break;
				}
				case 7:
				{
					result.add(pohNexusTeleport(HousePortal.SENNTISTEN));
					break;
				}
				case 8:
				{
					result.add(pohNexusTeleport(HousePortal.MARIM));
					break;
				}
				case 9:
				{
					result.add(pohNexusTeleport(HousePortal.KHARYRLL));
					break;
				}
				case 10:
				{
					result.add(pohNexusTeleport(HousePortal.LUNAR_ISLE));
					break;
				}
				case 11:
				{
					result.add(pohNexusTeleport(HousePortal.KOUREND));
					break;
				}
				case 12:
				{
					result.add(pohNexusTeleport(HousePortal.WATERBIRTH_ISLAND));
					break;
				}
				case 13:
				{
					result.add(pohNexusTeleport(HousePortal.FISHING_GUILD));
					break;
				}
				case 14:
				{
					result.add(pohNexusTeleport(HousePortal.ANNAKARL)); //wilderness
					break;
				}
				case 15:
				{
					result.add(pohNexusTeleport(HousePortal.TROLL_STRONGHOLD));
					break;
				}
				case 16:
				{
					result.add(pohNexusTeleport(HousePortal.CATHERBY));
					break;
				}
				case 17:
				{
					result.add(pohNexusTeleport(HousePortal.GHORROCK)); //wilderness
					break;
				}
				case 18:
				{
					result.add(pohNexusTeleport(HousePortal.CARRALLANGAR)); //wilderness
					break;
				}
				case 19:
				{
					result.add(pohNexusTeleport(HousePortal.WEISS));
					break;
				}
				case 20:
				{
					result.add(pohNexusTeleport(HousePortal.ARCEUUS_LIBRARY));
					break;
				}
				case 21:
				{
					result.add(pohNexusTeleport(HousePortal.DRAYNOR_MANOR));
					break;
				}
				case 22:
				{
					result.add(pohNexusTeleport(HousePortal.BATTLEFRONT));
					break;
				}
				case 23:
				{
					result.add(pohNexusTeleport(HousePortal.MIND_ALTAR));
					break;
				}
				case 24:
				{
					result.add(pohNexusTeleport(HousePortal.SALVE_GRAVEYARD));
					break;
				}
				case 25:
				{
					result.add(pohNexusTeleport(HousePortal.FENKENSTRAINS_CASTLE));
					break;
				}
				case 26:
				{
					result.add(pohNexusTeleport(HousePortal.WEST_ARDOUGNE));
					break;
				}
				case 27:
				{
					result.add(pohNexusTeleport(HousePortal.HARMONY_ISLAND));
					break;
				}
				case 28:
				{
					result.add(pohNexusTeleport(HousePortal.CEMETERY)); //wilderness
					break;
				}
				case 29:
				{
					result.add(pohNexusTeleport(HousePortal.BARROWS));
					break;
				}
				case 30:
				{
					result.add(pohNexusTeleport(HousePortal.APE_ATOLL_DUNGEON));
					break;
				}
			}
		}

		return result;
	}

	public static Teleport pohNexusTeleport(HousePortal housePortal)
	{
		final WorldPoint destination = housePortal.getDestination();
		return new Teleport(destination, 10, () ->
		{
			if (!RS2Players.getLocal().isIdle() || Static.getClient().getGameState() == GameState.LOADING)
			{
				return;
			}

			final RS2TileObject nexusPortal = RS2TileObjects.getNearest("Portal Nexus");
			if (nexusPortal == null)
			{
				enterHouse();
				return;
			}

			final RS2Widget teleportInterface = RS2Widgets.get(17, 12);
			if (teleportInterface == null || teleportInterface.isHidden())
			{
				nexusPortal.interact("Teleport Menu");
				return;
			}

			final Widget[] teleportChildren = teleportInterface.getDynamicChildren();
			if (teleportChildren == null || teleportChildren.length == 0)
			{
				//no teleports in the portal
				return;
			}

			final Optional<Widget> optionalTeleportWidget = Arrays.stream(teleportChildren).
				filter(Objects::nonNull).
				filter(widget -> widget.getText() != null).
				filter(widget -> widget.getText().contains(housePortal.getNexusTarget())).
				findFirst();

			if (optionalTeleportWidget.isEmpty())
			{
				//the teleport is not in the list
				return;
			}

			final Widget teleportWidget = optionalTeleportWidget.get();
			final String teleportChar = teleportWidget.getText().substring(12, 13);
			Keyboard.type(teleportChar);
		});
	}

	public static void jewelryPopupTeleport(String target, int... ids)
	{
		final RS2Item inv = RS2Inventory.getFirst(ids);

		if (inv != null)
		{
			final RS2Widget baseWidget = RS2Widgets.get(187, 3);
			if (RS2Widgets.isVisible(baseWidget))
			{
				Widget[] children = baseWidget.getChildren();
				if (children == null)
				{
					return;
				}

				for (int i = 0; i < children.length; i++)
				{
					Widget teleportItem = children[i];
					if (teleportItem.getText().contains(target))
					{
						Keyboard.type((i + 1));
						return;
					}
				}
			}

			inv.interact("Rub");
			return;
		}

		if (!RegionManager.useEquipmentJewellery())
		{
			return;
		}

		final RS2Item equipped = RS2Equipment.getFirst(ids);
		if (equipped != null)
		{
			equipped.interact(target);
		}
	}

	public static Teleport pohDigsitePendantTeleport(
		WorldPoint destination,
		int action
	)
	{
		return new Teleport(destination, 10, () ->
		{
			if (!RS2Players.getLocal().isIdle() || Static.getClient().getGameState() == GameState.LOADING)
			{
				return;
			}

			if (RS2Widgets.isVisible(RS2Widgets.get(WidgetInfo.ADVENTURE_LOG)))
			{
				Keyboard.type(action);
				return;
			}

			final RS2TileObject digsitePendant = RS2TileObjects.getNearest("Digsite Pendant");
			if (digsitePendant != null)
			{
				digsitePendant.interact("Teleport menu");
				return;
			}

			enterHouse();
		});
	}

	public static Teleport itemTeleport(TeleportItem teleportItem)
	{
		return new Teleport(teleportItem.getDestination(), 5, () ->
		{
			final RS2Item item = RS2Inventory.getFirst(teleportItem.getItemId());
			if (item != null)
			{
				item.interact(teleportItem.getAction());
			}
		});
	}

	public static Teleport pohWidgetTeleport(
		WorldPoint destination,
		char action
	)
	{
		return new Teleport(destination, 10, () ->
		{
			if (!RS2Players.getLocal().isIdle() || Static.getClient().getGameState() == GameState.LOADING)
			{
				return;
			}

			if (RS2Widgets.isVisible(RS2Widgets.get(InterfaceID.POH_JEWELLERY_BOX, 0)))
			{
				Keyboard.type(action);
				return;
			}

			final RS2TileObject box = RS2TileObjects.getNearest(to -> to.getName() != null && to.getName().contains("Jewellery Box"));
			if (box != null)
			{
				box.interact("Teleport Menu");
				return;
			}

			enterHouse();
		});
	}

	public static Teleport mountedPohTeleport(
		WorldPoint destination,
		int objId,
		String action
	)
	{
		return new Teleport(destination, 10, () ->
		{
			if (!RS2Players.getLocal().isIdle() || Static.getClient().getGameState() == GameState.LOADING)
			{
				return;
			}

			final RS2TileObject first = RS2TileObjects.getNearest(objId);
			if (first != null)
			{
				first.interact(action);
				return;
			}

			enterHouse();
		});
	}

	public static void jewelryWildernessTeleport(String target, int... ids)
	{
		jewelryTeleport(target, ids);
		Time.sleepTick();
		if (RS2Dialog.isViewingOptions() && RS2Dialog.getOptions().stream()
			.anyMatch(it -> it.getText() != null && WILDY_PATTERN.matcher(it.getText()).matches()))
		{
			RS2Dialog.chooseOption(1);
		}
	}

	public static void slayerRingTeleport(String target, int... ids)
	{
		RS2Item ring = RS2Inventory.getFirst(ids);

		if (ring == null && RegionManager.useEquipmentJewellery())
		{
			ring = RS2Equipment.getFirst(ids);
		}

		if (ring != null)
		{
			if (!RS2Dialog.isViewingOptions())
			{
				ring.interact("Teleport");
				Time.sleepTicksUntil(RS2Dialog::isViewingOptions, 2);
				return;
			}
			if (RS2Dialog.hasOption("Teleport"))
			{
				RS2Dialog.chooseOption("Teleport");
				return;
			}
			RS2Dialog.chooseOption(target);
		}
	}

	public static boolean ringOfDueling()
	{
		return RS2Inventory.getFirst(RING_OF_DUELING) != null
			|| (RegionManager.useEquipmentJewellery() && RS2Equipment.getFirst(RING_OF_DUELING) != null);
	}

	public static boolean gamesNecklace()
	{
		return RS2Inventory.getFirst(GAMES_NECKLACE) != null
			|| (RegionManager.useEquipmentJewellery() && RS2Equipment.getFirst(GAMES_NECKLACE) != null);
	}

	public static boolean combatBracelet()
	{
		return RS2Inventory.getFirst(COMBAT_BRACELET) != null
			|| (RegionManager.useEquipmentJewellery() && RS2Equipment.getFirst(COMBAT_BRACELET) != null);
	}

	public static boolean skillsNecklace()
	{
		return RS2Inventory.getFirst(SKILLS_NECKLACE) != null
			|| (RegionManager.useEquipmentJewellery() && RS2Equipment.getFirst(SKILLS_NECKLACE) != null);
	}

	public static boolean ringOfWealth()
	{
		return RS2Inventory.getFirst(RING_OF_WEALTH) != null
			|| (RegionManager.useEquipmentJewellery() && RS2Equipment.getFirst(RING_OF_WEALTH) != null);
	}

	public static boolean amuletOfGlory()
	{
		return RS2Inventory.getFirst(AMULET_OF_GLORY) != null
			|| (RegionManager.useEquipmentJewellery() && RS2Equipment.getFirst(AMULET_OF_GLORY) != null);
	}

	public static boolean necklaceOfPassage()
	{
		return RS2Inventory.getFirst(NECKLACE_OF_PASSAGE) != null
			|| (RegionManager.useEquipmentJewellery() && RS2Equipment.getFirst(NECKLACE_OF_PASSAGE) != null);
	}

	public static boolean xericsTalisman()
	{
		return RS2Inventory.getFirst(XERICS_TALISMAN) != null
			|| (RegionManager.useEquipmentJewellery() && RS2Equipment.getFirst(XERICS_TALISMAN) != null);
	}

	public static boolean slayerRing()
	{
		return RS2Inventory.getFirst(SLAYER_RING) != null
			|| (RegionManager.useEquipmentJewellery() && RS2Equipment.getFirst(SLAYER_RING) != null);
	}

	public static boolean digsitePendant()
	{
		return RS2Inventory.getFirst(DIGSITE_PENDANT) != null
			|| (RegionManager.useEquipmentJewellery() && RS2Equipment.getFirst(DIGSITE_PENDANT) != null);
	}

	public static boolean burningAmulet()
	{
		return RS2Inventory.getFirst(BURNING_AMULET) != null
			|| (RegionManager.useEquipmentJewellery() && RS2Equipment.getFirst(BURNING_AMULET) != null);
	}

	public static boolean ardyCloak()
	{
		return RS2Inventory.getFirst(ARDY_CLOAK) != null
			|| (RegionManager.useEquipmentTeleports() && RS2Equipment.getFirst(ARDY_CLOAK) != null);
	}
}