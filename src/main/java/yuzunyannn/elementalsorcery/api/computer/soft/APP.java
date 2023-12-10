package yuzunyannn.elementalsorcery.api.computer.soft;

import net.minecraft.util.ResourceLocation;
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

	public ResourceLocation getAppId() {
		return getRegistryName();
	}

}
