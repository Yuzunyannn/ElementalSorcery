package yuzunyannn.elementalsorcery.init.registries;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import yuzunyannn.elementalsorcery.crafting.RecipeColorRuler;
import yuzunyannn.elementalsorcery.crafting.RecipeManagement;
import yuzunyannn.elementalsorcery.crafting.RecipeRiteWrite;
import yuzunyannn.elementalsorcery.init.ESInitInstance;

public class ESCraftingRegistries {
	public static final void registerAll() {
		// 注册所有合成
		RecipeManagement.RegisterAll();
		// 注册所有烧炼
		registerAllSmelting();
		// 注册合成表
		ForgeRegistries.RECIPES.register(new RecipeColorRuler().setRegistryName("magicRuler"));
		ForgeRegistries.RECIPES.register(new RecipeRiteWrite().setRegistryName("riteWrite"));

	}

	private static void registerAllSmelting() {
		GameRegistry.addSmelting(ESInitInstance.BLOCKS.KYANITE_ORE, new ItemStack(ESInitInstance.ITEMS.KYANITE), 0.5f);
	}
}
