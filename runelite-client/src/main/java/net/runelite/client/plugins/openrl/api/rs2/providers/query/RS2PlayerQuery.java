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
import net.runelite.api.IndexedObjectSet;
import net.runelite.api.Player;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.abstraction.AbstractActorQuery;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Player;

public class RS2PlayerQuery extends AbstractActorQuery<RS2Player, RS2PlayerQuery>
{
	private RS2PlayerQuery()
	{
	}

	public static RS2PlayerQuery query()
	{
		return new RS2PlayerQuery();
	}

	@Override
	protected List<RS2Player> all(Predicate<? super RS2Player> filter)
	{
		final IndexedObjectSet<? extends Player> players = Static.getClient().getTopLevelWorldView().players();
		return players.stream()
			.map(RS2Player::new)
			.filter(filter)
			.collect(Collectors.toList());
	}

	public RS2Player getLocalPlayer()
	{
		return this.getLocal();
	}

	public RS2PlayerQuery excludeLocalPlayer()
	{
		return and(x -> x.getName() != null && !x.getName().equals(this.getLocal().getName()));
	}

	public RS2Player byIndex(int index)
	{
		return new RS2Player(Static.getClient().getTopLevelWorldView().players().byIndex(index));
	}
}