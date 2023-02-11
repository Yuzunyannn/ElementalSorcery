package yuzunyannn.elementalsorcery.building;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.util.TextHelper;

public class BuildingInherent extends Building {

	final String unlocalizedName;

	public BuildingInherent(NBTTagCompound nbt, String unlocalizedName) {
		super(nbt);
		this.unlocalizedName = "arc." + unlocalizedName + ".name";
		this.name = TextHelper.castToCapital(getKeyName());
	}

	@Override
	public String getName() {
		if (I18n.hasKey(unlocalizedName)) return I18n.format(unlocalizedName);
		return super.getName();
	}
}
