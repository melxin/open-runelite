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
package net.melxin.asm.mapper.annotations;

import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AnnotationAdder
{
	public static final String OBFUSCATED_NAME_ANNOTATION_DESC = "L" + ObfuscatedName.class.getName().replace('.', '/') + ";";
	public static final String OBFUSCATED_GETTER_ANNOTATION_DESC = "L" + ObfuscatedGetter.class.getName().replace('.', '/') + ";";
	public static final String OBFUSCATED_SIGNATURE_ANNOTATION_DESC = "L" + ObfuscatedSignature.class.getName().replace('.', '/') + ";";

	public static void addObfuscatedNameAnnotation(ClassNode classNode, String obfuscatedName)
	{
		if (hasAnnotation(classNode.visibleAnnotations, OBFUSCATED_NAME_ANNOTATION_DESC))
		{
			log.warn("Class: {} is already annotated with: {}", classNode.name, OBFUSCATED_NAME_ANNOTATION_DESC);
			return;
		}

		if (classNode.visibleAnnotations == null)
		{
			classNode.visibleAnnotations = new ArrayList<>();
		}

		final AnnotationNode annotation = new AnnotationNode(OBFUSCATED_NAME_ANNOTATION_DESC);
		annotation.values = new ArrayList<>();
		annotation.values.add("value");
		annotation.values.add(obfuscatedName);

		classNode.visibleAnnotations.add(annotation);
	}

	public static void addObfuscatedNameAnnotation(FieldNode fieldNode, String obfuscatedName)
	{
		if (hasAnnotation(fieldNode.visibleAnnotations, OBFUSCATED_NAME_ANNOTATION_DESC))
		{
			log.warn("Field: {} is already annotated with: {}", fieldNode.name, OBFUSCATED_NAME_ANNOTATION_DESC);
			return;
		}

		if (fieldNode.visibleAnnotations == null)
		{
			fieldNode.visibleAnnotations = new ArrayList<>();
		}

		final AnnotationNode annotation = new AnnotationNode(OBFUSCATED_NAME_ANNOTATION_DESC);
		annotation.values = new ArrayList<>();
		annotation.values.add("value");
		annotation.values.add(obfuscatedName);

		fieldNode.visibleAnnotations.add(annotation);
	}

	public static void addObfuscatedNameAnnotation(MethodNode methodNode, String obfuscatedName)
	{
		if (hasAnnotation(methodNode.visibleAnnotations, OBFUSCATED_NAME_ANNOTATION_DESC))
		{
			log.warn("Method: {} is already annotated with: {}", methodNode.name, OBFUSCATED_NAME_ANNOTATION_DESC);
			return;
		}

		if (methodNode.visibleAnnotations == null)
		{
			methodNode.visibleAnnotations = new ArrayList<>();
		}

		final AnnotationNode annotation = new AnnotationNode(OBFUSCATED_NAME_ANNOTATION_DESC);
		annotation.values = new ArrayList<>();
		annotation.values.add("value");
		annotation.values.add(obfuscatedName);

		methodNode.visibleAnnotations.add(annotation);
	}

	public static void addObfuscatedGetterAnnotation(FieldNode fieldNode, int value)
	{
		if (hasAnnotation(fieldNode.visibleAnnotations, OBFUSCATED_GETTER_ANNOTATION_DESC))
		{
			log.warn("Field: {} is already annotated with: {}", fieldNode.name, OBFUSCATED_GETTER_ANNOTATION_DESC);
			return;
		}

		if (fieldNode.visibleAnnotations == null)
		{
			fieldNode.visibleAnnotations = new ArrayList<>();
		}

		final AnnotationNode annotation = new AnnotationNode(OBFUSCATED_GETTER_ANNOTATION_DESC);
		annotation.values = new ArrayList<>();
		annotation.values.add("intValue");
		annotation.values.add(value);

		fieldNode.visibleAnnotations.add(annotation);
	}

	public static void addObfuscatedGetterAnnotation(FieldNode fieldNode, long value)
	{
		if (hasAnnotation(fieldNode.visibleAnnotations, OBFUSCATED_GETTER_ANNOTATION_DESC))
		{
			log.warn("Field: {} is already annotated with: {}", fieldNode.name, OBFUSCATED_GETTER_ANNOTATION_DESC);
			return;
		}

		if (fieldNode.visibleAnnotations == null)
		{
			fieldNode.visibleAnnotations = new ArrayList<>();
		}

		final AnnotationNode annotation = new AnnotationNode(OBFUSCATED_GETTER_ANNOTATION_DESC);
		annotation.values = new ArrayList<>();
		annotation.values.add("longValue");
		annotation.values.add(value);

		fieldNode.visibleAnnotations.add(annotation);
	}

	public static void addObfuscatedSignatureAnnotation(FieldNode fieldNode, String descriptor)
	{
		if (hasAnnotation(fieldNode.visibleAnnotations, OBFUSCATED_SIGNATURE_ANNOTATION_DESC))
		{
			log.warn("Field: {} is already annotated with: {}", fieldNode.name, OBFUSCATED_SIGNATURE_ANNOTATION_DESC);
			return;
		}

		if (fieldNode.visibleAnnotations == null)
		{
			fieldNode.visibleAnnotations = new ArrayList<>();
		}

		final AnnotationNode annotation = new AnnotationNode(OBFUSCATED_SIGNATURE_ANNOTATION_DESC);
		annotation.values = new ArrayList<>();
		annotation.values.add("descriptor");
		annotation.values.add(descriptor);

		fieldNode.visibleAnnotations.add(annotation);
	}

	public static void addObfuscatedSignatureAnnotation(MethodNode methodNode, String descriptor, String garbageValue)
	{
		if (hasAnnotation(methodNode.visibleAnnotations, OBFUSCATED_SIGNATURE_ANNOTATION_DESC))
		{
			log.warn("Method: {} is already annotated with: {}", methodNode.name, OBFUSCATED_SIGNATURE_ANNOTATION_DESC);
			return;
		}

		if (methodNode.visibleAnnotations == null)
		{
			methodNode.visibleAnnotations = new ArrayList<>();
		}

		final AnnotationNode annotation = new AnnotationNode(OBFUSCATED_SIGNATURE_ANNOTATION_DESC);
		annotation.values = new ArrayList<>();
		annotation.values.add("descriptor");
		annotation.values.add(descriptor);

		if (garbageValue != null)
		{
			annotation.values.add("garbageValue");
			annotation.values.add(garbageValue);
		}

		methodNode.visibleAnnotations.add(annotation);
	}

	public static boolean hasAnnotation(List<AnnotationNode> annotations, String descriptor)
	{
		if (annotations == null)
		{
			return false;
		}

		for (AnnotationNode annotation : annotations)
		{
			if (descriptor.equals(annotation.desc))
			{
				return true;
			}
		}
		return false;
	}
}