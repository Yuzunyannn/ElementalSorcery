package yuzunyannn.elementalsorcery.api.computer;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IDeviceInfo {

	String getName();

	@Nonnull
	@SideOnly(Side.CLIENT)
	String getDisplayWorkName();

	@SideOnly(Side.CLIENT)
	public void addInformation(List<String> tooltip);
}
