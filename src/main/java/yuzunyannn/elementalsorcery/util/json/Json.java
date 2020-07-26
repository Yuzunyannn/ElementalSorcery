package yuzunyannn.elementalsorcery.util.json;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.oredict.OreDictionary;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;

public abstract class Json {

	abstract public NBTBase asNBT();

	abstract public boolean isObject();

	abstract public int size();

	/** 通过json元素获取物品组 */
	protected ArrayList<ItemRecord> loadItems(JsonElement json) {
		ArrayList<ItemRecord> list = new ArrayList<>();
		loadItems(json, list);
		return list;
	}

	private void loadItems(JsonElement json, List<ItemRecord> list) {
		if (json == null) return;
		if (json.isJsonPrimitive()) {
			if (json.getAsJsonPrimitive().isString()) {
				String id = json.getAsString();
				Item item = getItem(id);
				list.add(new ItemRecord(item));
				return;
			}
			throw exception(ParseExceptionCode.PATTERN_ERROR, "物品描述", "json字段不是字符串");
		}
		if (json.isJsonArray()) {
			JsonArray jarray = json.getAsJsonArray();
			for (JsonElement j : jarray) loadItems(j, list);
			return;
		} else if (json.isJsonObject()) {
			JsonObject jobj = new JsonObject(json.getAsJsonObject());
			String id = jobj.needString("id", "item", "ore");
			if (jobj.size() == 1) {
				// 一个，直接转到字符串
				loadItems(new JsonPrimitive(id), list);
				return;
			}
			if (jobj.hasString("type")) {
				ResourceLocation rl = new ResourceLocation(jobj.getString("type"));
				if ("ore_dict".equals(rl.getResourcePath())) {
					NonNullList<ItemStack> oreList = OreDictionary.getOres(id);
					if (oreList.isEmpty()) ElementalSorcery.logger.warn("矿物词典：" + id + "中未包含任何内容");
					for (ItemStack stack : oreList) {
						if (stack.getHasSubtypes()) list.add(new ItemRecord(stack.copy()));
						else list.add(new ItemRecord(stack.getItem()));
					}
					return;
				}
			}
			// 没有矿物词典的情况
			Item item = getItem(id);
			int meta = 0;
			int count = 1;
			try {
				meta = jobj.needNumber("data", "damage").intValue();
			} catch (JsonParseException e) {}
			try {
				count = jobj.needNumber("count", "size").intValue();
			} catch (JsonParseException e) {}
			ItemStack stack = new ItemStack(item, count, meta);
			// nbt
			if (jobj.hasObject("nbt")) stack.setTagCompound(jobj.getObject("nbt").asNBT());
			list.add(new ItemRecord(stack));
		}
	}

	/** 通过json元素获取元素 */
	protected ArrayList<ElementStack> loadElements(JsonElement json) {
		ArrayList<ElementStack> list = new ArrayList<>();
		loadElements(json, list);
		return list;
	}

	private void loadElements(JsonElement json, List<ElementStack> list) {
		if (json == null) return;
		if (json.isJsonPrimitive()) {
			if (json.getAsJsonPrimitive().isString()) {
				String id = json.getAsString();
				Element element = getElement(id);
				list.add(new ElementStack(element));
				return;
			}
			throw exception(ParseExceptionCode.PATTERN_ERROR, "元素描述", "json字段不是字符串");
		}
		if (json.isJsonArray()) {
			JsonArray jarray = json.getAsJsonArray();
			for (JsonElement j : jarray) loadElements(j, list);
			return;
		} else if (json.isJsonObject()) {
			JsonObject jobj = new JsonObject(json.getAsJsonObject());
			String id = jobj.needString("id", "element");
			id = dealId(id);
			Element element = getElement(id);
			int count = 1;
			int power = 10;

			try {
				count = jobj.needNumber("count", "size").intValue();
			} catch (JsonParseException e) {}
			try {
				power = jobj.needNumber("power").intValue();
			} catch (JsonParseException e) {}

			ElementStack estack = new ElementStack(element, count, power);
			list.add(estack);
		}
	}

	/** 获取坐标 */
	protected List<Vec3d> loadPos(JsonElement json) {
		List<Vec3d> list = new ArrayList<>();
		loadPos(json, list);
		return list;
	}

	/** 获取坐标 */
	private void loadPos(JsonElement je, List<Vec3d> list) {
		if (je == null || je.isJsonNull()) return;
		if (je.isJsonArray()) {
			JsonArray jarray = je.getAsJsonArray();
			if (jarray.size() <= 0) return;
			je = jarray.get(0);
			if (je.isJsonArray()) for (JsonElement j : jarray) loadPos(j, list);
			else if (je.isJsonPrimitive() && je.getAsJsonPrimitive().isNumber()) {
				if (jarray.size() != 3) throw exception(ParseExceptionCode.PATTERN_ERROR, "坐标", "方块坐标应当是大小为3的json数组");
				double x = je.getAsDouble();
				double y = jarray.get(1).getAsDouble();
				double z = jarray.get(2).getAsDouble();
				list.add(new Vec3d(x, y, z));
			}
		}
	}

	/** 遍历所有json内容 */
	public static void ergodicAssets(ModContainer mod, String path, BiFunction<Path, JsonObject, Boolean> func) {
		CraftingHelper.findFiles(mod, "assets/" + mod.getModId() + path, (root) -> true, (root, file) -> {
			if (!file.toString().endsWith(".json")) return false;
			try {
				Loader.instance().setActiveModContainer(mod);
				return func.apply(file, new JsonObject(file));
			} catch (IOException e) {
				ElementalSorcery.logger.warn("读取json文件过程中出现IO异常：" + file, e);
				return false;
			} catch (JsonSyntaxException e1) {
				ElementalSorcery.logger.warn("读取json文件的内容出现异常：" + file, e1);
				return false;
			} catch (JsonParseException e2) {
				ElementalSorcery.logger.warn("解析json出现异常：" + file, e2);
				return false;
			}
		}, true, true);
	}

	static public enum ParseExceptionCode {
		NOT_HAVE("NotHave", "找不到：%s"), PATTERN_ERROR("PatternError", "%s的格式错误，原因：%s"), EMPTY("Emtpy", "%s为空"),
		NOT_LOAD_MOD("NotLoadMod", "找不到：%s因为没有加载mod：%s");

		final String code;
		final String format;

		ParseExceptionCode(String code, String format) {
			this.code = "[" + code + "]";
			this.format = format;
		}

		public String getCode() {
			return code;
		}

		public String format(Object... args) {
			return String.format(format, args);
		}
	}

	/** 获取异常 */
	public static JsonParseException exception(ParseExceptionCode code, Object... args) {
		return new JsonParseException(code.getCode() + code.format(args));
	}

	static protected Item getItem(String id) {
		Item item = Item.getByNameOrId(id);
		if (item == null) {
			ResourceLocation lid = new ResourceLocation(id);
			if (!Loader.isModLoaded(lid.getResourceDomain()))
				throw exception(ParseExceptionCode.NOT_LOAD_MOD, id, lid.getResourceDomain());
			throw exception(ParseExceptionCode.NOT_HAVE, id);
		}
		return item;
	}

	static protected Element getElement(String id) {
		Element element = Element.getElementFromName(id);
		if (element == null) {
			ResourceLocation lid = new ResourceLocation(id);
			if (!Loader.isModLoaded(lid.getResourceDomain()))
				throw exception(ParseExceptionCode.NOT_LOAD_MOD, id, lid.getResourceDomain());
			throw exception(ParseExceptionCode.NOT_HAVE, id);
		}
		return element;
	}

	static public String dealId(String id) {
		if (id.indexOf(':') == -1) {
			ModContainer mod = Loader.instance().activeModContainer();
			if (mod == null) id = ElementalSorcery.MODID + ":" + id;
			else id = mod.getModId() + ":" + id;
		}
		return id;
	}

	static public String fileToId(Path file, String root) {
		return idFormat(file.toString(), root);
	}

	static public String idFormat(String id, String root) {
		int i;
		if (root == null) {
			i = id.lastIndexOf('/');
			if (i != -1) id = id.substring(i + 1);
			i = id.lastIndexOf('\\');
			if (i != -1) id = id.substring(i + 1);
		} else {
			char ch = root.charAt(root.length() - 1);
			if (ch != '\\' || ch != '/') root += "/";
			id = id.replace('\\', '/');
			i = id.lastIndexOf(root);
			if (i != -1) id = id.substring(i + root.length());
		}
		i = id.lastIndexOf(".");
		if (i != -1) id = id.substring(0, i);
		return id;
	}

	static public List<ItemStack> to(List<ItemRecord> list) {
		List<ItemStack> items = new ArrayList<ItemStack>(list.size());
		for (ItemRecord ir : list) {
			if (ir.isJustItem()) items.add(new ItemStack(ir.getItem()));
			else items.add(ir.getStack());
		}
		return items;
	}

}
