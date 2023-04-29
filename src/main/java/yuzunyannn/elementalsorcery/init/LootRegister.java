package yuzunyannn.elementalsorcery.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.util.TextHelper;
import yuzunyannn.elementalsorcery.util.json.Json;
import yuzunyannn.elementalsorcery.world.LootFunctionRandomMantra;

public class LootRegister {

	static public ResourceLocation res(String id) {
		return TextHelper.toESResourceLocation(id);
	}

	public static final ResourceLocation ES_HALL = res("chests/es_hall");
	public static final ResourceLocation ES_VILLAGE_HALL = res("chests/es_village_hall");
	
	public static final ResourceLocation SPRITE_ZOMBIE = res("entities/sprite_zombie");
	public static final ResourceLocation RABID_RABBIT = res("entities/rabid_rabbit");
	public static final ResourceLocation DREAD_CUBE = res("entities/dread_cube");
	public static final ResourceLocation DEJECTED_SKELETON = res("entities/dejected_skeleton");

	static public void registerAll() {
		LootTableList.register(ES_HALL);
		LootTableList.register(ES_VILLAGE_HALL);
		LootTableList.register(RABID_RABBIT);
		LootTableList.register(DREAD_CUBE);
		LootTableList.register(DEJECTED_SKELETON);

		Json.ergodicAssets(ElementalSorcery.getModeContainer(), "/loot_tables/dungeon/", (file, json) -> {
			if (!ElementMap.checkModDemands(json)) return false;
			LootTableList.register(res("dungeon/" + file.getFileName()));
			return true;
		});

		LootFunctionManager.registerFunction(new LootFunctionRandomMantra.Serializer());
	}
}
