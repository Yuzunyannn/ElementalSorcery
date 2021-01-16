package yuzunyannn.elementalsorcery.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import yuzunyannn.elementalsorcery.api.crafting.IRecipe;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.elf.research.ResearchRecipeManagement;
import yuzunyannn.elementalsorcery.init.ESImplRegister;
import yuzunyannn.elementalsorcery.tile.altar.TileMagicDesk;
import yuzunyannn.elementalsorcery.tile.md.TileMDInfusion;
import yuzunyannn.elementalsorcery.tile.md.TileMDRubbleRepair;
import yuzunyannn.elementalsorcery.util.json.Json;
import yuzunyannn.elementalsorcery.util.json.JsonArray;
import yuzunyannn.elementalsorcery.util.json.JsonObject;
import yuzunyannn.elementalsorcery.util.text.TextHelper;

public class RecipeManagement extends ESImplRegister<IRecipe> {

	static final public RecipeManagement instance = new RecipeManagement();

	public RecipeManagement() {
		super(IRecipe.class);
	}

	// 寻找合成表
	public IRecipe findMatchingRecipe(IInventory craftMatrix, World worldIn) {
		for (IRecipe irecipe : this.getValues()) {
			if (irecipe.matches(craftMatrix, worldIn)) return irecipe;
		}
		return null;
	}

	public static void registerAll() {
		// 魔法书桌自动机合成
		TileMagicDesk.init();
		// 碎石修复注册
		TileMDRubbleRepair.init();
		// 注魔机
		TileMDInfusion.init();
		// 研究
		ResearchRecipeManagement.registerAll();
		// 加载json
		for (ModContainer mod : Loader.instance().getActiveModList()) loadRecipes(mod);
		// 独立的注册
		registerElementRecipes();
	}

	public static void registerElementRecipes() {
		instance.register(new RecipeGrimoire().setRegistryName(TextHelper.toESResourceLocation("grimoire")));
	}

	public static void loadRecipes(ModContainer mod) {
		Json.ergodicAssets(mod, "/element_recipes", (file, json) -> {
			String type = "normal";
			if (json.hasString("type")) type = json.getString("type");

			JsonArray patternJson = json.needArray("pattern");
			ArrayList<String> pattern = patternJson.asStringArray();

			JsonObject obj = json.needObject("key");
			Map<String, ItemStack[]> map = new HashMap<>();
			for (String key : obj) {
				List<ItemStack> stacks = Json.to(obj.needItems(key));
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
			ResourceLocation id = new ResourceLocation(mod.getModId(), Json.fileToId(file, "/element_recipes"));
			instance.register(recipe.setRegistryName(id));
			return true;
		});
	}

}
