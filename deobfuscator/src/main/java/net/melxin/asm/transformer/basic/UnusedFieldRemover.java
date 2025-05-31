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
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

@Slf4j
public class UnusedFieldRemover implements JarEntryTransformer
{
	@Override
	public void transform(JarEntryGroup jarEntryGroup)
	{
		int removed = 0;
		final Set<String> used = new HashSet<>();
		final Set<ClassNode> classNodes = jarEntryGroup.getClassNodes();

		// Check for field usages
		for (ClassNode classNode : classNodes)
		{
			for (MethodNode method : classNode.methods)
			{
				for (AbstractInsnNode insn : method.instructions)
				{
					if (insn instanceof FieldInsnNode)
					{
						final FieldInsnNode fi = (FieldInsnNode) insn;
						used.add(fi.owner + "#" + fi.name + "#" + fi.desc);
					}
					else if (insn instanceof InvokeDynamicInsnNode)
					{
						final InvokeDynamicInsnNode invokeDynamicInsn = (InvokeDynamicInsnNode) insn;
						final Handle bsmHandle = invokeDynamicInsn.bsm;
						if (bsmHandle != null)
						{
							for (Object arg : invokeDynamicInsn.bsmArgs)
							{
								if (arg instanceof Handle)
								{
									final Handle handle = (Handle) arg;
									if (handle.getTag() >= Opcodes.H_GETFIELD && handle.getTag() <= Opcodes.H_PUTSTATIC)
									{
										used.add(handle.getOwner() + "#" + handle.getName() + "#" + handle.getDesc());
										log.debug("Field dynamic handle: {}.{} {} tag={}", handle.getOwner(), handle.getName(), handle.getDesc(), handle.getTag());
									}
								}
							}
						}
					}
					else if (insn instanceof LdcInsnNode && ((LdcInsnNode) insn).cst instanceof ConstantDynamic)
					{
						final ConstantDynamic cd = (ConstantDynamic) ((LdcInsnNode) insn).cst;
						for (int i = 0; i < cd.getBootstrapMethodArgumentCount(); i++)
						{
							final Object arg = cd.getBootstrapMethodArgument(i);
							if (arg instanceof Handle)
							{
								final Handle handle = (Handle) arg;
								if (handle.getTag() >= Opcodes.H_GETFIELD && handle.getTag() <= Opcodes.H_PUTSTATIC)
								{
									used.add(handle.getOwner() + "#" + handle.getName() + "#" + handle.getDesc());
									log.debug("Field constant dynamic handle: {}.{} {} tag={}", handle.getOwner(), handle.getName(), handle.getDesc(), handle.getTag());
								}
							}
						}
					}
				}
			}
		}

		// Check if field is unused and remove it
		for (ClassNode classNode : classNodes)
		{
			for (ListIterator<FieldNode> iter = classNode.fields.listIterator(); iter.hasNext(); )
			{
				final FieldNode field = iter.next();

				if ((classNode.access & Opcodes.ACC_INTERFACE) != 0
					|| (field.access & Opcodes.ACC_PROTECTED) != 0
					|| (field.access & Opcodes.ACC_STATIC) != 0 && (field.access & Opcodes.ACC_FINAL) != 0)
				{
					continue;
				}

				if (!used.contains(classNode.name + "#" + field.name + "#" + field.desc))
				{
					iter.remove();
					removed++;
					log.debug("Removed unused field: {} {}.{} {}", Modifier.toString(field.access), classNode.name, field.name, field.desc);
				}
			}
		}
		log.info("Removed: {} unused fields", removed);
	}
}