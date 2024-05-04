package yuzunyannn.elementalsorcery.item.tool;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import yuzunyannn.elementalsorcery.computer.Computer;
import yuzunyannn.elementalsorcery.computer.ComputerDevice;
import yuzunyannn.elementalsorcery.computer.ComputerProviderOfItem;
import yuzunyannn.elementalsorcery.computer.Disk;
import yuzunyannn.elementalsorcery.computer.soft.EOS;
import yuzunyannn.elementalsorcery.computer.softs.AppCommand;

public class ItemCloverPad extends ItemPad {

	public ItemCloverPad() {
		this.setTranslationKey("cloverPad");
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		ComputerProviderOfItem provider = new ComputerProviderOfItem(stack, new ComputerDevice("cloverPad", stack));
		Computer computer = provider.getComputer();
		Disk disk = new Disk();
		disk.set(EOS.BOOT, AppCommand.ID.toString());
		computer.addDisk(disk);
		return provider;
	}

}
