package yuzunyan.elementalsorcery.building;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;

public class BuildingInherent extends Building {

	final String unlocalizedName;

	public BuildingInherent(NBTTagCompound nbt, String unlocalizedName) {
		super(nbt);
		this.unlocalizedName = "arc." + unlocalizedName + ".name";
	}

	@Override
	public String getName() {
		return I18n.format(this.unlocalizedName);
	}
}
