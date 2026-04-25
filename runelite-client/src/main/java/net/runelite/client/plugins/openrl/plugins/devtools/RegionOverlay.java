package net.runelite.client.plugins.openrl.plugins.devtools;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.GlobalCollisionMap;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.TransportLoader;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.Walker;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.Teleport;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.Transport;
import net.runelite.client.plugins.openrl.api.rs2.providers.scene.RS2Tiles;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.RS2Widgets;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Tile;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Widget;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2WorldPoint;
import net.runelite.client.plugins.openrl.plugins.devtools.utils.CoordUtils;
import net.runelite.client.plugins.openrl.plugins.devtools.utils.DrawUtils;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import static net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.Walker.buildTeleportLinks;

@Singleton
@Slf4j
public class RegionOverlay extends Overlay
{
	private static final Color RED_TRANSLUCENT = new Color(255, 0, 0, 128);

	private final OpenRuneLiteDevToolsConfig config;
	private final GlobalCollisionMap collisionMap;
	private final Client client;
	private final ExecutorService executorService;
	private WorldPoint startTile;

	private List<WorldPoint> path = new ArrayList<>();

	@Inject
	public RegionOverlay(OpenRuneLiteDevToolsConfig config, GlobalCollisionMap collisionMap, Client client)
	{
		this.config = config;
		this.collisionMap = collisionMap;
		this.client = client;
		this.executorService = getExecutorService();

		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(Overlay.PRIORITY_LOW);
	}

	public ExecutorService getExecutorService()
	{
		final int poolSize = 2 * Runtime.getRuntime().availableProcessors();

		// Will start up to poolSize threads (because of allowCoreThreadTimeOut) as necessary, and times out
		// unused threads after 1 minute
		final ThreadPoolExecutor executor = new ThreadPoolExecutor(poolSize, poolSize,
			60L, TimeUnit.SECONDS,
			new LinkedBlockingQueue<>(),
			new ThreadFactoryBuilder().setNameFormat("worker-%d").build());
		executor.allowCoreThreadTimeOut(true);

		return new NonScheduledExecutorServiceExceptionLogger(executor);
	}

	public void swapLayer()
	{
		if (getLayer() == OverlayLayer.ABOVE_SCENE)
		{
			setLayer(OverlayLayer.ABOVE_WIDGETS);
		}
		else
		{
			setLayer(OverlayLayer.ABOVE_SCENE);
		}
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		final RS2Widget worldMap = RS2Widgets.getWidget(InterfaceID.Worldmap.MAP_CONTAINER);
		if (worldMap != null)
		{
			final Rectangle mapBounds = worldMap.getBounds();
			graphics.setClip(mapBounds);
			if (config.transportsOverlay())
			{
				final List<Transport> transports = TransportLoader.buildTransports();
				for (Transport transport : transports)
				{
					DrawUtils.drawOnMap(graphics, transport.getDestination(), Color.magenta);
					Point center = CoordUtils.worldPointToWorldMap(transport.getSource());
					if (center == null)
					{
						continue;
					}

					Point linkCenter = CoordUtils.worldPointToWorldMap(transport.getDestination());
					if (linkCenter == null)
					{
						continue;
					}

					graphics.drawLine(center.getX(), center.getY(), linkCenter.getX(), linkCenter.getY());
				}
			}

			if (config.collisionOverlay())
			{
				final Collection<WorldPoint> worldMapTiles = RS2Tiles.getWorldMapTiles(Static.getClient().getPlane());
				for (WorldPoint worldMapTile : worldMapTiles)
				{
					if (worldMapTile != null && collisionMap.fullBlock(worldMapTile))
					{
						DrawUtils.drawOnMap(graphics, worldMapTile, RED_TRANSLUCENT);
					}
				}
			}

			if (config.pathOverlay() && !path.isEmpty())
			{
				for (WorldPoint tile : path)
				{
					DrawUtils.drawOnMap(graphics, tile, Color.RED);
				}

				DrawUtils.drawOnMap(graphics, path.get(path.size() - 1), Color.GREEN);
			}

			return null;
		}

		if (config.transportsOverlay())
		{
			DrawUtils.drawTransports(graphics);
		}

		if (config.collisionOverlay())
		{
			DrawUtils.drawCollisions(graphics);
		}

		if (config.pathOverlay() && !path.isEmpty())
		{
			for (WorldPoint tile : path)
			{
				new RS2WorldPoint(tile).outline(client, graphics, Color.RED);
			}

			new RS2WorldPoint(path.get(path.size() - 1)).outline(client, graphics, Color.GREEN, "Destination");
		}

		return null;
	}

	@Subscribe
	public void onMenuOpened(MenuOpened event)
	{
		if (!config.pathOverlay())
		{
			return;
		}
		Point mouse = client.getMouseCanvasPosition();

		final RS2Widget worldMap = RS2Widgets.getWidget(InterfaceID.Worldmap.MAP_CONTAINER);
		if (worldMap == null)
		{
			if (!event.getFirstEntry().getOption().equals("Walk here"))
			{
				return;
			}
			RS2Tile clickPoint = RS2Tiles.getHoveredTile();
			if (clickPoint == null)
			{
				return;
			}
			generateMenu(clickPoint.getWorldLocation());
		}
		else
		{
			if (!worldMap.getBounds().contains(mouse.getX(), mouse.getY()))
			{
				return;
			}
			WorldPoint clickPoint = CoordUtils.worldMapToWorldPoint(mouse);
			if (clickPoint == null)
			{
				return;
			}
			generateMenu(clickPoint);
		}
	}

	@Subscribe
	public void onConfigButtonClicked(ConfigButtonClicked e)
	{
		if (!e.getGroup().equals("unethicaldevtools") && !e.getKey().equals("pathDebugButton"))
		{
			return;
		}
		WorldPoint location = getConfigLocation();
		if (location == null)
		{
			return;
		}
		if (startTile == null)
		{
			executorService.execute(() -> path = Walker.calculatePath(location));
		}
		else
		{
			LinkedHashMap<WorldPoint, Teleport> teleports = buildTeleportLinks(location.toWorldArea());
			List<WorldPoint> startPoints = new ArrayList<>(teleports.keySet());
			startPoints.add(startTile);
			executorService.execute(() -> path = Walker.calculatePath(
				startPoints, location));
		}
	}

	private WorldPoint getConfigLocation()
	{
		String[] split = config.pathDebugLocation().split(" ");
		if (split.length != 3)
		{
			return null;
		}
		return new WorldPoint(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
	}

	private void generateMenu(WorldPoint clickPoint)
	{
		client.createMenuEntry(1)
			.setOption("<col=00ff00>Debug:</col>")
			.setTarget("Calculate path")
			.setType(MenuAction.RUNELITE_OVERLAY)
			.onClick(e ->
			{
				if (startTile == null)
				{
					executorService.execute(() -> path = Walker.calculatePath(clickPoint));
				}
				else
				{
					LinkedHashMap<WorldPoint, Teleport> teleports = buildTeleportLinks(clickPoint.toWorldArea());
					List<WorldPoint> startPoints = new ArrayList<>(teleports.keySet());
					startPoints.add(startTile);
					executorService.execute(() -> path = Walker.calculatePath(
						startPoints, clickPoint));
				}
			});
		if (!path.isEmpty())
		{
			client.createMenuEntry(1)
				.setOption("<col=00ff00>Debug:</col>")
				.setTarget("Set start")
				.setType(MenuAction.RUNELITE_OVERLAY)
				.onClick(e ->
					{
						startTile = clickPoint;
						LinkedHashMap<WorldPoint, Teleport> teleports = buildTeleportLinks(clickPoint.toWorldArea());
						List<WorldPoint> startPoints = new ArrayList<>(teleports.keySet());
						startPoints.add(startTile);
						executorService.execute(() -> path = Walker.calculatePath(
							startPoints, path.get(path.size() - 1)));
					}
				);
			client.createMenuEntry(1)
				.setOption("<col=00ff00>Debug:</col>")
				.setTarget("Clear path")
				.setType(MenuAction.RUNELITE_OVERLAY)
				.onClick(e ->
					{
						executorService.execute(() -> path.clear());
						startTile = null;
					}
				);
		}
	}
}