package net.runelite.client.plugins.openrl.api.account;

import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.vars.AccountType;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.game.Game;
import net.runelite.client.plugins.openrl.api.game.Vars;
import net.runelite.client.plugins.openrl.api.rs2.entities.RS2Player;
import net.runelite.client.plugins.openrl.api.rs2.entities.RS2Players;

public class LocalPlayer
{
	public static RS2Player get()
	{
		return RS2Players.getLocal();
	}

	public static String getUsername()
	{
		return Static.getClient().getUsername();
	}

	/*public static String getPassword()
	{
		return Static.getClient().getPassword();
	}*/

	public static int getTotalLevel()
	{
		return Static.getClient().getTotalLevel();
	}

	public static int getQuestPoints()
	{
		return Vars.getVarp(VarPlayerID.QP);
	}

	public static int getMembershipDays()
	{
		return Game.getMembershipDays();
	}

	public static boolean isMember()
	{
		return getMembershipDays() > 0;
	}

	public static String getDisplayName()
	{
		return get().getName();
	}

	public static AccountType getAccountType()
	{
		return Static.getClient().getAccountType();
	}
}