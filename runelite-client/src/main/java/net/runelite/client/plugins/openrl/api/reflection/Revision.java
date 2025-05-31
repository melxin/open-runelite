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
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;
import net.runelite.client.plugins.openrl.Static;

@Slf4j
public class Revision
{
	private static int revision = -1;

	@SneakyThrows
	public static int get()
	{
		if (revision == -1)
		{
			final Class<?> clientClazz = Static.getClient().getClass();
			final ClassReader classReader = new ClassReader(clientClazz.getName());
			final ClassNode classNode = new ClassNode(Opcodes.ASM9);
			classReader.accept(classNode, ClassReader.SKIP_FRAMES);

			final MethodNode method = classNode.methods.stream().filter(x -> x.name.equals("init") && x.desc.equals("()V")).findFirst().orElse(null);
			if (method == null)
			{
				log.error("No init method present!");
				return -1;
			}

			final InsnList ins = method.instructions;
			for (int i = ins.size() - 1; i >= 0; i--)
			{
				if (i - 2 < 0)
				{
					break;
				}

				final AbstractInsnNode current = ins.get(i);
				final AbstractInsnNode prev1 = ins.get(i - 1);
				final AbstractInsnNode prev2 = ins.get(i - 2);

				if (current.getOpcode() == Opcodes.SIPUSH &&
					prev1.getOpcode() == Opcodes.SIPUSH && ((IntInsnNode) prev1).operand == 503
					&& prev2.getOpcode() == Opcodes.SIPUSH && ((IntInsnNode) prev2).operand == 765)
				{
					revision = ((IntInsnNode) current).operand;
					break;
				}
			}
		}
		return revision;
	}
}