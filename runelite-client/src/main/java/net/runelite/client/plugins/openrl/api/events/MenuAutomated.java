package net.runelite.client.plugins.openrl.api.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.client.plugins.openrl.Static;

/**
 * Event class for menu interactions
 */
@Data
@AllArgsConstructor
public class MenuAutomated
{
	private int param0;
	private int param1;
	private MenuAction menuAction;
	private int index;
	private int itemId;
	private int worldViewId;
	private String option;
	private String target;
	private int canvasX;
	private int canvasY;

	public MenuEntry getMenuEntry()
	{
		return Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getMenu().createMenuEntry(-1)
			.setParam0(param0)
			.setParam1(param1)
			.setType(menuAction)
			.setIdentifier(index)
			.setItemId(itemId)
			.setWorldViewId(worldViewId)
			.setOption(option)
			.setTarget(target)
			.setForceLeftClick(false)
			.onClick(null))
			.orElse(null);
	}
}
