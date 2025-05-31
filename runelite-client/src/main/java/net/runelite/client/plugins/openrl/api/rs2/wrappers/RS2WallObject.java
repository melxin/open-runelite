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
import java.awt.Shape;
import net.runelite.api.Renderable;
import net.runelite.api.WallObject;

public class RS2WallObject extends RS2TileObject implements WallObject
{
	@NonNull
	@Getter(AccessLevel.PUBLIC)
	private final WallObject wallObject;

	public RS2WallObject(@NonNull WallObject wallObject)
	{
		super(wallObject);
		this.wallObject = wallObject;
	}

	@Override
	public int getOrientationA()
	{
		return wallObject.getOrientationA();
	}

	@Override
	public int getOrientationB()
	{
		return wallObject.getOrientationB();
	}

	@Override
	public int getConfig()
	{
		return wallObject.getConfig();
	}

	@Override
	public Shape getConvexHull()
	{
		return wallObject.getConvexHull();
	}

	@Override
	public Shape getConvexHull2()
	{
		return wallObject.getConvexHull2();
	}

	@Override
	public Renderable getRenderable1()
	{
		return wallObject.getRenderable1();
	}

	@Override
	public Renderable getRenderable2()
	{
		return wallObject.getRenderable2();
	}
}