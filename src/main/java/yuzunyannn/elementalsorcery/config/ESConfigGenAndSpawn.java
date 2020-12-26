package yuzunyannn.elementalsorcery.config;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.util.text.TextHelper;

public class ESConfigGenAndSpawn {

	/** 精灵的生成 */
	public final ESConfigGenAndSpawnGetter SPAWN_ELF;
	/** 精灵树 */
	public final ESConfigGenAndSpawnGetter GEN_ELF_TREE;
	/** 蓝晶石 */
	public final ESConfigGenAndSpawnGetter GEN_KYNATE;
	/** 星石 */
	public final ESConfigGenAndSpawnGetter GEN_START_STONE;
	/** 藏封石 */
	public final ESConfigGenAndSpawnGetter GEN_SEAL_STONE;

	public ESConfigGenAndSpawn(Configuration config) {
		Property property;

		property = config.get(Configuration.CATEGORY_GENERAL, "world_gens", getDefaultGenData(),
				"[生成Gen][mod里各种内容的生成][world$overworld=world$0]");
		Map<String, String> map = dealDataToMap(property.getStringList());

		GEN_ELF_TREE = new ESConfigGenAndSpawnGetter(map.get("elf_tree"));
		ElementalSorcery.logger.info("[生成]精灵树:" + GEN_ELF_TREE);

		GEN_KYNATE = new ESConfigGenAndSpawnGetter(map.get("kynate"));
		ElementalSorcery.logger.info("[生成]蓝晶石:" + GEN_KYNATE);

		GEN_START_STONE = new ESConfigGenAndSpawnGetter(map.get("star_stone"));
		ElementalSorcery.logger.info("[生成]星石:" + GEN_START_STONE);
		
		GEN_SEAL_STONE = new ESConfigGenAndSpawnGetter(map.get("seal_stone"));
		ElementalSorcery.logger.info("[生成]藏封石:" + GEN_SEAL_STONE); 

		property = config.get(Configuration.CATEGORY_GENERAL, "world_spawns", getDefaultSpawnData(),
				"[生成Spawn][mod里各种生物的生成]");
		map = dealDataToMap(property.getStringList());

		SPAWN_ELF = new ESConfigGenAndSpawnGetter(map.get("elf"));
		ElementalSorcery.logger.info("[生成]精灵:" + SPAWN_ELF);

	}

	private Map<String, String> dealDataToMap(String[] strs) {
		Map<String, String> map = new HashMap();
		for (String str : strs) {
			int i = str.indexOf('=');
			if (i == -1) continue;
			String key = str.substring(0, i);
			String value = str.substring(i + 1);
			map.put(TextHelper.castToUnderline(key.trim()).toLowerCase(), value.trim());
		}
		return map;
	}

	private String[] getDefaultSpawnData() {
		String[] strs = new String[1];
		strs[0] = "elf=biome$plains(8)|biome$desert|biome$hell|biome$forest|biome$birch_forest|biome$extreme_hills|biome$swampland";
		return strs;
	}

	private String[] getDefaultGenData() {
		String[] strs = new String[4];
		strs[0] = "elf_tree=world$overworld";
		strs[1] = "kynate=world$overworld";
		strs[2] = "star_stone=world$overworld";
		strs[3] = "seal_stone=world$overworld|world$the_nether(-2)";
		return strs;
	}

}
