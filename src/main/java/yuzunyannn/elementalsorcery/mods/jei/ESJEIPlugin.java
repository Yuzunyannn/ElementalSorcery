package yuzunyannn.elementalsorcery.mods.jei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.crafting.IRecipe;
import yuzunyannn.elementalsorcery.crafting.RecipeManagement;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.mods.jei.md.MDCategory;
import yuzunyannn.elementalsorcery.mods.jei.md.MDInfusionRW;
import yuzunyannn.elementalsorcery.mods.jei.md.MDMagicSolidifyRW;
import yuzunyannn.elementalsorcery.mods.jei.md.MDRubbleRepairRW;
import yuzunyannn.elementalsorcery.tile.TileRiteTable;
import yuzunyannn.elementalsorcery.tile.altar.TileMagicDesk;
import yuzunyannn.elementalsorcery.tile.md.TileMDInfusion;
import yuzunyannn.elementalsorcery.tile.md.TileMDRubbleRepair;

@JEIPlugin
public class ESJEIPlugin implements IModPlugin {

	public static IJeiHelpers jeiHelpers;
	public static IStackHelper stackHelper;
	public static IGuiHelper guiHelper;

	public static final String UID_MDRUBBLEREPAIR = ElementalSorcery.MODID + "." + "MDRubbleRepair";
	public static final String UID_MDMAGICSOLIDIFY = ElementalSorcery.MODID + "." + "MDMagicSolidify";
	public static final String UID_MDINFUSION = ElementalSorcery.MODID + "." + "MDInfusion";

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		if (guiHelper == null) guiHelper = registry.getJeiHelpers().getGuiHelper();
		// 注册合成类型
		registry.addRecipeCategories(new DescribeCategory());
		registry.addRecipeCategories(new RiteCategory());
		registry.addRecipeCategories(new ElementCraftingCategory());
		registry.addRecipeCategories(new MagicDeskCategory());
		registry.addRecipeCategories(new MDCategory<MDRubbleRepairRW>(UID_MDRUBBLEREPAIR));
		registry.addRecipeCategories(new MDCategory<MDMagicSolidifyRW>(UID_MDMAGICSOLIDIFY));
		registry.addRecipeCategories(new MDCategory<MDInfusionRW>(UID_MDINFUSION));
	}

	@Override
	public void register(IModRegistry registry) {
		// 获取句柄
		jeiHelpers = registry.getJeiHelpers();
		stackHelper = jeiHelpers.getStackHelper();
		guiHelper = jeiHelpers.getGuiHelper();
		final ESObjects.Blocks BLOCKS = ESInitInstance.BLOCKS;
		// 注册工厂
		registry.handleRecipes(DescribeRecipeWrapper.Describe.class, DescribeRecipeWrapper::new, DescribeCategory.UID);
		registry.handleRecipes(TileRiteTable.Recipe.class, RiteRecipeWrapper::new, RiteCategory.UID);
		registry.handleRecipes(IRecipe.class, ElementCraftingRecipeWrapper::new, ElementCraftingCategory.UID);
		registry.handleRecipes(TileMagicDesk.Recipe.class, MagicDeskRecipeWrapper::new, MagicDeskCategory.UID);
		registry.handleRecipes(TileMDRubbleRepair.Recipe.class, MDRubbleRepairRW::new, UID_MDRUBBLEREPAIR);
		registry.handleRecipes(MDMagicSolidifyRW.FakeRecipe.class, MDMagicSolidifyRW::new, UID_MDMAGICSOLIDIFY);
		registry.handleRecipes(TileMDInfusion.Recipe.class, MDInfusionRW::new, UID_MDINFUSION);
		// 设置新增
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.ELEMENT_WORKBENCH), VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.SUPREME_CRAFTING_TABLE), VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.RITE_TABLE), RiteCategory.UID);
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.SUPREME_CRAFTING_TABLE), ElementCraftingCategory.UID);
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.ELEMENT_CRAFTING_TABLE), ElementCraftingCategory.UID);
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.ELEMENT_WORKBENCH), ElementCraftingCategory.UID);
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.MAGIC_DESK), MagicDeskCategory.UID);
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.MD_RUBBLE_REPAIR), UID_MDRUBBLEREPAIR);
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.MD_MAGIC_SOLIDIFY), UID_MDMAGICSOLIDIFY);
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.MD_INFUSION), UID_MDINFUSION);
		// 添加所合成表
		registry.addRecipes(RecipeManagement.instance.getRecipes(), ElementCraftingCategory.UID);
		registry.addRecipes(TileRiteTable.getRecipes(), RiteCategory.UID);
		registry.addRecipes(TileMagicDesk.getRecipes(), MagicDeskCategory.UID);
		registry.addRecipes(TileMDRubbleRepair.getRecipes(), UID_MDRUBBLEREPAIR);
		registry.addRecipes(Arrays.asList(MDMagicSolidifyRW.FakeRecipe.values()), UID_MDMAGICSOLIDIFY);
		registry.addRecipes(TileMDInfusion.getRecipes(), UID_MDINFUSION);
		// 描述类
		registry.addRecipes(this.initDescribe(), DescribeCategory.UID);
	}

	private List<DescribeRecipeWrapper.Describe> initDescribe() {
		final ESObjects.Blocks BLOCKS = ESInitInstance.BLOCKS;
		final ESObjects.Items ITEMS = ESInitInstance.ITEMS;
		List<DescribeRecipeWrapper.Describe> describes = new ArrayList<>();
		describes.add(new DescribeRecipeWrapper.Describe("page.starSand", "page.starSand.ct", BLOCKS.STAR_SAND,
				BLOCKS.STAR_STONE, BLOCKS.STAR_SAND));
		describes.add(
				new DescribeRecipeWrapper.Describe("page.astone", "page.astone.ct.sec", new ItemStack(BLOCKS.ASTONE),
						DescribeRecipeWrapper.Describe.asList(ITEMS.MAGIC_STONE, ITEMS.KYANITE, Blocks.COBBLESTONE),
						DescribeRecipeWrapper.Describe.asList(new ItemStack(BLOCKS.ASTONE, 1, 1),
								new ItemStack(BLOCKS.ASTONE, 1, 0))));
		describes.add(new DescribeRecipeWrapper.Describe("page.enchantingBook", "page.enchantingBook.ct",
				new ItemStack(Blocks.ENCHANTING_TABLE),
				DescribeRecipeWrapper.Describe.asList(ITEMS.SPELLBOOK_ENCHANTMENT, Blocks.ENCHANTING_TABLE),
				DescribeRecipeWrapper.Describe.asList(BLOCKS.INVALID_ENCHANTMENT_TABLE)));
		return describes;
	}
}
