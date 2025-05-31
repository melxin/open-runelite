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
package net.melxin.asm.transformer.runelite;

import lombok.extern.slf4j.Slf4j;
import net.melxin.asm.transformer.JarEntryTransformer;
import net.melxin.asm.transformer.JarEntryGroup;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import java.util.ListIterator;

@Slf4j
public class RuneLiteNamedAnnotationsRemover implements JarEntryTransformer
{
	@Override
	public void transform(JarEntryGroup jarEntryGroup)
	{
		int removedFieldAnnotations = 0;
		int removedMethodAnnotations = 0;

		final String namedAnnotationDescriptor = "Ljavax/inject/Named;";
		for (ClassNode classNode : jarEntryGroup.getClassNodes())
		{
			final String className = classNode.name;
			if (className.length() > 3 && !className.equals("client") && !className.startsWith("com/jagex/") && !className.startsWith("rl"))
			{
				continue;
			}

			for (FieldNode field : classNode.fields)
			{
				if (field.invisibleAnnotations != null)
				{
					for (ListIterator<AnnotationNode> iter = field.invisibleAnnotations.listIterator(); iter.hasNext(); )
					{
						final AnnotationNode ian = iter.next();
						if (ian.desc.equals(namedAnnotationDescriptor) /*&& ian.values.get(1).toString().length() > 1000*/)
						{
							iter.remove();
							removedFieldAnnotations++;
						}
					}
				}
			}

			for (MethodNode method : classNode.methods)
			{
				if (method.invisibleAnnotations != null)
				{
					for (ListIterator<AnnotationNode> iter = method.invisibleAnnotations.listIterator(); iter.hasNext(); )
					{
						final AnnotationNode ian = iter.next();
						if (ian.desc.equals(namedAnnotationDescriptor) /*&& ian.values.get(1).toString().length() > 1000*/)
						{
							iter.remove();
							removedMethodAnnotations++;
						}
					}
				}
			}
		}
		log.info("Removed: {} named annotations", removedFieldAnnotations + removedMethodAnnotations);
	}
}