package net.runelite.client.plugins.openrl.api.reflection;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.runelite.api.ItemComposition;
import net.runelite.api.MenuAction;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.commons.Rand;
import net.runelite.client.plugins.openrl.api.input.Keyboard;

/**
 * Source <https://github.com/chsami/Microbot/blob/main/runelite-client/src/main/java/net/runelite/client/plugins/microbot/util/reflection/Rs2Reflection.java>
 */
@Slf4j
public class Reflection
{
	public static Method menuAction = null;

	@SneakyThrows
	public static void invokeMenuAction(int param0, int param1, int opcode, int identifier, int itemId, int worldViewId, String option, String target, int canvasX, int canvasY)
	{
		log.info("[invokeMenuAction] param0: {}, param1: {}, opcode: {}, identifier: {}, itemId: {}, worldViewId: {}, option: {}, target: {}, canvasX: {}, canvasY: {}", param0, param1, opcode, identifier, itemId, worldViewId, option, target, canvasX, canvasY);
		if (menuAction == null)
		{
			menuAction = Arrays.stream(Static.getClient().getClass().getDeclaredMethods())
				.filter(m -> m.getReturnType().getName().equals("void") && m.getParameters().length == 10 && Arrays.stream(m.getParameters())
					.anyMatch(p -> p.getType() == String.class))
				.findFirst()
				.orElse(null);

			if (menuAction == null)
			{
				log.error("invokeMenuAction is broken.. fall back to runelite menuAction");
				Static.getClientThread().invoke(() -> Static.getClient().menuAction(param0, param1, MenuAction.of(opcode), identifier, itemId, option, target));
				return;
			}
		}

		menuAction.setAccessible(true);
		Static.getClientThread().runOnClientThreadOptional(() -> menuAction.invoke(null, param0, param1, opcode, identifier, itemId, worldViewId, option, target, canvasX, canvasY));
		if (Static.getClient().getKeyboardIdleTicks() > Rand.nextInt(5000, 10000))
		{
			log.info("[Keyboard idle] {} Pressing back space", Static.getClient().getKeyboardIdleTicks());
			Keyboard.type((char) java.awt.event.KeyEvent.VK_BACK_SPACE);
		}
		menuAction.setAccessible(false);
	}

	@SneakyThrows
	public static String[] getGroundItemActions(ItemComposition item)
	{
		final List<Field> fields = Arrays.stream(item.getClass().getFields()).filter(x -> x.getType().isArray()).collect(Collectors.toList());
		for (Field field : fields)
		{
			if (field.getType().getComponentType().getName().equals("java.lang.String"))
			{
				final String[] actions = (String[]) field.get(item);
				if (Arrays.stream(actions).anyMatch(x -> x != null && x.equalsIgnoreCase("take")))
				{
					field.setAccessible(true);
					return actions;
				}
			}
		}
		return new String[]{};
	}
}
