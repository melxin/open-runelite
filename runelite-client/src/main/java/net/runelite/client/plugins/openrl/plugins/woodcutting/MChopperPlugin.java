package net.runelite.client.plugins.openrl.plugins.woodcutting;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.StatChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.commons.Rand;
import net.runelite.client.plugins.openrl.api.commons.Time;
import net.runelite.client.plugins.openrl.api.events.Draw;
import net.runelite.client.plugins.openrl.api.input.Keyboard;
import net.runelite.client.plugins.openrl.api.plugin.LoopedPlugin;
import net.runelite.client.plugins.openrl.api.rs2.providers.entities.RS2Players;
import net.runelite.client.plugins.openrl.api.rs2.providers.entities.RS2TileItems;
import net.runelite.client.plugins.openrl.api.rs2.providers.entities.RS2TileObjects;
import net.runelite.client.plugins.openrl.api.rs2.providers.items.RS2Inventory;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Item;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Player;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2TileItem;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2TileObject;

@PluginDescriptor(
	name = "MChopperPlugin",
	description = "Chop any tree",
	tags = {"woodcutting", "tree", "chopper"}
)
@Slf4j
public class MChopperPlugin extends LoopedPlugin
{
	@Override
	protected void startUp()
	{
	}

	@Inject
	protected MChopperConfig config;

	@Provides
	private MChopperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MChopperConfig.class);
	}

	protected Instant scriptStartTime;

	protected String getTimeRunning()
	{
		return scriptStartTime != null ? this.getTimeBetween(scriptStartTime, Instant.now()) : "";
	}

	protected State currentState;

	protected int totalExpGained;
	protected int logsGained;

	@Subscribe
	protected void onConfigButtonClicked(ConfigButtonClicked event)
	{
		if (!event.getGroup().equals("MChopper") || !event.getKey().equals("start"))
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
			this.currentState = State.INITIALIZING;
		}
	}

	protected void reset()
	{
		this.scriptStartTime = null;
		this.currentState = null;
		this.totalExpGained = 0;
		this.logsGained = 0;
	}

	protected enum State
	{
		INITIALIZING,
		INTERACT,
		CUTTING,
		DROPPING,
		IDLE,
		DESTINATION_LEVEL_REACHED
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

		if (config.destinationLevel() > 0 && Static.getClient().getBoostedSkillLevel(Skill.WOODCUTTING) >= config.destinationLevel())
		{
			log.info("Destination level reached!");
			this.currentState = State.DESTINATION_LEVEL_REACHED;
			return -5;
		}

		if (RS2Inventory.isFull() || RS2Inventory.getCount(i -> i.getName().toLowerCase().contains("logs")) >= 20 && !isChopping())
		{
			log.info("Should drop | Inv full? {} | Free slot count {}", RS2Inventory.isFull(), RS2Inventory.getFreeSlots());
			this.currentState = State.DROPPING;
			this.dropShiftClick();
			//this.dropFastInvoke();
			return -1;
		}

		final RS2TileItem nest = RS2TileItems.getNearest(x -> x.getName().contains("nest"));
		if (nest != null)
		{
			nest.interact("Take");
			Time.sleep(1000, 3000);
		}

		final RS2TileObject tree = RS2TileObjects.getNearest(config.treeName());

		if (tree != null)
		{
			log.info("Tree: {} | {}", tree.getId(), tree.getName());

			if (!isChopping())
			{
				log.info("CHOP!");
				this.currentState = State.INTERACT;
				Time.sleep(80, 300);
				tree.interact(0);
				Time.sleepUntil(this::isChopping, 3000);
			}
		}

		if (isChopping())
		{
			this.currentState = State.CUTTING;
			return -1;
		}

		this.currentState = State.IDLE;
		return Rand.nextInt(100, 1000);
	}

	protected boolean isChopping()
	{
		final RS2Player player = RS2Players.getLocal();
		return player.isInteracting() || player.getAnimation() > -1;
	}

	protected void dropShiftClick()
	{
		final List<RS2Item> items = RS2Inventory.getAll(x -> x.getName().equals("Logs") || x.getName().endsWith("logs"));

		Collections.shuffle(items);

		Keyboard.pressed(KeyEvent.VK_SHIFT);
		Time.sleep(60, 200);
		for (RS2Item item : items)
		{
			log.info("Name: {}", item.getName());
			item.interact(0);
			Time.sleep(45, 150);
		}
		Keyboard.released(KeyEvent.VK_SHIFT);
	}

	@Subscribe
	protected void onStatChanged(StatChanged statChanged)
	{
		if (scriptStartTime != null && statChanged.getSkill() == Skill.WOODCUTTING)
		{
			this.totalExpGained = + statChanged.getXp();
			this.logsGained++;
		}
	}

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
			graphics.drawString("State: " + this.currentState, 10, 35);
			graphics.drawString("XP gained: " + this.totalExpGained, 10, 50);
			graphics.drawString("Logs gained: " + this.logsGained, 10, 65);
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

	/*@Subscribe
	protected void onFocusChanged(FocusChanged event)
	{
		log.info("Focus changed: {}", event.isFocused() ? "Gained" : "Lost");
	}*/
}