package net.runelite.client.plugins.openrl.api.entities;

import org.jetbrains.annotations.Nullable;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.GroundObject;
import net.runelite.api.MenuAction;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Point;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.WallObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.events.MenuAutomated;
import net.runelite.client.plugins.openrl.api.input.utils.Randomizer;
import net.runelite.client.plugins.openrl.api.scene.Tiles;

public class TileObjects
{
	private static final TileObjects TILE_OBJECTS = new TileObjects();

	private TileObjects()
	{
	}

	public static List<TileObject> getAll()
	{
		return getAll(x -> true);
	}

	public static List<TileObject> getAll(Predicate<TileObject> filter)
	{
		return TILE_OBJECTS.all(filter);
	}

	private static List<TileObject> getTileObjects(Tile tile)
	{
		List<TileObject> out = new ArrayList<>();
		if (tile == null)
		{
			return out;
		}

		DecorativeObject dec = tile.getDecorativeObject();
		if (dec != null && dec.getId() != -1)
		{
			out.add(dec);
		}

		WallObject wall = tile.getWallObject();
		if (wall != null && wall.getId() != -1)
		{
			out.add(wall);
		}

		GroundObject grnd = tile.getGroundObject();
		if (grnd != null && grnd.getId() != -1)
		{
			out.add(grnd);
		}

		GameObject[] gameObjects = tile.getGameObjects();
		if (gameObjects != null)
		{
			for (GameObject gameObject : gameObjects)
			{
				if (gameObject == null || gameObject.getId() == -1)
				{
					continue;
				}

				out.add(gameObject);
			}
		}

		return out;
	}

	protected List<TileObject> all(Predicate<? super TileObject> filter)
	{
		final List<TileObject> out = new ArrayList<>();

		for (Tile tile : Tiles.getAll())
		{
			out.addAll(getTileObjects(tile));
		}

		return out.stream()
			.filter(filter)
			.collect(Collectors.toList());
	}

	protected List<TileObject> at(Tile tile, Predicate<? super TileObject> filter)
	{
		return getTileObjects(tile).stream()
				.filter(filter)
				.collect(Collectors.toList());
	}

	public static List<TileObject> getAt(WorldPoint worldPoint, Predicate<? super TileObject> filter)
	{
		return TILE_OBJECTS.at(Tiles.getAt(worldPoint), filter);
	}

	public static List<TileObject> getAt(int x, int y, int z, Predicate<? super TileObject> filter)
	{
		return TILE_OBJECTS.at(Tiles.getAt(x, y, z), filter);
	}

	public static void interact(TileObject tileObject, MenuAction menuAction)
	{
		int param0;
		int param1;
		final ObjectComposition objectComposition = Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getObjectDefinition(tileObject.getId())).orElse(null);
		if (objectComposition == null)
		{
			return;
		}

		if (tileObject instanceof GameObject)
		{
			final GameObject gameObject = (GameObject) tileObject;
			param0 = gameObject.sizeX() > 1 ? gameObject.getLocalLocation().getSceneX() - gameObject.sizeX() / 2 : gameObject.getLocalLocation().getSceneX();
			param1 = gameObject.sizeY() > 1 ? gameObject.getLocalLocation().getSceneY() - gameObject.sizeY() / 2 : gameObject.getLocalLocation().getSceneY();
		}
		else
		{
			param0 = tileObject.getLocalLocation().getSceneX();
			param1 = tileObject.getLocalLocation().getSceneY();
		}
		final int index = tileObject.getId();
		final int itemId = -1;
		final int worldViewId = -1;
		final String option = "";
		final String target = "";
		final Point clickPoint = getClickPoint(tileObject);
		final int x = clickPoint.getX();
		final int y = clickPoint.getY();

		Static.getEventBus().post(new MenuAutomated(param0, param1, menuAction, index, itemId, worldViewId, option, target, x, y));
	}

	public static void interact(TileObject tileObject, int index)
	{
		if (index == -1)
		{
			return;
		}
		interact(tileObject, getMenuAction(index));
	}

	public static void interact(TileObject tileObject, String action)
	{
		interact(tileObject, getActionIndex(tileObject, action));
	}

	@Nullable
	public static MenuAction getMenuAction(int index)
	{
		if (Static.getClient().isWidgetSelected())
		{
			return MenuAction.WIDGET_TARGET_ON_GAME_OBJECT;
		}

		switch (index)
		{
			case 0:
				return MenuAction.GAME_OBJECT_FIRST_OPTION;
			case 1:
				return MenuAction.GAME_OBJECT_SECOND_OPTION;
			case 2:
				return MenuAction.GAME_OBJECT_THIRD_OPTION;
			case 3:
				return MenuAction.GAME_OBJECT_FOURTH_OPTION;
			case 4:
				return MenuAction.GAME_OBJECT_FIFTH_OPTION;
			default:
				return null;
		}
	}

	@Nullable
	public static String[] getActions(TileObject tileObject)
	{
		final ObjectComposition objectComposition = Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getObjectDefinition(tileObject.getId())).orElse(null);
		if (objectComposition == null)
		{
			return null;
		}
		final String[] actions = objectComposition.getActions();
		return actions != null ? actions : null;
	}

	public static int getActionIndex(TileObject tileObject, String action)
	{
		final String[] actions = getActions(tileObject);
		if (actions == null)
		{
			return -1;
		}
		return Arrays.asList(actions).indexOf(action);
	}

	public static boolean hasAction(TileObject tileObject, String action)
	{
		final String[] actions = getActions(tileObject);
		if (actions == null)
		{
			return false;
		}

		return Arrays.asList(actions).contains(action);
	}

	public static Point getClickPoint(TileObject tileObject)
	{
		Shape shape;
		if (tileObject instanceof GameObject)
		{
			shape = ((GameObject) tileObject).getConvexHull();
		}
		else if (tileObject instanceof WallObject)
		{
			shape = ((WallObject) tileObject).getConvexHull();
		}
		else if (tileObject instanceof DecorativeObject)
		{
			shape = ((DecorativeObject) tileObject).getConvexHull();
		}
		else if (tileObject instanceof GroundObject)
		{
			shape = ((GroundObject) tileObject).getConvexHull();
		}
		else
		{
			shape = tileObject.getCanvasTilePoly();
		}

		return shape != null ? Randomizer.getRandomPointIn(shape.getBounds()) : null;
	}
}
