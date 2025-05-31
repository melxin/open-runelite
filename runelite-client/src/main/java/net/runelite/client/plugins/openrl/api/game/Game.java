package net.runelite.client.plugins.openrl.api.game;

import net.runelite.api.GameState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.rs2.providers.entities.RS2Players;
import net.runelite.client.plugins.openrl.api.rs2.providers.tabs.Tab;
import net.runelite.client.plugins.openrl.api.rs2.providers.tabs.RS2Tabs;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Player;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Widget;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.RS2Widgets;

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

	public static int getWildyLevel()
	{
		final RS2Widget wildyLevelWidget = RS2Widgets.getWidget(InterfaceID.PvpIcons.WILDERNESSLEVEL);
		if (!RS2Widgets.isVisible(wildyLevelWidget))
		{
			return 0;
		}

		final String wildyLevelWidgetText = wildyLevelWidget.getText();

		if (wildyLevelWidgetText.equals(""))
		{
			return 0;
		}

		// Dmm
		if (wildyLevelWidgetText.contains("Guarded") || wildyLevelWidgetText.contains("Protection"))
		{
			return 0;
		}

		if (wildyLevelWidgetText.contains("Deadman"))
		{
			return Integer.MAX_VALUE;
		}

		if (wildyLevelWidgetText.equals("Level: --"))
		{
			final RS2Player local = RS2Players.getLocal();
			final int y = WorldPoint.fromLocal(Static.getClient(), local.getLocalLocation()).getY();
			return 2 + (y - 3528) / 8;
		}

		final String levelText = wildyLevelWidgetText.contains("<br>") ? wildyLevelWidgetText.substring(0, wildyLevelWidgetText.indexOf("<br>")) : wildyLevelWidgetText;
		return Integer.parseInt(levelText.replace("Level: ", ""));
	}

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

		if (!RS2Tabs.isOpen(Tab.LOG_OUT))
		{
			RS2Tabs.open(Tab.LOG_OUT);
		}
	}
}