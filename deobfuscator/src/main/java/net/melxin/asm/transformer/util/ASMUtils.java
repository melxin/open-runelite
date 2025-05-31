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
package net.melxin.asm.transformer.util;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.BasicVerifier;
import org.objectweb.asm.util.Printer;
import java.util.ArrayDeque;
import java.util.Deque;

public class ASMUtils
{
	public static boolean isMethodVirtual(MethodNode method)
	{
		int access = method.access;
		if (method.name.equals("<init>") || method.name.equals("<clinit>"))
			return false;

		return (access & (Opcodes.ACC_ABSTRACT | Opcodes.ACC_STATIC | Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL)) == 0;
	}

	public static boolean isMethodValid(MethodNode method)
	{
		final Analyzer<BasicValue> analyzer = new Analyzer<>(new BasicVerifier());
		try
		{
			analyzer.analyze("owner", method);
			return true;
		}
		catch (AnalyzerException e)
		{
			return false;
		}
	}

	public static class ObjectStack<T>
	{
		private final Deque<T> stack = new ArrayDeque<>();

		public void push(T o)
		{
			stack.push(o);
		}

		public T pop()
		{
			return stack.isEmpty() ? null : stack.pop();
		}

		public T peek()
		{
			return stack.isEmpty() ? null : stack.peek();
		}

		public boolean isEmpty()
		{
			return stack.isEmpty();
		}

		public int size()
		{
			return stack.size();
		}

		public Deque<T> getStack()
		{
			return stack;
		}
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