package net.runelite.client.plugins.openrl.api.game;

import net.runelite.api.GameState;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.rs2.tabs.RS2Tab;
import net.runelite.client.plugins.openrl.api.rs2.tabs.RS2Tabs;
import net.runelite.client.plugins.openrl.api.rs2.widgets.RS2Widget;
import net.runelite.client.plugins.openrl.api.rs2.widgets.RS2Widgets;

public class Game
{
	public static boolean isLoggedIn()
	{
		return getState() == GameState.LOGGED_IN || getState() == GameState.LOADING;
	}

	public static boolean isOnLoginScreen()
	{
		return getState() == GameState.LOGIN_SCREEN
			|| getState() == GameState.LOGIN_SCREEN_AUTHENTICATOR
			|| getState() == GameState.LOGGING_IN;
	}

	public static GameState getState()
	{
		return Static.getClient().getGameState();
	}

	/*public static int getWildyLevel()
	{
		Widget wildyLevelWidget = Widgets.get(WidgetInfo.PVP_WILDERNESS_LEVEL);
		if (!Widgets.isVisible(wildyLevelWidget))
		{
			return 0;
		}

		// Dmm
		if (wildyLevelWidget.getText().contains("Guarded")
			|| wildyLevelWidget.getText().contains("Protection"))
		{
			return 0;
		}

		if (wildyLevelWidget.getText().contains("Deadman"))
		{
			return Integer.MAX_VALUE;
		}
		String widgetText = wildyLevelWidget.getText();
		if (widgetText.equals(""))
		{
			return 0;
		}
		if (widgetText.equals("Level: --"))
		{
			Player local = Players.getLocal();
			int y = WorldPoint.fromLocal(Static.getClient(), local.getLocalLocation()).getY();
			return 2 + (y - 3528) / 8;
		}
		String levelText = widgetText.contains("<br>") ? widgetText.substring(0, widgetText.indexOf("<br>")) : widgetText;
		return Integer.parseInt(levelText.replace("Level: ", ""));
	}*/

	public static int getMembershipDays()
	{
		return Vars.getVarp(VarPlayerID.ACCOUNT_CREDIT);
	}

	public static boolean isInCutscene()
	{
		return Vars.getBit(VarbitID.CUTSCENE_STATUS) > 0;
	}

	public static void logout()
	{
		final RS2Widget logOutHopper = RS2Widgets.get(WidgetID.WORLD_SWITCHER_GROUP_ID, x -> x.hasAction("Logout"));
		if (logOutHopper != null)
		{
			logOutHopper.interact("Logout");
			return;
		}

		final RS2Widget logOut = RS2Widgets.get(WidgetID.LOGOUT_PANEL_ID, x -> x.hasAction("Logout"));
		if (logOut != null)
		{
			logOut.interact("Logout");
			return;
		}

		if (!RS2Tabs.isOpen(RS2Tab.LOG_OUT))
		{
			RS2Tabs.open(RS2Tab.LOG_OUT);
		}
	}
}