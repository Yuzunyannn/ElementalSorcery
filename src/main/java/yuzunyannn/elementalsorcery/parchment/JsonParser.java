package yuzunyannn.elementalsorcery.parchment;

import static yuzunyannn.elementalsorcery.ElementalSorcery.logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.building.BuildingLib;
import yuzunyannn.elementalsorcery.crafting.mc.RecipeRiteWrite;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.tile.altar.TileMagicDesk;
import yuzunyannn.elementalsorcery.util.json.ItemRecord;
import yuzunyannn.elementalsorcery.util.json.JsonHelperOld;

public class JsonParser {

	public static final ItemStack defaultIcon = new ItemStack(ESInitInstance.ITEMS.SPELLBOOK);

	public static class Packet {
		Page page;
		int level;
		List<String> need;
		List<String> linked;
	}

	public static Packet read(JsonObject jobj) throws JsonParseException {
		Page page = readPage(jobj);
		if (page == null) return null;
		Packet packet = new Packet();
		packet.page = page;
		yuzunyannn.elementalsorcery.util.json.JsonObject json = new yuzunyannn.elementalsorcery.util.json.JsonObject(
				jobj);
		try {
			packet.level = json.needNumber("level", "lev").intValue();
		} catch (JsonParseException e) {}
		if (json.hasArray("need")) {
			yuzunyannn.elementalsorcery.util.json.JsonArray array = json.getArray("need");
			packet.need = array.asStringArray();
		}
		if (json.hasArray("linked")) {
			yuzunyannn.elementalsorcery.util.json.JsonArray array = json.getArray("linked");
			packet.linked = array.asStringArray();
		} else if (json.hasString("linked")) {
			packet.linked = new ArrayList<String>();
			packet.linked.add(json.getString("linked"));
		}
		return packet;
	}

	private static Page readPage(JsonObject jobj) {
		if (!JsonHelperOld.isString(jobj, "type")) return null;
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
		case "rite":
			return readPageTransform(jobj, PageTransform.RITE);
		case "buidling":
			return readPageBuilding(jobj);
		default:
			return readPageSimple(jobj);
		}
	}

	/** 获取一个多重页面 */
	private static Page readPageMult(JsonObject jobj) {
		if (!JsonHelperOld.isArray(jobj, "pages")) return null;
		List<Page> pages = new LinkedList<>();
		JsonArray jarray = jobj.get("pages").getAsJsonArray();
		for (JsonElement je : jarray) {
			if (!je.isJsonObject()) continue;
			Page p = readPage(je.getAsJsonObject());
			if (p == null) continue;
			pages.add(p);
		}
		if (pages.isEmpty()) throw new JsonParseException("多页面找不到任何一个子页");
		PageMult page = new PageMult(pages.toArray(new Page[pages.size()]));
		if (JsonHelperOld.isNumber(jobj, "lock")) {
			int at = jobj.get("lock").getAsInt();
			page.lockShowAt(at);
		}
		return page;
	}

	/**
	 * 获取一个简单页面
	 */
	private static PageSimple readPageSimple(JsonObject jobj) {
		ItemStack icon = defaultIcon;
		// 寻找图标
		if (JsonHelperOld.isString(jobj, "icon")) {
			String id = jobj.get("icon").getAsString();
			Item item = Item.getByNameOrId(id);
			icon = item == null ? defaultIcon : new ItemStack(item);
		}
		if (icon == defaultIcon) {
			if (jobj.has("item")) {
				List<ItemRecord> irList = JsonHelperOld.readItems(jobj.get("item"));
				if (!irList.isEmpty()) {
					ItemRecord ir = irList.get(0);
					icon = ir.getStack();
				}
			}
		}
		// 寻找标题和正文
		String title = null;
		String value = null;
		if (JsonHelperOld.isString(jobj, "name")) {
			PageSimple tmp = null;
			title = jobj.get("name").getAsString();
			if (JsonHelperOld.isString(jobj, "differ"))
				tmp = new PageSimpleInfo(title, jobj.get("differ").getAsString());
			else tmp = new PageSimple(title);
			title = tmp.getTitle();
			value = tmp.getContext();
		}
		if (JsonHelperOld.isString(jobj, "title")) title = jobj.get("title").getAsString();
		if (JsonHelperOld.isString(jobj, "value")) value = jobj.get("value").getAsString();
		if (title == null) throw new JsonParseException("找不到标题");
		// 寻找背景
		ItemStack background = ItemStack.EMPTY;
		if (JsonHelperOld.isString(jobj, "background")) {
			String id = jobj.get("background").getAsString();
			if ("inherit".equals(id)) background = icon;
			else {
				Item item = Item.getByNameOrId(id);
				if (item == null) logger.warn("找不到背景：" + id);
				else background = new ItemStack(item);
			}
		}
		return new PageSimple(title, value == null ? "null" : value, icon, background);
	}

	/** 获取一个合成界面 */
	private static Page readPageCraft(JsonObject jobj) {
		PageSimple page = readPageSimple(jobj);
		if (page == null) return null;
		List<ItemRecord> irList = JsonHelperOld.readItems(jobj.get("item"));
		if (irList.isEmpty()) throw new JsonParseException("找不到任何合成物,位于：" + jobj.get("item"));
		List<ItemStack> list = JsonHelperOld.to(irList);
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
	private static Page readPageTransform(JsonObject jobj, int id) {
		PageSimple page = readPageSimple(jobj);
		if (page == null) return null;
		List<ItemRecord> irList = JsonHelperOld.readItems(jobj.get("item"));
		if (irList.isEmpty()) throw new JsonParseException("找不到任何要转化的物品,位于：" + jobj.get("item"));
		List<ItemStack> list = JsonHelperOld.to(irList);
		switch (id) {
		case PageTransform.SMELTING:
			return new PageSmeltingSimple(page.getTitle(), page.getContext(), list.get(0));
		case PageTransform.INFUSION:
			if (list.size() < 2) throw new JsonParseException("注魔item字段需要两个");
			return new PageTransformSimple(page.getTitle(), page.getContext(), list.get(0), list.get(1),
					ItemStack.EMPTY, null, id);
		case PageTransform.RITE:
			if (list.size() < 2) throw new JsonParseException("仪式item字段需要两个");
			ItemStack p = new ItemStack(ESInitInstance.ITEMS.PARCHMENT);
			RecipeRiteWrite.setInnerStack(p, list.get(0));
			return new PageTransformSimple(page.getTitle(), page.getContext(), p, list.get(1), list.get(0), null, id);
		case PageTransform.SEPARATE:
			if (list.size() < 3) throw new JsonParseException("分离item字段需要三个");
			return new PageTransformSimple(page.getTitle(), page.getContext(), list.get(0), list.get(1), list.get(2),
					null, PageTransform.SPELLALTAR);
		case PageTransform.SPELLALTAR:
			if (list.size() < 2) throw new JsonParseException("书桌和合成item字段需要两个");
			List<ItemStack> s = null;
			for (TileMagicDesk.Recipe r : TileMagicDesk.getRecipes()) {
				if (ItemStack.areItemsEqual(r.getOutput(), list.get(1))) {
					s = r.getSequence();
					break;
				}
			}
			if (s == null) throw new JsonParseException("找不到书桌的合成表！");
			return new PageTransformSimple(page.getTitle(), page.getContext(), list.get(0), list.get(1),
					ItemStack.EMPTY, s, PageTransform.SPELLALTAR);
		default:
			break;
		}

		return null;
	}

	/** 获取一个建筑界面 */
	private static Page readPageBuilding(JsonObject jobj) {
		try {
			PageSimple page = readPageSimple(jobj);
			if (page == null) return null;
			if (!JsonHelperOld.isString(jobj, "building")) throw new JsonParseException("找不到任何合建筑,位于：" + jobj);
			String id = jobj.get("building").getAsString();
			Building building = BuildingLib.instance.getBuilding(id);
			if (building == null) throw new JsonParseException("建筑不存在：" + id);
			PageBuildingSimple bpage = new PageBuildingSimple(page.getTitle(), building);
			// 额外添加,数组型,pos字段为位置，item字段为方块类型
			JsonArray extra = null;
			if (JsonHelperOld.isArray(jobj, "extra")) extra = jobj.get("extra").getAsJsonArray();
			else if (JsonHelperOld.isArray(jobj, "add")) extra = jobj.get("add").getAsJsonArray();
			else if (JsonHelperOld.isArray(jobj, "attach")) extra = jobj.get("attach").getAsJsonArray();
			if (extra != null) {
				for (JsonElement je : extra) {
					if (je.isJsonObject()) {
						jobj = je.getAsJsonObject();
						List<ItemRecord> irList = JsonHelperOld.readItems(jobj.get("item"));
						if (irList.isEmpty()) continue;
						ItemStack stack = irList.get(0).getStack();
						Block block = Block.getBlockFromItem(stack.getItem());
						if (block == null || block == Blocks.AIR) continue;
						IBlockState state = block.getStateFromMeta(stack.getItemDamage());
						String type = "";
						if (JsonHelperOld.isString(jobj, "type")) type = jobj.get("type").getAsString();
						switch (type) {
						case "full": {
							BlockPos from = new BlockPos(JsonHelperOld.readBlockPos(jobj.get("from")).get(0));
							BlockPos to = new BlockPos(JsonHelperOld.readBlockPos(jobj.get("to")).get(0));
							for (int x = from.getX(); x <= to.getX(); x++) {
								for (int y = from.getY(); y <= to.getY(); y++) {
									for (int z = from.getZ(); z <= to.getZ(); z++) {
										BlockPos pos = new BlockPos(x, y, z);
										bpage.addExtraBlockNotOverlap(pos, state);
									}
								}
							}
						}
							break;
						default: {
							List<Vec3d> v3fs = JsonHelperOld.readBlockPos(jobj.get("pos"));
							if (v3fs.isEmpty()) continue;
							for (Vec3d v3f : v3fs) bpage.addExtraBlock(new BlockPos(v3f.x, v3f.y, v3f.z), state);
						}
							break;
						}
					}
				}
			}
			return bpage;
		} catch (IllegalArgumentException e) {
			throw new JsonParseException("建筑数据加载出现异常：" + jobj);
		}
	}

}
