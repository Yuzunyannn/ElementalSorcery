package yuzunyannn.elementalsorcery.api.computer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public interface IComputEnv {

	World getWorld();

	boolean isRemote();

	void sendMessageToClient(IComputerWatcher watcher, NBTTagCompound data);
}
