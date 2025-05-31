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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.api.Locatable;

public class RS2WorldPoint
{
	private final int packedX;
	private final int packedY;
	private final int packedZ;

	public RS2WorldPoint(WorldPoint worldPoint)
	{
		this.packedX = packX(worldPoint.getX());
		this.packedY = packY(worldPoint.getY());
		this.packedZ = packZ(worldPoint.getPlane());
	}

	public RS2WorldPoint(int x, int y, int z)
	{
		this.packedX = packX(x);
		this.packedY = packY(y);
		this.packedZ = packZ(z);
	}

	public static RS2WorldPoint fromWorldPoint(WorldPoint worldPoint)
	{
		return new RS2WorldPoint(worldPoint);
	}

	public static RS2WorldPoint fromWorldPoint(int x, int y, int z)
	{
		return new RS2WorldPoint(x, y, z);
	}

	public int getX()
	{
		return unpackX(packedX);
	}

	public int getY()
	{
		return unpackY(packedY);
	}

	public int getPlane()
	{
		return unpackZ(packedZ);
	}

	/**
	 * Get the unpacked world point
	 *
	 * @return the unpacked world point.
	 */
	public WorldPoint getWorldPoint()
	{
		return new WorldPoint(unpackX(packedX), unpackY(packedY), unpackZ(packedZ));
	}

	public int distanceTo(WorldPoint other)
	{
		return getWorldPoint().distanceTo(other);
	}

	public int distanceTo2D(WorldPoint other)
	{
		return getWorldPoint().distanceTo2D(other);
	}

	/**
	 * Gets the straight-line distance between this point and another.
	 * <p>
	 * If the other point is not on the same plane, this method will return
	 * {@link Float#MAX_VALUE}. If ignoring the plane is wanted, use the
	 * {@link #distanceTo2DHypotenuse(WorldPoint)} method.
	 *
	 * @param other other point
	 * @return the straight-line distance
	 */
	public float distanceToHypotenuse(WorldPoint other)
	{
		if (other.getPlane() != getPlane())
		{
			return Float.MAX_VALUE;
		}

		return distanceTo2DHypotenuse(other);
	}

	/**
	 * Find the straight-line distance from this point to another point.
	 * <p>
	 * This method disregards the plane value of the two tiles and returns
	 * the simple distance between the X-Z coordinate pairs.
	 *
	 * @param other other point
	 * @return the straight-line distance
	 */
	public float distanceTo2DHypotenuse(WorldPoint other)
	{
		return (float) Math.hypot(getX() - other.getX(), getY() - other.getY());
	}

	/**
	 * Create a packed Jagex coordinate
	 */
	public static int pack(WorldPoint worldPoint)
	{
		return pack(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane());
	}

	/**
	 * Create a packed Jagex coordinate
	 */
	public static int pack(int x, int y, int plane)
	{
		return ((x & 0x3FFF) << 14) | (y & 0x3FFF) | ((plane & 0x3) << 28);
	}

	/**
	 * Create a WorldPoint from a packed Jagex coordinate
	 */
	public static WorldPoint unpack(int packed)
	{
		return new WorldPoint((packed >>> 14) & 0x3FFF, packed & 0x3FFF, (packed >>> 28) & 0x3);
	}

	public static int packX(int x)
	{
		return (x & 0x3FFF) << 14;
	}

	public static int packY(int y)
	{
		return y & 0x3FFF;
	}

	public static int packZ(int z)
	{
		return (z & 0x3) << 28;
	}

	public static int unpackX(int packedX)
	{
		return (packedX >>> 14) & 0x3FFF;
	}

	public static int unpackY(int packedY)
	{
		return packedY & 0x3FFF;
	}

	public static int unpackZ(int packedZ)
	{
		return (packedZ >>> 28) & 0x3;
	}

	public void outline(Client client, Graphics2D graphics2D, Color color)
	{
		outline(client, graphics2D, color, null);
	}

	public void outline(Client client, Graphics2D graphics, Color color, String text)
	{
		final LocalPoint localPoint = LocalPoint.fromWorld(client, getWorldPoint());
		if (localPoint == null)
		{
			return;
		}

		final Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
		if (poly == null)
		{
			return;
		}

		if (text != null)
		{
			final var stringX = (int) (poly.getBounds().getCenterX() -
				graphics.getFont().getStringBounds(text, graphics.getFontRenderContext()).getWidth() / 2);
			final var stringY = (int) poly.getBounds().getCenterY();
			graphics.setColor(color);
			graphics.drawString(text, stringX, stringY);
		}

		graphics.setColor(color);
		final Stroke originalStroke = graphics.getStroke();
		graphics.setStroke(new BasicStroke(2));
		graphics.draw(poly);
		graphics.setColor(new Color(0, 0, 0, 50));
		graphics.fill(poly);
		graphics.setStroke(originalStroke);
	}

	public int distanceTo(Locatable locatable)
	{
		return locatable.getWorldLocation().distanceTo(getWorldPoint());
	}

	public WorldArea createWorldArea(int width, int height)
	{
		return new WorldArea(getWorldPoint(), width, height);
	}
}