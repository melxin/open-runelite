/*
 * Copyright (c) 2025, Melxin <https://github.com/melxin>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.openrl.api.rs2.providers.query;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.GroundObject;
import net.runelite.api.Tile;
import net.runelite.api.WallObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.abstraction.AbstractQuery;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2TileObject;

public class RS2TileObjectQuery extends AbstractQuery<RS2TileObject, RS2TileObjectQuery>
{
	private RS2TileObjectQuery()
	{
	}

	public static RS2TileObjectQuery query()
	{
		return new RS2TileObjectQuery();
	}

	@Override
	protected List<RS2TileObject> all(Predicate<? super RS2TileObject> filter)
	{
		return RS2TileQuery.query().stream()
			.flatMap(tile -> getTileObjects(tile.getTile()).stream())
			.filter(filter)
			.collect(Collectors.toList());
	}

	private static List<RS2TileObject> getTileObjects(Tile tile)
	{
		final List<RS2TileObject> out = new ArrayList<>();
		if (tile == null)
		{
			return out;
		}

		final DecorativeObject dec = tile.getDecorativeObject();
		if (dec != null && dec.getId() != -1)
		{
			out.add(new RS2TileObject(dec));
		}

		final WallObject wall = tile.getWallObject();
		if (wall != null && wall.getId() != -1)
		{
			out.add(new RS2TileObject(wall));
		}

		final GroundObject grnd = tile.getGroundObject();
		if (grnd != null && grnd.getId() != -1)
		{
			out.add(new RS2TileObject(grnd));
		}

		final GameObject[] gameObjects = tile.getGameObjects();
		if (gameObjects != null)
		{
			for (GameObject gameObject : gameObjects)
			{
				if (gameObject == null || gameObject.getId() == -1)
				{
					continue;
				}

				out.add(new RS2TileObject(gameObject));
			}
		}

		return out;
	}

	// Query

	public RS2TileObjectQuery idEquals(int... ids)
	{
		return and(x ->
		{
			final int objectId = x.getId();
			for (int id : ids)
			{
				if (objectId == id)
				{
					return true;
				}
			}
			return false;
		});
	}

	public RS2TileObjectQuery nameEquals(String... names)
	{
		return and(x ->
		{
			final String objectName = x.getName();
			if (objectName != null)
			{
				for (String name : names)
				{
					if (objectName.equalsIgnoreCase(name))
					{
						return true;
					}
				}
			}
			return false;
		});
	}

	public RS2TileObjectQuery nameContains(String... names)
	{
		return and(x ->
		{
			final String objectName = x.getName();
			if (objectName != null)
			{
				for (String name : names)
				{
					if (objectName.toLowerCase().contains(name.toLowerCase()))
					{
						return true;
					}
				}
			}
			return false;
		});
	}

	public RS2TileObjectQuery actionEquals(String... actions)
	{
		return and(x ->
		{
			final String[] objectActions = x.getActions();
			if (objectActions != null)
			{
				for (String action : actions)
				{
					for (String objectAction : objectActions)
					{
						if (objectAction != null && objectAction.equalsIgnoreCase(action))
						{
							return true;
						}
					}
				}
			}
			return false;
		});
	}

	public RS2TileObjectQuery actionContains(String... actions)
	{
		return and(x ->
		{
			final String[] objectActions = x.getActions();
			if (objectActions != null)
			{
				for (String action : actions)
				{
					for (String objectAction : objectActions)
					{
						if (objectAction != null)
						{
							if (objectAction.toLowerCase().contains(action.toLowerCase()))
							{
								return true;
							}
						}
					}
				}
			}
			return false;
		});
	}

	public RS2TileObjectQuery within(WorldArea area)
	{
		return and(x ->
		{
			final WorldPoint wp = x.getWorldLocation();
			return wp != null && area.contains(wp);
		});
	}

	public RS2TileObjectQuery withinDistance(WorldPoint source, int distance)
	{
		return and(x ->
		{
			final WorldPoint wp = x.getWorldLocation();
			return wp != null && wp.distanceTo2D(source) <= distance;
		});
	}

	public RS2TileObjectQuery withinDistance(int distance)
	{
		return withinDistance(getLocal().getWorldLocation(), distance);
	}

	// Results

	public RS2TileObject getNearest(LocalPoint source)
	{
		return stream().min(Comparator.comparingInt(x -> x.getLocalLocation().distanceTo(source))).orElse(null);
	}

	public RS2TileObject getNearest(WorldPoint source)
	{
		return stream().min(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo2D(source))).orElse(null);
	}

	public RS2TileObject getNearest()
	{
		return getNearest(getLocal().getWorldLocation());
	}

	public RS2TileObject getFarthest(LocalPoint source)
	{
		return stream().max(Comparator.comparingInt(x -> x.getLocalLocation().distanceTo(source))).orElse(null);
	}

	public RS2TileObject getFarthest(WorldPoint source)
	{
		return stream().max(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo2D(source))).orElse(null);
	}

	public RS2TileObject getFarthest()
	{
		return getFarthest(getLocal().getWorldLocation());
	}

	public List<RS2TileObject> getSortedByDistance(WorldPoint source)
	{
		return sorted(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo2D(source)));
	}

	public List<RS2TileObject> getSortedByDistance()
	{
		return getSortedByDistance(getLocal().getWorldLocation());
	}
}