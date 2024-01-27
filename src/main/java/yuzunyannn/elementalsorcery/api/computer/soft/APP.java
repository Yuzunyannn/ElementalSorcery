package yuzunyannn.elementalsorcery.api.computer.soft;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.ESImpClassRegister;
import yuzunyannn.elementalsorcery.api.util.ESImpClassRegister.EasyImp;
import yuzunyannn.elementalsorcery.api.util.detecter.DataDetectableMonitor;
import yuzunyannn.elementalsorcery.api.util.detecter.ISyncWatcher;

public class APP extends EasyImp<APP> implements ISoft, INBTSerializable<NBTTagCompound> {

	public static final ESImpClassRegister<APP> REGISTRY = new ESImpClassRegister();

	private final int pid;
	private final IOS os;
	private boolean isTask;

	public APP(IOS os, int pid) {
		this.pid = pid;
		this.os = os;
	}

	public int getPid() {
		return pid;
	}

	@Override
	public IOS getOS() {
		return os;
	}

	public ResourceLocation getAppId() {
		return getRegistryName();
	}

	public void bindDevice(UUID uuid) {

	}

	public void setTask(boolean isTask) {
		this.isTask = isTask;
	}

	public boolean isTask() {
		return isTask;
	}

	public void onDiskChange() {

	}

	@Override
	public void handleOperation(NBTTagCompound nbt) {

	}

	@SideOnly(Side.CLIENT)
	public void onRecvMessage(NBTTagCompound nbt) {

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
