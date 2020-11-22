package yuzunyannn.elementalsorcery.parchment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfessionScholar;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.item.ItemKyaniteTools;
import yuzunyannn.elementalsorcery.tile.TileRiteTable;
import yuzunyannn.elementalsorcery.util.json.Json;

public class Pages {

	/** 记录所有的page */
	static final Map<String, Page> pages = new HashMap<>();
	/** 数组形式记录所有page */
	static final List<Page> pageList = new ArrayList<>();

	public static Set<String> getPageIds() {
		return pages.keySet();
	}

	public static List<String> getPageNormalIds() {
		List<String> arr = new ArrayList<String>(pageList.size());
		for (int i = 2; i < pageList.size(); i++) arr.add(pageList.get(i).getId());
		return arr;
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

	/** 获取页面中的建筑 */
	public static Building getBuildingInPage(ItemStack stack) {
		Page page = getPage(stack);
		if (page == getErrorPage()) return null;
		if (page instanceof PageBuilding) return ((PageBuilding) page).building;
		if (page instanceof PageMult) {
			for (Page p : ((PageMult) page).pages) if (p instanceof PageBuilding) return ((PageBuilding) p).building;
		}
		return null;
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
			String key = Pages.itemToId.get(item);
			if (key != null) return getPage(key);
		}
		return null;
	}

	/** 获取书的页面 */
	static public PageBook getBookPage() {
		return (PageBook) getPage(BOOK);
	}

	/** item转跳查询表 */
	static public final Map<Item, String> itemToId = new HashMap<Item, String>();
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
		pages.clear();
		pageList.clear();
		itemToId.clear();
		// 其他教程相关
		ElfProfessionScholar.init();
		// 开始
		Pages.side = side;
		regPage(ERROR, new PageError());
		regPage(BOOK, new PageBook());
		// 自动扫描并加载json
		for (ModContainer mod : Loader.instance().getActiveModList()) loadParchments(mod);
		// 旧的
		regPage(ABOUT_STELA, aboutStela());
		// 转跳
		initItemToId();
	}

	static public void loadParchments(ModContainer mod) {
		Json.ergodicAssets(mod, "/parchments", (file, json) -> {
			JsonParser.Packet packet = JsonParser.read(json);
			if (packet == null) return false;
			String id = Json.fileToId(file, null);
			if (packet.need != null) for (String need : packet.need) packet.page.addRequire(Json.idFormat(need, null));
			if (packet.linked != null)
				for (String linked : packet.linked) addItemId(Item.getByNameOrId(Json.dealId(linked)), id);
			regPage(id, packet.page);
			if (packet.page.level < 0) {
				if (packet.page.level == -2) ElfProfessionScholar.addScholarPage(packet.page);
				return true;
			}
			TileRiteTable.addPage(id, packet.page.level);
			return true;
		});
	}

	static private void initItemToId() {
		addItemId(ESInit.BLOCKS.MD_INFUSION, ABOUT_INFUSION);
		addItemId(ESInit.BLOCKS.MD_TRANSFER, ABOUT_MD);
		addItemId(ESInit.BLOCKS.MD_MAGIC_GEN, ABOUT_MD);
		addItemId(ESInit.BLOCKS.MAGIC_TORCH, ABOUT_MD);
		addItemId(ESInit.BLOCKS.ASTONE, ABOUT_ASTONE);
		addItemId(ESInit.BLOCKS.MELT_CAULDRON, ABOUT_MELT_CAULDRON);
		addItemId(ESInit.ITEMS.MAGIC_STONE, ABOUT_MAGIC_STONE);
		addItemId(ESInit.BLOCKS.STONE_MILL, ABOUT_STONE_MILL);
		addItemId(ESInit.BLOCKS.KYANITE_ORE, ABOUT_KYANITE);
		addItemId(ESInit.ITEMS.KYANITE, ABOUT_KYANITE);
		addItemId(ESInit.ITEMS.MAGIC_PIECE, ABOUT_MAGIC_PIECE);
		addItemId(ESInit.ITEMS.MAGICAL_ENDER_EYE, ABOUT_MAGICAL_ENDEREYE);
		addItemId(ESInit.ITEMS.MAGIC_CRYSTAL, ABOUT_MAGIC_CRYSTAL);
		addItemId(ESInit.ITEMS.SPELL_CRYSTAL, ABOUT_SPELL_CRYSTAL);
		addItemId(ESInit.ITEMS.ELEMENT_CRYSTAL, ABOUT_ELEMENT_CRY);
		addItemId(ESInit.ITEMS.SPELLBOOK_COVER, ABOUT_BOOKCOVER);
		addItemId(ESInit.ITEMS.SPELLBOOK, ABOUT_SPELLBOOK);
		addItemId(ESInit.ITEMS.SPELLBOOK_ENCHANTMENT, ABOUT_ENCHANTINGBOOK);
		addItemId(ESInit.ITEMS.SPELL_PAPER, ABOUT_SPELL_PAPER);
	}

	static private void addItemId(Block block, String id) {
		itemToId.put(Item.getItemFromBlock(block), id);
	}

	static private void addItemId(Item item, String id) {
		if (item == null || item == Items.AIR) return;
		itemToId.put(item, id);
	}

	static private String regPage(String id, Page page) {
		page.id = id;
		if (side.isClient()) {
			addPage(id, page);
			return id;
		}
		Page recPage = new Page();
		if (page instanceof PageMult) out: {
			PageMult mult = (PageMult) page;
			for (Page p : mult.pages) {
				if (p instanceof PageBuilding) {
					recPage = new PageBuilding(((PageBuilding) p).building);
					break out;
				}
			}
		}
		addPage(id, recPage);
		return id;
	}

	// 特殊性质的多页
	static private class PageMultS extends PageMult {

		public PageMultS(int showAt, Page... pages) {
			super(pages);
			this.lockShowAt(showAt);
		}
	}

	static private Page aboutStela() {
		return new PageMultS(2, new PageSimple("how2ply"),
				new PageCraftingSimple("parchment", ESInit.ITEMS.PARCHMENT), new PageSimple("stela",
						new ItemStack(ESInit.BLOCKS.STELA), new ItemStack(ESInit.BLOCKS.STELA)));
	}

}
