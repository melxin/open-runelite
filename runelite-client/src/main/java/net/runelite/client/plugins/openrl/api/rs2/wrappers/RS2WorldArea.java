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

import java.util.concurrent.ThreadLocalRandom;
import net.runelite.api.Point;
import net.runelite.api.WorldView;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

public class RS2WorldArea
{
	private final long packed;

	public RS2WorldArea(int x, int y, int width, int height, int plane)
	{
		this.packed = pack(x, y, width, height, plane);
	}

	public RS2WorldArea(WorldArea worldArea)
	{
		this.packed = pack(worldArea.getX(), worldArea.getY(), worldArea.getWidth(), worldArea.getHeight(), worldArea.getPlane());
	}

	public RS2WorldArea(long packed)
	{
		this.packed = packed;
	}

	public RS2WorldArea(WorldPoint location, int width, int height)
	{
		this.packed = pack(location.getX(), location.getY(), width, height, location.getPlane());
	}

	public RS2WorldArea(WorldPoint swLocation, WorldPoint neLocation)
	{
		this.packed = pack(swLocation.getX(), swLocation.getY(), neLocation.getX() - swLocation.getX() + 1, neLocation.getY() - swLocation.getY() + 1, swLocation.getPlane());
	}

	public int getX()
	{
		return unpackX(packed);
	}

	public int getY()
	{
		return unpackY(packed);
	}

	public int getWidth()
	{
		return unpackWidth(packed);
	}

	public int getHeight()
	{
		return unpackHeight(packed);
	}

	public int getPlane()
	{
		return unpackPlane(packed);
	}

	public long pack(int x, int y, int width, int height, int plane)
	{
		return (((long) (x & 0x3FFF)) << 50)
			| (((long) (y & 0x3FFF)) << 36)
			| (((long) (width & 0x3FFF)) << 22)
			| (((long) (height & 0x3FFF)) << 8)
			| ((long) (plane & 0x3));
	}

	public int unpackX(long packed)
	{
		return (int) ((packed >>> 50) & 0x3FFF);
	}

	public int unpackY(long packed)
	{
		return (int) ((packed >>> 36) & 0x3FFF);
	}

	public int unpackWidth(long packed)
	{
		return (int) ((packed >>> 22) & 0x3FFF);
	}

	public int unpackHeight(long packed)
	{
		return (int) ((packed >>> 8) & 0x3FFF);
	}

	public static int unpackPlane(long packed)
	{
		return (int) (packed & 0x3);
	}

	public WorldArea getWorldArea()
	{
		return new WorldArea(unpackX(packed), unpackY(packed), unpackWidth(packed), unpackHeight(packed), unpackPlane(packed));
	}

	public WorldPoint getCenter()
	{
		return new WorldPoint(getX() + (getWidth() / 2), getY() + (getHeight() / 2), getPlane());
	}

	public WorldArea offset(int offset)
	{
		return new WorldArea(getX() - offset, getY() - offset, getWidth() + (2 * offset), getHeight() + (2 * offset), getPlane());
	}

	public WorldPoint getRandom()
	{
		return new WorldPoint(ThreadLocalRandom.current().nextInt(getX(), getX() + getWidth()),
			ThreadLocalRandom.current().nextInt(getY(), getY() + getHeight()), getPlane());
	}

	/**
	 * Checks whether a coordinate is within melee distance of this area.
	 *
	 * @param other the coordinate
	 * @return true if in melee distance, false otherwise
	 * @see WorldArea#isInMeleeDistance(WorldArea)
	 */
	public boolean isInMeleeDistance(WorldPoint other)
	{
		return getWorldArea().isInMeleeDistance(other.toWorldArea());
	}

	/**
	 * Checks whether this area is within melee distance of another without blocking in-between.
	 *
	 * @param wv the worldview to test in
	 * @param other the other area
	 * @return true if in melee distance without blocking, false otherwise
	 */
	public boolean canMelee(WorldView wv, RS2WorldArea other)
	{
		if (getWorldArea().isInMeleeDistance(other.getWorldArea()))
		{
			final Point p1 = this.getComparisonPoint(other);
			final Point p2 = other.getComparisonPoint(this);
			final WorldArea w1 = new WorldArea(p1.getX(), p1.getY() , 1, 1, this.getPlane());
			return (w1.canTravelInDirection(wv, p2.getX() - p1.getX(), p2.getY() - p1.getY()));
		}
		return false;
	}

	/**
	 * Gets the point within this area that is closest to another.
	 *
	 * @param other the other area
	 * @return the closest point to the passed area
	 */
	private Point getComparisonPoint(RS2WorldArea other)
	{
		int x, y;

		int myX = this.getX();
		int myY = this.getY();
		int myWidth = this.getWidth();
		int myHeight = this.getHeight();

		int otherX = other.getX();
		int otherY = other.getY();

		if (otherX <= myX)
		{
			x = myX;
		}
		else if (otherX >= myX + myHeight - 1)
		{
			x = myX + myWidth - 1;
		}
		else
		{
			x = otherX;
		}
		if (otherY <= myY)
		{
			y = myY;
		}
		else if (otherY >= myY + myHeight - 1)
		{
			y = myY + myHeight - 1;
		}
		else
		{
			y = otherY;
		}
		return new Point(x, y);
	}
}