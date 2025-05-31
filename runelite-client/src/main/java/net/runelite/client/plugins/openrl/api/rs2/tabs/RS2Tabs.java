package net.runelite.client.plugins.openrl.api.rs2.tabs;

import java.util.Arrays;
import net.runelite.api.GameState;
import net.runelite.api.VarClientInt;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.game.Game;
import net.runelite.client.plugins.openrl.api.game.Vars;
import net.runelite.client.plugins.openrl.api.managers.InteractionManager;
import net.runelite.client.plugins.openrl.api.rs2.widgets.RS2Widget;
import net.runelite.client.plugins.openrl.api.rs2.widgets.RS2Widgets;

public class RS2Tabs
{
	public static void open(RS2Tab tab)
	{
		if (Static.getClient() == null || Game.getState() != GameState.LOGGED_IN)
		{
			return;
		}

		if (Static.getOpenRuneLiteConfig().interactionMethod() == InteractionManager.InteractMethod.MOUSE_EVENTS)
		{
			final RS2Widget fixedLayoutTab = RS2Widgets.getWidget(tab.getFixedLayoutInterfaceId());
			final RS2Widget classicLayoutTab = RS2Widgets.getWidget(tab.getClassicLayoutInterfaceId());
			final RS2Widget modernLayoutTab = RS2Widgets.getWidget(tab.getModernLayoutInterfaceId());
			final RS2Widget currentLayoutTab = fixedLayoutTab != null ? fixedLayoutTab : classicLayoutTab != null ? classicLayoutTab : modernLayoutTab != null ? modernLayoutTab : null;
			if (currentLayoutTab != null)
			{
				currentLayoutTab.interact(0);
			}
			return;
		}

		Static.getClientThread().invoke(() -> Static.getClient().runScript(915, tab.getIndex()));
	}

	public static boolean isOpen(RS2Tab tab)
	{
		return Vars.getVarcInt(VarClientInt.INVENTORY_TAB) == Arrays.asList(RS2Tab.values()).indexOf(tab);
	}
}