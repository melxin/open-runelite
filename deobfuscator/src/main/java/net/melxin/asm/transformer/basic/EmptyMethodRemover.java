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
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import java.util.Iterator;

@Slf4j
public class EmptyMethodRemover implements JarEntryTransformer
{
	@Override
	public void transform(JarEntryGroup jarEntryGroup)
	{
		int removed = 0;
		for (ClassNode classNode : jarEntryGroup.getClassNodes())
		{
			Iterator<MethodNode> iterator = classNode.methods.iterator();

			outer:
			while (iterator.hasNext())
			{
				MethodNode method = iterator.next();

				if ((method.access & (Opcodes.ACC_ABSTRACT | Opcodes.ACC_NATIVE)) != 0)
				{
					continue;
				}

				if ("<init>".equals(method.name) || "<clinit>".equals(method.name))
				{
					continue;
				}

				if (method.instructions.size() > 0)
				{
					continue;
				}

				/*for (AbstractInsnNode ain : method.instructions)
				{
					if (ain.getOpcode() >= 0)
					{
						continue outer;
					}
				}*/

				iterator.remove();
				removed++;
			}
		}
		log.info("Removed: {} empty methods", removed);
	}
}