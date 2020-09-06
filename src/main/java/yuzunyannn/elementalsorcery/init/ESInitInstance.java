package yuzunyannn.elementalsorcery.init;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import yuzunyannn.elementalsorcery.ESCreativeTabs;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.block.BlockAStone;
import yuzunyannn.elementalsorcery.block.BlockCrystalFlower;
import yuzunyannn.elementalsorcery.block.BlockElementWorkbench;
import yuzunyannn.elementalsorcery.block.BlockElfFruit;
import yuzunyannn.elementalsorcery.block.BlockElfLeaf;
import yuzunyannn.elementalsorcery.block.BlockElfLog;
import yuzunyannn.elementalsorcery.block.BlockElfPlank;
import yuzunyannn.elementalsorcery.block.BlockElfSapling;
import yuzunyannn.elementalsorcery.block.BlockInvalidEnchantmentTable;
import yuzunyannn.elementalsorcery.block.BlockKyanite;
import yuzunyannn.elementalsorcery.block.BlockLifeDirt;
import yuzunyannn.elementalsorcery.block.BlockLifeFlower;
import yuzunyannn.elementalsorcery.block.BlockMagicPot;
import yuzunyannn.elementalsorcery.block.BlockMagicTorch;
import yuzunyannn.elementalsorcery.block.BlockStarSand;
import yuzunyannn.elementalsorcery.block.BlockStarStone;
import yuzunyannn.elementalsorcery.block.BlocksEStone;
import yuzunyannn.elementalsorcery.block.altar.BlockAnalysisAltar;
import yuzunyannn.elementalsorcery.block.altar.BlockBuildingAltar;
import yuzunyannn.elementalsorcery.block.altar.BlockDeconstructAltarTable;
import yuzunyannn.elementalsorcery.block.altar.BlockDeconstructAltarTableAdv;
import yuzunyannn.elementalsorcery.block.altar.BlockElementCraftingTable;
import yuzunyannn.elementalsorcery.block.altar.BlockElementalCube;
import yuzunyannn.elementalsorcery.block.altar.BlockMagicDesk;
import yuzunyannn.elementalsorcery.block.altar.BlockPortalAltar;
import yuzunyannn.elementalsorcery.block.altar.BlockSupremeTable;
import yuzunyannn.elementalsorcery.block.altar.BlockTranscribeInjection;
import yuzunyannn.elementalsorcery.block.altar.BlockTranscribeTable;
import yuzunyannn.elementalsorcery.block.container.BlockAbsorbBox;
import yuzunyannn.elementalsorcery.block.container.BlockDeconstructBox;
import yuzunyannn.elementalsorcery.block.container.BlockHearth;
import yuzunyannn.elementalsorcery.block.container.BlockItemStructureCraftNormal;
import yuzunyannn.elementalsorcery.block.container.BlockLantern;
import yuzunyannn.elementalsorcery.block.container.BlockMagicPlatform;
import yuzunyannn.elementalsorcery.block.container.BlockMeltCauldron;
import yuzunyannn.elementalsorcery.block.container.BlockRiteTable;
import yuzunyannn.elementalsorcery.block.container.BlockSmeltBox;
import yuzunyannn.elementalsorcery.block.container.BlockStela;
import yuzunyannn.elementalsorcery.block.container.BlockStoneMill;
import yuzunyannn.elementalsorcery.block.md.BlockMDAbsorbBox;
import yuzunyannn.elementalsorcery.block.md.BlockMDDeconstructBox;
import yuzunyannn.elementalsorcery.block.md.BlockMDHearth;
import yuzunyannn.elementalsorcery.block.md.BlockMDInfusion;
import yuzunyannn.elementalsorcery.block.md.BlockMDMagicGen;
import yuzunyannn.elementalsorcery.block.md.BlockMDMagicSolidify;
import yuzunyannn.elementalsorcery.block.md.BlockMDMagiclization;
import yuzunyannn.elementalsorcery.block.md.BlockMDResonantIncubator;
import yuzunyannn.elementalsorcery.block.md.BlockMDRubbleRepair;
import yuzunyannn.elementalsorcery.block.md.BlockMDTransfer;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementAir;
import yuzunyannn.elementalsorcery.element.ElementEarth;
import yuzunyannn.elementalsorcery.element.ElementEnder;
import yuzunyannn.elementalsorcery.element.ElementFire;
import yuzunyannn.elementalsorcery.element.ElementKnowledge;
import yuzunyannn.elementalsorcery.element.ElementMagic;
import yuzunyannn.elementalsorcery.element.ElementMetal;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.element.ElementWater;
import yuzunyannn.elementalsorcery.element.ElementWood;
import yuzunyannn.elementalsorcery.item.ItemAncientPaper;
import yuzunyannn.elementalsorcery.item.ItemElfPurse;
import yuzunyannn.elementalsorcery.item.ItemGrimoire;
import yuzunyannn.elementalsorcery.item.ItemKyaniteTools;
import yuzunyannn.elementalsorcery.item.ItemMagicRuler;
import yuzunyannn.elementalsorcery.item.ItemManual;
import yuzunyannn.elementalsorcery.item.ItemNatureDust;
import yuzunyannn.elementalsorcery.item.ItemParchment;
import yuzunyannn.elementalsorcery.item.ItemRedHandset;
import yuzunyannn.elementalsorcery.item.ItemRiteManual;
import yuzunyannn.elementalsorcery.item.ItemScroll;
import yuzunyannn.elementalsorcery.item.ItemSome;
import yuzunyannn.elementalsorcery.item.ItemSpellbook;
import yuzunyannn.elementalsorcery.item.ItemSpellbookArchitecture;
import yuzunyannn.elementalsorcery.item.ItemSpellbookCover;
import yuzunyannn.elementalsorcery.item.ItemSpellbookElement;
import yuzunyannn.elementalsorcery.item.ItemSpellbookEnchantment;
import yuzunyannn.elementalsorcery.item.ItemSpellbookLaunch;
import yuzunyannn.elementalsorcery.item.ItemSupremeTableComponent;
import yuzunyannn.elementalsorcery.item.crystal.ItemArchitectureCrystal;
import yuzunyannn.elementalsorcery.item.crystal.ItemCrystal;
import yuzunyannn.elementalsorcery.item.crystal.ItemElementCrystal;
import yuzunyannn.elementalsorcery.item.crystal.ItemItemCrystal;
import yuzunyannn.elementalsorcery.item.crystal.ItemMagicalCrystal;
import yuzunyannn.elementalsorcery.item.crystal.ItemNatureCrystal;
import yuzunyannn.elementalsorcery.item.crystal.ItemResonantCrystal;

public class ESInitInstance {

	public static final ESObjects.Items ITEMS = new ESObjects.Items();
	public static final ESObjects.Blocks BLOCKS = new ESObjects.Blocks();
	public static final ESObjects.Elements ELEMENTS = new ESObjects.Elements();
	public static final ESObjects.Village VILLAGE = new ESObjects.Village();
	public static final ESCreativeTabs tab = ESCreativeTabs.TAB;

	public static final void instance() throws ReflectiveOperationException {
		// 实例句柄集
		ESObjects.ITEMS = ITEMS;
		ESObjects.BLOCKS = BLOCKS;
		ESObjects.ELEMENTS = ELEMENTS;
		ESObjects.VILLAGE = VILLAGE;
		// 创造物品栏
		ESObjects.CREATIVE_TABS = tab;
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
		// 其他位置句柄获取
		ItemCrystal.init();
	}

	private static void initVoidElement() throws ReflectiveOperationException {
		Field field = ElementStack.class.getDeclaredField("element");
		field.setAccessible(true);
		field.set(ElementStack.EMPTY, ELEMENTS.VOID);
	}

	private static final void instanceBlocks() throws ReflectiveOperationException {

		BLOCKS.ESTONE = new BlocksEStone.EStone();
		BLOCKS.ESTONE_SLAB = new BlocksEStone.EStoneSlab();
		BLOCKS.ESTONE_STAIRS = new BlocksEStone.EStoneStairs();
		BLOCKS.ESTONE_PRISM = new BlocksEStone.EStonePrism();
		BLOCKS.ASTONE = new BlockAStone();

		BLOCKS.ELEMENTAL_CUBE = new BlockElementalCube();
		BLOCKS.HEARTH = new BlockHearth();
		BLOCKS.SMELT_BOX = new BlockSmeltBox(BlockHearth.EnumMaterial.COBBLESTONE);
		BLOCKS.SMELT_BOX_IRON = new BlockSmeltBox(BlockHearth.EnumMaterial.IRON);
		BLOCKS.SMELT_BOX_KYANITE = new BlockSmeltBox(BlockHearth.EnumMaterial.KYANITE);
		BLOCKS.KYANITE_BLOCK = new BlockKyanite();
		BLOCKS.KYANITE_ORE = new BlockKyanite.BlockKyaniteOre();
		BLOCKS.MAGIC_PLATFORM = new BlockMagicPlatform();
		BLOCKS.ABSORB_BOX = new BlockAbsorbBox();
		BLOCKS.INVALID_ENCHANTMENT_TABLE = new BlockInvalidEnchantmentTable();
		BLOCKS.ELEMENT_WORKBENCH = new BlockElementWorkbench();
		BLOCKS.DECONSTRUCT_BOX = new BlockDeconstructBox();
		BLOCKS.MAGIC_DESK = new BlockMagicDesk();
		BLOCKS.ELEMENT_CRAFTING_TABLE = new BlockElementCraftingTable();
		BLOCKS.DECONSTRUCT_ALTAR_TABLE = new BlockDeconstructAltarTable();
		BLOCKS.DECONSTRUCT_ALTAR_TABLE_ADV = new BlockDeconstructAltarTableAdv();
		BLOCKS.STELA = new BlockStela();
		BLOCKS.RITE_TABLE = new BlockRiteTable();
		BLOCKS.LANTERN = new BlockLantern();
		BLOCKS.BUILDING_ALTAR = new BlockBuildingAltar();
		BLOCKS.ANALYSIS_ALTAR = new BlockAnalysisAltar();
		BLOCKS.SUPREME_TABLE = new BlockSupremeTable();
		BLOCKS.MAGIC_TORCH = new BlockMagicTorch();
		BLOCKS.STAR_STONE = new BlockStarStone();
		BLOCKS.STAR_SAND = new BlockStarSand();
		BLOCKS.STONE_MILL = new BlockStoneMill();
		BLOCKS.MELT_CAULDRON = new BlockMeltCauldron();
		BLOCKS.ELF_LOG = new BlockElfLog(false);
		BLOCKS.ELF_LOG_CABIN_CENTER = new BlockElfLog(true);
		BLOCKS.ELF_LEAF = new BlockElfLeaf();
		BLOCKS.ELF_PLANK = new BlockElfPlank();
		BLOCKS.ELF_SAPLING = new BlockElfSapling();
		BLOCKS.ELF_FRUIT = new BlockElfFruit();
		BLOCKS.MD_MAGIC_GEN = new BlockMDMagicGen();
		BLOCKS.MD_HEARTH = new BlockMDHearth();
		BLOCKS.MD_RUBBLE_REPAIR = new BlockMDRubbleRepair();
		BLOCKS.MD_INFUSION = new BlockMDInfusion();
		BLOCKS.MD_TRANSFER = new BlockMDTransfer();
		BLOCKS.MD_MAGIC_SOLIDIFY = new BlockMDMagicSolidify();
		BLOCKS.MD_ABSORB_BOX = new BlockMDAbsorbBox();
		BLOCKS.MD_MAGICLIZATION = new BlockMDMagiclization();
		BLOCKS.MD_DECONSTRUCT_BOX = new BlockMDDeconstructBox();
		BLOCKS.MD_RESONANT_INCUBATOR = new BlockMDResonantIncubator();
		BLOCKS.LIFE_FLOWER = new BlockLifeFlower();
		BLOCKS.MAGIC_POT = new BlockMagicPot();
		BLOCKS.LIFE_DIRT = new BlockLifeDirt();
		BLOCKS.CRYSTAL_FLOWER = new BlockCrystalFlower();
		BLOCKS.IS_CRAFT_NORMAL = new BlockItemStructureCraftNormal();
		BLOCKS.PORTAL_ALTAR = new BlockPortalAltar();
		BLOCKS.TRANSCRIBE_TABLE = new BlockTranscribeTable();
		BLOCKS.TRANSCRIBE_INJECTION = new BlockTranscribeInjection();
		// 初始化所有tab
		Class<?> cls = BLOCKS.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			Block block = ((Block) field.get(BLOCKS));
			block.setCreativeTab(tab);
			block.setRegistryName(field.getName().toLowerCase());
		}
	}

	private static final void instanceItems() throws ReflectiveOperationException {
		ITEMS.KYANITE = ItemSome.newKyanite();
		ITEMS.MAGIC_PIECE = ItemSome.newMagicalPiece();
		ITEMS.MAGICAL_ENDER_EYE = ItemSome.newMagicalEnderEye();
		ITEMS.MAGIC_CRYSTAL = new ItemMagicalCrystal();
		ITEMS.TINY_KNIFE = ItemSome.newTinyKnife();
		ITEMS.MAGIC_PAPER = ItemSome.newMagicPaper();
		ITEMS.SPELL_PAPER = ItemSome.newSpellPaper();
		ITEMS.SPELL_CRYSTAL = ItemCrystal.newSpellCrystal();
		ITEMS.MAGIC_STONE = ItemSome.newMagicStone();
		ITEMS.KYANITE_PICKAXE = new ItemKyaniteTools.ItemKyanitePickaxe();
		ITEMS.KYANITE_AXE = new ItemKyaniteTools.ItemKyaniteAxe();
		ITEMS.KYANITE_SPADE = new ItemKyaniteTools.ItemKyaniteSpade();
		ITEMS.KYANITE_HOE = new ItemKyaniteTools.ItemKyaniteHoe();
		ITEMS.KYANITE_SWORD = new ItemKyaniteTools.ItemKyaniteSword();
		ITEMS.ARCHITECTURE_CRYSTAL = new ItemArchitectureCrystal();
		ITEMS.ELEMENT_CRYSTAL = new ItemElementCrystal();
		ITEMS.PARCHMENT = new ItemParchment();
		ITEMS.SPELLBOOK_COVER = new ItemSpellbookCover();
		ITEMS.SCROLL = new ItemScroll();
		ITEMS.MANUAL = new ItemManual();
		ITEMS.MAGIC_RULER = new ItemMagicRuler();
		ITEMS.ITEM_CRYSTAL = new ItemItemCrystal();
		ITEMS.ORDER_CRYSTAL = ItemCrystal.newOrderCrystal();
		ITEMS.MD_BASE = ItemSome.newMDBase();
		ITEMS.RITE_MANUAL = new ItemRiteManual();
		ITEMS.RED_HANDSET = new ItemRedHandset();
		ITEMS.AZURE_CRYSTAL = ItemCrystal.newAzureCrystal();
		ITEMS.RESONANT_CRYSTAL = new ItemResonantCrystal();
		ITEMS.ELF_CRYSTAL = ItemCrystal.newElfCrystal();
		ITEMS.SUPREME_TABLE_COMPONENT = new ItemSupremeTableComponent();
		ITEMS.ELF_COIN = ItemSome.newElfCoin();
		ITEMS.ELF_PURSE = new ItemElfPurse();
		ITEMS.NATURE_CRYSTAL = new ItemNatureCrystal();
		ITEMS.NATURE_DUST = new ItemNatureDust();
		ITEMS.ANCIENT_PAPER = new ItemAncientPaper();

		ITEMS.GRIMOIRE = new ItemGrimoire();
		ITEMS.SPELLBOOK = new ItemSpellbook();
		ITEMS.SPELLBOOK_ARCHITECTURE = new ItemSpellbookArchitecture();
		ITEMS.SPELLBOOK_ENCHANTMENT = new ItemSpellbookEnchantment();
		ITEMS.SPELLBOOK_LAUNCH = new ItemSpellbookLaunch();
		ITEMS.SPELLBOOK_ELEMENT = new ItemSpellbookElement();

		// 初始化所有tab
		Class<?> cls = ITEMS.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			Item item = ((Item) field.get(ITEMS));
			item.setCreativeTab(tab);
			item.setRegistryName(field.getName().toLowerCase());
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
