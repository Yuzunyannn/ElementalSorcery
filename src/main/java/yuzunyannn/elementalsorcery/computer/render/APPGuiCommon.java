package yuzunyannn.elementalsorcery.computer.render;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.computer.soft.APP;
import yuzunyannn.elementalsorcery.api.computer.soft.IAPPGui;
import yuzunyannn.elementalsorcery.api.computer.soft.IAPPGuiRuntime;
import yuzunyannn.elementalsorcery.computer.soft.AppGuiThemePart;
import yuzunyannn.elementalsorcery.nodegui.GScene;
import yuzunyannn.elementalsorcery.util.helper.Color;

@SideOnly(Side.CLIENT)
public class APPGuiCommon implements IAPPGui {

	public static final ResourceLocation TEXTURE_1 = new ResourceLocation(ESAPI.MODID,
			"textures/gui/computer/common_1.png");

	protected final APP appInst;
	protected GScene scene;
	protected GNodeAppBar bar;
	protected boolean isInit = false;
	protected IAPPGuiRuntime runtime;

	public APPGuiCommon(APP appInst) {
		this.appInst = appInst;
		this.scene = new GScene();
	}

	public void init(IAPPGuiRuntime runtime) {
		this.runtime = runtime;
		if (isInit) return;
		isInit = true;
		onInit(runtime);
	}

	public boolean isRootApp() {
		return this.appInst.getPid() == 0;
	}

	public void reinit(IAPPGuiRuntime runtime) {
		onInit(runtime);
	}

	public void onCloseComputer() {
		runtime.sendNotice("power-off");
	}

	public void onCloseCurrAPP() {
		if (this.appInst.getPid() == 0) {
			this.onCloseComputer();
			return;
		}
	}

	protected void onInit(IAPPGuiRuntime runtime) {
		this.scene.clear();
		this.initStatusBar(runtime);
	}

	protected void initStatusBar(IAPPGuiRuntime runtime) {
		int width = runtime.getWidth();
		bar = new GNodeAppBar(this, width);
		scene.addChild(bar);
	}

	public Color getThemeColor(AppGuiThemePart part) {
		switch (part) {
		case BACKGROUND_2:
			return new Color(0xff9ace);
		case OBJECT_2:
			return new Color(0x4c259b);
		case OBJECT_2_ACTIVE:
			return new Color(0x9519a6);
		default:
			break;
		}
		return new Color(0xffffff);
	}

	@Override
	public void update() {
		scene.tick();
	}

	@Override
	public void render(float partialTicks) {
		scene.draw(partialTicks);
	}

	@Override
	public void onMouseEvent(Vec3d vec3d) {
		scene.onMouseEvent(vec3d);
	}

}
