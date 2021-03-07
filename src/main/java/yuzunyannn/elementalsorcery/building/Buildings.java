package yuzunyannn.elementalsorcery.building;

public class Buildings {

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
	}
}
