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
import net.runelite.api.GameObject;
import net.runelite.api.Point;
import net.runelite.api.Renderable;

public class RS2GameObject extends RS2TileObject implements GameObject
{
	@NonNull
	@Getter(AccessLevel.PUBLIC)
	private final GameObject gameObject;

	public RS2GameObject(@NonNull GameObject gameObject)
	{
		super(gameObject);
		this.gameObject = gameObject;
	}

	@Override
	public int sizeX()
	{
		return gameObject.sizeX();
	}

	@Override
	public int sizeY()
	{
		return gameObject.sizeY();
	}

	@Override
	public Point getSceneMinLocation()
	{
		return gameObject.getSceneMinLocation();
	}

	@Override
	public Point getSceneMaxLocation()
	{
		return gameObject.getSceneMaxLocation();
	}

	@Override
	public Shape getConvexHull()
	{
		return gameObject.getConvexHull();
	}

	@Override
	public int getOrientation()
	{
		return gameObject.getOrientation();
	}

	@Override
	public Renderable getRenderable()
	{
		return gameObject.getRenderable();
	}

	@Override
	public int getModelOrientation()
	{
		return gameObject.getModelOrientation();
	}

	@Override
	public int getConfig()
	{
		return gameObject.getConfig();
	}
}