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
package net.runelite.client.plugins.openrl.api.rs2.wrappers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import javax.annotation.Nullable;
import net.runelite.api.Actor;
import net.runelite.api.Animation;
import net.runelite.api.Model;
import net.runelite.api.Node;
import net.runelite.api.Projectile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.api.Identifiable;

@RequiredArgsConstructor
public class RS2Projectile implements Projectile, Identifiable
{
	@NonNull
	@Getter(AccessLevel.PUBLIC)
	private final Projectile projectile;

	@Override
	public int getId()
	{
		return projectile.getId();
	}

	@Override
	public int getSourceLevel()
	{
		return projectile.getSourceLevel();
	}

	@Override
	public WorldPoint getSourcePoint()
	{
		return projectile.getSourcePoint();
	}

	@Nullable
	@Override
	public Actor getSourceActor()
	{
		return projectile.getSourceActor();
	}

	@Override
	public int getTargetLevel()
	{
		return projectile.getTargetLevel();
	}

	@Override
	public WorldPoint getTargetPoint()
	{
		return projectile.getTargetPoint();
	}

	@Nullable
	@Override
	public Actor getTargetActor()
	{
		return projectile.getTargetActor();
	}

	@Override
	public LocalPoint getTarget()
	{
		return projectile.getTarget();
	}

	@Override
	public double getX()
	{
		return projectile.getX();
	}

	@Override
	public double getY()
	{
		return projectile.getY();
	}

	@Override
	public double getZ()
	{
		return projectile.getZ();
	}

	@Override
	public int getOrientation()
	{
		return projectile.getOrientation();
	}

	@Nullable
	@Override
	public Animation getAnimation()
	{
		return projectile.getAnimation();
	}

	@Override
	public int getAnimationFrame()
	{
		return projectile.getAnimationFrame();
	}

	@Override
	public int getX1()
	{
		return projectile.getX1();
	}

	@Override
	public int getY1()
	{
		return projectile.getY1();
	}

	@Override
	public int getFloor()
	{
		return projectile.getFloor();
	}

	@Override
	public int getHeight()
	{
		return projectile.getHeight();
	}

	@Override
	public int getEndHeight()
	{
		return projectile.getEndHeight();
	}

	@Override
	public int getStartCycle()
	{
		return projectile.getStartCycle();
	}

	@Override
	public int getEndCycle()
	{
		return projectile.getEndCycle();
	}

	@Override
	public void setEndCycle(int cycle)
	{
		projectile.setEndCycle(cycle);
	}

	@Override
	public int getRemainingCycles()
	{
		return projectile.getRemainingCycles();
	}

	@Override
	public int getSlope()
	{
		return projectile.getSlope();
	}

	@Override
	public int getStartPos()
	{
		return projectile.getStartPos();
	}

	@Override
	public int getStartHeight()
	{
		return projectile.getStartHeight();
	}

	@Override
	public Model getModel()
	{
		return projectile.getModel();
	}

	@Override
	public int getModelHeight()
	{
		return projectile.getModelHeight();
	}

	@Override
	public void setModelHeight(int modelHeight)
	{
		projectile.setModelHeight(modelHeight);
	}

	@Override
	public int getAnimationHeightOffset()
	{
		return projectile.getAnimationHeightOffset();
	}

	@Override
	public int getRenderMode()
	{
		return projectile.getRenderMode();
	}

	@Override
	public Node getNext()
	{
		return projectile.getNext();
	}

	@Override
	public Node getPrevious()
	{
		return projectile.getPrevious();
	}

	@Override
	public long getHash()
	{
		return projectile.getHash();
	}
}