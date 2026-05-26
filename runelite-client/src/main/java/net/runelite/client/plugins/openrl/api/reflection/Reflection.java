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
import org.objectweb.asm.tree.TypeInsnNode;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import net.runelite.api.Client;
import net.runelite.api.EntityOps;
import net.runelite.api.HeadIcon;
import net.runelite.api.ItemComposition;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPCComposition;
import net.runelite.api.Scene;
import net.runelite.api.WorldView;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.commons.Rand;
import net.runelite.client.plugins.openrl.api.input.Keyboard;
import net.runelite.client.plugins.openrl.api.managers.InteractionSafety;

// Helper source <https://github.com/chsami/Microbot/blob/main/runelite-client/src/main/java/net/runelite/client/plugins/microbot/util/reflection/Rs2Reflection.java>
@Slf4j
public class Reflection
{
	// All cached Method/Field references are made accessible once at cache time and never reset.
	// volatile ensures safe publication across threads without full synchronization.
	private static volatile Method menuAction;
	private static volatile Object menuActionGarbageValue;

	@SneakyThrows
	public static void invokeMenuAction(int param0, int param1, int opcode, int identifier, int itemId, int worldViewId, String option, String target, int canvasX, int canvasY)
	{
		// Capture safety reference once. Null during early startup before static injection completes.
		final InteractionSafety safety = Static.getInteractionSafety();
		if (safety != null && !safety.isInteractionSafe())
		{
			log.warn("invokeMenuAction blocked — system not safe: {}", safety.getUnsafeReason());
			return;
		}

		if (menuAction == null)
		{
			final int MENU_ACTION_ACCESS_FLAGS_VANILLA = (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL);
			final String MENU_ACTION_DESCRIPTOR_VANILLA_BYTE_GARBAGE_VALUE = "(IIIIIILjava/lang/String;Ljava/lang/String;IIB)V";
			final String MENU_ACTION_DESCRIPTOR_VANILLA_INT_GARBAGE_VALUE = "(IIIIIILjava/lang/String;Ljava/lang/String;III)V";
			final String MENU_ACTION_DESCRIPTOR_RUNELITE = "(IILnet/runelite/api/MenuAction;IILjava/lang/String;Ljava/lang/String;)V";

			final Class<?> clientClazz = Static.getClient().getClass();
			final ClassReader classReader = new ClassReader(clientClazz.getName());
			final ClassNode classNode = new ClassNode(Opcodes.ASM9);
			classReader.accept(classNode, ClassReader.SKIP_FRAMES);

			final MethodNode targetMethodNode = classNode.methods.stream()
				.filter(m -> m.access == Opcodes.ACC_PUBLIC
					&& (m.name.equals("menuAction") && m.desc.equals(MENU_ACTION_DESCRIPTOR_RUNELITE)
					|| m.name.equals("openWorldHopper") && m.desc.equals("()V")
					|| m.name.equals("hopToWorld") && m.desc.equals("(Lnet/runelite/api/World;)V")))
				.findFirst()
				.orElse(null);

			if (targetMethodNode != null)
			{
				final InsnList instructions = targetMethodNode.instructions;
				for (AbstractInsnNode insnNode : instructions)
				{
					if ((insnNode instanceof LdcInsnNode || insnNode instanceof IntInsnNode) && insnNode.getNext() instanceof MethodInsnNode)
					{
						Object garbageValue = null;
						if (insnNode instanceof LdcInsnNode)
						{
							garbageValue = ((LdcInsnNode) insnNode).cst;
						}
						else
						{
							final IntInsnNode intInsn = (IntInsnNode) insnNode;
							if (intInsn.getOpcode() == Opcodes.BIPUSH)
							{
								garbageValue = (byte) intInsn.operand;
							}
							else if (intInsn.getOpcode() == Opcodes.SIPUSH)
							{
								garbageValue = (short) intInsn.operand;
							}
						}

						final MethodInsnNode menuActionVanillaInsn = (MethodInsnNode) insnNode.getNext();
						if (!menuActionVanillaInsn.desc.equals(MENU_ACTION_DESCRIPTOR_VANILLA_BYTE_GARBAGE_VALUE)
							&& !menuActionVanillaInsn.desc.equals(MENU_ACTION_DESCRIPTOR_VANILLA_INT_GARBAGE_VALUE))
						{
							log.error("Menu action descriptor vanilla has changed from: {} or: {} to: {}",
								MENU_ACTION_DESCRIPTOR_VANILLA_BYTE_GARBAGE_VALUE,
								MENU_ACTION_DESCRIPTOR_VANILLA_INT_GARBAGE_VALUE,
								menuActionVanillaInsn.desc);
							if (safety != null) safety.reportHookFailed("menuAction", InteractionSafety.HookSeverity.CRITICAL,
								"vanilla descriptor changed to: " + menuActionVanillaInsn.desc);
							break;
						}

						final Method resolved = Arrays.stream(Class.forName(menuActionVanillaInsn.owner).getDeclaredMethods())
							.filter(m -> m.getName().equals(menuActionVanillaInsn.name))
							.findFirst()
							.orElse(null);

						if (resolved != null && garbageValue != null)
						{
							resolved.setAccessible(true);
							menuAction = resolved;
							menuActionGarbageValue = garbageValue;
							if (safety != null) safety.reportHookResolved("menuAction", InteractionSafety.HookSeverity.CRITICAL);
						}
						break;
					}
				}
			}
		}

		if (menuAction == null || menuActionGarbageValue == null)
		{
			if (safety != null) safety.reportHookFailed("menuAction", InteractionSafety.HookSeverity.CRITICAL,
				"vanilla method not found after bytecode scan");
			log.error("invokeMenuAction: vanilla method not found, falling back to RuneLite menuAction (worldViewId/canvasX/canvasY will be ignored)");
			Static.getClientThread().invoke(() -> Static.getClient().menuAction(param0, param1, MenuAction.of(opcode), identifier, itemId, option, target));
			return;
		}

		final Method m = menuAction;
		final Object gv = menuActionGarbageValue;
		Static.getClientThread().runOnClientThreadOptional(() ->
		{
			try
			{
				m.invoke(null, param0, param1, opcode, identifier, itemId, worldViewId, option, target, canvasX, canvasY, gv);
				if (safety != null) safety.reportInteractionSuccess();
			}
			catch (IllegalAccessException e)
			{
				// Hook became inaccessible — treat as a mapping change.
				// Clear cache so the next call attempts re-discovery.
				menuAction = null;
				menuActionGarbageValue = null;
				log.error("invokeMenuAction: IllegalAccessException — hook may be stale, cache cleared", e);
				if (safety != null) safety.reportHookFailed("menuAction", InteractionSafety.HookSeverity.CRITICAL,
					"IllegalAccessException: " + e.getMessage());
			}
			catch (InvocationTargetException e)
			{
				// The vanilla method was invoked correctly but the game threw.
				// This may be a bad parameter or a transient game error — not necessarily a mapping change.
				log.error("invokeMenuAction: invocation threw", e.getCause());
				if (safety != null) safety.reportInteractionAttemptFailed("invokeMenuAction");
			}

			// Keyboard idle reset must happen on the client thread so the idle counter
			// is read and acted on in the same tick that the menu action fires.
			final int idleTicks = Static.getClient().getKeyboardIdleTicks();
			if (idleTicks > Rand.nextInt(5000, 10000))
			{
				log.info("[Keyboard idle] {} ticks — pressing back space", idleTicks);
				Keyboard.type((char) java.awt.event.KeyEvent.VK_BACK_SPACE);
			}

			return null;
		});
	}

	private static volatile Method setItemIdMethod;

	public static void setItemId(MenuEntry menuEntry, int itemId)
	{
		try
		{
			if (setItemIdMethod == null)
			{
				final Method method = Arrays.stream(menuEntry.getClass().getMethods())
					.filter(x -> x.getName().equals("setItemId"))
					.findFirst()
					.orElse(null);
				if (method == null)
				{
					log.error("setItemId: method not found on MenuEntry");
					return;
				}
				setItemIdMethod = method;
			}
			setItemIdMethod.invoke(menuEntry, itemId);
		}
		catch (IllegalAccessException | InvocationTargetException e)
		{
			log.error("setItemId failed", e);
		}
	}

	private static volatile Field entityOpsClassInstanceField;
	private static volatile MethodInsnNode entityOpSetterMethodInsn;
	private static volatile Field entityOpsArrayListField;
	private static volatile Class<?> entityOpsStringGetterClass;
	private static volatile Field entityOpStringGetterField;

	@SneakyThrows
	public static String[] getGroundItemActions(ItemComposition itemComposition)
	{
		if (entityOpSetterMethodInsn == null || entityOpsClassInstanceField == null)
		{
			final Class<?> itemCompositionClazz = itemComposition.getClass();
			final ClassReader classReader = new ClassReader(itemCompositionClazz.getName());
			final ClassNode classNode = new ClassNode(Opcodes.ASM9);
			classReader.accept(classNode, ClassReader.SKIP_FRAMES);

			final MethodNode init = classNode.methods.stream()
				.filter(x -> x.name.equals("<init>"))
				.findFirst()
				.orElse(null);

			if (init == null)
			{
				log.error("getGroundItemActions: <init> not found in ItemComposition");
				return new String[]{};
			}

			for (AbstractInsnNode ain : init.instructions)
			{
				if (ain instanceof FieldInsnNode && ain.getNext() != null && ain.getNext().getOpcode() == Opcodes.ICONST_2)
				{
					final FieldInsnNode fin = (FieldInsnNode) ain;
					final Field f = Class.forName(fin.owner).getDeclaredField(fin.name);
					f.setAccessible(true);
					entityOpsClassInstanceField = f;
				}
				else if ((ain.getOpcode() == Opcodes.INVOKEVIRTUAL || ain.getOpcode() == Opcodes.INVOKESTATIC)
					&& ain instanceof MethodInsnNode
					&& ((MethodInsnNode) ain).desc.endsWith("ILjava/lang/String;I)V"))
				{
					entityOpSetterMethodInsn = (MethodInsnNode) ain;
				}
			}
		}

		if (entityOpsClassInstanceField == null || entityOpSetterMethodInsn == null)
		{
			log.error("getGroundItemActions: failed to locate entityOps instance or setter");
			return new String[]{};
		}

		if (entityOpsArrayListField == null || entityOpsStringGetterClass == null || entityOpStringGetterField == null)
		{
			final ClassReader classReader = new ClassReader(entityOpSetterMethodInsn.owner);
			final ClassNode classNode = new ClassNode(Opcodes.ASM9);
			classReader.accept(classNode, ClassReader.SKIP_FRAMES);

			final MethodNode targetMethod = classNode.methods.stream()
				.filter(x -> x.name.equals(entityOpSetterMethodInsn.name))
				.findFirst()
				.orElse(null);

			if (targetMethod == null)
			{
				log.error("getGroundItemActions: setter method {} not found", entityOpSetterMethodInsn.name);
				return new String[]{};
			}

			for (AbstractInsnNode ain : targetMethod.instructions)
			{
				if (entityOpsArrayListField == null && ain.getOpcode() == Opcodes.GETFIELD && ain instanceof FieldInsnNode)
				{
					final FieldInsnNode fin = (FieldInsnNode) ain;
					if (fin.desc.equals("Ljava/util/ArrayList;"))
					{
						final Field f = Class.forName(fin.owner).getDeclaredField(fin.name);
						f.setAccessible(true);
						entityOpsArrayListField = f;
					}
				}
				else if (entityOpsStringGetterClass == null && ain.getOpcode() == Opcodes.NEW && ain instanceof TypeInsnNode)
				{
					final TypeInsnNode typeInsn = (TypeInsnNode) ain;
					if (typeInsn.desc.length() <= 3)
					{
						entityOpsStringGetterClass = Class.forName(typeInsn.desc);
						final Field sf = Arrays.stream(entityOpsStringGetterClass.getDeclaredFields())
							.filter(x -> x.getType() == String.class)
							.findFirst()
							.orElse(null);
						if (sf != null)
						{
							sf.setAccessible(true);
							entityOpStringGetterField = sf;
						}
					}
				}
			}
		}

		if (entityOpsArrayListField == null || entityOpsStringGetterClass == null
			|| entityOpsClassInstanceField == null || entityOpStringGetterField == null)
		{
			log.error("getGroundItemActions: one or more reflection fields could not be resolved");
			return new String[]{};
		}

		final Object instance = entityOpsClassInstanceField.get(itemComposition);
		final Object targetObject = entityOpsArrayListField.get(instance);

		if (targetObject instanceof ArrayList)
		{
			final ArrayList<?> list = (ArrayList<?>) targetObject;
			final String[] groundItemActions = new String[list.size()];
			for (int i = 0; i < list.size(); ++i)
			{
				final Object actionBean = list.get(i);
				if (actionBean != null)
				{
					groundItemActions[i] = (String) entityOpStringGetterField.get(actionBean);
				}
			}
			return groundItemActions;
		}

		return new String[]{};
	}

	@SneakyThrows
	public static EntityOps getGroundItemEntityOps(ItemComposition itemComposition)
	{
		if (entityOpsClassInstanceField == null)
		{
			final Class<?> itemCompositionClazz = itemComposition.getClass();
			final ClassReader classReader = new ClassReader(itemCompositionClazz.getName());
			final ClassNode classNode = new ClassNode(Opcodes.ASM9);
			classReader.accept(classNode, ClassReader.SKIP_FRAMES);

			final MethodNode init = classNode.methods.stream()
				.filter(x -> x.name.equals("<init>"))
				.findFirst()
				.orElse(null);

			if (init == null)
			{
				log.error("getGroundItemEntityOps: <init> not found in ItemComposition");
				return null;
			}

			for (AbstractInsnNode ain : init.instructions)
			{
				if (ain instanceof FieldInsnNode && ain.getNext() != null && ain.getNext().getOpcode() == Opcodes.ICONST_2)
				{
					final FieldInsnNode fin = (FieldInsnNode) ain;
					final Field f = Class.forName(fin.owner).getDeclaredField(fin.name);
					f.setAccessible(true);
					entityOpsClassInstanceField = f;
				}
			}
		}

		if (entityOpsClassInstanceField == null)
		{
			log.error("getGroundItemEntityOps: entityOps instance field not found");
			return null;
		}

		final Object entityOpsClassInstance = entityOpsClassInstanceField.get(itemComposition);
		if (entityOpsClassInstance instanceof EntityOps)
		{
			return (EntityOps) entityOpsClassInstance;
		}

		return null;
	}

	// @TODO Test if this works as intended
	private static volatile Field baseXField;
	private static volatile Field baseYField;
	private static volatile Field viewportWalkingField;

	/**
	 * Sets the scene destination used by the game for viewport walking.
	 * Schedules execution on the client thread — safe to call from any thread.
	 */
	public static void setDestination(int baseX, int baseY)
	{
		// Resolve fields on the client thread, then mutate on the client thread.
		// Writing Scene fields from an off-thread context risks racing the game loop.
		Static.getClientThread().invoke(() ->
		{
			try
			{
				final Client client = Static.getClient();
				final WorldView worldView = client.getTopLevelWorldView();
				final Scene scene = worldView.getScene();

				if (baseXField == null || baseYField == null || viewportWalkingField == null)
				{
					final Class<? extends Scene> sceneClazz = scene.getClass();
					final ClassReader classReader = new ClassReader(sceneClazz.getName());
					final ClassNode classNode = new ClassNode(Opcodes.ASM9);
					classReader.accept(classNode, ClassReader.SKIP_FRAMES);

					for (MethodNode method : classNode.methods)
					{
						final InsnList instructions = method.instructions;
						if (baseXField == null && method.name.equals("getBaseX"))
						{
							final FieldInsnNode fin = (FieldInsnNode) instructions.get(3);
							log.info("baseX: {}.{}", fin.owner, fin.name);
							final Field f = sceneClazz.getDeclaredField(fin.name);
							f.setAccessible(true);
							baseXField = f;
						}
						if (baseYField == null && method.name.equals("getBaseY"))
						{
							final FieldInsnNode fin = (FieldInsnNode) instructions.get(3);
							log.info("baseY: {}.{}", fin.owner, fin.name);
							final Field f = sceneClazz.getDeclaredField(fin.name);
							f.setAccessible(true);
							baseYField = f;
						}
						if (viewportWalkingField == null && method.desc.equals("(Z)V"))
						{
							for (AbstractInsnNode insnNode : instructions)
							{
								if (insnNode.getOpcode() == Opcodes.ILOAD && insnNode.getNext() instanceof FieldInsnNode)
								{
									final FieldInsnNode fin = (FieldInsnNode) insnNode.getNext();
									log.info("viewportWalking: {}.{} {}", fin.owner, fin.name, fin.desc);
									final Field f = sceneClazz.getDeclaredField(fin.name);
									f.setAccessible(true);
									viewportWalkingField = f;
								}
							}
						}
					}
				}

				if (baseXField != null && baseYField != null && viewportWalkingField != null)
				{
					baseXField.setInt(scene, baseX);
					baseYField.setInt(scene, baseY);
					viewportWalkingField.setBoolean(scene, true);
				}
			}
			catch (IOException | NoSuchFieldException | IllegalAccessException e)
			{
				log.error("setDestination failed", e);
			}
		});
	}

	// @TODO Test if this works as intended
	private static volatile Method getHeadIconSpriteIndexMethod;

	public static HeadIcon getHeadIcon(NPCComposition npcComposition)
	{
		try
		{
			if (getHeadIconSpriteIndexMethod == null)
			{
				final Class<? extends NPCComposition> npcCompositionClazz = npcComposition.getClass();
				final ClassReader classReader = new ClassReader(npcCompositionClazz.getName());
				final ClassNode classNode = new ClassNode(Opcodes.ASM9);
				classReader.accept(classNode, ClassReader.SKIP_FRAMES);

				for (MethodNode method : classNode.methods)
				{
					if (method.desc.equals("(II)S"))
					{
						final Method resolved = npcCompositionClazz.getDeclaredMethod(method.name, int.class, int.class);
						resolved.setAccessible(true);
						getHeadIconSpriteIndexMethod = resolved;
						break;
					}
				}
			}

			if (getHeadIconSpriteIndexMethod == null)
			{
				log.error("getHeadIcon: method with descriptor (II)S not found in NPCComposition");
				return null;
			}

			final short headIconSpriteIndex = (short) getHeadIconSpriteIndexMethod.invoke(npcComposition, 0, 0);
			return HeadIcon.values()[headIconSpriteIndex];
		}
		catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
		{
			log.error("getHeadIcon failed", e);
		}
		return null;
	}

	public static volatile Method setPrintMenuActionsMethod;

	public static void setPrintMenuActions(boolean printMenuActions)
	{
		// Invoking a client method from off-thread risks racing the game loop.
		// Schedule on the client thread unconditionally.
		Static.getClientThread().invoke(() ->
		{
			try
			{
				final Client client = Static.getClient();
				if (setPrintMenuActionsMethod == null)
				{
					setPrintMenuActionsMethod = Arrays.stream(client.getClass().getDeclaredMethods())
						.filter(x -> x.getName().equals("setPrintMenuActions"))
						.findFirst()
						.orElse(null);
				}
				if (setPrintMenuActionsMethod == null)
				{
					log.error("setPrintMenuActions: method not found");
					return;
				}
				setPrintMenuActionsMethod.invoke(client, printMenuActions);
			}
			catch (IllegalAccessException | InvocationTargetException e)
			{
				log.error("setPrintMenuActions failed", e);
			}
		});
	}
}
