package yuzunyannn.elementalsorcery.util.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
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
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.util.NBTTag;

public class JsonObject extends Json implements Iterable<String> {

	protected final com.google.gson.JsonObject json;

	public JsonObject() {
		json = new com.google.gson.JsonObject();
	}

	public JsonObject(Path file) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(file)) {
			Gson gson = new Gson();
			com.google.gson.JsonObject json = gson.fromJson(reader, com.google.gson.JsonObject.class);
			this.json = json == null ? new com.google.gson.JsonObject() : json;
		}
	}

	public JsonObject(File file) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"))) {
			Gson gson = new Gson();
			com.google.gson.JsonObject json = gson.fromJson(reader, com.google.gson.JsonObject.class);
			this.json = json == null ? new com.google.gson.JsonObject() : json;
		}
	}

	public JsonObject(ResourceLocation resPath) throws IOException {
		String rPath = "/assets/" + resPath.getNamespace() + "/" + resPath.getPath();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(JsonObject.class.getResourceAsStream(rPath), "utf-8"))) {
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

	public void remove(String key) {
		json.remove(key);
	}

	public boolean has(String key) {
		return json.has(key);
	}

	public boolean hasObject(String key) {
		return json.has(key) ? json.get(key).isJsonObject() : false;
	}

	public JsonObject getObject(String key) {
		return new JsonObject(json.get(key).getAsJsonObject());
	}

	public JsonObject getOrCreateObject(String key) {
		if (this.hasObject(key)) return this.getObject(key);
		JsonObject jobj;
		set(key, jobj = new JsonObject());
		return jobj;
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

	public JsonArray getOrCreateArray(String key) {
		if (this.hasArray(key)) return this.getArray(key);
		JsonArray jarray;
		set(key, jarray = new JsonArray());
		return jarray;
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

	public String getString(String key, String def) {
		if (hasString(key)) return getString(key);
		return def;
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

	public Number getNumber(String key, Number def) {
		if (hasNumber(key)) return getNumber(key);
		return def;
	}

	public void set(String key, Number n) {
		json.add(key, new JsonPrimitive(n));
	}

	public boolean hasBoolean(String key) {
		if (json.has(key)) {
			JsonElement je = json.get(key);
			return je.isJsonPrimitive() ? je.getAsJsonPrimitive().isBoolean() : false;
		}
		return false;
	}

	public boolean getBoolean(String key) {
		return json.get(key).getAsBoolean();
	}

	public void set(String key, boolean n) {
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

	public void save(File file, boolean useIndent) {
		try (FileWriter fileWriter = new FileWriter(file)) {
			JsonWriter jsonWriter = new JsonWriter(fileWriter);
			if (useIndent) jsonWriter.setIndent("  ");
			Streams.write(getGoogleJson(), jsonWriter);
		} catch (Exception e) {
			ElementalSorcery.logger.warn("json数据保存失败", e);
		}
	}

	public void merge(JsonObject other) {
		for (Map.Entry<String, JsonElement> entry : other.json.entrySet()) {
			json.add(entry.getKey(), entry.getValue());
		}
	}

	// ---> need <---

	public void need(String... keys) {
		for (String key : keys) if (has(key)) return;
		throw exception(ParseExceptionCode.NOT_HAVE, Arrays.asList(keys).toString());
	}

	public JsonArray needArray(String... keys) {
		for (String key : keys) if (hasArray(key)) return getArray(key);
		throw exception(ParseExceptionCode.NOT_HAVE, Arrays.asList(keys).toString());
	}

	public JsonObject needObject(String key) {
		if (this.hasObject(key)) return this.getObject(key);
		throw exception(ParseExceptionCode.NOT_HAVE, key);
	}

	public String needString(String... keys) {
		for (String key : keys) if (hasString(key)) return getString(key);
		throw exception(ParseExceptionCode.NOT_HAVE, Arrays.asList(keys).toString());
	}

	public Number needNumber(String... keys) {
		for (String key : keys) if (hasNumber(key)) return getNumber(key);
		throw exception(ParseExceptionCode.NOT_HAVE, Arrays.asList(keys).toString());
	}

	public String[] needStrings(String... keys) {
		for (String key : keys) {
			if (hasString(key)) return new String[] { getString(key) };
			else if (hasArray(key)) return getArray(key).asStringArray();
		}
		throw exception(ParseExceptionCode.NOT_HAVE, Arrays.asList(keys).toString());
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
