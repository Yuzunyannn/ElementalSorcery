package yuzunyannn.elementalsorcery.config;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import yuzunyannn.elementalsorcery.util.NBTTag;

public class ConfigNBTGetter implements IConfigGetter {

	private NBTTagCompound configNBT;

	public ConfigNBTGetter(NBTTagCompound data) {
		this.configNBT = data;
	}

	private NBTTagCompound get(String kind, String group) {
		return configNBT.getCompoundTag(kind).getCompoundTag(group);
	}

	@Override
	public boolean get(String kind, String group, String name, boolean def, String note) {
		NBTTagCompound config = this.get(kind, group);
		return config.hasKey(name, NBTTag.TAG_NUMBER) ? config.getBoolean(name) : def;
	}

	@Override
	public double get(String kind, String group, String name, double def, String note) {
		NBTTagCompound config = this.get(kind, group);
		return config.hasKey(name, NBTTag.TAG_NUMBER) ? config.getDouble(name) : def;
	}

	@Override
	public int get(String kind, String group, String name, int def, String note) {
		NBTTagCompound config = this.get(kind, group);
		return config.hasKey(name, NBTTag.TAG_NUMBER) ? config.getInteger(name) : def;
	}

	@Override
	public String get(String kind, String group, String name, String def, String note) {
		NBTTagCompound config = this.get(kind, group);
		return config.hasKey(name, NBTTag.TAG_STRING) ? config.getString(name) : def;
	}

	@Override
	public String[] get(String kind, String group, String name, String[] def, String note) {
		NBTTagCompound config = this.get(kind, group);
		if (config.hasKey(name, NBTTag.TAG_LIST)) {
			NBTTagList list = config.getTagList(name, NBTTag.TAG_SHORT);
			String[] value = new String[list.tagCount()];
			for (int i = 0; i < value.length; i++) value[i] = list.getStringTagAt(i);
			return value;
		}
		return def;
	}

	@Override
	public int[] get(String kind, String group, String name, int[] def, String note) {
		NBTTagCompound config = this.get(kind, group);
		return config.hasKey(name, NBTTag.TAG_INT_ARRAY) ? config.getIntArray(name) : def;
	}

	@Override
	public double[] get(String kind, String group, String name, double[] def, String note) {
		NBTTagCompound config = this.get(kind, group);
		if (config.hasKey(name, NBTTag.TAG_LIST)) {
			NBTTagList list = config.getTagList(name, NBTTag.TAG_DOUBLE);
			double[] value = new double[list.tagCount()];
			for (int i = 0; i < value.length; i++) value[i] = list.getDoubleAt(i);
			return value;
		}
		return def;
	}

}
