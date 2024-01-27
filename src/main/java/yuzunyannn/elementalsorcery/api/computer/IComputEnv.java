package yuzunyannn.elementalsorcery.api.computer;

import net.minecraft.nbt.NBTTagCompound;

public interface IComputEnv extends IDeviceEnv {

	void sendMessageToClient(IComputerWatcher watcher, NBTTagCompound data);
}
