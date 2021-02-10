package yuzunyannn.elementalsorcery.config;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import yuzunyannn.elementalsorcery.ElementalSorcery;

public class ConfigGetter implements IConfigGetter {

	private Map<String, Configuration> configs = new HashMap<>();

	public ConfigGetter() {

	}

	public void close() {
		for (Configuration config : configs.values()) config.save();
		configs.clear();
	}

	private Configuration get(String kind) {
		if (configs.containsKey(kind)) return configs.get(kind);
		Configuration config = new Configuration(ElementalSorcery.data.getFile("config", kind + ".cfg"));
		configs.put(kind, config);
		return config;
	}

	@Override
	public boolean get(String kind, String group, String name, boolean def, String note) {
		Configuration config = this.get(kind);
		Property property = config.get(group, name, def, note);
		return property.getBoolean();
	}

	@Override
	public double get(String kind, String group, String name, double def, String note) {
		Configuration config = this.get(kind);
		Property property = config.get(group, name, def, note);
		return property.getDouble();
	}

	@Override
	public int get(String kind, String group, String name, int def, String note) {
		Configuration config = this.get(kind);
		Property property = config.get(group, name, def, note);
		return property.getInt();
	}

	@Override
	public String get(String kind, String group, String name, String def, String note) {
		Configuration config = this.get(kind);
		Property property = config.get(group, name, def, note);
		return property.getString();
	}

	@Override
	public String[] get(String kind, String group, String name, String[] def, String note) {
		Configuration config = this.get(kind);
		Property property = config.get(group, name, def, note);
		return property.getStringList();
	}

	@Override
	public int[] get(String kind, String group, String name, int[] def, String note) {
		Configuration config = this.get(kind);
		Property property = config.get(group, name, def, note);
		return property.getIntList();
	}

	@Override
	public double[] get(String kind, String group, String name, double[] def, String note) {
		Configuration config = this.get(kind);
		Property property = config.get(group, name, def, note);
		return property.getDoubleList();
	}

}
