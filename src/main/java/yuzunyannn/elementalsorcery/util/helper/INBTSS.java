package yuzunyannn.elementalsorcery.util.helper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface INBTSS extends INBTSerializable<NBTTagCompound> {
	default public void writeSaveData(INBTWriter writer) {
	}

	default public void readSaveData(INBTReader reader) {
	}

	default public void writeUpdateData(INBTWriter writer) {
	}

	default public void readUpdateData(INBTReader reader) {
	}

	@Override
	default void deserializeNBT(NBTTagCompound nbt) {
		readSaveData(new NBTSaver(nbt));
	}

	@Override
	default NBTTagCompound serializeNBT() {
		NBTSaver saver = new NBTSaver();
		writeSaveData(saver);
		return saver.tag();
	}
}
