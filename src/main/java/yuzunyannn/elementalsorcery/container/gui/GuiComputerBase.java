package yuzunyannn.elementalsorcery.container.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.soft.APP;
import yuzunyannn.elementalsorcery.api.computer.soft.IComputerException;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGui;
import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGuiRuntime;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.RenderTexutreFrame;
import yuzunyannn.elementalsorcery.computer.exception.ComputerException;
import yuzunyannn.elementalsorcery.computer.render.ComputerScreen;
import yuzunyannn.elementalsorcery.computer.render.ComputerScreenRender;
import yuzunyannn.elementalsorcery.container.ContainerComputer;
import yuzunyannn.elementalsorcery.container.gui.reactor.GuiElementReactor;
import yuzunyannn.elementalsorcery.nodegui.GActionEaseInBack;
import yuzunyannn.elementalsorcery.nodegui.GActionFadeTo;
import yuzunyannn.elementalsorcery.nodegui.GActionForever;
import yuzunyannn.elementalsorcery.nodegui.GActionFunction;
import yuzunyannn.elementalsorcery.nodegui.GActionRotateBy;
import yuzunyannn.elementalsorcery.nodegui.GActionScaleBy;
import yuzunyannn.elementalsorcery.nodegui.GActionSequence;
import yuzunyannn.elementalsorcery.nodegui.GImage;
import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.nodegui.GScene;
import yuzunyannn.elementalsorcery.nodegui.IGInteractor;
import yuzunyannn.elementalsorcery.util.helper.Color;

@SideOnly(Side.CLIENT)
public abstract class GuiComputerBase extends GuiContainer {

	protected final GScene scene = new GScene();
	protected final ContainerComputer containerComputer;
	protected IComputerException exception;
	protected int computerX, computerY;
	protected int computerWidth, computerHeight;
	protected ComputerScreen screenFront;
	protected float screenFrontAppear = 1;
	protected boolean isPowerOn;

	protected APP currApp;
	protected APP currTask;
	protected ISoftGui appGUI;
	protected ISoftGui taskGUI;

	protected static class TooltipInfo {
		final Vec3d vec;
		final Supplier<List<String>> factory;
		final ItemStack stack;
		int duration;

		public TooltipInfo(Vec3d vec, Supplier<List<String>> factory, int duration) {
			this.vec = vec;
			this.factory = factory;
			this.duration = duration;
			this.stack = ItemStack.EMPTY;
		}

		public TooltipInfo(Vec3d vec, ItemStack stack, int duration) {
			this.vec = vec;
			this.factory = () -> new ArrayList<>();
			this.duration = duration;
			this.stack = stack;
		}

		public boolean isMouseFollow() {
			return this.vec.z == -99;
		}

	}

	protected Map<String, TooltipInfo> tooltipMap = new HashMap<>();

	class APPGuiRuntime implements ISoftGuiRuntime {

		private int pid = -1;

		public APPGuiRuntime(int pid) {
			this.pid = pid;
		}

		@Override
		public int getWidth() {
			return screenFront.getWidth();
		}

		@Override
		public int getHeight() {
			return screenFront.getHeight();
		}

		@Override
		public int getDisplayHeight() {
			return screenFront.getDisplayHeight();
		}

		@Override
		public int getDisplayWidth() {
			return screenFront.getDisplayWidth();
		}

		@Override
		public void sendOperation(NBTTagCompound nbt) {
			if (pid == -1) return;
			nbt.setInteger("_op_", pid);
			containerComputer.sendToServer(nbt);
		}

		@Override
		public void sendNotice(String str) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("_nt_", str);
			nbt.setInteger("pid", pid);
			containerComputer.sendToServer(nbt);
		}

		@Override
		public void setTooltip(String key, Vec3d vec, int duration, Supplier<List<String>> factory) {
			tooltipMap.put(key, new TooltipInfo(vec, factory, duration));
		}

		@Override
		public void setTooltip(String key, Vec3d vec, int duration, ItemStack stack) {
			tooltipMap.put(key, new TooltipInfo(vec, stack, duration));
		}

	}

	protected APPGuiRuntime runtime;
	protected APPGuiRuntime taskRuntime;

	public GuiComputerBase(ContainerComputer containerComputer) {
		super(containerComputer);
		this.containerComputer = containerComputer;
		this.containerComputer.msgHook = (params) -> {
			int pid = params.get("pid", Integer.class);
			NBTTagCompound data = params.get("data", NBTTagCompound.class);
			if (currTask != null && currTask.getPid() == pid) {
				if (taskGUI != null) taskGUI.onRecvMessage(data);
			} else if (currApp != null && currApp.getPid() == pid) {
				if (appGUI != null) appGUI.onRecvMessage(data);
			}
		};
	}

	public IComputer getComputer() {
		return this.containerComputer.getComputer();
	}

	@Override
	public void initGui() {
		super.initGui();
		screenFront = ComputerScreenRender.apply();
		screenFront.setAPPGui(appGUI);
		screenFront.setTaskAppGui(taskGUI);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		screenFront.release();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);

		Iterator<TooltipInfo> iter = tooltipMap.values().iterator();
		while (iter.hasNext()) {
			TooltipInfo info = iter.next();
			int x, y;
			if (info.isMouseFollow()) {
				x = mouseX;
				y = mouseY;
			} else {
				x = (int) info.vec.x;
				y = (int) info.vec.y;
			}
			ItemStack stack = info.stack;
			if (stack.isEmpty()) this.drawHoveringText(info.factory.get(), x, y, fontRenderer);
			else this.renderToolTip(stack, x, y);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		partialTicks = mc.getRenderPartialTicks();
		GlStateManager.pushMatrix();
		this.scene.draw(partialTicks);
		GlStateManager.popMatrix();

		if (isPowerOn) {
			GlStateManager.pushMatrix();
			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.disableBlend();
			screenFront.bindTexture();
			float offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
			offsetX = offsetX + computerX + computerWidth / 2;
			offsetY = offsetY + computerY + computerHeight / 2;
			GlStateManager.translate(offsetX, offsetY, 0);
			if (screenFrontAppear < 1) {
				float prevScreenFrontAppear = screenFrontAppear - 0.075f;
				float a = RenderFriend.getPartialTicks(screenFrontAppear, prevScreenFrontAppear, partialTicks);
				if (a < 0.5) GlStateManager.scale(0, 0, 0);
				else {
					a = (a - 0.5f) / 0.5f;
					GlStateManager.scale(a, a, 1);
				}
			}
			RenderFriend.drawTextureRectInCenter(0, 0, computerWidth, computerHeight);
			GlStateManager.enableBlend();
			GlStateManager.popMatrix();
		}

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

	}

	@Override
	public void onResize(Minecraft mcIn, int w, int h) {
		super.onResize(mcIn, w, h);
		if (openImage != null) {
			updateWaitingOpenScene();
			openImage.updateGaps();
		}
	}

	GNode openImage;

	@Override
	public void updateScreen() {
		super.updateScreen();
		this.scene.tick();

		if (exception != null) { return; }

		Iterator<Entry<String, TooltipInfo>> iter = tooltipMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, TooltipInfo> entry = iter.next();
			TooltipInfo info = entry.getValue();
			if (info.duration-- <= 0) iter.remove();
		}

//		if (ESAPI.isDevelop) {
//			if (appGUI instanceof AppCommandGui) {
//				((AppCommandGui) appGUI).reinit(screenFront);
//			}
//		}

		IComputer computer = getComputer();

		if (computer == null) {
			this.exception = IComputerException.easy("unknow");
			return;
		}

		screenFront.hold();

		if (!computer.isPowerOn()) {
			isPowerOn = false;
			if (openImage == null) this.createWaitingOpenScene();
			this.updateWaitingOpenScene();
			screenFront.setAPPGui(null);
			screenFront.setTaskAppGui(null);
			currApp = null;
			currTask = null;
			return;
		}

		isPowerOn = true;

		if (openImage != null) {
			closeOpenScene();
			openImage = null;
		}

		if (screenFrontAppear < 1) screenFrontAppear = Math.min(screenFrontAppear + 0.075f, 1);

		try {
			IOS ios = computer.getSystem();
			APP app = ios.getAppInst(ios.getForeground());
			if (app != currApp) {
				APP oldApp = currApp;
				currApp = app;
				onCurrAppChange(oldApp);
			}
			int topTask = ios.getTopTask();
			if (topTask > 0) {
				APP task = ios.getAppInst(topTask);
				if (currTask != task) {
					taskGUI = task.createGUIRender();
					taskGUI.init(taskRuntime = new APPGuiRuntime(task.getPid()));
					currTask = task;
					screenFront.setTaskAppGui(taskGUI);
				}
			} else if (currTask != null) {
				currTask = null;
				screenFront.setTaskAppGui(taskGUI = null);
			}
		} catch (ComputerException e) {
			this.exception = e;
		}
	}

	protected void onCurrAppChange(APP old) {
		appGUI = null;
		screenFront.setAPPGui(null);
		runtime = null;

		APP app = this.currApp;
		if (app == null) return;

		runtime = new APPGuiRuntime(currApp.getPid());
		appGUI = app.createGUIRender();
		appGUI.init(runtime);
		screenFront.setAPPGui(appGUI);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
		int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
		this.scene.onMouseEvent(new Vec3d(mouseX, mouseY, 0));

		if (this.exception != null) return;

		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		offsetX = offsetX + computerX;
		offsetY = offsetY + computerY;
		double emouseX = mouseX - offsetX;
		double emouseY = mouseY - offsetY;
		emouseX = emouseX / (double) this.computerWidth;
		emouseY = emouseY / (double) this.computerHeight;
		try {
			this.screenFront.onMouseEvent(new Vec3d(emouseX, emouseY, 0));
		} catch (Exception e) {
			ESAPI.logger.warn("click error", e);
			this.exception = IComputerException.easy("click error " + e);
		}
	}

	protected void updateWaitingOpenScene() {
		if (openImage == null) return;

		screenFrontAppear = 0;

		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		offsetX = offsetX + computerX;
		offsetY = offsetY + computerY;

		int w = computerWidth / 2;
		int h = computerHeight / 2;

		openImage.setPosition(offsetX + w, offsetY + h, 0);
	}

	protected void onClickOpenScene() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("_nt_", "power-on");
		containerComputer.sendToServer(nbt);
	}

	protected void closeOpenScene() {
		openImage.runAction(new GActionEaseInBack(new GActionScaleBy(8, new Vec3d(-1, -1, 0))));
		openImage.runAction(
				new GActionSequence(new GActionFadeTo(12, 0), new GActionFunction((e) -> e.removeFromParent())));
	}

	protected void createWaitingOpenScene() {
		openImage = new GNode();
		this.scene.addChild(openImage);

		for (int i = 0; i < 4; i++) {
			Color color = GuiElementReactor.fcolors[i];
			GImage image = new GImage(GuiElementReactor.COMS, new RenderTexutreFrame(0, 0, 38, 54, 256, 256));
			image.setColor(color);
			image.setSize(38 / 2, 54 / 2);
			image.setAnchor(0.5, 1.1, 0);
			image.runAction(new GActionRotateBy(10, i * 90));
			image.setGaps(true);
			image.setName(String.format("f%d", i));
			openImage.addChild(image);
		}

		openImage.setGaps(true);
		updateWaitingOpenScene();
		openImage.updateGaps();

		openImage.setSize(54, 54);
		openImage.setAnchor(0.5, 0.5, 0);
		openImage.setInteractor(new IGInteractor() {
			@Override
			public void onMouseReleased(GNode node, Vec3d worldPos) {
				onClickOpenScene();
			}
		});
		openImage.setAlpha(0);
		openImage.runAction(new GActionFadeTo(10, 1));
		openImage.runAction(new GActionForever(new GActionRotateBy(360, 360)));
	}

}
