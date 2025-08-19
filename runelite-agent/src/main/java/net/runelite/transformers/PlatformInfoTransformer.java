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
package net.runelite.transformers;

import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Transform vanilla platform info
 */
@Slf4j
public class PlatformInfoTransformer implements ClassFileTransformer
{
	private static final int OS_VALUE = 1; // WINDOWS=1, MAC_OS=2, LINUX=3, OTHER=4 // arg0, var1
	private static final boolean ARCH_64 = true; // arg1, var2
	private static final int OS_VERSION = 11; // arg2, var3

	private static final int JAVA_VENDOR = 4; // SUN=1, MICROSOFT=2, APPLE=3, ORACLE=5, OTHER=4 // arg3, var4
	private static final int JAVA_MAJOR = 11; // arg4, var5
	private static final int JAVA_MINOR = 0; // arg5, var6
	private static final int JAVA_PATCH = 28; // arg6, var7

	private static final int MAX_MEMORY = 3949; // arg8, var9

	private static final int CPU_CORES = 4; // arg9, var10
	private static final int CLOCK_SPEED = 0; // arg11, var12

	//private final String RL$DEV_BUILD = "idea_rt.jar"; // arg25, var26

	private static final Set<String> transformedClasses = ConcurrentHashMap.newKeySet();

	private String targetClassName;

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classFileBuffer)
	{
		if (transformedClasses.contains(className) || className.length() > 2 && !className.equals("client"))
		{
			return classFileBuffer;
		}

		transformedClasses.add(className);

		final ClassReader reader = new ClassReader(classFileBuffer);
		final ClassNode classNode = new ClassNode(Opcodes.ASM9);
		reader.accept(classNode, ClassReader.SKIP_FRAMES);

		final List<MethodNode> methods = classNode.methods;
		for (MethodNode method : methods)
		{
			if (method.name.equals("<init>") && method.desc.equals("(IZIIIIIZIIIILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IIIILjava/lang/String;Ljava/lang/String;[IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"))
			{
				this.targetClassName = className;
				log.info("Found PlatformInfo: {}.{}", className, method.name);

				final InsnList instructions = method.instructions;
				for (ListIterator<AbstractInsnNode> iterator = instructions.iterator(); iterator.hasNext(); )
				{
					final AbstractInsnNode insn = iterator.next();
					if (insn.getOpcode() == Opcodes.ILOAD)
					{
						final VarInsnNode iLoad = (VarInsnNode) insn;
						if (iLoad.var == 1)
						{
							iterator.set(new LdcInsnNode(OS_VALUE));
						}
						else if (iLoad.var == 2)
						{
							iterator.set(new LdcInsnNode(ARCH_64));
						}
						else if (iLoad.var == 3)
						{
							iterator.set(new LdcInsnNode(OS_VERSION));
						}
						else if (iLoad.var == 4)
						{
							iterator.set(new LdcInsnNode(JAVA_VENDOR));
						}
						else if (iLoad.var == 5)
						{
							iterator.set(new LdcInsnNode(JAVA_MAJOR));
						}
						else if (iLoad.var == 6)
						{
							iterator.set(new LdcInsnNode(JAVA_MINOR));
						}
						else if (iLoad.var == 7)
						{
							iterator.set(new LdcInsnNode(JAVA_PATCH));
						}
						else if (iLoad.var == 9)
						{
							iterator.set(new LdcInsnNode(MAX_MEMORY));
						}
						else if (iLoad.var == 10)
						{
							iterator.set(new LdcInsnNode(CPU_CORES));
						}
						else if (iLoad.var == 12)
						{
							iterator.set(new LdcInsnNode(CLOCK_SPEED));
						}
						/*else if (iLoad.var == 26)
						{
							iterator.set(new LdcInsnNode(RL$DEV_BUILD));
						}*/
					}
				}
			}
		}

		if (targetClassName != null && targetClassName.equals(className))
		{
			log.info("Transformed PlatformInfo class: {}", className);
			final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
			classNode.accept(writer);
			return writer.toByteArray();
		}

		return classFileBuffer;
	}
}