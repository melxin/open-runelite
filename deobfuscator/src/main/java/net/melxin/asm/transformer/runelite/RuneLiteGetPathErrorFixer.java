package net.melxin.asm.transformer.runelite;

import lombok.extern.slf4j.Slf4j;
import net.melxin.asm.transformer.JarEntryGroup;
import net.melxin.asm.transformer.JarEntryTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

@Slf4j
public class RuneLiteGetPathErrorFixer implements JarEntryTransformer
{
	@Override
	public void transform(JarEntryGroup jarEntryGroup)
	{
		int count = 0;
		int visitedCount = 0;
		for (ClassNode classNode : jarEntryGroup.getClassNodes())
		{
			for (MethodNode methodNode : classNode.methods)
			{
				for (AbstractInsnNode insn : methodNode.instructions)
				{
					if (insn instanceof MethodInsnNode)
					{
						MethodInsnNode methodInsn = (MethodInsnNode) insn;
						if (!methodInsn.name.equals("getPath"))
						{
							continue;
						}
						if (++visitedCount == 2)
						{
							methodNode.instructions.insertBefore(insn, new InsnNode(Opcodes.POP));
							methodNode.instructions.set(insn, new LdcInsnNode(""));
							count++;
							break;
						}
					}
				}
			}
		}
		log.info("Fixed: {} getPath() method calls (This is to fix a bug with FernFlower decompiler of V1_6 bytecode with runtime V1_8)", count);
	}
}