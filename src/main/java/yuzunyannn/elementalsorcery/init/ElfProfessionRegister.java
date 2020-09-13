package yuzunyannn.elementalsorcery.init;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfession;

public class ElfProfessionRegister {

	public static void registerAll() throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = ElfProfession.class.getFields();
		for (Field field : fields) {
			if (!Modifier.isStatic(field.getModifiers())) continue;
			if (!ElfProfession.class.isAssignableFrom(field.getType())) continue;
			ElfProfession elfPro = (ElfProfession) field.get(ElfProfession.class);
			elfPro.setRegistryName(new ResourceLocation(ElementalSorcery.MODID, field.getName().toLowerCase()));
			ElfProfession.REGISTRY.register(elfPro);
		}
	}
}
