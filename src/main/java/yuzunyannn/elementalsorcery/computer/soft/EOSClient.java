package yuzunyannn.elementalsorcery.computer.soft;

import java.util.Collections;
import java.util.List;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.computer.IDisk;
import yuzunyannn.elementalsorcery.api.computer.soft.APP;
import yuzunyannn.elementalsorcery.api.computer.soft.AppDiskType;
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
	public List<IDisk> getDisks() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public IDeviceStorage getDisk(APP app, AppDiskType type) {
		throw new ComputerHardwareMissingException(this.computer, "disk is in the cloud");
	}

	@Override
	public APP getAppInst(int pid) {
		return processTree.getAppCache(this, pid);
	}

}
