package net.runelite.client.plugins.openrl.api.entities;

import org.jetbrains.annotations.Nullable;
import java.awt.Shape;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.runelite.api.MenuAction;
import net.runelite.api.Player;
import net.runelite.api.PlayerComposition;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.events.MenuAutomated;
import net.runelite.client.plugins.openrl.api.input.utils.Randomizer;

public class Players
{
	private static final Players PLAYERS = new Players();

	private Players()
	{
	}

	protected List<Player> all(Predicate<? super Player> filter)
	{
		return Static.getClient().getTopLevelWorldView().players()
				.stream()
				.filter(filter)
				.collect(Collectors.toList());
	}

	public static List<Player> getAll()
	{
		return getAll(player -> true);
	}

	public static List<Player> getAll(Predicate<Player> filter)
	{
		return PLAYERS.all(filter);
	}

	public static List<Player> getAll(String... names)
	{
		return PLAYERS.all(p -> Arrays.asList(names).contains(p.getName()));
	}

	public static Player getNearest()
	{
		return getNearest(player -> true);
	}

	public static Player getNearest(Predicate<Player> filter)
	{
		return getNearest(getLocal().getWorldLocation(), filter);
	}

	public static Player getNearest(String... names)
	{
		return getNearest(getLocal().getWorldLocation(), names);
	}

	public static Player getNearest(WorldPoint to, Predicate<Player> filter)
	{
		return getAll(filter.and(p -> p != getLocal())).stream().min(Comparator.comparingInt(p -> p.getWorldLocation().distanceTo2D(to))).orElse(null);
	}

	public static Player getNearest(WorldPoint to, String... names)
	{
		return getNearest(to, p -> Arrays.asList(names).contains(p.getName()));
	}

	public static void interact(Player player, MenuAction menuAction)
	{
		final int param0 = 0;
		final int param1 = 0;
		final int index = player.getId();
		final int itemId = -1;
		final int worldViewId = -1;
		final String option = "";
		final String target = "";
		final Point clickPoint = getClickPoint(player);
		final int x = clickPoint.getX();
		final int y = clickPoint.getY();
		Static.getEventBus().post(new MenuAutomated(param0, param1, menuAction, index, itemId, worldViewId, option, target, x, y));
	}

	public static void interact(Player player, int index)
	{
		interact(player, getMenuAction(index));
	}

	public static void interact(Player player, String action)
	{
		interact(player, getMenuAction(action));
	}

	@Nullable
	public static MenuAction getMenuAction(String action)
	{
		if (action.equalsIgnoreCase("attack"))
		{
			return MenuAction.PLAYER_SECOND_OPTION;
		}
		else if (action.equalsIgnoreCase("walk here"))
		{
			return MenuAction.WALK;
		}
		else if (action.equalsIgnoreCase("follow"))
		{
			return MenuAction.PLAYER_THIRD_OPTION;
		}
		else if (action.equalsIgnoreCase("challenge"))
		{
			return MenuAction.PLAYER_FIRST_OPTION;
		}
		else if (action.equalsIgnoreCase("trade with"))
		{
			return MenuAction.PLAYER_FOURTH_OPTION;
		}
		else if (action.equalsIgnoreCase("cast"))
		{
			return MenuAction.WIDGET_TARGET_ON_PLAYER;
		}
		else if (action.equalsIgnoreCase("use"))
		{
			return MenuAction.WIDGET_TARGET_ON_PLAYER;
		}

		return null;
	}

	@Nullable
	public static MenuAction getMenuAction(int index)
	{
		if (Static.getClient().isWidgetSelected())
		{
			return MenuAction.WIDGET_TARGET_ON_PLAYER;
		}

		switch (index)
		{
			case 0:
				return MenuAction.PLAYER_FIRST_OPTION;
			case 1:
				return MenuAction.PLAYER_SECOND_OPTION;
			case 2:
				return MenuAction.PLAYER_THIRD_OPTION;
			case 3:
				return MenuAction.PLAYER_FOURTH_OPTION;
			case 4:
				return MenuAction.PLAYER_FIFTH_OPTION;
			case 5:
				return MenuAction.PLAYER_SIXTH_OPTION;
			case 6:
				return MenuAction.PLAYER_SEVENTH_OPTION;
			case 7:
				return MenuAction.PLAYER_EIGHTH_OPTION;
			default:
				return null;
		}
	}

	public static Player getHintArrowPlayer()
	{
		return Static.getClient().getHintArrowPlayer();
	}

	public static Player getLocal()
	{
		final Player local = Static.getClient().getLocalPlayer();
		if (local == null)
		{
			throw new IllegalStateException("Local player was null, are you logged in?");
		}

		return local;
	}

	public static String getName(Player player)
	{
		return Static.getClientThread().runOnClientThreadOptional(() -> player.getName()).orElse(null);
	}

	public static PlayerComposition getComposition(Player player)
	{
		return Static.getClientThread().runOnClientThreadOptional(() -> player.getPlayerComposition()).orElse(null);
	}

	public static Point getClickPoint(Player player)
	{
		final Shape convexHull = player.getConvexHull();
		return convexHull != null ? Randomizer.getRandomPointIn(convexHull.getBounds()) : null;
	}
}
