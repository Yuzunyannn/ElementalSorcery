package yuzunyannn.elementalsorcery.computer;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.computer.IDeviceInfo;
import yuzunyannn.elementalsorcery.api.util.render.DOItem;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTSS;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;

public abstract class DeviceInfo implements IDeviceInfo, INBTSS {

	protected String name = "";
	protected String manufacturer;
	protected ItemStack icon = ItemStack.EMPTY;

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public ItemStack getIcon() {
		return icon;
	}

	public void setIcon(ItemStack icon) {
		this.icon = icon;
	}

	@Override
	public void readSaveData(INBTReader reader) {
		name = reader.string("name");
	}

	@Override
	public void writeSaveData(INBTWriter writer) {
		writer.write("name", name);
	}

	@Override
	public void readUpdateData(INBTReader reader) {
		name = reader.string("name");
	}

	@Override
	public void writeUpdateData(INBTWriter writer) {
		writer.write("name", name);
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(List<Object> tooltip) {
		if (manufacturer != null) tooltip.add(I18n.format("es.app.manufacturer") + manufacturer);
		if (!icon.isEmpty()) tooltip.add(new Object[] { "⭐", new DOItem(icon).setScale(0.5f), "⭐" });
	}

}
