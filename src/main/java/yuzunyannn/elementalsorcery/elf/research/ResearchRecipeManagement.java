package yuzunyannn.elementalsorcery.elf.research;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.crafting.IResearchRecipe;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.util.json.ItemRecord;
import yuzunyannn.elementalsorcery.util.json.Json;
import yuzunyannn.elementalsorcery.util.json.JsonArray;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class ResearchRecipeManagement {

	static final public ResearchRecipeManagement instance = new ResearchRecipeManagement();

	private final Map<ResourceLocation, IResearchRecipe> recipes = new HashMap();

	// 寻找合成表
	public List<IResearchRecipe> findMatchingRecipe(Researcher researcher, World world) {
		List<IResearchRecipe> list = new ArrayList<>();
		for (IResearchRecipe irecipe : this.recipes.values()) {
			if (irecipe.matches(researcher, world)) list.add(irecipe);
		}
		return list;
	}

	public Map<ResourceLocation, IResearchRecipe> getRecipes() {
		return recipes;
	}

	public void register(ResourceLocation id, IResearchRecipe recipe) {
		recipes.put(id, recipe);
	}

	public static void reload() {
		instance.recipes.clear();
		registerAll();
	}

	public static void registerAll() {
		for (ModContainer mod : Loader.instance().getActiveModList()) loadRecipes(mod);
		loadCustomRecipes();
	}

	public static boolean loadRecipe(ResourceLocation id, JsonObject json) {
		if (!ElementMap.checkType(json, "research")) return false;
		if (!ElementMap.checkModDemands(json)) return false;
		JsonObject topics = json.needObject("topics");
		JsonArray needs = json.needArray("items", "item");
		ItemRecord output = json.needItem("result");
		ResearchRecipe recipe = new ResearchRecipe(output.getStack());
		for (String key : topics) recipe.add(key, topics.getNumber(key).intValue());
		for (int i = 0; i < needs.size(); i++) {
			List<ItemRecord> list = needs.needItems(i);
			recipe.add(ItemRecord.asIngredient(list));
		}
		instance.register(id, recipe);
		return true;
	}

	public static void loadRecipes(ModContainer mod) {
		Json.ergodicAssets(mod, "/research_recipes", (file, json) -> {
			ResourceLocation id = new ResourceLocation(mod.getModId(), Json.fileToId(file, "/research_recipes"));
			return loadRecipe(id, json);
		});
	}

	public static void loadCustomRecipes() {
		Json.ergodicFile("recipes/research", (file, json) -> {
			ResourceLocation id = new ResourceLocation(ESAPI.MODID, Json.fileToId(file, "/research"));
			return loadRecipe(id, json);
		});
	}

}
