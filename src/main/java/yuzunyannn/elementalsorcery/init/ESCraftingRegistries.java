package yuzunyannn.elementalsorcery.init;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.crafting.RecipeManagement;
import yuzunyannn.elementalsorcery.crafting.mc.RecipeColorRuler;
import yuzunyannn.elementalsorcery.crafting.mc.RecipeLifeDirt;
import yuzunyannn.elementalsorcery.crafting.mc.RecipePageBuilding;
import yuzunyannn.elementalsorcery.crafting.mc.RecipeRiteWrite;

public class ESCraftingRegistries {
	public static final void registerAll() {
		// 注册所有合成
		RecipeManagement.registerAll();
		// 注册所有烧炼
		registerAllSmelting();
		// 注册合成表
		ForgeRegistries.RECIPES.register(new RecipeColorRuler().setRegistryName("magicRuler"));
		ForgeRegistries.RECIPES.register(new RecipeRiteWrite().setRegistryName("riteWrite"));
		ForgeRegistries.RECIPES.register(new RecipeLifeDirt().setRegistryName("lifeDirt"));
		ForgeRegistries.RECIPES.register(new RecipePageBuilding().setRegistryName("pageBuilding"));

	}

	private static void registerAllSmelting() {
		ESObjects.Items ITEMS = ESInit.ITEMS;
		ESObjects.Blocks BLOCKS = ESInit.BLOCKS;
		GameRegistry.addSmelting(BLOCKS.KYANITE_ORE, new ItemStack(ITEMS.KYANITE), 0.5f);
		GameRegistry.addSmelting(new ItemStack(BLOCKS.ELF_FRUIT, 1, 2), new ItemStack(ITEMS.ELF_CRYSTAL), 0.25f);
	}
}
