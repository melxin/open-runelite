package net.runelite.client.plugins.openrl.api.rs2.tabs;

import java.util.Arrays;
import net.runelite.api.GameState;
import net.runelite.api.VarClientInt;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.game.Game;
import net.runelite.client.plugins.openrl.api.game.Vars;

public class RS2Tabs
{
	public static void open(RS2Tab tab)
	{
		if (Static.getClient() == null || Game.getState() != GameState.LOGGED_IN)
		{
			return;
		}

		Static.getClientThread().invoke(() -> Static.getClient().runScript(915, tab.getIndex()));
	}

	public static boolean isOpen(RS2Tab tab)
	{
		return Vars.getVarcInt(VarClientInt.INVENTORY_TAB) == Arrays.asList(RS2Tab.values()).indexOf(tab);
	}
}