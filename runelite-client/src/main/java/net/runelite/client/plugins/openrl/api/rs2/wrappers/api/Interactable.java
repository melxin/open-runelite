package net.runelite.client.plugins.openrl.api.rs2.wrappers.api;

import java.util.Arrays;
import java.util.function.Predicate;
import net.runelite.client.plugins.openrl.api.commons.Predicates;

public interface Interactable
{
	String[] getActions();

	void interact(int index);

	void interact(String action);

	default boolean hasAction(String... actions)
	{
		return hasAction(Predicates.textEquals(actions));
	}

	default boolean hasAction(Predicate<String> filter)
	{
		final String[] actions = getActions();
		return actions != null && Arrays.stream(actions).anyMatch(filter);
	}

	default void interact(String... actions)
	{
		interact(Predicates.textEquals(actions));
	}

	default void interact(Predicate<String> predicate)
	{
		final String[] actions = getActions();
		if (actions == null)
		{
			return;
		}

		for (int i = 0; i < actions.length; i++)
		{
			if (predicate.test(actions[i]))
			{
				interact(i);
				return;
			}
		}
	}
}