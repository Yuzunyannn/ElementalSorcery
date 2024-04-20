package yuzunyannn.elementalsorcery.computer.softs;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGui;
import yuzunyannn.elementalsorcery.computer.soft.AppBase;

public class AppCommand extends AppBase {

	public AppCommand(IOS os, int pid) {
		super(os, pid);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ISoftGui createGUIRender() {
		return new AppCommandGui(this);
	}

}
