package net.runelite.client.plugins.openrl.api.movement;

import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;
import net.runelite.api.MenuAction;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.events.MenuAutomated;
import net.runelite.client.plugins.openrl.api.input.Mouse;
import net.runelite.client.plugins.openrl.api.rs2.providers.camera.RS2Camera;
import net.runelite.client.plugins.openrl.api.rs2.providers.entities.RS2Players;
import net.runelite.client.plugins.openrl.api.rs2.providers.minimap.RS2MiniMap;

@Slf4j
public class Pathfinder
{
	// Basic method to get a path (placeholder for actual pathfinding logic)
	public static List<WorldPoint> findPath(WorldPoint to)
	{
		final List<WorldPoint> path = new ArrayList<>();
		final int steps = 10;
		final WorldPoint from = RS2Players.getLocal().getWorldLocation();
		for (int i = 0; i <= steps; i++)
		{
			int x = from.getX() + (to.getX() - from.getX()) * i / steps;
			int y = from.getY() + (to.getY() - from.getY()) * i / steps;
			int plane = from.getPlane();
			path.add(new WorldPoint(x, y, plane));
		}
		return path;
	}

	// Method to walk along the path
	public static void walkPath(WorldPoint to)
	{
		final List<WorldPoint> path = findPath(to);
		for (WorldPoint point : path)
		{
			//RS2Tiles.getAt(point).walkHere();
			//walkMiniMap(point);
			walkCanvas(point);
		}
	}

	public static boolean walkMiniMap(WorldPoint worldPoint)
	{
		return walkMiniMap(worldPoint, 5);
	}

	public static boolean walkMiniMap(WorldPoint worldPoint, double zoomDistance)
	{
		if (Static.getClient().getMinimapZoom() != zoomDistance)
		{
			Static.getClient().setMinimapZoom(zoomDistance);
		}

		final Point point = RS2MiniMap.worldToMinimap(worldPoint);

		if (point == null || !RS2MiniMap.isPointInsideMinimap(point))
		{
			return false;
		}

		Mouse.click(point.getX(), point.getY(), false);
		return true;
	}

	/**
	 * Used in instances like vorkath, jad, nmz
	 *
	 * @param localPoint A two-dimensional point in the local coordinate space.
	 */
	public static void walkFastLocal(LocalPoint localPoint)
	{
		final Point canvas = Perspective.localToCanvas(Static.getClient(), localPoint, Static.getClient().getTopLevelWorldView().getPlane());
		final int canvasX = canvas != null ? canvas.getX() : -1;
		final int canvasY = canvas != null ? canvas.getY() : -1;

		Static.getEventBus().post(new MenuAutomated(canvasX, canvasY, MenuAction.WALK, 0, -1, -1, "Walk here", "", canvasX, canvasY));
	}

	public static boolean walkFastCanvas(WorldPoint worldPoint)
	{
		return walkFastCanvas(worldPoint, true);
	}

	public static boolean walkFastCanvas(WorldPoint worldPoint, boolean toggleRun)
	{

		//RS2Players.toggleRunEnergy(toggleRun);
		Point canvas;
		final LocalPoint localPoint = LocalPoint.fromWorld(Static.getClient().getTopLevelWorldView(), worldPoint);

		// @TODO FIX
		/*if (Static.getClient().getTopLevelWorldView().isInstance() && localPoint == null)
		{
			localPoint = Rs2LocalPoint.fromWorldInstance(worldPoint);
		}*/

		if (localPoint == null)
		{
			log.error("Tried to walk worldpoint {} using the canvas but localpoint returned null", worldPoint);
			return false;
		}

		canvas = Perspective.localToCanvas(Static.getClient(), localPoint, Static.getClient().getTopLevelWorldView().getPlane());

		final int canvasX = canvas != null ? canvas.getX() : -1;
		final int canvasY = canvas != null ? canvas.getY() : -1;

		//if the tile is not on screen, use minimap
		if (!RS2Camera.isTileOnScreen(localPoint) || canvasX < 0 || canvasY < 0)
		{
			return walkMiniMap(worldPoint);
		}
		Static.getEventBus().post(new MenuAutomated(canvasX, canvasY, MenuAction.WALK, 0, -1, -1, "Walk here", "", canvasX, canvasY));
		return true;
	}

	public static WorldPoint walkCanvas(WorldPoint worldPoint)
	{
		final LocalPoint localPoint = LocalPoint.fromWorld(Static.getClient().getTopLevelWorldView(), worldPoint);
		if (localPoint == null)
		{
			log.error("Tried to walkCanvas but localpoint returned null");
			return null;
		}
		final Point point = Perspective.localToCanvas(Static.getClient(), localPoint, Static.getClient().getTopLevelWorldView().getPlane());

		if (point == null)
		{
			return null;
		}

		Mouse.click(point.getX(), point.getY(), false);
		return worldPoint;
	}
}