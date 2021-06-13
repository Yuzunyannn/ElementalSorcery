package yuzunyannn.elementalsorcery.mods;

import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.Loader;

public class ModCheckClassTransformer implements IClassTransformer {

	private List<String> getRequireModId(List<AnnotationNode> anns) {
		if (anns == null) return null;
		String type = Type.getDescriptor(ModRequire.class);
		for (AnnotationNode ann : anns) {
			if (ann.desc.equals(type)) {
				if (ann.values == null) continue;
				for (int x = 0; x < ann.values.size() - 1; x += 2) {
					Object key = ann.values.get(x);
					Object value = ann.values.get(x + 1);
					if ("value".equals(key)) return (List<String>) value;
				}
			}
		}
		return null;
	}

	private boolean needRemoveIds(List<String> modids) {
		if (modids == null) return false;
		for (String modid : modids) if (!Loader.isModLoaded(modid)) return true;
		return false;
	}

	private boolean needRemove(List<AnnotationNode> anns) {
		return needRemoveIds(getRequireModId(anns));
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (bytes == null) return null;
		if (!name.startsWith("yuzunyannn")) return bytes;

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		List<String> modids = getRequireModId(classNode.visibleAnnotations);
		if (needRemoveIds(modids)) {
			throw new RuntimeException(
					String.format("Attempted to load class %s without mods %s", classNode.name, modids.toString()));
		}

		Iterator<FieldNode> fields = classNode.fields.iterator();
		while (fields.hasNext()) {
			FieldNode field = fields.next();
			if (needRemove(field.visibleAnnotations)) fields.remove();
		}

		LambdaGatherer lambdaGatherer = new LambdaGatherer();
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while (methods.hasNext()) {
			MethodNode method = methods.next();
			if (needRemove(method.visibleAnnotations)) {
				methods.remove();
				lambdaGatherer.accept(method);
			}
		}

		for (List<Handle> dynamicLambdaHandles = lambdaGatherer.getDynamicLambdaHandles(); !dynamicLambdaHandles
				.isEmpty(); dynamicLambdaHandles = lambdaGatherer.getDynamicLambdaHandles()) {
			lambdaGatherer = new LambdaGatherer();
			methods = classNode.methods.iterator();
			while (methods.hasNext()) {
				MethodNode method = methods.next();
				if ((method.access & Opcodes.ACC_SYNTHETIC) == 0) continue;
				for (Handle dynamicLambdaHandle : dynamicLambdaHandles) {
					if (method.name.equals(dynamicLambdaHandle.getName())
							&& method.desc.equals(dynamicLambdaHandle.getDesc())) {
						methods.remove();
						lambdaGatherer.accept(method);
					}
				}
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}

}
