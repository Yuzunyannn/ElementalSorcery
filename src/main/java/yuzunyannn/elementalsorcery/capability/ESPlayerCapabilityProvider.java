package yuzunyannn.elementalsorcery.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import yuzunyannn.elementalsorcery.api.entity.IFairyCubeMaster;
import yuzunyannn.elementalsorcery.elf.quest.IAdventurer;

public class ESPlayerCapabilityProvider implements ICapabilitySerializable<NBTTagCompound> {

	private IAdventurer adventurer;
	private IFairyCubeMaster master;

	public ESPlayerCapabilityProvider() {
		adventurer = new Adventurer();
		master = new FairyCubeMaster();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return Adventurer.ADVENTURER_CAPABILITY.equals(capability)
				|| FairyCubeMaster.FAIRY_CUBE_MASTER_CAPABILITY.equals(capability);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (Adventurer.ADVENTURER_CAPABILITY.equals(capability)) return (T) adventurer;
		if (FairyCubeMaster.FAIRY_CUBE_MASTER_CAPABILITY.equals(capability)) return (T) master;
		return null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = (NBTTagCompound) CapabilityProvider.AdventurerProvider.storage
				.writeNBT(Adventurer.ADVENTURER_CAPABILITY, adventurer, null);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		CapabilityProvider.AdventurerProvider.storage.readNBT(Adventurer.ADVENTURER_CAPABILITY, adventurer, null, nbt);
	}

}
