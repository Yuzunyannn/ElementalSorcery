package yuzunyannn.elementalsorcery.computer.soft;

import java.util.Collection;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.soft.App;

@SideOnly(Side.CLIENT)
public class EOSClient extends EOS {

	public int topTaskPid = -1;

	public EOSClient(IComputer computer) {
		super(computer);
		processTree.processChangeCallback = () -> onProcessChange();
	}

	@Override
	public boolean isRemote() {
		return true;
	}

	@Override
	public void markDirty(App app) {
	}

	@Override
	public void markDirty() {
	}

	@Override
	public void onDiskChange(boolean onlyData) {
		if (onlyData) return;
//		disksCache = null;
//		appDiskCacheMap.clear();
	}

//	@Override
//	public List<IDisk> getDisks() {
//		return Collections.EMPTY_LIST;
//	}

//	@Override
//	public IDeviceStorage getDisk(App app, AppDiskType type) {
//		throw new ComputerHardwareMissingException(this.computer, "disk is in the cloud");
//	}

	@Override
	public App getAppInst(int pid) {
		return processTree.getAppCache(this, pid);
	}

	@Override
	public int getTopTask() {
		return topTaskPid;
	}

	public void onProcessChange() {
		topTaskPid = -1;
		int currPid = processTree.getForeground();
		while (currPid >= 0) {
			Collection<Integer> children = processTree.getChildren(currPid);
			int pid = checkHasTask(children);
			if (pid != -1) {
				topTaskPid = pid;
				break;
			}
			int newPid = processTree.getParent(currPid);
			if (currPid == newPid) break;
			currPid = newPid;
		}
	}

	protected int checkHasTask(Collection<Integer> children) {
		if (children == null) return -1;
		for (int pid : children) {
			App app = processTree.getAppCache(this, pid);
			if (app == null) continue;
			if (app.isTask()) return pid;
		}
		return -1;
	}

	@Override
	public void message(App app, NBTTagCompound nbt) {
		super.message(app, nbt);
	}

}
