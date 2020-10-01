package yuzunyannn.elementalsorcery.init;

import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.elf.edifice.EFloorHall;
import yuzunyannn.elementalsorcery.elf.edifice.EFloorLivingRoom;
import yuzunyannn.elementalsorcery.elf.edifice.EFloorRefinery;
import yuzunyannn.elementalsorcery.elf.edifice.EFloorWorkshop;
import yuzunyannn.elementalsorcery.elf.edifice.ElfEdificeFloor;
import yuzunyannn.elementalsorcery.util.TextHelper;

public class ElfEdificeRegister {

	private static void register(String id, ElfEdificeFloor floor) {
		floor = floor.setRegistryName(new ResourceLocation(ElementalSorcery.MODID, id));
		floor = floor.setUnlocalizedName(TextHelper.castToCamel(id));
		ElfEdificeFloor.REGISTRY.register(floor);
	}

	static public void registerAll() {
		register("hall", EFloorHall.instance);
		register("living_room", new EFloorLivingRoom());
		register("workshop", new EFloorWorkshop());
		register("refinery", new EFloorRefinery());
	}
}
