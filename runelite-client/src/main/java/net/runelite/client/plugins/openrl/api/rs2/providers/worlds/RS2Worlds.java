package net.runelite.client.plugins.openrl.api.rs2.providers.worlds;

import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.World;
import net.runelite.api.WorldType;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.commons.Rand;
import net.runelite.client.plugins.openrl.api.commons.Time;
import net.runelite.client.plugins.openrl.api.game.Game;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.RS2WorldQuery;
import net.runelite.client.plugins.openrl.api.rs2.providers.tabs.Tab;
import net.runelite.client.plugins.openrl.api.rs2.providers.tabs.RS2Tabs;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.RS2Dialog;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Widget;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.RS2Widgets;
import net.runelite.http.api.worlds.WorldResult;

@Slf4j
public class RS2Worlds
{
	public static RS2WorldQuery query()
	{
		return RS2WorldQuery.query();
	}

	private static List<World> lookup()
	{
		List<World> out = new ArrayList<>();
		WorldResult lookup = Static.getWorldService().getWorlds();
		if (lookup == null)
		{
			return Collections.emptyList();
		}

		lookup.getWorlds().forEach(w ->
		{
			World world = Static.getClient().createWorld();
			world.setActivity(w.getActivity());
			world.setAddress(w.getAddress());
			world.setId(w.getId());
			world.setPlayerCount(w.getPlayers());
			world.setLocation(w.getLocation());
			EnumSet<WorldType> types = EnumSet.noneOf(WorldType.class);
			w.getTypes().stream().map(RS2Worlds::toApiWorldType).forEach(types::add);
			world.setTypes(types);
			out.add(world);
		});

		// @TODO FIX
		//Static.getClient().setWorldList(out.toArray(new World[0]));

		return out;
	}

	public static List<World> getAll(Predicate<World> filter)
	{
		List<World> out = new ArrayList<>();
		List<World> loadedWorlds;

		try
		{
			World[] worlds = Static.getClient().getWorldList();
			if (worlds == null)
			{
				loadWorlds();
				return out;
			}

			loadedWorlds = Arrays.asList(worlds);
		}
		catch (Exception e)
		{
			log.warn("Game couldn't load worlds, falling back to RuneLite API.");
			loadedWorlds = lookup();
		}

		for (World world : loadedWorlds)
		{
			if (filter.test(world))
			{
				out.add(world);
			}
		}

		return out;
	}

	public static World getFirst(Predicate<World> filter)
	{
		return getAll(filter)
			.stream()
			.findFirst()
			.orElse(null);
	}

	public static World getFirst(int id)
	{
		return getFirst(x -> x.getId() == id);
	}

	public static World getRandom(Predicate<World> filter)
	{
		List<World> all = getAll(filter);
		if (all.isEmpty())
		{
			return null;
		}

		return all.get(Rand.nextInt(0, all.size()));
	}

	public static int getCurrentId()
	{
		return Static.getClient().getWorld();
	}

	public static void hopTo(World world)
	{
		hopTo(world, false);
	}

	public static void hopTo(World world, boolean spam)
	{
		if (!isHopperOpen())
		{
			openHopper();
			Time.sleepUntil(RS2Worlds::isHopperOpen, 3000);
		}

		RS2Widget rememberOption = RS2Dialog.getOptions().stream()
			.filter(x -> x.getText().contains("Yes. In future, only warn about"))
			.findFirst()
			.orElse(null);
		if (RS2Widgets.isVisible(rememberOption))
		{
			RS2Dialog.chooseOption(2);
			Time.sleepUntil(() -> Game.getState() == GameState.HOPPING, 3000);
			return;
		}

		log.debug("Hoping to world {}", world.getId());
		// @TODO FIX
		//Static.getClient().interact(1, MenuAction.CC_OP.getId(), world.getId(), WidgetInfo.WORLD_SWITCHER_LIST.getId());
		Static.invokeMenuAction("Switch", "<col=ff9040>" + (world.getId()) + "</col>", 1, MenuAction.CC_OP.getId(), world.getId(), WidgetInfo.WORLD_SWITCHER_LIST.getId(), -1, -1, -1);
		if (!spam)
		{
			Time.sleepUntil(() -> Game.getState() == GameState.HOPPING, 3000);
		}

		if (RS2Dialog.isViewingOptions())
		{
			RS2Dialog.chooseOption(2);
			Time.sleepUntil(() -> Game.getState() == GameState.HOPPING, 3000);
		}
	}

	public static World getCurrentWorld()
	{
		return getFirst(Static.getClient().getWorld());
	}

	public static boolean inMembersWorld()
	{
		return inMembersWorld(false);
	}

	public static boolean inMembersWorld(boolean useLookUp)
	{
		if (useLookUp)
		{
			World currentWorld = lookup().stream()
				.filter(x -> x.getId() == getCurrentId())
				.findFirst()
				.orElse(null);
			if (currentWorld != null)
			{
				return Arrays.asList(currentWorld.getTypes()).contains(WorldType.MEMBERS);
			}
		}
		return Static.getClient().getWorldType().contains(WorldType.MEMBERS);
	}

	public static void loadWorlds()
	{
		if (Game.isOnLoginScreen())
		{
			if (openLobbyWorlds())
			{
				Time.sleep(200);
				closeLobbyWorlds();
				return;
			}
			Time.sleep(200);
			return;
		}

		if (Game.isLoggedIn())
		{
			openHopper();
		}
	}

	public static void openHopper()
	{
		if (!RS2Tabs.isOpen(Tab.LOG_OUT))
		{
			RS2Tabs.open(Tab.LOG_OUT);
		}
		// @TODO FIX
		//Static.getClient().interact(1, MenuAction.CC_OP.getId(), -1, WidgetInfo.WORLD_SWITCHER_BUTTON.getId());
		Static.invokeMenuAction("World Switcher", "", 1, MenuAction.CC_OP.getId(), -1, WidgetInfo.WORLD_SWITCHER_BUTTON.getId(), -1, -1, -1);
	}

	public static boolean openLobbyWorlds()
	{
		// @TODO FIX
		/*if (Static.getClient().loadWorlds())
		{
			Static.getClient().setWorldSelectOpen(true);
			return true;
		}*/
		return false;
	}

	public static void closeLobbyWorlds()
	{
		// @TODO FIX
		//Static.getClient().setWorldSelectOpen(false);
	}

	public static boolean isHopperOpen()
	{
		return RS2Widgets.isVisible(RS2Widgets.get(WidgetInfo.WORLD_SWITCHER_LIST));
	}

	private static WorldType toApiWorldType(net.runelite.http.api.worlds.WorldType httpWorld)
	{
		if (httpWorld == net.runelite.http.api.worlds.WorldType.TOURNAMENT)
		{
			return WorldType.TOURNAMENT_WORLD;
		}

		return WorldType.valueOf(httpWorld.name());
	}
}