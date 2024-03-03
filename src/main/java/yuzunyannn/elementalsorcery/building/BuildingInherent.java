package yuzunyannn.elementalsorcery.building;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.util.TextHelper;

public class BuildingInherent extends Building {

	final String unlocalizedName;

	public BuildingInherent(NBTTagCompound nbt, String unlocalizedName) {
		super(nbt);
		this.unlocalizedName = "arc." + unlocalizedName + ".name";
		this.name = TextHelper.castToCapital(getKeyName());
	}

	public String getTanslateName() {
		return unlocalizedName;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getDisplayName() {
		if (I18n.hasKey(unlocalizedName)) return I18n.format(unlocalizedName);
		return super.getName();
	}
}
