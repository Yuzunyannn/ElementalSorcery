package yuzunyannn.elementalsorcery.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import yuzunyannn.elementalsorcery.util.text.TextHelper;

public class LootRegister {

	public static final ResourceLocation ES_HALL = TextHelper.toESResourceLocation("chests/es_hall");
	public static final ResourceLocation RABID_RABBIT = TextHelper.toESResourceLocation("entities/rabid_rabbit");

	static public void registerAll() {
		LootTableList.register(ES_HALL);
		LootTableList.register(RABID_RABBIT);
	}
}
