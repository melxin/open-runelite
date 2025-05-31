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
import net.melxin.asm.transformer.JarEntryGroup;
import net.melxin.asm.transformer.JarEntryTransformer;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

@Slf4j
public class RuneLiteAdjacentTryCatchBlockMerger implements JarEntryTransformer
{
	@Override
	public void transform(JarEntryGroup jarEntryGroup)
	{
		int merged = 0;
		for (ClassNode classNode : jarEntryGroup.getClassNodes())
		{
			for (MethodNode methodNode : classNode.methods)
			{
				for (int i = 0; i < methodNode.tryCatchBlocks.size(); i++)
				{
					TryCatchBlockNode a = methodNode.tryCatchBlocks.get(i);
					for (int j = i + 1; j < methodNode.tryCatchBlocks.size(); j++)
					{
						TryCatchBlockNode b = methodNode.tryCatchBlocks.get(j);

						if (a.handler != b.handler || !sameType(a.type, b.type))
						{
							continue;
						}

						if (a.start == null || a.end == null || b.start == null || b.end == null)
						{
							continue;
						}

						/*if (a.end == b.end || a.start == b.start)
						{
							methodNode.tryCatchBlocks.remove(j);
							merged++;
						}*/
						if (a.end == b.start)
						{
							a.end = b.end;
						}
						else if (b.end == a.start)
						{
							a.start = b.start;
						}
						methodNode.tryCatchBlocks.remove(j);
						merged++;
					}
				}
			}
		}
		log.info("Merged: {} adjacent try-catch ranges", merged);
	}

	private boolean sameType(String a, String b)
	{
		return a == null ? b == null : a.equals(b);
	}
}