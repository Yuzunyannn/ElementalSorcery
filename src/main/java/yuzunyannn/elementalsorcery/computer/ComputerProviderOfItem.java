package yuzunyannn.elementalsorcery.computer;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class ComputerProviderOfItem implements ICapabilitySerializable<NBTTagCompound> {

	protected ComputerDevice computer;

	public ComputerProviderOfItem(ItemStack stack, String appearance) {
		computer = new ComputerDevice(appearance, stack);
	}

	public ComputerProviderOfItem(ItemStack stack, ComputerDevice computer) {
		this.computer = computer;
	}

	public Computer getComputer() {
		return computer;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return Computer.COMPUTER_CAPABILITY.equals(capability) || Computer.DEVICE_CAPABILITY.equals(capability);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (Computer.COMPUTER_CAPABILITY.equals(capability)) return (T) computer;
		if (Computer.DEVICE_CAPABILITY.equals(capability)) return (T) computer.device();
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
