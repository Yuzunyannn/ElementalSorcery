package yuzunyannn.elementalsorcery.building;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;

import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class BuildingSaveDataJson extends BuildingSaveData {

	public BuildingSaveDataJson(Building building) {
		super(building, "json", ".json");
	}

	public BuildingSaveDataJson(String key, File file) throws IOException {
		super(key, file);
	}

	@Override
	protected String getKey(Building building) {
		String key = building.getKeyName();
		if (key == null || key.isEmpty()) return BuildingSaveData.randomKeyName(building.getAuthor());
		return key;
	}

	@Override
	public void readDataFromFile() throws IOException {
		JsonObject json = new JsonObject(file);
		this.deserializeNBT(json.asNBT());
	}

	@Override
	public void writeDataToFile() throws IOException {
		JsonObject json = new JsonObject(this.serializeNBT());

		try (FileWriter fileWriter = new FileWriter(this.file)) {
			JsonWriter jsonWriter = new JsonWriter(fileWriter);
			jsonWriter.setIndent("  ");
			Streams.write(json.getGoogleJson(), jsonWriter);
		}

	}

}
