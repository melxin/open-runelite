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
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.runelite.api.GameObject;
import net.runelite.api.Tile;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.abstraction.AbstractTileObjectQuery;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2GameObject;

public class RS2GameObjectQuery extends AbstractTileObjectQuery<RS2GameObject, RS2GameObjectQuery>
{
	private RS2GameObjectQuery()
	{
	}

	public static RS2GameObjectQuery query()
	{
		return new RS2GameObjectQuery();
	}

	@Override
	protected List<RS2GameObject> all(Predicate<? super RS2GameObject> filter)
	{
		return RS2TileQuery.query().stream()
			.flatMap(tile -> getGameObjects(tile.getTile()).stream())
			.filter(filter)
			.collect(Collectors.toList());
	}

	private static List<RS2GameObject> getGameObjects(Tile tile)
	{
		final List<RS2GameObject> out = new ArrayList<>();
		if (tile == null)
		{
			return out;
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

				out.add(new RS2GameObject(gameObject));
			}
		}

		return out;
	}
}