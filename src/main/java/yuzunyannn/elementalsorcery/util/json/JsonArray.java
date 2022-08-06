package yuzunyannn.elementalsorcery.util.json;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.MathHelper;
import yuzunyannn.elementalsorcery.api.util.NBTTag;

public class JsonArray extends Json {

	final com.google.gson.JsonArray json;

	public JsonArray() {
		json = new com.google.gson.JsonArray();
	}

	public JsonArray(com.google.gson.JsonArray json) {
		this.json = json;
	}

	public JsonArray(NBTTagList nbt) {
		this();
		this.parse(nbt);
	}

	public JsonArray(NBTTagIntArray nbt) {
		this();
		this.parse(nbt);
	}

	public com.google.gson.JsonArray getGoogleJson() {
		return json;
	}

	@Override
	public int size() {
		return json.size();
	}

	@Override
	public boolean isObject() {
		return false;
	}

	@Override
	public String toString() {
		return json.toString();
	}

	public boolean hasString(int i) {
		if (i < 0 || i >= json.size()) return false;
		return json.get(i).isJsonPrimitive() ? json.get(i).getAsJsonPrimitive().isString() : false;
	}

	public String getString(int i) {
		return json.get(i).getAsString();
	}

	public void append(String str) {
		json.add(str);
	}

	public boolean hasNumber(int i) {
		if (i < 0 || i >= json.size()) return false;
		return json.get(i).isJsonPrimitive() ? json.get(i).getAsJsonPrimitive().isNumber() : false;
	}

	public Number getNumber(int i) {
		return json.get(i).getAsNumber();
	}

	public JsonArray append(Number i) {
		json.add(i);
		return this;
	}

	public boolean hasObject(int i) {
		if (i < 0 || i >= json.size()) return false;
		return json.get(i).isJsonObject();
	}

	public JsonObject getObject(int i) {
		return new JsonObject(json.get(i).getAsJsonObject());
	}

	public JsonArray append(JsonObject obj) {
		json.add(obj.getGoogleJson());
		return this;
	}

	public boolean hasArray(int i) {
		if (i < 0 || i >= json.size()) return false;
		return json.get(i).isJsonArray();
	}

	public JsonArray getArray(int i) {
		return new JsonArray(json.get(i).getAsJsonArray());
	}

	public JsonArray append(JsonArray obj) {
		json.add(obj.getGoogleJson());
		return this;
	}

	public String[] asStringArray() {
		String[] array = new String[json.size()];
		for (int i = 0; i < json.size(); i++) if (hasString(i)) array[i] = getString(i);
		return array;
	}

	public float[] asFloatArray() {
		float[] array = new float[json.size()];
		for (int i = 0; i < json.size(); i++) if (hasNumber(i)) array[i] = getNumber(i).floatValue();
		return array;
	}

	private NBTTagIntArray asNBTIntArray() {
		ArrayList<Integer> array = new ArrayList<>();
		for (JsonElement je : json) {
			if (je.isJsonPrimitive()) {
				JsonPrimitive jp = je.getAsJsonPrimitive();
				if (jp.isNumber()) array.add(je.getAsInt());
			}
		}
		int[] ints = new int[array.size()];
		for (int i = 0; i < ints.length; i++) ints[i] = array.get(i);
		return new NBTTagIntArray(ints);
	}

	@Override
	public NBTBase asNBT() {
		if (json.size() <= 0) return new NBTTagList();
		// 检查intarray
		JsonElement checkJE = json.get(0);
		if (checkJE.isJsonPrimitive()) {
			JsonPrimitive jp = checkJE.getAsJsonPrimitive();
			Number number = jp.getAsNumber();
			float f = number.floatValue();
			if (f - MathHelper.floor(f) == 0) return this.asNBTIntArray();
		}
		// 其他情况
		NBTTagList nbt = new NBTTagList();
		for (JsonElement je : json) {
			NBTBase base = null;
			if (je.isJsonNull()) continue;
			else if (je.isJsonObject()) base = new JsonObject(je.getAsJsonObject()).asNBT();
			else if (je.isJsonArray()) base = new JsonArray(je.getAsJsonArray()).asNBT();
			else if (je.isJsonPrimitive()) {
				JsonPrimitive jp = je.getAsJsonPrimitive();
				if (jp.isString()) base = new NBTTagString(je.getAsString());
				else if (jp.isBoolean()) base = new NBTTagByte((byte) (je.getAsBoolean() ? 1 : 0));
				else if (jp.isNumber()) {
					float n = je.getAsFloat();
					if (n == Math.floor(n)) base = new NBTTagInt(je.getAsInt());
					else base = new NBTTagFloat(je.getAsFloat());
				}
			}
			if (base == null) continue;
			if (nbt.getTagType() == 0) nbt.appendTag(base);
			else if (nbt.getTagType() == base.getId()) nbt.appendTag(base);
		}
		return nbt;

	}

	private void parse(NBTTagList nbt) {
		for (NBTBase base : nbt) {
			int id = base.getId();
			if (base instanceof NBTPrimitive) {
				NBTPrimitive primitive = ((NBTPrimitive) base);
				if (id == NBTTag.TAG_FLOAT) this.append(primitive.getFloat());
				else if (id == NBTTag.TAG_INT) this.append(primitive.getInt());
				else if (id == NBTTag.TAG_DOUBLE) this.append(primitive.getDouble());
				else if (id == NBTTag.TAG_LONG) this.append(primitive.getLong());
				else if (id == NBTTag.TAG_SHORT) this.append(primitive.getShort());
				else if (id == NBTTag.TAG_BYTE) this.append(primitive.getByte());
			} else if (id == NBTTag.TAG_STRING) this.append(((NBTTagString) base).getString());
			else if (id == NBTTag.TAG_COMPOUND) this.append(new JsonObject((NBTTagCompound) base));
			else if (id == NBTTag.TAG_LIST) this.append(new JsonArray((NBTTagList) base));
			else if (id == NBTTag.TAG_INT_ARRAY) this.append(new JsonArray((NBTTagIntArray) base));
		}
	}

	private void parse(NBTTagIntArray nbt) {
		for (int i : nbt.getIntArray()) this.append(i);
	}

	// ---> need <---

	public String needString(int i) {
		if (hasString(i)) return getString(i);
		throw exception(ParseExceptionCode.NOT_HAVE, i);
	}

	public Number needNumber(int i) {
		if (hasNumber(i)) return getNumber(i);
		throw exception(ParseExceptionCode.NOT_HAVE, i);
	}

	public JsonObject needObject(int i) {
		if (hasObject(i)) return getObject(i);
		throw exception(ParseExceptionCode.NOT_HAVE, i);
	}

	public JsonArray needArray(int i) {
		if (hasArray(i)) return getArray(i);
		throw exception(ParseExceptionCode.NOT_HAVE, i);
	}

	public List<ItemRecord> needItems(int i) {
		return loadItems(json.get(i));
	}
}
