/*
 * Copyright (c) 2025, Melxin <https://github.com/melxin>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.openrl.utils;

import com.google.common.reflect.ClassPath;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.util.Printer;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import net.runelite.client.plugins.openrl.Static;

@Slf4j
public class ASMUtils
{
	private static Set<ClassNode> classNodes = new HashSet<>();

	@SneakyThrows
	public static Set<ClassNode> getClassNodes()
	{
		if (classNodes == null || classNodes.isEmpty())
		{
			final Set<String> classNames = ClassPath.from(Static.getClient().getClass().getClassLoader())
				.getAllClasses()
				.stream()
				.filter(clazz -> clazz.getPackageName().equalsIgnoreCase(Static.getClient().getClass().getPackageName()))
				.filter(clazz -> clazz.getName().length() <= 3 || clazz.getName().equals("client"))
				.map(clazz -> clazz.getName())
				.collect(Collectors.toSet());

			for (String className : classNames)
			{
				final ClassReader classReader = new ClassReader(className);
				final ClassNode classNode = new ClassNode(Opcodes.ASM9);
				classReader.accept(classNode, ClassReader.SKIP_FRAMES);
				classNodes.add(classNode);
			}
		}
		return classNodes;
	}

	public static String getOpcodeName(int opcode)
	{
		return (opcode >= 0 && opcode < Printer.OPCODES.length) ? Printer.OPCODES[opcode] : "UNKNOWN";
	}

	public static String insnToString(AbstractInsnNode insn)
	{
		return insn.getClass().getSimpleName() + " " + getOpcodeName(insn.getOpcode());
	}

	public static int getIntValue(AbstractInsnNode insnNode)
	{
		final int opcode = insnNode.getOpcode();
		switch (opcode)
		{
			case Opcodes.ICONST_M1:
				return -1;
			case Opcodes.ICONST_0:
				return 0;
			case Opcodes.ICONST_1:
				return 1;
			case Opcodes.ICONST_2:
				return 2;
			case Opcodes.ICONST_3:
				return 3;
			case Opcodes.ICONST_4:
				return 4;
			case Opcodes.ICONST_5:
				return 5;
			case Opcodes.BIPUSH:
			case Opcodes.SIPUSH:
				if (insnNode instanceof IntInsnNode)
				{
					return ((IntInsnNode) insnNode).operand;
				}
				break;
			case Opcodes.LDC:
				if (insnNode instanceof LdcInsnNode)
				{
					final Object cst = ((LdcInsnNode) insnNode).cst;
					if (cst instanceof Integer)
					{
						return (Integer) cst;
					}
				}
				break;
		}
		return -1;
	}
}