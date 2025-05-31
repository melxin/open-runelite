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
import net.melxin.asm.transformer.JarEntryGroup;
import net.melxin.asm.transformer.JarEntryTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

@Slf4j
public class IllegalStateExceptionRemover implements JarEntryTransformer
{
	@Override
	public void transform(JarEntryGroup jarEntryGroup)
	{
		int totalRemovedSequences = 0;
		for (ClassNode classNode : jarEntryGroup.getClassNodes())
		{
			for (MethodNode method : classNode.methods)
			{
				final InsnList instructions = method.instructions;

				for (AbstractInsnNode insn = instructions.getFirst(); insn != null; insn = insn.getNext())
				{
					if (insn.getOpcode() >= Opcodes.IFEQ && insn.getOpcode() <= Opcodes.IF_ACMPEQ)
					{
						if (insn.getNext().getOpcode() == Opcodes.NEW && insn.getNext() instanceof TypeInsnNode)
						{
							final TypeInsnNode newInsn = (TypeInsnNode) insn.getNext();

							if ("java/lang/IllegalStateException".equals(newInsn.desc))
							{
								// Mark the IF instruction for removal
								//instructions.remove(insn);

								// Remove the NEW instruction
								instructions.remove(newInsn);

								// Traverse and remove until ATHROW
								AbstractInsnNode current = newInsn.getNext();
								while (current != null && current.getOpcode() != Opcodes.ATHROW)
								{
									instructions.remove(current);
									current = current.getNext();
								}

								// Remove ATHROW if found
								if (current != null && current.getOpcode() == Opcodes.ATHROW)
								{
									instructions.remove(current);
								}

								// Insert GOTO
								if (insn.getPrevious() != null && insn.getPrevious().getPrevious() != null && insn.getPrevious().getPrevious().getOpcode() == Opcodes.ILOAD)
								{
									instructions.insertBefore(insn.getPrevious().getPrevious(), new JumpInsnNode(Opcodes.GOTO, ((JumpInsnNode) insn).label));
								}
								else
								{
									instructions.insert(insn, new JumpInsnNode(Opcodes.GOTO, ((JumpInsnNode) insn).label));
								}

								totalRemovedSequences++;
							}
						}
					}
				}
			}
		}
		log.info("Total sequences replaced with goto: {}", totalRemovedSequences);
	}
}