package net.runelite.client.plugins.openrl.api.rs2.magic;

import lombok.Value;

@Value
public class RuneRequirement
{
	int quantity;
	Rune rune;

	public boolean meetsRequirements()
	{
		return rune.getQuantity() >= quantity;
	}
}