package yuzunyannn.elementalsorcery.init;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.block.altar.BlockElementalCube;
import yuzunyannn.elementalsorcery.crafting.RecipeManagement;
import yuzunyannn.elementalsorcery.crafting.mc.RecipeColorful;
import yuzunyannn.elementalsorcery.crafting.mc.RecipeLifeDirt;
import yuzunyannn.elementalsorcery.crafting.mc.RecipePageBuilding;
import yuzunyannn.elementalsorcery.crafting.mc.RecipeRiteWrite;
import yuzunyannn.elementalsorcery.item.tool.ItemMagicRuler;

public class ESCraftingRegistries {

	public static final void registerAll() {
		// 注册所有合成
		RecipeManagement.registerAll();
		// 注册所有烧炼
		registerAllSmelting();
		// 注册合成表
		IForgeRegistry<IRecipe> RECIPES = ForgeRegistries.RECIPES;
		RECIPES.register(new RecipeRiteWrite().setRegistryName("riteWrite"));
		RECIPES.register(new RecipeLifeDirt().setRegistryName("lifeDirt"));
		RECIPES.register(new RecipePageBuilding().setRegistryName("pageBuilding"));

		RECIPES.register(new RecipeColorful(new ItemStack(ESInit.ITEMS.MAGIC_RULER))
				.colorSetter((stack, color) -> ItemMagicRuler.setColor(stack, color)).setRegistryName("magicRuler"));
		RECIPES.register(new RecipeColorful(new ItemStack(ESInit.BLOCKS.ELEMENTAL_CUBE))
				.colorSetter((stack, color) -> BlockElementalCube.setDyeColor(stack, color))
				.setRegistryName("colorElementCube"));
	}

	private static void registerAllSmelting() {
		ESObjects.Items ITEMS = ESInit.ITEMS;
		ESObjects.Blocks BLOCKS = ESInit.BLOCKS;
		GameRegistry.addSmelting(BLOCKS.ELF_LOG, new ItemStack(Items.COAL, 1, 1), 0.25f);
		GameRegistry.addSmelting(BLOCKS.KYANITE_ORE, new ItemStack(ITEMS.KYANITE), 0.5f);
		GameRegistry.addSmelting(new ItemStack(BLOCKS.ELF_FRUIT, 1, 2), new ItemStack(ITEMS.ELF_CRYSTAL), 0.25f);
	}
}
