package yuzunyannn.elementalsorcery.computer.soft;

import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.computer.DNNBTParams;
import yuzunyannn.elementalsorcery.api.computer.DNRequest;
import yuzunyannn.elementalsorcery.api.computer.soft.App;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.util.detecter.DataDetectableMonitor;
import yuzunyannn.elementalsorcery.api.util.detecter.ISyncWatcher;
import yuzunyannn.elementalsorcery.computer.softs.TaskNetwork;
import yuzunyannn.elementalsorcery.tile.device.DeviceFeature;
import yuzunyannn.elementalsorcery.tile.device.DeviceFeatureMap;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;
import yuzunyannn.elementalsorcery.util.helper.NBTSaver;

public class AppBase extends App {

	final DeviceFeatureMap feature;

	public AppBase(IOS os, int pid) {
		super(os, pid);
		this.feature = DeviceFeatureMap.getOrCreate(getClass());
	}

	@Override
	final public NBTTagCompound serializeNBT() {
		NBTSaver saver = new NBTSaver(super.serializeNBT());
		writeSaveData(saver);
		return saver.tag();
	}

	@Override
	final public void deserializeNBT(NBTTagCompound nbt) {
		super.deserializeNBT(nbt);
		readSaveData(new NBTSaver(nbt));
	}

	@Override
	public void handleOperation(NBTTagCompound nbt) {
		if (nbt.hasKey(":m")) {
			String method = nbt.getString(":m");
			DNRequest params = new DNNBTParams(nbt);
			this.feature.invoke(this, method, params);
		}
	}

	@DeviceFeature(id = "link-task")
	public void openLinkUI() {
		getOS().exec(this, TaskNetwork.ID);
	}

	public void writeSaveData(INBTWriter writer) {

	}

	public void readSaveData(INBTReader reader) {

	}

	@Override
	public void onUpdate() {
		super.onUpdate();
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
