package yuzunyannn.elementalsorcery.parchment;

import static yuzunyannn.elementalsorcery.ElementalSorcery.logger;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.building.BuildingLib;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.tile.altar.TileMagicDesk;
import yuzunyannn.elementalsorcery.util.JsonHelper;
import yuzunyannn.elementalsorcery.util.JsonHelper.ItemRecord;

public class JsonParser {

	public static final ItemStack defaultIcon = new ItemStack(ESInitInstance.ITEMS.SPELLBOOK);

	public static class Packet {
		Page page;
		int level;
	}

	public static Packet read(JsonObject jobj) throws Exception {
		Page page = readPage(jobj);
		if (page == null) return null;
		Packet packet = new Packet();
		packet.page = page;
		if (JsonHelper.isNumber(jobj, "level")) packet.level = jobj.get("level").getAsInt();
		else if (JsonHelper.isNumber(jobj, "lev")) packet.level = jobj.get("lev").getAsInt();
		return packet;
	}

	private static Page readPage(JsonObject jobj) throws Exception {
		if (!JsonHelper.isString(jobj, "type")) return null;
		String type = jobj.get("type").getAsString();
		switch (type.toLowerCase()) {
		case "multpage":
		case "mult":
			return readPageMult(jobj);
		case "craft":
			return readPageCraft(jobj);
		case "smelting":
		case "smelt":
			return readPageTransform(jobj, PageTransform.SMELTING);
		case "infusion":
			return readPageTransform(jobj, PageTransform.INFUSION);
		case "separate":
			return readPageTransform(jobj, PageTransform.SEPARATE);
		case "spellaltar":
			return readPageTransform(jobj, PageTransform.SPELLALTAR);
		case "buidling":
			return readPageBuilding(jobj);
		default:
			return readPageSimple(jobj);
		}
	}

	/** 获取一个多重页面 */
	private static Page readPageMult(JsonObject jobj) throws Exception {
		if (!JsonHelper.isArray(jobj, "pages")) return null;
		List<Page> pages = new LinkedList<>();
		JsonArray jarray = jobj.get("pages").getAsJsonArray();
		for (JsonElement je : jarray) {
			if (!je.isJsonObject()) continue;
			Page p = readPage(je.getAsJsonObject());
			if (p == null) continue;
			pages.add(p);
		}
		if (pages.isEmpty()) throw new Exception("多页面找不到任何一个子页");
		PageMult page = new PageMult(pages.toArray(new Page[pages.size()]));
		if (JsonHelper.isNumber(jobj, "lock")) {
			int at = jobj.get("lock").getAsInt();
			page.lockShowAt(at);
		}
		return page;
	}

	/**
	 * 获取一个简单页面
	 */
	private static PageSimple readPageSimple(JsonObject jobj) throws Exception {
		ItemStack icon = defaultIcon;
		// 寻找图标
		if (JsonHelper.isString(jobj, "icon")) {
			String id = jobj.get("icon").getAsString();
			Item item = Item.getByNameOrId(id);
			if (item == null) {
				if (jobj.has("item")) {
					List<ItemRecord> irList = JsonHelper.readItems(jobj.get("item"));
					if (!irList.isEmpty()) {
						ItemRecord ir = irList.get(0);
						if (ir.isJustItem()) item = ir.getItem();
						else item = ir.getStack().getItem();
					}
				}
				if (item == null) logger.warn("找不到图标：" + id);
			}
			icon = item == null ? defaultIcon : new ItemStack(item);
		}
		// 寻找标题和正文
		String title = null;
		String value = null;
		if (JsonHelper.isString(jobj, "name")) {
			PageSimple tmp = null;
			title = jobj.get("name").getAsString();
			if (JsonHelper.isString(jobj, "differ")) tmp = new PageSimpleInfo(title, jobj.get("differ").getAsString());
			else tmp = new PageSimple(title);
			title = tmp.getTitle();
			value = tmp.getContext();
		}
		if (JsonHelper.isString(jobj, "title")) title = jobj.get("title").getAsString();
		if (JsonHelper.isString(jobj, "value")) value = jobj.get("value").getAsString();
		if (title == null) throw new Exception("找不到标题");
		// 寻找背景
		ItemStack background = ItemStack.EMPTY;
		if (JsonHelper.isString(jobj, "background")) {
			String id = jobj.get("background").getAsString();
			Item item = Item.getByNameOrId(id);
			if (item == null) logger.warn("找不到背景：" + id);
			else background = new ItemStack(item);
		}
		return new PageSimple(title, value == null ? "null" : value, icon, background);
	}

	/** 获取一个合成界面 */
	private static Page readPageCraft(JsonObject jobj) throws Exception {
		PageSimple page = readPageSimple(jobj);
		if (page == null) return null;
		List<ItemRecord> irList = JsonHelper.readItems(jobj.get("item"));
		if (irList.isEmpty()) throw new Exception("找不到任何合成物,位于：" + jobj.get("item"));
		List<ItemStack> list = JsonHelper.to(irList);
		// 有图标的情况复写下
		if (!page.getIcon().isEmpty()) {
			final ItemStack icon = page.getIcon();
			new PageCraftingSimple(page.getTitle(), page.getContext(), list.toArray(new ItemStack[list.size()])) {
				@Override
				public ItemStack getIcon() {
					return icon;
				}
			};
		}
		// 没有图标正常
		return new PageCraftingSimple(page.getTitle(), page.getContext(), list.toArray(new ItemStack[list.size()]));
	}

	/** 获取一个转化界面 */
	private static Page readPageTransform(JsonObject jobj, int id) throws Exception {
		PageSimple page = readPageSimple(jobj);
		if (page == null) return null;
		List<ItemRecord> irList = JsonHelper.readItems(jobj.get("item"));
		if (irList.isEmpty()) throw new Exception("找不到任何要转化的物品,位于：" + jobj.get("item"));
		List<ItemStack> list = JsonHelper.to(irList);
		switch (id) {
		case PageTransform.SMELTING:
			return new PageSmeltingSimple(page.getTitle(), page.getContext(), list.get(0));
		case PageTransform.INFUSION:
			if (list.size() < 2) throw new Exception("注魔item字段需要两个");
			return new PageTransformSimple(page.getTitle(), page.getContext(), list.get(0), list.get(1),
					ItemStack.EMPTY, null, PageTransform.INFUSION);
		case PageTransform.SEPARATE:
			if (list.size() < 3) throw new Exception("分离item字段需要三个");
			return new PageTransformSimple(page.getTitle(), page.getContext(), list.get(0), list.get(1), list.get(2),
					null, PageTransform.SPELLALTAR);
		case PageTransform.SPELLALTAR:
			if (list.size() < 2) throw new Exception("书桌和合成item字段需要两个");
			List<ItemStack> s = null;
			for (TileMagicDesk.Recipe r : TileMagicDesk.getRecipes()) {
				if (ItemStack.areItemsEqual(r.getOutput(), list.get(1))) {
					s = r.getSequence();
					break;
				}
			}
			if (s == null) throw new Exception("找不到书桌的合成表！");
			return new PageTransformSimple(page.getTitle(), page.getContext(), list.get(0), list.get(1),
					ItemStack.EMPTY, s, PageTransform.SPELLALTAR);
		default:
			break;
		}

		return null;
	}

	/** 获取一个建筑界面 */
	private static Page readPageBuilding(JsonObject jobj) throws Exception {
		PageSimple page = readPageSimple(jobj);
		if (page == null) return null;
		if (!JsonHelper.isString(jobj, "building")) throw new Exception("找不到任何合建筑,位于：" + jobj);
		String id = jobj.get("building").getAsString();
		Building building = BuildingLib.instance.getBuilding(id);
		if (building == null) throw new Exception("建筑不存在：" + id);
		PageBuildingSimple bpage = new PageBuildingSimple(page.getTitle(), building);
		// 额外添加
		JsonArray extra = null;
		if (JsonHelper.isArray(jobj, "extra")) extra = jobj.get("extra").getAsJsonArray();
		else if (JsonHelper.isArray(jobj, "add")) extra = jobj.get("add").getAsJsonArray();
		else if (JsonHelper.isArray(jobj, "attach")) extra = jobj.get("attach").getAsJsonArray();
		if (extra != null) {
			for (JsonElement je : extra) {
				if (je.isJsonObject()) {
					jobj = je.getAsJsonObject();
					List<Vector3f> v3fs = JsonHelper.readBlockPos(jobj.get("pos"));
					if (v3fs.isEmpty()) continue;
					List<ItemRecord> irList = JsonHelper.readItems(jobj.get("item"));
					if (irList.isEmpty()) continue;
					ItemStack stack = irList.get(0).getStack();
					Block block = Block.getBlockFromItem(stack.getItem());
					if (block == null || block == Blocks.AIR) continue;
					IBlockState state = block.getStateFromMeta(stack.getItemDamage());
					for (Vector3f v3f : v3fs) bpage.addExtraBlock(new BlockPos(v3f.x, v3f.y, v3f.z), state);
				}
			}
		}
		return bpage;
	}

}
