package yuzunyannn.elementalsorcery.building;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.tile.md.TileMDFrequencyMapping.Vibrate;
import yuzunyannn.elementalsorcery.util.TextHelper;
import yuzunyannn.elementalsorcery.util.json.JsonArray;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class Buildings {

	public static class VibrateKey extends Vibrate {

		private final String key;

		public VibrateKey(String key) {
			super(key);
			this.key = key;
		}

		public VibrateKey(String key, float[] f) {
			super(f);
			this.key = key;
		}

		public String getKey() {
			return key;
		}

	}

	static public final List<VibrateKey> frequencyMapping = new ArrayList<>();

	static public Building INFUSION;
	static public Building ABSORB_BOX;
	static public Building DECONSTRUCT_BOX;
	static public Building SUPREME_ALTAR;
	static public Building SPELLBOOK_ALTAR;
	static public Building ELEMENT_CRAFTING_ALTAR;
	static public Building DECONSTRUCT_ALTAR;
	static public Building DECONSTRUCT_ALTAR_ADV;
	static public Building BUILING_ALTAR;
	static public Building ANALYSIS_ALTAR;
	static public Building ANALYSIS_ALTAR_ADD;
	static public Building CRYSTAL_GARDEN;
	static public Building RESONANT_INCUBATOR;
	static public Building PORTAL_ALTAR;
	static public Building TRANSCRIBE_ALTAR;
	static public Building MAPPING_ALTAR;
	static public Building DECONSTRUCT_WINDMILL;
	

	static public void init() {
		INFUSION = BuildingLib.instance.getBuilding("infusion");
		ABSORB_BOX = BuildingLib.instance.getBuilding("absorb_box");
		DECONSTRUCT_BOX = BuildingLib.instance.getBuilding("deconstruct_box");
		SUPREME_ALTAR = BuildingLib.instance.getBuilding("supreme_altar");
		SPELLBOOK_ALTAR = BuildingLib.instance.getBuilding("spellbook_altar");
		ELEMENT_CRAFTING_ALTAR = BuildingLib.instance.getBuilding("element_crafting_altar");
		DECONSTRUCT_ALTAR = BuildingLib.instance.getBuilding("deconstruct_altar");
		DECONSTRUCT_ALTAR_ADV = BuildingLib.instance.getBuilding("deconstruct_altar_adv");
		BUILING_ALTAR = BuildingLib.instance.getBuilding("builing_altar");
		ANALYSIS_ALTAR = BuildingLib.instance.getBuilding("analysis_altar");
		ANALYSIS_ALTAR_ADD = BuildingLib.instance.getBuilding("analysis_altar_add");
		CRYSTAL_GARDEN = BuildingLib.instance.getBuilding("crystal_garden");
		RESONANT_INCUBATOR = BuildingLib.instance.getBuilding("resonant_incubator");
		PORTAL_ALTAR = BuildingLib.instance.getBuilding("portal_altar");
		TRANSCRIBE_ALTAR = BuildingLib.instance.getBuilding("transcribe_altar");
		MAPPING_ALTAR = BuildingLib.instance.getBuilding("mapping_altar");
		DECONSTRUCT_WINDMILL = BuildingLib.instance.getBuilding("deconstruct_windmill");
		initFrequencyMapping();
	}

	static public void initFrequencyMapping() {
		File mapFile = ElementalSorcery.data.getFile("building/", "frequency_map.json");
		JsonObject mappingData;
		try {
			mappingData = new JsonObject(mapFile);
		} catch (Exception e) {
			mappingData = getDefaultMappingExample();
			mappingData.save(mapFile, true);
			copyExample("example_hut_1.json");
			copyExample("example_hut_2.json");
			copyExample("example_cactus_farm.json");
			copyExample("example_useless_tower.json");
		}
		readFrequencyMapping(mappingData);
	}

	static private JsonObject getDefaultMappingExample() {
		JsonObject mappingData = new JsonObject();
		{
			JsonArray array = new JsonArray();
			array.append("example_hut_1");
			array.append("example_hut_2");
			mappingData.set("auto", array);
		}
		{
			JsonObject mapData = new JsonObject();
			mapData.set("example_cactus_farm", new JsonArray().append(22.22f).append(89.7f));
			mapData.set("example_useless_tower", new JsonArray().append(89.7f).append(34.3f).append(27.77f));
			mappingData.set("map", mapData);
		}
		return mappingData;
	}

	static private void copyExample(String name) {
		try {
			File file = ElementalSorcery.data.getFile("building/json/", name);
			new JsonObject(TextHelper.toESResourceLocation("examples/" + name)).save(file, true);
		} catch (Exception e) {}
	}

	static private void readFrequencyMapping(JsonObject mappingData) {
		if (mappingData.hasArray("auto")) {
			String[] keys = mappingData.getArray("auto").asStringArray();
			for (String key : keys) frequencyMapping.add(new VibrateKey(key));
		}
		if (mappingData.hasObject("map")) {
			JsonObject keys = mappingData.getObject("map");
			for (String key : keys) {
				if (!keys.hasArray(key)) continue;
				frequencyMapping.add(new VibrateKey(key, keys.getArray(key).asFloatArray()));
			}
		}
	}

	static public List<String> getKeys() {
		Collection<Building> bs = BuildingLib.instance.getBuildingsFromLib();
		List<String> list = new ArrayList<>(frequencyMapping.size() + bs.size());
		for (Building b : bs) list.add(b.getKeyName());
		for (VibrateKey vk : frequencyMapping) list.add(vk.getKey());
		return list;
	}
}
