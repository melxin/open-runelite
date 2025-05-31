package net.runelite.client.plugins.openrl;

import com.google.inject.Injector;
import lombok.Getter;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.client.RuneLite;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.openrl.api.input.Keyboard;
import net.runelite.client.plugins.openrl.api.input.Mouse;
import net.runelite.client.plugins.openrl.api.input.naturalmouse.NaturalMouse;
import net.runelite.client.plugins.openrl.api.managers.InteractionManager;
import net.runelite.client.plugins.openrl.api.managers.NeverLogoutManager;
import net.runelite.client.plugins.openrl.api.managers.GameDataCachedManager;
import net.runelite.client.plugins.openrl.api.plugin.LoopedPluginManager;
import net.runelite.client.plugins.openrl.api.reflection.Reflection;

@Singleton
public class Static
{
	@Inject
	@Getter
	private static EventBus eventBus;

	@Inject
	@Getter
	private static ClientThread clientThread;

	@Inject
	@Getter
	private static Client client;

	@Inject
	@Getter
	private static InteractionManager interactionManager;

	@Inject
	@Getter
	private static Mouse mouse;

	@Inject
	@Getter
	private static NaturalMouse naturalMouse;

	@Inject
	@Getter
	private static Keyboard keyboard;

	@Inject
	@Getter
	private static NeverLogoutManager neverLogoutManager;

	@Inject
	@Getter
	private static ItemManager itemManager;

	@Inject
	@Getter
	private static PluginManager pluginManager;

	@Inject
	@Getter
	private static LoopedPluginManager loopedPluginManager;

	@Inject
	@Getter
	private static ConfigManager configManager;

	@Inject
	@Getter
	private static OpenRuneLiteConfig openRuneLiteConfig;

	@Inject
	@Getter
	private static GameDataCachedManager gameDataCached;

	public static void invokeMenuAction(String option, String target, int index, int opcode, int param0, int param1, int itemId, int canvasX, int canvasY)
	{
		invokeMenuAction(param0, param1, opcode, index, itemId, -1, option, target, canvasX, canvasY);
	}

	public static void invokeMenuAction(int param0, int param1, MenuAction menuAction, int index, int itemId, int worldViewId, String option, String target, int canvasX, int canvasY)
	{
		invokeMenuAction(param0, param1, menuAction.getId(), index, itemId, worldViewId, option, target, canvasX, canvasY);
	}

	public static void invokeMenuAction(int param0, int param1, int opcode, int index, int itemId, int worldViewId, String option, String target, int canvasX, int canvasY)
	{
		//getClient().menuAction(param0, param1, menuAction, index, itemId, option, target);
		Reflection.invokeMenuAction(param0, param1, opcode, index, itemId, worldViewId, option, target, canvasX, canvasY);
	}

	public static Injector getInjector()
	{
		return RuneLite.getInjector();
	}

}
