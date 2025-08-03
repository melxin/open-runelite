package net.runelite.client.plugins.openrl.plugins.mining;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.commons.Time;
import net.runelite.client.plugins.openrl.api.events.Draw;
import net.runelite.client.plugins.openrl.api.input.Keyboard;
import net.runelite.client.plugins.openrl.api.input.utils.Randomizer;
import net.runelite.client.plugins.openrl.api.plugin.LoopedPlugin;
import net.runelite.client.plugins.openrl.api.rs2.providers.entities.RS2Players;
import net.runelite.client.plugins.openrl.api.rs2.providers.entities.RS2TileObjects;
import net.runelite.client.plugins.openrl.api.rs2.providers.items.RS2Inventory;
import net.runelite.client.plugins.openrl.api.rs2.providers.scene.RS2Tiles;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Item;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Player;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Tile;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2TileObject;

@PluginDescriptor(
	name = "MMiningGuildIronOre",
	description = "Mining guild iron ores",
	tags = {"mining", "guild", "iron", "ores"}
)
@Slf4j
public class MMiningGuildPlugin extends LoopedPlugin
{
	@Override
	protected void startUp()
	{
	}

	@Inject
	protected MMiningGuildConfig config;

	@Provides
	private MMiningGuildConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MMiningGuildConfig.class);
	}

	protected Instant scriptStartTime;

	protected String getTimeRunning()
	{
		return scriptStartTime != null ? this.getTimeBetween(scriptStartTime, Instant.now()) : "";
	}

	private RS2Tile startTile;
	@Subscribe
	protected void onConfigButtonClicked(ConfigButtonClicked event)
	{
		if (!event.getGroup().equals("MMiningGuild") || !event.getKey().equals("start"))
		{
			return;
		}

		if (scriptStartTime != null)
		{
			reset();
		}
		else
		{
			this.scriptStartTime = Instant.now();
		}
	}

	protected void reset()
	{
		this.scriptStartTime = null;
		this.startTile = null;
	}

	@Override
	protected int loop()
	{
		if (scriptStartTime == null || Static.getClient().getGameState() != GameState.LOGGED_IN)
		{
			return -1;
		}

		final RS2Player local = RS2Players.getLocal();
		if (local == null)
		{
			return -1;
		}

		if (startTile == null)
		{
			this.startTile = RS2Tiles.getAt(local.getWorldLocation());
		}

		if (startTile != null && !local.getWorldLocation().equals(startTile.getWorldLocation()))
		{
			startTile.walkHere();
			return -5;
		}

		log.info("Iron ore count: {} contains? {}", RS2Inventory.getCount("Iron ore"), RS2Inventory.contains("Iron ore"));
		if (RS2Inventory.isFull() || RS2Inventory.getCount(i -> i.getId() == ItemID.IRON_ORE) >= 20 && !isMining())
		{
			log.info("Should drop | Inv full? {} | Free slot count {}", RS2Inventory.isFull(), RS2Inventory.getFreeSlots());
			this.dropShiftClick();
			//this.dropFastInvoke();
			return -1;
		}

		final List<RS2TileObject> rocks = RS2TileObjects.getAll(t -> t.getId() == ObjectID.IRONROCK1 || t.getId() == ObjectID.IRONROCK2);
		for (RS2TileObject rock : rocks)
		{
			if (rock.getWorldLocation().distanceTo2D(local.getWorldLocation()) > 1)
			{
				continue;
			}

			log.info("Rock: {}", rock.getId());

			if (!isMining())
			{
				Time.sleep(60, 200);
				log.info("MINE!");
				rock.interact("Mine");
				Time.sleepTicks(2);
			}
		}

		return -5;
	}

	private boolean isMining()
	{
		final RS2Player player = RS2Players.getLocal();
		return player.isInteracting() || player.getAnimation() == 624 || player.getAnimation() == 7139;
	}

	private void dropShiftClick()
	{
		final List<RS2Item> items = RS2Inventory.getAll(i -> i.getId() == ItemID.IRON_ORE
			|| i.getId() == ItemID.UNCUT_RUBY
			|| i.getId() == ItemID.UNCUT_SAPPHIRE
			|| i.getId() == ItemID.UNCUT_EMERALD
			|| i.getId() == ItemID.UNCUT_DIAMOND
			|| i.getId() == ItemID.UNCUT_OPAL);

		Collections.shuffle(items);

		Keyboard.pressed(KeyEvent.VK_SHIFT);
		Time.sleep(60, 200);
		for (RS2Item item : items)
		{
			log.info("Name: {}", item.getName());
			item.interact(0);
			Time.sleep(45, 270);
		}
		Keyboard.released(KeyEvent.VK_SHIFT);
	}

	private void dropFastInvoke()
	{
		final List<RS2Item> items = RS2Inventory.getAll(i -> i.getId() == ItemID.IRON_ORE
			|| i.getId() == ItemID.UNCUT_RUBY
			|| i.getId() == ItemID.UNCUT_SAPPHIRE
			|| i.getId() == ItemID.UNCUT_EMERALD
			|| i.getId() == ItemID.UNCUT_DIAMOND
			|| i.getId() == ItemID.UNCUT_OPAL);

		Static.getClientThread().invoke(() ->
		{
			for (RS2Item item : items)
			{
				final int slot = item.getSlot();
				final WidgetItem widgetItem = item.getWidgetItem();
				final Widget widget = widgetItem.getWidget();
				final int param0 = slot;
				final int param1 = widget.getId();
				final MenuAction menuAction = item.getMenuAction("Drop");
				final String[] actions = widget.getActions();
				final int actionIndex = Arrays.asList(actions).indexOf("Drop") + 1;
				final int itemId = widget.getItemId();
				final int worldViewId = -1;
				final String option = "Drop";
				final String target =  "<col=ff9040>" + item.getName() + "</col>";
				final Point clickPoint = Randomizer.getRandomPointIn(widget.getBounds());
				final int x = clickPoint.getX();
				final int y = clickPoint.getY();
				Static.invokeMenuAction(param0, param1, menuAction, actionIndex, itemId, worldViewId, option, target, x, y);
			}
		});
	}

	/*public void dropAllItems(int itemId)
	{
		// Open inventory tab
		Widget inventoryWidget = Static.getClient().getWidget(WidgetInfo.INVENTORY);
		if (inventoryWidget == null || inventoryWidget.isHidden())
		{
			Static.invokeMenuAction("Inventory", "", 1, MenuAction.CC_OP.getId(), -1, 10551357);
		}

		Static.getClientThread().invoke(() ->
		{
			final ItemContainer itemContainer = Static.getClient().getItemContainer(InventoryID.INV);

			if (itemContainer == null)
			{
				return;
			}

			final Item[] items = itemContainer.getItems();

			for (int i = 0; i < items.length; i++)
			{
				Item item = items[i];
				if (item != null && item.getId() == itemId)
				{
					ItemComposition itemComposition = Static.getClient().getItemDefinition(itemId);
					if (itemComposition != null)
					{
						Static.invokeMenuAction("Drop", "<col=ff9040>" + itemComposition.getName() + "</col>", 7, MenuAction.CC_OP_LOW_PRIORITY.getId(), i, 9764864, item.getId(), -1, -1);
					}
				}
			}
		});
	}*/

	@Subscribe
	protected void onDraw(Draw event)
	{
		final Graphics graphics = event.getGraphics();

		if (scriptStartTime == null || graphics == null)
		{
			return;
		}

		if (config.overlayEnabled())
		{
			graphics.setColor(config.overlayColor());
			graphics.drawString("Time running: " + this.getTimeRunning(), 10, 20);
			if (startTile != null)
			{
				outline(Static.getClient(), startTile.getWorldLocation(), (Graphics2D) graphics, Color.ORANGE);
			}
			//graphics.drawString("State: " + this.currentState, 10, 35);
			//graphics.drawString("XP gained: " + this.totalExpGained, 10, 50);
			//graphics.drawString("Logs gained: " + this.logsGained, 10, 65);
		}
	}

	/**
	 * Get time as string between two instants
	 *
	 * @param start
	 * @param finish
	 * @return
	 */
	protected String getTimeBetween(Instant start, Instant finish)
	{
		long timeElapsed = Duration.between(start, finish).getSeconds();
		int days = (int) TimeUnit.SECONDS.toDays(timeElapsed);
		long hours = TimeUnit.SECONDS.toHours(timeElapsed) - (days * 24);
		long minutes = TimeUnit.SECONDS.toMinutes(timeElapsed) - (TimeUnit.SECONDS.toHours(timeElapsed) * 60);
		long seconds = TimeUnit.SECONDS.toSeconds(timeElapsed) - (TimeUnit.SECONDS.toMinutes(timeElapsed) * 60);

		String timeAsString = new StringBuilder()
			.append(days)
			.append("\r Days \r")
			.append(hours)
			.append("\r Hours \r")
			.append(minutes)
			.append("\r Minutes \r")
			.append(seconds)
			.append("\r Seconds \r")
			.toString();

		return timeAsString;
	}

	public void outline(Client client, WorldPoint worldPoint, Graphics2D graphics2D, Color color)
	{
		outline(client, worldPoint, graphics2D, color, null);
	}

	public void outline(Client client, WorldPoint worldPoint, Graphics2D graphics, Color color, String text)
	{
		LocalPoint localPoint = LocalPoint.fromWorld(client.getTopLevelWorldView(), worldPoint);
		if (localPoint == null)
		{
			return;
		}

		Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
		if (poly == null)
		{
			return;
		}

		if (text != null)
		{
			var stringX = (int) (poly.getBounds().getCenterX() -
				graphics.getFont().getStringBounds(text, graphics.getFontRenderContext()).getWidth() / 2);
			var stringY = (int) poly.getBounds().getCenterY();
			graphics.setColor(color);
			graphics.drawString(text, stringX, stringY);
		}

		graphics.setColor(color);
		final Stroke originalStroke = graphics.getStroke();
		graphics.setStroke(new BasicStroke(2));
		graphics.draw(poly);
		graphics.setColor(new Color(0, 0, 0, 50));
		graphics.fill(poly);
		graphics.setStroke(originalStroke);
	}
}
