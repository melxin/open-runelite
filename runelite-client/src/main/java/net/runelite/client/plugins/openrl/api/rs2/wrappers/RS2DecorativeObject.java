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
import net.runelite.api.DecorativeObject;
import net.runelite.api.Renderable;

public class RS2DecorativeObject extends RS2TileObject implements DecorativeObject
{
	@NonNull
	@Getter(AccessLevel.PUBLIC)
	private final DecorativeObject decorativeObject;

	public RS2DecorativeObject(@NonNull DecorativeObject decorativeObject)
	{
		super(decorativeObject);
		this.decorativeObject = decorativeObject;
	}

	@Override
	public Shape getConvexHull()
	{
		return decorativeObject.getConvexHull();
	}

	@Override
	public Shape getConvexHull2()
	{
		return decorativeObject.getConvexHull2();
	}

	@Override
	public Renderable getRenderable()
	{
		return decorativeObject.getRenderable();
	}

	@Override
	public Renderable getRenderable2()
	{
		return decorativeObject.getRenderable2();
	}

	@Override
	public int getXOffset()
	{
		return decorativeObject.getXOffset();
	}

	@Override
	public int getYOffset()
	{
		return decorativeObject.getYOffset();
	}

	@Override
	public int getXOffset2()
	{
		return decorativeObject.getXOffset2();
	}

	@Override
	public int getYOffset2()
	{
		return decorativeObject.getYOffset2();
	}

	@Override
	public int getConfig()
	{
		return decorativeObject.getConfig();
	}
}