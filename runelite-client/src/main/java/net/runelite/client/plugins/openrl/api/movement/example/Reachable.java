package net.runelite.client.plugins.openrl.api.movement.example;

import net.runelite.api.CollisionData;
import net.runelite.api.CollisionDataFlag;
import net.runelite.client.plugins.openrl.Static;

// @TODO FIX
public class Reachable
{
	/**
	 * Checks if the tile at (x, y, plane) is walkable.
	 */
	public static boolean isWalkable(int x, int y, int plane)
	{
		final CollisionData[] collisionMaps = Static.getClient().getTopLevelWorldView().getCollisionMaps();
		if (collisionMaps == null)
		{
			return false;
		}

		final int[][] flags = collisionMaps[plane].getFlags();
		return (flags[x][y] & CollisionDataFlag.BLOCK_MOVEMENT_FULL) == 0;
	}
}