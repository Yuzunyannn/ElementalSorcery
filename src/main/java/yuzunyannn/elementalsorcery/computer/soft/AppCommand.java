package yuzunyannn.elementalsorcery.computer.soft;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.computer.soft.APP;
import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGui;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;

public class AppCommand extends APP {

	public AppCommand(IOS os, int pid) {
		super(os, pid);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ISoftGui createGUIRender() {
		return new AppCommandGui(this);
	}

}
