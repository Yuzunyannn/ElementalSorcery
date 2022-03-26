package yuzunyannn.elementalsorcery.parchment;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfessionScholar;
import yuzunyannn.elementalsorcery.tile.TileRiteTable;
import yuzunyannn.elementalsorcery.util.json.ItemRecord;
import yuzunyannn.elementalsorcery.util.json.Json;

public class Pages {

	public static final int PAGE_LEVEL_NONE = -1;
	public static final int PAGE_LEVEL_ELF = -2;

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
	static public Page itemToPage(ItemStack item) {
		String key = Pages.itemToId.get(item.getItem());
		if (key != null) return getPage(key);
		for (Entry<ItemStack, String> entry : stackToId)
			if (entry.getKey().isItemEqual(item)) return getPage(entry.getValue());
		return null;
	}

	/** 获取书的页面 */
	static public PageBook getBookPage() {
		return (PageBook) getPage(BOOK);
	}

	/** item转跳查询表 */
	static public final Map<Item, String> itemToId = new HashMap<Item, String>();
	static public final List<Entry<ItemStack, String>> stackToId = new ArrayList<>();
	/** 端 */
	static private Side side;
	/** 基本页面 */
	static public final String ERROR = "error";
	static public final String BOOK = "book";
	static public final String ROCK_CAMRERA = "rock_camera";

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
	}

	static public void loadParchments(ModContainer mod) {
		Json.ergodicAssets(mod, "/parchments", (file, json) -> {
			if (!ElementMap.checkModDemands(json)) return false;

			JsonParser.Packet packet = JsonParser.read(json);
			if (packet == null) return false;
			String id = Json.fileToId(file, null);
			if (packet.need != null) for (String need : packet.need) packet.page.addRequire(Json.idFormat(need, null));
			for (ItemRecord linked : packet.linked) {
				if (linked.isJustItem()) addItemId(linked.getItem(), id);
				else addItemId(linked.getStack(), id);
			}
			regPage(id, packet.page);
			if (packet.page.level < 0) {
				if (packet.page.level == PAGE_LEVEL_ELF) ElfProfessionScholar.addScholarPage(packet.page);
				return true;
			}
			TileRiteTable.addPage(id, packet.page.level);
			return true;
		});
	}

	static private void addItemId(Item item, String id) {
		if (item == null || item == Items.AIR) return;
		itemToId.put(item, id);
	}

	static private void addItemId(ItemStack stack, String id) {
		if (stack.isEmpty()) return;
		stackToId.add(new AbstractMap.SimpleEntry(stack, id));
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
		recPage.level = page.level;
		addPage(id, recPage);
		return id;
	}

}
