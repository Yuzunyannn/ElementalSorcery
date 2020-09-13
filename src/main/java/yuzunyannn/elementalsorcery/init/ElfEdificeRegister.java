package yuzunyannn.elementalsorcery.init;

import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.elf.edifice.EFloorHall;
import yuzunyannn.elementalsorcery.elf.edifice.ElfEdificeFloor;

public class ElfEdificeRegister {

	private static void register(String id, ElfEdificeFloor element) {
		ElfEdificeFloor.REGISTRY.register(element.setRegistryName(new ResourceLocation(ElementalSorcery.MODID, id)));
	}

	static public void registerAll() {
		register("hall", EFloorHall.instance);
	}
}
