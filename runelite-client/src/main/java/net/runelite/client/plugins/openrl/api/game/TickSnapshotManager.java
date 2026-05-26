package net.runelite.client.plugins.openrl.api.game;

import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

/**
 * Keeps {@link TickSnapshot} up to date.
 *
 * Bound as an eager singleton in {@code RuneLiteModule} so it registers its
 * event subscription as soon as the injector is created, even before any
 * plugin loads.
 */
@Singleton
public class TickSnapshotManager
{
	@Inject
	private Client client;

	@Inject
	TickSnapshotManager(EventBus eventBus)
	{
		eventBus.register(this);
	}

	@Subscribe(priority = Integer.MAX_VALUE)
	public void onGameTick(GameTick event)
	{
		// GameTick is always fired on the client thread — safe to read all client state here.
		TickSnapshot.update(TickSnapshot.capture(client));
	}
}
