/*
 * Copyright (c) 2024, Melxin <https://github.com/melxin/>
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
package net.runelite.utils;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class FieldUtils
{
	/**
	 * Find field multiplication
	 *
	 * Attempt to find field multiplication inside classNode provided
	 *
	 * @param classNode, attempt to find the field multiplication inside the classNode
	 * @param fieldInsnNode, the fieldInsnNode to find the multiplication for
	 * @param setter, true for setter, false for getter multiplication
	 * @return LdcInsnNode containing the getter or setter multiplication
	 */
	public static LdcInsnNode findFieldMultiplication(ClassNode classNode, FieldInsnNode fieldInsnNode, boolean setter)
	{
		for (MethodNode methodNode : classNode.methods)
		{
			for (AbstractInsnNode insnNode : methodNode.instructions)
			{
				if (setter && (insnNode.getOpcode() == Opcodes.PUTFIELD || insnNode.getOpcode() == Opcodes.PUTSTATIC)
					|| !setter && (insnNode.getOpcode() == Opcodes.GETFIELD || insnNode.getOpcode() == Opcodes.GETSTATIC))
				{
					FieldInsnNode targetFieldInsn = (FieldInsnNode) insnNode;
					if (targetFieldInsn.owner.equals(fieldInsnNode.owner) && targetFieldInsn.name.equals(fieldInsnNode.name) && targetFieldInsn.desc.equals(fieldInsnNode.desc))
					{
						return (insnNode.getPrevious().getOpcode() == Opcodes.IMUL || insnNode.getPrevious().getOpcode() == Opcodes.LMUL) && insnNode.getPrevious().getPrevious() instanceof LdcInsnNode ? (LdcInsnNode) insnNode.getPrevious().getPrevious()
							:
							(insnNode.getNext().getOpcode() == Opcodes.IMUL || insnNode.getNext().getOpcode() == Opcodes.LMUL) && insnNode.getPrevious() instanceof LdcInsnNode ? (LdcInsnNode) insnNode.getPrevious()
								:
								insnNode.getNext() instanceof LdcInsnNode && (insnNode.getNext().getNext().getOpcode() == Opcodes.IMUL || insnNode.getNext().getNext().getOpcode() == Opcodes.LMUL) ? (LdcInsnNode) insnNode.getNext()
									: null;
					}
				}
			}
		}
		return null;
	}
}