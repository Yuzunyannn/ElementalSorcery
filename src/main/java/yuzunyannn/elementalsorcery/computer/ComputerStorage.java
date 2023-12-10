package yuzunyannn.elementalsorcery.computer;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import yuzunyannn.elementalsorcery.api.computer.IComputer;

public class ComputerStorage implements Capability.IStorage<IComputer> {

	@Override
	public NBTBase writeNBT(Capability<IComputer> capability, IComputer instance, EnumFacing side) {
		return instance.serializeNBT();
	}

	@Override
	public void readNBT(Capability<IComputer> capability, IComputer instance, EnumFacing side, NBTBase nbt) {
		instance.deserializeNBT((NBTTagCompound) nbt);
	}

}
