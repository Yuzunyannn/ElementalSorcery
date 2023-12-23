package yuzunyannn.elementalsorcery.api.computer.soft;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.ESImpClassRegister;
import yuzunyannn.elementalsorcery.api.util.ESImpClassRegister.EasyImp;

public class APP extends EasyImp<APP> {

	public static final ESImpClassRegister<APP> REGISTRY = new ESImpClassRegister();

	private final int pid;
	private final IOS os;

	public APP(IOS os, int pid) {
		this.pid = pid;
		this.os = os;
	}

	public int getPid() {
		return pid;
	}

	public IOS getOS() {
		return os;
	}

	public ResourceLocation getAppId() {
		return getRegistryName();
	}

	@SideOnly(Side.CLIENT)
	public IAPPGui createGUIRender() {
		return null;
	}

	public void handleOperation(NBTTagCompound nbt) {

	}

	public void onStartup() {

	}

	public void onUpdate() {

	}

	public void onExit() {

	}

}
