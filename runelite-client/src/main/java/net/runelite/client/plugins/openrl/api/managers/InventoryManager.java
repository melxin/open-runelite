package net.runelite.client.plugins.openrl.api.managers;

import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

@Singleton
public class InventoryManager
{
	@Inject
	private Client client;

	@Inject
	InventoryManager(EventBus eventBus)
	{
		eventBus.register(this);
	}

	@Subscribe(priority = Integer.MAX_VALUE)
	private void onItemContainerChanged(ItemContainerChanged e)
	{
		if (e.getContainerId() == InventoryID.INV)
		{
			// Reload inventory
			client.runScript(6009, 9764864, 28, 1, -1);
		}
	}
}
