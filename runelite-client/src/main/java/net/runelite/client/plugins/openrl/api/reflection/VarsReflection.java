/*
 * Copyright (c) 2026, Melxin <https://github.com/melxin>
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
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import java.lang.reflect.Field;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.utils.ASMUtils;

@Slf4j
public class VarsReflection
{
	private static Field varpLongBooleanArray;

	@SneakyThrows
	public static boolean isVarpLong(int varpId)
	{
		if (varpLongBooleanArray == null)
		{
			final Class<?> clientClazz = Static.getClient().getClass();
			final ClassNode classNode = ASMUtils.getClassNode(clientClazz.getName());
			final MethodNode method = classNode.methods.stream()
				.filter(x -> x.name.equals("getVarpValue") && x.desc.equals("(I)I"))
				.findFirst()
				.orElse(null);

			if (method == null)
			{
				log.error("Method getVarpValue is not present!");
				return false;
			}

			final InsnList ins = method.instructions;
			for (int i = 0; i < ins.size(); i++)
			{
				AbstractInsnNode ain = ins.get(i);
				if (ain.getOpcode() == Opcodes.GETSTATIC)
				{
					FieldInsnNode fi = (FieldInsnNode) ain;
					if (fi.desc.equals("[Z"))
					{
						varpLongBooleanArray = Class.forName(fi.owner).getDeclaredField(fi.name);
						varpLongBooleanArray.setAccessible(true);
						break;
					}
				}
			}
		}

		if (varpLongBooleanArray == null)
		{
			log.error("Unable to find varpLongBooleanArray");
			return false;
		}

		//varpLongBooleanArray.setAccessible(true);
		final boolean[] isVarpLongValue = (boolean[]) varpLongBooleanArray.get(null);
		//varpLongBooleanArray.setAccessible(false);

		return isVarpLongValue[varpId];
	}
}