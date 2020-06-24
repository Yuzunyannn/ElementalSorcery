package yuzunyannn.elementalsorcery.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;

public class JsonHelper {

	/** json转化到nbt */
	static public NBTTagCompound jsonToNBT(JsonObject json) {
		NBTTagCompound nbt = new NBTTagCompound();
		for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
			String key = entry.getKey();
			JsonElement je = entry.getValue();
			if (je.isJsonNull()) continue;
			else if (je.isJsonObject()) nbt.setTag(key, jsonToNBT(json));
			else if (je.isJsonArray()) nbt.setTag(key, jsonToNBT(je.getAsJsonArray()));
			else if (je.isJsonPrimitive()) {
				JsonPrimitive jp = je.getAsJsonPrimitive();
				if (jp.isString()) nbt.setString(key, je.getAsString());
				else if (jp.isBoolean()) nbt.setBoolean(key, je.getAsBoolean());
				else if (jp.isNumber()) nbt.setFloat(key, je.getAsFloat());
			}
		}
		return nbt;
	}

	/** json转化到nbt */
	static public NBTTagList jsonToNBT(JsonArray json) {
		NBTTagList nbt = new NBTTagList();
		for (JsonElement je : json) {
			NBTBase base = null;
			if (je.isJsonNull()) continue;
			else if (je.isJsonObject()) base = jsonToNBT(je.getAsJsonObject());
			else if (je.isJsonArray()) base = jsonToNBT(je.getAsJsonArray());
			else if (je.isJsonPrimitive()) {
				JsonPrimitive jp = je.getAsJsonPrimitive();
				if (jp.isString()) base = new NBTTagString(je.getAsString());
				else if (jp.isBoolean()) base = new NBTTagByte((byte) (je.getAsBoolean() ? 1 : 0));
				else if (jp.isNumber()) base = new NBTTagFloat(je.getAsFloat());
			}
			if (base == null) continue;
			if (nbt.getTagType() == 0) nbt.appendTag(base);
			else if (nbt.getTagType() == base.getId()) nbt.appendTag(base);
		}
		return nbt;
	}

	static public boolean isArray(JsonObject jobj, String key) {
		if (!jobj.has(key)) return false;
		JsonElement je = jobj.get(key);
		if (je.isJsonArray()) return true;
		return false;
	}

	static public boolean isObject(JsonObject jobj, String key) {
		if (!jobj.has(key)) return false;
		JsonElement je = jobj.get(key);
		if (je.isJsonObject()) return true;
		return false;
	}

	static public boolean isNumber(JsonObject jobj, String key) {
		if (!jobj.has(key)) return false;
		JsonElement je = jobj.get(key);
		if (je.isJsonPrimitive() && je.getAsJsonPrimitive().isNumber()) return true;
		return false;
	}

	static public boolean isString(JsonObject jobj, String key) {
		if (!jobj.has(key)) return false;
		JsonElement je = jobj.get(key);
		if (je.isJsonPrimitive() && je.getAsJsonPrimitive().isString()) return true;
		return false;
	}

	/** 描述物品记录，是stack还是单单是物品 */
	public static class ItemRecord {
		private final ItemStack stack;
		private final Item item;

		public ItemRecord(ItemStack stack) {
			this.stack = stack;
			this.item = null;
		}

		public ItemRecord(Item item) {
			this.item = item;
			this.stack = ItemStack.EMPTY;
		}

		public boolean isJustItem() {
			return item != null;
		}

		public ItemStack getStack() {
			return stack;
		}

		public Item getItem() {
			return item;
		}

		@Override
		public String toString() {
			return item == null ? stack.toString() : item.getUnlocalizedName();
		}
	}

	static public List<ItemRecord> readItems(JsonElement je) {
		List<ItemRecord> list = new ArrayList<>();
		readItems(je, list);
		return list;
	}

	/** 根据json元素，获取物品 */
	static private void readItems(JsonElement je, List<ItemRecord> list) {
		if (je.isJsonNull()) return;
		// 单个字符串id
		if (je.isJsonPrimitive()) {
			if (je.getAsJsonPrimitive().isString()) {
				String id = je.getAsString();
				Item item = Item.getByNameOrId(id);
				if (item == null) {
					ElementalSorcery.logger.warn("找不到对应的物品：" + id);
					return;
				}
				list.add(new ItemRecord(item));
			}
		}
		// 带有类的
		else if (je.isJsonObject()) {
			JsonObject jobj = je.getAsJsonObject();
			if (!isString(jobj, "id")) return;
			if (jobj.size() == 1) readItems(jobj.get("id"), list);
			else {
				String id = jobj.get("id").getAsString();
				// 矿物词典
				if (isString(jobj, "type")) {
					ResourceLocation rl = new ResourceLocation(jobj.get("type").getAsString());
					if ("ore_dict".equals(rl.getResourcePath())) {
						NonNullList<ItemStack> oreList = OreDictionary.getOres(id);
						if (oreList.isEmpty()) ElementalSorcery.logger.warn("矿物词典：" + id + "中未包含任何内容");
						for (ItemStack stack : oreList) {
							if (stack.getHasSubtypes()) list.add(new ItemRecord(stack));
							else list.add(new ItemRecord(stack.getItem()));
						}
						return;
					}
				}
				// 没有矿物词典的情况
				Item item = Item.getByNameOrId(id);
				if (item == null) {
					ElementalSorcery.logger.warn("找不到对应的物品：" + id);
					return;
				}
				int meta = 1;
				if (isNumber(jobj, "damage")) meta = jobj.get("damage").getAsInt();
				else if (isNumber(jobj, "data")) meta = jobj.get("data").getAsInt();
				ItemStack stack = new ItemStack(item, 1, meta);
				list.add(new ItemRecord(stack));
			}
		}
		// 数组的
		else if (je.isJsonArray()) {
			JsonArray jarray = je.getAsJsonArray();
			for (JsonElement j : jarray) readItems(j, list);
		}
	}

	static public List<ElementStack> readElements(JsonElement je) {
		List<ElementStack> list = new ArrayList<>();
		readElements(je, list);
		return list;
	}

	/** 根据json元素，获取物品 */
	static private void readElements(JsonElement je, List<ElementStack> list) {
		if (je.isJsonNull()) return;
		// 带有类的
		if (je.isJsonObject()) {
			JsonObject jobj = je.getAsJsonObject();
			if (!isString(jobj, "id")) return;
			String id = jobj.get("id").getAsString();
			if (id.indexOf(':') == -1) id = ElementalSorcery.MODID + ":" + id;
			Element element = Element.getElementFromName(id);
			if (element == null) {
				ElementalSorcery.logger.warn("找不到对应的元素：" + id);
				return;
			}
			int count = 1;
			if (isNumber(jobj, "count")) count = jobj.get("count").getAsInt();
			else if (isNumber(jobj, "size")) count = jobj.get("size").getAsInt();
			int power = 1;
			if (isNumber(jobj, "power")) power = jobj.get("power").getAsInt();
			ElementStack estack = new ElementStack(element, count, power);
			list.add(estack);
		}
		// 数组的
		else if (je.isJsonArray()) {
			JsonArray jarray = je.getAsJsonArray();
			for (JsonElement j : jarray) readElements(j, list);
		}
	}

}
