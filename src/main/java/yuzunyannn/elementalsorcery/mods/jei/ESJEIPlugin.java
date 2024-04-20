package yuzunyannn.elementalsorcery.mods.jei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.IVanillaRecipeFactory;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.crafting.IElementRecipe;
import yuzunyannn.elementalsorcery.api.crafting.IResearchRecipe;
import yuzunyannn.elementalsorcery.crafting.ISmashRecipe;
import yuzunyannn.elementalsorcery.elf.research.ResearchRecipeManagement;
import yuzunyannn.elementalsorcery.item.prop.ItemBlessingJadePiece;
import yuzunyannn.elementalsorcery.mods.jei.md.MDCategory;
import yuzunyannn.elementalsorcery.mods.jei.md.MDInfusionRW;
import yuzunyannn.elementalsorcery.mods.jei.md.MDMagicSolidifyRW;
import yuzunyannn.elementalsorcery.mods.jei.md.MDRubbleRepairRW;
import yuzunyannn.elementalsorcery.tile.TileMeltCauldron;
import yuzunyannn.elementalsorcery.tile.TileMeltCauldron.MeltCauldronRecipe;
import yuzunyannn.elementalsorcery.tile.TileRiteTable;
import yuzunyannn.elementalsorcery.tile.altar.TileMagicDesk;
import yuzunyannn.elementalsorcery.tile.md.TileMDInfusion;
import yuzunyannn.elementalsorcery.tile.md.TileMDRubbleRepair;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

@JEIPlugin
public class ESJEIPlugin implements IModPlugin {

	public static IJeiHelpers jeiHelpers;
	public static IStackHelper stackHelper;
	public static IGuiHelper guiHelper;

	public static final String UID_MDRUBBLEREPAIR = ESAPI.MODID + "." + "MDRubbleRepair";
	public static final String UID_MDMAGICSOLIDIFY = ESAPI.MODID + "." + "MDMagicSolidify";
	public static final String UID_MDINFUSION = ESAPI.MODID + "." + "MDInfusion";

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		if (guiHelper == null) guiHelper = registry.getJeiHelpers().getGuiHelper();
		// 注册合成类型
		registry.addRecipeCategories(new DescribeCategory());
		registry.addRecipeCategories(new RiteCategory());
		registry.addRecipeCategories(new ElementCraftingCategory());
		registry.addRecipeCategories(new MagicDeskCategory());
		registry.addRecipeCategories(new ResearchCategory());
		registry.addRecipeCategories(new MeltCauldronCategory());
		registry.addRecipeCategories(new MDCategory<MDRubbleRepairRW>(UID_MDRUBBLEREPAIR));
		registry.addRecipeCategories(new MDCategory<MDMagicSolidifyRW>(UID_MDMAGICSOLIDIFY));
		registry.addRecipeCategories(new MDCategory<MDInfusionRW>(UID_MDINFUSION));
		registry.addRecipeCategories(new SmashCategory());
	}

	@Override
	public void register(IModRegistry registry) {
		// 获取句柄
		jeiHelpers = registry.getJeiHelpers();
		stackHelper = jeiHelpers.getStackHelper();
		guiHelper = jeiHelpers.getGuiHelper();
		final ESObjects.Blocks BLOCKS = ESObjects.BLOCKS;
		final ESObjects.Items ITEMS = ESObjects.ITEMS;
		// 注册工厂
		registry.handleRecipes(DescribeRecipeWrapper.Describe.class, DescribeRecipeWrapper::new, DescribeCategory.UID);
		registry.handleRecipes(TileRiteTable.Recipe.class, RiteRecipeWrapper::new, RiteCategory.UID);
		registry.handleRecipes(IElementRecipe.class, ElementCraftingRecipeWrapper::new, ElementCraftingCategory.UID);
		registry.handleRecipes(TileMagicDesk.Recipe.class, MagicDeskRecipeWrapper::new, MagicDeskCategory.UID);
		registry.handleRecipes(IResearchRecipe.class, ResearchRecipeWrapper::new, ResearchCategory.UID);
		registry.handleRecipes(MeltCauldronRecipe.class, MeltCauldronRecipeWrapper::new, MeltCauldronCategory.UID);
		registry.handleRecipes(TileMDRubbleRepair.Recipe.class, MDRubbleRepairRW::new, UID_MDRUBBLEREPAIR);
		registry.handleRecipes(MDMagicSolidifyRW.FakeRecipe.class, MDMagicSolidifyRW::new, UID_MDMAGICSOLIDIFY);
		registry.handleRecipes(TileMDInfusion.Recipe.class, MDInfusionRW::new, UID_MDINFUSION);
		registry.handleRecipes(ISmashRecipe.class, SmashRecipeWrapper::new, SmashCategory.UID);
		// 设置新增
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.ELEMENT_WORKBENCH), VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.SUPREME_TABLE), VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.RITE_TABLE), RiteCategory.UID);
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.SUPREME_TABLE), ElementCraftingCategory.UID);
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.ELEMENT_CRAFTING_TABLE), ElementCraftingCategory.UID);
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.ELEMENT_WORKBENCH), ElementCraftingCategory.UID);
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.MAGIC_DESK), MagicDeskCategory.UID);
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.RESEARCHER), ResearchCategory.UID);
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.MELT_CAULDRON), MeltCauldronCategory.UID);
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.MD_RUBBLE_REPAIR), UID_MDRUBBLEREPAIR);
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.MD_MAGIC_SOLIDIFY), UID_MDMAGICSOLIDIFY);
		registry.addRecipeCatalyst(new ItemStack(BLOCKS.MD_INFUSION), UID_MDINFUSION);
		registry.addRecipeCatalyst(new ItemStack(ITEMS.MILL_HAMMER), SmashCategory.UID);
		// 添加所合成表
		registry.addRecipes(ESAPI.recipeMgr.getValues(), ElementCraftingCategory.UID);
		registry.addRecipes(TileRiteTable.getRecipes(), RiteCategory.UID);
		registry.addRecipes(TileMagicDesk.getRecipes(), MagicDeskCategory.UID);
		registry.addRecipes(ResearchRecipeManagement.instance.getRecipes().values(), ResearchCategory.UID);
		registry.addRecipes(TileMeltCauldron.recipes, MeltCauldronCategory.UID);
		registry.addRecipes(TileMDRubbleRepair.getRecipes(), UID_MDRUBBLEREPAIR);
		registry.addRecipes(Arrays.asList(MDMagicSolidifyRW.FakeRecipe.values()), UID_MDMAGICSOLIDIFY);
		registry.addRecipes(TileMDInfusion.recipes, UID_MDINFUSION);
		registry.addRecipes(ISmashRecipe.recipes, SmashCategory.UID);
		this.registerOther(registry);
		// 描述类
		registry.addRecipes(this.initDescribe(), DescribeCategory.UID);
	}

	private void registerOther(IModRegistry registry) {
		IVanillaRecipeFactory vrf = jeiHelpers.getVanillaRecipeFactory();
		// === 修理 ===
		// 替罪羊
		ItemStack stack = new ItemStack(ESObjects.ITEMS.SCAPEGOAT);
		stack.setItemDamage(stack.getMaxDamage() / 4 * 3);
		ItemStack stack1 = stack.copy();
		stack1.setItemDamage(stack.getMaxDamage());
		ItemStack stack2 = new ItemStack(Items.WHEAT);
		IRecipeWrapper repairWithMaterial = vrf.createAnvilRecipe(stack1, ItemHelper.toList(stack2),
				ItemHelper.toList(stack));
		registry.addRecipes(Collections.singletonList(repairWithMaterial), VanillaRecipeCategoryUid.ANVIL);
	}

	private List<DescribeRecipeWrapper.Describe> initDescribe() {
		final ESObjects.Blocks BLOCKS = ESObjects.BLOCKS;
		final ESObjects.Items ITEMS = ESObjects.ITEMS;
		List<DescribeRecipeWrapper.Describe> describes = new ArrayList<>();
		describes.add(new DescribeRecipeWrapper.Describe("es.tutorial.1_2_0.title", "es.tutorial.1_2_0.describe", BLOCKS.STAR_SAND,
				BLOCKS.STAR_STONE, BLOCKS.STAR_SAND));
//		describes.add(new DescribeRecipeWrapper.Describe("page.astone", "page.astone.ct.sec",
//				new ItemStack(BLOCKS.ASTONE), ItemHelper.toList(ITEMS.MAGIC_STONE, ITEMS.KYANITE, Blocks.COBBLESTONE),
//				ItemHelper.toList(new ItemStack(BLOCKS.ASTONE, 1, 1), new ItemStack(BLOCKS.ASTONE, 1, 0))));
		describes.add(new DescribeRecipeWrapper.Describe("es.tutorial.3_8_0.title", "es.tutorial.3_8_0.describe",
				new ItemStack(Blocks.ENCHANTING_TABLE),
				ItemHelper.toList(ITEMS.SPELLBOOK_ENCHANTMENT, Blocks.ENCHANTING_TABLE),
				ItemHelper.toList(BLOCKS.INVALID_ENCHANTMENT_TABLE)));
		describes.add(new DescribeRecipeWrapper.Describe("es.tutorial.3_2_0.title", "es.tutorial.3_2_0.describe",
				new ItemStack(ITEMS.AZURE_CRYSTAL),
				ItemHelper.toList(ITEMS.MAGIC_CRYSTAL, new ItemStack(Items.DYE, 1, 4)),
				ItemHelper.toList(ITEMS.AZURE_CRYSTAL)));
		describes.add(
				new DescribeRecipeWrapper.Describe("es.tutorial.2_5_0.title", "es.tutorial.2_5_0.describe", new ItemStack(ITEMS.QUILL, 1, 1),
						ItemHelper.toList(ITEMS.QUILL, 1, 1), ItemHelper.toList(ITEMS.QUILL, 1, 2, ITEMS.QUILL, 1, 3)));
		describes.add(new DescribeRecipeWrapper.Describe("es.tutorial.3_0_2.title", "es.tutorial.3_0_2.describe",
				new ItemStack(ITEMS.LIFE_LEATHER, 1, 1), ItemHelper.toList(Items.LEATHER, ITEMS.SOUL_FRAGMENT),
				ItemHelper.toList(ITEMS.LIFE_LEATHER, 1, 0, ITEMS.LIFE_LEATHER, 1, 1)));
		describes.add(new DescribeRecipeWrapper.Describe("es.tutorial.5_5_1.title", "es.tutorial.5_5_1.describe",
				new ItemStack(ITEMS.MAGIC_PAPER, 1, 3), ItemHelper.toList(ITEMS.MAGIC_PAPER, 1, 2),
				ItemHelper.toList(ITEMS.MAGIC_PAPER, 1, 3)));
		List<ItemStack> blessingJadePieces = new ArrayList<>();
		for (int i = 0; i < 8; i++) blessingJadePieces.add(ItemBlessingJadePiece.createPiece(i));
		describes.add(new DescribeRecipeWrapper.Describe("es.page.blessingJade", "es.page.blessingJade.ct",
				new ItemStack(ITEMS.BLESSING_JADE), blessingJadePieces, blessingJadePieces));

		return describes;
	}
}
