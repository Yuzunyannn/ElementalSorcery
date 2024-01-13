package yuzunyannn.elementalsorcery.api.util;

import net.minecraft.nbt.NBTTagCompound;

public interface ISyncDetectableMonitor extends ISyncDetectable<NBTTagCompound> {

	public void add(String key, ISyncDetectable<?> detectable);

	void remove(String key);

	void markDirty(String key);

}
