/*
 * Copyright (c) 2025, Melxin <https://github.com/melxin>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.openrl.api.reflection;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.runelite.api.Actor;
import net.runelite.api.ItemComposition;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.commons.Rand;
import net.runelite.client.plugins.openrl.api.input.Keyboard;

/**
 * Helper source <https://github.com/chsami/Microbot/blob/main/runelite-client/src/main/java/net/runelite/client/plugins/microbot/util/reflection/Rs2Reflection.java>
 */
@Slf4j
public class Reflection
{
	private static Method menuAction;
	private static Object menuActionGarbageValue;
	@SneakyThrows
	public static void invokeMenuAction(int param0, int param1, int opcode, int identifier, int itemId, int worldViewId, String option, String target, int canvasX, int canvasY)
	{
		if (menuAction == null)
		{
			final int MENU_ACTION_ACCESS_FLAGS_VANILLA = (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL);
			final String MENU_ACTION_DESCRIPTOR_VANILLA = "(IIIIIILjava/lang/String;Ljava/lang/String;IIB)V"; // This is with the garbage value, last byte
			final String MENU_ACTION_DESCRIPTOR_WITHOUT_GARBAGE_VALUE = "(IIIIIILjava/lang/String;Ljava/lang/String;II)V";
			final String MENU_ACTION_DESCRIPTOR_RUNELITE = "(IILnet/runelite/api/MenuAction;IILjava/lang/String;Ljava/lang/String;)V";

			final Class<?> clientClazz = Static.getClient().getClass();
			final ClassReader classReader = new ClassReader(clientClazz.getName());
			final ClassNode classNode = new ClassNode(Opcodes.ASM9);
			classReader.accept(classNode, ClassReader.SKIP_FRAMES);

			final MethodNode targetMethodNodeContainingMenuActionInvocation = classNode.methods.stream()
				.filter(m -> m.access == Opcodes.ACC_PUBLIC
					&& (m.name.equals("menuAction") && m.desc.equals(MENU_ACTION_DESCRIPTOR_RUNELITE)
					|| m.name.equals("openWorldHopper") && m.desc.equals("()V")
					|| m.name.equals("hopToWorld") && m.desc.equals("(Lnet/runelite/api/World;)V")))
				.findFirst()
				.orElse(null);

			if (targetMethodNodeContainingMenuActionInvocation != null)
			{
				final InsnList instructions = targetMethodNodeContainingMenuActionInvocation.instructions;
				for (AbstractInsnNode insnNode : instructions)
				{
					if ((insnNode instanceof LdcInsnNode || (insnNode instanceof IntInsnNode)) && insnNode.getNext() instanceof MethodInsnNode)
					{
						if (insnNode instanceof LdcInsnNode)
						{
							menuActionGarbageValue = ((LdcInsnNode) insnNode).cst;
						}
						else if (insnNode instanceof IntInsnNode)
						{
							if (insnNode.getOpcode() == Opcodes.BIPUSH)
							{
								menuActionGarbageValue = ((byte) ((IntInsnNode) insnNode).operand);
							}
							else if (insnNode.getOpcode() == Opcodes.SIPUSH)
							{
								menuActionGarbageValue = ((short) ((IntInsnNode) insnNode).operand);
							}
						}

						final MethodInsnNode menuActionVanillaInsn = (MethodInsnNode) insnNode.getNext();
						if (!menuActionVanillaInsn.desc.equals(MENU_ACTION_DESCRIPTOR_VANILLA))
						{
							throw new RuntimeException("Menu action descriptor vanilla has changed from: " + MENU_ACTION_DESCRIPTOR_VANILLA + " to: " + menuActionVanillaInsn.desc);
						}
						menuAction = Arrays.stream(Class.forName(menuActionVanillaInsn.owner).getDeclaredMethods())
							.filter(m -> m.getName().equals(menuActionVanillaInsn.name))
							.findFirst()
							.orElse(null);
						break;
					}
				}
			}
		}

		if (menuAction == null || menuActionGarbageValue == null)
		{
			log.error("invokeMenuAction is broken!");
			log.info("[PRESENT?] menuAction: {}, menuActionGarbageValue: {}", menuAction != null, menuActionGarbageValue != null);
			log.warn("Falling back to runelite menuAction..");
			Static.getClientThread().invoke(() -> Static.getClient().menuAction(param0, param1, MenuAction.of(opcode), identifier, itemId, option, target));
			return;
		}

		menuAction.setAccessible(true);
		Static.getClientThread().runOnClientThreadOptional(() -> menuAction.invoke(null, param0, param1, opcode, identifier, itemId, worldViewId, option, target, canvasX, canvasY, menuActionGarbageValue));
		menuAction.setAccessible(false);

		if (Static.getClient().getKeyboardIdleTicks() > Rand.nextInt(5000, 10000))
		{
			log.info("[Keyboard idle] {} Pressing back space", Static.getClient().getKeyboardIdleTicks());
			Keyboard.type((char) java.awt.event.KeyEvent.VK_BACK_SPACE);
		}
	}

	/*@SneakyThrows
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
	}*/

	@SneakyThrows
	public static void setItemId(MenuEntry menuEntry, int itemId)
	{
		var list = Arrays.stream(menuEntry.getClass().getMethods())
			.filter(x -> x.getName().equals("setItemId"))
			.collect(Collectors.toList());

		list.get(0).invoke(menuEntry, itemId); // use the setItemId method through reflection
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

	/**
	 * get animation id
	 *
	 * @param actor
	 * @return uncensored animation id
	 */
	private static Field sequenceField;
	private static int sequenceFieldMultiplierValue;
	public static int getAnimation(Actor actor)
	{
		try
		{
			if (sequenceField == null)
			{
				final Class<?> actorSubClazz = actor.getClass();
				final Class<?> actorClazz = actorSubClazz.getSuperclass();
				log.info("Actor class: {} | Actor sub class: {}", actorClazz.getName(), actorSubClazz.getName());
				final ClassReader classReader = new ClassReader(actorClazz.getName());
				final ClassNode classNode = new ClassNode(Opcodes.ASM9);
				classReader.accept(classNode, ClassReader.SKIP_FRAMES);
				final MethodNode getAnimationMethodNode = classNode.methods.stream()
					.filter(m -> m.name.equals("getAnimation") && m.desc.equals("()I"))
					.findFirst()
					.orElse(null);
				if (getAnimationMethodNode != null)
				{
					final InsnList instructions = getAnimationMethodNode.instructions;
					for (AbstractInsnNode insnNode : instructions)
					{
						if (insnNode instanceof FieldInsnNode && ((FieldInsnNode) insnNode).desc.equals("I")
							&& insnNode.getNext() instanceof LdcInsnNode)
						{
							final FieldInsnNode sequenceFieldInsn = (FieldInsnNode) insnNode;
							final LdcInsnNode multiplierInsn = (LdcInsnNode) insnNode.getNext();
							log.info("Found sequence field: {}.{} * {}", sequenceFieldInsn.owner, sequenceFieldInsn.name, multiplierInsn.cst);

							sequenceField = actorClazz.getDeclaredField(sequenceFieldInsn.name);
							sequenceFieldMultiplierValue = (int) multiplierInsn.cst;
						}
					}
				}
			}

			if (sequenceField == null)
			{
				log.error("getAnimation method is broken!");
				return -1;
			}

			sequenceField.setAccessible(true);
			final int animationId = sequenceField.getInt(actor) * sequenceFieldMultiplierValue;
			sequenceField.setAccessible(false);
			log.info("Animation id: {}", animationId);
			return animationId;
		}
		catch (Exception e)
		{
			log.error("Failed to get animation id", e);
		}
		return -1;
	}
}
