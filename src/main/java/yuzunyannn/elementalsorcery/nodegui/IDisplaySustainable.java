package yuzunyannn.elementalsorcery.nodegui;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.IAliveStatusable;
import yuzunyannn.elementalsorcery.api.util.ICastable;
import yuzunyannn.elementalsorcery.api.util.detecter.IDataDetectable;

public interface IDisplaySustainable<T, U extends NBTBase> extends IAliveStatusable, IDataDetectable<T, U> {

	void updateServer(ICastable env);

	void setDead(Side side);
	
	void abandon();

	@SideOnly(Side.CLIENT)
	default void afterChangesMerged(U nbt) {
	}

	@Nullable
	default String digest() {
		return null;
	}

}
