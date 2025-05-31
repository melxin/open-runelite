package net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder;

import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import javax.inject.Singleton;
import net.runelite.api.NpcID;
import net.runelite.api.WallObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.commons.Predicates;
import net.runelite.client.plugins.openrl.api.commons.Rand;
import net.runelite.client.plugins.openrl.api.commons.Time;
import net.runelite.client.plugins.openrl.api.game.Game;
import net.runelite.client.plugins.openrl.api.managers.RegionManager;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.Movement;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.Reachable;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.Teleport;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.Transport;
import net.runelite.client.plugins.openrl.api.rs2.providers.entities.RS2NPCs;
import net.runelite.client.plugins.openrl.api.rs2.providers.entities.RS2Players;
import net.runelite.client.plugins.openrl.api.rs2.providers.entities.RS2TileObjects;
import net.runelite.client.plugins.openrl.api.rs2.providers.items.RS2Equipment;
import net.runelite.client.plugins.openrl.api.rs2.providers.items.RS2Inventory;
import net.runelite.client.plugins.openrl.api.rs2.providers.scene.RS2Tiles;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.RS2Widgets;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.dialog.RS2Dialog;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2NPC;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Player;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Tile;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2TileObject;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2WallObject;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Widget;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2WorldPoint;
import net.runelite.client.plugins.openrl.plugins.walker.WalkerPlugin;

@Singleton
@Slf4j
public class Walker
{
	public static final int MAX_INTERACT_DISTANCE = 20;
	private static final int MIN_TILES_WALKED_IN_STEP = Static.getWalkerConfig().minStepDistance();
	private static final int MAX_TILES_WALKED_IN_STEP = Static.getWalkerConfig().maxStepDistance();
	private static final int MAX_MIN_ENERGY = 50;
	private static final int MIN_ENERGY = 5;
	private static final int MAX_NEAREST_SEARCH_ITERATIONS = 10;

	private static final ExecutorService executor = Executors.newSingleThreadExecutor();
	private static Future<List<WorldPoint>> pathFuture = null;
	private static WorldArea currentDestination = null;
	private static boolean disableTeleports;
	private static boolean disableTransports;

	public static boolean walkTo(WorldPoint destination)
	{
		return walkTo(destination, false);
	}

	public static boolean walkTo(WorldPoint destination, boolean disableTeleports)
	{
		return walkTo(destination, disableTeleports, false);
	}

	public static boolean walkTo(WorldPoint destination, boolean disableTeleports, boolean disableTransports)
	{
		return walkTo(destination.toWorldArea(), disableTeleports, disableTransports);
	}

	public static boolean walkTo(WorldArea destination)
	{
		return walkTo(destination, false);
	}

	public static boolean walkTo(WorldArea destination, boolean disableTeleports)
	{
		return walkTo(destination, disableTeleports, false);
	}

	public static boolean walkTo(WorldArea destination, boolean disableTeleports, boolean disableTransports)
	{
		Walker.disableTeleports = disableTeleports;
		Walker.disableTransports = disableTransports;

		final RS2Player local = RS2Players.getLocal();
		if (destination.contains(local.getWorldLocation()))
		{
			currentDestination = null;
			return true;
		}

		if (Game.isInCutscene() || RS2Widgets.isVisible(RS2Widgets.get(299, 0)))
		{
			Time.sleepTicks(2);
			return false;
		}

		final Map<WorldPoint, List<Transport>> transports = buildTransportLinks();
		final LinkedHashMap<WorldPoint, Teleport> teleports = buildTeleportLinks(destination);
		List<WorldPoint> path = buildPath(destination);

		Static.getEntityRenderer().setCurrentPath(path);
		WalkerPlugin.setCurrentPath(path);

		if (path == null || path.isEmpty())
		{
			log.error(path == null ? "Path is null" : "Path is empty");
			return false;
		}

		final WorldPoint startPosition = path.get(0);
		final Teleport teleport = teleports.get(startPosition);
		final WorldPoint localWP = local.getWorldLocation();
		final boolean offPath = path.stream().noneMatch(t -> t.distanceTo(localWP) <= 5 && canPathTo(localWP, t));

		// Teleport or refresh path if our direction changed
		if (offPath)
		{
			if (teleport != null)
			{
				log.debug("Casting teleport {}", teleport);
				if (RS2Players.getLocal().isIdle())
				{
					teleport.getHandler().run();
					Time.sleepTick();
				}
				Time.sleepUntil(() -> RS2Players.getLocal().distanceTo(teleport.getDestination()) < 10, 500);
				return false;
			}

			path = buildPath(destination, true);
			log.debug("Refreshed path {}", path.size() - 1);
		}

		return walkAlong(path, transports);
	}

	public static boolean walkAlong(List<WorldPoint> path, Map<WorldPoint, List<Transport>> transports)
	{
		final List<WorldPoint> remainingPath = remainingPath(path);

		if (handleTransports(remainingPath, transports))
		{
			return false;
		}

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

		final int targetDistance = Rand.nextInt(MIN_TILES_WALKED_IN_STEP, nextTileIdx);
		return step(reachablePath.get(targetDistance));
	}

	public static List<WorldPoint> reachablePath(List<WorldPoint> remainingPath)
	{
		final RS2Player local = RS2Players.getLocal();
		final List<WorldPoint> out = new ArrayList<>();
		for (WorldPoint p : remainingPath)
		{
			final RS2Tile tile = RS2Tiles.getAt(p);
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
		Movement.walk(destination);

		if (local.getWorldLocation().equals(destination))
		{
			return false;
		}

		if (!Movement.isRunEnabled() && (Movement.getRunEnergy() >= Rand.nextInt(MIN_ENERGY, MAX_MIN_ENERGY) || (local.getHealthScale() > -1 && Movement.getRunEnergy() > 0)))
		{
			Movement.toggleRun();
			Time.sleepUntil(Movement::isRunEnabled, 2000);
			return true;
		}

		if (!Movement.isRunEnabled() && Movement.getRunEnergy() > 0 && Movement.isStaminaBoosted())
		{
			Movement.toggleRun();
			Time.sleepUntil(Movement::isRunEnabled, 2000);
			return true;
		}

		// Handles when stuck on those trees next to draynor manor
		if (!local.isMoving())
		{
			final RS2NPC tree = RS2NPCs.getNearest(n -> n.getId() == NpcID.TREE_4416 && n.getInteracting() == local && n.getWorldLocation().distanceTo2D(local.getWorldLocation()) <= 1);
			if (tree != null)
			{
				final WorldArea area = new RS2WorldPoint(local.getWorldLocation().dx(-1).dy(-1)).createWorldArea(3, 3);
				area.toWorldPointList().stream()
					.filter(wp -> !wp.equals(local.getWorldLocation()) && !wp.equals(tree.getWorldLocation()) && canPathTo(local.getWorldLocation(), wp))
					.unordered()
					.min(Comparator.comparingInt(wp -> wp.distanceTo2D(tree.getWorldLocation())))
					.ifPresent(Movement::walk);
				return false;
			}
		}

		return true;
	}

	public static boolean handleTransports(List<WorldPoint> path, Map<WorldPoint, List<Transport>> transports)
	{
		// Edgeville/ardy wilderness lever warning
		final RS2Widget leverWarningWidget = RS2Widgets.get(229, 1);
		if (RS2Widgets.isVisible(leverWarningWidget))
		{
			log.debug("Handling Wilderness lever warning widget");
			RS2Dialog.continueSpace();
			return true;
		}

		// Wilderness ditch warning
		final RS2Widget wildyDitchWidget = RS2Widgets.get(475, 11);
		if (RS2Widgets.isVisible(wildyDitchWidget))
		{
			log.debug("Handling Wilderness warning widget");
			wildyDitchWidget.interact("Enter Wilderness");
			return true;
		}

		if (RS2Dialog.getOptions().stream()
			.anyMatch(widget -> widget.getText() != null && widget.getText().contains("Eeep! The Wilderness")))
		{
			log.debug("Handling wilderness warning dialog");
			RS2Dialog.chooseOption("Yes, I'm brave");
			return true;
		}

		for (int i = 0; i < MAX_INTERACT_DISTANCE; i++)
		{
			if (i + 1 >= path.size())
			{
				break;
			}

			final WorldPoint a = getTrueWorldPoint(path.get(i));
			final WorldPoint b = getTrueWorldPoint(path.get(i + 1));

			final RS2Tile tileA = RS2Tiles.getAt(path.get(i));
			final RS2Tile tileB = RS2Tiles.getAt(path.get(i + 1));

			if (a.distanceTo(b) > 1
				|| (tileA != null && tileB != null && !Reachable.isWalkable(b)))
			{
				final Transport transport = transports.getOrDefault(a, List.of()).stream()
					.filter(x -> x.getSource().equals(a) && x.getDestination().equals(b))
					.findFirst()
					.orElse(null);

				if (transport != null)
				{
					if (ignoreObstacle(transport.getSource(), 2))
					{
						return true;
					}
					log.debug("Trying to use transport at {} to move {} -> {}", transport.getSource(), a, b);
					transport.getHandler().run();
					Time.sleepTick();
					return true;
				}
			}

			// MLM Rocks
			final RS2TileObject rockfall = RS2TileObjects.getFirstAt(a, "Rockfall");
			final boolean hasPickaxe = RS2Inventory.contains(Predicates.nameContains("pickaxe")) || RS2Equipment.contains(Predicates.nameContains("pickaxe"));
			if (rockfall != null && hasPickaxe)
			{
				log.debug("Handling MLM rockfall");
				if (!RS2Players.getLocal().isIdle())
				{
					return true;
				}
				rockfall.interact("Mine");
				return true;
			}

			if (tileA == null)
			{
				return false;
			}

			// Diagonal door bullshit
			if (Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() + b.getY()) > 1 && a.getPlane() == b.getPlane())
			{
				final RS2TileObject wall = RS2TileObjects.getFirstAt(tileA, it ->
					!(it instanceof WallObject) && it.getName() != null && it.getName().equals("Door")
				);
				if (wall != null && wall.hasAction("Open"))
				{
					if (ignoreObstacle(wall.getWorldLocation(), 1))
					{
						return true;
					}
					log.debug("Handling diagonal door at {}", wall.getWorldLocation());
					wall.interact("Open");
					Time.sleepUntil(() -> !wall.hasAction("Open"), 2000);
					return true;
				}
			}

			if (tileB == null)
			{
				return false;
			}

			// Normal doors
			if (Reachable.isDoored(tileA, tileB))
			{
				final WallObject wall = tileA.getWallObject();
				if (ignoreObstacle(wall.getWorldLocation(), 1))
				{
					return true;
				}
				new RS2WallObject(wall).interact("Open");
				log.debug("Handling door at {}", wall.getWorldLocation());
				Time.sleepUntil(() -> tileA.getWallObject() == null
					|| !new RS2WallObject(wall).hasAction("Open"), 2000);
				return true;
			}

			if (Reachable.isDoored(tileB, tileA))
			{
				final WallObject wall = tileB.getWallObject();
				if (ignoreObstacle(wall.getWorldLocation(), 1))
				{
					return true;
				}
				new RS2WallObject(wall).interact("Open");
				log.debug("Handling door at {}", wall.getWorldLocation());
				Time.sleepUntil(() -> tileB.getWallObject() == null
					|| !new RS2WallObject(wall).hasAction("Open"), 2000);
				return true;
			}
		}

		return false;
	}

	private static boolean ignoreObstacle(WorldPoint point, int distance)
	{
		if (RS2Players.getLocal().isMoving())
		{
			final LocalPoint localDesti = Static.getClient().getLocalDestinationLocation();
			if (localDesti != null)
			{
				final WorldPoint desti = WorldPoint.fromLocal(Static.getClient(), localDesti);
				return desti.distanceTo2D(point) <= distance;
			}
		}
		return false;
	}

	public static WorldPoint nearestWalkableTile(WorldPoint source, Predicate<WorldPoint> filter)
	{
		final CollisionMap cm = Static.getGlobalCollisionMap();

		if (!cm.fullBlock(source) && filter.test(source))
		{
			return source;
		}

		int currentIteration = 1;
		for (int radius = currentIteration; radius < MAX_NEAREST_SEARCH_ITERATIONS; radius++)
		{
			for (int x = -radius; x < radius; x++)
			{
				for (int y = -radius; y < radius; y++)
				{
					final WorldPoint p = source.dx(x).dy(y);
					if (cm.fullBlock(p) || !filter.test(p))
					{
						continue;
					}
					return p;
				}
			}
		}
		log.debug("Could not find a walkable tile near {}", source);
		return null;
	}

	public static WorldPoint nearestWalkableTile(WorldPoint source)
	{
		return nearestWalkableTile(source, x -> true);
	}

	public static List<WorldPoint> remainingPath(List<WorldPoint> path)
	{
		final RS2Player local = RS2Players.getLocal();
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

	public static List<WorldPoint> calculatePath(WorldArea destination)
	{
		final RS2Player local = RS2Players.getLocal();
		final LinkedHashMap<WorldPoint, Teleport> teleports = buildTeleportLinks(destination);
		final List<WorldPoint> startPoints = new ArrayList<>(teleports.keySet());
		startPoints.add(local.getWorldLocation());
		return calculatePath(startPoints, destination);
	}

	public static List<WorldPoint> calculatePath(List<WorldPoint> startPoints, WorldArea destination)
	{
		if (Static.getClient().isClientThread())
		{
			throw new RuntimeException("Calculate path cannot be called on client thread");
		}
		return new Pathfinder(Static.getGlobalCollisionMap(), buildTransportLinks(), startPoints, destination, RegionManager.avoidWilderness()).find();
	}

	public static List<WorldPoint> calculatePath(WorldPoint destination)
	{
		return calculatePath(destination.toWorldArea());
	}

	public static List<WorldPoint> calculatePath(List<WorldPoint> startPoints, WorldPoint destination)
	{
		return calculatePath(startPoints, destination.toWorldArea());
	}

	private static List<WorldPoint> buildPath(
		List<WorldPoint> startPoints,
		WorldArea destination,
		boolean avoidWilderness,
		boolean forced
	)
	{
		if (pathFuture == null)
		{
			pathFuture = executor.submit(new Pathfinder(Static.getGlobalCollisionMap(), buildTransportLinks(), startPoints, destination, avoidWilderness));
			currentDestination = destination;
		}

		final boolean sameDestination = currentDestination != null
			&& destination.getX() == currentDestination.getX()
			&& destination.getY() == currentDestination.getY()
			&& destination.getPlane() == currentDestination.getPlane()
			&& destination.getWidth() == currentDestination.getWidth()
			&& destination.getHeight() == currentDestination.getHeight();
		final boolean shouldRefresh = RegionManager.shouldRefreshPath();

		if (shouldRefresh)
		{
			log.debug("Path should refresh!");
		}

		if (!sameDestination || shouldRefresh || forced)
		{
			log.debug("Cancelling current path");
			pathFuture.cancel(true);
			pathFuture = executor.submit(new Pathfinder(Static.getGlobalCollisionMap(), buildTransportLinks(), startPoints, destination, avoidWilderness));
			currentDestination = destination;
		}

		try
		{
			if (Static.getClient().isClientThread())
			{
				// 16-17ms for 60fps, 6-7ms for 144fps
				return pathFuture.get(10, TimeUnit.MILLISECONDS);
			}
			return pathFuture.get();
		}
		catch (Exception e)
		{
			log.debug("Path is loading");
			return List.of();
		}
	}

	public static List<WorldPoint> buildPath()
	{
		if (currentDestination == null)
		{
			return List.of();
		}
		return buildPath(currentDestination);
	}

	public static List<WorldPoint> buildPath(WorldArea destination, boolean avoidWilderness, boolean forced)
	{
		final RS2Player local = RS2Players.getLocal();
		final LinkedHashMap<WorldPoint, Teleport> teleports = buildTeleportLinks(destination);
		final List<WorldPoint> startPoints = new ArrayList<>(teleports.keySet());
		startPoints.add(local.getWorldLocation());

		return buildPath(startPoints, destination, avoidWilderness, forced);
	}

	public static List<WorldPoint> buildPath(WorldArea destination)
	{
		return buildPath(destination, RegionManager.avoidWilderness(), false);
	}

	public static List<WorldPoint> buildPath(WorldArea destination, boolean forced)
	{
		return buildPath(destination, RegionManager.avoidWilderness(), forced);
	}

	public static List<WorldPoint> buildPath(WorldPoint destination)
	{
		return buildPath(destination.toWorldArea());
	}

	public static List<WorldPoint> buildPath(WorldPoint destination, boolean forced)
	{
		return buildPath(destination.toWorldArea(), forced);
	}

	public static List<WorldPoint> buildPath(WorldPoint destination, boolean avoidWilderness, boolean forced)
	{
		return buildPath(destination.toWorldArea(), avoidWilderness, forced);
	}

	public static List<WorldPoint> buildPath(List<WorldPoint> startPoints, WorldPoint destination, boolean avoidWilderness, boolean forced)
	{
		return buildPath(startPoints, destination.toWorldArea(), avoidWilderness, forced);
	}

	public static Map<WorldPoint, List<Transport>> buildTransportLinks()
	{
		final Map<WorldPoint, List<Transport>> out = new HashMap<>();
		if (!Static.getWalkerConfig().useTransports() || disableTransports)
		{
			return out;
		}

		for (Transport transport : TransportLoader.buildTransports())
		{
			out.computeIfAbsent(transport.getSource(), x -> new ArrayList<>()).add(transport);
		}

		return out;
	}

	public static LinkedHashMap<WorldPoint, Teleport> buildTeleportLinks(WorldArea destination)
	{
		final LinkedHashMap<WorldPoint, Teleport> out = new LinkedHashMap<>();
		if (!Static.getWalkerConfig().useTeleports() || disableTeleports)
		{
			return out;
		}

		final RS2Player local = RS2Players.getLocal();

		for (Teleport teleport : TeleportLoader.buildTeleports())
		{
			if (teleport.getDestination().distanceTo(local.getWorldLocation()) > 50
				&& destination.distanceTo(local.getWorldLocation()) > destination.distanceTo(teleport.getDestination()) + 20)
			{
				out.putIfAbsent(teleport.getDestination(), teleport);
			}
		}

		return out;
	}

	public static Map<WorldPoint, List<Transport>> buildTransportLinksOnPath(List<WorldPoint> path)
	{
		final Map<WorldPoint, List<Transport>> out = new HashMap<>();
		for (Transport transport : TransportLoader.buildTransports())
		{
			final WorldPoint destination = transport.getDestination();
			if (path.contains(destination))
			{
				out.computeIfAbsent(transport.getSource(), x -> new ArrayList<>()).add(transport);
			}
		}
		return out;
	}

	public static LinkedHashMap<WorldPoint, Teleport> buildTeleportLinksOnPath(List<WorldPoint> path)
	{
		final LinkedHashMap<WorldPoint, Teleport> out = new LinkedHashMap<>();
		for (Teleport teleport : TeleportLoader.buildTeleports())
		{
			final WorldPoint destination = teleport.getDestination();
			if (path.contains(destination))
			{
				out.putIfAbsent(destination, teleport);
			}
		}
		return out;
	}

	public static boolean canPathTo(WorldPoint start, WorldPoint destination)
	{
		final List<WorldPoint> pathTo = new RS2WorldPoint(start).pathTo(Static.getClient(), destination);
		return pathTo != null && pathTo.contains(destination);
	}

	private static WorldPoint getTrueWorldPoint(WorldPoint point)
	{
		try
		{
			final LocalPoint localPoint = LocalPoint.fromWorld(Static.getClient(), point);
			if (localPoint == null)
			{
				return point;
			}
			return WorldPoint.fromLocalInstance(
				Static.getClient(),
				localPoint
			);
		}
		catch (Exception e)
		{
			log.warn("Failed to get true world point for {}", point, e);
		}
		return point;
	}
}