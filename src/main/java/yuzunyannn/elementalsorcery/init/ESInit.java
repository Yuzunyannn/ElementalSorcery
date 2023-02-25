package yuzunyannn.elementalsorcery.init;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
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
import net.minecraftforge.registries.IForgeRegistryEntry;
import yuzunyannn.elementalsorcery.ESCreativeTabs;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.entity.IFairyCubeMaster;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.util.client.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet.Variable;
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
import yuzunyannn.elementalsorcery.block.altar.BlockDevolveCube;
import yuzunyannn.elementalsorcery.block.altar.BlockDisintegrateStela;
import yuzunyannn.elementalsorcery.block.altar.BlockElementCraftingTable;
import yuzunyannn.elementalsorcery.block.altar.BlockElementCube;
import yuzunyannn.elementalsorcery.block.altar.BlockElementReactor;
import yuzunyannn.elementalsorcery.block.altar.BlockElementTranslocator;
import yuzunyannn.elementalsorcery.block.altar.BlockInstantConstitute;
import yuzunyannn.elementalsorcery.block.altar.BlockMagicDesk;
import yuzunyannn.elementalsorcery.block.altar.BlockPortalAltar;
import yuzunyannn.elementalsorcery.block.altar.BlockSupremeTable;
import yuzunyannn.elementalsorcery.block.altar.BlockTranscribeInjection;
import yuzunyannn.elementalsorcery.block.altar.BlockTranscribeTable;
import yuzunyannn.elementalsorcery.block.container.BlockEStoneCrock;
import yuzunyannn.elementalsorcery.block.container.BlockEStoneMatrix;
import yuzunyannn.elementalsorcery.block.container.BlockElementPlatform;
import yuzunyannn.elementalsorcery.block.container.BlockElfBeacon;
import yuzunyannn.elementalsorcery.block.container.BlockElfTreeCore;
import yuzunyannn.elementalsorcery.block.container.BlockHearth;
import yuzunyannn.elementalsorcery.block.container.BlockIceRockCrystalBlock;
import yuzunyannn.elementalsorcery.block.container.BlockIceRockNode;
import yuzunyannn.elementalsorcery.block.container.BlockIceRockStand;
import yuzunyannn.elementalsorcery.block.container.BlockItemStructureCraftCC;
import yuzunyannn.elementalsorcery.block.container.BlockItemStructureCraftNormal;
import yuzunyannn.elementalsorcery.block.container.BlockLantern;
import yuzunyannn.elementalsorcery.block.container.BlockMagicPlatform;
import yuzunyannn.elementalsorcery.block.container.BlockMeltCauldron;
import yuzunyannn.elementalsorcery.block.container.BlockResearcher;
import yuzunyannn.elementalsorcery.block.container.BlockRiteTable;
import yuzunyannn.elementalsorcery.block.container.BlockSmeltBox;
import yuzunyannn.elementalsorcery.block.container.BlockStoneMill;
import yuzunyannn.elementalsorcery.block.env.BlockDungeonBrick;
import yuzunyannn.elementalsorcery.block.env.BlockDungeonDoor;
import yuzunyannn.elementalsorcery.block.env.BlockDungeonDoorExpand;
import yuzunyannn.elementalsorcery.block.env.BlockDungeonFunction;
import yuzunyannn.elementalsorcery.block.env.BlockDungeonStairs;
import yuzunyannn.elementalsorcery.block.env.BlockGoatGoldBrick;
import yuzunyannn.elementalsorcery.block.md.BlockMDAbsorbBox;
import yuzunyannn.elementalsorcery.block.md.BlockMDDeconstructBox;
import yuzunyannn.elementalsorcery.block.md.BlockMDFrequencyMapping;
import yuzunyannn.elementalsorcery.block.md.BlockMDHearth;
import yuzunyannn.elementalsorcery.block.md.BlockMDInfusion;
import yuzunyannn.elementalsorcery.block.md.BlockMDLiquidizer;
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
import yuzunyannn.elementalsorcery.dungeon.DungeonRoomLib;
import yuzunyannn.elementalsorcery.element.ElementAir;
import yuzunyannn.elementalsorcery.element.ElementEarth;
import yuzunyannn.elementalsorcery.element.ElementEnder;
import yuzunyannn.elementalsorcery.element.ElementFire;
import yuzunyannn.elementalsorcery.element.ElementKnowledge;
import yuzunyannn.elementalsorcery.element.ElementMagic;
import yuzunyannn.elementalsorcery.element.ElementMetal;
import yuzunyannn.elementalsorcery.element.ElementStar;
import yuzunyannn.elementalsorcery.element.ElementWater;
import yuzunyannn.elementalsorcery.element.ElementWood;
import yuzunyannn.elementalsorcery.elf.AutoName;
import yuzunyannn.elementalsorcery.elf.quest.IAdventurer;
import yuzunyannn.elementalsorcery.elf.quest.Quests;
import yuzunyannn.elementalsorcery.elf.research.KnowledgeType;
import yuzunyannn.elementalsorcery.elf.research.Topic;
import yuzunyannn.elementalsorcery.entity.fcube.FairyCubeModuleInGame;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.event.EventServer;
import yuzunyannn.elementalsorcery.event.KeyBoard;
import yuzunyannn.elementalsorcery.explore.ExploreManagement;
import yuzunyannn.elementalsorcery.grimoire.Grimoire;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraArrow;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraBlockCrash;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraElementWhirl;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraEnderTeleport;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFireArea;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFireBall;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFireCharge;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFloat;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFloatArea;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFluorspar;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFootbridge;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFrozen;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraGoldShield;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraIceCrystalBomb;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraLaser;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraLaunch;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraLightningArea;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraLush;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraMagicStrafe;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraMiningArea;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraNaturalMedal;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraPotent;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraPuppetArea;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraSlowFall;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraSprint;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraSturdyArea;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraSummon;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraTimeHourglass;
import yuzunyannn.elementalsorcery.grimoire.mantra.crack.MantraCrackOpen;
import yuzunyannn.elementalsorcery.item.ItemAddressPlate;
import yuzunyannn.elementalsorcery.item.ItemAncientPaper;
import yuzunyannn.elementalsorcery.item.ItemAppleCandy;
import yuzunyannn.elementalsorcery.item.ItemElementBoard;
import yuzunyannn.elementalsorcery.item.ItemElfFruitBomb;
import yuzunyannn.elementalsorcery.item.ItemElfPurse;
import yuzunyannn.elementalsorcery.item.ItemElfWatch;
import yuzunyannn.elementalsorcery.item.ItemEntangleNode;
import yuzunyannn.elementalsorcery.item.ItemFairyCube;
import yuzunyannn.elementalsorcery.item.ItemFairyCubeModule;
import yuzunyannn.elementalsorcery.item.ItemGlassCup;
import yuzunyannn.elementalsorcery.item.ItemJuiceConcentrate;
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
import yuzunyannn.elementalsorcery.item.prop.ItemArrogantWool;
import yuzunyannn.elementalsorcery.item.prop.ItemBlessingJade;
import yuzunyannn.elementalsorcery.item.prop.ItemBlessingJadePiece;
import yuzunyannn.elementalsorcery.item.prop.ItemCalamityGem;
import yuzunyannn.elementalsorcery.item.prop.ItemCollapse;
import yuzunyannn.elementalsorcery.item.prop.ItemController;
import yuzunyannn.elementalsorcery.item.prop.ItemCubeCore;
import yuzunyannn.elementalsorcery.item.prop.ItemDejectedTear;
import yuzunyannn.elementalsorcery.item.prop.ItemDreadGem;
import yuzunyannn.elementalsorcery.item.prop.ItemElementCrack;
import yuzunyannn.elementalsorcery.item.prop.ItemElementStone;
import yuzunyannn.elementalsorcery.item.prop.ItemElfDiamond;
import yuzunyannn.elementalsorcery.item.prop.ItemFairyCore;
import yuzunyannn.elementalsorcery.item.prop.ItemFusionCrystal;
import yuzunyannn.elementalsorcery.item.prop.ItemIceRockChip;
import yuzunyannn.elementalsorcery.item.prop.ItemIceRockSpar;
import yuzunyannn.elementalsorcery.item.prop.ItemInvertGem;
import yuzunyannn.elementalsorcery.item.prop.ItemKeepsake;
import yuzunyannn.elementalsorcery.item.prop.ItemLifeLeather;
import yuzunyannn.elementalsorcery.item.prop.ItemMagicCore;
import yuzunyannn.elementalsorcery.item.prop.ItemMagicPaper;
import yuzunyannn.elementalsorcery.item.prop.ItemMagicTerminal;
import yuzunyannn.elementalsorcery.item.prop.ItemMantraGem;
import yuzunyannn.elementalsorcery.item.prop.ItemMaterialDebris;
import yuzunyannn.elementalsorcery.item.prop.ItemQuill;
import yuzunyannn.elementalsorcery.item.prop.ItemRabidLeather;
import yuzunyannn.elementalsorcery.item.prop.ItemRelicGuardCore;
import yuzunyannn.elementalsorcery.item.prop.ItemSome;
import yuzunyannn.elementalsorcery.item.prop.ItemSoulFragment;
import yuzunyannn.elementalsorcery.item.prop.ItemSpellbookCover;
import yuzunyannn.elementalsorcery.item.prop.ItemSupremeTableComponent;
import yuzunyannn.elementalsorcery.item.prop.ItemVoidContainer;
import yuzunyannn.elementalsorcery.item.prop.ItemVoidContainerElement;
import yuzunyannn.elementalsorcery.item.prop.ItemVoidFragment;
import yuzunyannn.elementalsorcery.item.prop.ItemVortex;
import yuzunyannn.elementalsorcery.item.prop.ItemWindmillBladeFrame;
import yuzunyannn.elementalsorcery.item.tool.ItemCollapseWand;
import yuzunyannn.elementalsorcery.item.tool.ItemCubeDemarcator;
import yuzunyannn.elementalsorcery.item.tool.ItemDragonBreathPickaxe;
import yuzunyannn.elementalsorcery.item.tool.ItemKyaniteTools;
import yuzunyannn.elementalsorcery.item.tool.ItemMagicBlastWand;
import yuzunyannn.elementalsorcery.item.tool.ItemMagicGoldTools;
import yuzunyannn.elementalsorcery.item.tool.ItemMagicRuler;
import yuzunyannn.elementalsorcery.item.tool.ItemRedHandset;
import yuzunyannn.elementalsorcery.item.tool.ItemRelicDisc;
import yuzunyannn.elementalsorcery.item.tool.ItemRockCamera;
import yuzunyannn.elementalsorcery.item.tool.ItemShockWand;
import yuzunyannn.elementalsorcery.item.tool.ItemSoulKillerSword;
import yuzunyannn.elementalsorcery.item.tool.ItemSoulWoodSword;
import yuzunyannn.elementalsorcery.item.tool.ItemStarBell;
import yuzunyannn.elementalsorcery.mods.Mods;
import yuzunyannn.elementalsorcery.mods.ae2.ESAE2Core;
import yuzunyannn.elementalsorcery.mods.ic2.ESIC2Core;
import yuzunyannn.elementalsorcery.network.ESNetwork;
import yuzunyannn.elementalsorcery.parchment.Pages;
import yuzunyannn.elementalsorcery.potion.PotionBlessing;
import yuzunyannn.elementalsorcery.potion.PotionCalamity;
import yuzunyannn.elementalsorcery.potion.PotionCombatSkill;
import yuzunyannn.elementalsorcery.potion.PotionDefenseSkill;
import yuzunyannn.elementalsorcery.potion.PotionElementCrackAttack;
import yuzunyannn.elementalsorcery.potion.PotionEndercorps;
import yuzunyannn.elementalsorcery.potion.PotionEnderization;
import yuzunyannn.elementalsorcery.potion.PotionEnthusiasticStudy;
import yuzunyannn.elementalsorcery.potion.PotionFireWalker;
import yuzunyannn.elementalsorcery.potion.PotionFluoresceWalker;
import yuzunyannn.elementalsorcery.potion.PotionFrozen;
import yuzunyannn.elementalsorcery.potion.PotionGoldShield;
import yuzunyannn.elementalsorcery.potion.PotionGoldenEye;
import yuzunyannn.elementalsorcery.potion.PotionHealthBalance;
import yuzunyannn.elementalsorcery.potion.PotionNaturalMedal;
import yuzunyannn.elementalsorcery.potion.PotionPoundWalker;
import yuzunyannn.elementalsorcery.potion.PotionPowerPitcher;
import yuzunyannn.elementalsorcery.potion.PotionRebirthFromFire;
import yuzunyannn.elementalsorcery.potion.PotionSilent;
import yuzunyannn.elementalsorcery.potion.PotionStar;
import yuzunyannn.elementalsorcery.potion.PotionTideWalker;
import yuzunyannn.elementalsorcery.potion.PotionTimeSlow;
import yuzunyannn.elementalsorcery.potion.PotionTypeES;
import yuzunyannn.elementalsorcery.potion.PotionVerdantWalker;
import yuzunyannn.elementalsorcery.potion.PotionWaterCalamity;
import yuzunyannn.elementalsorcery.potion.PotionWindShield;
import yuzunyannn.elementalsorcery.potion.PotionWindWalker;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.item.RenderItemElementCrack;
import yuzunyannn.elementalsorcery.render.item.RenderItemFairyCube;
import yuzunyannn.elementalsorcery.render.item.RenderItemFairyCubeModule;
import yuzunyannn.elementalsorcery.render.item.RenderItemGlassCup;
import yuzunyannn.elementalsorcery.render.item.RenderItemGrimoire;
import yuzunyannn.elementalsorcery.render.item.RenderItemGuardCore;
import yuzunyannn.elementalsorcery.render.item.RenderItemMagicBlastWand;
import yuzunyannn.elementalsorcery.render.item.RenderItemSpellbook;
import yuzunyannn.elementalsorcery.render.item.RenderItemSupremeTable;
import yuzunyannn.elementalsorcery.render.item.SpellbookRenderInfo;
import yuzunyannn.elementalsorcery.render.tile.RenderTileAnalysisAltar;
import yuzunyannn.elementalsorcery.render.tile.RenderTileBuildingAltar;
import yuzunyannn.elementalsorcery.render.tile.RenderTileCrystalFlower;
import yuzunyannn.elementalsorcery.render.tile.RenderTileDeconstructAltarTable;
import yuzunyannn.elementalsorcery.render.tile.RenderTileDeconstructWindmill;
import yuzunyannn.elementalsorcery.render.tile.RenderTileDevolveCube;
import yuzunyannn.elementalsorcery.render.tile.RenderTileDisintegrateStela;
import yuzunyannn.elementalsorcery.render.tile.RenderTileEStoneMatrix;
import yuzunyannn.elementalsorcery.render.tile.RenderTileElementCraftingTable;
import yuzunyannn.elementalsorcery.render.tile.RenderTileElementPlatform;
import yuzunyannn.elementalsorcery.render.tile.RenderTileElementReactor;
import yuzunyannn.elementalsorcery.render.tile.RenderTileElementTranslocator;
import yuzunyannn.elementalsorcery.render.tile.RenderTileElementalCube;
import yuzunyannn.elementalsorcery.render.tile.RenderTileElfBeacon;
import yuzunyannn.elementalsorcery.render.tile.RenderTileElfTreeCore;
import yuzunyannn.elementalsorcery.render.tile.RenderTileIceRockNode;
import yuzunyannn.elementalsorcery.render.tile.RenderTileIceRockSendRecv;
import yuzunyannn.elementalsorcery.render.tile.RenderTileIceRockStand;
import yuzunyannn.elementalsorcery.render.tile.RenderTileInstantConstitute;
import yuzunyannn.elementalsorcery.render.tile.RenderTileItemStructureCraftCC;
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
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDLiquidizer;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDMagicGen;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDMagicSolidify;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDMagiclization;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDResonantIncubator;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDRubbleRepair;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDTransfer;
import yuzunyannn.elementalsorcery.summon.recipe.SummonRecipe;
import yuzunyannn.elementalsorcery.tile.TileCrystalFlower;
import yuzunyannn.elementalsorcery.tile.TileEStoneCrock;
import yuzunyannn.elementalsorcery.tile.TileEStoneMatrix;
import yuzunyannn.elementalsorcery.tile.TileElementPlatform;
import yuzunyannn.elementalsorcery.tile.TileElfBeacon;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;
import yuzunyannn.elementalsorcery.tile.TileHearth;
import yuzunyannn.elementalsorcery.tile.TileItemStructureCraftCC;
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
import yuzunyannn.elementalsorcery.tile.altar.TileDevolveCube;
import yuzunyannn.elementalsorcery.tile.altar.TileDisintegrateStela;
import yuzunyannn.elementalsorcery.tile.altar.TileElementCraftingTable;
import yuzunyannn.elementalsorcery.tile.altar.TileElementReactor;
import yuzunyannn.elementalsorcery.tile.altar.TileElementTranslocator;
import yuzunyannn.elementalsorcery.tile.altar.TileElementalCube;
import yuzunyannn.elementalsorcery.tile.altar.TileInstantConstitute;
import yuzunyannn.elementalsorcery.tile.altar.TileMagicDesk;
import yuzunyannn.elementalsorcery.tile.altar.TilePortalAltar;
import yuzunyannn.elementalsorcery.tile.altar.TileSupremeTable;
import yuzunyannn.elementalsorcery.tile.altar.TileTranscribeInjection;
import yuzunyannn.elementalsorcery.tile.altar.TileTranscribeTable;
import yuzunyannn.elementalsorcery.tile.dungeon.TileDungeonDoor;
import yuzunyannn.elementalsorcery.tile.dungeon.TileDungeonFunction;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockCrystalBlock;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockNode;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockStand;
import yuzunyannn.elementalsorcery.tile.md.TileMDAbsorbBox;
import yuzunyannn.elementalsorcery.tile.md.TileMDDeconstructBox;
import yuzunyannn.elementalsorcery.tile.md.TileMDFrequencyMapping;
import yuzunyannn.elementalsorcery.tile.md.TileMDHearth;
import yuzunyannn.elementalsorcery.tile.md.TileMDInfusion;
import yuzunyannn.elementalsorcery.tile.md.TileMDLiquidizer;
import yuzunyannn.elementalsorcery.tile.md.TileMDMagicGen;
import yuzunyannn.elementalsorcery.tile.md.TileMDMagicSolidify;
import yuzunyannn.elementalsorcery.tile.md.TileMDMagiclization;
import yuzunyannn.elementalsorcery.tile.md.TileMDResonantIncubator;
import yuzunyannn.elementalsorcery.tile.md.TileMDRubbleRepair;
import yuzunyannn.elementalsorcery.tile.md.TileMDTransfer;
import yuzunyannn.elementalsorcery.util.helper.SilentWorld;
import yuzunyannn.elementalsorcery.util.render.Shaders;
import yuzunyannn.elementalsorcery.util.render.WorldScene;
import yuzunyannn.elementalsorcery.util.var.Variables;
import yuzunyannn.elementalsorcery.worldgen.WorldGeneratorES;

public class ESInit {

	public static final List<Class<? extends TileEntity>> ES_TILE_ENTITY = new ArrayList<>();

	public static final ESCreativeTabs tab = ESCreativeTabs.TAB;

	public static final void instance() throws ReflectiveOperationException {
		// 创造物品栏
		ESObjects.CREATIVE_TABS = tab;
		// 初始化虚空元素
		ESObjects.ELEMENTS.VOID = new Element(Element.rgb(0, 0, 0)).setRegistryName("void").setTranslationKey("void");
		// 初始化魔力元素
		ESObjects.ELEMENTS.MAGIC = new ElementMagic().setRegistryName("magic").setTranslationKey("magic");
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
		ElementalSorcery.setAPIField(new SilentWorld());
	}

	private static void initVoidElement() throws ReflectiveOperationException {
		Field field = ElementStack.class.getDeclaredField("element");
		field.setAccessible(true);
		field.set(ElementStack.EMPTY, ESObjects.ELEMENTS.VOID);
	}

	private static final void instanceBlocks() throws ReflectiveOperationException {

		ESObjects.BLOCKS.ASTONE = new BlockAStone();
		ESObjects.BLOCKS.ESTONE = new BlocksEStone.EStone();
		ESObjects.BLOCKS.ESTONE_SLAB = new BlocksEStone.EStoneSlab();
		ESObjects.BLOCKS.ESTONE_STAIRS = new BlocksEStone.EStoneStairs();
		ESObjects.BLOCKS.ESTONE_PRISM = new BlocksEStone.EStonePrism();
		ESObjects.BLOCKS.ESTONE_MATRIX = new BlockEStoneMatrix();
		ESObjects.BLOCKS.ESTONE_CROCK = new BlockEStoneCrock();
		ESObjects.BLOCKS.DUNGEON_BRICK = new BlockDungeonBrick();
		ESObjects.BLOCKS.DUNGEON_STAIRS = new BlockDungeonStairs();

		ESObjects.BLOCKS.ELEMENTAL_CUBE = new BlockElementCube();
		ESObjects.BLOCKS.HEARTH = new BlockHearth();
		ESObjects.BLOCKS.SMELT_BOX = new BlockSmeltBox(BlockHearth.EnumMaterial.COBBLESTONE);
		ESObjects.BLOCKS.SMELT_BOX_IRON = new BlockSmeltBox(BlockHearth.EnumMaterial.IRON);
		ESObjects.BLOCKS.SMELT_BOX_KYANITE = new BlockSmeltBox(BlockHearth.EnumMaterial.KYANITE);
		ESObjects.BLOCKS.KYANITE_BLOCK = new BlockKyanite();
		ESObjects.BLOCKS.KYANITE_ORE = new BlockKyanite.BlockKyaniteOre();
		ESObjects.BLOCKS.MAGIC_PLATFORM = new BlockMagicPlatform();
		ESObjects.BLOCKS.INVALID_ENCHANTMENT_TABLE = new BlockInvalidEnchantmentTable();
		ESObjects.BLOCKS.ELEMENT_WORKBENCH = new BlockElementWorkbench();
		ESObjects.BLOCKS.MAGIC_DESK = new BlockMagicDesk();
		ESObjects.BLOCKS.ELEMENT_CRAFTING_TABLE = new BlockElementCraftingTable();
		ESObjects.BLOCKS.DECONSTRUCT_ALTAR_TABLE = new BlockDeconstructAltarTable();
		ESObjects.BLOCKS.DECONSTRUCT_ALTAR_TABLE_ADV = new BlockDeconstructAltarTableAdv();
		ESObjects.BLOCKS.RITE_TABLE = new BlockRiteTable();
		ESObjects.BLOCKS.LANTERN = new BlockLantern();
		ESObjects.BLOCKS.BUILDING_ALTAR = new BlockBuildingAltar();
		ESObjects.BLOCKS.ANALYSIS_ALTAR = new BlockAnalysisAltar();
		ESObjects.BLOCKS.SUPREME_TABLE = new BlockSupremeTable();
		ESObjects.BLOCKS.MAGIC_TORCH = new BlockMagicTorch();
		ESObjects.BLOCKS.STAR_STONE = new BlockStarStone();
		ESObjects.BLOCKS.STAR_SAND = new BlockStarSand();
		ESObjects.BLOCKS.STONE_MILL = new BlockStoneMill();
		ESObjects.BLOCKS.MELT_CAULDRON = new BlockMeltCauldron();
		ESObjects.BLOCKS.ELF_LOG = new BlockElfLog();
		ESObjects.BLOCKS.ELF_LEAF = new BlockElfLeaf();
		ESObjects.BLOCKS.ELF_PLANK = new BlockElfPlank();
		ESObjects.BLOCKS.ELF_SAPLING = new BlockElfSapling();
		ESObjects.BLOCKS.ELF_FRUIT = new BlockElfFruit();
		ESObjects.BLOCKS.MD_MAGIC_GEN = new BlockMDMagicGen();
		ESObjects.BLOCKS.MD_HEARTH = new BlockMDHearth();
		ESObjects.BLOCKS.MD_RUBBLE_REPAIR = new BlockMDRubbleRepair();
		ESObjects.BLOCKS.MD_INFUSION = new BlockMDInfusion();
		ESObjects.BLOCKS.MD_TRANSFER = new BlockMDTransfer();
		ESObjects.BLOCKS.MD_MAGIC_SOLIDIFY = new BlockMDMagicSolidify();
		ESObjects.BLOCKS.MD_ABSORB_BOX = new BlockMDAbsorbBox();
		ESObjects.BLOCKS.MD_MAGICLIZATION = new BlockMDMagiclization();
		ESObjects.BLOCKS.MD_DECONSTRUCT_BOX = new BlockMDDeconstructBox();
		ESObjects.BLOCKS.MD_RESONANT_INCUBATOR = new BlockMDResonantIncubator();
		ESObjects.BLOCKS.MD_FREQUENCY_MAPPING = new BlockMDFrequencyMapping();
		ESObjects.BLOCKS.MD_LIQUIDIZER = new BlockMDLiquidizer();
		ESObjects.BLOCKS.LIFE_FLOWER = new BlockLifeFlower();
		ESObjects.BLOCKS.MAGIC_POT = new BlockMagicPot();
		ESObjects.BLOCKS.LIFE_DIRT = new BlockLifeDirt();
		ESObjects.BLOCKS.CRYSTAL_FLOWER = new BlockCrystalFlower();
		ESObjects.BLOCKS.IS_CRAFT_NORMAL = new BlockItemStructureCraftNormal();
		ESObjects.BLOCKS.PORTAL_ALTAR = new BlockPortalAltar();
		ESObjects.BLOCKS.TRANSCRIBE_TABLE = new BlockTranscribeTable();
		ESObjects.BLOCKS.TRANSCRIBE_INJECTION = new BlockTranscribeInjection();
		ESObjects.BLOCKS.ELF_TREE_CORE = new BlockElfTreeCore();
		ESObjects.BLOCKS.ELF_BEACON = new BlockElfBeacon();
		ESObjects.BLOCKS.RESEARCHER = new BlockResearcher();
		ESObjects.BLOCKS.SEAL_STONE = new BlockSealStone();
		ESObjects.BLOCKS.SCARLET_CRYSTAL_ORE = new BlockScarletCrystalOre();
		ESObjects.BLOCKS.STAR_FLOWER = new BlockStarFlower();
		ESObjects.BLOCKS.CRUDE_QUARTZ = new BlockCrudeQuartz();
		ESObjects.BLOCKS.ELEMENT_PLATFORM = new BlockElementPlatform();
		ESObjects.BLOCKS.FLUORSPAR = new BlockFluorspar();
		ESObjects.BLOCKS.DECONSTRUCT_WINDMILL = new BlockDeconstructWindmill();
		ESObjects.BLOCKS.ELEMENT_TRANSLOCATOR = new BlockElementTranslocator();
		ESObjects.BLOCKS.GOAT_GOLD_BRICK = new BlockGoatGoldBrick();
		ESObjects.BLOCKS.DEVOLVE_CUBE = new BlockDevolveCube();
		ESObjects.BLOCKS.DISINTEGRATE_STELA = new BlockDisintegrateStela();
		ESObjects.BLOCKS.ICE_ROCK_STAND = new BlockIceRockStand();
		ESObjects.BLOCKS.ICE_ROCK_CRYSTAL_BLOCK = new BlockIceRockCrystalBlock();
		ESObjects.BLOCKS.ICE_ROCK_NODE = new BlockIceRockNode();
		ESObjects.BLOCKS.ELEMENT_REACTOR = new BlockElementReactor();
		ESObjects.BLOCKS.INSTANT_CONSTITUTE = new BlockInstantConstitute();
		ESObjects.BLOCKS.IS_CRAFT_CC = new BlockItemStructureCraftCC();
		ESObjects.BLOCKS.DUNGEON_DOOR = new BlockDungeonDoor();
		ESObjects.BLOCKS.DUNGEON_DOOR_EXPAND = new BlockDungeonDoorExpand();
		ESObjects.BLOCKS.DUNGEON_FUNCTION = new BlockDungeonFunction();

		// 初始化所有tab
		Class<?> cls = ESObjects.BLOCKS.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			Block block = ((Block) field.get(ESObjects.BLOCKS));
			block.setCreativeTab(tab);
			block.setRegistryName(field.getName().toLowerCase());
		}
	}

	private static final void instanceItems() throws ReflectiveOperationException {
		ESObjects.ITEMS.KYANITE = ItemSome.newKyanite();
		ESObjects.ITEMS.MAGIC_PIECE = ItemSome.newMagicalPiece();
		ESObjects.ITEMS.MAGICAL_ENDER_EYE = ItemSome.newMagicalEnderEye();
		ESObjects.ITEMS.MAGIC_CRYSTAL = new ItemMagicalCrystal();
		ESObjects.ITEMS.TINY_KNIFE = ItemSome.newTinyKnife();
		ESObjects.ITEMS.MAGIC_PAPER = new ItemMagicPaper();
		ESObjects.ITEMS.SPELL_CRYSTAL = ItemCrystal.newSpellCrystal();
		ESObjects.ITEMS.MAGIC_STONE = ItemSome.newMagicStone();
		ESObjects.ITEMS.KYANITE_PICKAXE = new ItemKyaniteTools.ItemKyanitePickaxe();
		ESObjects.ITEMS.KYANITE_AXE = new ItemKyaniteTools.ItemKyaniteAxe();
		ESObjects.ITEMS.KYANITE_SPADE = new ItemKyaniteTools.ItemKyaniteSpade();
		ESObjects.ITEMS.KYANITE_HOE = new ItemKyaniteTools.ItemKyaniteHoe();
		ESObjects.ITEMS.KYANITE_SWORD = new ItemKyaniteTools.ItemKyaniteSword();
		ESObjects.ITEMS.ARCHITECTURE_CRYSTAL = new ItemArchitectureCrystal();
		ESObjects.ITEMS.ELEMENT_CRYSTAL = new ItemElementCrystal();
		ESObjects.ITEMS.PARCHMENT = new ItemParchment();
		ESObjects.ITEMS.SPELLBOOK_COVER = new ItemSpellbookCover();
		ESObjects.ITEMS.SCROLL = new ItemScroll();
		ESObjects.ITEMS.MANUAL = new ItemManual();
		ESObjects.ITEMS.MAGIC_RULER = new ItemMagicRuler();
		ESObjects.ITEMS.ITEM_CRYSTAL = new ItemItemCrystal();
		ESObjects.ITEMS.ORDER_CRYSTAL = new ItemOrderCrystal();
		ESObjects.ITEMS.MD_BASE = ItemSome.newMDBase();
		ESObjects.ITEMS.RITE_MANUAL = new ItemRiteManual();
		ESObjects.ITEMS.RED_HANDSET = new ItemRedHandset();
		ESObjects.ITEMS.AZURE_CRYSTAL = ItemCrystal.newAzureCrystal();
		ESObjects.ITEMS.RESONANT_CRYSTAL = new ItemResonantCrystal();
		ESObjects.ITEMS.ELF_CRYSTAL = ItemCrystal.newElfCrystal();
		ESObjects.ITEMS.SUPREME_TABLE_COMPONENT = new ItemSupremeTableComponent();
		ESObjects.ITEMS.ELF_COIN = ItemSome.newElfCoin();
		ESObjects.ITEMS.ELF_PURSE = new ItemElfPurse();
		ESObjects.ITEMS.NATURE_CRYSTAL = new ItemNatureCrystal();
		ESObjects.ITEMS.NATURE_DUST = new ItemNatureDust();
		ESObjects.ITEMS.ANCIENT_PAPER = new ItemAncientPaper();
		ESObjects.ITEMS.QUEST = new ItemQuest();
		ESObjects.ITEMS.ELF_WATCH = new ItemElfWatch();
		ESObjects.ITEMS.MAGIC_GOLD = ItemSome.newMagicGold();
		ESObjects.ITEMS.MAGIC_GOLD_PICKAXE = new ItemMagicGoldTools.ItemMagicGoldPickaxe();
		ESObjects.ITEMS.MAGIC_GOLD_AXE = new ItemMagicGoldTools.ItemMagicGoldAxe();
		ESObjects.ITEMS.MAGIC_GOLD_SPADE = new ItemMagicGoldTools.ItemMagicGoldSpade();
		ESObjects.ITEMS.MAGIC_GOLD_HOE = new ItemMagicGoldTools.ItemMagicGoldHoe();
		ESObjects.ITEMS.MAGIC_GOLD_SWORD = new ItemMagicGoldTools.ItemMagicGoldSword();
		ESObjects.ITEMS.PARCEL = new ItemParcel();
		ESObjects.ITEMS.ADDRESS_PLATE = new ItemAddressPlate();
		ESObjects.ITEMS.ELF_STAR = ItemSome.newElfStar();
		ESObjects.ITEMS.JUMP_GEM = ItemSome.newJumpGem();
		ESObjects.ITEMS.UNSCRAMBLE_NOTE = new ItemUnscrambleNote();
		ESObjects.ITEMS.SOUL_FRAGMENT = new ItemSoulFragment();
		ESObjects.ITEMS.SOUL_WOOD_SWORD = new ItemSoulWoodSword();
		ESObjects.ITEMS.RELIC_GEM = ItemSome.newRelicGem();
		ESObjects.ITEMS.ROCK_CAMERA = new ItemRockCamera();
		ESObjects.ITEMS.KEEPSAKE = new ItemKeepsake();
		ESObjects.ITEMS.QUILL = new ItemQuill();
		ESObjects.ITEMS.FUSION_CRYSTAL = new ItemFusionCrystal();
		ESObjects.ITEMS.VORTEX = new ItemVortex();
		ESObjects.ITEMS.ELEMENT_STONE = new ItemElementStone();
		ESObjects.ITEMS.LIFE_LEATHER = new ItemLifeLeather();
		ESObjects.ITEMS.MAGIC_BLAST_WAND = new ItemMagicBlastWand();
		ESObjects.ITEMS.SOUL_KILLER_SWORD = new ItemSoulKillerSword();
		ESObjects.ITEMS.SCAPEGOAT = new ItemScapegoat();
		ESObjects.ITEMS.MAGIC_CORE = new ItemMagicCore();
		ESObjects.ITEMS.SCARLET_CRYSTAL = new ItemScarletCrystal();
		ESObjects.ITEMS.STAR_BELL = new ItemStarBell();
		ESObjects.ITEMS.APPLE_CANDY = new ItemAppleCandy();
		ESObjects.ITEMS.FAIRY_CUBE = new ItemFairyCube();
		ESObjects.ITEMS.FAIRY_CUBE_MODULE = new ItemFairyCubeModule();
		ESObjects.ITEMS.RABID_LEATHER = new ItemRabidLeather();
		ESObjects.ITEMS.CUBE_CORE = new ItemCubeCore();
		ESObjects.ITEMS.DREAD_GEM = new ItemDreadGem();
		ESObjects.ITEMS.DEJECTED_TEAR = new ItemDejectedTear();
		ESObjects.ITEMS.MERCHANT_INVITATION = new ItemMerchantInvitation();
		ESObjects.ITEMS.ELEMENT_BOARD = new ItemElementBoard();
		ESObjects.ITEMS.CUBE_DEMARCATOR = new ItemCubeDemarcator();
		ESObjects.ITEMS.WINDMILL_BLADE_FRAME = new ItemWindmillBladeFrame();
		ESObjects.ITEMS.WINDMILL_BLADE = new ItemWindmillBlade();
		ESObjects.ITEMS.WINDMILL_BLADE_ASTONE = new ItemWindmillBlades.AStone();
		ESObjects.ITEMS.WINDMILL_BLADE_WOOD = new ItemWindmillBlades.WOOD();
		ESObjects.ITEMS.WINDMILL_BLADE_CRYSTAL = new ItemWindmillBlades.CRYSTAL();
		ESObjects.ITEMS.ELF_FRUIT_BOMB = new ItemElfFruitBomb();
		ESObjects.ITEMS.GLASS_CUP = new ItemGlassCup();
		ESObjects.ITEMS.CALAMITY_GEM = new ItemCalamityGem();
		ESObjects.ITEMS.BLESSING_JADE = new ItemBlessingJade();
		ESObjects.ITEMS.ARROGANT_WOOL = new ItemArrogantWool();
		ESObjects.ITEMS.BLESSING_JADE_PIECE = new ItemBlessingJadePiece();
		ESObjects.ITEMS.ELEMENT_CRACK = new ItemElementCrack();
		ESObjects.ITEMS.ENTANGLE_NODE = new ItemEntangleNode();
		ESObjects.ITEMS.DRAGON_BREATH_PICKAXE = new ItemDragonBreathPickaxe();
		ESObjects.ITEMS.INVERT_GEM = new ItemInvertGem();
		ESObjects.ITEMS.ICE_ROCK_CHIP = new ItemIceRockChip();
		ESObjects.ITEMS.ICE_ROCK_SPAR = new ItemIceRockSpar();
		ESObjects.ITEMS.FAIRY_CORE = new ItemFairyCore();
		ESObjects.ITEMS.MATERIAL_DEBRIS = new ItemMaterialDebris();
		ESObjects.ITEMS.MANTRA_GEM = new ItemMantraGem();
		ESObjects.ITEMS.CONTROLLER = new ItemController();
		ESObjects.ITEMS.MAGIC_TERMINAL = new ItemMagicTerminal();
		ESObjects.ITEMS.VOID_FRAGMENT = new ItemVoidFragment();
		ESObjects.ITEMS.VOID_CONTAINER = new ItemVoidContainer();
		ESObjects.ITEMS.VOID_CONTAINER_ELEMENT = new ItemVoidContainerElement();
		ESObjects.ITEMS.ELF_DIAMOND = new ItemElfDiamond();
		ESObjects.ITEMS.COLLAPSE = new ItemCollapse();
		ESObjects.ITEMS.COLLAPSE_WAND = new ItemCollapseWand();
		ESObjects.ITEMS.RELIC_GUARD_CORE = new ItemRelicGuardCore();
		ESObjects.ITEMS.SHOCK_WAND = new ItemShockWand();
		ESObjects.ITEMS.RELIC_DISC = new ItemRelicDisc();
		ESObjects.ITEMS.JUICE_CONCENTRATE = new ItemJuiceConcentrate();

		ESObjects.ITEMS.GRIMOIRE = new ItemGrimoire();
		ESObjects.ITEMS.SPELLBOOK = new ItemSpellbook();
		ESObjects.ITEMS.SPELLBOOK_ARCHITECTURE = new ItemSpellbookArchitecture();
		ESObjects.ITEMS.SPELLBOOK_ENCHANTMENT = new ItemSpellbookEnchantment();
		ESObjects.ITEMS.SPELLBOOK_LAUNCH = new ItemSpellbookLaunch();
		ESObjects.ITEMS.SPELLBOOK_ELEMENT = new ItemSpellbookElement();

		// 初始化所有tab
		Class<?> cls = ESObjects.ITEMS.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			Item item = ((Item) field.get(ESObjects.ITEMS));
			item.setCreativeTab(tab);
			item.setRegistryName(field.getName().toLowerCase());
		}
	}

	private static final void instanceMantras() throws ReflectiveOperationException {

		ESObjects.MANTRAS.ENDER_TELEPORT = new MantraEnderTeleport();
		ESObjects.MANTRAS.FLOAT = new MantraFloat();
		ESObjects.MANTRAS.SPRINT = new MantraSprint();
		ESObjects.MANTRAS.FIRE_BALL = new MantraFireBall();
		ESObjects.MANTRAS.LUSH = new MantraLush();
		ESObjects.MANTRAS.BLOCK_CRASH = new MantraBlockCrash();
		ESObjects.MANTRAS.MINING_AREA = new MantraMiningArea();
		ESObjects.MANTRAS.LIGHTNING_AREA = new MantraLightningArea();
		ESObjects.MANTRAS.SUMMON = new MantraSummon();
		ESObjects.MANTRAS.SLOW_FALL = new MantraSlowFall();
		ESObjects.MANTRAS.FOOTBRIDGE = new MantraFootbridge();
		ESObjects.MANTRAS.FIRE_AREA = new MantraFireArea();
		ESObjects.MANTRAS.MAGIC_STRAFE = new MantraMagicStrafe();
		ESObjects.MANTRAS.FLOAT_AREA = new MantraFloatArea();
		ESObjects.MANTRAS.FIRE_CHARGE = new MantraFireCharge();
		ESObjects.MANTRAS.ARROW = new MantraArrow();
		ESObjects.MANTRAS.POTENT = new MantraPotent();
		ESObjects.MANTRAS.FLUORSPAR = new MantraFluorspar();
		ESObjects.MANTRAS.TIME_HOURGLASS = new MantraTimeHourglass();
		ESObjects.MANTRAS.ELEMENT_WHIRL = new MantraElementWhirl();
		ESObjects.MANTRAS.LASER = new MantraLaser();
		ESObjects.MANTRAS.STURDY_AREA = new MantraSturdyArea();
		ESObjects.MANTRAS.FROZEN = new MantraFrozen();
		ESObjects.MANTRAS.ICE_CRYSTAL_BOMB = new MantraIceCrystalBomb();
		ESObjects.MANTRAS.NATURAL_MEDAL = new MantraNaturalMedal();
		ESObjects.MANTRAS.PUPPET_AREA = new MantraPuppetArea();
		ESObjects.MANTRAS.GOLD_SHIELD = new MantraGoldShield();

		ESObjects.MANTRAS.ECRACK_OPEN = new MantraCrackOpen();

		ESObjects.MANTRAS.LAUNCH_ECR = new MantraLaunch(ICraftingLaunch.TYPE_ELEMENT_CRAFTING, 0xffec3d);
		ESObjects.MANTRAS.LAUNCH_EDE = new MantraLaunch(ICraftingLaunch.TYPE_ELEMENT_DECONSTRUCT, 0xff4a1a);
		ESObjects.MANTRAS.LAUNCH_ECO = new MantraLaunch(ICraftingLaunch.TYPE_ELEMENT_CONSTRUCT, 0x00b5e5);
		ESObjects.MANTRAS.LAUNCH_BRC = new MantraLaunch(ICraftingLaunch.TYPE_BUILING_RECORD, 0x18632b);

		// 初始化所有
		Class<?> cls = ESObjects.MANTRAS.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			Mantra mantra = ((Mantra) field.get(ESObjects.MANTRAS));
			mantra.setRegistryName(field.getName().toLowerCase());
		}
	}

	private static final void instanceElements() {
		ESObjects.ELEMENTS.FIRE = new ElementFire().setRegistryName("fire");
		ESObjects.ELEMENTS.ENDER = new ElementEnder().setRegistryName("ender");
		ESObjects.ELEMENTS.WATER = new ElementWater().setRegistryName("water");
		ESObjects.ELEMENTS.AIR = new ElementAir().setRegistryName("air");
		ESObjects.ELEMENTS.EARTH = new ElementEarth().setRegistryName("earth");
		ESObjects.ELEMENTS.WOOD = new ElementWood().setRegistryName("wood");
		ESObjects.ELEMENTS.METAL = new ElementMetal().setRegistryName("metal");
		ESObjects.ELEMENTS.KNOWLEDGE = new ElementKnowledge().setRegistryName("knowledge");
		ESObjects.ELEMENTS.STAR = new ElementStar().setRegistryName("star");
	}

	private static final void instancePotions() throws ReflectiveOperationException {
		ESObjects.POTIONS.TIME_SLOW = new PotionTimeSlow();
		ESObjects.POTIONS.FIRE_WALKER = new PotionFireWalker();
		ESObjects.POTIONS.TIDE_WALKER = new PotionTideWalker();
		ESObjects.POTIONS.WIND_WALKER = new PotionWindWalker();
		ESObjects.POTIONS.FLUORESCE_WALKER = new PotionFluoresceWalker();
		ESObjects.POTIONS.POUND_WALKER = new PotionPoundWalker();
		ESObjects.POTIONS.VERDANT_WALKER = new PotionVerdantWalker();
		ESObjects.POTIONS.REBIRTH_FROM_FIRE = new PotionRebirthFromFire();
		ESObjects.POTIONS.WATER_CALAMITY = new PotionWaterCalamity();
		ESObjects.POTIONS.ENDERIZATION = new PotionEnderization();
		ESObjects.POTIONS.ENDERCORPS = new PotionEndercorps();
		ESObjects.POTIONS.WIND_SHIELD = new PotionWindShield();
		ESObjects.POTIONS.GOLDEN_EYE = new PotionGoldenEye();
		ESObjects.POTIONS.POWER_PITCHER = new PotionPowerPitcher();
		ESObjects.POTIONS.HEALTH_BALANCE = new PotionHealthBalance();
		ESObjects.POTIONS.COMBAT_SKILL = new PotionCombatSkill();
		ESObjects.POTIONS.DEFENSE_SKILL = new PotionDefenseSkill();
		ESObjects.POTIONS.STAR = new PotionStar();
		ESObjects.POTIONS.CALAMITY = new PotionCalamity();
		ESObjects.POTIONS.BLESSING = new PotionBlessing();
		ESObjects.POTIONS.ELEMENT_CRACK_ATTACK = new PotionElementCrackAttack();
		ESObjects.POTIONS.ENTHUSIASTIC_STUDY = new PotionEnthusiasticStudy();
		ESObjects.POTIONS.SILENT = new PotionSilent();
		ESObjects.POTIONS.FROZEN = new PotionFrozen();
		ESObjects.POTIONS.NATURAL_MEDAL = new PotionNaturalMedal();
		ESObjects.POTIONS.GOLD_SHIELD = new PotionGoldShield();

		ESObjects.POTION_TYPES.SILENT = PotionTypeES.create("silent",
				new PotionEffect(ESObjects.POTIONS.SILENT, 20 * 16));

		{
			Class<?> cls = ESObjects.POTIONS.getClass();
			Field[] fields = cls.getDeclaredFields();
			for (Field field : fields) {
				Potion potion = ((Potion) field.get(ESObjects.POTIONS));
				potion.setRegistryName(field.getName().toLowerCase());
			}
		}
		{

			Class<?> cls = ESObjects.POTION_TYPES.getClass();
			Field[] fields = cls.getDeclaredFields();
			for (Field field : fields) {
				PotionType potion = ((PotionType) field.get(ESObjects.POTION_TYPES));
				potion.setRegistryName(field.getName().toLowerCase());
			}
		}
	}

	private static final void instanceVillage() {
		ESObjects.VILLAGE.ES_VILLEGER = new VillagerRegistry.VillagerProfession("elementalsorcery:antique_dealer",
				"elementalsorcery:textures/entity/villager/es_studier.png",
				"elementalsorcery:textures/entity/zombie_villager/es_studier.png");
	}

	// 正式开始进行注册

	public final static void preInit(FMLPreInitializationEvent event) throws Throwable {
		// 注册物品
		registerAllItems();
		// 注册方块
		registerAllBlocks();
		// 注册元素
		registerAllElements();
		// 注册tileentity
		registerAllTiles();
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
		DungeonRoomLib.registerAll();
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
		// 药水
		registerAllPotionTypes();
		// 精灵立方体模块注册
		FairyCubeModuleInGame.registerAll();
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
		// 其他mod
		preInitObjectForOtherMod();
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
		// 所有熔炼容量
		TileMeltCauldron.initVolumeMap();
		// 所有任务
		Quests.loadAll();
	}

	public final static void postInit(FMLPostInitializationEvent event) throws Throwable {
		// 通过查找注册元素映射
		ElementMap.findAndRegisterCraft();
		// 其他mod
		postInitObjectForOtherMod();
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
		FairyCubeModuleInGame.registerAllRender();
		// 客户端事件
		MinecraftForge.EVENT_BUS.register(EventClient.class);
		// 世界离屏渲染
		if (ESConfig.PORTAL_RENDER_TYPE == 2) WorldScene.init();
		// 所有shader
		Shaders.init();
		// 其他mode
		preInitObjectForOtherModClient();
	}

	@SideOnly(Side.CLIENT)
	public final static void initClinet(FMLInitializationEvent event) throws Throwable {
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
		Class<?> cls = ESObjects.ITEMS.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			register((Item) field.get(ESObjects.ITEMS));
		}
	}

	static void registerAllBlocks() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// 遍历所有，将所有内容注册
		Class<?> cls = ESObjects.BLOCKS.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			Block block = (Block) field.get(ESObjects.BLOCKS);
			try {
				Method method = block.getClass().getDeclaredMethod("getItemBlock");
				register(block, (ItemBlock) method.invoke(block));
			} catch (NoSuchMethodException e) {
				register(block);
			}
		}
	}

	static public void registerAllElements() throws ReflectiveOperationException {
		Class<?> cls = ESObjects.ELEMENTS.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			Element element = ((Element) field.get(ESObjects.ELEMENTS));
			Element.REGISTRY.register(element);
			try {
				Field mantraElementMarkField = Variables.class.getDeclaredField(field.getName());
				mantraElementMarkField.setAccessible(true);
				Field modifiersField = Field.class.getDeclaredField("modifiers");
				modifiersField.setAccessible(true);
				modifiersField.setInt(mantraElementMarkField, mantraElementMarkField.getModifiers() & ~Modifier.FINAL);
				mantraElementMarkField.set(null, new Variable<>("E^" + element.getRegistryId(), VariableSet.ELEMENT));
			} catch (ReflectiveOperationException e) {}
		}
	}

	static void registerAllMantras() throws IllegalArgumentException, IllegalAccessException {
		// 遍历所有，将所有内容注册
		Class<?> cls = ESObjects.MANTRAS.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			register((Mantra) field.get(ESObjects.MANTRAS));
		}
	}

	static void registerAllPotions() throws IllegalArgumentException, IllegalAccessException {
		Class<?> cls = ESObjects.POTIONS.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			register((Potion) field.get(ESObjects.POTIONS));
		}
	}

	static void registerAllPotionTypes() throws IllegalArgumentException, IllegalAccessException {
		Class<?> cls = ESObjects.POTION_TYPES.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			register((PotionType) field.get(ESObjects.POTION_TYPES));
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
		register(TileMDLiquidizer.class, "MDLiquidizer");
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
		register(TileDevolveCube.class, "DevolveCube");
		register(TileDisintegrateStela.class, "DisintegrateStela");
		register(TileIceRockStand.class, "IceRockStand");
		register(TileIceRockCrystalBlock.class, "IceRockCrystalBlock");
		register(TileIceRockNode.class, "IceRockNode");
		register(TileElementReactor.class, "ElementReactor");
		register(TileEStoneMatrix.class, "EStoneMatrix");
		register(TileInstantConstitute.class, "IConstitute");
		register(TileEStoneCrock.class, "EStoneCrock");
		register(TileItemStructureCraftCC.class, "ISCraftCC");
		register(TileDungeonDoor.class, "DungeonDoor");
		register(TileDungeonFunction.class, "DungeonFunc");
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
		final ESObjects.Blocks BLOCKS = ESObjects.BLOCKS;
		final ESObjects.Items ITEMS = ESObjects.ITEMS;
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
		registerRender(ITEMS.FUSION_CRYSTAL);
		registerRender(ITEMS.VORTEX);
		registerRender(ITEMS.ELEMENT_STONE);
		registerRender(ITEMS.LIFE_LEATHER, 0, "life_leather_incomplete");
		registerRender(ITEMS.LIFE_LEATHER, 1, "life_leather");
		registerRender(ITEMS.MAGIC_BLAST_WAND, new RenderItemMagicBlastWand(0));
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
		registerRender(ITEMS.CALAMITY_GEM);
		registerRender(ITEMS.BLESSING_JADE);
		registerRender(ITEMS.ARROGANT_WOOL);
		registerRender(ITEMS.BLESSING_JADE_PIECE, 0, "blessing_jade_piece/p0");
		registerRender(ITEMS.BLESSING_JADE_PIECE, 1, "blessing_jade_piece/p1");
		registerRender(ITEMS.BLESSING_JADE_PIECE, 2, "blessing_jade_piece/p2");
		registerRender(ITEMS.BLESSING_JADE_PIECE, 3, "blessing_jade_piece/p3");
		registerRender(ITEMS.BLESSING_JADE_PIECE, 4, "blessing_jade_piece/p4");
		registerRender(ITEMS.BLESSING_JADE_PIECE, 5, "blessing_jade_piece/p5");
		registerRender(ITEMS.BLESSING_JADE_PIECE, 6, "blessing_jade_piece/p6");
		registerRender(ITEMS.BLESSING_JADE_PIECE, 7, "blessing_jade_piece/p7");
		registerRender(ITEMS.ELEMENT_CRACK, new RenderItemElementCrack());
		registerRender(ITEMS.ENTANGLE_NODE);
		registerRender(ITEMS.DRAGON_BREATH_PICKAXE);
		registerRender(ITEMS.INVERT_GEM);
		registerRender(ITEMS.ICE_ROCK_CHIP);
		registerRender(ITEMS.ICE_ROCK_SPAR);
		registerRender(ITEMS.FAIRY_CORE);
		registerRender(ITEMS.MATERIAL_DEBRIS);
		registerRender(ITEMS.MANTRA_GEM);
		registerRender(ITEMS.MAGIC_TERMINAL);
		registerRender(ITEMS.VOID_FRAGMENT);
		registerRender(ITEMS.VOID_CONTAINER);
		registerRender(ITEMS.VOID_CONTAINER_ELEMENT);
		registerRender(ITEMS.ELF_DIAMOND);
		registerRender(ITEMS.COLLAPSE);
		registerRender(ITEMS.COLLAPSE_WAND, new RenderItemMagicBlastWand(1));
		registerRender(ITEMS.RELIC_GUARD_CORE, new RenderItemGuardCore());
		registerRender(ITEMS.SHOCK_WAND, new RenderItemMagicBlastWand(2));
		registerRender(ITEMS.RELIC_DISC);
		registerRender(ITEMS.JUICE_CONCENTRATE);

		for (ItemMagicPaper.EnumType paperType : ItemMagicPaper.EnumType.values())
			registerRender(ITEMS.MAGIC_PAPER, paperType.getMeta(), paperType.getName() + "_paper");
		for (ItemKeepsake.EnumType keepsakeType : ItemKeepsake.EnumType.values())
			registerRender(ITEMS.KEEPSAKE, keepsakeType.getMeta(), keepsakeType.getName());
		for (ItemQuill.EnumType quillType : ItemQuill.EnumType.values())
			registerRender(ITEMS.QUILL, quillType.getMeta(), "quill_" + quillType.getName());
		for (ItemController.EnumType keepsakeType : ItemController.EnumType.values())
			registerRender(ITEMS.CONTROLLER, keepsakeType.getMeta(), "controller_" + keepsakeType.getName());

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
		for (BlockDungeonBrick.EnumType type : BlockDungeonBrick.EnumType.values())
			registerRender(BLOCKS.DUNGEON_BRICK, type.getMeta(), "dungeon_brick_" + type.getName());
		registerRender(BLOCKS.DUNGEON_STAIRS);
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
		registerRender(BLOCKS.GOAT_GOLD_BRICK, 0, "goat_gold_brick_normal");
		registerRender(BLOCKS.GOAT_GOLD_BRICK, 1, "goat_gold_brick_glow");
		registerRender(BLOCKS.GOAT_GOLD_BRICK, 2, "goat_gold_brick_move");
		registerRender(BLOCKS.GOAT_GOLD_BRICK, 3, "goat_gold_brick_jump");
		registerRender(BLOCKS.GOAT_GOLD_BRICK, 4, "goat_gold_brick_wither");
		registerRender(BLOCKS.ICE_ROCK_CRYSTAL_BLOCK);
		registerRender(BLOCKS.ESTONE_CROCK);
		registerRender(BLOCKS.DUNGEON_DOOR);
		registerRender(BLOCKS.DUNGEON_DOOR_EXPAND);
		registerRender(BLOCKS.DUNGEON_FUNCTION);

		registerRender(TileMagicPlatform.class, new RenderTileMagicPlatform());
		registerRender(TileCrystalFlower.class, new RenderTileCrystalFlower());
		registerRender(TilePortalAltar.class, new RenderTileShowItem<TilePortalAltar>(0.65));
		registerRender(TileTranscribeTable.class, new RenderTileTranscribeTable());
		registerRender(TileIceRockCrystalBlock.class, new RenderTileIceRockSendRecv());

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
		registerRender(BLOCKS.MD_LIQUIDIZER, TileMDLiquidizer.class, new RenderTileMDLiquidizer());
		registerRender(BLOCKS.ELF_TREE_CORE, TileElfTreeCore.class, new RenderTileElfTreeCore());
		registerRender(BLOCKS.TRANSCRIBE_INJECTION, TileTranscribeInjection.class, new RenderTileTranscribeInjection());
		registerRender(BLOCKS.ELF_BEACON, TileElfBeacon.class, new RenderTileElfBeacon());
		registerRender(BLOCKS.ELEMENT_PLATFORM, TileElementPlatform.class, new RenderTileElementPlatform());
		registerRender(BLOCKS.DECONSTRUCT_WINDMILL, TileDeconstructWindmill.class, new RenderTileDeconstructWindmill());
		registerRender(BLOCKS.ELEMENT_TRANSLOCATOR, TileElementTranslocator.class, new RenderTileElementTranslocator());
		registerRender(BLOCKS.DEVOLVE_CUBE, TileDevolveCube.class, new RenderTileDevolveCube());
		registerRender(BLOCKS.DISINTEGRATE_STELA, TileDisintegrateStela.class, new RenderTileDisintegrateStela());
		registerRender(BLOCKS.ICE_ROCK_STAND, TileIceRockStand.class, new RenderTileIceRockStand());
		registerRender(BLOCKS.ICE_ROCK_NODE, TileIceRockNode.class, new RenderTileIceRockNode());
		registerRender(BLOCKS.ELEMENT_REACTOR, TileElementReactor.class, new RenderTileElementReactor());
		registerRender(BLOCKS.ESTONE_MATRIX, TileEStoneMatrix.class, new RenderTileEStoneMatrix());
		registerRender(BLOCKS.IS_CRAFT_CC, TileItemStructureCraftCC.class, new RenderTileItemStructureCraftCC());
		registerRender(BLOCKS.INSTANT_CONSTITUTE, TileInstantConstitute.class, new RenderTileInstantConstitute());

		registerRender(ITEMS.GRIMOIRE, new RenderItemGrimoire());
		registerRender(ITEMS.SPELLBOOK, RenderItemSpellbook.instance);
		registerRender(ITEMS.SPELLBOOK_ARCHITECTURE, RenderItemSpellbook.instance);
		registerRender(ITEMS.SPELLBOOK_ENCHANTMENT, RenderItemSpellbook.instance);
		registerRender(ITEMS.SPELLBOOK_LAUNCH, RenderItemSpellbook.instance);
		registerRender(ITEMS.SPELLBOOK_ELEMENT, RenderItemSpellbook.instance);
	}

	@SideOnly(Side.CLIENT)
	static void registerAllRenderPost() {
		registerRenderColor(ESObjects.BLOCKS.ELF_LEAF, ((BlockElfLeaf) ESObjects.BLOCKS.ELF_LEAF).getBlockColor());
		registerRenderColor(ESObjects.BLOCKS.ELF_FRUIT, ((BlockElfFruit) ESObjects.BLOCKS.ELF_FRUIT).getBlockColor());
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
		GameRegistry.registerTileEntity(tileEntityClass, new ResourceLocation(ESAPI.MODID, id));
		ES_TILE_ENTITY.add(tileEntityClass);
	}

	private static void register(Potion potion) {
		ForgeRegistries.POTIONS.register(potion);
	}

	private static void register(PotionType portionType) {
		ForgeRegistries.POTION_TYPES.register(portionType);
	}
//	
//	private static void register(String id, PotionEffect... effects) {
//		PotionType type = new PotionTypeES(TextHelper.castToCamel(id), effects)
//				.setRegistryName(new ResourceLocation(ESAPI.MODID, id));
//		ForgeRegistries.POTION_TYPES.register(type);
//	}

	@SideOnly(Side.CLIENT)
	public static void registerRender(Item item) {
		registerRender(item, 0);
	}

	@SideOnly(Side.CLIENT)
	private static void registerRender(Item item, int meta) {
		ModelResourceLocation model = new ModelResourceLocation(item.getRegistryName(), "inventory");
		ModelLoader.setCustomModelResourceLocation(item, meta, model);
	}

	@SideOnly(Side.CLIENT)
	private static void registerRender(Item item, int meta, String id) {
		ResourceLocation location = new ResourceLocation(item.getRegistryName().getNamespace(), id);
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
		registerRender(block, meta, block.getRegistryName().getPath());
	}

	@SideOnly(Side.CLIENT)
	private static void registerRender(Block block, int meta, String id) {
		ModelResourceLocation model = new ModelResourceLocation(ESAPI.MODID + ":" + id, "inventory");
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

	public static final void initModsObject(Class<?> cls) throws Throwable {
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			Object obj = field.get(cls);
			if (obj instanceof IForgeRegistryEntry) {
				((IForgeRegistryEntry) obj)
						.setRegistryName(new ResourceLocation(ESAPI.MODID, field.getName().toLowerCase()));
			}
			if (obj instanceof Item) {
				((Item) obj).setCreativeTab(tab);
			} else if (obj instanceof Block) {
				((Block) obj).setCreativeTab(tab);
			}
		}
	}

	public static final void registerModsObject(Class<?> cls) throws Throwable {
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			Object obj = field.get(cls);
			if (obj instanceof Item) register((Item) obj);
			else if (obj instanceof Block) register((Block) obj);
		}
	}

	private final static void preInitObjectForOtherMod() throws Throwable {
	}

	private final static void postInitObjectForOtherMod() throws Throwable {
		if (Mods.isLoaded(Mods.IC2)) ESIC2Core.postInit();
		if (Mods.isLoaded(Mods.AE2)) ESAE2Core.postInit();
	}

	@SideOnly(Side.CLIENT)
	private final static void preInitObjectForOtherModClient() throws Throwable {
	}

}
