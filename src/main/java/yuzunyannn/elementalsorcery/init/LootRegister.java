package yuzunyannn.elementalsorcery.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import yuzunyannn.elementalsorcery.util.text.TextHelper;

public class LootRegister {

	static public ResourceLocation res(String id) {
		return TextHelper.toESResourceLocation(id);
	}

	public static final ResourceLocation ES_HALL = res("chests/es_hall");
	public static final ResourceLocation RABID_RABBIT = res("entities/rabid_rabbit");
	public static final ResourceLocation DREAD_CUBE = res("entities/dread_cube");
	public static final ResourceLocation DEJECTED_SKELETON = res("entities/dejected_skeleton");

	static public void registerAll() {
		LootTableList.register(ES_HALL);
		LootTableList.register(RABID_RABBIT);
		LootTableList.register(DREAD_CUBE);
		LootTableList.register(DEJECTED_SKELETON);
	}
}
