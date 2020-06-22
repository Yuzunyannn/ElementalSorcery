package yuzunyannn.elementalsorcery.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

public class JsonHelper {

	static public JsonObject parserJosn(ResourceLocation path) {
		IResourceManager rm = Minecraft.getMinecraft().getResourceManager();
		IResource r = null;
		JsonObject obj = null;
		try {
			r = rm.getResource(path);
			Gson gson = new Gson();
			obj = gson.fromJson(new InputStreamReader(r.getInputStream()), JsonObject.class);
		} catch (IOException e) {} finally {
			IOUtils.closeQuietly(r);
		}
		return obj;
	}

	static public NBTTagCompound jsonToNBT(JsonObject json) {
		NBTTagCompound nbt = new NBTTagCompound();
		for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
			String key = entry.getKey();
			JsonElement je = entry.getValue();
			if (je.isJsonObject()) nbt.setTag(key, jsonToNBT(json));
			else if (je.isJsonArray()) nbt.setTag(key, jsonToNBT(je.getAsJsonArray()));
			else {
				Class<?> cls = je.getClass();
				if (Number.class.isAssignableFrom(cls)) {
					if (Double.class.equals(cls)) nbt.setFloat(key, je.getAsFloat());
					else nbt.setInteger(key, je.getAsInt());
				} else if (String.class.equals(cls)) nbt.setString(key, je.getAsString());
			}
		}
		return nbt;
	}

	static public NBTTagList jsonToNBT(JsonArray json) {
		NBTTagList nbt = new NBTTagList();

		return nbt;
	}

}
