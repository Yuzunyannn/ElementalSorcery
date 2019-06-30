package yuzunyan.elementalsorcery.init.registries;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import yuzunyan.elementalsorcery.crafting.RecipeColorRuler;
import yuzunyan.elementalsorcery.crafting.RecipeManagement;
import yuzunyan.elementalsorcery.init.ESInitInstance;

public class ESCraftingRegistries {
	public static final void registerAll() {
		// 注册所有合成
		RecipeManagement.RegisterAll();
		// 注册所有烧炼
		registerAllSmelting();
		// 注册合成表
		ForgeRegistries.RECIPES.register(new RecipeColorRuler().setRegistryName("magicRuler"));
	}

	private static void registerAllSmelting() {
		GameRegistry.addSmelting(ESInitInstance.BLOCKS.KYNAITE_ORE, new ItemStack(ESInitInstance.ITEMS.KYNAITE), 0.5f);
	}
}
