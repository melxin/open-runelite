package net.runelite.client.plugins.openrl.plugins.walker;

import lombok.extern.slf4j.Slf4j;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.openrl.api.movement.Pathfinder;
import net.runelite.client.plugins.openrl.api.rs2.providers.scene.RS2Tiles;

@PluginDescriptor(
	name = "Open RuneLite Walker plugin",
	description = "Add walk option",
	tags = {"walker", "movement", "menu option", "test"},
	enabledByDefault = false
)
@Slf4j
public class WalkerPlugin extends Plugin
{
	@Inject
	public Client client;

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		final int type = event.getType();
		if (type == MenuAction.WALK.getId())
		{
			client.createMenuEntry(-2)
				.setParam0(event.getActionParam0())
				.setParam1(event.getActionParam1())
				.setTarget(event.getTarget())
				.setOption("Test walker")
				.setType(MenuAction.RUNELITE)
				.setIdentifier(event.getIdentifier())
				.setItemId(event.getItemId())
				.onClick(e ->
				{
					final WorldPoint selectedSceneWorldPoint = RS2Tiles.getSelectedSceneWorldPoint();
					if (selectedSceneWorldPoint == null)
					{
						log.warn("Selected scene world point is null!");
						return;
					}
					log.info("Selected scene world point: {}", selectedSceneWorldPoint);
					Pathfinder.walkPath(selectedSceneWorldPoint);
				});
		}
	}
}