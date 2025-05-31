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

import lombok.extern.slf4j.Slf4j;
import net.melxin.asm.mapper.annotations.AnnotationAdder;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Remapper
{
	public Remapper()
	{
	}

	private final Map<String, String> CLASS_MAPPINGS = new HashMap<>();
	private final Map<String, String> FIELD_MAPPINGS = new HashMap<>();
	private final Map<String, String> METHOD_MAPPINGS = new HashMap<>();

	public Map<String, String> getClassMappings()
	{
		return CLASS_MAPPINGS;
	}

	public Map<String, String> getFieldMappings()
	{
		return FIELD_MAPPINGS;
	}

	public Map<String, String> getMethodMappings()
	{
		return METHOD_MAPPINGS;
	}

	public void mapClass(ClassNode from, ClassNode to)
	{
		mapClass(from, to.name);
	}

	public void mapClass(ClassNode from, String to)
	{
		if (CLASS_MAPPINGS.containsKey(from.name) || CLASS_MAPPINGS.containsValue(to))
		{
			return;
		}

		AnnotationAdder.addObfuscatedNameAnnotation(from, from.name);

		CLASS_MAPPINGS.put(from.name, to);
		//from.name = to;
		//from.accept(new ClassRemapper(new ClassNode(Opcodes.ASM9), remapper));
	}

	public void mapField(ClassNode owner, FieldNode fieldNode, FieldNode targetFieldNode)
	{
		mapField(owner, fieldNode, targetFieldNode.name);
	}

	public void mapField(ClassNode owner, FieldNode fieldNode, String newName)
	{
		mapField(owner.name, fieldNode, newName);
	}

	public void mapField(String owner, FieldNode fieldNode, String newName)
	{
		if (fieldNode.name.equals(newName))
		{
			return;
		}

		final String key = key(owner, fieldNode.name, fieldNode.desc);
		if (FIELD_MAPPINGS.containsKey(key))
		{
			return;
		}

		AnnotationAdder.addObfuscatedNameAnnotation(fieldNode, fieldNode.name);
		FIELD_MAPPINGS.put(key, newName);
	}

	public void mapMethod(ClassNode owner, MethodNode methodNode, MethodNode targetMethodNode)
	{
		mapMethod(owner, methodNode, targetMethodNode.name);
	}

	public void mapMethod(ClassNode owner, MethodNode methodNode, String newName)
	{
		mapMethod(owner.name, methodNode, newName);
	}

	public void mapMethod(String owner, MethodNode methodNode, String newName)
	{
		if ("<init>".equals(methodNode.name) || "<clinit>".equals(methodNode.name) || methodNode.name.equals(newName))
		{
			return;
		}

		final String key = key(owner, methodNode.name, methodNode.desc);
		if (METHOD_MAPPINGS.containsKey(key))
		{
			return;
		}

		AnnotationAdder.addObfuscatedNameAnnotation(methodNode, methodNode.name);
		METHOD_MAPPINGS.put(key, newName);
	}

	public final org.objectweb.asm.commons.Remapper remapper = new org.objectweb.asm.commons.Remapper()
	{
		@Override
		public String map(String internalName)
		{
			final String mappedName = CLASS_MAPPINGS.getOrDefault(internalName, internalName);
			if (!internalName.equals(mappedName))
			{
				log.debug("Map class: {} -> {}", internalName, mappedName);
			}
			return mappedName;
		}

		@Override
		public String mapFieldName(String owner, String name, String descriptor)
		{
			final String mappedName = FIELD_MAPPINGS.getOrDefault(key(owner, name, descriptor), name);
			if (!name.equals(mappedName))
			{
				log.debug("Map field: {} -> {}", name, mappedName);
			}
			return mappedName;
		}

		@Override
		public String mapMethodName(String owner, String name, String descriptor)
		{
			final String mappedName = METHOD_MAPPINGS.getOrDefault(key(owner, name, descriptor), name);
			if (!name.equals(mappedName))
			{
				log.debug("Map method: {} -> {}", name, mappedName);
			}
			return mappedName;
		}
	};

	private static String key(String owner, String name, String descriptor)
	{
		return owner + "#" + name + "#" + descriptor;
	}
}