package yuzunyannn.elementalsorcery.api.computer.soft;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.detecter.ISyncDetectable;

public interface ISoft extends ISyncDetectable<NBTTagCompound> {

	public IOS getOS();

	default public void handleOperation(NBTTagCompound nbt) {

	}

	default public void onStartup() {

	}

	default public void onUpdate() {

	}

	default public void onAbort() {

	}

	default public void onExit() {

	}

	@SideOnly(Side.CLIENT)
	default public ISoftGui createGUIRender() {
		return null;
	}

}
