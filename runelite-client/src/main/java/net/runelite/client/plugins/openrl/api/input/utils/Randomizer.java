package net.runelite.client.plugins.openrl.api.input.utils;

import net.runelite.api.Point;
import java.awt.Rectangle;
import java.util.concurrent.ThreadLocalRandom;

public class Randomizer
{
	public static Point getRandomPointIn(Rectangle rect)
	{
		if (rect == null)
		{
			return new Point(0, 0);
		}

		final int xDeviation = (int) Math.log(rect.getWidth() * Math.PI);
		final int yDeviation = (int) Math.log(rect.getHeight() * Math.PI);
		return getRandomPointIn(rect, xDeviation, yDeviation);
	}

	public static Point getRandomPointIn(Rectangle rect, int xDeviation, int yDeviation)
	{
		final double centerX = rect.getCenterX();
		final double centerY = rect.getCenterY();

		final double randX = Math.max(
			Math.min(centerX + xDeviation * ThreadLocalRandom.current().nextGaussian(), rect.getMaxX()),
			rect.getMinX());

		final double randY = Math.max(
			Math.min(centerY + yDeviation * ThreadLocalRandom.current().nextGaussian(), rect.getMaxY()),
			rect.getMinY());

		return new Point((int) randX, (int) randY);
	}
}