package net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.requirement;

import java.util.function.Function;
import net.runelite.client.plugins.openrl.api.game.Vars;

public enum VarType implements Function<Integer, Integer>
{
	VARBIT,
	VARP;

	@Override
	public Integer apply(Integer index)
	{
		switch (this)
		{
			case VARBIT:
				return Vars.getBit(index);
			case VARP:
				return Vars.getVarp(index);
		}
		return 0;
	}
}