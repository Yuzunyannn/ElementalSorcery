package yuzunyannn.elementalsorcery.computer.render;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.computer.soft.App;
import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGuiRuntime;

@SideOnly(Side.CLIENT)
public class AppGuiCommon<T extends App> extends SoftGuiCommon<T> {

	public AppGuiCommon(T appInst) {
		super(appInst);
	}

	@Override
	protected void onInit(ISoftGuiRuntime runtime) {
		this.scene.clear();
		this.initStatusBar(runtime);
	}

	protected void initStatusBar(ISoftGuiRuntime runtime) {
		int width = runtime.getWidth();
		bar = new GNodeAppBar(this, width);
		bar.setPosition(0, 0, 100);
		scene.addChild(bar);
	}

}
