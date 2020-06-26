package yuzunyannn.elementalsorcery.parchment;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonObject;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.ESData;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.item.ItemKyaniteTools;
import yuzunyannn.elementalsorcery.tile.TileRiteTable;

public class Pages {

	/** 记录所有的page */
	static final Map<String, Page> pages = new HashMap<>();
	/** 数组形式记录所有page */
	static final List<Page> pageList = new ArrayList<>();

	public static Set<String> getPageIds() {
		return pages.keySet();
	}

	/** 获取page个数 */
	public static int getCount() {
		return pages.size();
	}

	/** 是否有效 */
	public static boolean isVaild(String id) {
		return pages.containsKey(id);
	}

	/** 添加页面 */
	public static void addPage(String id, Page page) {
		page.id = id;
		pages.put(id, page);
		pageList.add(page);
	}

	/** 获取错误页面 */
	public static Page getErrorPage() {
		return pages.get("error");
	}

	/** 获取page */
	public static Page getPage(String id) {
		if (pages.containsKey(id)) return pages.get(id);
		return getErrorPage();
	}

	/** 通过index获取page */
	public static Page getPage(int index) {
		if (index < 0 || index >= pageList.size()) return getErrorPage();
		return pageList.get(index);
	}

	/** 是否有效 */
	public static boolean isVaild(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return false;
		return isVaild(nbt.getString("pId"));
	}

	/** 获取page */
	public static Page getPage(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return getErrorPage();
		return getPage(nbt.getString("pId"));
	}

	/** 设置page */
	public static ItemStack setPage(String id, ItemStack stack) {
		if (id == null) id = "error";
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) {
			nbt = new NBTTagCompound();
			stack.setTagCompound(nbt);
		}
		nbt.setString("pId", id);
		return stack;
	}

	/** 检测item需转跳的对应的page */
	static public Page itemToPage(Item item) {
		if (item instanceof ItemKyaniteTools.toolsCapability) return getPage(Pages.ABOUT_KYNATIE_TOOLS);
		else {
			for (Entry<Item, String> entry : Pages.itemToId) {
				if (entry.getKey() == item) return getPage(entry.getValue());
			}
		}
		return null;
	}

	/** 获取书的页面 */
	static public PageBook getBookPage() {
		return (PageBook) getPage(BOOK);
	}

	/** item转跳查询表 */
	static public List<Entry<Item, String>> itemToId = new ArrayList<Entry<Item, String>>();
	/** 端 */
	static private Side side;
	/** 基本页面 */
	static public final String ERROR = "error";
	static public final String BOOK = "book";
	/** 其他页面 */
	static public final String ABOUT_ELEMENT = "element";
	static public final String ABOUT_STELA = "stela";
	static public final String ABOUT_HEARTH = "hearth";
	static public final String ABOUT_SMELT_BOX = "smelt_box";
	static public final String ABOUT_KYANITE = "kyanite";
	static public final String ABOUT_MAGIC_PIECE = "magic_piece";
	static public final String ABOUT_STAR_SAND = "star_sand";
	static public final String ABOUT_STONE_MILL = "stone_mill";
	static public final String ABOUT_MAGIC_STONE = "magic_stone";
	static public final String ABOUT_MELT_CAULDRON = "melt_cauldron";
	static public final String ABOUT_ASTONE = "astone";
	static public final String ABOUT_MD = "md";
	static public final String ABOUT_INFUSION = "infusion";
	static public final String ABOUT_MAGICAL_ENDEREYE = "magical_ender_eye";
	static public final String ABOUT_KYNATIE_TOOLS = "kyanite_tools";
	static public final String ABOUT_ABSORB_BOX = "absorb_box";
	static public final String ABOUT_MAGIC_PLATFORM = "magic_platform";
	static public final String ABOUT_MAGIC_CRYSTAL = "magic_crystal";
	static public final String ABOUT_MAGIC_ESTONE = "estone";
	static public final String ABOUT_ELEMENT_CRY = "element_crystal";
	static public final String ABOUT_ENCHANTINGBOOK = "enchantbook";
	static public final String ABOUT_SPELL_CRYSTAL = "spell_crystal";
	static public final String ABOUT_EWORKBENCH = "element_workbench";
	static public final String ABOUT_MANUAL = "manual";
	static public final String ABOUT_DEC_BOX = "dec_box";
	static public final String ABOUT_MAGIC_PAPER = "magic_paper";
	static public final String ABOUT_SPELL_PAPER = "spell_paper";
	static public final String ABOUT_BOOKCOVER = "book_cover";
	static public final String ABOUT_SPELLBOOK = "spellbook";
	static public final String ABOUT_ELEMENT_CUBE = "element_cube";
	static public final String ABOUT_MAGIC_DESK = "magic_desk";
	static public final String ABOUT_SPLAUNCH = "spellbook_launch";
	static public final String ABOUT_SPELEMENT = "spellbook_element";

	// 注册
	static public void init(Side side) throws IOException {
		Pages.side = side;

		regPage(ERROR, new PageError());
		regPage(BOOK, new PageBook());

		ESData data = ElementalSorcery.data;
		final String MODID = ElementalSorcery.MODID;
		// 自动扫描parchment文件夹读取数据
		String[] parchments = data.getFilesFromResource(new ResourceLocation(MODID, "parchment"));
		for (String path : parchments) {
			try {
				if (!path.endsWith(".json")) continue;
				JsonObject jobj = data.getJsonFromResource(new ResourceLocation(MODID, "parchment/" + path));
				JsonParser.Packet packet = JsonParser.read(jobj);
				if (packet == null) continue;
				String id = path.substring(0, path.lastIndexOf('.'));
				regPage(id, packet.page);
				TileRiteTable.addPage(id, packet.level);
			} catch (Exception e) {
				ElementalSorcery.logger.warn("解析教程json出现异常：" + path, e);
			}
		}

		// regPage(ABOUT_ELEMENT, new PageSimple("element",
		// ESInitInstance.ITEMS.SPELLBOOK));
		regPage(ABOUT_STELA, aboutStela());
		// regPage(ABOUT_HEARTH, aboutHearth());
		// regPage(ABOUT_SMELT_BOX, aboutSmeltBox());
		// regPage(ABOUT_KYANITE, new PageSmeltingSimple("kyanite",
		// ESInitInstance.BLOCKS.KYANITE_ORE));
		// regPage(ABOUT_MAGIC_PIECE, new PageSimple("mgicalPiece",
		// ESInitInstance.ITEMS.MAGIC_PIECE));
		// regPage(ABOUT_STAR_SAND, new PageSimple("starSand", new
		// ItemStack(ESInitInstance.BLOCKS.STAR_SAND),new
		// ItemStack(ESInitInstance.BLOCKS.STAR_SAND)));
		// regPage(ABOUT_STONE_MILL, new PageCraftingSimple("stoneMill",
		// ESInitInstance.BLOCKS.STONE_MILL));
		// regPage(ABOUT_MAGIC_STONE, new PageCraftingSimple("magicStone",
		// ESInitInstance.ITEMS.MAGIC_STONE));
		// regPage(ABOUT_MELT_CAULDRON, new PageCraftingSimple("meltCauldron",
		// ESInitInstance.BLOCKS.MELT_CAULDRON));
		// regPage(ABOUT_ASTONE, aboutAStone());
		// regPage(ABOUT_MD, aboutMD());
		// regPage(ABOUT_INFUSION, new PageCraftingSimple("infusion",
		// ESInitInstance.BLOCKS.MD_INFUSION));
		// regPage(ABOUT_MAGICAL_ENDEREYE,new PageCraftingSimple("magicalEndereye",
		// ESInitInstance.ITEMS.MAGICAL_ENDER_EYE));
		// regPage(ABOUT_KYNATIE_TOOLS, aboutKynatieTools());
		// regPage(ABOUT_ABSORB_BOX, new PageCraftingSimple("absorbBox",
		// ESInitInstance.BLOCKS.ABSORB_BOX));
		// regPage(ABOUT_MAGIC_PLATFORM,
		// new PageCraftingSimple("magicPl", new
		// ItemStack(ESInitInstance.BLOCKS.MAGIC_PLATFORM, 1, 0),
		// new ItemStack(ESInitInstance.BLOCKS.MAGIC_PLATFORM, 1, 1)));
		// regPage(ABOUT_MAGIC_CRYSTAL, new PageTransformSimple("magicCrystal",
		// ESInitInstance.ITEMS.KYANITE,
		// ESInitInstance.ITEMS.MAGIC_CRYSTAL, PageTransform.INFUSION));
		// regPage(ABOUT_DEC_BOX, new PageCraftingSimple("decBox",
		// ESInitInstance.BLOCKS.DECONSTRUCT_BOX));
		// regPage(ABOUT_MAGIC_ESTONE, aboutEStone());
		// regPage(ABOUT_ELEMENT_CRY, new PageTransformSimple("elementCry",
		// ESInitInstance.ITEMS.MAGIC_CRYSTAL,
		// ESInitInstance.ITEMS.ELEMENT_CRYSTAL, PageTransform.INFUSION));
		// regPage(ABOUT_ENCHANTINGBOOK, aboutEnchantingBook());
		// regPage(ABOUT_SPELL_CRYSTAL, new PageTransformSimple("spellCrystal",
		// ESInitInstance.ITEMS.MAGIC_CRYSTAL,
		// ESInitInstance.ITEMS.SPELL_CRYSTAL, PageTransform.INFUSION));
		// regPage(ABOUT_EWORKBENCH, new PageCraftingSimple("EWorkbench",
		// ESInitInstance.BLOCKS.ELEMENT_WORKBENCH));
		// regPage(ABOUT_MANUAL, new PageCraftingSimple("manual",
		// ESInitInstance.ITEMS.MANUAL));
		// regPage(ABOUT_MAGIC_PAPER, new PageCraftingSimple("magicPaper",
		// ESInitInstance.ITEMS.MAGIC_PAPER));
		// regPage(ABOUT_SPELL_PAPER, new PageCraftingSimple("spellPaper",
		// ESInitInstance.ITEMS.SPELL_PAPER));
		// regPage(ABOUT_BOOKCOVER,
		// new PageCraftingSimple("bookCover", new
		// ItemStack(ESInitInstance.ITEMS.SPELLBOOK_COVER, 1, 0),
		// new ItemStack(ESInitInstance.ITEMS.SPELLBOOK_COVER, 1, 1)));
		// regPage(ABOUT_SPELLBOOK, aboutSpellbook());
		// regPage(ABOUT_ELEMENT_CUBE, new PageCraftingSimple("elementCube",
		// ESInitInstance.BLOCKS.ELEMENTAL_CUBE));
		// regPage(ABOUT_MAGIC_DESK, aboutMagicDesk());
		// regPage(ABOUT_SPLAUNCH, aboutSP("spLaunch", ESInitInstance.ITEMS.SPELLBOOK,
		// ESInitInstance.ITEMS.SPELLBOOK_LAUNCH, TileMagicDesk.AUTO_LAUNCH_BOOK));
		// regPage(ABOUT_SPELEMENT, aboutSP("spElement", ESInitInstance.ITEMS.SPELLBOOK,
		// ESInitInstance.ITEMS.SPELLBOOK_ELEMENT, TileMagicDesk.AUTO_ELEMENT_BOOK));
		initItemToId();
	}

	static private void initItemToId() {
		addItemId(ESInitInstance.BLOCKS.MD_INFUSION, ABOUT_INFUSION);
		addItemId(ESInitInstance.BLOCKS.MD_TRANSFER, ABOUT_MD);
		addItemId(ESInitInstance.BLOCKS.MD_MAGIC_GEN, ABOUT_MD);
		addItemId(ESInitInstance.BLOCKS.MAGIC_TORCH, ABOUT_MD);
		addItemId(ESInitInstance.BLOCKS.ASTONE, ABOUT_ASTONE);
		addItemId(ESInitInstance.BLOCKS.MELT_CAULDRON, ABOUT_MELT_CAULDRON);
		addItemId(ESInitInstance.ITEMS.MAGIC_STONE, ABOUT_MAGIC_STONE);
		addItemId(ESInitInstance.BLOCKS.STONE_MILL, ABOUT_STONE_MILL);
		addItemId(ESInitInstance.BLOCKS.KYANITE_ORE, ABOUT_KYANITE);
		addItemId(ESInitInstance.ITEMS.KYANITE, ABOUT_KYANITE);
		addItemId(ESInitInstance.ITEMS.MAGIC_PIECE, ABOUT_MAGIC_PIECE);
		addItemId(ESInitInstance.ITEMS.MAGICAL_ENDER_EYE, ABOUT_MAGICAL_ENDEREYE);
		addItemId(ESInitInstance.ITEMS.MAGIC_CRYSTAL, ABOUT_MAGIC_CRYSTAL);
		addItemId(ESInitInstance.ITEMS.SPELL_CRYSTAL, ABOUT_SPELL_CRYSTAL);
		addItemId(ESInitInstance.ITEMS.ELEMENT_CRYSTAL, ABOUT_ELEMENT_CRY);
		addItemId(ESInitInstance.ITEMS.SPELLBOOK_COVER, ABOUT_BOOKCOVER);
		addItemId(ESInitInstance.ITEMS.SPELLBOOK, ABOUT_SPELLBOOK);
		addItemId(ESInitInstance.ITEMS.SPELLBOOK_ENCHANTMENT, ABOUT_ENCHANTINGBOOK);
		addItemId(ESInitInstance.ITEMS.SPELL_PAPER, ABOUT_SPELL_PAPER);
	}

	static private void addItemId(Block block, String id) {
		itemToId.add(new AbstractMap.SimpleEntry(Item.getItemFromBlock(block), id));
	}

	static private void addItemId(Item item, String id) {
		itemToId.add(new AbstractMap.SimpleEntry(item, id));
	}

	static private String regPage(String id, Page page) {
		if (side.isClient()) addPage(id, page);
		else addPage(id, new Page());
		return id;
	}

	// 特殊性质的多页
	static private class PageMultS extends PageMult {

		public PageMultS(int showAt, Page... pages) {
			super(pages);
			this.lockShowAt(showAt);
		}
	}

	/*
	 * static private Page aboutHearth() { return new PageCraftingSimple("hearth",
	 * new ItemStack(ESInitInstance.BLOCKS.HEARTH, 1, 0), new
	 * ItemStack(ESInitInstance.BLOCKS.HEARTH, 1, 1), new
	 * ItemStack(ESInitInstance.BLOCKS.HEARTH, 1, 2)); }
	 */
	static private Page aboutStela() {
		return new PageMultS(2, new PageSimple("how2ply"),
				new PageCraftingSimple("parchment", ESInitInstance.ITEMS.PARCHMENT), new PageSimple("stela",
						new ItemStack(ESInitInstance.BLOCKS.STELA), new ItemStack(ESInitInstance.BLOCKS.STELA)));
	}

	/*
	 * static private Page aboutSmeltBox() { return new
	 * PageCraftingSimple("smeltBox", new
	 * ItemStack(ESInitInstance.BLOCKS.SMELT_BOX), new
	 * ItemStack(ESInitInstance.BLOCKS.SMELT_BOX_IRON), new
	 * ItemStack(ESInitInstance.BLOCKS.SMELT_BOX_KYANITE)); }
	 */

	/*
	 * static private Page aboutAStone() { return new PageMultS(0, new
	 * PageSimpleInfo("astone", "fir", new ItemStack(ESInitInstance.BLOCKS.ASTONE),
	 * new ItemStack(ESInitInstance.BLOCKS.ASTONE)), new PageSimpleInfo("astone",
	 * "sec", new ItemStack(ESInitInstance.BLOCKS.ASTONE), new
	 * ItemStack(ESInitInstance.BLOCKS.MELT_CAULDRON)), new PageSimpleInfo("astone",
	 * "thi"), new PageCraftingSimple("astoneCrafting", new
	 * ItemStack(ESInitInstance.BLOCKS.ASTONE, 1, 2), new
	 * ItemStack(ESInitInstance.BLOCKS.ASTONE, 1, 3), new
	 * ItemStack(ESInitInstance.BLOCKS.ASTONE, 1, 4), new
	 * ItemStack(ESInitInstance.BLOCKS.ASTONE, 1, 5), new
	 * ItemStack(ESInitInstance.BLOCKS.ASTONE, 1, 6))); }
	 */

	/*
	 * static private Page aboutMD() { return new PageMultS(0, new PageSimple("md",
	 * new ItemStack(ESInitInstance.BLOCKS.ASTONE)), new
	 * PageCraftingSimple("magicTorch", ESInitInstance.BLOCKS.MAGIC_TORCH), new
	 * PageCraftingSimple("magicGen", ESInitInstance.BLOCKS.MD_MAGIC_GEN), new
	 * PageCraftingSimple("magicTransfer", ESInitInstance.BLOCKS.MD_TRANSFER)); }
	 */

	/*
	 * static private Page aboutKynatieTools() { return new
	 * PageCraftingSimple("kynatieTools", new
	 * ItemStack(ESInitInstance.ITEMS.KYANITE_AXE), new
	 * ItemStack(ESInitInstance.ITEMS.KYANITE_HOE), new
	 * ItemStack(ESInitInstance.ITEMS.KYANITE_PICKAXE), new
	 * ItemStack(ESInitInstance.ITEMS.KYANITE_SPADE), new
	 * ItemStack(ESInitInstance.ITEMS.KYANITE_SWORD)); }
	 */

	/*
	 * static private Page aboutEStone() { return new PageCraftingSimple("estone",
	 * new ItemStack(ESInitInstance.BLOCKS.ESTONE, 1, 0), new
	 * ItemStack(ESInitInstance.BLOCKS.ESTONE, 1, 1), new
	 * ItemStack(ESInitInstance.BLOCKS.ESTONE, 1, 2), new
	 * ItemStack(ESInitInstance.BLOCKS.ESTONE_SLAB), new
	 * ItemStack(ESInitInstance.BLOCKS.ESTONE_STAIRS)); }
	 */

	/*
	 * static private Page aboutEnchantingBook() { ItemStack extra = new
	 * ItemStack(ESInitInstance.ITEMS.KYANITE_HOE);
	 * ((ItemKyaniteTools.toolsCapability) extra.getItem()).provide(extra);
	 * IElementInventory inv = new ElementInventory(); inv.insertElement(new
	 * ElementStack(ESInitInstance.ELEMENTS.ENDER, 10, 5), false);
	 * inv.saveState(extra); return new PageTransformSimple("enchantingBook", new
	 * ItemStack(Blocks.ENCHANTING_TABLE), new
	 * ItemStack(ESInitInstance.ITEMS.SPELLBOOK_ENCHANTMENT), extra, null,
	 * PageTransform.SEPARATE); }
	 */

	/*
	 * static private Page aboutSpellbook() { return new PageMultS(1, new
	 * PageSimpleInfo("spellbook", "fir"), new PageCraftingSimple("spellbook",
	 * ESInitInstance.ITEMS.SPELLBOOK), new PageSimpleInfo("spellbook", "sec")); }
	 */

	/*
	 * static private Page aboutMagicDesk() { return new PageMultS(0, new
	 * PageCraftingSimple("magicDesk", ESInitInstance.BLOCKS.MAGIC_DESK), new
	 * PageBuildingSimple("magicDesk", Buildings.SPELLBOOK_ALTAR), new
	 * PageSimpleInfo("magicDesk", "crafting"), new PageSimpleInfo("magicDesk",
	 * "charge")); }
	 */

	static private Page aboutSP(String name, Item book, Item newBook, List<ItemStack> list) {
		return new PageMultS(0, new PageTransformSimple(name, book, newBook, list, PageTransform.SPELLALTAR),
				new PageSimpleInfo(name, "info"));
	}

}
