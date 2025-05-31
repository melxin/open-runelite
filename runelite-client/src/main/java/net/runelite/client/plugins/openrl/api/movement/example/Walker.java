package net.runelite.client.plugins.openrl.api.movement.example;

import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.runelite.api.MenuAction;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.commons.Rand;
import net.runelite.client.plugins.openrl.api.events.MenuAutomated;
import net.runelite.client.plugins.openrl.api.input.Mouse;
import net.runelite.client.plugins.openrl.api.rs2.providers.camera.RS2Camera;
import net.runelite.client.plugins.openrl.api.rs2.providers.entities.RS2Players;
import net.runelite.client.plugins.openrl.api.rs2.providers.minimap.RS2MiniMap;
import net.runelite.client.plugins.openrl.api.rs2.providers.scene.RS2Tiles;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.RS2Widgets;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Player;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Tile;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Widget;

// @TODO FIX
@Slf4j
public class Walker
{
	private static final int MIN_TILES_WALKED_IN_STEP = 4;
	private static final int MAX_TILES_WALKED_IN_STEP = 10;

	public static List<WorldPoint> path;

	public static boolean walkTo(WorldPoint to)
	{
		final RS2Player local = RS2Players.getLocal();
		final WorldPoint localWP = local.getWorldLocation();

		if (localWP.equals(to) || localWP == to || localWP.distanceTo(to) <= 1)
		{
			path = null;
			log.info("Destination reached!");
			return false;
		}

		if (path == null || path.isEmpty())
		{
			path = Pathfinder.findPath(to);
		}

		boolean offPath = path.stream().noneMatch(t -> t.distanceTo(localWP) <= 5 /*&& canPathTo(localWP, t)*/);
		if (offPath)
		{
			log.info("Refreshing path...");
			path = Pathfinder.findPath(to);
		}

		/*for (WorldPoint point : path)
		{
			//RS2Tiles.getAt(point).walkHere();
			//walkMiniMap(point);
			//walkCanvas(point);
			walkCanvas(point);
		}*/
		return walkAlong(path);
	}

	public static boolean walkAlong(List<WorldPoint> path)
	{
		final List<WorldPoint> remainingPath = remainingPath(path);
		return stepAlong(remainingPath);
	}

	public static boolean stepAlong(List<WorldPoint> path)
	{
		final List<WorldPoint> reachablePath = reachablePath(path);
		if (reachablePath.isEmpty())
		{
			return false;
		}
		int nextTileIdx = reachablePath.size() - 1;
		if (nextTileIdx <= MIN_TILES_WALKED_IN_STEP)
		{
			return step(reachablePath.get(nextTileIdx));
		}

		if (nextTileIdx > MAX_TILES_WALKED_IN_STEP)
		{
			nextTileIdx = MAX_TILES_WALKED_IN_STEP;
		}

		int targetDistance = Rand.nextInt(MIN_TILES_WALKED_IN_STEP, nextTileIdx);
		return step(reachablePath.get(targetDistance));
	}

	public static List<WorldPoint> remainingPath(List<WorldPoint> path)
	{
		final Player local = RS2Players.getLocal();
		if (local == null)
		{
			return Collections.emptyList();
		}

		final var nearest = path.stream().min(Comparator.comparingInt(x -> x.distanceTo(local.getWorldLocation())))
			.orElse(null);
		if (nearest == null)
		{
			return Collections.emptyList();
		}

		return path.subList(path.indexOf(nearest), path.size());
	}

	public static List<WorldPoint> reachablePath(List<WorldPoint> remainingPath)
	{
		final RS2Player local = RS2Players.getLocal();
		final List<WorldPoint> out = new ArrayList<>();
		for (WorldPoint p : remainingPath)
		{
			RS2Tile tile = RS2Tiles.getAt(p);
			if (tile == null)
			{
				break;
			}

			out.add(p);
		}

		if (out.isEmpty() || out.size() == 1 && out.get(0).equals(local.getWorldLocation()))
		{
			return Collections.emptyList();
		}

		return out;
	}

	public static boolean step(WorldPoint destination)
	{
		final RS2Player local = RS2Players.getLocal();
		log.debug("Stepping towards " + destination);
		walkCanvas(destination);

		if (local.getWorldLocation().equals(destination))
		{
			return false;
		}
		return true;
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

		Static.getEventBus().post(new MenuAutomated(canvasX, canvasY, MenuAction.WALK, 0, -1, WorldView.TOPLEVEL, "Walk here", "", canvasX, canvasY));
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
		Static.getEventBus().post(new MenuAutomated(canvasX, canvasY, MenuAction.WALK, 0, -1, WorldView.TOPLEVEL, "Walk here", "", canvasX, canvasY));
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
		//RS2Tiles.getAt(worldPoint).walkHere();
		return worldPoint;
	}

	public static void toggleRun()
	{
		final RS2Widget widget = RS2Widgets.get(WidgetInfo.MINIMAP_TOGGLE_RUN_ORB);
		if (widget != null)
		{
			widget.interact("Toggle Run");
		}
	}
}