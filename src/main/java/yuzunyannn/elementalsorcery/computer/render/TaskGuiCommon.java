package yuzunyannn.elementalsorcery.computer.render;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.computer.soft.APP;
import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGuiRuntime;

@SideOnly(Side.CLIENT)
public class TaskGuiCommon extends SoftGuiCommon {

	public TaskGuiCommon(APP appInst) {
		super(appInst);
	}

	protected void onInit(ISoftGuiRuntime runtime) {
		this.scene.clear();
	}

}
