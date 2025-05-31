package net.runelite.client.plugins.openrl.api.managers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

@Singleton
@Deprecated
public class InventoryManager
{
	@Getter
	private static final Map<Integer, ItemContainerSnapshot> cachedItemContainers = new ConcurrentHashMap<>();

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
		final ItemContainerSnapshot items = new ItemContainerSnapshot(e);
		cachedItemContainers.put(e.getContainerId(), items);

		if (e.getContainerId() == InventoryID.INV)
		{
			// Reload inventory
			client.runScript(6009, 9764864, 28, 1, -1);
		}
	}

	@Getter
	@RequiredArgsConstructor
	public static class ItemContainerSnapshot
	{
		/**
		 * ID of the container the snapshot was taken of.
		 */
		private final int containerId;

		/**
		 * Items within the inventory at the given snapshot.
		 */
		private final Item[] items;

		/**
		 * Snapshot creation timestamp.
		 */
		private final Instant timestamp;

		public ItemContainerSnapshot(ItemContainerChanged event)
		{
			this.containerId = event.getContainerId();
			this.items = event.getItemContainer().getItems();
			this.timestamp = Instant.now();
		}
	}
}