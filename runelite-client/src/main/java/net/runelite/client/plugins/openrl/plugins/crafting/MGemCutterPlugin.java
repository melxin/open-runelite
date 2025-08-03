package net.runelite.client.plugins.openrl.plugins.crafting;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import java.awt.Graphics;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.commons.Time;
import net.runelite.client.plugins.openrl.api.events.Draw;
import net.runelite.client.plugins.openrl.api.plugin.LoopedPlugin;
import net.runelite.client.plugins.openrl.api.rs2.providers.entities.RS2NPCs;
import net.runelite.client.plugins.openrl.api.rs2.providers.entities.RS2Players;
import net.runelite.client.plugins.openrl.api.rs2.providers.items.RS2Bank;
import net.runelite.client.plugins.openrl.api.rs2.providers.items.RS2Inventory;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.dialog.RS2Dialog;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Item;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2NPC;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Player;

@PluginDescriptor(
	name = "MGemCutterPlugin",
	description = "Cut any uncut stone",
	tags = {"crafting", "uncut", "cut", "chisel"}
)
@Slf4j
public class MGemCutterPlugin extends LoopedPlugin
{
	@Override
	protected void startUp()
	{
	}

	@Inject
	protected MGemCutterConfig config;

	@Provides
	private MGemCutterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MGemCutterConfig.class);
	}

	protected Instant scriptStartTime;

	protected String getTimeRunning()
	{
		return scriptStartTime != null ? this.getTimeBetween(scriptStartTime, Instant.now()) : "";
	}

	protected State currentState;

	@Subscribe
	protected void onConfigButtonClicked(ConfigButtonClicked event)
	{
		if (!event.getGroup().equals("MGemCutter") || !event.getKey().equals("start"))
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
	}

	protected enum State
	{
		INITIALIZING,
		BANK,
		CUT,
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

		if (config.destinationLevel() > 0 && Static.getClient().getBoostedSkillLevel(Skill.CRAFTING) >= config.destinationLevel())
		{
			log.info("Destination level reached!");
			this.currentState = State.DESTINATION_LEVEL_REACHED;
			return -5;
		}

		final List<RS2Item> allUncutStones = RS2Inventory.getAll(config.uncutName());
		Collections.shuffle(allUncutStones);
		if (RS2Inventory.getCount(config.uncutName()) > 0 && !RS2Dialog.isOpen() && !isCutting())
		{
			RS2Inventory.getFirst("Chisel").useOn(allUncutStones.get(0));
			Time.sleepUntil(() -> RS2Dialog.isOpen(), 2000);
			Time.sleep(95, 350);
		}

		if (RS2Dialog.isOpen())
		{
			RS2Dialog.continueSpace();
		}

		if (RS2Inventory.getCount(config.uncutName()) == 0 && !RS2Bank.isOpen())
		{
			this.currentState = State.BANK;
			Time.sleep(120, 400);
			RS2NPC banker = RS2NPCs.getNearest("Banker");
			if (banker == null)
			{
				log.warn("No banker found.. Stopping..");
				reset();
			}
			banker.interact("Bank");
			Time.sleepUntil(() -> RS2Bank.isOpen(), 2000);

			if (RS2Bank.isOpen())
			{
				if (RS2Inventory.getFreeSlots() != 27)
				{
					RS2Bank.depositAllExcept("Chisel");
				}
				Time.sleepUntil(() -> RS2Inventory.getFreeSlots() == 27, 2000);
				Time.sleep(95, 250);
				if (RS2Bank.getCount(true, config.uncutName()) <= 1)
				{
					log.warn("No more Uncut material in bank.. Stopping..");
					reset();
					return -1;
				}
				RS2Bank.withdrawAll(config.uncutName(), RS2Bank.WithdrawMode.ITEM);
				Time.sleepUntil(() -> RS2Inventory.getCount(config.uncutName()) > 0, 2000);
				Time.sleep(75, 175);
				RS2Bank.close();
				Time.sleep(110, 650);
			}
		}

		this.currentState = State.IDLE;
		return -5;
	}

	protected boolean isCutting()
	{
		final RS2Player player = RS2Players.getLocal();
		return player.isInteracting() || player.getAnimation() > -1;
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
}