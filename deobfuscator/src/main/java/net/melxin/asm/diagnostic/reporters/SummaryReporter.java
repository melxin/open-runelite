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
package net.melxin.asm.diagnostic.reporters;

import lombok.extern.slf4j.Slf4j;
import net.melxin.asm.diagnostic.DiagnosticJarEntryReporter;
import net.melxin.asm.transformer.JarEntryGroup;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

@Slf4j
public class SummaryReporter implements DiagnosticJarEntryReporter
{
	@Override
	public void report(JarEntryGroup jarEntryGroup)
	{
		int classCount = 0;
		int interfaceCount = 0;
		int enumCount = 0;
		int abstractClassCount = 0;
		int fieldCount = 0;
		int methodCount = 0;
		int constructorCount = 0;
		int classInitializerCount = 0;
		int nativeMethodCount = 0;
		int syntheticMethodCount = 0;
		int mainMethodCount = 0;

		for (ClassNode classNode : jarEntryGroup.getClassNodes())
		{
			classCount++;
			fieldCount += classNode.fields.size();
			methodCount += classNode.methods.size();

			if ((classNode.access & Opcodes.ACC_INTERFACE) != 0) interfaceCount++;
			if ((classNode.access & Opcodes.ACC_ENUM) != 0) enumCount++;
			if ((classNode.access & Opcodes.ACC_ABSTRACT) != 0
				&& (classNode.access & Opcodes.ACC_INTERFACE) == 0)
			{
				abstractClassCount++;
			}

			for (MethodNode methodNode : classNode.methods)
			{
				if (methodNode.name.equals("<init>")) constructorCount++;
				if (methodNode.name.equals("<clinit>")) classInitializerCount++;
				if ((methodNode.access & Opcodes.ACC_NATIVE) != 0) nativeMethodCount++;
				if ((methodNode.access & Opcodes.ACC_SYNTHETIC) != 0) syntheticMethodCount++;
				if (isMainMethod(methodNode))
				{
					mainMethodCount++;
					log.info("Main method: {}.{} {}", classNode.name, methodNode.name, methodNode.desc);
				}
			}
		}

		log.info("Classes: {} (interfaces: {}, enums: {}, abstract classes: {})", classCount, interfaceCount, enumCount, abstractClassCount);
		log.info("Members: {} fields, {} methods ({} constructors, {} class initializers)", fieldCount, methodCount, constructorCount, classInitializerCount);
		log.info("Methods: {} native, {} synthetic, {} main candidates", nativeMethodCount, syntheticMethodCount, mainMethodCount);
	}

	private static boolean isMainMethod(MethodNode method)
	{
		return method.name.equals("main")
			//&& "([Ljava/lang/String;)V".equals(method.desc)
			&& Type.getReturnType(method.desc).equals(Type.VOID_TYPE)
			&& Type.getArgumentTypes(method.desc).length == 1
			&& Type.getArgumentTypes(method.desc)[0].equals(
			Type.getType(String[].class))
			&& (method.access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC))
			== (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC);
	}
}