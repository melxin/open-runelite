package net.runelite.client.plugins.openrl.api.rs2.wrappers.api;

import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

public interface Locatable
{
	/**
	 * Gets the server-side location of the actor.
	 * <p>
	 * This value is typically ahead of where the client renders and is not
	 * affected by things such as animations.
	 *
	 * @return the server location
	 */
	WorldPoint getWorldLocation();

	/**
	 * Gets the client-side location of the actor.
	 *
	 * @return the client location
	 */
	LocalPoint getLocalLocation();

	default int distanceTo(LocalPoint point)
	{
		return getLocalLocation().distanceTo(point);
	}

	default int distanceTo(WorldPoint point)
	{
		return getWorldLocation().distanceTo(point);
	}

	default int distanceTo2D(WorldPoint point)
	{
		return getWorldLocation().distanceTo2D(point);
	}

	default int distanceTo(Locatable locatable)
	{
		return getWorldLocation().distanceTo(locatable.getWorldLocation());
	}

	default int distanceTo2D(Locatable locatable)
	{
		return getWorldLocation().distanceTo2D(locatable.getWorldLocation());
	}

	default int getWorldX()
	{
		return getWorldLocation().getX();
	}

	default int getWorldY()
	{
		return getWorldLocation().getY();
	}

	default int getPlane()
	{
		return getWorldLocation().getPlane();
	}

	/**
	 * Gets the server-side location of the actor as a WorldArea.
	 * <p>
	 * This value is typically ahead of where the client renders and is not
	 * affected by things such as animations.
	 *
	 * @return the server location
	 */
	default WorldArea getWorldArea()
	{
		return getWorldLocation().toWorldArea();
	}
}