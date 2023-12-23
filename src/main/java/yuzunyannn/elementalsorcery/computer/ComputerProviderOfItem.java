package yuzunyannn.elementalsorcery.computer;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.computer.soft.EOS;

public class ComputerProviderOfItem implements ICapabilitySerializable<NBTTagCompound> {

	protected Computer computer;

	public ComputerProviderOfItem(ItemStack stack, String appearance) {
		computer = new Computer(appearance);
		Disk disk = new Disk();
		disk.set(EOS.BOOT, new ResourceLocation(ESAPI.MODID, "command").toString());
		computer.addDisk(disk);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return Computer.COMPUTER_CAPABILITY.equals(capability);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (Computer.COMPUTER_CAPABILITY.equals(capability)) return (T) computer;
		return null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return computer.serializeNBT();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		computer.deserializeNBT(nbt);
	}
}
