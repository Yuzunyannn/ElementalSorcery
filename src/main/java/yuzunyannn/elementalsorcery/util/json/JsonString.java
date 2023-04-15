package yuzunyannn.elementalsorcery.util.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;

public class JsonString extends Json {

	public String str = "";

	public JsonString(String str) {
		this.str = str;
	}

	public JsonString(JsonElement str) {
		this.str = str.getAsString();
	}

	@Override
	public NBTBase asNBT() {
		return new NBTTagString(str);
	}

	@Override
	public int size() {
		return str.length();
	}

	@Override
	public JsonElement getGoogleJson() {
		return new JsonPrimitive(str);
	}

}
