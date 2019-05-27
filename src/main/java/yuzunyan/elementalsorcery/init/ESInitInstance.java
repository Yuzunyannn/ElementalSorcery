package yuzunyan.elementalsorcery.init;

import yuzunyan.elementalsorcery.ESCreativeTabs;
import yuzunyan.elementalsorcery.api.ESObjects;
import yuzunyan.elementalsorcery.api.ESRegister;
import yuzunyan.elementalsorcery.api.element.Element;
import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.block.BlockAbsorbBox;
import yuzunyan.elementalsorcery.block.BlockDeconstructAltarTable;
import yuzunyan.elementalsorcery.block.BlockDeconstructBox;
import yuzunyan.elementalsorcery.block.BlockElementCraftingTable;
import yuzunyan.elementalsorcery.block.BlockElementWorkbench;
import yuzunyan.elementalsorcery.block.BlockElementalCube;
import yuzunyan.elementalsorcery.block.BlockHearth;
import yuzunyan.elementalsorcery.block.BlockInfusionBox;
import yuzunyan.elementalsorcery.block.BlockInvalidEnchantmentTable;
import yuzunyan.elementalsorcery.block.BlockKynaite;
import yuzunyan.elementalsorcery.block.BlockMagicDesk;
import yuzunyan.elementalsorcery.block.BlockMagicPlatform;
import yuzunyan.elementalsorcery.block.BlockSmeltBox;
import yuzunyan.elementalsorcery.block.BlockStela;
import yuzunyan.elementalsorcery.block.BlocksEStone;
import yuzunyan.elementalsorcery.crafting.RecipeManagement;
import yuzunyan.elementalsorcery.element.ElementAir;
import yuzunyan.elementalsorcery.element.ElementEarth;
import yuzunyan.elementalsorcery.element.ElementEnder;
import yuzunyan.elementalsorcery.element.ElementFire;
import yuzunyan.elementalsorcery.element.ElementKnowledge;
import yuzunyan.elementalsorcery.element.ElementMap;
import yuzunyan.elementalsorcery.element.ElementMetal;
import yuzunyan.elementalsorcery.element.ElementWater;
import yuzunyan.elementalsorcery.element.ElementWood;
import yuzunyan.elementalsorcery.init.registries.ElementRegister;
import yuzunyan.elementalsorcery.item.ItemArchitectureCrystal;
import yuzunyan.elementalsorcery.item.ItemElementCrystal;
import yuzunyan.elementalsorcery.item.ItemKynaiteTools;
import yuzunyan.elementalsorcery.item.ItemManual;
import yuzunyan.elementalsorcery.item.ItemParchment;
import yuzunyan.elementalsorcery.item.ItemScroll;
import yuzunyan.elementalsorcery.item.ItemSome;
import yuzunyan.elementalsorcery.item.ItemSpellbook;
import yuzunyan.elementalsorcery.item.ItemSpellbookArchitecture;
import yuzunyan.elementalsorcery.item.ItemSpellbookCover;
import yuzunyan.elementalsorcery.item.ItemSpellbookElement;
import yuzunyan.elementalsorcery.item.ItemSpellbookEnchantment;
import yuzunyan.elementalsorcery.item.ItemSpellbookLaunch;

public class ESInitInstance {

	public static ESObjects.Items ITEMS = new ESObjects.Items();
	public static ESObjects.Blocks BLOCKS = new ESObjects.Blocks();
	public static ESObjects.Elements ELEMENTS = new ESObjects.Elements();
	public static ESCreativeTabs tab;

	public static final void instance() {
		// ES注册
		ESRegister.ELEMENT = ElementRegister.instance;
		ESRegister.ELEMENT_MAP = ElementMap.instance;
		ESRegister.RECIPE = RecipeManagement.instance;
		// 实例句柄集
		ESObjects.ITEMS = ITEMS;
		ESObjects.BLOCKS = BLOCKS;
		ESObjects.ELEMENTS = ELEMENTS;
		// 创造物品栏
		ESCreativeTabs.TAB = new ESCreativeTabs();
		ESObjects.CREATIVE_TABS = tab;
		tab = ESCreativeTabs.TAB;
		// 初始化虚空元素
		ELEMENTS.VOID = new Element(Element.rgb(0, 0, 0)).setRegistryName("void").setUnlocalizedName("void");
		ElementStack.EMPTY = new ElementStack(ELEMENTS.VOID, 0);
		// 实例化方块和物品等
		instanceBlocks();
		instanceItems();
		instanceElements();
	}

	private static final void instanceBlocks() {

		BLOCKS.ESTONE = new BlocksEStone.EStone().setRegistryName("estone");
		BLOCKS.ESTONE_SLAB = new BlocksEStone.EStoneSlab().setRegistryName("estone_slab");
		BLOCKS.ESTONE_STAIRS = new BlocksEStone.EStoneStairs().setRegistryName("estone_stairs");

		BLOCKS.ESTONE.setCreativeTab(tab);
		BLOCKS.ESTONE_SLAB.setCreativeTab(tab);
		BLOCKS.ESTONE_STAIRS.setCreativeTab(tab);

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

		BLOCKS.ELEMENTAL_CUBE.setCreativeTab(tab);
		BLOCKS.HEARTH.setCreativeTab(tab);
		BLOCKS.SMELT_BOX.setCreativeTab(tab);
		BLOCKS.SMELT_BOX_IRON.setCreativeTab(tab);
		BLOCKS.SMELT_BOX_KYNAITE.setCreativeTab(tab);
		BLOCKS.KYNAITE_BLOCK.setCreativeTab(tab);
		BLOCKS.KYNAITE_ORE.setCreativeTab(tab);
		BLOCKS.MAGIC_PLATFORM.setCreativeTab(tab);
		BLOCKS.ABSORB_BOX.setCreativeTab(tab);
		BLOCKS.INVALID_ENCHANTMENT_TABLE.setCreativeTab(tab);
		BLOCKS.ELEMENT_WORKBENCH.setCreativeTab(tab);
		BLOCKS.DECONSTRUCT_BOX.setCreativeTab(tab);
		BLOCKS.INFUSION_BOX.setCreativeTab(tab);
		BLOCKS.MAGIC_DESK.setCreativeTab(tab);
		BLOCKS.ELEMENT_CRAFTING_TABLE.setCreativeTab(tab);
		BLOCKS.DECONSTRUCT_ALTAR_TABLE.setCreativeTab(tab);
		BLOCKS.STELA.setCreativeTab(tab);
	}

	private static final void instanceItems() {
		ITEMS.KYNAITE = ItemSome.newKynaite().setRegistryName("kynaite");
		ITEMS.MAGICAL_PIECE = ItemSome.newMagicalPiece().setRegistryName("magical_piece");
		ITEMS.MAGICAL_ENDER_EYE = ItemSome.newMagicalEnderEye().setRegistryName("magical_ender_eye");
		ITEMS.KYNAITE_PICKAXE = new ItemKynaiteTools.ItemKynaitePickaxe().setRegistryName("kynaite_pickaxe");
		ITEMS.KYNAITE_AXE = new ItemKynaiteTools.ItemKynaiteAxe().setRegistryName("kynaite_axe");
		ITEMS.KYNAITE_SPADE = new ItemKynaiteTools.ItemKynaiteSpade().setRegistryName("kynaite_spade");
		ITEMS.KYNAITE_HOE = new ItemKynaiteTools.ItemKynaiteHoe().setRegistryName("kynaite_hoe");
		ITEMS.KYNAITE_SWORD = new ItemKynaiteTools.ItemKynaiteSword().setRegistryName("kynaite_sword");
		ITEMS.ARCHITECTURE_CRYSTAL = new ItemArchitectureCrystal().setRegistryName("architecture_crystal");
		ITEMS.ELEMENT_CRYSTAL = new ItemElementCrystal().setRegistryName("element_crystal");
		ITEMS.MAGIC_CRYSTAL = ItemSome.newMagicalCrystal().setRegistryName("magic_crystal");
		ITEMS.PARCHMENT = new ItemParchment().setRegistryName("parchment");
		ITEMS.MAGIC_PAPER = ItemSome.newMagicPaper().setRegistryName("magic_paper");
		ITEMS.SPELL_PAPER = ItemSome.newSpellPaper().setRegistryName("spell_paper");
		ITEMS.SPELL_CRYSTAL = ItemSome.newSpellCrystal().setRegistryName("spell_crystal");
		ITEMS.SPELLBOOK_COVER = new ItemSpellbookCover().setRegistryName("spellbook_cover");
		ITEMS.SCROLL = new ItemScroll().setRegistryName("scroll");
		ITEMS.MANUAL = new ItemManual().setRegistryName("manual");

		ITEMS.KYNAITE.setCreativeTab(tab);
		ITEMS.MAGICAL_PIECE.setCreativeTab(tab);
		ITEMS.MAGICAL_ENDER_EYE.setCreativeTab(tab);
		ITEMS.KYNAITE_PICKAXE.setCreativeTab(tab);
		ITEMS.KYNAITE_AXE.setCreativeTab(tab);
		ITEMS.KYNAITE_SPADE.setCreativeTab(tab);
		ITEMS.KYNAITE_HOE.setCreativeTab(tab);
		ITEMS.KYNAITE_SWORD.setCreativeTab(tab);
		ITEMS.ARCHITECTURE_CRYSTAL.setCreativeTab(tab);
		ITEMS.ELEMENT_CRYSTAL.setCreativeTab(tab);
		ITEMS.MAGIC_CRYSTAL.setCreativeTab(tab);
		ITEMS.PARCHMENT.setCreativeTab(tab);
		ITEMS.MAGIC_PAPER.setCreativeTab(tab);
		ITEMS.SPELL_PAPER.setCreativeTab(tab);
		ITEMS.SPELL_CRYSTAL.setCreativeTab(tab);
		ITEMS.SPELLBOOK_COVER.setCreativeTab(tab);
		ITEMS.SCROLL.setCreativeTab(tab);
		ITEMS.MANUAL.setCreativeTab(tab);

		ITEMS.SPELLBOOK = new ItemSpellbook().setRegistryName("spellbook");
		ITEMS.SPELLBOOK_ARCHITECTURE = new ItemSpellbookArchitecture().setRegistryName("spellbook_architecture");
		ITEMS.SPELLBOOK_ENCHANTMENT = new ItemSpellbookEnchantment().setRegistryName("spellbook_enchantment");
		ITEMS.SPELLBOOK_LAUNCH = new ItemSpellbookLaunch().setRegistryName("spellbook_launch");
		ITEMS.SPELLBOOK_ELEMENT = new ItemSpellbookElement().setRegistryName("spellbook_element");

		ITEMS.SPELLBOOK.setCreativeTab(tab);
		ITEMS.SPELLBOOK_ARCHITECTURE.setCreativeTab(tab);
		ITEMS.SPELLBOOK_ENCHANTMENT.setCreativeTab(tab);
		ITEMS.SPELLBOOK_LAUNCH.setCreativeTab(tab);
		ITEMS.SPELLBOOK_ELEMENT.setCreativeTab(tab);
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
}
