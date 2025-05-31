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
import net.melxin.asm.transformer.JarEntryGroup;
import net.melxin.asm.transformer.util.ASMUtils;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import java.util.Set;

@Slf4j
public class RenameUnique implements Mapper
{
	@Override
	public void map(JarEntryGroup sourceGroup, JarEntryGroup targetGroup, Remapper remapper)
	{
		int renamedClasses = 0, renamedFields = 0, renamedMethods = 0;
		int totalClasses = 0, totalFields = 0, totalMethods = 0;

		int classIdx = 0, fieldIdx = 0, methodIdx = 0;
		final Set<ClassNode> classNodes = targetGroup.getClassNodes();
		for (ClassNode classNode : classNodes)
		{
			totalClasses++;
			if (targetGroup.isObfuscated(classNode.name))
			{
				remapper.mapClass(classNode, "class" + classIdx++);
				renamedClasses++;
			}

			for (FieldNode fieldNode : classNode.fields)
			{
				totalFields++;
				if (targetGroup.isObfuscated(fieldNode.name))
				{
					remapper.mapField(classNode, fieldNode, "field" + fieldIdx++);
					renamedFields++;
				}
			}

			for (MethodNode methodNode : classNode.methods)
			{
				totalMethods++;
				if (targetGroup.isObfuscated(methodNode.name))
				{
					if (ASMUtils.isMethodVirtual(methodNode))
					{
						remapper.mapMethod(classNode, methodNode, "vmethod" + methodIdx++);
						renamedMethods++;
					}
					else
					{
						remapper.mapMethod(classNode, methodNode, "method" + methodIdx++);
						renamedMethods++;
					}
				}
			}
		}
		log.info("Renamed: {} / {} classes | {} / {} fields | {} / {} methods", renamedClasses, totalClasses, renamedFields, totalFields, renamedMethods, totalMethods);
	}
}