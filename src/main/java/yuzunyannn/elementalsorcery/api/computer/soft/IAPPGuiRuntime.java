package yuzunyannn.elementalsorcery.api.computer.soft;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IAPPGuiRuntime {

	int getWidth();

	int getHeight();

	int getDisplayWidth();

	int getDisplayHeight();

	void sendOperation(NBTTagCompound nbt);

	void sendNotice(String str);

}
