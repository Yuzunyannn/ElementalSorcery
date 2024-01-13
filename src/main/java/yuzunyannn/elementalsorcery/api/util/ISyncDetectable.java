package yuzunyannn.elementalsorcery.api.util;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;

public interface ISyncDetectable<N extends NBTBase> {

	@Nullable
	public N detectChanges(ISyncWatcher watcher);

	public void mergeChanges(N nbt);

}
