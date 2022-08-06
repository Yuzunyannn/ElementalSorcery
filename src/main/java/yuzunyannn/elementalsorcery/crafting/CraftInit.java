package yuzunyannn.elementalsorcery.crafting;

import java.io.File;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.crafting.RecipeManagement;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.elf.research.ResearchRecipeManagement;
import yuzunyannn.elementalsorcery.tile.TileItemStructureCraft;
import yuzunyannn.elementalsorcery.tile.altar.TileMagicDesk;
import yuzunyannn.elementalsorcery.tile.md.TileMDInfusion;
import yuzunyannn.elementalsorcery.tile.md.TileMDRubbleRepair;
import yuzunyannn.elementalsorcery.util.TextHelper;
import yuzunyannn.elementalsorcery.util.json.ItemRecord;
import yuzunyannn.elementalsorcery.util.json.Json;
import yuzunyannn.elementalsorcery.util.json.JsonArray;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class CraftInit {

	public static void registerAll() {
		ElementalSorcery.setAPIField(new RecipeManagement());
		// is
		TileItemStructureCraft.init();
		// 魔法书桌自动机合成
		TileMagicDesk.init();
		// 碎石修复注册
		TileMDRubbleRepair.init();
		// 注魔机
		TileMDInfusion.init();
		// 研究
		ResearchRecipeManagement.registerAll();
		// 加载json
		for (ModContainer mod : Loader.instance().getActiveModList()) {
			boolean isElementalSorcery = mod.getModId().equals(ESAPI.MODID);
			// 自定义
			final Map<ResourceLocation, Entry<File, JsonObject>> customRecipes = new HashMap<>();
			if (isElementalSorcery) {
				Json.ergodicFile("recipes/element_craft", (file, json) -> {
					ResourceLocation id = new ResourceLocation(mod.getModId(), Json.fileToId(file, "/element_craft"));
					customRecipes.put(id, new AbstractMap.SimpleEntry(file, json));
					return true;
				});
			}
			loadRecipes(mod, customRecipes);
			// 自定义加载
			if (isElementalSorcery) {
				for (Entry<ResourceLocation, Entry<File, JsonObject>> entry : customRecipes.entrySet()) {
					Json.trySyntaxJson(() -> {
						loadRecipe(entry.getKey(), entry.getValue().getValue());
					}, entry.getValue().getKey().toString());
				}
			}
		}
		// 独立的注册
		registerElementRecipes();
		// 包外的注册
	}

	public static void registerElementRecipes() {
		ESAPI.recipeMgr.register(new RecipeGrimoire().setRegistryName(TextHelper.toESResourceLocation("grimoire")));
	}

	public static void loadRecipes(ModContainer mod,
			@Nullable Map<ResourceLocation, Entry<File, JsonObject>> customRecipes) {
		Json.ergodicAssets(mod, "/element_recipes", (file, json) -> {
			ResourceLocation id = new ResourceLocation(mod.getModId(), Json.fileToId(file, "/element_recipes"));
			if (customRecipes == null) return loadRecipe(id, json);
			// 替换到玩家自定义的合成表
			if (customRecipes.containsKey(id)) {
				json = customRecipes.get(id).getValue();
				customRecipes.remove(id);
			}
			return loadRecipe(id, json);

		});
	}

	public static boolean loadRecipe(ResourceLocation id, JsonObject json) {
		if (!ElementMap.checkModDemands(json)) return false;

		String type = "normal";
		if (json.hasString("type")) type = json.getString("type");

		JsonArray patternJson = json.needArray("pattern");
		List<String> pattern = Arrays.asList(patternJson.asStringArray());

		JsonObject obj = json.needObject("key");
		Map<String, ItemStack[]> map = new HashMap<>();
		for (String key : obj) {
			List<ItemStack> stacks = ItemRecord.asItemStackList(obj.needItems(key));
			map.put(key, stacks.toArray(new ItemStack[stacks.size()]));
		}
		ItemStack output = json.needItem("result").getStack();

		List<ElementStack> elements = json.needElements("element");

		Recipe recipe;
		switch (type.toLowerCase()) {
		case "inherit_element":
			recipe = new RecipeInheritElement(output);
			break;
		default:
			recipe = new Recipe(output);
			break;
		}
		recipe.parse(output, pattern, map, elements);
		ESAPI.recipeMgr.register(recipe.setRegistryName(id));
		return true;
	}
}
