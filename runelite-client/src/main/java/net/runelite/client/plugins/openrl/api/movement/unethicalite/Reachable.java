package net.runelite.client.plugins.openrl.api.movement.unethicalite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import net.runelite.api.CollisionData;
import net.runelite.api.Tile;
import net.runelite.api.WallObject;
import net.runelite.api.coords.Direction;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.rs2.providers.entities.RS2Players;
import net.runelite.client.plugins.openrl.api.rs2.providers.scene.RS2Tiles;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Player;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Tile;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2WallObject;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2WorldArea;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.api.Locatable;

public class Reachable
{
	private static final int MAX_ATTEMPTED_TILES = 64 * 64;

	public static boolean check(int flag, int checkFlag)
	{
		return (flag & checkFlag) != 0;
	}

	public static boolean isObstacle(int endFlag)
	{
		return check(endFlag, 0x100 | 0x20000 | 0x200000 | 0x1000000);
	}

	public static boolean isObstacle(WorldPoint worldPoint)
	{
		return isObstacle(getCollisionFlag(worldPoint));
	}

	public static int getCollisionFlag(WorldPoint point)
	{
		final CollisionData[] collisionMaps = Static.getClient().getCollisionMaps();
		if (collisionMaps == null)
		{
			return 0xFFFFFF;
		}

		final CollisionData collisionData = collisionMaps[Static.getClient().getPlane()];
		if (collisionData == null)
		{
			return 0xFFFFFF;
		}

		final LocalPoint localPoint = LocalPoint.fromWorld(Static.getClient(), point);
		if (localPoint == null)
		{
			return 0xFFFFFF;
		}

		return collisionData.getFlags()[localPoint.getSceneX()][localPoint.getSceneY()];
	}

	public static boolean isWalled(Direction direction, int startFlag)
	{
		switch (direction)
		{
			case NORTH:
				return check(startFlag, 0x2);
			case SOUTH:
				return check(startFlag, 0x20);
			case WEST:
				return check(startFlag, 0x80);
			case EAST:
				return check(startFlag, 0x8);
			default:
				throw new IllegalArgumentException();
		}
	}

	public static boolean isWalled(WorldPoint source, WorldPoint destination)
	{
		return isWalled(RS2Tiles.getAt(source), RS2Tiles.getAt(destination));
	}

	public static boolean isWalled(RS2Tile source, Tile destination)
	{
		final WallObject wall = source.getWallObject();
		if (wall == null)
		{
			return false;
		}

		final WorldPoint a = source.getWorldLocation();
		final WorldPoint b = destination.getWorldLocation();

		switch (wall.getOrientationA())
		{
			case 1:
				return a.dx(-1).equals(b) || a.dx(-1).dy(1).equals(b) || a.dx(-1).dy(-1).equals(b);
			case 2:
				return a.dy(1).equals(b) || a.dx(-1).dy(1).equals(b) || a.dx(1).dy(1).equals(b);
			case 4:
				return a.dx(1).equals(b) || a.dx(1).dy(1).equals(b) || a.dx(1).dy(-1).equals(b);
			case 8:
				return a.dy(-1).equals(b) || a.dx(-1).dy(-1).equals(b) || a.dx(-1).dy(1).equals(b);
			default:
				return false;
		}
	}

	public static boolean hasDoor(WorldPoint source, Direction direction)
	{
		final RS2Tile tile = RS2Tiles.getAt(source);
		if (tile == null)
		{
			return false;
		}

		return hasDoor(tile, direction);
	}

	public static boolean hasDoor(RS2Tile source, Direction direction)
	{
		final WallObject wall = source.getWallObject();
		if (wall == null)
		{
			return false;
		}

		return isWalled(direction, getCollisionFlag(source.getWorldLocation())) && new RS2WallObject(wall).hasAction("Open", "Close");
	}

	public static boolean isDoored(RS2Tile source, Tile destination)
	{
		final WallObject wall = source.getWallObject();
		if (wall == null)
		{
			return false;
		}

		return isWalled(source, destination) && new RS2WallObject(wall).hasAction("Open");
	}

	public static boolean canWalk(Direction direction, int startFlag, int endFlag)
	{
		if (isObstacle(endFlag))
		{
			return false;
		}

		return !isWalled(direction, startFlag);
	}

	public static WorldPoint getNeighbour(Direction direction, WorldPoint source)
	{
		switch (direction)
		{
			case NORTH:
				return source.dy(1);
			case SOUTH:
				return source.dy(-1);
			case WEST:
				return source.dx(-1);
			case EAST:
				return source.dx(1);
			default:
				throw new IllegalArgumentException();
		}
	}

	public static List<WorldPoint> getNeighbours(WorldPoint destination)
	{
		final List<WorldPoint> out = new ArrayList<>();
		for (Direction dir : Direction.values())
		{
			final WorldPoint neighbour = getNeighbour(dir, destination);
			if (!neighbour.isInScene(Static.getClient()))
			{
				continue;
			}

			/*if (destination instanceof Locatable)
			{
				Locatable targetObject = (Locatable) destination;
				boolean containsPoint;
				if (targetObject instanceof RS2GameObject)
				{
					containsPoint = ((GameObject) targetObject).getWorldLocation().toWorldArea().contains(neighbour);
				}
				else
				{
					containsPoint = targetObject.getWorldLocation().equals(neighbour);
				}

				if (containsPoint
					&& (!isWalled(dir, getCollisionFlag(dest)) || targetObject instanceof WallObject))
				{
					out.add(neighbour);
					continue;
				}
			}*/

			if (!canWalk(dir, getCollisionFlag(destination), getCollisionFlag(neighbour)))
			{
				continue;
			}

			out.add(neighbour);
		}

		return out;
	}

	public static List<WorldPoint> getVisitedTiles(WorldPoint destination)
	{
		final RS2Player local = RS2Players.getLocal();
		if (local == null || !destination.isInScene(Static.getClient()))
		{
			return Collections.emptyList();
		}

		final List<WorldPoint> visitedTiles = new ArrayList<>();
		final LinkedList<WorldPoint> queue = new LinkedList<>();

		if (local.getWorldLocation().getPlane() != destination.getPlane())
		{
			return visitedTiles;
		}

		queue.add(local.getWorldLocation());

		while (!queue.isEmpty())
		{
			if (visitedTiles.size() > MAX_ATTEMPTED_TILES)
			{
				return visitedTiles;
			}

			final WorldPoint current = queue.pop();
			visitedTiles.add(current);

			if (current.equals(destination))
			{
				return visitedTiles;
			}

			final List<WorldPoint> neighbours = getNeighbours(destination)
				.stream().filter(x -> !visitedTiles.contains(x) && !queue.contains(x))
				.collect(Collectors.toList());
			queue.addAll(neighbours);
		}

		return visitedTiles;
	}

	public static boolean isInteractable(Locatable locatable)
	{
		return getInteractable(locatable).stream().anyMatch(Reachable::isWalkable);
	}

	public static List<WorldPoint> getInteractable(Locatable locatable)
	{
		final WorldArea locatableArea = locatable.getWorldArea();
		final WorldArea surrounding = new RS2WorldArea(locatableArea).offset(1);

		// List of tiles that can interact with worldArea and can be walked on
		return surrounding.toWorldPointList().stream()
			.filter(p -> !locatableArea.contains(p))
			.filter(p -> new RS2WorldArea(locatableArea).canMelee(Static.getClient().getTopLevelWorldView(), new RS2WorldArea(p.toWorldArea())))
			.filter(p -> !isObstacle(p))
			.collect(Collectors.toList());
	}

	public static boolean isWalkable(WorldPoint worldPoint)
	{
		return getVisitedTiles(worldPoint).contains(worldPoint);
	}
}