package yuzunyannn.elementalsorcery.computer.soft;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.computer.IDisk;
import yuzunyannn.elementalsorcery.api.computer.IMemory;
import yuzunyannn.elementalsorcery.api.computer.IStorageMonitor;
import yuzunyannn.elementalsorcery.api.computer.soft.APP;
import yuzunyannn.elementalsorcery.api.computer.soft.AppDiskType;
import yuzunyannn.elementalsorcery.api.computer.soft.IComputerException;
import yuzunyannn.elementalsorcery.computer.exception.ComputerHardwareMissingException;

@SideOnly(Side.CLIENT)
public class EOSClient extends EOS {

	public EOSClient(IComputer computer) {
		super(computer);
	}

	@Override
	public boolean isClient() {
		return true;
	}

	@Override
	public IMemory getMemory() {
		IMemory memory = computer.getMemory();
		if (memory == null) throw new ComputerHardwareMissingException(this.computer, "memory is missing");
		return memory;
	}

	@Override
	public List<IDisk> getDisks() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public IDeviceStorage getDisk(APP app, AppDiskType type) {
		throw new ComputerHardwareMissingException(this.computer, "disk is in the cloud");
	}

	@Override
	public IStorageMonitor getMemoryMonitor() {
		return null;
	}

	@Override
	public APP getAppInst(int pid) {
		IMemory memory = this.getMemory();
		return memory.get(PROCESS).getAppCache(this, pid);
	}

	@Override
	public void onStorageSync(IDeviceStorage storage, List<String[]> changes) {
		if (storage != computer.getMemory()) return;
		this.onMemoryChange();
		Set<String> keySet = new TreeSet<>();
		for (String[] strs : changes) {
			try {
				String str = strs[0];
				if (str.charAt(0) != '>') continue;
				if (str.charAt(1) != '#') continue;
				if (keySet.contains(str)) continue;
				keySet.add(str);
				str = str.substring(2);
				int pid = Integer.parseInt(str);
				APP app = getAppInst(pid);
				if (app == null) continue;
				app.onMemoryChange();
			} catch (RuntimeException e) {
				if (e instanceof IComputerException) {

				} else {
					if (ESAPI.isDevelop) ESAPI.logger.warn("咋回事？", e);
				}
			}

		}
	}
}
