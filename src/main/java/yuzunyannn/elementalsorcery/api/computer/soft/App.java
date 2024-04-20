package yuzunyannn.elementalsorcery.api.computer.soft;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.ESImpClassRegister;
import yuzunyannn.elementalsorcery.api.util.ESImpClassRegister.EasyImp;
import yuzunyannn.elementalsorcery.api.util.detecter.ISyncDetectable;
import yuzunyannn.elementalsorcery.api.util.detecter.ISyncWatcher;

public class App extends EasyImp<App> implements ISyncDetectable<NBTTagCompound>, INBTSerializable<NBTTagCompound> {

	public static final ESImpClassRegister<App> REGISTRY = new ESImpClassRegister();

	private final int pid;
	private final IOS os;
	private boolean isTask;
	private boolean closing;

	public App(IOS os, int pid) {
		this.pid = pid;
		this.os = os;
	}

	public final int getPid() {
		return pid;
	}

	public final IOS getOS() {
		return os;
	}

	public final ResourceLocation getAppId() {
		return getRegistryName();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {

	}

	@Override
	public NBTTagCompound serializeNBT() {
		return new NBTTagCompound();
	}

	public void setTask(boolean isTask) {
		this.isTask = isTask;
	}

	public boolean isTask() {
		return isTask;
	}

	public void bindDevice(UUID uuid) {

	}

	public void onDiskChange() {

	}

	public void handleOperation(NBTTagCompound nbt) {

	}

	@SideOnly(Side.CLIENT)
	public void onRecvMessage(NBTTagCompound nbt) {

	}

	public void onStartup() {
	}

	public void onUpdate() {
	}

	public void onAbort() {
	}

	public void onExit() {
	}

	@SideOnly(Side.CLIENT)
	public ISoftGui createGUIRender() {
		return null;
	}

	@Override
	public NBTTagCompound detectChanges(ISyncWatcher watcher) {
		return null;
	}

	@Override
	public void mergeChanges(NBTTagCompound nbt) {

	}

	public boolean isClosing() {
		return closing;
	}

	public void exit() {
		this.closing = true;
	}

}
