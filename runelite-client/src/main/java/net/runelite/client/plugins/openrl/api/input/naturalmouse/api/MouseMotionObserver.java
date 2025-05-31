package net.runelite.client.plugins.openrl.api.input.naturalmouse.api;

/**
 * Use to observe mouse movement in MouseMotion
 */
public interface MouseMotionObserver
{
	void observe(int xPos, int yPos);
}
