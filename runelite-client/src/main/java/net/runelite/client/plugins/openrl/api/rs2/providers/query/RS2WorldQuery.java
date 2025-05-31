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
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.runelite.api.World;
import net.runelite.api.WorldType;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.abstraction.AbstractQuery;
import net.runelite.client.util.Text;
import net.runelite.http.api.worlds.WorldRegion;
import net.runelite.http.api.worlds.WorldResult;

public class RS2WorldQuery extends AbstractQuery<World, RS2WorldQuery>
{
	private RS2WorldQuery()
	{
	}

	public static RS2WorldQuery query()
	{
		return new RS2WorldQuery();
	}

	@Override
	protected List<World> all(Predicate<? super World> filter)
	{
		final World[] worldList = Static.getClient().getWorldList();

		if (worldList == null)
		{
			return lookup().stream()
				.filter(filter)
				.collect(Collectors.toList());
		}

		return Stream.of(worldList)
			.filter(filter)
			.collect(Collectors.toList());
	}

	private List<World> lookup()
	{
		final List<World> out = new ArrayList<>();
		final WorldResult lookup = Static.getWorldService().getWorlds();
		if (lookup == null)
		{
			return Collections.emptyList();
		}

		lookup.getWorlds().forEach(w ->
		{
			final World world = Static.getClient().createWorld();
			world.setActivity(w.getActivity());
			world.setAddress(w.getAddress());
			world.setId(w.getId());
			world.setPlayerCount(w.getPlayers());
			world.setLocation(w.getLocation());
			final EnumSet<WorldType> types = EnumSet.noneOf(WorldType.class);
			w.getTypes().stream().map(this::toApiWorldType).forEach(types::add);
			world.setTypes(types);
			out.add(world);
		});

		// @TODO FIX
		//Static.getClient().setWorldList(out.toArray(new World[0]));

		return out;
	}

	private WorldType toApiWorldType(net.runelite.http.api.worlds.WorldType httpWorld)
	{
		if (httpWorld == net.runelite.http.api.worlds.WorldType.TOURNAMENT)
		{
			return WorldType.TOURNAMENT_WORLD;
		}

		return WorldType.valueOf(httpWorld.name());
	}

	// Queries

	public RS2WorldQuery withId(int id)
	{
		return and(x -> x.getId() == id);
	}

	public RS2WorldQuery withIds(int... ids)
	{
		return and(x ->
		{
			final int worldId = x.getId();
			for (int id : ids)
			{
				if (worldId == id)
				{
					return true;
				}
			}
			return false;
		});
	}

	public RS2WorldQuery withIds(Collection<Integer> ids)
	{
		return and(x -> ids.contains(x.getId()));
	}

	public RS2WorldQuery withType(WorldType worldType)
	{
		return and(x -> x.getTypes().contains(worldType));
	}

	public RS2WorldQuery withTypes(WorldType... worldTypes)
	{
		return and(x ->
		{
			final EnumSet<WorldType> types = x.getTypes();
			for (WorldType worldType : worldTypes)
			{
				for (WorldType type : types)
				{
					if (worldType == type)
					{
						return true;
					}
				}
			}
			return false;
		});
	}

	public RS2WorldQuery notWithType(WorldType worldType)
	{
		return not(x -> x.getTypes().contains(worldType));
	}

	public RS2WorldQuery notWithTypes(WorldType... worldTypes)
	{
		return not(x ->
		{
			final EnumSet<WorldType> types = x.getTypes();
			for (WorldType worldType : worldTypes)
			{
				for (WorldType type : types)
				{
					if (worldType == type)
					{
						return true;
					}
				}
			}
			return false;
		});
	}

	public RS2WorldQuery withActivity(String activity)
	{
		return and(x -> Text.sanitize(x.getActivity().toLowerCase()).contains(activity.toLowerCase()));
	}

	public RS2WorldQuery notWithActivity(String activity)
	{
		return not(x -> Text.sanitize(x.getActivity().toLowerCase()).contains(activity.toLowerCase()));
	}

	public RS2WorldQuery withPlayerCount(int playerCount)
	{
		return and(x -> x.getPlayerCount() == playerCount);
	}

	public RS2WorldQuery withPlayerCountInRange(int min, int max)
	{
		return and(x -> x.getPlayerCount() >= min && x.getPlayerCount() <= max);
	}

	public RS2WorldQuery withRegion(WorldRegion worldRegion)
	{
		return and(x -> WorldRegion.valueOf(x.getLocation()).equals(worldRegion));
	}

	public RS2WorldQuery isMembers()
	{
		return and(x -> x.getTypes().contains(WorldType.MEMBERS));
	}

	public RS2WorldQuery isP2P()
	{
		return and(x -> x.getTypes().contains(WorldType.MEMBERS));
	}

	public RS2WorldQuery isF2P()
	{
		return and(x -> !x.getTypes().contains(WorldType.MEMBERS));
	}

	// Results

	public int getCurrentId()
	{
		return Static.getClient().getWorld();
	}
}