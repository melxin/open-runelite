package net.runelite.client.plugins.openrl.plugins.devtools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.openrl.api.events.Draw;
import net.runelite.client.plugins.openrl.api.events.MenuAutomated;
import net.runelite.client.plugins.openrl.api.game.MessageUtils;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.TransportLoader;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.dto.TransportDto;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.requirement.Requirements;
import net.runelite.client.plugins.openrl.api.rs2.providers.entities.RS2TileObjects;
import net.runelite.client.plugins.openrl.api.rs2.providers.scene.RS2Tiles;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Tile;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2TileObject;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Open RuneLite Dev Tools",
	description = "Shows entity information",
	enabledByDefault = false
)
@Slf4j
public class OpenRuneLiteDevToolsPlugin extends Plugin
{
	@Inject
	private OpenRuneLiteDevToolsConfig config;

	@Inject
	private OpenRuneLiteDevToolsOverlay overlay;

	@Inject
	private RegionOverlay regionOverlay;

	@Inject
	private InteractionOverlay interactionOverlay;

	@Inject
	private MemoryUsageOverlay memoryUsageOverlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private EventBus eventBus;

	@Inject
	private Client client;

	//@Inject
	//private InputManager inputManager;

	private WorldPoint sourceTile;
	private Pair<String, Integer> transportObject;
	private boolean selectingSource = true;
	private boolean selectingDestination;
	private boolean selectingObject;
	private ArrayList<TransportDto> tempTransports;

	@Override
	public void startUp()
	{
		overlayManager.add(overlay);
		overlayManager.add(regionOverlay);
		overlayManager.add(interactionOverlay);
		overlayManager.add(memoryUsageOverlay);

		eventBus.register(regionOverlay);
	}

	@Override
	public void shutDown()
	{
		overlayManager.remove(overlay);
		overlayManager.remove(regionOverlay);
		overlayManager.remove(interactionOverlay);
		overlayManager.remove(memoryUsageOverlay);

		eventBus.unregister(regionOverlay);
	}

	@Provides
	public OpenRuneLiteDevToolsConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(OpenRuneLiteDevToolsConfig.class);
	}

	@Subscribe
	public void onMenuAutomated(MenuAutomated e)
	{
		if (config.debugMenuActions())
		{
			String debug = "O=" + e.getOption()
				+ " | T=" + e.getTarget()
				+ " | ID=" + e.getIndex()
				+ " | OP=" + e.getMenuAction().getId()
				+ " | P0=" + e.getParam0()
				+ " | P1=" + e.getParam1()
				+ " | X=" + e.getCanvasX()
				+ " | Y=" + e.getCanvasY();

			log.info("[Automated] {}", debug);
		}
	}

	@Subscribe
	public void onMenuOpened(MenuOpened e)
	{
		if (config.transportCreator())
		{
			createTransportMenuOptions(e);
		}
	}

	private void createTransportMenuOptions(MenuOpened e)
	{
		boolean validTile = false;
		final Map<String, RS2TileObject> objectOptions = new HashMap<>(3);
		for (MenuEntry entry : e.getMenuEntries())
		{
			if (entry.getType().equals(MenuAction.WALK))
			{
				validTile = true;
			}
			if (selectingObject)
			{
				if (entry.getOption().equalsIgnoreCase("examine"))
				{
					continue;
				}
				final String target = entry.getTarget().replaceFirst("<.*>", "");
				final RS2TileObject obj = RS2TileObjects.getNearest(RS2Tiles.getHoveredTile().getWorldLocation(), target);
				if (obj != null)
				{
					objectOptions.put(entry.getOption(), obj);
				}
			}
		}
		if (selectingSource & validTile)
		{
			client.createMenuEntry(1)
				.setOption("<col=00ff00>Transport Creator:</col>")
				.setTarget("Select source")
				.setType(MenuAction.RUNELITE)
				.onClick(x ->
				{
					RS2Tile clickPoint = RS2Tiles.getHoveredTile();
					if (clickPoint == null)
					{
						return;
					}
					sourceTile = WorldPoint.fromLocalInstance(client, clickPoint.getLocalLocation());
					selectingSource = false;
					selectingObject = true;
				});
		}
		if (selectingObject)
		{
			objectOptions.forEach((action, object) ->
				client.createMenuEntry(1)
					.setOption("<col=00ff00>Transport Creator:</col>")
					.setTarget("Select " + action + " " + object.getName())
					.setType(MenuAction.RUNELITE)
					.onClick(x ->
					{
						transportObject = Pair.of(action, object.getId());
						selectingObject = false;
						selectingDestination = true;
					}));
		}
		if (selectingDestination & validTile)
		{
			client.createMenuEntry(1)
				.setOption("<col=00ff00>Transport Creator:</col>")
				.setTarget("Select destination")
				.setType(MenuAction.RUNELITE)
				.onClick(x ->
				{
					RS2Tile clickPoint = RS2Tiles.getHoveredTile();
					if (clickPoint == null)
					{
						return;
					}
					final TransportDto transport = new TransportDto(
						sourceTile,
						WorldPoint.fromLocalInstance(client, clickPoint.getLocalLocation()),
						transportObject.getKey(),
						transportObject.getValue(),
						new Requirements()
					);
					if (tempTransports == null)
					{
						tempTransports = new ArrayList<>();
					}
					tempTransports.add(transport);
					if (config.useCreatedTransports())
					{
						TransportLoader.updateTempTransports(tempTransports.stream().map(TransportDto::toTransport).collect(Collectors.toList()));
					}
					selectingDestination = false;
					selectingSource = true;
					sourceTile = null;
					transportObject = null;
				});
		}
		if (sourceTile != null || transportObject != null)
		{
			client.createMenuEntry(1)
				.setOption("<col=00ff00>Transport Creator:</col>")
				.setTarget("<col=ff0000>Reset current</col>")
				.setType(MenuAction.RUNELITE)
				.onClick(x ->
				{
					sourceTile = null;
					transportObject = null;
					selectingSource = true;
					selectingDestination = false;
					selectingObject = false;
				});
		}
		if (tempTransports != null && !tempTransports.isEmpty())
		{
			client.createMenuEntry(1)
				.setOption("<col=00ff00>Transport Creator:</col>")
				.setTarget("Export")
				.setType(MenuAction.RUNELITE)
				.onClick(x -> exportTransports());
			client.createMenuEntry(1)
				.setOption("<col=00ff00>Transport Creator:</col>")
				.setTarget("<col=ff0000>Clear list</col>")
				.setType(MenuAction.RUNELITE)
				.onClick(x ->
				{
					tempTransports = null;
					TransportLoader.clearTempTransports();
				});
		}
	}

	private void exportTransports()
	{
		final Gson gson = new GsonBuilder().setPrettyPrinting().create();
		final StringBuilder output = new StringBuilder();
		for (TransportDto transportDto : tempTransports)
		{
			final JsonObject transportObj = gson.toJsonTree(transportDto).getAsJsonObject();
			transportObj.add("requirements", new JsonObject());
			final String transport = gson.toJson(transportObj);
			output.append(transport).append(",\n");
		}
		final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		final StringSelection stringSelection = new StringSelection(output.toString());
		clipboard.setContents(stringSelection, null);
		MessageUtils.addMessage("Transports copied to clipboard!");
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked e)
	{
		if (config.debugMenuActions())
		{
			String action = "O=" + e.getMenuOption()
				+ " | T=" + e.getMenuTarget()
				+ " | ID=" + e.getId()
				+ " | OP=" + e.getMenuAction().getId()
				+ " | P0=" + e.getParam0()
				+ " | P1=" + e.getParam1()
				+ " | ITEM=" + e.getItemId();
			log.info("[Menu Action] {}", action);
		}
	}

	/*@Subscribe
	public void onDialogProcessed(DialogProcessed e)
	{
		if (!config.debugDialogs())
		{
			return;
		}

		DialogOption dialogOption = DialogOption.of(e.getDialogOption().getWidgetUid(), e.getDialogOption().getMenuIndex());
		if (dialogOption != null)
		{
			log.info("Dialog processed {}", dialogOption);
		}
		else
		{
			log.warn("Unknown or unmapped dialog {}", e);
		}
	}*/

	@Subscribe
	public void onConfigChanged(ConfigChanged e)
	{
		if (!e.getGroup().equals("openrldevtools"))
		{
			return;
		}

		/*if ("staffLevel".equals(e.getKey()))
		{
			client.setStaffModLevel(Integer.parseInt(e.getNewValue()));
		}*/

		if ("useCreatedTransports".equals(e.getKey()))
		{
			if (config.useCreatedTransports())
			{
				TransportLoader.updateTempTransports(tempTransports.stream().map(TransportDto::toTransport).collect(Collectors.toList()));
			}
			else
			{
				TransportLoader.clearTempTransports();
			}
		}
	}

	@Subscribe
	public void onDraw(Draw event)
	{
	}

	@Subscribe
	private void onConfigButtonClicked(ConfigButtonClicked configButtonClicked)
	{
		if (!configButtonClicked.getGroup().equalsIgnoreCase("openrldevtools"))
		{
			return;
		}

		if (configButtonClicked.getKey().equals("printStackTrace"))
		{
			for (Map.Entry<Thread, StackTraceElement[]> entry :
				Thread.getAllStackTraces().entrySet())
			{
				System.out.println(entry.getKey().getName() + ":");
				for (StackTraceElement element : entry.getValue())
					System.out.println("\t" + element);
			}
		}
	}

	@Subscribe
	private void onWidgetLoaded(WidgetLoaded widgetLoaded)
	{
		if (widgetLoaded.getGroupId() == InterfaceID.WORLDMAP)
		{
			overlayManager.remove(regionOverlay);
			regionOverlay.swapLayer();
			overlayManager.add(regionOverlay);
		}
	}

	@Subscribe
	private void onWidgetClosed(WidgetClosed widgetClosed)
	{
		if (widgetClosed.getGroupId() == InterfaceID.WORLDMAP)
		{
			overlayManager.remove(regionOverlay);
			regionOverlay.swapLayer();
			overlayManager.add(regionOverlay);
		}
	}
}