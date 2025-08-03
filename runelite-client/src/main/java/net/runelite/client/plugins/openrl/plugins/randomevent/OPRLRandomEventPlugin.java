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
package net.runelite.client.plugins.openrl.plugins.randomevent;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.gameval.NpcID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.account.LocalPlayer;
import net.runelite.client.plugins.openrl.api.commons.Rand;
import net.runelite.client.plugins.openrl.api.commons.Time;
import net.runelite.client.plugins.openrl.api.events.Draw;
import net.runelite.client.plugins.openrl.api.game.Game;
import net.runelite.client.plugins.openrl.api.plugin.LoopedPlugin;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.dialog.RS2Dialog;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.xpreward.RS2ExperienceLamp;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2NPC;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Player;
import net.runelite.client.ui.overlay.OverlayUtil;

@PluginDescriptor(
	name = "OPRLRandomEventPlugin",
	description = "Dismiss random events/solve genie",
	tags = {"random", "event", "dismiss", "genie", "xp", "reward", "lamp"},
	enabledByDefault = false
)
@Slf4j
public class OPRLRandomEventPlugin extends LoopedPlugin
{
	@Inject
	protected OPRLRandomEventConfig config;

	@Provides
	private OPRLRandomEventConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(OPRLRandomEventConfig.class);
	}

	@Subscribe
	protected void onConfigChanged(ConfigChanged configChanged)
	{
		if (!configChanged.getGroup().equals(OPRLRandomEventConfig.GROUP))
		{
			return;
		}
	}

	private Set<Integer> RANDOM_EVENT_NPCS;

	private RS2NPC currentRandomEvent;
	private RS2NPC lastRandomEvent;
	private Instant lastSpawnTime;
	private long remainingSecondsPaused = -1;

	@Override
	protected void startUp()
	{
		this.RANDOM_EVENT_NPCS = ImmutableSet.of(
			NpcID.MACRO_BEEKEEPER_INVITATION,
			NpcID.MACRO_COMBILOCK_PIRATE,
			NpcID.MACRO_JEKYLL, NpcID.MACRO_JEKYLL_UNDERWATER,
			NpcID.MACRO_DWARF,
			NpcID.PATTERN_INVITATION,
			NpcID.MACRO_EVIL_BOB_OUTSIDE, NpcID.MACRO_EVIL_BOB_PRISON,
			NpcID.PINBALL_INVITATION,
			NpcID.MACRO_FORESTER_INVITATION,
			NpcID.MACRO_FROG_CRIER,
			NpcID.MACRO_GENI, NpcID.MACRO_GENI_UNDERWATER,
			NpcID.MACRO_GILES, NpcID.MACRO_GILES_UNDERWATER,
			NpcID.MACRO_GRAVEDIGGER_INVITATION,
			NpcID.MACRO_MILES, NpcID.MACRO_MILES_UNDERWATER,
			NpcID.MACRO_MYSTERIOUS_OLD_MAN, NpcID.MACRO_MYSTERIOUS_OLD_MAN_UNDERWATER,
			NpcID.MACRO_MAZE_INVITATION, NpcID.MACRO_MIME_INVITATION,
			NpcID.MACRO_NILES, NpcID.MACRO_NILES_UNDERWATER,
			NpcID.MACRO_PILLORY_GUARD,
			NpcID.GRAB_POSTMAN,
			NpcID.MACRO_MAGNESON_INVITATION,
			NpcID.MACRO_HIGHWAYMAN, NpcID.MACRO_HIGHWAYMAN_UNDERWATER,
			NpcID.MACRO_SANDWICH_LADY_NPC,
			NpcID.MACRO_DRILLDEMON_INVITATION,
			NpcID.MACRO_COUNTCHECK_SURFACE, NpcID.MACRO_COUNTCHECK_UNDERWATER);
	}

	@Override
	protected void shutDown()
	{
		this.RANDOM_EVENT_NPCS = null;
		this.currentRandomEvent = null;
		this.lastRandomEvent = null;
		this.lastSpawnTime = null;
		this.remainingSecondsPaused = -1;
	}

	@Subscribe
	protected void onInteractingChanged(InteractingChanged event)
	{
		final Actor source = event.getSource();
		final Actor target = event.getTarget();
		final Player player = Static.getClient().getLocalPlayer();

		// Check that the npc is interacting with the player and the player isn't interacting with the npc, so
		// that it doesn't fire from talking to other user's randoms
		if (player == null
			|| target != player
			|| player.getInteracting() == source
			|| !(source instanceof NPC)
			|| !RANDOM_EVENT_NPCS.contains(((NPC) source).getId()))
		{
			return;
		}

		log.info("Random event spawn: {}", source.getName());
		this.currentRandomEvent = new RS2NPC((NPC) source);
		this.lastRandomEvent = currentRandomEvent;
		this.lastSpawnTime = Instant.now();
		this.remainingSecondsPaused = -1;
	}

	@Subscribe
	protected void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() != GameState.LOGGED_IN
			&& event.getGameState() != GameState.LOADING
			&& event.getGameState() != GameState.HOPPING)
		{
			this.currentRandomEvent = null;
			this.remainingSecondsPaused = -1;
		}
	}

	@Override
	protected int loop()
	{
		if (Game.getState() != GameState.LOGGED_IN)
		{
			return Rand.nextInt(800, 1600);
		}

		final RS2Player local = LocalPlayer.get();
		if (local == null /*|| !local.isIdle()*/)
		{
			return Rand.nextInt(800, 1600);
		}

		if (config.solveXPRewardLamp() && RS2ExperienceLamp.inInventory())
		{
			if (!RS2ExperienceLamp.isOpen())
			{
				RS2ExperienceLamp.open();
				Time.sleepUntil(RS2ExperienceLamp::isOpen, 2000);
			}

			Time.sleep(800, 1600);

			if (RS2ExperienceLamp.isOpen())
			{
				if (!RS2ExperienceLamp.isSkillSelected(config.xpRewardSkill()))
				{
					RS2ExperienceLamp.chooseSkill(config.xpRewardSkill());
				}

				Time.sleep(Rand.nextInt(800, 1600));

				if (RS2ExperienceLamp.isSkillSelected(config.xpRewardSkill()))
				{
					RS2ExperienceLamp.confirm();
				}
			}
		}

		if (currentRandomEvent == null)
		{
			return Rand.nextInt(800, 1600);
		}

		if (config.solveGenie() && currentRandomEvent.getName().equals("Genie"))
		{
			log.info("Solving {} event..", currentRandomEvent.getName());
			currentRandomEvent.interact("Talk-to");
			Time.sleepUntil(() -> RS2Dialog.isOpen(), 2000);
			if (RS2Dialog.isOpen())
			{
				Time.sleep(Rand.nextInt(600, 1000));
				RS2Dialog.continueSpace();
			}
			//Static.invokeMenuAction(0, 0, MenuAction.NPC_FIRST_OPTION, currentRandomEvent.getIndex(), -1, WorldView.TOPLEVEL, "Talk-to", "<col=ffff00>Genie", -1, -1);
		}
		else if (config.dismiss())
		{
			log.info("Dismiss random event: {}", currentRandomEvent.getName());
			currentRandomEvent.interact("Dismiss");
			//Static.invokeMenuAction(0, 0, MenuAction.NPC_FIFTH_OPTION, currentRandomEvent.getIndex(), -1, WorldView.TOPLEVEL, "Dismiss", "<col=ffff00>" + currentRandomEvent.getName(), -1, -1);
		}
		return Rand.nextInt(5000, 10000);
	}

	@Subscribe
	protected void onNpcDespawned(NpcDespawned npcDespawned)
	{
		if (currentRandomEvent == null)
		{
			return;
		}

		final NPC npc = npcDespawned.getNpc();

		if (npc == currentRandomEvent.getNpc())
		{
			log.info("Random event despawn: {}", currentRandomEvent.getName());
			this.currentRandomEvent = null;
			this.remainingSecondsPaused = -1;
			//this.lastSpawnTime = null;
		}
	}

	@Subscribe
	protected void onDraw(Draw event)
	{
		if (config.despawnTimer() && currentRandomEvent != null && lastSpawnTime != null)
		{
			final Graphics graphics = event.getGraphics();
			if (graphics == null)
			{
				return;
			}

			final Graphics2D g2d = (Graphics2D) graphics;

			final long elapsedSeconds = Duration.between(lastSpawnTime, Instant.now()).getSeconds();
			long remainingSeconds = 60 - elapsedSeconds;

			if (remainingSeconds < 0)
			{
				remainingSeconds = 0;
			}

			if (RS2Dialog.isOpen() && remainingSecondsPaused == -1)
			{
				remainingSecondsPaused = remainingSeconds;
			}
			else if (remainingSecondsPaused > -1)
			{
				lastSpawnTime = Instant.now().minusSeconds(60 - remainingSecondsPaused);
				remainingSecondsPaused = -1;
			}

			remainingSeconds = RS2Dialog.isOpen() && remainingSecondsPaused > -1 ? remainingSecondsPaused : remainingSeconds;

			if (remainingSeconds >= 30)
			{
				OverlayUtil.renderActorOverlay(g2d, currentRandomEvent, remainingSeconds + "s", Color.GREEN);
			}
			else if (remainingSeconds > 10 && remainingSeconds < 30)
			{
				OverlayUtil.renderActorOverlay(g2d, currentRandomEvent, remainingSeconds + "s", Color.ORANGE);
			}
			else if (remainingSeconds <= 10)
			{
				OverlayUtil.renderActorOverlay(g2d, currentRandomEvent, remainingSeconds + "s", Color.RED);
			}
		}
	}
}