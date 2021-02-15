package yuzunyannn.elementalsorcery.util.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.util.NBTTag;

public class JsonObject extends Json implements Iterable<String> {
	final com.google.gson.JsonObject json;

	public JsonObject() {
		json = new com.google.gson.JsonObject();
	}

	public JsonObject(Path file) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(file)) {
			Gson gson = new Gson();
			json = gson.fromJson(reader, com.google.gson.JsonObject.class);
		}
	}

	public JsonObject(File file) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			Gson gson = new Gson();
			json = gson.fromJson(reader, com.google.gson.JsonObject.class);
		}
	}

	public JsonObject(ResourceLocation resPath) throws IOException {
		String rPath = "/assets/" + resPath.getResourceDomain() + "/" + resPath.getResourcePath();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(JsonObject.class.getResourceAsStream(rPath)))) {
			Gson gson = new Gson();
			json = gson.fromJson(reader, com.google.gson.JsonObject.class);
		}
	}

	public JsonObject(Reader reader) throws IOException {
		Gson gson = new Gson();
		json = gson.fromJson(reader, com.google.gson.JsonObject.class);
	}

	public JsonObject(com.google.gson.JsonObject json) {
		this.json = json;
	}

	public JsonObject(NBTTagCompound nbt) {
		json = new com.google.gson.JsonObject();
		this.parse(nbt);
	}

	@Override
	public int size() {
		return json.size();
	}

	@Override
	public String toString() {
		return json.toString();
	}

	public com.google.gson.JsonObject getGoogleJson() {
		return json;
	}

	public Set<String> keySet() {
		Set<String> keys = new HashSet<>();
		for (Map.Entry<String, JsonElement> entry : json.entrySet()) keys.add(entry.getKey());
		return keys;
	}

	@Override
	public boolean isObject() {
		return true;
	}

	@Override
	public Iterator<String> iterator() {
		return keySet().iterator();
	}

	public boolean hasObject(String key) {
		return json.has(key) ? json.get(key).isJsonObject() : false;
	}

	public JsonObject getObject(String key) {
		return new JsonObject(json.get(key).getAsJsonObject());
	}

	public JsonObject needObject(String key) {
		if (this.hasObject(key)) return new JsonObject(json.get(key).getAsJsonObject());
		throw exception(ParseExceptionCode.NOT_HAVE, key);
	}

	public void set(String key, JsonObject obj) {
		json.add(key, obj.getGoogleJson());
	}

	public boolean hasArray(String key) {
		return json.has(key) ? json.get(key).isJsonArray() : false;
	}

	public JsonArray getArray(String key) {
		return new JsonArray(json.get(key).getAsJsonArray());
	}

	public JsonArray needArray(String... keys) {
		for (String key : keys) if (hasArray(key)) return getArray(key);
		throw exception(ParseExceptionCode.NOT_HAVE, Arrays.asList(keys).toString());
	}

	public void set(String key, JsonArray array) {
		json.add(key, array.getGoogleJson());
	}

	public boolean hasString(String key) {
		if (json.has(key)) {
			JsonElement je = json.get(key);
			return je.isJsonPrimitive() ? je.getAsJsonPrimitive().isString() : false;
		}
		return false;
	}

	public String getString(String key) {
		return json.get(key).getAsString();
	}

	public String needString(String... keys) {
		for (String key : keys) if (hasString(key)) return getString(key);
		throw exception(ParseExceptionCode.NOT_HAVE, Arrays.asList(keys).toString());
	}

	public void set(String key, String str) {
		json.add(key, new JsonPrimitive(str));
	}

	public boolean hasNumber(String key) {
		if (json.has(key)) {
			JsonElement je = json.get(key);
			return je.isJsonPrimitive() ? je.getAsJsonPrimitive().isNumber() : false;
		}
		return false;
	}

	public Number getNumber(String key) {
		return json.get(key).getAsNumber();
	}

	public Number needNumber(String... keys) {
		for (String key : keys) if (hasNumber(key)) return getNumber(key);
		throw exception(ParseExceptionCode.NOT_HAVE, Arrays.asList(keys).toString());
	}

	public void set(String key, Number n) {
		json.add(key, new JsonPrimitive(n));
	}

	@Override
	public NBTTagCompound asNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
			String key = entry.getKey();
			JsonElement je = entry.getValue();
			if (je.isJsonNull()) continue;
			else if (je.isJsonObject()) nbt.setTag(key, new JsonObject(je.getAsJsonObject()).asNBT());
			else if (je.isJsonArray()) nbt.setTag(key, new JsonArray(je.getAsJsonArray()).asNBT());
			else if (je.isJsonPrimitive()) {
				JsonPrimitive jp = je.getAsJsonPrimitive();
				if (jp.isString()) nbt.setString(key, je.getAsString());
				else if (jp.isBoolean()) nbt.setBoolean(key, je.getAsBoolean());
				else if (jp.isNumber()) {
					float n = je.getAsFloat();
					if (n == Math.floor(n)) nbt.setInteger(key, je.getAsInt());
					else nbt.setFloat(key, je.getAsFloat());
				}
			}
		}
		return nbt;
	}

	private void parse(NBTTagCompound nbt) {
		for (String key : nbt.getKeySet()) {
			NBTBase base = nbt.getTag(key);
			int id = base.getId();
			if (base instanceof NBTPrimitive) {
				NBTPrimitive primitive = ((NBTPrimitive) base);
				if (id == NBTTag.TAG_FLOAT) this.set(key, primitive.getFloat());
				else if (id == NBTTag.TAG_INT) this.set(key, primitive.getInt());
				else if (id == NBTTag.TAG_DOUBLE) this.set(key, primitive.getDouble());
				else if (id == NBTTag.TAG_LONG) this.set(key, primitive.getLong());
				else if (id == NBTTag.TAG_SHORT) this.set(key, primitive.getShort());
				else if (id == NBTTag.TAG_BYTE) this.set(key, primitive.getByte());
			} else if (id == NBTTag.TAG_STRING) this.set(key, ((NBTTagString) base).getString());
			else if (id == NBTTag.TAG_COMPOUND) this.set(key, new JsonObject((NBTTagCompound) base));
			else if (id == NBTTag.TAG_LIST) this.set(key, new JsonArray((NBTTagList) base));
			else if (id == NBTTag.TAG_INT_ARRAY) this.set(key, new JsonArray((NBTTagIntArray) base));
		}
	}

	public List<ItemRecord> needItems(String key) {
		return loadItems(json.get(key));
	}

	public ItemRecord needItem(String key) {
		List<ItemRecord> list = loadItems(json.get(key));
		if (list.isEmpty()) throw exception(ParseExceptionCode.EMPTY, "物品数据为空");
		return list.get(0);
	}

	public List<ElementStack> needElements(String key) {
		return loadElements(json.get(key));
	}

	public ElementStack needElement(String key) {
		List<ElementStack> list = loadElements(json.get(key));
		if (list.isEmpty()) throw exception(ParseExceptionCode.EMPTY, "元素数据为空");
		return list.get(0);
	}

	public List<Vec3d> needPos(String key) {
		return loadPos(json.get(key));
	}
}
