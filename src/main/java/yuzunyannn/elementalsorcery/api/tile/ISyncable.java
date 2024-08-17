package yuzunyannn.elementalsorcery.api.tile;

import net.minecraft.nbt.NBTBase;

public interface ISyncable {

	public void setSyncDispatcher(ISyncDispatcher dispatcher);

	public void onRecvMessage(NBTBase data);

}
