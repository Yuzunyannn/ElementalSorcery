package yuzunyannn.elementalsorcery.parchment;

import static yuzunyannn.elementalsorcery.ElementalSorcery.logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonParseException;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.building.BuildingBlocks;
import yuzunyannn.elementalsorcery.building.BuildingLib;
import yuzunyannn.elementalsorcery.crafting.mc.RecipeRiteWrite;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.tile.altar.TileMagicDesk;
import yuzunyannn.elementalsorcery.util.json.ItemRecord;
import yuzunyannn.elementalsorcery.util.json.Json;
import yuzunyannn.elementalsorcery.util.json.Json.ParseExceptionCode;
import yuzunyannn.elementalsorcery.util.json.JsonArray;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class JsonParser {

	public static final ItemStack defaultIcon = new ItemStack(ESInit.ITEMS.SPELLBOOK);

	public static class Packet {
		Page page;
		List<String> need;
		List<ItemRecord> linked;
	}

	public static Packet read(JsonObject json) throws JsonParseException {
		Page page = readPage(json);
		if (page == null) return null;
		Packet packet = new Packet();
		packet.page = page;
		try {
			packet.page.level = json.needNumber("level", "lev").intValue();
		} catch (JsonParseException e) {}
		if (json.hasArray("need")) {
			JsonArray array = json.getArray("need");
			packet.need = new ArrayList<>(Arrays.asList(array.asStringArray()));
		}
		packet.linked = json.needItems("linked");
		return packet;
	}

	private static Page readPage(JsonObject json) {
		if (!json.hasString("type")) throw Json.exception(ParseExceptionCode.NOT_HAVE, "type");
		String type = json.getString("type");
		switch (type.toLowerCase()) {
		case "multpage":
		case "mult":
			return readPageMult(json);
		case "craft":
			return readPageCraft(json);
		case "smelting":
		case "smelt":
			return readPageTransform(json, PageTransform.SMELTING);
		case "infusion":
			return readPageTransform(json, PageTransform.INFUSION);
		case "separate":
			return readPageTransform(json, PageTransform.SEPARATE);
		case "spellaltar":
			return readPageTransform(json, PageTransform.SPELLALTAR);
		case "rite":
			return readPageTransform(json, PageTransform.RITE);
		case "research":
			return readPageTransform(json, PageTransform.RESEARCH);
		case "buidling":
			return readPageBuilding(json);
		default:
			return readPageSimple(json);
		}
	}

	/** 获取一个多重页面 */
	private static Page readPageMult(JsonObject json) {
		JsonArray jarray = json.needArray("pages");
		List<Page> pages = new LinkedList<>();
		for (int i = 0; i < jarray.size(); i++) {
			if (!jarray.hasObject(i)) continue;
			Page p = readPage(jarray.getObject(i));
			pages.add(p);
		}
		if (pages.isEmpty()) throw Json.exception(ParseExceptionCode.EMPTY, "多页面");
		PageMult page = new PageMult(pages.toArray(new Page[pages.size()]));
		if (json.hasNumber("lock")) {
			int at = json.getNumber("lock").intValue();
			page.lockShowAt(at);
		}
		return page;
	}

	/** 获取一个简单页面 */
	private static PageSimple readPageSimple(JsonObject json) {
		ItemStack icon = defaultIcon;
		try {
			icon = json.needItem("icon").getStack();
		} catch (JsonParseException e) {
			try {
				icon = json.needItem("item").getStack();
			} catch (JsonParseException e1) {}
		}
		String title = null;
		String value = null;
		if (json.hasString("name")) {
			PageSimple tmp = null;
			title = json.getString("name");
			if (json.hasString("differ")) tmp = new PageSimpleInfo(title, json.getString("differ"));
			else tmp = new PageSimple(title);
			title = tmp.getTitle();
			value = tmp.getContext();
		}
		if (json.hasString("title")) title = json.getString("title");
		if (json.hasString("value")) value = json.getString("value");
		if (title == null) throw Json.exception(ParseExceptionCode.NOT_HAVE, "title");
		ItemStack background = ItemStack.EMPTY;
		if (json.hasString("background")) {
			String id = json.getString("background");
			if ("inherit".equals(id)) background = icon;
			else {
				Item item = Item.getByNameOrId(id);
				if (item == null) logger.warn("找不到背景：" + id);
				else background = new ItemStack(item);
			}
		} else if (json.hasObject("background")) {
			ItemRecord itemRecord = json.needItem("background");
			background = itemRecord.getStack();
		}
		return new PageSimple(title, value == null ? "null" : value, icon, background);
	}

	/** 获取一个合成界面 */
	private static Page readPageCraft(JsonObject json) {
		PageSimple page = readPageSimple(json);
		List<ItemRecord> irList = json.needItems("item");
		if (irList.isEmpty()) throw Json.exception(ParseExceptionCode.EMPTY, "item");
		List<ItemStack> list = Json.to(irList);
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
	private static Page readPageTransform(JsonObject json, int id) {
		PageSimple page = readPageSimple(json);
		List<ItemRecord> irList = json.needItems("item");
		if (irList.isEmpty()) throw Json.exception(ParseExceptionCode.EMPTY, "item");
		List<ItemStack> list = Json.to(irList);
		switch (id) {
		case PageTransform.SMELTING:
			return new PageSmeltingSimple(page.getTitle(), page.getContext(), list.get(0));
		case PageTransform.INFUSION:
			if (list.size() < 2) throw new JsonParseException("注魔item字段需要两个");
			return new PageTransformSimple(page.getTitle(), page.getContext(), list.get(0), list.get(1),
					ItemStack.EMPTY, null, id);
		case PageTransform.RITE:
			if (list.size() < 2) throw new JsonParseException("仪式item字段需要两个");
			ItemStack p = new ItemStack(ESInit.ITEMS.PARCHMENT);
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
		case PageTransform.RESEARCH:
			if (list.size() < 1) throw new JsonParseException("研究合成item字段需要一个输出");
			return new PageResearchSimple(page.getTitle(), page.getContext(), list.get(0));
		default:
			throw Json.exception(ParseExceptionCode.PATTERN_ERROR, "转化id", "未知的id");
		}
	}

	private static Page readPageBuilding(JsonObject json) {
		try {
			PageSimple page = readPageSimple(json);
			String id = json.needString("building");
			Building building = BuildingLib.instance.getBuilding(id);
			if (building == null) throw Json.exception(ParseExceptionCode.NOT_HAVE, "building");
			PageBuildingSimple bPage = new PageBuildingSimple(page.getTitle(), building);
			// 额外添加,数组型,pos字段为位置，item字段为方块类型
			try {
				JsonArray extra = json.needArray("extra", "add", "attach");
				for (int i = 0; i < extra.size(); i++) {
					if (!extra.hasObject(i)) continue;
					json = extra.getObject(i);
					// 类型
					String type = "";
					if (json.hasString("type")) type = json.getString("type");
					if ("building".equals(type)) {
						// 设置建筑的情况
						List<Vec3d> v3fs = json.needPos("pos");
						id = json.needString("building", "id");
						building = BuildingLib.instance.getBuilding(id);
						if (building == null) continue;
						for (Vec3d v3f : v3fs) {
							BlockPos pos = new BlockPos(v3f);
							BuildingBlocks iter = building.getBuildingIterator().setPosOff(pos);
							while (iter.next()) bPage.addExtraBlockNotOverlap(iter.getPos(), iter.getState());
						}
					} else {
						// 获取blockstate
						List<ItemRecord> irList = json.needItems("item");
						if (irList.isEmpty()) continue;
						ItemStack stack = irList.get(0).getStack();
						Block block = Block.getBlockFromItem(stack.getItem());
						if (block == null || block == Blocks.AIR) continue;
						IBlockState state = block.getStateFromMeta(stack.getItemDamage());
						// 填充
						if ("full".equals(type)) {
							BlockPos from = new BlockPos(json.needPos("from").get(0));
							BlockPos to = new BlockPos(json.needPos("to").get(0));
							for (int x = from.getX(); x <= to.getX(); x++) {
								for (int y = from.getY(); y <= to.getY(); y++) {
									for (int z = from.getZ(); z <= to.getZ(); z++) {
										BlockPos pos = new BlockPos(x, y, z);
										bPage.addExtraBlockNotOverlap(pos, state);
									}
								}
							}
						}
						// 普通
						else {
							List<Vec3d> v3fs = json.needPos("pos");
							if (v3fs.isEmpty()) continue;
							for (Vec3d v3f : v3fs) bPage.addExtraBlock(new BlockPos(v3f.x, v3f.y, v3f.z), state);
						}
					}
				}
			} catch (JsonParseException e) {}
			return bPage;
		} catch (IllegalArgumentException e) {
			throw Json.exception(ParseExceptionCode.PATTERN_ERROR, "建筑", "建筑添加等操作异常");
		}
	}

}
