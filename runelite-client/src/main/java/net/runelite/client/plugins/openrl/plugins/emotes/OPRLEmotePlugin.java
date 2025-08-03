/*
 * Copyright (c) 2025, Melxin <https://github.com/melxin>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.openrl.plugins.emotes;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.openrl.api.events.Draw;
import net.runelite.client.plugins.openrl.api.game.Game;
import net.runelite.client.plugins.openrl.api.plugin.LoopedPlugin;
import net.runelite.client.plugins.openrl.api.rs2.providers.emotes.RS2Emotes;
import net.runelite.client.plugins.openrl.api.rs2.providers.entities.RS2Players;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Player;

@PluginDescriptor(
	name = "OPRLEmotePlugin",
	description = "Loop emote",
	tags = {"emote", "emotes", "perform", "loop"},
	enabledByDefault = false
)
@Slf4j
public class OPRLEmotePlugin extends LoopedPlugin
{
	@Inject
	private Client client;

	@Inject
	protected OPRLEmoteConfig config;

	@Provides
	private OPRLEmoteConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(OPRLEmoteConfig.class);
	}

	@Subscribe
	protected void onConfigChanged(ConfigChanged configChanged)
	{
		if (!configChanged.getGroup().equals(OPRLEmoteConfig.GROUP))
		{
			return;
		}
	}

	private Instant scriptStartTime;
	private int loopedCount;

	private String getTimeRunning()
	{
		return scriptStartTime != null ? this.getTimeBetween(scriptStartTime, Instant.now()) : "";
	}

	@Subscribe
	protected void onConfigButtonClicked(ConfigButtonClicked event)
	{
		if (!event.getGroup().equals(OPRLEmoteConfig.GROUP) || !event.getKey().equals("start"))
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

	@Override
	protected void shutDown()
	{
		reset();
	}

	private void reset()
	{
		this.scriptStartTime = null;
		this.loopedCount = 0;
	}

	@Override
	protected int loop()
	{
		if (scriptStartTime == null)
		{
			return 1000;
		}

		if (Game.getState() != GameState.LOGGED_IN)
		{
			return 1000;
		}

		final RS2Player local = RS2Players.getLocal();
		if (local == null)
		{
			return 1000;
		}

		if (local.getAnimation() == -1)
		{
			RS2Emotes.perform(config.emoteId(), true);
			loopedCount++;
		}
		return -1;
	}

	@Subscribe
	protected void onDraw(Draw event)
	{
		if (!config.overlayEnabled() || scriptStartTime == null)
		{
			return;
		}

		final Graphics graphics = event.getGraphics();
		if (graphics == null)
		{
			return;
		}
		final Graphics2D g2d = (Graphics2D) graphics;

		g2d.setColor(config.overlayColor());
		g2d.drawString("Time running: " + this.getTimeRunning(), 10, 20);
		g2d.drawString("Looped: " + loopedCount, 10, 35);
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