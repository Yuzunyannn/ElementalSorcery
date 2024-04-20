package yuzunyannn.elementalsorcery.computer.render;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.computer.soft.App;
import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGuiRuntime;
import yuzunyannn.elementalsorcery.nodegui.GImage;
import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.util.helper.Color;

@SideOnly(Side.CLIENT)
public class TaskGuiCommon<T extends App> extends SoftGuiCommon<T> {

	protected double cWidth, cHeight;
	protected GNode bg;

	public TaskGuiCommon(T appInst) {
		super(appInst);
	}

	protected void onInit(ISoftGuiRuntime runtime) {
		this.scene.clear();
		cWidth = (int) (runtime.getWidth() * 0.975);
		cHeight = (int) (runtime.getHeight() * 0.975);

		this.initBG();
	}

	protected void initBG() {
		Color color = this.getThemeColor(SoftGuiThemePart.BACKGROUND_1);
		GImage bg = new GImage(SoftGuiCommon.TEXTURE_1, FRAME_P1);
		bg.setColorRef(color);
		bg.setSplit9();
		bg.setSize(cWidth, cHeight);
		bg.setPosition((runtime.getWidth() - cWidth) / 2, (runtime.getHeight() - cHeight) / 2, 0);
		scene.addChild(this.bg = bg);
	}

}
