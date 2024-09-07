package yuzunyannn.elementalsorcery.tile.device;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.computer.DeviceInfoTile;
import yuzunyannn.elementalsorcery.util.element.ElementInventoryStronger;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;

public class TileElementTerminal extends TileDevice {

	protected ElementInventoryStronger inventory = ElementInventory.sensor(new ElementInventoryStronger(), this);

	public TileElementTerminal() {
		DeviceInfoTile info = (DeviceInfoTile) device.getInfo();
		info.setIcon(new ItemStack(ESObjects.BLOCKS.ELEMENT_TERMINAL));
	}

	@Override
	public void writeSaveData(INBTWriter writer) {
		super.writeSaveData(writer);
	}

	@Override
	public void readSaveData(INBTReader reader) {
		super.readSaveData(reader);
	}

	@Override
	public void writeUpdateData(INBTWriter writer) {
		super.writeUpdateData(writer);
	}

	@Override
	public void readUpdateData(INBTReader reader) {
		super.readUpdateData(reader);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (ElementInventory.ELEMENTINVENTORY_CAPABILITY == capability) return true;
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (ElementInventory.ELEMENTINVENTORY_CAPABILITY == capability) return (T) inventory;
		return super.getCapability(capability, facing);
	}

	@SideOnly(Side.CLIENT)
	public void updateAnimation() {

	}

}
