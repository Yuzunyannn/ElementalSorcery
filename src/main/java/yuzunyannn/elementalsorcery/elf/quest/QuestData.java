package yuzunyannn.elementalsorcery.elf.quest;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public class QuestData implements INBTSerializable<NBTTagCompound> {

	protected NBTTagCompound data;

	public QuestData() {
		this(new NBTTagCompound());
	}

	public QuestData(NBTTagCompound nbt) {
		data = nbt == null ? new NBTTagCompound() : nbt;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return data;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		data = nbt;
	}

	public NBTTagCompound getNBT() {
		return data;
	}

	public boolean isEmpty() {
		return data.hasNoTags();
	}

	public int getInteger(String key) {
		return data.getInteger(key);
	}

	public void setInteger(String key, int value) {
		data.setInteger(key, value);
	}

	public void growInteger(String key, int value) {
		data.setInteger(key, data.getInteger(key) + value);
	}

}
