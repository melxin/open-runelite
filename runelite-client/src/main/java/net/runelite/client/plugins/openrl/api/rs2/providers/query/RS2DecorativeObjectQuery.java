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
import net.runelite.api.DecorativeObject;
import net.runelite.api.Tile;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.abstraction.AbstractTileObjectQuery;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2DecorativeObject;

public class RS2DecorativeObjectQuery extends AbstractTileObjectQuery<RS2DecorativeObject, RS2DecorativeObjectQuery>
{
	private RS2DecorativeObjectQuery()
	{
	}

	public static RS2DecorativeObjectQuery query()
	{
		return new RS2DecorativeObjectQuery();
	}

	@Override
	protected List<RS2DecorativeObject> all(Predicate<? super RS2DecorativeObject> filter)
	{
		return RS2TileQuery.query().stream()
			.flatMap(tile -> getDecorativeObjects(tile.getTile()).stream())
			.filter(filter)
			.collect(Collectors.toList());
	}

	private static List<RS2DecorativeObject> getDecorativeObjects(Tile tile)
	{
		final List<RS2DecorativeObject> out = new ArrayList<>();
		if (tile == null)
		{
			return out;
		}

		final DecorativeObject dec = tile.getDecorativeObject();
		if (dec != null && dec.getId() != -1)
		{
			out.add(new RS2DecorativeObject(dec));
		}
		return out;
	}
}