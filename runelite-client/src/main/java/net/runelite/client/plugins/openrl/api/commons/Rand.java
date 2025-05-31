package net.runelite.client.plugins.openrl.api.commons;

import java.util.concurrent.ThreadLocalRandom;

public class Rand
{
	public static synchronized int nextInt(int min, int max)
	{
		return ThreadLocalRandom.current().nextInt(min, max);
	}

	public static synchronized int nextInt()
	{
		return ThreadLocalRandom.current().nextInt();
	}

	public static synchronized int nextInt(int bound)
	{
		return ThreadLocalRandom.current().nextInt(bound);
	}

	public static synchronized boolean nextBool()
	{
		return ThreadLocalRandom.current().nextBoolean();
	}

	public static synchronized double nextDouble()
	{
		return ThreadLocalRandom.current().nextDouble();
	}

	public static synchronized double nextDouble(double origin, double bound)
	{
		return ThreadLocalRandom.current().nextDouble(origin, bound);
	}

	public static synchronized double nextGaussian()
	{
		return ThreadLocalRandom.current().nextGaussian();
	}
}
