package yuzunyannn.elementalsorcery.config;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.util.NBTTag;

public class ConfigGetter implements IConfigGetter {

	private Map<String, Configuration> configs = new HashMap<>();
	private NBTTagCompound syncData = new NBTTagCompound();

	public ConfigGetter() {

	}

	public NBTTagCompound getSyncData() {
		return syncData;
	}

	public void close() {
		for (Configuration config : configs.values()) config.save();
		configs.clear();
	}

	private NBTTagCompound getOrCreateTagCompound(NBTTagCompound nbt, String key) {
		if (nbt.hasKey(key, NBTTag.TAG_COMPOUND)) return nbt.getCompoundTag(key);
		NBTTagCompound n = new NBTTagCompound();
		nbt.setTag(key, n);
		return n;
	}

	private NBTTagCompound getOrCreate(String kind, String group) {
		return getOrCreateTagCompound(getOrCreateTagCompound(syncData, kind), group);
	}

	private boolean sync = false;

	@Override
	public void begin(boolean isSync) {
		sync = isSync;
	}

	@Override
	public void end() {
		sync = false;
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
		boolean value = property.getBoolean();
		if (sync) getOrCreate(kind, group).setBoolean(name, value);
		return value;
	}

	@Override
	public double get(String kind, String group, String name, double def, String note) {
		Configuration config = this.get(kind);
		Property property = config.get(group, name, def, note);
		double value = property.getDouble();
		if (sync) getOrCreate(kind, group).setFloat(name, (float) value);
		return value;
	}

	@Override
	public int get(String kind, String group, String name, int def, String note) {
		Configuration config = this.get(kind);
		Property property = config.get(group, name, def, note);
		int value = property.getInt();
		if (sync) getOrCreate(kind, group).setInteger(name, value);
		return value;
	}

	@Override
	public String get(String kind, String group, String name, String def, String note) {
		Configuration config = this.get(kind);
		Property property = config.get(group, name, def, note);
		String value = property.getString();
		if (sync) getOrCreate(kind, group).setString(name, value);
		return value;
	}

	@Override
	public String[] get(String kind, String group, String name, String[] def, String note) {
		Configuration config = this.get(kind);
		Property property = config.get(group, name, def, note);
		String[] value = property.getStringList();
		if (sync) {
			NBTTagCompound nbt = getOrCreate(kind, group);
			NBTTagList list = new NBTTagList();
			nbt.setTag(name, list);
			for (String v : value) list.appendTag(new NBTTagString(v));
		}
		return value;
	}

	@Override
	public int[] get(String kind, String group, String name, int[] def, String note) {
		Configuration config = this.get(kind);
		Property property = config.get(group, name, def, note);
		int[] value = property.getIntList();
		if (sync) getOrCreate(kind, group).setIntArray(name, value);
		return value;
	}

	@Override
	public double[] get(String kind, String group, String name, double[] def, String note) {
		Configuration config = this.get(kind);
		Property property = config.get(group, name, def, note);
		double[] value = property.getDoubleList();
		if (sync) {
			NBTTagCompound nbt = getOrCreate(kind, group);
			NBTTagList list = new NBTTagList();
			nbt.setTag(name, list);
			for (double v : value) list.appendTag(new NBTTagDouble(v));
		}
		return value;
	}

}
