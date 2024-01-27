package yuzunyannn.elementalsorcery.api.util.detecter;

import net.minecraft.nbt.NBTTagCompound;

public interface IDataDetectableMonitor extends ISyncDetectable<NBTTagCompound> {

	public void add(String key, IDataDetectable unit);

	void remove(String key);

	void markDirty(String key);

}
