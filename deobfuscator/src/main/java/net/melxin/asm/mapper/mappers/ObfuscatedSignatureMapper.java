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
package net.melxin.asm.mapper.mappers;

import lombok.extern.slf4j.Slf4j;
import net.melxin.asm.mapper.Mapper;
import net.melxin.asm.mapper.Remapper;
import net.melxin.asm.mapper.annotations.AnnotationAdder;
import net.melxin.asm.transformer.JarEntryGroup;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

@Slf4j
public class ObfuscatedSignatureMapper implements Mapper
{
	@Override
	public void map(JarEntryGroup sourceGroup, JarEntryGroup targetGroup, Remapper remapper)
	{
		int fieldAnnotationCount = 0;
		int methodAnnotationCount = 0;
		for (ClassNode classNode : targetGroup.getClassNodes())
		{
			for (FieldNode field : classNode.fields)
			{
				if (!targetGroup.isObfuscated(field.name))
				{
					continue;
				}
				//log.info("Field: {}.{} {}", classNode.name, field.name, field.desc);
				if (field.desc.startsWith("L") && field.desc.endsWith(";") && field.desc.length() <= 5)
				{
					AnnotationAdder.addObfuscatedSignatureAnnotation(field, field.desc);
					fieldAnnotationCount++;
				}
			}

			for (MethodNode method : classNode.methods)
			{
				if (!targetGroup.isObfuscated(method.name))
				{
					continue;
				}
				//log.info("method: {}.{} {}", classNode.name, method.name, method.desc);
				AnnotationAdder.addObfuscatedSignatureAnnotation(method, method.desc, null);
				methodAnnotationCount++;
			}
		}

		log.info("Added: {} field | {} method obfuscated signature annotations", fieldAnnotationCount, methodAnnotationCount);
	}
}
