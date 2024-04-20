package yuzunyannn.elementalsorcery.computer;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.computer.IDeviceInfo;

public class DeviceInfo implements IDeviceInfo, INBTSerializable<NBTTagCompound> {

	protected String name = "";
	protected String manufacturer;

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("name", name);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		name = nbt.getString("name");
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getDisplayWorkName() {
		return "";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(List<String> tooltip) {
		if (manufacturer != null) tooltip.add(I18n.format("es.app.manufacturer") + manufacturer);
	}

}
