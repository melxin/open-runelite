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
package net.melxin.asm.transformer.unused;

import lombok.extern.slf4j.Slf4j;
import net.melxin.asm.transformer.JarEntryGroup;
import net.melxin.asm.transformer.JarEntryTransformer;
import net.melxin.asm.transformer.util.ASMUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ControlFlow implements JarEntryTransformer
{
	private static final Set<Integer> ARITHMETIC_OPCODES = Set.of(
		Opcodes.IADD, Opcodes.LADD,
		Opcodes.ISUB, Opcodes.LSUB,
		Opcodes.IMUL, Opcodes.LMUL,
		Opcodes.IDIV, Opcodes.LDIV,
		Opcodes.IREM, Opcodes.LREM,
		Opcodes.IAND, Opcodes.LAND,
		Opcodes.IOR, Opcodes.LOR,
		Opcodes.IXOR, Opcodes.LXOR,
		Opcodes.INEG, Opcodes.LNEG,
		Opcodes.ISHL, Opcodes.LSHL,
		Opcodes.ISHR, Opcodes.LSHR,
		Opcodes.IUSHR, Opcodes.LUSHR
	);

	private boolean isArithmeticOpcode(int opcode)
	{
		return ARITHMETIC_OPCODES.contains(opcode);
	}

	@Override
	public void transform(JarEntryGroup jarEntryGroup)
	{
		for (ClassNode classNode : jarEntryGroup.getClassNodes())
		{
			for (MethodNode method : classNode.methods)
			{
				boolean changed;
				do
				{
					// Save a copy of instructions for potential revert
					InsnList originalInstructions = cloneInstructions(method.instructions);

					changed = false;
					changed |= simplifyJumpChains(method);

					// Validate method
					if (!ASMUtils.isMethodValid(method))
					{
						// Revert to original instructions if invalid
						method.instructions = cloneInstructions(originalInstructions);
						// Mark changed as false to stop further optimization on invalid method
						changed = false;
						log.info("Reverted invalid modifications");
					}
				}
				while (changed);
			}
		}
	}

	private InsnList cloneInstructions(InsnList instructions)
	{
		if (instructions == null)
		{
			return new InsnList();
		}

		Map<LabelNode, LabelNode> labelMap = new HashMap<>();
		InsnList clone = new InsnList();

		// First pass: clone all label nodes
		for (AbstractInsnNode insn : instructions)
		{
			if (insn instanceof LabelNode)
			{
				LabelNode label = (LabelNode) insn;
				labelMap.put(label, new LabelNode());
			}
		}

		// Second pass: clone instructions with mapped labels
		for (AbstractInsnNode insn : instructions)
		{
			if (insn == null) continue;
			AbstractInsnNode insnClone = insn.clone(labelMap);
			if (insnClone == null)
			{
				log.warn("Cloned instruction is null");
				continue;
			}
			// For JumpInsnNode, ensure target is valid
			if (insnClone instanceof JumpInsnNode)
			{
				JumpInsnNode jump = (JumpInsnNode) insnClone;
				if (jump.label == null)
				{
					log.warn("Jump instruction with null label");
				}
			}
			clone.add(insnClone);
		}
		return clone;
	}

	private boolean simplifyJumpChains(MethodNode method)
	{
		boolean modified = false;
		InsnList instructions = method.instructions;
		for (AbstractInsnNode insn = instructions.getFirst(); insn != null; insn = insn.getNext())
		{
			if (insn instanceof JumpInsnNode)
			{
				JumpInsnNode jumpInsn = (JumpInsnNode) insn;
				LabelNode targetLabel = jumpInsn.label;

				// Find the instruction at the target label
				AbstractInsnNode targetInsn = targetLabel;
				while (targetInsn != null && targetInsn.getType() == AbstractInsnNode.LABEL)
				{
					targetInsn = targetInsn.getNext();
				}

				// Follow chain of jumps
				while (targetInsn instanceof JumpInsnNode)
				{
					JumpInsnNode targetJump = (JumpInsnNode) targetInsn;
					targetInsn = targetJump.label;
				}

				// Update jump if needed
				if (targetInsn instanceof LabelNode && targetInsn != jumpInsn.label)
				{
					jumpInsn.label = (LabelNode) targetInsn;
					modified = true;
					log.info("Modified jump");
				}
			}
		}
		return modified;
	}
}