package net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder;

import net.runelite.api.coords.Direction;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.Reachable;
import net.runelite.client.plugins.openrl.api.rs2.providers.scene.RS2Tiles;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Tile;

public class LocalCollisionMap implements CollisionMap
{
	private final boolean blockDoors;

	public LocalCollisionMap(boolean blockDoors)
	{
		this.blockDoors = blockDoors;
	}

	@Override
	public boolean n(int x, int y, int z)
	{
		final WorldPoint current = new WorldPoint(x, y, z);
		if (Reachable.isObstacle(current))
		{
			return false;
		}

		final RS2Tile currentTile = RS2Tiles.getAt(current);
		final RS2Tile destinationTile = RS2Tiles.getAt(current.dy(1));

		if (currentTile != null
			&& destinationTile != null
			&& (Reachable.isDoored(currentTile, destinationTile) || Reachable.isDoored(destinationTile, currentTile))
			&& !blockDoors
		)
		{
			return !Reachable.isObstacle(destinationTile.getWorldLocation());
		}

		return Reachable.canWalk(Direction.NORTH, Reachable.getCollisionFlag(current), Reachable.getCollisionFlag(current.dy(1)));
	}

	@Override
	public boolean e(int x, int y, int z)
	{
		final WorldPoint current = new WorldPoint(x, y, z);
		if (Reachable.isObstacle(current))
		{
			return false;
		}

		final RS2Tile currentTile = RS2Tiles.getAt(current);
		final RS2Tile destinationTile = RS2Tiles.getAt(current.dx(1));

		if (currentTile != null
			&& destinationTile != null
			&& (Reachable.isDoored(currentTile, destinationTile) || Reachable.isDoored(destinationTile, currentTile))
			&& !blockDoors
		)
		{
			return !Reachable.isObstacle(destinationTile.getWorldLocation());
		}

		return Reachable.canWalk(Direction.EAST, Reachable.getCollisionFlag(current), Reachable.getCollisionFlag(current.dx(1)));
	}
}