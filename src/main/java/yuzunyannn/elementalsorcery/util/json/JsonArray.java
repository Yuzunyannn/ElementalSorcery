package yuzunyannn.elementalsorcery.util.json;

import java.util.ArrayList;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class JsonArray extends Json {

	final com.google.gson.JsonArray json;

	public JsonArray() {
		json = new com.google.gson.JsonArray();
	}

	public JsonArray(com.google.gson.JsonArray json) {
		this.json = json;
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

	public String needString(int i) {
		if (hasString(i)) return getString(i);
		throw exception(ParseExceptionCode.NOT_HAVE, i);
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

	public Number needNumber(int i) {
		if (hasNumber(i)) return getNumber(i);
		throw exception(ParseExceptionCode.NOT_HAVE, i);
	}

	public void append(Number i) {
		json.add(i);
	}

	public boolean hasObject(int i) {
		if (i < 0 || i >= json.size()) return false;
		return json.get(i).isJsonObject();
	}

	public JsonObject getObject(int i) {
		return new JsonObject(json.get(i).getAsJsonObject());
	}

	public JsonObject needObject(int i) {
		if (hasObject(i)) return getObject(i);
		throw exception(ParseExceptionCode.NOT_HAVE, i);
	}

	public void append(JsonObject obj) {
		json.add(obj.getGoogleJson());
	}

	public boolean hasArray(int i) {
		if (i < 0 || i >= json.size()) return false;
		return json.get(i).isJsonArray();
	}

	public JsonArray getArray(int i) {
		return new JsonArray(json.get(i).getAsJsonArray());
	}

	public JsonArray needArray(int i) {
		if (hasArray(i)) return getArray(i);
		throw exception(ParseExceptionCode.NOT_HAVE, i);
	}

	public void append(JsonArray obj) {
		json.add(obj.getGoogleJson());
	}

	@Override
	public NBTTagList asNBT() {
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
				else if (jp.isNumber()) base = new NBTTagFloat(je.getAsFloat());
			}
			if (base == null) continue;
			if (nbt.getTagType() == 0) nbt.appendTag(base);
			else if (nbt.getTagType() == base.getId()) nbt.appendTag(base);
		}
		return nbt;
	}

	public ArrayList<String> asStringArray() {
		ArrayList<String> list = new ArrayList<>();
		for (int i = 0; i < json.size(); i++) if (hasString(i)) list.add(getString(i));
		return list;
	}
}
