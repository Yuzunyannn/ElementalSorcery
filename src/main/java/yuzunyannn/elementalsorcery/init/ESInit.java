package yuzunyannn.elementalsorcery.init;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.storage.loot.LootTableList;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.block.BlockElfFruit;
import yuzunyannn.elementalsorcery.block.BlockElfLeaf;
import yuzunyannn.elementalsorcery.block.container.BlockHearth;
import yuzunyannn.elementalsorcery.building.BuildingLib;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.capability.Spellbook;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.elf.AutoName;
import yuzunyannn.elementalsorcery.elf.pro.ElfProRegister;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.event.EventServer;
import yuzunyannn.elementalsorcery.network.ESNetwork;
import yuzunyannn.elementalsorcery.parchment.Pages;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.item.RenderItemSpellbook;
import yuzunyannn.elementalsorcery.render.item.RenderItemSupremeTable;
import yuzunyannn.elementalsorcery.render.item.SpellbookRenderInfo;
import yuzunyannn.elementalsorcery.render.particle.Effects;
import yuzunyannn.elementalsorcery.render.tile.RenderTileAnalysisAltar;
import yuzunyannn.elementalsorcery.render.tile.RenderTileBuildingAltar;
import yuzunyannn.elementalsorcery.render.tile.RenderTileCrystalFlower;
import yuzunyannn.elementalsorcery.render.tile.RenderTileDeconstructAltarTable;
import yuzunyannn.elementalsorcery.render.tile.RenderTileElementCraftingTable;
import yuzunyannn.elementalsorcery.render.tile.RenderTileElementalCube;
import yuzunyannn.elementalsorcery.render.tile.RenderTileLantern;
import yuzunyannn.elementalsorcery.render.tile.RenderTileMagicDesk;
import yuzunyannn.elementalsorcery.render.tile.RenderTileMagicPlatform;
import yuzunyannn.elementalsorcery.render.tile.RenderTileMeltCauldron;
import yuzunyannn.elementalsorcery.render.tile.RenderTileRiteTable;
import yuzunyannn.elementalsorcery.render.tile.RenderTileStela;
import yuzunyannn.elementalsorcery.render.tile.RenderTileStoneMill;
import yuzunyannn.elementalsorcery.render.tile.RenderTileSupremeTable;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDAbsorbBox;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDBase;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDDeconstructBox;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDHearth;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDInfusion;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDMagicGen;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDMagicSolidify;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDMagiclization;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDResonantIncubator;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDRubbleRepair;
import yuzunyannn.elementalsorcery.render.tile.md.RenderTileMDTransfer;
import yuzunyannn.elementalsorcery.tile.TileAbsorbBox;
import yuzunyannn.elementalsorcery.tile.TileCrystalFlower;
import yuzunyannn.elementalsorcery.tile.TileDeconstructBox;
import yuzunyannn.elementalsorcery.tile.TileHearth;
import yuzunyannn.elementalsorcery.tile.TileItemStructureCraftNormal;
import yuzunyannn.elementalsorcery.tile.TileLantern;
import yuzunyannn.elementalsorcery.tile.TileLifeDirt;
import yuzunyannn.elementalsorcery.tile.TileMagicPlatform;
import yuzunyannn.elementalsorcery.tile.TileMeltCauldron;
import yuzunyannn.elementalsorcery.tile.TileRiteTable;
import yuzunyannn.elementalsorcery.tile.TileSmeltBox;
import yuzunyannn.elementalsorcery.tile.TileStela;
import yuzunyannn.elementalsorcery.tile.TileStoneMill;
import yuzunyannn.elementalsorcery.tile.altar.TileAnalysisAltar;
import yuzunyannn.elementalsorcery.tile.altar.TileBuildingAltar;
import yuzunyannn.elementalsorcery.tile.altar.TileDeconstructAltarTable;
import yuzunyannn.elementalsorcery.tile.altar.TileDeconstructAltarTableAdv;
import yuzunyannn.elementalsorcery.tile.altar.TileElementCraftingTable;
import yuzunyannn.elementalsorcery.tile.altar.TileElementalCube;
import yuzunyannn.elementalsorcery.tile.altar.TileMagicDesk;
import yuzunyannn.elementalsorcery.tile.altar.TilePortalAltar;
import yuzunyannn.elementalsorcery.tile.altar.TileSupremeTable;
import yuzunyannn.elementalsorcery.tile.md.TileMDAbsorbBox;
import yuzunyannn.elementalsorcery.tile.md.TileMDDeconstructBox;
import yuzunyannn.elementalsorcery.tile.md.TileMDHearth;
import yuzunyannn.elementalsorcery.tile.md.TileMDInfusion;
import yuzunyannn.elementalsorcery.tile.md.TileMDMagicGen;
import yuzunyannn.elementalsorcery.tile.md.TileMDMagicSolidify;
import yuzunyannn.elementalsorcery.tile.md.TileMDMagiclization;
import yuzunyannn.elementalsorcery.tile.md.TileMDResonantIncubator;
import yuzunyannn.elementalsorcery.tile.md.TileMDRubbleRepair;
import yuzunyannn.elementalsorcery.tile.md.TileMDTransfer;
import yuzunyannn.elementalsorcery.util.render.WorldScene;
import yuzunyannn.elementalsorcery.worldgen.WorldGeneratorES;

public class ESInit {

	public final static void preInit(FMLPreInitializationEvent event) throws Throwable {
		// 初始化创建所有实例
		ESInitInstance.instance();
		// 注册物品
		registerAllItems();
		// 注册方块
		registerAllBlocks();
		// 注册tileentity
		registerAllTiles();
		// 注册元素
		registerAllElements();
		// 注册能力
		registerAllCapability();
		// 矿物词典注册
		OreDictionaryRegistries.registerAll();
		// 注册实体
		EntityRegistries.registerAll();
		// 注册精灵相关
		ElfProRegister.registerAll();
		AutoName.init();
		// 注册默认所有建筑
		BuildingLib.registerAll();
		// 测试村庄相关
		VillegeRegistries.registerAll();
		// 注册战利品
		registerAllLoot();
		// 成就触发器
		ESCriteriaTriggers.init();
		// 注册GUI句柄
		NetworkRegistry.INSTANCE.registerGuiHandler(ElementalSorcery.instance, new ESGuiHandler());
		// 注册世界生成
		MinecraftForge.ORE_GEN_BUS.register(new WorldGeneratorES.genOre());
		MinecraftForge.EVENT_BUS.register(new WorldGeneratorES.genDecorate());
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
		TileStela.init();
	}

	public final static void postInit(FMLPostInitializationEvent event) throws Throwable {
		// 通过查找注册元素映射
		ElementMap.findAndRegisterCraft();
	}

	@SideOnly(Side.CLIENT)
	public final static void initClient(FMLPreInitializationEvent event) {
		// 设置自定义模型加载
		TileItemRenderRegistries.instance = new TileItemRenderRegistries();
		ModelLoaderRegistry.registerLoader(TileItemRenderRegistries.instance);
		// 注册所有渲染
		registerAllRender();
		// 注册实体渲染
		EntityRegistries.registerAllRender();
		// 所有需要网传的特效
		Effects.registerAll();
		// 客户端事件
		MinecraftForge.EVENT_BUS.register(EventClient.class);
		// 世界离屏渲染
		if (ElementalSorcery.config.PORTAL_RENDER_TYPE == 2) WorldScene.init();
	}

	@SideOnly(Side.CLIENT)
	public final static void postInitClinet(FMLPostInitializationEvent event) {
		// 注册所有渲染
		registerAllRenderPost();
	}

	// 注册

	static void registerAllLoot() {
		LootTableList.register(new ResourceLocation(ElementalSorcery.MODID, "hall/es_hall"));
	}

	static void registerAllItems() throws IllegalArgumentException, IllegalAccessException {
		// 遍历所有，将所有内容注册
		Class<?> cls = ESInitInstance.ITEMS.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			register((Item) field.get(ESInitInstance.ITEMS));
		}
	}

	static void registerAllBlocks() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// 遍历所有，将所有内容注册
		Class<?> cls = ESInitInstance.BLOCKS.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			Block block = (Block) field.get(ESInitInstance.BLOCKS);
			try {
				Method method = block.getClass().getDeclaredMethod("getItemBlock");
				register(block, (ItemBlock) method.invoke(block));
			} catch (NoSuchMethodException e) {
				register(block);
			}
		}
	}

	static void registerAllTiles() {
		register(TileElementalCube.class, "ElementalCube");
		register(TileHearth.class, "Hearth");
		register(TileSmeltBox.class, "SmeltBox");
		register(TileMagicPlatform.class, "MagicPlatform");
		register(TileAbsorbBox.class, "AbsorbBox");
		register(TileDeconstructBox.class, "DeconstructBox");
		register(TileMagicDesk.class, "MagicDesk");
		register(TileElementCraftingTable.class, "ElementCraftingTable");
		register(TileDeconstructAltarTable.class, "DeconstructAltarTable");
		register(TileDeconstructAltarTableAdv.class, "DeconstructAltarTableAdv");
		register(TileStela.class, "Stela");
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
		register(TileLifeDirt.class, "LifeDirt");
		register(TileCrystalFlower.class, "CrystalFlower");
		register(TileItemStructureCraftNormal.class, "ISCraftNormal");
		register(TilePortalAltar.class, "PortalAltar");
	}

	static void registerAllElements() {
		register(ESInitInstance.ELEMENTS.VOID);
		register(ESInitInstance.ELEMENTS.MAGIC);
		register(ESInitInstance.ELEMENTS.ENDER);
		register(ESInitInstance.ELEMENTS.FIRE);
		register(ESInitInstance.ELEMENTS.WATER);
		register(ESInitInstance.ELEMENTS.AIR);
		register(ESInitInstance.ELEMENTS.EARTH);
		register(ESInitInstance.ELEMENTS.METAL);
		register(ESInitInstance.ELEMENTS.WOOD);
		register(ESInitInstance.ELEMENTS.KNOWLEDGE);
	}

	static void registerAllCapability() {
		register(IElementInventory.class, new ElementInventory.Storage(), ElementInventory.class);
		register(Spellbook.class, new Spellbook.Storage(), Spellbook.class);
	}

	@SideOnly(Side.CLIENT)
	static void registerAllRender() {
		final ESObjects.Blocks BLOCKS = ESInitInstance.BLOCKS;
		final ESObjects.Items ITEMS = ESInitInstance.ITEMS;
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
		registerRender(ITEMS.MAGIC_PAPER);
		registerRender(ITEMS.SPELL_PAPER);
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
		registerRender(BLOCKS.ABSORB_BOX);
		registerRender(BLOCKS.INVALID_ENCHANTMENT_TABLE);
		registerRender(BLOCKS.ELEMENT_WORKBENCH);
		registerRender(BLOCKS.DECONSTRUCT_BOX);
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
		registerRender(BLOCKS.ELF_LOG_CABIN_CENTER);
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

		registerRender(TileMagicPlatform.class, new RenderTileMagicPlatform());
		registerRender(TileCrystalFlower.class, new RenderTileCrystalFlower());

		registerRender(BLOCKS.ELEMENTAL_CUBE, TileElementalCube.class, new RenderTileElementalCube());
		registerRender(BLOCKS.MAGIC_DESK, TileMagicDesk.class, new RenderTileMagicDesk());
		registerRender(BLOCKS.ELEMENT_CRAFTING_TABLE, TileElementCraftingTable.class,
				new RenderTileElementCraftingTable());
		registerRender(BLOCKS.DECONSTRUCT_ALTAR_TABLE, TileDeconstructAltarTable.class,
				new RenderTileDeconstructAltarTable(false));
		registerRender(BLOCKS.DECONSTRUCT_ALTAR_TABLE_ADV, TileDeconstructAltarTableAdv.class,
				new RenderTileDeconstructAltarTable(true));
		registerRender(BLOCKS.STELA, TileStela.class, new RenderTileStela());
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

		registerRender(ITEMS.SPELLBOOK, RenderItemSpellbook.instance);
		registerRender(ITEMS.SPELLBOOK_ARCHITECTURE, RenderItemSpellbook.instance);
		registerRender(ITEMS.SPELLBOOK_ENCHANTMENT, RenderItemSpellbook.instance);
		registerRender(ITEMS.SPELLBOOK_LAUNCH, RenderItemSpellbook.instance);
		registerRender(ITEMS.SPELLBOOK_ELEMENT, RenderItemSpellbook.instance);
	}

	@SideOnly(Side.CLIENT)
	static void registerAllRenderPost() {
		registerRenderColor(ESInitInstance.BLOCKS.ELF_LEAF,
				((BlockElfLeaf) ESInitInstance.BLOCKS.ELF_LEAF).getBlockColor());
		registerRenderColor(ESInitInstance.BLOCKS.ELF_FRUIT,
				((BlockElfFruit) ESInitInstance.BLOCKS.ELF_FRUIT).getBlockColor());
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

	private static void register(Element element) {
		ElementRegister.instance.register(element);
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
