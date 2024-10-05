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
package net.runelite.transformers.client;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Create client::createWidget
 */
@Slf4j
public class CreateWidgetTransformer implements ClassFileTransformer
{
	private static final Set<String> transformedClasses = ConcurrentHashMap.newKeySet();

	private static final String VANILLA_CLIENT_NAME = "client";

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classFileBuffer)
	{
		if (transformedClasses.contains(className) || !className.equals(VANILLA_CLIENT_NAME))
		{
			return classFileBuffer;
		}

		transformedClasses.add(className);

		final ClassReader reader = new ClassReader(classFileBuffer);
		final ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);

		Stopwatch stopWatch = Stopwatch.createStarted();

		final ClassNode classNode = new ClassNode();
		reader.accept(classNode, ClassReader.SKIP_FRAMES);

		final AtomicBoolean isInserted = new AtomicBoolean();

		reader.accept(new ClassVisitor(Opcodes.ASM9, writer)
		{
			@Override
			public void visitEnd()
			{
				super.visitEnd();

				MethodNode targetMethod = classNode.methods.stream()
					.filter(m -> m.access == Opcodes.ACC_PUBLIC && m.desc.equals("(Lnet/runelite/api/widgets/Widget;)V"))
					.findFirst()
					.orElseThrow(() -> new NoSuchElementException("Widget target method was not found"));

				for (AbstractInsnNode insnNode : targetMethod.instructions)
				{
					if (insnNode.getOpcode() == Opcodes.CHECKCAST)
					{
						TypeInsnNode checkCast = (TypeInsnNode) insnNode;
						log.info("Found widget class: {}", checkCast.desc);

						// Generate the createWidget method
						MethodVisitor mv = writer.visitMethod(Opcodes.ACC_PUBLIC, "createWidget", "()Lnet/runelite/api/widgets/Widget;", null, null);
						mv.visitCode();

						// var3 = new Widget(); // new ny();
						mv.visitTypeInsn(Opcodes.NEW, checkCast.desc); // Create a new instance
						mv.visitInsn(Opcodes.DUP); // Duplicate the top value on the stack (the new instance)
						mv.visitMethodInsn(Opcodes.INVOKESPECIAL, checkCast.desc, "<init>", "()V", false); // Call the constructor
						mv.visitVarInsn(Opcodes.ASTORE, 1); // Store the reference to var3 (local variable 1)

						// Return the widget instance
						mv.visitVarInsn(Opcodes.ALOAD, 1); // Load the widget instance from local variable 1
						mv.visitInsn(Opcodes.ARETURN); // Return the widget instance
						mv.visitMaxs(2, 2); // Set the max stack size and local variable size
						mv.visitEnd();

						isInserted.set(true);

						break;
					}
				}
			}
		}, ClassReader.SKIP_FRAMES);

		if (isInserted.get())
		{
			log.info("Took: {}", stopWatch);
		}
		return writer.toByteArray();
	}
}