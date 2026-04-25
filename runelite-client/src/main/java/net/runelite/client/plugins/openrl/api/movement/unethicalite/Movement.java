package net.runelite.client.plugins.openrl.api.movement.unethicalite;

import lombok.extern.slf4j.Slf4j;
import java.util.Comparator;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.commons.Rand;
import net.runelite.client.plugins.openrl.api.commons.Time;
import net.runelite.client.plugins.openrl.api.game.Vars;
import net.runelite.client.plugins.openrl.api.input.Mouse;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.Walker;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.BankLocation;
import net.runelite.client.plugins.openrl.api.reflection.Reflection;
import net.runelite.client.plugins.openrl.api.rs2.providers.camera.RS2Camera;
import net.runelite.client.plugins.openrl.api.rs2.providers.entities.RS2Players;
import net.runelite.client.plugins.openrl.api.rs2.providers.minimap.RS2MiniMap;
import net.runelite.client.plugins.openrl.api.rs2.providers.scene.RS2Tiles;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.RS2Widgets;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Player;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Tile;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Widget;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2WorldArea;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.api.Locatable;

@Slf4j
public class Movement
{
	private static final int STAMINA_VARBIT = 25;
	private static final int RUN_VARP = 173;

	public static void setDestination(int sceneX, int sceneY)
	{
		//Static.getClient().getTopLevelWorldView().getScene().setBaseX(sceneX);
		//Static.getClient().getTopLevelWorldView().getScene().setBaseY(sceneY);
		//Static.getClient().getTopLevelWorldView().getScene().setViewportWalking(true);
		Reflection.setDestination(sceneX, sceneY);
	}

	public static WorldPoint getDestination()
	{
		final Client client = Static.getClient();
		final LocalPoint localDestinationLocation = client.getLocalDestinationLocation();
		if (localDestinationLocation == null)
		{
			return null;
		}

		final int destinationX = localDestinationLocation.getSceneX();
		final int destinationY = localDestinationLocation.getSceneY();

		if (destinationX == 0 && destinationY == 0)
		{
			return null;
		}

		return new WorldPoint(destinationX + client.getBaseX(), destinationX + client.getBaseY(), client.getPlane());
	}

	public static boolean isWalking()
	{
		final RS2Player local = RS2Players.getLocal();
		final WorldPoint destination = getDestination();
		return local.isMoving()
			&& destination != null
			&& destination.distanceTo(local.getWorldLocation()) > 4;
	}

	public static void walk(WorldPoint worldPoint)
	{
		final RS2Player local = RS2Players.getLocal();
		if (local == null)
		{
			return;
		}

		WorldPoint walkPoint = worldPoint;
		final RS2Tile destinationTile = RS2Tiles.getAt(worldPoint);
		// Check if tile is in loaded client scene
		if (destinationTile == null)
		{
			log.debug("Destination {} is not in scene", worldPoint);
			final RS2Tile nearestInScene = RS2Tiles.getAll()
				.stream()
				.min(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo(local.getWorldLocation())))
				.orElse(null);
			if (nearestInScene == null)
			{
				log.debug("Couldn't find nearest walkable tile");
				return;
			}

			walkPoint = nearestInScene.getWorldLocation();
		}

		//int sceneX = walkPoint.getX() - client.getBaseX();
		//int sceneY = walkPoint.getY() - client.getBaseY();
		//Point canv = Perspective.localToCanvas(client, LocalPoint.fromScene(sceneX, sceneY), client.getPlane());
		//int x = canv != null ? canv.getX() : -1;
		//int y = canv != null ? canv.getY() : -1;

		/*client.interact(
			0,
			MenuAction.WALK.getId(),
			sceneX,
			sceneY,
			x,
			y
		);*/
		//Static.invokeMenuAction(sceneX, sceneY, MenuAction.WALK, 0, 0, WorldView.TOPLEVEL, "Walk here", "", x, y);
		//walkCanvas(walkPoint);
		walkMiniMap(walkPoint);
		//setDestination(sceneX, sceneY);
	}

	public static boolean walkTo(WorldArea worldArea)
	{
		return Walker.walkTo(worldArea);
	}

	public static boolean walkTo(WorldArea worldArea, boolean disableTeleports)
	{
		return Walker.walkTo(worldArea, disableTeleports);
	}

	public static boolean walkTo(WorldArea worldArea, boolean disableTeleports, boolean disableTransports)
	{
		return Walker.walkTo(worldArea, disableTeleports, disableTransports);
	}

	public static void walk(Locatable locatable)
	{
		walk(locatable.getWorldLocation());
	}

	public static boolean walkTo(WorldPoint worldPoint)
	{
		return Walker.walkTo(worldPoint);
	}

	public static boolean walkTo(WorldPoint worldPoint, boolean disableTeleports)
	{
		return Walker.walkTo(worldPoint, disableTeleports);
	}

	public static boolean walkTo(WorldPoint worldPoint, boolean disableTeleports, boolean disableTransports)
	{
		return Walker.walkTo(worldPoint, disableTeleports, disableTransports);
	}

	public static boolean walkTo(Locatable locatable)
	{
		return walkTo(locatable.getWorldLocation());
	}

	public static boolean walkTo(BankLocation bankLocation)
	{
		return walkTo(bankLocation.getArea());
	}

	public static boolean walkTo(int x, int y)
	{
		return walkTo(x, y, Static.getClient().getPlane());
	}

	public static boolean walkTo(int x, int y, int plane)
	{
		return walkTo(new WorldPoint(x, y, plane));
	}

	/**
	 * Walk next to a Locatable.
	 * This will first attempt to walk to tile that can interact with the locatable.
	 */
	public static boolean walkNextTo(Locatable locatable)
	{
		// WorldPoints that can interact with the locatable
		final List<WorldPoint> interactPoints = Reachable.getInteractable(locatable);

		// If no tiles are interactable, use un-interactable tiles instead  (exclusing self)
		if (interactPoints.isEmpty())
		{
			interactPoints.addAll(new RS2WorldArea(locatable.getWorldArea()).offset(1).toWorldPointList());
			interactPoints.removeIf(p -> locatable.getWorldArea().contains(p));
		}

		// First WorldPoint that is walkable from the list of interactPoints
		final WorldPoint walkableInteractPoint = interactPoints.stream()
			.filter(Reachable::isWalkable)
			.findFirst()
			.orElse(null);

		// Priority to a walkable tile, otherwise walk to the first tile next to locatable
		return (walkableInteractPoint != null) ? walkTo(walkableInteractPoint) : walkTo(interactPoints.get(0));
	}

	public static boolean isRunEnabled()
	{
		return Vars.getVarp(RUN_VARP) == 1;
	}

	public static void toggleRun()
	{
		final RS2Widget widget = RS2Widgets.get(WidgetInfo.MINIMAP_TOGGLE_RUN_ORB);
		if (widget != null)
		{
			widget.interact("Toggle Run");
		}
	}

	public static boolean isStaminaBoosted()
	{
		return Vars.getBit(STAMINA_VARBIT) == 1;
	}

	public static int getRunEnergy()
	{
		return Static.getClient().getEnergy() / 100;
	}

	public static int calculateDistance(WorldArea destination)
	{
		return Walker.calculatePath(destination).size();
	}

	public static int calculateDistance(WorldPoint start, WorldArea destination)
	{
		return calculateDistance(List.of(start), destination);
	}

	public static int calculateDistance(List<WorldPoint> start, WorldArea destination)
	{
		return Walker.calculatePath(start, destination).size();
	}

	public static int calculateDistance(WorldPoint destination)
	{
		return calculateDistance(destination.toWorldArea());
	}

	public static int calculateDistance(WorldPoint start, WorldPoint destination)
	{
		return calculateDistance(start, destination.toWorldArea());
	}

	public static int calculateDistance(List<WorldPoint> start, WorldPoint destination)
	{
		return calculateDistance(start, destination.toWorldArea());
	}

	private static boolean walkMiniMap(WorldPoint worldPoint)
	{
		return walkMiniMap(worldPoint, 5);
	}

	private static boolean walkMiniMap(WorldPoint worldPoint, double zoomDistance)
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

		interact(point.getX(), point.getY());
		return true;
	}

	/**
	 * Used in instances like vorkath, jad, nmz
	 *
	 * @param localPoint A two-dimensional point in the local coordinate space.
	 */
	private static boolean walkLocalCanvas(LocalPoint localPoint)
	{
		final Point canvas = Perspective.localToCanvas(Static.getClient(), localPoint, Static.getClient().getTopLevelWorldView().getPlane());
		if (canvas == null)
		{
			return false;
		}

		final int canvasX = canvas.getX();
		final int canvasY = canvas.getY();

		interact(canvasX, canvasY);
		return true;
	}

	private static boolean walkCanvas(WorldPoint worldPoint)
	{
		final LocalPoint localPoint = LocalPoint.fromWorld(Static.getClient().getTopLevelWorldView(), worldPoint);
		if (localPoint == null)
		{
			log.error("Tried to walkCanvas but localpoint returned null");
			return false;
		}

		final Point canvas = Perspective.localToCanvas(Static.getClient(), localPoint, Static.getClient().getTopLevelWorldView().getPlane());
		if (canvas == null)
		{
			return false;
		}

		final int canvasX = canvas.getX();
		final int canvasY = canvas.getY();

		//if the tile is not on screen, use minimap
		if (!RS2Camera.isTileOnScreen(localPoint) || canvasX < 0 || canvasY < 0)
		{
			return walkMiniMap(worldPoint);
		}

		interact(canvasX, canvasY);
		return true;
	}

	private static void interact(Point clickPoint)
	{
		interact(clickPoint.getX(), clickPoint.getY());
	}

	private static void interact(int x, int y)
	{
		Static.getNaturalMouse().moveTo(x, y);
		Time.sleep(Rand.nextInt(10, 30));
		Mouse.click(x, y, false);
	}
}