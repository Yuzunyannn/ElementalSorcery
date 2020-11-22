package yuzunyannn.elementalsorcery.elf.researcher;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import yuzunyannn.elementalsorcery.api.crafting.IResearchRecipe;
import yuzunyannn.elementalsorcery.util.json.ItemRecord;
import yuzunyannn.elementalsorcery.util.json.Json;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class ResearchRecipeManagement {

	static final public ResearchRecipeManagement instance = new ResearchRecipeManagement();

	private final List<IResearchRecipe> recipes = new ArrayList<>();

	// 寻找合成表
	public List<IResearchRecipe> findMatchingRecipe(Researcher researcher, World world) {
		List<IResearchRecipe> list = new ArrayList<>();
		for (IResearchRecipe irecipe : this.recipes) {
			if (irecipe.matches(researcher, world)) list.add(irecipe);
		}
		return list;
	}

	public List<IResearchRecipe> getRecipes() {
		return recipes;
	}

	public void register(IResearchRecipe recipe) {
		recipes.add(recipe);
	}

	public static void registerAll() {
		for (ModContainer mod : Loader.instance().getActiveModList()) loadRecipes(mod);
	}

	public static void loadRecipes(ModContainer mod) {
		Json.ergodicAssets(mod, "/research_recipes", (file, json) -> {
			JsonObject topics = json.needObject("topics");
			ItemRecord output = json.needItem("result");
			ResearchRecipe recipe = new ResearchRecipe(output.getStack());
			for (String key : topics) recipe.add(key, topics.getNumber(key).intValue());
			instance.register(recipe);
			return true;
		});
	}

}
