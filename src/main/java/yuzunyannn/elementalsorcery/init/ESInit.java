package yuzunyannn.elementalsorcery.init;

import java.util.concurrent.Callable;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.crash.CrashReport;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemMultiTexture.Mapper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
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
import yuzunyannn.elementalsorcery.api.ESRegister;
import yuzunyannn.elementalsorcery.api.ability.IElementInventory;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.block.BlocksEStone;
import yuzunyannn.elementalsorcery.block.altar.BlockElementalCube;
import yuzunyannn.elementalsorcery.block.container.BlockHearth;
import yuzunyannn.elementalsorcery.building.BuildingLib;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.capability.Spellbook;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.event.ESTestAndDebug;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.event.EventServer;
import yuzunyannn.elementalsorcery.init.registries.ESCraftingRegistries;
import yuzunyannn.elementalsorcery.init.registries.EntityRegistries;
import yuzunyannn.elementalsorcery.init.registries.OreDictionaryRegistries;
import yuzunyannn.elementalsorcery.init.registries.TileItemRenderRegistries;
import yuzunyannn.elementalsorcery.init.registries.VillegeRegistries;
import yuzunyannn.elementalsorcery.network.ESNetwork;
import yuzunyannn.elementalsorcery.parchment.Pages;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.item.RenderItemSpellbook;
import yuzunyannn.elementalsorcery.render.item.SpellbookRenderInfo;
import yuzunyannn.elementalsorcery.render.tile.RednerTileSupremeCraftingTable;
import yuzunyannn.elementalsorcery.render.tile.RenderTileAnalysisAltar;
import yuzunyannn.elementalsorcery.render.tile.RenderTileBuildingAltar;
import yuzunyannn.elementalsorcery.render.tile.RenderTileDeconstructAltarTable;
import yuzunyannn.elementalsorcery.render.tile.RenderTileElementCraftingTable;
import yuzunyannn.elementalsorcery.render.tile.RenderTileElementalCube;
import yuzunyannn.elementalsorcery.render.tile.RenderTileLantern;
import yuzunyannn.elementalsorcery.render.tile.RenderTileMagicDesk;
import yuzunyannn.elementalsorcery.render.tile.RenderTileMagicPlatform;
import yuzunyannn.elementalsorcery.render.tile.RenderTileMeltCauldron;
import yuzunyannn.elementalsorcery.render.tile.RenderTileStela;
import yuzunyannn.elementalsorcery.render.tile.RenderTileStoneMill;
import yuzunyannn.elementalsorcery.tile.TileAbsorbBox;
import yuzunyannn.elementalsorcery.tile.TileDeconstructBox;
import yuzunyannn.elementalsorcery.tile.TileHearth;
import yuzunyannn.elementalsorcery.tile.TileInfusionBox;
import yuzunyannn.elementalsorcery.tile.TileLantern;
import yuzunyannn.elementalsorcery.tile.TileMagicPlatform;
import yuzunyannn.elementalsorcery.tile.TileMeltCauldron;
import yuzunyannn.elementalsorcery.tile.TileSmeltBox;
import yuzunyannn.elementalsorcery.tile.TileStela;
import yuzunyannn.elementalsorcery.tile.TileStoneMill;
import yuzunyannn.elementalsorcery.tile.altar.TileAnalysisAltar;
import yuzunyannn.elementalsorcery.tile.altar.TileBuildingAltar;
import yuzunyannn.elementalsorcery.tile.altar.TileDeconstructAltarTable;
import yuzunyannn.elementalsorcery.tile.altar.TileElementCraftingTable;
import yuzunyannn.elementalsorcery.tile.altar.TileElementalCube;
import yuzunyannn.elementalsorcery.tile.altar.TileMagicDesk;
import yuzunyannn.elementalsorcery.tile.altar.TileSupremeCraftingTable;
import yuzunyannn.elementalsorcery.worldgen.WorldGeneratorES;

public class ESInit {

	public final static void preInit(FMLPreInitializationEvent event) {
		try {
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
			// 注册元素映射
			ElementMap.registerAll();
			// 注册实体
			EntityRegistries.registerAll();
			// 注册默认所有建筑
			BuildingLib.registerAll();
			// 测试村庄相关
			VillegeRegistries.registerAll();
			// 注册战利品
			registerAllLoot();
			// 注册GUI句柄
			NetworkRegistry.INSTANCE.registerGuiHandler(ElementalSorcery.instance, new ESGuiHandler());
			// 注册世界生成
			MinecraftForge.ORE_GEN_BUS.register(new WorldGeneratorES());
			// 注册网络
			ESNetwork.registerAll();
			// 注册事件
			MinecraftForge.EVENT_BUS.register(EventServer.class);
			// 测试类
			new ESTestAndDebug();
		} catch (Exception e) {
			CrashReport report = CrashReport.makeCrashReport(e, "Elementalsorcery初始化异常！");
			Minecraft.getMinecraft().crashed(report);
			Minecraft.getMinecraft().displayCrashReport(report);
		}
	}

	public final static void init(FMLInitializationEvent event) {
		// 注册所有配方
		ESCraftingRegistries.registerAll();
		// 初始化所有说明界面
		Pages.init(event.getSide());
		// 注册所有知识映射
		TileStela.init();
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
		// 客户端事件
		MinecraftForge.EVENT_BUS.register(EventClient.class);
	}

	@SideOnly(Side.CLIENT)
	public final static void postInitClinet(FMLPostInitializationEvent event) {

	}

	// 注册

	static void registerAllLoot() {
		LootTableList.register(new ResourceLocation(ElementalSorcery.MODID, "hall/es_hall"));
	}

	static void registerAllItems() {
		register(ESInitInstance.ITEMS.SPELLBOOK);
		register(ESInitInstance.ITEMS.SPELLBOOK_ARCHITECTURE);
		register(ESInitInstance.ITEMS.SPELLBOOK_ENCHANTMENT);
		register(ESInitInstance.ITEMS.SPELLBOOK_LAUNCH);
		register(ESInitInstance.ITEMS.SPELLBOOK_ELEMENT);

		register(ESInitInstance.ITEMS.KYNAITE);
		register(ESInitInstance.ITEMS.MAGICAL_PIECE);
		register(ESInitInstance.ITEMS.MAGICAL_ENDER_EYE);
		register(ESInitInstance.ITEMS.KYNAITE_PICKAXE);
		register(ESInitInstance.ITEMS.KYNAITE_AXE);
		register(ESInitInstance.ITEMS.KYNAITE_SPADE);
		register(ESInitInstance.ITEMS.KYNAITE_HOE);
		register(ESInitInstance.ITEMS.KYNAITE_SWORD);
		register(ESInitInstance.ITEMS.ARCHITECTURE_CRYSTAL);
		register(ESInitInstance.ITEMS.ELEMENT_CRYSTAL);
		register(ESInitInstance.ITEMS.MAGIC_CRYSTAL);
		register(ESInitInstance.ITEMS.PARCHMENT);
		register(ESInitInstance.ITEMS.MAGIC_PAPER);
		register(ESInitInstance.ITEMS.SPELL_PAPER);
		register(ESInitInstance.ITEMS.SPELL_CRYSTAL);
		register(ESInitInstance.ITEMS.SPELLBOOK_COVER);
		register(ESInitInstance.ITEMS.SCROLL);
		register(ESInitInstance.ITEMS.MANUAL);
		register(ESInitInstance.ITEMS.MAGIC_RULER);
		register(ESInitInstance.ITEMS.ITEM_CRYSTAL);
		register(ESInitInstance.ITEMS.MAGIC_STONE);
	}

	static void registerAllBlocks() {
		register(ESInitInstance.BLOCKS.HEARTH);
		register(ESInitInstance.BLOCKS.SMELT_BOX);
		register(ESInitInstance.BLOCKS.SMELT_BOX_IRON);
		register(ESInitInstance.BLOCKS.SMELT_BOX_KYNAITE);
		register(ESInitInstance.BLOCKS.KYNAITE_ORE);
		register(ESInitInstance.BLOCKS.KYNAITE_BLOCK);
		register(ESInitInstance.BLOCKS.ELEMENTAL_CUBE,
				((BlockElementalCube) ESInitInstance.BLOCKS.ELEMENTAL_CUBE).getItemBlock());
		register(ESInitInstance.BLOCKS.ESTONE);
		register(ESInitInstance.BLOCKS.ESTONE_SLAB,
				((BlocksEStone.EStoneSlab) ESInitInstance.BLOCKS.ESTONE_SLAB).getItemBlock());
		register(ESInitInstance.BLOCKS.ESTONE_STAIRS);
		register(ESInitInstance.BLOCKS.MAGIC_PLATFORM);
		register(ESInitInstance.BLOCKS.ABSORB_BOX);
		register(ESInitInstance.BLOCKS.INVALID_ENCHANTMENT_TABLE);
		register(ESInitInstance.BLOCKS.ELEMENT_WORKBENCH);
		register(ESInitInstance.BLOCKS.DECONSTRUCT_BOX);
		register(ESInitInstance.BLOCKS.INFUSION_BOX);
		register(ESInitInstance.BLOCKS.MAGIC_DESK);
		register(ESInitInstance.BLOCKS.ELEMENT_CRAFTING_TABLE);
		register(ESInitInstance.BLOCKS.DECONSTRUCT_ALTAR_TABLE);
		register(ESInitInstance.BLOCKS.STELA);
		register(ESInitInstance.BLOCKS.LANTERN);
		register(ESInitInstance.BLOCKS.BUILDING_ALTAR);
		register(ESInitInstance.BLOCKS.ANALYSIS_ALTAR);
		register(ESInitInstance.BLOCKS.SUPREME_CRAFTING_TABLE);
		register(ESInitInstance.BLOCKS.MAGIC_TORCH);
		register(ESInitInstance.BLOCKS.ASTONE);
		register(ESInitInstance.BLOCKS.STAR_STONE);
		register(ESInitInstance.BLOCKS.STAR_SAND);
		register(ESInitInstance.BLOCKS.STONE_MILL);
		register(ESInitInstance.BLOCKS.MELT_CAULDRON);
	}

	static void registerAllTiles() {
		register(TileElementalCube.class, "ElementalCrystal");
		register(TileHearth.class, "Hearth");
		register(TileSmeltBox.class, "SmeltBox");
		register(TileMagicPlatform.class, "MagicPlatform");
		register(TileAbsorbBox.class, "AbsorbBox");
		register(TileDeconstructBox.class, "DeconstructBox");
		register(TileInfusionBox.class, "InfusionBox");
		register(TileMagicDesk.class, "MagicDesk");
		register(TileElementCraftingTable.class, "ElementCraftingTable");
		register(TileDeconstructAltarTable.class, "DeconstructAltarTable");
		register(TileStela.class, "Stela");
		register(TileLantern.class, "Lantern");
		register(TileBuildingAltar.class, "BuildingAltar");
		register(TileAnalysisAltar.class, "AnalysisAltar");
		register(TileSupremeCraftingTable.class, "SupremeCraftingTable");
		register(TileStoneMill.class, "StoneMill");
		register(TileMeltCauldron.class, "MeltCauldron");
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
		// 初始化句柄
		SpellbookRenderInfo.renderInstance = RenderItemSpellbook.instance;

		registerRender(ESInitInstance.ITEMS.KYNAITE);
		registerRender(ESInitInstance.ITEMS.MAGICAL_PIECE);
		registerRender(ESInitInstance.ITEMS.MAGICAL_ENDER_EYE);
		registerRender(ESInitInstance.ITEMS.KYNAITE_PICKAXE);
		registerRender(ESInitInstance.ITEMS.KYNAITE_AXE);
		registerRender(ESInitInstance.ITEMS.KYNAITE_SPADE);
		registerRender(ESInitInstance.ITEMS.KYNAITE_HOE);
		registerRender(ESInitInstance.ITEMS.KYNAITE_SWORD);
		registerRender(ESInitInstance.ITEMS.ARCHITECTURE_CRYSTAL);
		registerRender(ESInitInstance.ITEMS.ELEMENT_CRYSTAL);
		registerRender(ESInitInstance.ITEMS.MAGIC_CRYSTAL);
		registerRender(ESInitInstance.ITEMS.PARCHMENT);
		registerRender(ESInitInstance.ITEMS.MAGIC_PAPER);
		registerRender(ESInitInstance.ITEMS.SPELL_PAPER);
		registerRender(ESInitInstance.ITEMS.SPELL_CRYSTAL);
		registerRender(ESInitInstance.ITEMS.SPELLBOOK_COVER, 0, "spellbook_cover");
		registerRender(ESInitInstance.ITEMS.SPELLBOOK_COVER, 1, "spellbook_back_cover");
		registerRender(ESInitInstance.ITEMS.SCROLL);
		registerRender(ESInitInstance.ITEMS.MANUAL);
		registerRender(ESInitInstance.ITEMS.MAGIC_RULER);
		registerRender(ESInitInstance.ITEMS.ITEM_CRYSTAL);
		registerRender(ESInitInstance.ITEMS.MAGIC_STONE);

		registerStateMapper(ESInitInstance.BLOCKS.HEARTH, BlockHearth.MATERIAL, "hearth");
		registerRender(ESInitInstance.BLOCKS.HEARTH, 0, "cobblestone_hearth");
		registerRender(ESInitInstance.BLOCKS.HEARTH, 1, "iron_hearth");
		registerRender(ESInitInstance.BLOCKS.HEARTH, 2, "kynaite_hearth");
		registerRender(ESInitInstance.BLOCKS.SMELT_BOX);
		registerRender(ESInitInstance.BLOCKS.SMELT_BOX_IRON);
		registerRender(ESInitInstance.BLOCKS.SMELT_BOX_KYNAITE);
		registerRender(ESInitInstance.BLOCKS.KYNAITE_ORE);
		registerRender(ESInitInstance.BLOCKS.KYNAITE_BLOCK);
		registerRender(ESInitInstance.BLOCKS.ESTONE, 0, "estone_default");
		registerRender(ESInitInstance.BLOCKS.ESTONE, 1, "estone_chiseled");
		registerRender(ESInitInstance.BLOCKS.ESTONE, 2, "estone_lines");
		registerRender(ESInitInstance.BLOCKS.ESTONE_SLAB);
		registerRender(ESInitInstance.BLOCKS.ESTONE_STAIRS);
		registerRender(ESInitInstance.BLOCKS.MAGIC_PLATFORM, 0, "magic_platform");
		registerRender(ESInitInstance.BLOCKS.MAGIC_PLATFORM, 1, "magic_platform_estone");
		registerRender(ESInitInstance.BLOCKS.ABSORB_BOX);
		registerRender(ESInitInstance.BLOCKS.INVALID_ENCHANTMENT_TABLE);
		registerRender(ESInitInstance.BLOCKS.ELEMENT_WORKBENCH);
		registerRender(ESInitInstance.BLOCKS.DECONSTRUCT_BOX);
		registerRender(ESInitInstance.BLOCKS.INFUSION_BOX);
		registerRender(ESInitInstance.BLOCKS.MAGIC_TORCH);
		registerRender(ESInitInstance.BLOCKS.ASTONE, 0, "astone");
		registerRender(ESInitInstance.BLOCKS.ASTONE, 1, "astone_fragmented");
		registerRender(ESInitInstance.BLOCKS.ASTONE, 2, "astone_smooth");
		registerRender(ESInitInstance.BLOCKS.ASTONE, 3, "astone_vein");
		registerRender(ESInitInstance.BLOCKS.ASTONE, 4, "astone_circle");
		registerRender(ESInitInstance.BLOCKS.STAR_STONE);
		registerRender(ESInitInstance.BLOCKS.STAR_SAND);

		registerRender(TileMagicPlatform.class, new RenderTileMagicPlatform());
		registerRender(new RenderTileElementalCube(), ESInitInstance.BLOCKS.ELEMENTAL_CUBE, TileElementalCube.class);
		registerRender(new RenderTileMagicDesk(), ESInitInstance.BLOCKS.MAGIC_DESK, TileMagicDesk.class);
		registerRender(new RenderTileElementCraftingTable(), ESInitInstance.BLOCKS.ELEMENT_CRAFTING_TABLE,
				TileElementCraftingTable.class);
		registerRender(new RenderTileDeconstructAltarTable(), ESInitInstance.BLOCKS.DECONSTRUCT_ALTAR_TABLE,
				TileDeconstructAltarTable.class);
		registerRender(new RenderTileStela(), ESInitInstance.BLOCKS.STELA, TileStela.class);
		registerRender(new RenderTileLantern(), ESInitInstance.BLOCKS.LANTERN, TileLantern.class);
		registerRender(new RenderTileBuildingAltar(), ESInitInstance.BLOCKS.BUILDING_ALTAR, TileBuildingAltar.class);
		registerRender(new RenderTileAnalysisAltar(), ESInitInstance.BLOCKS.ANALYSIS_ALTAR, TileAnalysisAltar.class);
		registerRender(new RednerTileSupremeCraftingTable(), ESInitInstance.BLOCKS.SUPREME_CRAFTING_TABLE,
				TileSupremeCraftingTable.class);
		registerRender(new RenderTileStoneMill(), ESInitInstance.BLOCKS.STONE_MILL, TileStoneMill.class);
		registerRender(new RenderTileMeltCauldron(), ESInitInstance.BLOCKS.MELT_CAULDRON, TileMeltCauldron.class);

		registerRender(ESInitInstance.ITEMS.SPELLBOOK, RenderItemSpellbook.instance);
		registerRender(ESInitInstance.ITEMS.SPELLBOOK_ARCHITECTURE, RenderItemSpellbook.instance);
		registerRender(ESInitInstance.ITEMS.SPELLBOOK_ENCHANTMENT, RenderItemSpellbook.instance);
		registerRender(ESInitInstance.ITEMS.SPELLBOOK_LAUNCH, RenderItemSpellbook.instance);
		registerRender(ESInitInstance.ITEMS.SPELLBOOK_ELEMENT, RenderItemSpellbook.instance);
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
		ESRegister.ELEMENT.register(element);
	}

	private static void register(Item item) {
		ForgeRegistries.ITEMS.register(item);
	}

	private static void register(Block block) {
		if (block instanceof Mapper)
			register(block, (Mapper) block);
		else
			register(block, new ItemBlock(block));
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
	private static <T extends TileEntity> void registerRender(Class<T> tile, TileEntitySpecialRenderer<T> renderer) {
		ClientRegistry.bindTileEntitySpecialRenderer(tile, renderer);
	}

	@SideOnly(Side.CLIENT)
	private static void registerRender(Block block, ItemMeshDefinition meshDefinition) {
		registerRender(Item.getItemFromBlock(block), meshDefinition);
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
	private static <T extends TileEntity, R extends TileEntitySpecialRenderer<T> & IRenderItem> void registerRender(
			R render_instance, Block block, Class<T> tile) {
		registerRender(tile, render_instance);
		registerRender(ItemBlock.getItemFromBlock(block), render_instance);
	}

}
