package yuzunyannn.elementalsorcery.api.util.detecter;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;

public interface IDataDetectable<T, N extends NBTBase> {

	@Nullable
	N detectChanges(IDataRef<T> templateRef);

	void mergeChanges(N nbt);
}
