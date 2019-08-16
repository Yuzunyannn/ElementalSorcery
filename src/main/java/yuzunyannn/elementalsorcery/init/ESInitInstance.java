package yuzunyannn.elementalsorcery.init;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import yuzunyannn.elementalsorcery.ESCreativeTabs;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.ESRegister;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementMagic;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.block.BlockElementWorkbench;
import yuzunyannn.elementalsorcery.block.BlockInvalidEnchantmentTable;
import yuzunyannn.elementalsorcery.block.BlockKynaite;
import yuzunyannn.elementalsorcery.block.BlockMagicTorch;
import yuzunyannn.elementalsorcery.block.BlockStarSand;
import yuzunyannn.elementalsorcery.block.BlockStarStone;
import yuzunyannn.elementalsorcery.block.BlocksAStone;
import yuzunyannn.elementalsorcery.block.BlocksEStone;
import yuzunyannn.elementalsorcery.block.altar.BlockAnalysisAltar;
import yuzunyannn.elementalsorcery.block.altar.BlockBuildingAltar;
import yuzunyannn.elementalsorcery.block.altar.BlockDeconstructAltarTable;
import yuzunyannn.elementalsorcery.block.altar.BlockElementCraftingTable;
import yuzunyannn.elementalsorcery.block.altar.BlockElementalCube;
import yuzunyannn.elementalsorcery.block.altar.BlockMagicDesk;
import yuzunyannn.elementalsorcery.block.altar.BlockSupremeCraftingTable;
import yuzunyannn.elementalsorcery.block.container.BlockAbsorbBox;
import yuzunyannn.elementalsorcery.block.container.BlockDeconstructBox;
import yuzunyannn.elementalsorcery.block.container.BlockHearth;
import yuzunyannn.elementalsorcery.block.container.BlockInfusionBox;
import yuzunyannn.elementalsorcery.block.container.BlockLantern;
import yuzunyannn.elementalsorcery.block.container.BlockMagicPlatform;
import yuzunyannn.elementalsorcery.block.container.BlockMeltCauldron;
import yuzunyannn.elementalsorcery.block.container.BlockSmeltBox;
import yuzunyannn.elementalsorcery.block.container.BlockStela;
import yuzunyannn.elementalsorcery.block.container.BlockStoneMill;
import yuzunyannn.elementalsorcery.crafting.RecipeManagement;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.element.ElementAir;
import yuzunyannn.elementalsorcery.element.ElementEarth;
import yuzunyannn.elementalsorcery.element.ElementEnder;
import yuzunyannn.elementalsorcery.element.ElementFire;
import yuzunyannn.elementalsorcery.element.ElementKnowledge;
import yuzunyannn.elementalsorcery.element.ElementMetal;
import yuzunyannn.elementalsorcery.element.ElementWater;
import yuzunyannn.elementalsorcery.element.ElementWood;
import yuzunyannn.elementalsorcery.init.registries.ElementRegister;
import yuzunyannn.elementalsorcery.item.ItemArchitectureCrystal;
import yuzunyannn.elementalsorcery.item.ItemElementCrystal;
import yuzunyannn.elementalsorcery.item.ItemItemCrystal;
import yuzunyannn.elementalsorcery.item.ItemKynaiteTools;
import yuzunyannn.elementalsorcery.item.ItemMagicRuler;
import yuzunyannn.elementalsorcery.item.ItemManual;
import yuzunyannn.elementalsorcery.item.ItemParchment;
import yuzunyannn.elementalsorcery.item.ItemScroll;
import yuzunyannn.elementalsorcery.item.ItemSome;
import yuzunyannn.elementalsorcery.item.ItemSpellbook;
import yuzunyannn.elementalsorcery.item.ItemSpellbookArchitecture;
import yuzunyannn.elementalsorcery.item.ItemSpellbookCover;
import yuzunyannn.elementalsorcery.item.ItemSpellbookElement;
import yuzunyannn.elementalsorcery.item.ItemSpellbookEnchantment;
import yuzunyannn.elementalsorcery.item.ItemSpellbookLaunch;

public class ESInitInstance {

	public static ESObjects.Items ITEMS = new ESObjects.Items();
	public static ESObjects.Blocks BLOCKS = new ESObjects.Blocks();
	public static ESObjects.Elements ELEMENTS = new ESObjects.Elements();
	public static ESObjects.Village VILLAGE = new ESObjects.Village();
	public static ESCreativeTabs tab;

	public static final void instance() throws ReflectiveOperationException {
		// ES注册
		ESRegister.ELEMENT = ElementRegister.instance;
		ESRegister.ELEMENT_MAP = ElementMap.instance;
		ESRegister.RECIPE = RecipeManagement.instance;
		// 实例句柄集
		ESObjects.ITEMS = ITEMS;
		ESObjects.BLOCKS = BLOCKS;
		ESObjects.ELEMENTS = ELEMENTS;
		ESObjects.VILLAGE = VILLAGE;
		// 创造物品栏
		ESCreativeTabs.TAB = new ESCreativeTabs();
		ESObjects.CREATIVE_TABS = tab;
		tab = ESCreativeTabs.TAB;
		// 初始化虚空元素
		ELEMENTS.VOID = new Element(Element.rgb(0, 0, 0)).setRegistryName("void").setUnlocalizedName("void");
		// 初始化魔力元素
		ELEMENTS.MAGIC = new ElementMagic().setRegistryName("magic").setUnlocalizedName("magic");
		initVoidElement();
		// 实例化方块和物品等
		instanceBlocks();
		instanceItems();
		instanceElements();
		instanceVillage();
	}

	private static void initVoidElement() throws ReflectiveOperationException {
		Field field = ElementStack.class.getDeclaredField("element");
		field.setAccessible(true);
		field.set(ElementStack.EMPTY, ELEMENTS.VOID);
	}

	private static final void instanceBlocks() throws ReflectiveOperationException {

		BLOCKS.ESTONE = new BlocksEStone.EStone().setRegistryName("estone");
		BLOCKS.ESTONE_SLAB = new BlocksEStone.EStoneSlab().setRegistryName("estone_slab");
		BLOCKS.ESTONE_STAIRS = new BlocksEStone.EStoneStairs().setRegistryName("estone_stairs");
		BLOCKS.ASTONE = new BlocksAStone().setRegistryName("astone");

		BLOCKS.ELEMENTAL_CUBE = new BlockElementalCube().setRegistryName("elemental_cube");
		BLOCKS.HEARTH = new BlockHearth().setRegistryName("hearth");
		BLOCKS.SMELT_BOX = BlockSmeltBox.newBlockSmeltBox(BlockHearth.EnumMaterial.COBBLESTONE)
				.setRegistryName("smelt_box");
		BLOCKS.SMELT_BOX_IRON = BlockSmeltBox.newBlockSmeltBox(BlockHearth.EnumMaterial.IRON)
				.setRegistryName("smelt_box_iron");
		BLOCKS.SMELT_BOX_KYNAITE = BlockSmeltBox.newBlockSmeltBox(BlockHearth.EnumMaterial.KYNAITE)
				.setRegistryName("smelt_box_kynaite");
		BLOCKS.KYNAITE_BLOCK = new BlockKynaite().setRegistryName("kynaite_block");
		BLOCKS.KYNAITE_ORE = new BlockKynaite.BlockKynaiteOre().setRegistryName("kynaite_ore");
		BLOCKS.MAGIC_PLATFORM = new BlockMagicPlatform().setRegistryName("magic_platform");
		BLOCKS.ABSORB_BOX = new BlockAbsorbBox().setRegistryName("absorb_box");
		BLOCKS.INVALID_ENCHANTMENT_TABLE = new BlockInvalidEnchantmentTable()
				.setRegistryName("invalid_enchantment_table");
		BLOCKS.ELEMENT_WORKBENCH = new BlockElementWorkbench().setRegistryName("element_workbench");
		BLOCKS.DECONSTRUCT_BOX = new BlockDeconstructBox().setRegistryName("deconstruct_box");
		BLOCKS.INFUSION_BOX = new BlockInfusionBox().setRegistryName("infusion_box");
		BLOCKS.MAGIC_DESK = new BlockMagicDesk().setRegistryName("magic_desk");
		BLOCKS.ELEMENT_CRAFTING_TABLE = new BlockElementCraftingTable().setRegistryName("element_crafting_table");
		BLOCKS.DECONSTRUCT_ALTAR_TABLE = new BlockDeconstructAltarTable().setRegistryName("deconstruct_altar_table");
		BLOCKS.STELA = new BlockStela().setRegistryName("stela");
		BLOCKS.LANTERN = new BlockLantern().setRegistryName("lantern");
		BLOCKS.BUILDING_ALTAR = new BlockBuildingAltar().setRegistryName("building_altar");
		BLOCKS.ANALYSIS_ALTAR = new BlockAnalysisAltar().setRegistryName("analysis_altar");
		BLOCKS.SUPREME_CRAFTING_TABLE = new BlockSupremeCraftingTable().setRegistryName("supreme_crafting_table");
		BLOCKS.MAGIC_TORCH = new BlockMagicTorch().setRegistryName("magic_torch");
		BLOCKS.STAR_STONE = new BlockStarStone().setRegistryName("star_stone");
		BLOCKS.STAR_SAND = new BlockStarSand().setRegistryName("star_sand");
		BLOCKS.STONE_MILL = new BlockStoneMill().setRegistryName("stone_mill");
		BLOCKS.MELT_CAULDRON = new BlockMeltCauldron().setRegistryName("melt_cauldron");
		// 初始化所有tab
		Class<?> cls = BLOCKS.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			((Block) field.get(BLOCKS)).setCreativeTab(tab);
		}
	}

	private static final void instanceItems() throws ReflectiveOperationException {
		ITEMS.KYNAITE = ItemSome.newKynaite().setRegistryName("kynaite");
		ITEMS.MAGICAL_PIECE = ItemSome.newMagicalPiece().setRegistryName("magical_piece");
		ITEMS.MAGICAL_ENDER_EYE = ItemSome.newMagicalEnderEye().setRegistryName("magical_ender_eye");
		ITEMS.MAGIC_CRYSTAL = ItemSome.newMagicalCrystal().setRegistryName("magic_crystal");
		ITEMS.MAGIC_PAPER = ItemSome.newMagicPaper().setRegistryName("magic_paper");
		ITEMS.SPELL_PAPER = ItemSome.newSpellPaper().setRegistryName("spell_paper");
		ITEMS.SPELL_CRYSTAL = ItemSome.newSpellCrystal().setRegistryName("spell_crystal");
		ITEMS.MAGIC_STONE = ItemSome.newMagicStone().setRegistryName("magic_stone");
		ITEMS.KYNAITE_PICKAXE = new ItemKynaiteTools.ItemKynaitePickaxe().setRegistryName("kynaite_pickaxe");
		ITEMS.KYNAITE_AXE = new ItemKynaiteTools.ItemKynaiteAxe().setRegistryName("kynaite_axe");
		ITEMS.KYNAITE_SPADE = new ItemKynaiteTools.ItemKynaiteSpade().setRegistryName("kynaite_spade");
		ITEMS.KYNAITE_HOE = new ItemKynaiteTools.ItemKynaiteHoe().setRegistryName("kynaite_hoe");
		ITEMS.KYNAITE_SWORD = new ItemKynaiteTools.ItemKynaiteSword().setRegistryName("kynaite_sword");
		ITEMS.ARCHITECTURE_CRYSTAL = new ItemArchitectureCrystal().setRegistryName("architecture_crystal");
		ITEMS.ELEMENT_CRYSTAL = new ItemElementCrystal().setRegistryName("element_crystal");
		ITEMS.PARCHMENT = new ItemParchment().setRegistryName("parchment");
		ITEMS.SPELLBOOK_COVER = new ItemSpellbookCover().setRegistryName("spellbook_cover");
		ITEMS.SCROLL = new ItemScroll().setRegistryName("scroll");
		ITEMS.MANUAL = new ItemManual().setRegistryName("manual");
		ITEMS.MAGIC_RULER = new ItemMagicRuler().setRegistryName("magic_ruler");
		ITEMS.ITEM_CRYSTAL = new ItemItemCrystal().setRegistryName("item_crystal");

		ITEMS.SPELLBOOK = new ItemSpellbook().setRegistryName("spellbook");
		ITEMS.SPELLBOOK_ARCHITECTURE = new ItemSpellbookArchitecture().setRegistryName("spellbook_architecture");
		ITEMS.SPELLBOOK_ENCHANTMENT = new ItemSpellbookEnchantment().setRegistryName("spellbook_enchantment");
		ITEMS.SPELLBOOK_LAUNCH = new ItemSpellbookLaunch().setRegistryName("spellbook_launch");
		ITEMS.SPELLBOOK_ELEMENT = new ItemSpellbookElement().setRegistryName("spellbook_element");

		// 初始化所有tab
		Class<?> cls = ITEMS.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			((Item) field.get(ITEMS)).setCreativeTab(tab);
		}
	}

	private static final void instanceElements() {
		ELEMENTS.FIRE = new ElementFire().setRegistryName("fire");
		ELEMENTS.ENDER = new ElementEnder().setRegistryName("ender");
		ELEMENTS.WATER = new ElementWater().setRegistryName("water");
		ELEMENTS.AIR = new ElementAir().setRegistryName("air");
		ELEMENTS.EARTH = new ElementEarth().setRegistryName("earth");
		ELEMENTS.WOOD = new ElementWood().setRegistryName("wood");
		ELEMENTS.METAL = new ElementMetal().setRegistryName("metal");
		ELEMENTS.KNOWLEDGE = new ElementKnowledge().setRegistryName("knowledge");
	}

	private static final void instanceVillage() {
		ESInitInstance.VILLAGE.ES_VILLEGER = new VillagerRegistry.VillagerProfession("elementalsorcery:antique_dealer",
				"elementalsorcery:textures/entity/villager/es_studier.png",
				"elementalsorcery:textures/entity/zombie_villager/es_studier.png");
	}
}
