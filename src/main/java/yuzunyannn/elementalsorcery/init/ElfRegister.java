package yuzunyannn.elementalsorcery.init;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.elf.edifice.EFloorHall;
import yuzunyannn.elementalsorcery.elf.edifice.EFloorLaboratory;
import yuzunyannn.elementalsorcery.elf.edifice.EFloorLibrary;
import yuzunyannn.elementalsorcery.elf.edifice.EFloorLivingRoom;
import yuzunyannn.elementalsorcery.elf.edifice.EFloorPostOffice;
import yuzunyannn.elementalsorcery.elf.edifice.EFloorRefinery;
import yuzunyannn.elementalsorcery.elf.edifice.EFloorWorkshop;
import yuzunyannn.elementalsorcery.elf.edifice.ElfEdificeFloor;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfession;
import yuzunyannn.elementalsorcery.util.text.TextHelper;

public class ElfRegister {

	private static void register(String id, ElfEdificeFloor floor) {
		floor = floor.setRegistryName(new ResourceLocation(ElementalSorcery.MODID, id));
		if (floor.getUnlocalizedName() == null) floor = floor.setUnlocalizedName(TextHelper.castToCamel(id));
		ElfEdificeFloor.REGISTRY.register(floor);
	}

	static public void registerAllFloor() {
		register("hall", EFloorHall.instance);
		register("living_room", new EFloorLivingRoom());
		register("workshop", new EFloorWorkshop());
		register("refinery", new EFloorRefinery());
		register("post_office", new EFloorPostOffice());
		register("laboratory", new EFloorLaboratory());
		register("library", new EFloorLibrary());
	}

	public static void registerAllProfession() throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = ElfProfession.class.getFields();
		for (Field field : fields) {
			if (!Modifier.isStatic(field.getModifiers())) continue;
			if (!ElfProfession.class.isAssignableFrom(field.getType())) continue;
			ElfProfession elfPro = (ElfProfession) field.get(ElfProfession.class);
			String id = field.getName().toLowerCase();
			elfPro.setRegistryName(new ResourceLocation(ElementalSorcery.MODID, id));
			if (elfPro.getUnlocalizedName() == null) elfPro.setUnlocalizedName(TextHelper.castToCamel(id));
			ElfProfession.REGISTRY.register(elfPro);
		}
	}
}
