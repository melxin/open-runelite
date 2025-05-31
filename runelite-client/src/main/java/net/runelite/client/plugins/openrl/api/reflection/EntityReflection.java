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
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2NPC;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Player;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2TileItem;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2TileObject;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.api.SceneEntity;

@Slf4j
public class EntityReflection
{
	public static void setEntityAtMouse(SceneEntity sceneEntity)
	{
		Static.getClientThread().invoke(() ->
		{
			long tag = -1;
			if (sceneEntity instanceof RS2NPC)
			{
				final RS2NPC npc = (RS2NPC) sceneEntity;
				tag = calculateTag(0, 0, 0, 1, Objects.requireNonNull(npc.getComposition()).isInteractible(), npc.getIndex(), npc.getWorldView().getId());
			}
			else if (sceneEntity instanceof RS2Player)
			{
				final RS2Player player = (RS2Player) sceneEntity;
				tag = calculateTag(0, 0, 0, 0, false, player.getId(), player.getWorldView().getId());
			}
			else if (sceneEntity instanceof RS2TileObject)
			{
				final RS2TileObject tileObject = (RS2TileObject) sceneEntity;
				tag = tileObject.getHash();
			}
			else if (sceneEntity instanceof RS2TileItem)
			{
				final RS2TileItem tileItem = (RS2TileItem) sceneEntity;
				tag = calculateTag(tileItem.getWorldX(), tileItem.getWorldY(), 0, 3, false, 0, Static.getClient().getTopLevelWorldView().getId());
			}

			if (tag == -1)
			{
				log.error("Invalid tag for entity: {}", sceneEntity.getName());
				return;
			}
			setEntityAtMouse(tag);
		});
	}

	private static Method calculateTag;

	@SneakyThrows
	public static long calculateTag(int arg0, int arg1, int arg2, int arg3, boolean arg4, int arg5, int arg6)
	{
		if (calculateTag == null)
		{
			final Class<?> clientClazz = Static.getClient().getClass();
			calculateTag = Arrays.stream(Class.forName(clientClazz.getName()).getDeclaredMethods())
				.filter(m -> (m.getModifiers() & Modifier.STATIC) != 0)
				.filter(m -> m.getReturnType() == long.class)
				.filter(m -> m.getParameterCount() == 7)
				.filter(m ->
				{
					final Class<?>[] paramTypes = m.getParameterTypes();
					return paramTypes.length == 7 &&
						paramTypes[0] == int.class &&
						paramTypes[1] == int.class &&
						paramTypes[2] == int.class &&
						paramTypes[3] == int.class &&
						paramTypes[4] == boolean.class &&
						paramTypes[5] == int.class &&
						paramTypes[6] == int.class;
				})
				.findFirst()
				.orElse(null);
		}

		if (calculateTag == null)
		{
			log.error("Calculate tag method is broken!");
			return -1;
		}

		calculateTag.setAccessible(true);
		final long tag = (long) calculateTag.invoke(null, arg0, arg1, arg2, arg3, arg4, arg5, arg6);
		calculateTag.setAccessible(false);
		return tag;
	}

	private static Field entitiesAtMouse;
	private static Field entitiesAtMouseCount;
	private static Object entitiesAtMouseCountGetterMultiplier;
	private static Object entitiesAtMouseCountSetterMultiplier;

	@SneakyThrows
	public static void setEntityAtMouse(long entityTag)
	{
		if (entitiesAtMouse == null || entitiesAtMouseCount == null || entitiesAtMouseCountGetterMultiplier == null || entitiesAtMouseCountSetterMultiplier == null)
		{
			final Class<?> clientClazz = Static.getClient().getClass();
			final ClassReader classReader = new ClassReader(clientClazz.getName());
			final ClassNode classNode = new ClassNode(Opcodes.ASM9);
			classReader.accept(classNode, ClassReader.SKIP_FRAMES);
			for (MethodNode method : classNode.methods)
			{
				if (method == null
					|| method.instructions == null
					|| (method.access & Modifier.STATIC) == 0
					|| !method.desc.equals("(JI)V"))
				{
					continue;
				}

				for (AbstractInsnNode insn : method.instructions)
				{
					if (entitiesAtMouseCountSetterMultiplier == null && insn.getOpcode() == Opcodes.IADD && insn.getNext() instanceof LdcInsnNode)
					{
						entitiesAtMouseCountSetterMultiplier = ((LdcInsnNode) insn.getNext()).cst;
					}
					else if (insn instanceof FieldInsnNode && insn.getOpcode() == Opcodes.GETSTATIC
						&& insn.getNext() instanceof FieldInsnNode && insn.getNext().getOpcode() == Opcodes.GETSTATIC
						&& insn.getNext().getNext() instanceof LdcInsnNode)
					{
						final FieldInsnNode fi1 = (FieldInsnNode) insn;
						final FieldInsnNode fi2 = (FieldInsnNode) insn.getNext();
						final LdcInsnNode ldc = (LdcInsnNode) insn.getNext().getNext();

						final Class<?> clazz = Class.forName(fi1.owner);
						entitiesAtMouse = clazz.getDeclaredField(fi1.name);
						entitiesAtMouseCount = clazz.getDeclaredField(fi2.name);
						entitiesAtMouseCountGetterMultiplier = ldc.cst;
						//log.info("found!: {} & {}", fi1.name, fi2.name);
						break;
					}
				}
			}
		}

		if (entitiesAtMouse == null || entitiesAtMouseCount == null || entitiesAtMouseCountGetterMultiplier == null || entitiesAtMouseCountSetterMultiplier == null)
		{
			log.error("Set entity at mouse method is broken!");
			return;
		}

		//log.info("entity method: {}", entitiesAtMouse.getName());
		entitiesAtMouse.setAccessible(true);
		entitiesAtMouseCount.setAccessible(true);
		final long[] entities = (long[]) entitiesAtMouse.get(null);
		final int count = (int) entitiesAtMouseCount.get(null) * (int) entitiesAtMouseCountGetterMultiplier;
		if (count < 1000)
		{
			//entities[count] = entity.getTag();
			entities[count] = entityTag;
			entitiesAtMouse.set(null, entities);
			entitiesAtMouseCount.set(null, (count + 1) * (int) entitiesAtMouseCountSetterMultiplier);
		}
		entitiesAtMouse.setAccessible(false);
		entitiesAtMouseCount.setAccessible(false);
	}
	/*public void setHoveredEntity(SceneEntity entity)
	{
		if (entity != null)
		{
			long[] entitiesAtMouse = client.getEntitiesAtMouse();
			int count = client.getEntitiesAtMouseCount();
			if (count < 1000)
			{
				entitiesAtMouse[count] = entity.getTag();
				client.setEntitiesAtMouseCount(count + 1);
			}
		}
	}*/
}