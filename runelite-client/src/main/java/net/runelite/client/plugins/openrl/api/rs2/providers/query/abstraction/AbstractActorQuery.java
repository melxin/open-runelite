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
package net.runelite.client.plugins.openrl.api.rs2.providers.query.abstraction;

import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Actor;

public abstract class AbstractActorQuery<T extends RS2Actor, Q extends AbstractActorQuery<T, Q>> extends AbstractSceneEntityQuery<T, Q>
{
	// Queries

	public Q withCombatLevels(int... levels)
	{
		return and(x ->
		{
			for (int level : levels)
			{
				if (x.getCombatLevel() == level)
				{
					return true;
				}
			}
			return false;
		});
	}

	public Q withCombatLevelsInRange(int min, int max)
	{
		return and(x -> x.getCombatLevel() >= min && x.getCombatLevel() <= max);
	}

	public Q withAnimation(int animation)
	{
		return and(x -> x.getAnimation() == animation);
	}

	public Q notWithAnimation(int animation)
	{
		return not(x -> x.getAnimation() == animation);
	}

	public Q withGraphic(int... graphicIds)
	{
		return and(x -> x.hasGraphic(graphicIds));
	}

	public Q isInteractingWith(RS2Actor actor)
	{
		return and(x -> x.getInteracting().equals(actor));
	}

	public Q isNotInteracting()
	{
		return not(x -> x.isInteracting());
	}

	public Q isAlive()
	{
		return not(x -> x.isDead());
	}

	public Q isIdle()
	{
		return and(x -> x.isIdle());
	}

	public Q isAttackable()
	{
		return and(x -> x.isAttackable());
	}

	public Q isNotAttackable()
	{
		return not(x -> x.isAttackable());
	}

	public Q withHealthBarVisible()
	{
		return and(x -> x.isHealthBarVisible());
	}

	public Q notWithHealthBarVisible()
	{
		return not(x -> x.isHealthBarVisible());
	}
}