package net.runelite.client.plugins.openrl.api.rs2.providers.widgets.friends;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.runelite.api.Friend;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.commons.Time;
import net.runelite.client.plugins.openrl.api.input.Keyboard;
import net.runelite.client.plugins.openrl.api.rs2.providers.tabs.RS2Tabs;
import net.runelite.client.plugins.openrl.api.rs2.providers.tabs.Tab;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.RS2Widgets;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.dialog.RS2Dialog;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Widget;

public class RS2Friends
{
	public static List<Friend> getAll(Predicate<Friend> filter)
	{
		return Arrays.stream(Static.getClient().getFriendContainer().getMembers())
			.filter(filter)
			.collect(Collectors.toList());
	}

	public static List<Friend> getAll(String... names)
	{
		return getAll(x ->
		{
			if (x.getName() == null)
			{
				return false;
			}

			for (String name : names)
			{
				if (name.equals(x.getName()))
				{
					return true;
				}
			}

			return false;
		});
	}

	public static List<Friend> getAll(int... worlds)
	{
		return getAll(x ->
		{
			for (int world : worlds)
			{
				if (world == x.getWorld())
				{
					return true;
				}
			}

			return false;
		});
	}

	public static Friend getFirst(Predicate<Friend> filter)
	{
		return getAll(filter).stream()
			.findFirst()
			.orElse(null);
	}

	public static Friend getFirst(String... names)
	{
		return getFirst(x ->
		{
			if (x.getName() == null)
			{
				return false;
			}

			for (String name : names)
			{
				if (name.equals(x.getName()))
				{
					return true;
				}
			}

			return false;
		});
	}

	public static Friend getFirst(int... worlds)
	{
		return getFirst(x ->
		{
			for (int world : worlds)
			{
				if (world == x.getWorld())
				{
					return true;
				}
			}

			return false;
		});
	}

	public static boolean isAdded(String name)
	{
		return Static.getClient().isFriended(name, false);
	}

	public static boolean isOnline(Friend friend)
	{
		return isOnline(friend.getName());
	}

	public static boolean isOnline(String name)
	{
		return Static.getClient().isFriended(name, true);
	}

	public static void add(String name)
	{
		if (!RS2Tabs.isOpen(Tab.FRIENDS))
		{
			RS2Tabs.open(Tab.FRIENDS);
		}

		final RS2Widget addFriendWidget = RS2Widgets.getWidget(InterfaceID.Friends.ADDFRIEND);
		if (!RS2Widgets.isVisible(addFriendWidget))
		{
			return;
		}
		addFriendWidget.interact(0);
		Time.sleepUntil(() -> RS2Dialog.isOpen(), 2000);
		if (RS2Dialog.isOpen())
		{
			Keyboard.type(name, true);
		}
		//Static.getClient().addFriend(name);
	}

	public static void remove(String name)
	{
		if (!RS2Tabs.isOpen(Tab.FRIENDS))
		{
			RS2Tabs.open(Tab.FRIENDS);
		}

		final RS2Widget delFriendWidget = RS2Widgets.getWidget(InterfaceID.Friends.DELFRIEND);
		if (!RS2Widgets.isVisible(delFriendWidget))
		{
			return;
		}
		delFriendWidget.interact(0);
		Time.sleepUntil(() -> RS2Dialog.isOpen(), 2000);
		if (RS2Dialog.isOpen())
		{
			Keyboard.type(name, true);
		}
		//Static.getClient().removeFriend(name);
	}
}