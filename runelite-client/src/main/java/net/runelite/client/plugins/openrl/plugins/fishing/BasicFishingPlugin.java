package net.runelite.client.plugins.openrl.plugins.fishing;

import lombok.extern.slf4j.Slf4j;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.List;
import net.runelite.api.GameState;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.commons.Time;
import net.runelite.client.plugins.openrl.api.input.Keyboard;
import net.runelite.client.plugins.openrl.api.plugin.LoopedPlugin;
import net.runelite.client.plugins.openrl.api.rs2.providers.entities.RS2NPCs;
import net.runelite.client.plugins.openrl.api.rs2.providers.entities.RS2Players;
import net.runelite.client.plugins.openrl.api.rs2.providers.items.RS2Inventory;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.dialog.RS2Dialog;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Item;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2NPC;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Player;

@PluginDescriptor(
	name = "BasicFishingPlugin",
	description = "Basic fishing",
	tags = {"fishing", "1-48"}
)
@Slf4j
public class BasicFishingPlugin extends LoopedPlugin
{
	@Override
	protected void startUp()
	{
	}

	@Override
	protected int loop()
	{
		if (Static.getClient().getGameState() != GameState.LOGGED_IN)
		{
			return -1;
		}

		final RS2Player local = RS2Players.getLocal();
		if (local == null)
		{
			return -1;
		}

		if (RS2Inventory.isFull() || RS2Inventory.getCount(i -> i.getName().startsWith("Raw")) >= 20 && !isFishing())
		{
			log.info("Should drop | Inv full? {} | Free slot count {}", RS2Inventory.isFull(), RS2Inventory.getFreeSlots());
			this.dropShiftClick();
			//this.dropFastInvoke();
			return -1;
		}

		final RS2NPC fishingSpot = RS2NPCs.getAll(n -> n.getName().equals("Fishing spot"))
			.stream()
			.findFirst()
			.orElse(null);

		if (fishingSpot != null)
		{
			log.info("Fishing spot: {} | {}", fishingSpot.getId(), fishingSpot.getName());

			/*final Rectangle clickableViewport = RS2Camera.getClickableViewport();
			if (clickableViewport != null)
			{
				if (clickableViewport.intersects(fishingSpot.getCanvasTilePoly().getBounds()))
				{
					log.info("in viewport");
				}
				else
				{
					log.info("not in viewport!");
					RS2Camera.turnTo(fishingSpot.getNpc());
				}
			}*/

			if (RS2Dialog.isOpen())
			{
				//RS2Dialog.continueSpace();
				fishingSpot.interact(0);
				Time.sleepTicks(2);
			}

			if (!isFishing())
			{
				log.info("FISH!");
				fishingSpot.interact(0);
				Time.sleepTicks(2);
				Time.sleepUntil(this::isFishing, 3000);
			}
		}

		return -1;
	}

	private boolean isFishing()
	{
		final RS2Player player = RS2Players.getLocal();
		return player.isInteracting() || player.getAnimation() > -1;
	}

	private void dropShiftClick()
	{
		final List<RS2Item> items = RS2Inventory.getAll(i -> i.getName().contains("Raw"));

		Collections.shuffle(items);

		Keyboard.pressed(KeyEvent.VK_SHIFT);
		for (RS2Item item : items)
		{
			log.info("Name: {}", item.getName());
			item.interact(0);
			Time.sleep(45, 150);
		}
		Keyboard.released(KeyEvent.VK_SHIFT);
	}
}