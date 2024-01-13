package yuzunyannn.elementalsorcery.computer.render;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.computer.soft.APP;
import yuzunyannn.elementalsorcery.api.computer.soft.IAPPGui;
import yuzunyannn.elementalsorcery.api.computer.soft.IAPPGuiRuntime;
import yuzunyannn.elementalsorcery.api.util.client.RenderTexutreFrame;
import yuzunyannn.elementalsorcery.nodegui.GScene;
import yuzunyannn.elementalsorcery.util.helper.Color;

@SideOnly(Side.CLIENT)
public class APPGuiCommon implements IAPPGui {

	public static final ResourceLocation TEXTURE_1 = new ResourceLocation(ESAPI.MODID,
			"textures/gui/computer/common_1.png");
	public static final int COMMON_CLICK_CD = 20;

	protected final APP appInst;
	protected GScene scene;
	protected GNodeAppBar bar;
	protected boolean isInit = false;
	protected IAPPGuiRuntime runtime;

	public static final RenderTexutreFrame FRAME_CLOSE = new RenderTexutreFrame(32, 11, 6, 6, 256, 256);
	public final static RenderTexutreFrame FRAME_P1 = new RenderTexutreFrame(0, 19, 11, 11, 256, 256);
	public final static RenderTexutreFrame FRAME_L1 = new RenderTexutreFrame(12, 19, 10, 3, 256, 256);
	public final static RenderTexutreFrame FRAME_L2 = new RenderTexutreFrame(23, 19, 3, 10, 256, 256);
	public final static RenderTexutreFrame FRAME_L3 = new RenderTexutreFrame(19, 24, 3, 27, 256, 256);
	public final static RenderTexutreFrame FRAME_ITEM = new RenderTexutreFrame(0, 31, 18, 18, 256, 256);
	public final static RenderTexutreFrame FRAME_ITEM_HOVER = new RenderTexutreFrame(0, 50, 18, 18, 256, 256);
	public final static RenderTexutreFrame FRAME_ITEM_LOCKED = new RenderTexutreFrame(0, 69, 18, 18, 256, 256);
	public final static RenderTexutreFrame FRAME_ARROW_1_LEFT = new RenderTexutreFrame(23, 31, 9, 18, 256, 256);
	public final static RenderTexutreFrame FRAME_ARROW_1_RIGHT = new RenderTexutreFrame(32, 31, 9, 18, 256, 256);

	public APPGuiCommon(APP appInst) {
		this.appInst = appInst;
		this.scene = new GScene();
	}

	public void init(IAPPGuiRuntime runtime) {
		this.runtime = runtime;
		if (isInit) return;
		isInit = true;
		this.scene.setDisplaySize(runtime.getDisplayWidth(), runtime.getDisplayHeight());
		this.scene.setSize(runtime.getWidth(), runtime.getHeight());
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
		bar.setPosition(0, 0, 100);
		scene.addChild(bar);
	}

	public IAPPGuiRuntime getGuiRuntime() {
		return runtime;
	}

	public Color getThemeColor(AppGuiThemePart part) {
		switch (part) {
		case BACKGROUND_1:
			return new Color(0xf0d6ff);
		case BACKGROUND_2:
			return new Color(0xda96f6);
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
