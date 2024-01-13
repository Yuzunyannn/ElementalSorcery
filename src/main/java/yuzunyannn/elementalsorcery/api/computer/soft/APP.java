package yuzunyannn.elementalsorcery.api.computer.soft;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.ESImpClassRegister;
import yuzunyannn.elementalsorcery.api.util.ESImpClassRegister.EasyImp;
import yuzunyannn.elementalsorcery.api.util.ISyncDetectable;
import yuzunyannn.elementalsorcery.api.util.ISyncWatcher;
import yuzunyannn.elementalsorcery.util.detecter.DataDetectableMonitor;

public class APP extends EasyImp<APP> implements ISyncDetectable<NBTTagCompound>, INBTSerializable<NBTTagCompound> {

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

	public void onDiskChange() {

	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {

	}

	@Override
	public NBTTagCompound serializeNBT() {
		return new NBTTagCompound();
	}

	protected DataDetectableMonitor detecter = new DataDetectableMonitor("inst$app");

	@Override
	public NBTTagCompound detectChanges(ISyncWatcher watcher) {
		return detecter.detectChanges(watcher);
	}

	@Override
	public void mergeChanges(NBTTagCompound nbt) {
		detecter.mergeChanges(nbt);
	}

}
