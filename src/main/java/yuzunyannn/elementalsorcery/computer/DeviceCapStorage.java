package yuzunyannn.elementalsorcery.computer;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import yuzunyannn.elementalsorcery.api.computer.IDevice;

public class DeviceCapStorage implements Capability.IStorage<IDevice> {

	@Override
	public NBTBase writeNBT(Capability<IDevice> capability, IDevice instance, EnumFacing side) {
		return null;
	}

	@Override
	public void readNBT(Capability<IDevice> capability, IDevice instance, EnumFacing side, NBTBase nbt) {

	}

}
