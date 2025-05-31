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

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.abstraction.AbstractSceneEntityQuery;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.api.SceneEntity;

public class RS2SceneEntityQuery extends AbstractSceneEntityQuery<SceneEntity, RS2SceneEntityQuery>
{
	private RS2SceneEntityQuery()
	{
	}

	public static RS2SceneEntityQuery query()
	{
		return new RS2SceneEntityQuery();
	}

	public static RS2ActorQuery actorQuery()
	{
		return RS2ActorQuery.query();
	}

	public static RS2PlayerQuery playerQuery()
	{
		return RS2PlayerQuery.query();
	}

	public static RS2NPCQuery npcQuery()
	{
		return RS2NPCQuery.query();
	}

	public static RS2TileObjectQuery tileObjectQuery()
	{
		return RS2TileObjectQuery.query();
	}

	public static RS2GameObjectQuery gameObjectQuery()
	{
		return RS2GameObjectQuery.query();
	}

	public static RS2WallObjectQuery wallObjectQuery()
	{
		return RS2WallObjectQuery.query();
	}

	public static RS2DecorativeObjectQuery decorativeObjectQuery()
	{
		return RS2DecorativeObjectQuery.query();
	}

	public static RS2GroundObjectQuery groundObjectQuery()
	{
		return RS2GroundObjectQuery.query();
	}

	public static RS2TileItemQuery tileItemQuery()
	{
		return RS2TileItemQuery.query();
	}

	@Override
	protected List<SceneEntity> all(Predicate<? super SceneEntity> filter)
	{
		return Stream.of(actorQuery().stream(),
			tileObjectQuery().stream(),
			tileItemQuery().stream())
			.flatMap(s -> s)
			.filter(filter)
			.collect(Collectors.toList());
	}
}