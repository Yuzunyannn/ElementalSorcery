package yuzunyannn.elementalsorcery.tile.device;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.IItemHandlerModifiable;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.computer.IDisk;
import yuzunyannn.elementalsorcery.computer.Computer;
import yuzunyannn.elementalsorcery.computer.ComputerEnvTile;
import yuzunyannn.elementalsorcery.computer.DiskItem;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;
import yuzunyannn.elementalsorcery.util.helper.NBTSender;
import yuzunyannn.elementalsorcery.util.item.ItemStackHandlerLinker;

public abstract class TileComputer extends TileDevice implements ITickable {

	protected ComputerTile computer;
	protected ComputerEnvTile myEnv;
	protected boolean isComputerPowerOn = false;
	protected ItemStackHandlerLinker linkerHandler = new ItemStackHandlerLinker();

	public TileComputer() {
		this.computer = new ComputerTile(this);
		this.device.addFeature(this.computer);
		this.device.setEnv(myEnv = new ComputerEnvTile(this, this.computer));
		this.linkerHandler.addLinker(() -> {
			List<IDisk> list = computer.getDisks();
			if (list.isEmpty()) return ItemStack.EMPTY;
			DiskItem disk = (DiskItem) list.get(0);
			return disk.getItemStack();
		}, itemStack -> {
			List<IDisk> list = computer.getDisks();
			if (list.isEmpty()) {
				if (itemStack.isEmpty()) return;
				computer.addDisk(new DiskItem(itemStack));
			} else {
				if (itemStack.isEmpty()) computer.removeDisk(0);
				else computer.setDisk(0, new DiskItem(itemStack));
			}
		}, itemStack -> itemStack.getItem() == ESObjects.ITEMS.DISK);
	}

	public abstract String getAppearance();

	public IItemHandlerModifiable getEditorItemHandler() {
		return linkerHandler;
	}

	public ComputerTile getComputer() {
		return computer;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (Computer.COMPUTER_CAPABILITY.equals(capability)) return (T) computer;
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (Computer.COMPUTER_CAPABILITY.equals(capability)) return true;
		return super.hasCapability(capability, facing);
	}

	@Override
	public void update() {
		super.update();
		computer.update();
		if (world.isRemote) return;
		if (ticksExisted % 10 == 0) {
			if (isComputerPowerOn != computer.isPowerOn()) {
				NBTSender.SHARE.write("pf", isComputerPowerOn = computer.isPowerOn());
				this.updateToClient(NBTSender.SHARE.spitOut());
			}
		}
	}

	public boolean isPowerOn() {
		if (world.isRemote) return isComputerPowerOn;
		return computer.isPowerOn();
	}

	@Override
	public void readSaveData(INBTReader reader) {
		super.readSaveData(reader);
		computer = reader.obj("_PC_", computer);
	}

	@Override
	public void writeSaveData(INBTWriter writer) {
		super.writeSaveData(writer);
		writer.write("_PC_", computer);
	}

	@Override
	public void readUpdateData(INBTReader reader) {
		super.readUpdateData(reader);
		isComputerPowerOn = reader.nboolean("pf");
	}

	@Override
	public void writeUpdateData(INBTWriter writer) {
		super.writeUpdateData(writer);
		writer.write("pf", isComputerPowerOn);
	}

	@Override
	public void recvUpdateData(INBTReader reader) {
		super.recvUpdateData(reader);
		if (reader.has("pf")) isComputerPowerOn = reader.nboolean("pf");
	}

}
