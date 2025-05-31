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
package net.melxin.asm.mapper;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.melxin.asm.mapper.mappers.FieldMultiplicationMapper;
import net.melxin.asm.mapper.mappers.MethodGarbageValueMapper;
import net.melxin.asm.mapper.mappers.ObfuscatedSignatureMapper;
import net.melxin.asm.mapper.mappers.RenameUnique;
import net.melxin.asm.mapper.mappers.RuneLiteAPIClassMapper;
import net.melxin.asm.transformer.JarEntryGroup;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.tree.ClassNode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

@Slf4j
public class Mappings
{
	@Nullable
	private final JarEntryGroup sourceGroup;

	@NonNull
	private final JarEntryGroup targetGroup;

	@NonNull
	private final Remapper remapper;

	public Mappings(@Nullable JarEntryGroup sourceGroup, @NonNull JarEntryGroup targetGroup)
	{
		this.sourceGroup = sourceGroup;
		this.targetGroup = targetGroup;
		this.remapper = new Remapper();
	}

	public void map()
	{
		mappers.forEach(mapper ->
		{
			log.debug("Running mapper: {}", mapper.name());
			mapper.map(sourceGroup, targetGroup, remapper);
		});

		final Set<ClassNode> classNodes = targetGroup.getClassNodes();
		final Set<ClassNode> remappedClassNodes = new HashSet<>();
		for (ClassNode classNode : classNodes)
		{
			ClassNode remappedClassNode = new ClassNode(Opcodes.ASM9);
			classNode.accept(new ClassRemapper(remappedClassNode, remapper.remapper));
			remappedClassNodes.add(remappedClassNode);
		}
		classNodes.clear();
		classNodes.addAll(remappedClassNodes);
		//targetGroup.writeOutputJar(new File("Mapped_NEW.jar"));
	}

	private static final List<Mapper> mappers = List.of(
		new RuneLiteAPIClassMapper(),
		new RenameUnique(),
		new FieldMultiplicationMapper(),
		new ObfuscatedSignatureMapper(),
		new MethodGarbageValueMapper()
	);
}