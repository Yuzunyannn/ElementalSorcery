package yuzunyannn.elementalsorcery.mods.jei;

import java.util.Arrays;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.crafting.IRecipe;
import yuzunyannn.elementalsorcery.crafting.RecipeManagement;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.mods.jei.md.MDCategory;
import yuzunyannn.elementalsorcery.mods.jei.md.RWMDMagicSolidify;
import yuzunyannn.elementalsorcery.mods.jei.md.RWMDRubbleRepair;
import yuzunyannn.elementalsorcery.tile.md.TileMDRubbleRepair;

@JEIPlugin
public class ESJEIPlugin implements IModPlugin {

	public static IJeiHelpers jeiHelpers;
	public static IStackHelper stackHelper;
	public static IGuiHelper guiHelper;

	public static final String UID_MDRUBBLEREPAIR = ElementalSorcery.MODID + "." + "MDRubbleRepair";
	public static final String UID_MDMAGICSOLIDIFY = ElementalSorcery.MODID + "." + "MDMagicSolidify";

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		if (guiHelper == null) guiHelper = registry.getJeiHelpers().getGuiHelper();
		// 注册合成类型
		registry.addRecipeCategories(new ElementCraftingCategory());
		registry.addRecipeCategories(new MDCategory<RWMDRubbleRepair>(UID_MDRUBBLEREPAIR));
		registry.addRecipeCategories(new MDCategory<RWMDMagicSolidify>(UID_MDMAGICSOLIDIFY));
	}

	@Override
	public void register(IModRegistry registry) {
		// 获取句柄
		jeiHelpers = registry.getJeiHelpers();
		stackHelper = jeiHelpers.getStackHelper();
		guiHelper = jeiHelpers.getGuiHelper();
		final ESObjects.Blocks BLOCKS = ESInitInstance.BLOCKS;
		// 注册工厂
		registry.handleRecipes(IRecipe.class, ElementCraftingRecipeWrapper::new, ElementCraftingCategory.UID);
		registry.handleRecipes(TileMDRubbleRepair.Recipe.class, RWMDRubbleRepair::new, UID_MDRUBBLEREPAIR);
		registry.handleRecipes(RWMDMagicSolidify.FakeRecipe.class, RWMDMagicSolidify::new, UID_MDMAGICSOLIDIFY);
		// 设置新增
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.ELEMENT_WORKBENCH), VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.SUPREME_CRAFTING_TABLE), VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.SUPREME_CRAFTING_TABLE), ElementCraftingCategory.UID);
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.ELEMENT_CRAFTING_TABLE), ElementCraftingCategory.UID);
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.ELEMENT_WORKBENCH), ElementCraftingCategory.UID);
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.MD_RUBBLE_REPAIR), UID_MDRUBBLEREPAIR);
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.MD_MAGIC_SOLIDIFY), UID_MDMAGICSOLIDIFY);
		// 添加所合成表
		registry.addRecipes(RecipeManagement.instance.getRecipes(), ElementCraftingCategory.UID);
		registry.addRecipes(TileMDRubbleRepair.getRecipes(), UID_MDRUBBLEREPAIR);
		registry.addRecipes(Arrays.asList(RWMDMagicSolidify.FakeRecipe.values()), UID_MDMAGICSOLIDIFY);
	}
}
