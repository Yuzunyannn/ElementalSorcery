package yuzunyannn.elementalsorcery.api.computer.soft;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IDeviceShell extends INBTSerializable<NBTTagCompound> {

	boolean isEmpty();

	void invoke(IDeviceShellExecutor executor) throws DeviceShellBadInvoke;

	void setArgs(Object... args);

}
