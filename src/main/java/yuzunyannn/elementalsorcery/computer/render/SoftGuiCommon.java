package yuzunyannn.elementalsorcery.computer.render;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.computer.soft.APP;
import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGui;
import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGuiRuntime;
import yuzunyannn.elementalsorcery.api.util.client.RenderTexutreFrame;
import yuzunyannn.elementalsorcery.nodegui.GActionMoveBy;
import yuzunyannn.elementalsorcery.nodegui.GActionRemove;
import yuzunyannn.elementalsorcery.nodegui.GActionSequence;
import yuzunyannn.elementalsorcery.nodegui.GActionTime;
import yuzunyannn.elementalsorcery.nodegui.GImage;
import yuzunyannn.elementalsorcery.nodegui.GLabel;
import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.nodegui.GScene;
import yuzunyannn.elementalsorcery.util.helper.Color;

@SideOnly(Side.CLIENT)
public abstract class SoftGuiCommon implements ISoftGui {

	public static final ResourceLocation TEXTURE_1 = new ResourceLocation(ESAPI.MODID,
			"textures/gui/computer/common_1.png");
	public static final int COMMON_CLICK_CD = 20;

	protected final APP appInst;
	protected GScene scene;
	protected GNodeAppBar bar;
	protected boolean isInit = false;
	protected ISoftGuiRuntime runtime;

	public static final RenderTexutreFrame FRAME_CLOSE = new RenderTexutreFrame(32, 11, 6, 6, 256, 256);
	public final static RenderTexutreFrame FRAME_P1 = new RenderTexutreFrame(0, 19, 11, 11, 256, 256);
	public final static RenderTexutreFrame FRAME_P2 = new RenderTexutreFrame(38, 19, 11, 11, 256, 256);
	public final static RenderTexutreFrame FRAME_L1 = new RenderTexutreFrame(12, 19, 10, 3, 256, 256);
	public final static RenderTexutreFrame FRAME_L2 = new RenderTexutreFrame(23, 19, 3, 10, 256, 256);
	public final static RenderTexutreFrame FRAME_L3 = new RenderTexutreFrame(19, 24, 3, 27, 256, 256);
	public final static RenderTexutreFrame FRAME_ITEM = new RenderTexutreFrame(0, 31, 18, 18, 256, 256);
	public final static RenderTexutreFrame FRAME_ITEM_HOVER = new RenderTexutreFrame(0, 50, 18, 18, 256, 256);
	public final static RenderTexutreFrame FRAME_ITEM_LOCKED = new RenderTexutreFrame(0, 69, 18, 18, 256, 256);
	public final static RenderTexutreFrame FRAME_ARROW_1_LEFT = new RenderTexutreFrame(23, 31, 9, 18, 256, 256);
	public final static RenderTexutreFrame FRAME_ARROW_1_RIGHT = new RenderTexutreFrame(32, 31, 9, 18, 256, 256);

	public SoftGuiCommon(APP appInst) {
		this.appInst = appInst;
		this.scene = new GScene();
	}

	public void init(ISoftGuiRuntime runtime) {
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

	public void reinit(ISoftGuiRuntime runtime) {
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
		runtime.sendNotice("exit");
	}

	abstract protected void onInit(ISoftGuiRuntime runtime);

	public ISoftGuiRuntime getGuiRuntime() {
		return runtime;
	}

	public Color getThemeColor(SoftGuiThemePart part) {
		switch (part) {
		case BACKGROUND_1:
			return new Color(0xf0d6ff);
		case BACKGROUND_2:
			return new Color(0xda96f6);
		case OBJECT_1:
			return new Color(0x9d43d0);
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
		if (sendOperationCD > 0) sendOperationCD--;
	}

	@Override
	public void render(float partialTicks) {
		scene.draw(partialTicks);
	}

	@Override
	public void onMouseEvent(Vec3d vec3d) {
		scene.onMouseEvent(vec3d);
	}

	public int sendOperationCD = 0;

	public void trySendOperation(NBTTagCompound nbt) {
		if (sendOperationCD > 0) return;
		sendOperationCD = 10;
		getGuiRuntime().sendOperation(nbt);
	}

	public void tip(String msg) {
		GNode tip = createTip(msg);
		tip.setPosition((runtime.getWidth() - tip.getWidth()) / 2, -tip.getHeight(), 2000);
		tip.setGaps(true);
		this.scene.addChild(tip);
		tip.runAction(new GActionSequence(new GActionMoveBy(4, 0, tip.getHeight()), new GActionTime(60),
				new GActionMoveBy(2, 0, -tip.getHeight()), new GActionRemove()));
		tip.setInteractor(new BtnBaseInteractor() {
			public void onClick() {
				tip.clearActions();
				tip.runAction(new GActionSequence(new GActionMoveBy(2, 0, -tip.getHeight()), new GActionRemove()));
			};
		});
	}

	public GNode createTip(String msg) {
		Color color = this.getThemeColor(SoftGuiThemePart.BACKGROUND_2);
		Color colorObj = this.getThemeColor(SoftGuiThemePart.OBJECT_2);
		color = color.copy().weight(new Color(0xffffff), 0.35f);

		GLabel label = new GLabel();
		label.setColorRef(colorObj);
		label.setString(msg);
		label.setWrapWidth((int) (runtime.getWidth() * 0.8));

		GImage tip = new GImage(SoftGuiCommon.TEXTURE_1, FRAME_P1);
		tip.setColorRef(color);
		tip.setSplit9();
		tip.setSize(label.getWidth() + 10, label.getHeight() + 4);
		tip.addChild(label);
		label.setPosition(5, 2);

		return tip;
	}

}
