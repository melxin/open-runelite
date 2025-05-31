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
import net.melxin.asm.transformer.util.ASMUtils;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

@Slf4j
public class UnusedMethodRemover implements JarEntryTransformer
{
	@Override
	public void transform(JarEntryGroup jarEntryGroup)
	{
		int removed = 0;
		final Set<String> used = new HashSet<>();
		final Set<ClassNode> classNodes = jarEntryGroup.getClassNodes();

		// Check for method usages
		for (ClassNode classNode : classNodes)
		{
			for (MethodNode method : classNode.methods)
			{
				if (method.name.equals("main") && method.desc.equals("([Ljava/lang/String;)V") && (method.access & Opcodes.ACC_PUBLIC) != 0 && (method.access & Opcodes.ACC_STATIC) != 0)
				{
					log.debug("main: {}.{} {}", classNode.name, method.name, method.desc);
					used.add(classNode.name + "#" + "main" + "#" + "([Ljava/lang/String;)V");
				}

				if (method.visibleAnnotations != null)
				{
					used.add(classNode.name + "#" + method.name + "#" + method.desc);
					/*for (AnnotationNode annotation : method.visibleAnnotations)
					{
					}*/
				}
				if (method.invisibleAnnotations != null)
				{
					for (AnnotationNode annotation : method.invisibleAnnotations)
					{
						if (annotation != null && annotation.desc != null && !annotation.desc.equals("Ljavax/inject/Named;"))
						{
							used.add(classNode.name + "#" + method.name + "#" + method.desc);
						}
						//log.debug("Invisible annotation: {} {}", annotation.desc, Arrays.toString(annotation.values.toArray()));
					}
				}

				for (AbstractInsnNode insn : method.instructions)
				{
					if (insn instanceof MethodInsnNode)
					{
						final MethodInsnNode methodInsn = (MethodInsnNode) insn;
						used.add(methodInsn.owner + "#" + methodInsn.name + "#" + methodInsn.desc);
					}
					else if (insn instanceof InvokeDynamicInsnNode)
					{
						final InvokeDynamicInsnNode invokeDynamicInsn = (InvokeDynamicInsnNode) insn;
						final Handle bootstrapHandle = invokeDynamicInsn.bsm;
						if (bootstrapHandle != null)
						{
							//used.add(classNode.name + "#" + method.name + "#" + method.desc);
							log.debug("Invoke dynamic with bootstrap: {}.{} in method: {}.{}", bootstrapHandle.getOwner(), bootstrapHandle.getOwner(), classNode.name, method.name);
							for (Object arg : invokeDynamicInsn.bsmArgs)
							{
								if (arg instanceof Handle)
								{
									final Handle handle = (Handle) arg;
									if (handle.getTag() >= Opcodes.H_INVOKEVIRTUAL && handle.getTag() <= Opcodes.H_NEWINVOKESPECIAL)
									{
										used.add(handle.getOwner() + "#" + handle.getName() + "#" + handle.getDesc());
										log.debug("Invoke dynamic handle: {}.{} {} tag={}", handle.getOwner(), handle.getName(), handle.getDesc(), handle.getTag());
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
								if (handle.getTag() >= Opcodes.H_INVOKEVIRTUAL && handle.getTag() <= Opcodes.H_NEWINVOKESPECIAL)
								{
									used.add(handle.getOwner() + "#" + handle.getName() + "#" + handle.getDesc());
									log.debug("Method constant dynamic handle: {}.{} {} tag={}", handle.getOwner(), handle.getName(), handle.getDesc(), handle.getTag());
								}
							}
						}
					}
				}
			}
		}

		// Check for reflection usages
		for (ClassNode classNode : classNodes)
		{
			for (MethodNode method : classNode.methods)
			{
				final ASMUtils.ObjectStack stack = new ASMUtils.ObjectStack();

				for (AbstractInsnNode insn : method.instructions)
				{
					if (insn instanceof LdcInsnNode)
					{
						final Object cst = ((LdcInsnNode) insn).cst;
						if (cst instanceof String || cst instanceof Type)
						{
							stack.push(cst);
						}
					}
					else if (insn instanceof MethodInsnNode)
					{
						final MethodInsnNode mInsn = (MethodInsnNode) insn;
						final String owner = mInsn.owner;
						final String name = mInsn.name;

						if (owner.equals("java/lang/Class") && name.equals("forName"))
						{
							final String className = (String) stack.pop();
							if (className != null)
							{
								used.add(className.replace('/', '.') + "#" + "<clinit>" + "#" + "?");
							}
						}
						else if (owner.equals("java/lang/Class") && (name.equals("getMethod") || name.equals("getDeclaredMethod") || name.equals("getDeclaredMethods")))
						{
							final String methodName = (String) stack.pop();
							final Type type = (Type) stack.pop();
							final String className = type != null ? type.getClassName() : classNode.name;
							if (methodName != null)
							{
								used.add(className + "#" + methodName + "#" + "?");
							}
						}
						else if (owner.equals("java/lang/reflect/Method") && (name.equals("invoke") || name.equals("get") || name.equals("set")))
						{
							used.add(classNode.name + "#" + method.name + "#" + method.desc);
						}
						else if (owner.equals("java/lang/reflect/Constructor") && name.equals("newInstance"))
						{
							used.add(classNode.name + "#" + method.name + "#" + method.desc);
						}
						else if (owner.equals("java/lang/reflect/Field") && (name.equals("get") || name.equals("set")))
						{
							used.add(classNode.name + "#" + method.name + "#" + method.desc);
						}
						else if (owner.equals("java/lang/reflect/Proxy") && name.equals("newProxyInstance"))
						{
							used.add(classNode.name + "#" + method.name + "#" + "?");
						}
					}
				}
			}
		}

		// Check if method is unused and remove it
		for (ClassNode classNode : classNodes)
		{
			for (ListIterator<MethodNode> iter = classNode.methods.listIterator(); iter.hasNext(); )
			{
				final MethodNode method = iter.next();

				if (!jarEntryGroup.isObfuscated(method.name) && !method.name.equals("<init>"))
				{
					continue;
				}

				if ((classNode.access & Opcodes.ACC_INTERFACE) != 0
					|| (method.access & Opcodes.ACC_PROTECTED) != 0
					|| (method.access & Opcodes.ACC_ABSTRACT) != 0
					|| (method.access & Opcodes.ACC_SYNTHETIC) != 0
					|| (method.access & Opcodes.ACC_NATIVE) != 0
					|| (method.access & Opcodes.ACC_BRIDGE) != 0
					|| method.name.equals("<init>")
					|| method.name.equals("<clinit>")
					|| method.name.length() > 3)
				{
					continue;
				}

				if (used.contains(classNode.name + "#" + method.name + "#" + method.desc)
					|| used.contains(classNode.name + "#" + method.name + "#" + "?"))
				{
					continue;
				}

				iter.remove();
				removed++;
				log.debug("Removed unused method: {} {}.{} {}", Modifier.toString(method.access), classNode.name, method.name, method.desc);
			}
		}
		log.info("Removed: {} unused methods", removed);
	}
}