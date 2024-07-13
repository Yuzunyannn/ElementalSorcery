package yuzunyannn.elementalsorcery.util.helper;

import net.minecraft.nbt.NBTTagCompound;

public interface INBTSSA extends INBTSS {

	@Override
	public void writeSaveData(INBTWriter writer);

	@Override
	public void readSaveData(INBTReader reader);

	@Override
	default public void writeUpdateData(INBTWriter writer) {
		writeSaveData(writer);
	}

	@Override
	default public void readUpdateData(INBTReader reader) {
		readSaveData(reader);
	}

	@Override
	default void deserializeNBT(NBTTagCompound nbt) {
		readSaveData(new NBTSender(nbt));
	}

	@Override
	default NBTTagCompound serializeNBT() {
		NBTSender saver = new NBTSender();
		writeSaveData(saver);
		return saver.tag();
	}

}
