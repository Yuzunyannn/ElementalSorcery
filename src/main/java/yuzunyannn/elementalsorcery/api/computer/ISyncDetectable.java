package yuzunyannn.elementalsorcery.api.computer;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;

public interface ISyncDetectable {

	@Nullable
	public NBTTagCompound detectChanges(ISyncWatcher watcher);

	public void mergeChanges(NBTTagCompound nbt);

}
