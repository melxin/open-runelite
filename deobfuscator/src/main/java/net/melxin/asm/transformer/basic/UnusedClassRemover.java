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
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class UnusedClassRemover implements JarEntryTransformer
{
	@Override
	public void transform(JarEntryGroup jarEntryGroup)
	{
		int removed = 0;
		final Set<ClassNode> unused = new HashSet<>();
		final Set<ClassNode> classNodes = jarEntryGroup.getClassNodes();

		// Check for class usages
		for (ClassNode classNode : classNodes)
		{
			if (!classNode.fields.isEmpty())
			{
				continue;
			}

			if (!classNode.methods.isEmpty())
			{
				continue;
			}

			if ((classNode.access & Opcodes.ACC_INTERFACE) != 0)
			{
				continue;
			}

			if (isImplemented(classNodes, classNode))
			{
				continue;
			}

			unused.add(classNode);

			for (MethodNode method : classNode.methods)
			{
				for (AbstractInsnNode insn : method.instructions)
				{
					if (insn instanceof FieldInsnNode)
					{
						final FieldInsnNode fin = (FieldInsnNode) insn;
						unused.removeIf(x -> x.name.equals(fin.owner));
					}
					else if (insn instanceof MethodInsnNode)
					{
						final MethodInsnNode min = (MethodInsnNode) insn;
						unused.removeIf(x -> x.name.equals(min.owner));
					}
					else if (insn instanceof TypeInsnNode)
					{
						final TypeInsnNode tin = (TypeInsnNode) insn;
						unused.removeIf(x -> x.name.equals(tin.desc.replace('/', '.')));
					}
					else if (insn instanceof LdcInsnNode && ((LdcInsnNode) insn).cst instanceof Type)
					{
						final Type type = (Type) ((LdcInsnNode) insn).cst;
						unused.removeIf(x -> x.name.equals(type.getClassName()));
					}
				}
			}
		}

		// Check if class is unused and remove it
		for (ClassNode classNode : unused)
		{
			classNodes.remove(classNode);
			removed++;
			log.debug("Removed unused class: {}", classNode.name);
		}
		log.info("Removed: {} unused classes", removed);
	}

	private boolean isImplemented(Set<ClassNode> classNodes, ClassNode iface)
	{
		for (ClassNode classNode : classNodes)
		{
			if (classNode.interfaces.contains(iface.name))
			{
				return true;
			}
		}
		return false;
	}
}