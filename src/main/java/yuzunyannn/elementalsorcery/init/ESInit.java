package yuzunyannn.elementalsorcery.init;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemMultiTexture.Mapper;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ESCreativeTabs;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.block.BlockAStone;
import yuzunyannn.elementalsorcery.block.BlockCrudeQuartz;
import yuzunyannn.elementalsorcery.block.BlockCrystalFlower;
import yuzunyannn.elementalsorcery.block.BlockElementWorkbench;
import yuzunyannn.elementalsorcery.block.BlockElfFruit;
import yuzunyannn.elementalsorcery.block.BlockElfLeaf;
import yuzunyannn.elementalsorcery.block.BlockElfLog;
import yuzunyannn.elementalsorcery.block.BlockElfPlank;
import yuzunyannn.elementalsorcery.block.BlockElfSapling;
import yuzunyannn.elementalsorcery.block.BlockFluorspar;
import yuzunyannn.elementalsorcery.block.BlockInvalidEnchantmentTable;
import yuzunyannn.elementalsorcery.block.BlockKyanite;
import yuzunyannn.elementalsorcery.block.BlockLifeDirt;
import yuzunyannn.elementalsorcery.block.BlockLifeFlower;
import yuzunyannn.elementalsorcery.block.BlockMagicPot;
import yuzunyannn.elementalsorcery.block.BlockMagicTorch;
import yuzunyannn.elementalsorcery.block.BlockResearcher;
import yuzunyannn.elementalsorcery.block.BlockScarletCrystalOre;
import yuzunyannn.elementalsorcery.block.BlockSealStone;
import yuzunyannn.elementalsorcery.block.BlockStarFlower;
import yuzunyannn.elementalsorcery.block.BlockStarSand;
import yuzunyannn.elementalsorcery.block.BlockStarStone;
import yuzunyannn.elementalsorcery.block.BlocksEStone;
import yuzunyannn.elementalsorcery.block.altar.BlockAnalysisAltar;
import yuzunyannn.elementalsorcery.block.altar.BlockBuildingAltar;
import yuzunyannn.elementalsorcery.block.altar.BlockDeconstructAltarTable;
import yuzunyannn.elementalsorcery.block.altar.BlockDeconstructAltarTableAdv;
import yuzunyannn.elementalsorcery.block.altar.BlockDeconstructWindmill;
import yuzunyannn.elementalsorcery.block.altar.BlockElementCraftingTable;
import yuzunyannn.elementalsorcery.block.altar.BlockElementTranslocator;
import yuzunyannn.elementalsorcery.block.altar.BlockElementalCube;
import yuzunyannn.elementalsorcery.block.altar.BlockMagicDesk;
import yuzunyannn.elementalsorcery.block.altar.BlockPortalAltar;
import yuzunyannn.elementalsorcery.block.altar.BlockSupremeTable;
import yuzunyannn.elementalsorcery.block.altar.BlockTranscribeInjection;
import yuzunyannn.elementalsorcery.block.altar.BlockTranscribeTable;
import yuzunyannn.elementalsorcery.block.container.BlockElementPlatform;
import yuzunyannn.elementalsorcery.block.container.BlockElfBeacon;
import yuzunyannn.elementalsorcery.block.container.BlockElfTreeCore;
import yuzunyannn.elementalsorcery.block.container.BlockHearth;
import yuzunyannn.elementalsorcery.block.container.BlockItemStructureCraftNormal;
import yuzunyannn.elementalsorcery.block.container.BlockLantern;
import yuzunyannn.elementalsorcery.block.container.BlockMagicPlatform;
import yuzunyannn.elementalsorcery.block.container.BlockMeltCauldron;
import yuzunyannn.elementalsorcery.block.container.BlockRiteTable;
import yuzunyannn.elementalsorcery.block.container.BlockSmeltBox;
import yuzunyannn.elementalsorcery.block.container.BlockStoneMill;
import yuzunyannn.elementalsorcery.block.md.BlockMDAbsorbBox;
import yuzunyannn.elementalsorcery.block.md.BlockMDDeconstructBox;
import yuzunyannn.elementalsorcery.block.md.BlockMDFrequencyMapping;
import yuzunyannn.elementalsorcery.block.md.BlockMDHearth;
import yuzunyannn.elementalsorcery.block.md.BlockMDInfusion;
import yuzunyannn.elementalsorcery.block.md.BlockMDMagicGen;
import yuzunyannn.elementalsorcery.block.md.BlockMDMagicSolidify;
import yuzunyannn.elementalsorcery.block.md.BlockMDMagiclization;
import yuzunyannn.elementalsorcery.block.md.BlockMDResonantIncubator;
import yuzunyannn.elementalsorcery.block.md.BlockMDRubbleRepair;
import yuzunyannn.elementalsorcery.block.md.BlockMDTransfer;
import yuzunyannn.elementalsorcery.building.BuildingLib;
import yuzunyannn.elementalsorcery.capability.Adventurer;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.capability.FairyCubeMaster;
import yuzunyannn.elementalsorcery.capability.Spellbook;
import yuzunyannn.elementalsorcery.config.ESConfig;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunch;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementAir;
import yuzunyannn.elementalsorcery.element.ElementEarth;
import yuzunyannn.elementalsorcery.element.ElementEnder;
import yuzunyannn.elementalsorcery.element.ElementFire;
import yuzunyannn.elementalsorcery.element.ElementKnowledge;
import yuzunyannn.elementalsorcery.element.ElementMagic;
import yuzunyannn.elementalsorcery.element.ElementMetal;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.element.ElementStar;
import yuzunyannn.elementalsorcery.element.ElementWater;
import yuzunyannn.elementalsorcery.element.ElementWood;
import yuzunyannn.elementalsorcery.elf.AutoName;
import yuzunyannn.elementalsorcery.elf.quest.IAdventurer;
import yuzunyannn.elementalsorcery.elf.quest.Quests;
import yuzunyannn.elementalsorcery.elf.research.KnowledgeType;
import yuzunyannn.elementalsorcery.elf.research.Topic;
import yuzunyannn.elementalsorcery.entity.fcube.IFairyCubeMaster;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.event.EventServer;
import yuzunyannn.elementalsorcery.event.KeyBoard;
import yuzunyannn.elementalsorcery.explore.ExploreManagement;
import yuzunyannn.elementalsorcery.grimoire.Grimoire;
import yuzunyannn.elementalsorcery.grimoire.mantra.Mantra;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraArrow;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraBlockCrash;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraEnderTeleport;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFireArea;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFireBall;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFireCharge;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFloat;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFloatArea;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFluorspar;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFootbridge;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraLaunch;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraLightningArea;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraLush;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraMagicStrafe;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraMiningArea;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraPotent;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraSlowFall;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraSprint;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraSummon;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraTimeHourglass;
import yuzunyannn.elementalsorcery.item.ItemAddressPlate;
import yuzunyannn.elementalsorcery.item.ItemAncientPaper;
import yuzunyannn.elementalsorcery.item.ItemAppleCandy;
import yuzunyannn.elementalsorcery.item.ItemElementBoard;
import yuzunyannn.elementalsorcery.item.ItemElfFruitBomb;
import yuzunyannn.elementalsorcery.item.ItemElfPurse;
import yuzunyannn.elementalsorcery.item.ItemElfWatch;
import yuzunyannn.elementalsorcery.item.ItemFairyCube;
import yuzunyannn.elementalsorcery.item.ItemFairyCubeModule;
import yuzunyannn.elementalsorcery.item.ItemGlassCup;
import yuzunyannn.elementalsorcery.item.ItemManual;
import yuzunyannn.elementalsorcery.item.ItemMerchantInvitation;
import yuzunyannn.elementalsorcery.item.ItemNatureDust;
import yuzunyannn.elementalsorcery.item.ItemParcel;
import yuzunyannn.elementalsorcery.item.ItemParchment;
import yuzunyannn.elementalsorcery.item.ItemQuest;
import yuzunyannn.elementalsorcery.item.ItemRiteManual;
import yuzunyannn.elementalsorcery.item.ItemScapegoat;
import yuzunyannn.elementalsorcery.item.ItemScroll;
import yuzunyannn.elementalsorcery.item.ItemUnscrambleNote;
import yuzunyannn.elementalsorcery.item.ItemWindmillBlade;
import yuzunyannn.elementalsorcery.item.ItemWindmillBlades;
import yuzunyannn.elementalsorcery.item.book.ItemGrimoire;
import yuzunyannn.elementalsorcery.item.book.ItemSpellbook;
import yuzunyannn.elementalsorcery.item.book.ItemSpellbookArchitecture;
import yuzunyannn.elementalsorcery.item.book.ItemSpellbookElement;
import yuzunyannn.elementalsorcery.item.book.ItemSpellbookEnchantment;
import yuzunyannn.elementalsorcery.item.book.ItemSpellbookLaunch;
import yuzunyannn.elementalsorcery.item.crystal.ItemArchitectureCrystal;
import yuzunyannn.elementalsorcery.item.crystal.ItemCrystal;
import yuzunyannn.elementalsorcery.item.crystal.ItemElementCrystal;
import yuzunyannn.elementalsorcery.item.crystal.ItemItemCrystal;
import yuzunyannn.elementalsorcery.item.crystal.ItemMagicalCrystal;
import yuzunyannn.elementalsorcery.item.crystal.ItemNatureCrystal;
import yuzunyannn.elementalsorcery.item.crystal.ItemOrderCrystal;
import yuzunyannn.elementalsorcery.item.crystal.ItemResonantCrystal;
import yuzunyannn.elementalsorcery.item.crystal.ItemScarletCrystal;
import yuzunyannn.elementalsorcery.item.prop.ItemCubeCore;
import yuzunyannn.elementalsorcery.item.prop.ItemDejectedTear;
import yuzunyannn.elementalsorcery.item.prop.ItemDreadGem;
import yuzunyannn.elementalsorcery.item.prop.ItemElementStone;
import yuzunyannn.elementalsorcery.item.prop.ItemFusionCrystal;
import yuzunyannn.elementalsorcery.item.prop.ItemKeepsake;
import yuzunyannn.elementalsorcery.item.prop.ItemLifeLeather;
import yuzunyannn.elementalsorcery.item.prop.ItemMagicCore;
import yuzunyannn.elementalsorcery.item.prop.ItemMagicPaper;
import yuzunyannn.elementalsorcery.item.prop.ItemQuill;
import yuzunyannn.elementalsorcery.item.prop.ItemRabidLeather;
import yuzunyannn.elementalsorcery.item.prop.ItemSome;
import yuzunyannn.elementalsorcery.item.prop.ItemSoulFragment;
import yuzunyannn.elementalsorcery.item.prop.ItemSpellbookCover;
import yuzunyannn.elementalsorcery.item.prop.ItemSupremeTableComponent;
import yuzunyannn.elementalsorcery.item.prop.ItemVortex;
import yuzunyannn.elementalsorcery.item.prop.ItemWindmillBladeFrame;
import yuzunyannn.elementalsorcery.item.tool.ItemCubeDemarcator;
import yuzunyannn.elementalsorcery.item.tool.ItemKyaniteTools;
import yuzunyannn.elementalsorcery.item.tool.ItemMagicBlastWand;
import yuzunyannn.elementalsorcery.item.tool.ItemMagicGoldTools;
import yuzunyannn.elementalsorcery.item.tool.ItemMagicRuler;
import yuzunyannn.elementalsorcery.item.tool.ItemRedHandset;
import yuzunyannn.elementalsorcery.item.tool.ItemRockCamera;
import yuzunyannn.elementalsorcery.item.tool.ItemSoulKillerSword;
import yuzunyannn.elementalsorcery.item.tool.ItemSoulWoodSword;
import yuzunyannn.elementalsorcery.item.tool.ItemStarBell;
import yuzunyannn.elementalsorcery.network.ESNetwork;
import yuzunyannn.elementalsorcery.parchment.Pages;
import yuzunyannn.elementalsorcery.potion.PotionFireWalker;
import yuzunyannn.elementalsorcery.potion.PotionTideWalker;
import yuzunyannn.elementalsorcery.potion.PotionTimeSlow;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.item.RenderItemFairyCube;
import yuzunyannn.elementalsorcery.render.item.RenderItemFairyCubeModule;
import yuzunyannn.elementalsorcery.render.item.RenderItemGlassCup;
import yuzunyannn.elementalsorcery.render.item.RenderItemGrimoire;
import yuzunyannn.elementalsorcery.render.item.RenderItemMagicBlastWand;
import yuzunyannn.elementalsorcery.render.item.RenderItemSpellbook;
import yuzunyannn.elementalsorcery.render.item.RenderItemSupremeTable;
import yuzunyannn.elementalsorcery.render.item.SpellbookRenderInfo;
import yuzunyannn.elementalsorcery.render.tile.RenderTileAnalysisAltar;
import yuzunyannn.elementalsorcery.render.tile.RenderTileBuildingAltar;
import yuzunyannn.elementalsorcery.render.tile.RenderTileCrystalFlower;
import yuzunyannn.elementalsorcery.render.tile.RenderTileDeconstructAltarTable;
import yuzunyannn.elementalsorcery.render.tile.RenderTileDeconstructWindmill;
import yuzunyannn.elementalsorcery.render.tile.RenderTileElementCraftingTable;
import yuzunyannn.elementalsorcery.render.tile.RenderTileElementPlatform;
import yuzunyannn.elementalsorcery.render.tile.RenderTileElementTranslocator;
import yuzunyannn.elementalsorcery.render.tile.RenderTileElementalCube;
import yuzunyannn.elementalsorcery.render.tile.RenderTileElfBeacon;
import yuzunyannn.elementalsorcery.render.tile.RenderTileElfTreeCore;
import yuzunyannn.elementalsorcery.render.tile.RenderTileLantern;
import yuzunyannn.elementalsorcery.render.tile.RenderTileMagicDesk;
import yuzunyannn.elementalsorcery.render.tile.RenderTileMagicPlatform;
import yuzunyannn.elementalsorcery.render.tile.RenderTileMeltCauldron;
import yuzunyannn.elementalsorcery.render.tile.RenderTileRiteTable;
import yuzunyannn.elementalsorcery.render.tile.RenderTileShowItem;
import yuzunyannn.elementalsorcery.render.tile.RenderTileStoneMill;
import yuzunyannn.elementalsorcery.render.tile.RenderTileSupremeTable;
import yuzunyannn.elementalsorcery.render.tile.RenderTileTranscribeInjection;
import yuzunyannn.elementalsorcery.render.tile.RenderTileTranscribeTable;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDAbsorbBox;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDBase;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDDeconstructBox;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDFrequencyMapping;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDHearth;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDInfusion;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDMagicGen;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDMagicSolidify;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDMagiclization;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDResonantIncubator;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDRubbleRepair;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDTransfer;
import yuzunyannn.elementalsorcery.summon.recipe.SummonRecipe;
import yuzunyannn.elementalsorcery.tile.TileCrystalFlower;
import yuzunyannn.elementalsorcery.tile.TileElementPlatform;
import yuzunyannn.elementalsorcery.tile.TileElfBeacon;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;
import yuzunyannn.elementalsorcery.tile.TileHearth;
import yuzunyannn.elementalsorcery.tile.TileItemStructureCraftNormal;
import yuzunyannn.elementalsorcery.tile.TileLantern;
import yuzunyannn.elementalsorcery.tile.TileLifeDirt;
import yuzunyannn.elementalsorcery.tile.TileMagicPlatform;
import yuzunyannn.elementalsorcery.tile.TileMeltCauldron;
import yuzunyannn.elementalsorcery.tile.TileRiteTable;
import yuzunyannn.elementalsorcery.tile.TileSmeltBox;
import yuzunyannn.elementalsorcery.tile.TileStarFlower;
import yuzunyannn.elementalsorcery.tile.TileStoneMill;
import yuzunyannn.elementalsorcery.tile.altar.TileAnalysisAltar;
import yuzunyannn.elementalsorcery.tile.altar.TileBuildingAltar;
import yuzunyannn.elementalsorcery.tile.altar.TileDeconstructAltarTable;
import yuzunyannn.elementalsorcery.tile.altar.TileDeconstructAltarTableAdv;
import yuzunyannn.elementalsorcery.tile.altar.TileDeconstructWindmill;
import yuzunyannn.elementalsorcery.tile.altar.TileElementCraftingTable;
import yuzunyannn.elementalsorcery.tile.altar.TileElementTranslocator;
import yuzunyannn.elementalsorcery.tile.altar.TileElementalCube;
import yuzunyannn.elementalsorcery.tile.altar.TileMagicDesk;
import yuzunyannn.elementalsorcery.tile.altar.TilePortalAltar;
import yuzunyannn.elementalsorcery.tile.altar.TileSupremeTable;
import yuzunyannn.elementalsorcery.tile.altar.TileTranscribeInjection;
import yuzunyannn.elementalsorcery.tile.altar.TileTranscribeTable;
import yuzunyannn.elementalsorcery.tile.md.TileMDAbsorbBox;
import yuzunyannn.elementalsorcery.tile.md.TileMDDeconstructBox;
import yuzunyannn.elementalsorcery.tile.md.TileMDFrequencyMapping;
import yuzunyannn.elementalsorcery.tile.md.TileMDHearth;
import yuzunyannn.elementalsorcery.tile.md.TileMDInfusion;
import yuzunyannn.elementalsorcery.tile.md.TileMDMagicGen;
import yuzunyannn.elementalsorcery.tile.md.TileMDMagicSolidify;
import yuzunyannn.elementalsorcery.tile.md.TileMDMagiclization;
import yuzunyannn.elementalsorcery.tile.md.TileMDResonantIncubator;
import yuzunyannn.elementalsorcery.tile.md.TileMDRubbleRepair;
import yuzunyannn.elementalsorcery.tile.md.TileMDTransfer;
import yuzunyannn.elementalsorcery.util.render.Shaders;
import yuzunyannn.elementalsorcery.util.render.WorldScene;
import yuzunyannn.elementalsorcery.worldgen.WorldGeneratorES;

public class ESInit {

	public static final List<Class<? extends TileEntity>> ES_TILE_ENTITY = new ArrayList<>();

	public static final ESObjects.Items ITEMS = new ESObjects.Items();
	public static final ESObjects.Blocks BLOCKS = new ESObjects.Blocks();
	public static final ESObjects.Elements ELEMENTS = new ESObjects.Elements();
	public static final ESObjects.Mantras MANTRAS = new ESObjects.Mantras();
	public static final ESObjects.Potions POTIONS = new ESObjects.Potions();
	public static final ESObjects.Village VILLAGE = new ESObjects.Village();
	public static final ESCreativeTabs tab = ESCreativeTabs.TAB;

	public static final void instance() throws ReflectiveOperationException {
		// 实例句柄集
		ESObjects.ITEMS = ITEMS;
		ESObjects.BLOCKS = BLOCKS;
		ESObjects.ELEMENTS = ELEMENTS;
		ESObjects.MANTRAS = MANTRAS;
		ESObjects.POTIONS = POTIONS;
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
		instanceMantras();
		instancePotions();
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
		BLOCKS.INVALID_ENCHANTMENT_TABLE = new BlockInvalidEnchantmentTable();
		BLOCKS.ELEMENT_WORKBENCH = new BlockElementWorkbench();
		BLOCKS.MAGIC_DESK = new BlockMagicDesk();
		BLOCKS.ELEMENT_CRAFTING_TABLE = new BlockElementCraftingTable();
		BLOCKS.DECONSTRUCT_ALTAR_TABLE = new BlockDeconstructAltarTable();
		BLOCKS.DECONSTRUCT_ALTAR_TABLE_ADV = new BlockDeconstructAltarTableAdv();
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
		BLOCKS.ELF_LOG = new BlockElfLog();
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
		BLOCKS.MD_FREQUENCY_MAPPING = new BlockMDFrequencyMapping();
		BLOCKS.LIFE_FLOWER = new BlockLifeFlower();
		BLOCKS.MAGIC_POT = new BlockMagicPot();
		BLOCKS.LIFE_DIRT = new BlockLifeDirt();
		BLOCKS.CRYSTAL_FLOWER = new BlockCrystalFlower();
		BLOCKS.IS_CRAFT_NORMAL = new BlockItemStructureCraftNormal();
		BLOCKS.PORTAL_ALTAR = new BlockPortalAltar();
		BLOCKS.TRANSCRIBE_TABLE = new BlockTranscribeTable();
		BLOCKS.TRANSCRIBE_INJECTION = new BlockTranscribeInjection();
		BLOCKS.ELF_TREE_CORE = new BlockElfTreeCore();
		BLOCKS.ELF_BEACON = new BlockElfBeacon();
		BLOCKS.RESEARCHER = new BlockResearcher();
		BLOCKS.SEAL_STONE = new BlockSealStone();
		BLOCKS.SCARLET_CRYSTAL_ORE = new BlockScarletCrystalOre();
		BLOCKS.STAR_FLOWER = new BlockStarFlower();
		BLOCKS.CRUDE_QUARTZ = new BlockCrudeQuartz();
		BLOCKS.ELEMENT_PLATFORM = new BlockElementPlatform();
		BLOCKS.FLUORSPAR = new BlockFluorspar();
		BLOCKS.DECONSTRUCT_WINDMILL = new BlockDeconstructWindmill();
		BLOCKS.ELEMENT_TRANSLOCATOR = new BlockElementTranslocator();

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
		ITEMS.MAGIC_PAPER = new ItemMagicPaper();
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
		ITEMS.ORDER_CRYSTAL = new ItemOrderCrystal();
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
		ITEMS.QUEST = new ItemQuest();
		ITEMS.ELF_WATCH = new ItemElfWatch();
		ITEMS.MAGIC_GOLD = ItemSome.newMagicGold();
		ITEMS.MAGIC_GOLD_PICKAXE = new ItemMagicGoldTools.ItemMagicGoldPickaxe();
		ITEMS.MAGIC_GOLD_AXE = new ItemMagicGoldTools.ItemMagicGoldAxe();
		ITEMS.MAGIC_GOLD_SPADE = new ItemMagicGoldTools.ItemMagicGoldSpade();
		ITEMS.MAGIC_GOLD_HOE = new ItemMagicGoldTools.ItemMagicGoldHoe();
		ITEMS.MAGIC_GOLD_SWORD = new ItemMagicGoldTools.ItemMagicGoldSword();
		ITEMS.PARCEL = new ItemParcel();
		ITEMS.ADDRESS_PLATE = new ItemAddressPlate();
		ITEMS.ELF_STAR = ItemSome.newElfStar();
		ITEMS.JUMP_GEM = ItemSome.newJumpGem();
		ITEMS.UNSCRAMBLE_NOTE = new ItemUnscrambleNote();
		ITEMS.SOUL_FRAGMENT = new ItemSoulFragment();
		ITEMS.SOUL_WOOD_SWORD = new ItemSoulWoodSword();
		ITEMS.RELIC_GEM = ItemSome.newRelicGem();
		ITEMS.ROCK_CAMERA = new ItemRockCamera();
		ITEMS.KEEPSAKE = new ItemKeepsake();
		ITEMS.QUILL = new ItemQuill();
		ITEMS.FUSION_CRYSTAL = new ItemFusionCrystal();
		ITEMS.VORTEX = new ItemVortex();
		ITEMS.ELEMENT_STONE = new ItemElementStone();
		ITEMS.LIFE_LEATHER = new ItemLifeLeather();
		ITEMS.MAGIC_BLAST_WAND = new ItemMagicBlastWand();
		ITEMS.SOUL_KILLER_SWORD = new ItemSoulKillerSword();
		ITEMS.SCAPEGOAT = new ItemScapegoat();
		ITEMS.MAGIC_CORE = new ItemMagicCore();
		ITEMS.SCARLET_CRYSTAL = new ItemScarletCrystal();
		ITEMS.STAR_BELL = new ItemStarBell();
		ITEMS.APPLE_CANDY = new ItemAppleCandy();
		ITEMS.FAIRY_CUBE = new ItemFairyCube();
		ITEMS.FAIRY_CUBE_MODULE = new ItemFairyCubeModule();
		ITEMS.RABID_LEATHER = new ItemRabidLeather();
		ITEMS.CUBE_CORE = new ItemCubeCore();
		ITEMS.DREAD_GEM = new ItemDreadGem();
		ITEMS.DEJECTED_TEAR = new ItemDejectedTear();
		ITEMS.MERCHANT_INVITATION = new ItemMerchantInvitation();
		ITEMS.ELEMENT_BOARD = new ItemElementBoard();
		ITEMS.CUBE_DEMARCATOR = new ItemCubeDemarcator();
		ITEMS.WINDMILL_BLADE_FRAME = new ItemWindmillBladeFrame();
		ITEMS.WINDMILL_BLADE = new ItemWindmillBlade();
		ITEMS.WINDMILL_BLADE_ASTONE = new ItemWindmillBlades.AStone();
		ITEMS.WINDMILL_BLADE_WOOD = new ItemWindmillBlades.WOOD();
		ITEMS.WINDMILL_BLADE_CRYSTAL = new ItemWindmillBlades.CRYSTAL();
		ITEMS.ELF_FRUIT_BOMB = new ItemElfFruitBomb();
		ITEMS.GLASS_CUP = new ItemGlassCup();

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

	private static final void instanceMantras() throws ReflectiveOperationException {

		MANTRAS.ENDER_TELEPORT = new MantraEnderTeleport();
		MANTRAS.FLOAT = new MantraFloat();
		MANTRAS.SPRINT = new MantraSprint();
		MANTRAS.FIRE_BALL = new MantraFireBall();
		MANTRAS.LUSH = new MantraLush();
		MANTRAS.BLOCK_CRASH = new MantraBlockCrash();
		MANTRAS.MINING_AREA = new MantraMiningArea();
		MANTRAS.LIGHTNING_AREA = new MantraLightningArea();
		MANTRAS.SUMMON = new MantraSummon();
		MANTRAS.SLOW_FALL = new MantraSlowFall();
		MANTRAS.FOOTBRIDGE = new MantraFootbridge();
		MANTRAS.FIRE_AREA = new MantraFireArea();
		MANTRAS.MAGIC_STRAFE = new MantraMagicStrafe();
		MANTRAS.FLOAT_AREA = new MantraFloatArea();
		MANTRAS.FIRE_CHARGE = new MantraFireCharge();
		MANTRAS.ARROW = new MantraArrow();
		MANTRAS.POTENT = new MantraPotent();
		MANTRAS.FLUORSPAR = new MantraFluorspar();
		MANTRAS.TIME_HOURGLASS = new MantraTimeHourglass();

		MANTRAS.LAUNCH_ECR = new MantraLaunch(ICraftingLaunch.TYPE_ELEMENT_CRAFTING, 0xffec3d);
		MANTRAS.LAUNCH_EDE = new MantraLaunch(ICraftingLaunch.TYPE_ELEMENT_DECONSTRUCT, 0xff4a1a);
		MANTRAS.LAUNCH_ECO = new MantraLaunch(ICraftingLaunch.TYPE_ELEMENT_CONSTRUCT, 0x00b5e5);
		MANTRAS.LAUNCH_BRC = new MantraLaunch(ICraftingLaunch.TYPE_BUILING_RECORD, 0x18632b);

		// 初始化所有
		Class<?> cls = MANTRAS.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			Mantra mantra = ((Mantra) field.get(MANTRAS));
			mantra.setRegistryName(field.getName().toLowerCase());
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
		ELEMENTS.STAR = new ElementStar().setRegistryName("star");
	}

	private static final void instancePotions() throws ReflectiveOperationException {
		POTIONS.TIME_SLOW = new PotionTimeSlow();
		POTIONS.FIRE_WALKER = new PotionFireWalker();
		POTIONS.TIDE_WALKER = new PotionTideWalker();

		Class<?> cls = POTIONS.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			Potion potion = ((Potion) field.get(POTIONS));
			potion.setRegistryName(field.getName().toLowerCase());
		}
	}

	private static final void instanceVillage() {
		ESInit.VILLAGE.ES_VILLEGER = new VillagerRegistry.VillagerProfession("elementalsorcery:antique_dealer",
				"elementalsorcery:textures/entity/villager/es_studier.png",
				"elementalsorcery:textures/entity/zombie_villager/es_studier.png");
	}

	// 正式开始进行注册

	public final static void preInit(FMLPreInitializationEvent event) throws Throwable {
		// 注册物品
		registerAllItems();
		// 注册方块
		registerAllBlocks();
		// 注册tileentity
		registerAllTiles();
		// 注册元素
		Element.registerAll();
		// 注册能力
		registerAllCapability();
		// 矿物词典注册
		OreDictionaryRegistries.registerAll();
		// 注册实体
		EntityRegistries.registerAll();
		// 注册精灵相关
		ElfRegister.registerAllProfession();
		AutoName.init();
		// 注册默认所有建筑
		BuildingLib.registerAll();
		// 注册咒文
		registerAllMantras();
		KnowledgeType.registerAll();
		// 召唤注册
		SummonRecipe.registerAll();
		// 探索数据注册
		ExploreManagement.registerAll();
		// 注册精灵大厦楼层
		ElfRegister.registerAllFloor();
		// 任务相关注册
		QuestRegister.registerAll();
		// 附魔注册
		EnchantmentRegister.registerAll();
		// bufff
		registerAllPotions();
		// 精灵立方体模块注册
		FairyCubeModuleRegister.registerAll();
		// 测试村庄相关
		VillegeRegistries.registerAll();
		// 注册战利品
		LootRegister.registerAll();
		// 成就触发器
		ESCriteriaTriggers.init();
		// 注册GUI句柄
		NetworkRegistry.INSTANCE.registerGuiHandler(ElementalSorcery.instance, new ESGuiHandler());
		// 注册世界生成
		MinecraftForge.ORE_GEN_BUS.register(new WorldGeneratorES.GenOre());
		MinecraftForge.EVENT_BUS.register(new WorldGeneratorES.GenDecorate());
		// MinecraftForge.TERRAIN_GEN_BUS.register(new WorldGeneratorES.GenTerrain());
		// 注册网络
		ESNetwork.registerAll();
		// 注册事件
		MinecraftForge.EVENT_BUS.register(EventServer.class);
	}

	public final static void init(FMLInitializationEvent event) throws Throwable {
		// 注册元素映射
		ElementMap.registerAll();
		// 注册所有配方
		ESCraftingRegistries.registerAll();
		// 初始化所有说明界面
		Pages.init(event.getSide());
		// 注册所有知识
		TileRiteTable.init();
		// 所有任务
		Quests.loadAll();
	}

	public final static void postInit(FMLPostInitializationEvent event) throws Throwable {
		// 通过查找注册元素映射
		ElementMap.findAndRegisterCraft();
	}

	@SideOnly(Side.CLIENT)
	public final static void preInitClient(FMLPreInitializationEvent event) throws Throwable {
		// 设置自定义模型加载
		TileItemRenderRegistries.instance = new TileItemRenderRegistries();
		ModelLoaderRegistry.registerLoader(TileItemRenderRegistries.instance);
		// 注册所有渲染
		registerAllRender();
		// 注册实体渲染
		EntityRegistries.registerAllRender();
		// 研究绘图注册
		Topic.registerAll();
		// 所有需要网传的特效
		Effects.registerAll();
		// 精灵立方体图标注册
		FairyCubeModuleRegister.registerAllRender();
		// 客户端事件
		MinecraftForge.EVENT_BUS.register(EventClient.class);
		// 世界离屏渲染
		if (ESConfig.PORTAL_RENDER_TYPE == 2) WorldScene.init();
		// 所有shader
		Shaders.init();
	}

	@SideOnly(Side.CLIENT)
	public final static void initClinet(FMLInitializationEvent event) {
		// 注册按键绑定
		KeyBoard.registerAll();
	}

	@SideOnly(Side.CLIENT)
	public final static void postInitClinet(FMLPostInitializationEvent event) {
		// 注册所有渲染
		registerAllRenderPost();
	}

	static void registerAllItems() throws IllegalArgumentException, IllegalAccessException {
		// 遍历所有，将所有内容注册
		Class<?> cls = ESInit.ITEMS.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			register((Item) field.get(ESInit.ITEMS));
		}
	}

	static void registerAllBlocks() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// 遍历所有，将所有内容注册
		Class<?> cls = ESInit.BLOCKS.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			Block block = (Block) field.get(ESInit.BLOCKS);
			try {
				Method method = block.getClass().getDeclaredMethod("getItemBlock");
				register(block, (ItemBlock) method.invoke(block));
			} catch (NoSuchMethodException e) {
				register(block);
			}
		}
	}

	static void registerAllMantras() throws IllegalArgumentException, IllegalAccessException {
		// 遍历所有，将所有内容注册
		Class<?> cls = ESInit.MANTRAS.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			register((Mantra) field.get(ESInit.MANTRAS));
		}
	}

	static void registerAllPotions() throws IllegalArgumentException, IllegalAccessException {
		Class<?> cls = ESInit.POTIONS.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			register((Potion) field.get(ESInit.POTIONS));
		}
	}

	static void registerAllTiles() {
		register(TileElementalCube.class, "ElementalCube");
		register(TileHearth.class, "Hearth");
		register(TileSmeltBox.class, "SmeltBox");
		register(TileMagicPlatform.class, "MagicPlatform");
		register(TileMagicDesk.class, "MagicDesk");
		register(TileElementCraftingTable.class, "ElementCraftingTable");
		register(TileDeconstructAltarTable.class, "DeconstructAltarTable");
		register(TileDeconstructAltarTableAdv.class, "DeconstructAltarTableAdv");
		register(TileRiteTable.class, "riteTable");
		register(TileLantern.class, "Lantern");
		register(TileBuildingAltar.class, "BuildingAltar");
		register(TileAnalysisAltar.class, "AnalysisAltar");
		register(TileSupremeTable.class, "SupremeTable");
		register(TileStoneMill.class, "StoneMill");
		register(TileMeltCauldron.class, "MeltCauldron");
		register(TileMDMagicGen.class, "MDMagicGen");
		register(TileMDHearth.class, "MDHearth");
		register(TileMDRubbleRepair.class, "MDRubbleRepair");
		register(TileMDInfusion.class, "MDfusion");
		register(TileMDTransfer.class, "MDTransfer");
		register(TileMDMagicSolidify.class, "MDMagicSolidify");
		register(TileMDAbsorbBox.class, "MDAbsorbBox");
		register(TileMDMagiclization.class, "MDMagiclization");
		register(TileMDDeconstructBox.class, "MDDeconstructBox");
		register(TileMDResonantIncubator.class, "MDResonantIncubator");
		register(TileMDFrequencyMapping.class, "MDFrequencyMapping");
		register(TileLifeDirt.class, "LifeDirt");
		register(TileCrystalFlower.class, "CrystalFlower");
		register(TileItemStructureCraftNormal.class, "ISCraftNormal");
		register(TilePortalAltar.class, "PortalAltar");
		register(TileTranscribeTable.class, "TranscribeTable");
		register(TileTranscribeInjection.class, "TranscribeInjection");
		register(TileElfTreeCore.class, "ElfTreeCore");
		register(TileElfBeacon.class, "ElfBeacon");
		register(TileStarFlower.class, "StarFlower");
		register(TileElementPlatform.class, "ElementPlatform");
		register(TileDeconstructWindmill.class, "DeconstructWindmill");
		register(TileElementTranslocator.class, "ElementTranslocator");
	}

	static void registerAllCapability() {
		register(IElementInventory.class, new ElementInventory.Storage(), ElementInventory.class);
		register(Spellbook.class, new Spellbook.Storage(), Spellbook.class);
		register(Grimoire.class, new Grimoire.Storage(), Grimoire.class);
		register(IAdventurer.class, new Adventurer.Storage(), Adventurer.class);
		register(IFairyCubeMaster.class, new FairyCubeMaster.Storage(), FairyCubeMaster.class);
	}

	@SideOnly(Side.CLIENT)
	static void registerAllRender() {
		final ESObjects.Blocks BLOCKS = ESInit.BLOCKS;
		final ESObjects.Items ITEMS = ESInit.ITEMS;
		// 初始化句柄
		SpellbookRenderInfo.renderInstance = RenderItemSpellbook.instance;

		registerRender(ITEMS.KYANITE);
		registerRender(ITEMS.MAGIC_PIECE);
		registerRender(ITEMS.MAGICAL_ENDER_EYE);
		registerRender(ITEMS.KYANITE_PICKAXE);
		registerRender(ITEMS.KYANITE_AXE);
		registerRender(ITEMS.KYANITE_SPADE);
		registerRender(ITEMS.KYANITE_HOE);
		registerRender(ITEMS.KYANITE_SWORD);
		registerRender(ITEMS.ARCHITECTURE_CRYSTAL);
		registerRender(ITEMS.ELEMENT_CRYSTAL);
		registerRender(ITEMS.MAGIC_CRYSTAL);
		registerRender(ITEMS.PARCHMENT);
		for (ItemMagicPaper.EnumType paperType : ItemMagicPaper.EnumType.values())
			registerRender(ITEMS.MAGIC_PAPER, paperType.getMeta(), paperType.getName() + "_paper");
		registerRender(ITEMS.SPELL_CRYSTAL);
		registerRender(ITEMS.SPELLBOOK_COVER, 0, "spellbook_cover");
		registerRender(ITEMS.SPELLBOOK_COVER, 1, "spellbook_back_cover");
		registerRender(ITEMS.SCROLL);
		registerRender(ITEMS.MANUAL);
		registerRender(ITEMS.MAGIC_RULER);
		registerRender(ITEMS.ITEM_CRYSTAL);
		registerRender(ITEMS.MAGIC_STONE);
		registerRender(ITEMS.TINY_KNIFE);
		registerRender(ITEMS.ORDER_CRYSTAL);
		registerRender(ITEMS.MD_BASE, new RenderTileMDBase());
		registerRender(ITEMS.RITE_MANUAL);
		registerRender(ITEMS.RED_HANDSET);
		registerRender(ITEMS.AZURE_CRYSTAL);
		registerRender(ITEMS.RESONANT_CRYSTAL);
		registerRender(ITEMS.ELF_CRYSTAL);
		registerRender(ITEMS.SUPREME_TABLE_COMPONENT, new RenderItemSupremeTable());
		registerRender(ITEMS.ELF_COIN);
		registerRender(ITEMS.ELF_PURSE);
		registerRender(ITEMS.NATURE_CRYSTAL);
		registerRender(ITEMS.NATURE_DUST, 0, "nature_dust");
		registerRender(ITEMS.NATURE_DUST, 1, "explore_dust");
		registerRender(ITEMS.NATURE_DUST, 2, "explore_adv_dust");
		registerRender(ITEMS.ANCIENT_PAPER, 0, "ancient_paper");
		registerRender(ITEMS.ANCIENT_PAPER, 1, "ancient_paper_unscramble");
		registerRender(ITEMS.ANCIENT_PAPER, 2, "ancient_paper_new");
		registerRender(ITEMS.ANCIENT_PAPER, 3, "ancient_paper_new_written");
		registerRender(ITEMS.QUEST, 0, "quest");
		registerRender(ITEMS.QUEST, 1, "quest_finish");
		registerRender(ITEMS.ELF_WATCH);
		registerRender(ITEMS.MAGIC_GOLD);
		registerRender(ITEMS.MAGIC_GOLD_PICKAXE);
		registerRender(ITEMS.MAGIC_GOLD_AXE);
		registerRender(ITEMS.MAGIC_GOLD_SPADE);
		registerRender(ITEMS.MAGIC_GOLD_HOE);
		registerRender(ITEMS.MAGIC_GOLD_SWORD);
		registerRender(ITEMS.PARCEL, 0, "parcel_pack");
		registerRender(ITEMS.PARCEL, 1, "parcel_open");
		registerRender(ITEMS.ADDRESS_PLATE, 0, "address_plate");
		registerRender(ITEMS.ADDRESS_PLATE, 1, "address_plate_vip");
		registerRender(ITEMS.ELF_STAR);
		registerRender(ITEMS.JUMP_GEM);
		registerRender(ITEMS.UNSCRAMBLE_NOTE);
		registerRender(ITEMS.SOUL_FRAGMENT);
		registerRender(ITEMS.SOUL_WOOD_SWORD);
		registerRender(ITEMS.RELIC_GEM);
		registerRender(ITEMS.ROCK_CAMERA);
		for (ItemKeepsake.EnumType keepsakeType : ItemKeepsake.EnumType.values())
			registerRender(ITEMS.KEEPSAKE, keepsakeType.getMeta(), keepsakeType.getName());
		for (ItemQuill.EnumType quillType : ItemQuill.EnumType.values())
			registerRender(ITEMS.QUILL, quillType.getMeta(), "quill_" + quillType.getName());
		registerRender(ITEMS.FUSION_CRYSTAL);
		registerRender(ITEMS.VORTEX);
		registerRender(ITEMS.ELEMENT_STONE);
		registerRender(ITEMS.LIFE_LEATHER, 0, "life_leather_incomplete");
		registerRender(ITEMS.LIFE_LEATHER, 1, "life_leather");
		registerRender(ITEMS.MAGIC_BLAST_WAND, new RenderItemMagicBlastWand());
		registerRender(ITEMS.SOUL_KILLER_SWORD);
		registerRender(ITEMS.SCAPEGOAT);
		registerRender(ITEMS.MAGIC_CORE);
		registerRender(ITEMS.SCARLET_CRYSTAL);
		registerRender(ITEMS.STAR_BELL, 0, "bell");
		registerRender(ITEMS.STAR_BELL, 1, "BELL_STAR");
		registerRender(ITEMS.FAIRY_CUBE, new RenderItemFairyCube());
		registerRender(ITEMS.FAIRY_CUBE_MODULE, new RenderItemFairyCubeModule());
		registerRender(ITEMS.RABID_LEATHER);
		registerRender(ITEMS.APPLE_CANDY);
		registerRender(ITEMS.CUBE_CORE);
		registerRender(ITEMS.DREAD_GEM);
		registerRender(ITEMS.DEJECTED_TEAR);
		registerRender(ITEMS.MERCHANT_INVITATION);
		registerRender(ITEMS.ELEMENT_BOARD);
		registerRender(ITEMS.CUBE_DEMARCATOR);
		registerRender(ITEMS.WINDMILL_BLADE_FRAME, 0, "windmill_blade_frame");
		registerRender(ITEMS.WINDMILL_BLADE_FRAME, 1, "windmill_blade_frame_adv");
		registerRender(ITEMS.WINDMILL_BLADE);
		registerRender(ITEMS.WINDMILL_BLADE_ASTONE);
		registerRender(ITEMS.WINDMILL_BLADE_WOOD);
		registerRender(ITEMS.WINDMILL_BLADE_CRYSTAL);
		registerRender(ITEMS.ELF_FRUIT_BOMB);
		registerRender(ITEMS.GLASS_CUP, new RenderItemGlassCup());

		registerStateMapper(BLOCKS.HEARTH, BlockHearth.MATERIAL, "hearth");
		registerRender(BLOCKS.HEARTH, 0, "cobblestone_hearth");
		registerRender(BLOCKS.HEARTH, 1, "iron_hearth");
		registerRender(BLOCKS.HEARTH, 2, "kyanite_hearth");
		registerRender(BLOCKS.SMELT_BOX);
		registerRender(BLOCKS.SMELT_BOX_IRON);
		registerRender(BLOCKS.SMELT_BOX_KYANITE);
		registerRender(BLOCKS.KYANITE_ORE);
		registerRender(BLOCKS.KYANITE_BLOCK);
		registerRender(BLOCKS.ESTONE, 0, "estone_default");
		registerRender(BLOCKS.ESTONE, 1, "estone_chiseled");
		registerRender(BLOCKS.ESTONE, 2, "estone_lines");
		registerRender(BLOCKS.ESTONE_SLAB);
		registerRender(BLOCKS.ESTONE_STAIRS);
		registerRender(BLOCKS.MAGIC_PLATFORM, 0, "magic_platform");
		registerRender(BLOCKS.MAGIC_PLATFORM, 1, "magic_platform_estone");
		registerRender(BLOCKS.INVALID_ENCHANTMENT_TABLE);
		registerRender(BLOCKS.ELEMENT_WORKBENCH);
		registerRender(BLOCKS.MAGIC_TORCH);
		registerRender(BLOCKS.ASTONE, 0, "astone");
		registerRender(BLOCKS.ASTONE, 1, "astone_fragmented");
		registerRender(BLOCKS.ASTONE, 2, "astone_smooth");
		registerRender(BLOCKS.ASTONE, 3, "astone_vein");
		registerRender(BLOCKS.ASTONE, 4, "astone_circle");
		registerRender(BLOCKS.ASTONE, 5, "astone_brick");
		registerRender(BLOCKS.ASTONE, 6, "astone_trans");
		registerRender(BLOCKS.STAR_STONE);
		registerRender(BLOCKS.STAR_SAND);
		registerRender(BLOCKS.ELF_LOG);
		registerRender(BLOCKS.ELF_LEAF);
		registerStateMapper(BLOCKS.ELF_LEAF,
				new StateMap.Builder().ignore(BlockElfLeaf.CHECK_DECAY, BlockElfLeaf.DECAYABLE).build());
		registerRender(BLOCKS.ELF_SAPLING);
		registerStateMapper(BLOCKS.ELF_FRUIT, new StateMap.Builder().ignore(BlockElfFruit.STAGE).build());
		registerRender(BLOCKS.ELF_FRUIT, 0);
		registerRender(BLOCKS.ELF_FRUIT, BlockElfFruit.MAX_STATE);
		registerRender(BLOCKS.ELF_PLANK, 0);
		registerRender(BLOCKS.ELF_PLANK, 1, "elf_plank_dark");
		registerRender(BLOCKS.LIFE_FLOWER);
		registerRender(BLOCKS.MAGIC_POT);
		registerRender(BLOCKS.LIFE_DIRT);
		registerRender(BLOCKS.CRYSTAL_FLOWER);
		registerRender(BLOCKS.IS_CRAFT_NORMAL);
		registerRender(BLOCKS.ESTONE_PRISM);
		registerRender(BLOCKS.PORTAL_ALTAR);
		registerRender(BLOCKS.TRANSCRIBE_TABLE);
		registerRender(BLOCKS.TRANSCRIBE_INJECTION);
		registerRender(BLOCKS.RESEARCHER);
		registerRender(BLOCKS.SEAL_STONE, 0, "seal_stone");
		registerRender(BLOCKS.SEAL_STONE, 1, "seal_stone_netherrack");
		registerRender(BLOCKS.SCARLET_CRYSTAL_ORE);
		registerRender(BLOCKS.STAR_FLOWER, 0, "star_flower_seed");
		registerRender(BLOCKS.STAR_FLOWER, 2, "star_flower");
		registerRender(BLOCKS.STAR_FLOWER, 4, "star_flower_element");
		registerRender(BLOCKS.CRUDE_QUARTZ);
		registerRender(BLOCKS.FLUORSPAR, 0, "fluorspar_stone");
		registerRender(BLOCKS.FLUORSPAR, 1, "fluorspar_cobblestone");
		registerRender(BLOCKS.FLUORSPAR, 2, "fluorspar_dirt");
		registerRender(BLOCKS.FLUORSPAR, 3, "fluorspar_netherrack");
		registerRender(BLOCKS.FLUORSPAR, 4, "fluorspar_andesite");
		registerRender(BLOCKS.FLUORSPAR, 5, "fluorspar_granite");
		registerRender(BLOCKS.FLUORSPAR, 6, "fluorspar_diorite");

		registerRender(TileMagicPlatform.class, new RenderTileMagicPlatform());
		registerRender(TileCrystalFlower.class, new RenderTileCrystalFlower());
		registerRender(TilePortalAltar.class, new RenderTileShowItem<TilePortalAltar>(0.65));
		registerRender(TileTranscribeTable.class, new RenderTileTranscribeTable());

		registerRender(BLOCKS.ELEMENTAL_CUBE, TileElementalCube.class, new RenderTileElementalCube());
		registerRender(BLOCKS.MAGIC_DESK, TileMagicDesk.class, new RenderTileMagicDesk());
		registerRender(BLOCKS.ELEMENT_CRAFTING_TABLE, TileElementCraftingTable.class,
				new RenderTileElementCraftingTable());
		registerRender(BLOCKS.DECONSTRUCT_ALTAR_TABLE, TileDeconstructAltarTable.class,
				new RenderTileDeconstructAltarTable(false));
		registerRender(BLOCKS.DECONSTRUCT_ALTAR_TABLE_ADV, TileDeconstructAltarTableAdv.class,
				new RenderTileDeconstructAltarTable(true));
		registerRender(BLOCKS.RITE_TABLE, TileRiteTable.class, new RenderTileRiteTable());
		registerRender(BLOCKS.LANTERN, TileLantern.class, new RenderTileLantern());
		registerRender(BLOCKS.BUILDING_ALTAR, TileBuildingAltar.class, new RenderTileBuildingAltar());
		registerRender(BLOCKS.ANALYSIS_ALTAR, TileAnalysisAltar.class, new RenderTileAnalysisAltar());
		registerRender(BLOCKS.SUPREME_TABLE, TileSupremeTable.class, new RenderTileSupremeTable());
		registerRender(BLOCKS.STONE_MILL, TileStoneMill.class, new RenderTileStoneMill());
		registerRender(BLOCKS.MELT_CAULDRON, TileMeltCauldron.class, new RenderTileMeltCauldron());
		registerRender(BLOCKS.MD_MAGIC_GEN, TileMDMagicGen.class, new RenderTileMDMagicGen());
		registerRender(BLOCKS.MD_HEARTH, TileMDHearth.class, new RenderTileMDHearth());
		registerRender(BLOCKS.MD_RUBBLE_REPAIR, TileMDRubbleRepair.class, new RenderTileMDRubbleRepair());
		registerRender(BLOCKS.MD_INFUSION, TileMDInfusion.class, new RenderTileMDInfusion());
		registerRender(BLOCKS.MD_TRANSFER, TileMDTransfer.class, new RenderTileMDTransfer());
		registerRender(BLOCKS.MD_MAGIC_SOLIDIFY, TileMDMagicSolidify.class, new RenderTileMDMagicSolidify());
		registerRender(BLOCKS.MD_ABSORB_BOX, TileMDAbsorbBox.class, new RenderTileMDAbsorbBox());
		registerRender(BLOCKS.MD_MAGICLIZATION, TileMDMagiclization.class, new RenderTileMDMagiclization());
		registerRender(BLOCKS.MD_DECONSTRUCT_BOX, TileMDDeconstructBox.class, new RenderTileMDDeconstructBox());
		registerRender(BLOCKS.MD_RESONANT_INCUBATOR, TileMDResonantIncubator.class,
				new RenderTileMDResonantIncubator());
		registerRender(BLOCKS.MD_FREQUENCY_MAPPING, TileMDFrequencyMapping.class, new RenderTileMDFrequencyMapping());
		registerRender(BLOCKS.ELF_TREE_CORE, TileElfTreeCore.class, new RenderTileElfTreeCore());
		registerRender(BLOCKS.TRANSCRIBE_INJECTION, TileTranscribeInjection.class, new RenderTileTranscribeInjection());
		registerRender(BLOCKS.ELF_BEACON, TileElfBeacon.class, new RenderTileElfBeacon());
		registerRender(BLOCKS.ELEMENT_PLATFORM, TileElementPlatform.class, new RenderTileElementPlatform());
		registerRender(BLOCKS.DECONSTRUCT_WINDMILL, TileDeconstructWindmill.class, new RenderTileDeconstructWindmill());
		registerRender(BLOCKS.ELEMENT_TRANSLOCATOR, TileElementTranslocator.class, new RenderTileElementTranslocator());

		registerRender(ITEMS.GRIMOIRE, new RenderItemGrimoire());
		registerRender(ITEMS.SPELLBOOK, RenderItemSpellbook.instance);
		registerRender(ITEMS.SPELLBOOK_ARCHITECTURE, RenderItemSpellbook.instance);
		registerRender(ITEMS.SPELLBOOK_ENCHANTMENT, RenderItemSpellbook.instance);
		registerRender(ITEMS.SPELLBOOK_LAUNCH, RenderItemSpellbook.instance);
		registerRender(ITEMS.SPELLBOOK_ELEMENT, RenderItemSpellbook.instance);
	}

	@SideOnly(Side.CLIENT)
	static void registerAllRenderPost() {
		registerRenderColor(ESInit.BLOCKS.ELF_LEAF, ((BlockElfLeaf) ESInit.BLOCKS.ELF_LEAF).getBlockColor());
		registerRenderColor(ESInit.BLOCKS.ELF_FRUIT, ((BlockElfFruit) ESInit.BLOCKS.ELF_FRUIT).getBlockColor());
	}

	// 分离的注册函数

	private static <T, U extends Capability.IStorage<T>, V extends T> void register(Class<T> _interface, U storage,
			Class<V> icalss) {
		CapabilityManager.INSTANCE.register(_interface, storage, new Callable<V>() {
			@Override
			public V call() throws Exception {
				return icalss.newInstance();
			}
		});
	}

	private static void register(Mantra mantra) {
		Mantra.REGISTRY.register(mantra);
	}

	private static void register(Item item) {
		ForgeRegistries.ITEMS.register(item);
	}

	private static void register(Block block) {
		if (block instanceof Mapper) register(block, (Mapper) block);
		else register(block, new ItemBlock(block));
	}

	private static void register(Block block, Mapper mapper) {
		register(block, new ItemMultiTexture(block, block, mapper));
	}

	private static void register(Block block, ItemBlock itemBlock) {
		ForgeRegistries.BLOCKS.register(block);
		ForgeRegistries.ITEMS.register(itemBlock.setRegistryName(block.getRegistryName()));
	}

	private static void register(Class<? extends TileEntity> tileEntityClass, String id) {
		GameRegistry.registerTileEntity(tileEntityClass, new ResourceLocation(ElementalSorcery.MODID, id));
		ES_TILE_ENTITY.add(tileEntityClass);
	}

	private static void register(Potion potion) {
		ForgeRegistries.POTIONS.register(potion);
	}

	@SideOnly(Side.CLIENT)
	private static void registerRender(Item item) {
		registerRender(item, 0);
	}

	@SideOnly(Side.CLIENT)
	private static void registerRender(Item item, int meta) {
		ModelResourceLocation model = new ModelResourceLocation(item.getRegistryName(), "inventory");
		ModelLoader.setCustomModelResourceLocation(item, meta, model);
	}

	@SideOnly(Side.CLIENT)
	private static void registerRender(Item item, int meta, String id) {
		ResourceLocation location = new ResourceLocation(item.getRegistryName().getResourceDomain(), id);
		ModelResourceLocation model = new ModelResourceLocation(location, "inventory");
		ModelLoader.setCustomModelResourceLocation(item, meta, model);
	}

	@SideOnly(Side.CLIENT)
	private static void registerRender(Item item, ItemMeshDefinition meshDefinition) {
		ModelLoader.setCustomMeshDefinition(item, meshDefinition);
	}

	@SideOnly(Side.CLIENT)
	private static void registerRenderNoMeta(Item item) {
		registerRender(item, new ItemMeshDefinition() {
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack) {
				return new ModelResourceLocation(item.getRegistryName(), "inventory");
			}
		});
	}

	@SideOnly(Side.CLIENT)
	private static void registerRender(Item item, IRenderItem item_render) {
		TileItemRenderRegistries.instance.register(item, item_render);
	}

	@SideOnly(Side.CLIENT)
	private static <T extends TileEntity> void registerRender(Class<T> tile,
			TileEntitySpecialRenderer<? super T> renderer) {
		ClientRegistry.bindTileEntitySpecialRenderer(tile, renderer);
	}

	@SideOnly(Side.CLIENT)
	private static void registerRender(Block block, ItemMeshDefinition meshDefinition) {
		registerRender(Item.getItemFromBlock(block), meshDefinition);
	}

	@SideOnly(Side.CLIENT)
	private static void registerRenderColor(Block block, IBlockColor blockColor) {
		registerRender(block, blockColor);
		registerRender(block, new IItemColor() {
			public int colorMultiplier(ItemStack stack, int tintIndex) {
				IBlockState iblockstate = ((ItemBlock) stack.getItem()).getBlock()
						.getStateFromMeta(stack.getMetadata());
				return blockColor.colorMultiplier(iblockstate, (IBlockAccess) null, (BlockPos) null, tintIndex);
			}
		});
	}

	@SideOnly(Side.CLIENT)
	private static void registerRender(Block block, IBlockColor blockColor) {
		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(blockColor, block);
	}

	@SideOnly(Side.CLIENT)
	private static void registerRender(Block block, IItemColor itemColor) {
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(itemColor, block);
	}

	@SideOnly(Side.CLIENT)
	private static void registerRender(Block block) {
		registerRender(block, 0);
	}

	@SideOnly(Side.CLIENT)
	private static void registerRender(Block block, int meta) {
		registerRender(block, meta, block.getRegistryName().getResourcePath());
	}

	@SideOnly(Side.CLIENT)
	private static void registerRender(Block block, int meta, String id) {
		ModelResourceLocation model = new ModelResourceLocation(ElementalSorcery.MODID + ":" + id, "inventory");
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, model);
	}

	@SideOnly(Side.CLIENT)
	private static <T extends Enum<T> & IStringSerializable> void registerStateMapper(Block block,
			PropertyEnum<T> _enum, String suffix) {
		registerStateMapper(block, new StateMap.Builder().withName(_enum).withSuffix("_" + suffix).build());

	}

	@SideOnly(Side.CLIENT)
	private static void registerStateMapper(Block block, IStateMapper mapper) {
		ModelLoader.setCustomStateMapper(block, mapper);
	}

	@SideOnly(Side.CLIENT)
	private static <T extends TileEntity, R extends TileEntitySpecialRenderer<? super T> & IRenderItem> void registerRender(
			Block block, Class<T> tile, R render_instance) {
		registerRender(tile, render_instance);
		registerRender(ItemBlock.getItemFromBlock(block), render_instance);
	}

}
