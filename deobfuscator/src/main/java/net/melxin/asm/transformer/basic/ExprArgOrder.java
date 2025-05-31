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
package net.melxin.asm.transformer.basic;

import lombok.extern.slf4j.Slf4j;
import net.melxin.asm.transformer.JarEntryTransformer;
import net.melxin.asm.transformer.JarEntryGroup;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

@Slf4j
public class ExprArgOrder implements JarEntryTransformer
{
	private int reorderedCount;

	@Override
	public void transform(JarEntryGroup jarEntryGroup)
	{
		for (ClassNode classNode : jarEntryGroup.getClassNodes())
		{
			for (MethodNode method : classNode.methods)
			{
				reorderGetFields(method);
				reorderPutFields(method);
			}
		}
		log.info("Reordered {} constants", reorderedCount);
	}

	/**
	 * LDC * FIELD -> FIELD * LDC
	 */
	private void reorderGetFields(MethodNode method)
	{
		final InsnList instructions = method.instructions;
		for (AbstractInsnNode insn : instructions)
		{
			if (insn instanceof FieldInsnNode)
			{
				final FieldInsnNode fin = (FieldInsnNode) insn;
				if (!fin.desc.equals("I") && !fin.desc.equals("J"))
				{
					continue;
				}

				if (insn.getNext() != null && (insn.getNext().getOpcode() == Opcodes.IMUL || insn.getNext().getOpcode() == Opcodes.LMUL))
				{
					LdcInsnNode ldc = null;

					if (insn.getPrevious() == null)
					{
						continue;
					}

					if (insn.getPrevious() instanceof LdcInsnNode)
					{
						ldc = (LdcInsnNode) insn.getPrevious();
					}
					else if (insn.getPrevious().getPrevious() != null && insn.getPrevious().getPrevious() instanceof LdcInsnNode)
					{
						ldc = (LdcInsnNode) insn.getPrevious().getPrevious();
					}

					if (ldc != null)
					{
						instructions.remove(ldc);
						instructions.insert(insn, ldc);
						reorderedCount++;
					}
				}
			}
		}
	}

	/**
	 * ILOAD, LDC, IMUL PUTFIELD -> LDC, ILOAD, IMUL, PUTFIELD
	 */
	private void reorderPutFields(MethodNode method)
	{
		final InsnList instructions = method.instructions;
		for (AbstractInsnNode insn : instructions)
		{
			if (insn.getOpcode() == Opcodes.LDC
				&& insn.getNext() != null && insn.getNext().getOpcode() == Opcodes.ILOAD
				&& insn.getNext().getNext() != null && insn.getNext().getNext().getOpcode() == Opcodes.IMUL
				&& insn.getNext().getNext().getNext() != null && insn.getNext().getNext().getNext().getOpcode() == Opcodes.PUTFIELD)
			{
				final AbstractInsnNode ldc = insn;
				final AbstractInsnNode iload = insn.getNext();
				instructions.remove(ldc);
				instructions.insert(iload, ldc);
				reorderedCount++;
				continue;
			}
		}
	}
}