package yuzunyannn.elementalsorcery.container.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.IDeviceNetwork;
import yuzunyannn.elementalsorcery.api.computer.soft.App;
import yuzunyannn.elementalsorcery.api.computer.soft.IComputerException;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGui;
import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGuiRuntime;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.render.RenderTexutreFrame;
import yuzunyannn.elementalsorcery.computer.DeviceNetworkLocal;
import yuzunyannn.elementalsorcery.computer.render.BtnColorInteractor;
import yuzunyannn.elementalsorcery.computer.render.ComputerScreen;
import yuzunyannn.elementalsorcery.computer.render.ComputerScreenRender;
import yuzunyannn.elementalsorcery.computer.render.GDisplayObject;
import yuzunyannn.elementalsorcery.computer.render.GDragContainer;
import yuzunyannn.elementalsorcery.computer.render.GEasyLayoutContainer;
import yuzunyannn.elementalsorcery.computer.render.SoftGuiCommon;
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
import yuzunyannn.elementalsorcery.nodegui.GLabel;
import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.nodegui.GScene;
import yuzunyannn.elementalsorcery.nodegui.GuiScene;
import yuzunyannn.elementalsorcery.nodegui.IGInteractor;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.helper.JavaHelper;

@SideOnly(Side.CLIENT)
public abstract class GuiComputerBase extends GuiContainer {

	protected final GScene scene = new GuiScene();
	protected final ContainerComputer containerComputer;
	protected IComputerException exception;
	protected GNode exceptionNode;
	protected int computerX, computerY;
	protected int computerWidth, computerHeight;
	protected ComputerScreen screenFront;
	protected float screenFrontAppear = 1;
	protected boolean isPowerOn;

	protected App currApp;
	protected App currTask;
	protected ISoftGui appGUI;
	protected ISoftGui taskGUI;

	protected static class TooltipInfo {
		final Vec3d vec;
		final Supplier<List<String>> factory;
		final ItemStack stack;
		int duration;
		Consumer<List<String>> hook;

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
		public BlockPos getPosition() {
			return containerComputer.getCurrPosition();
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
		public void sendNotice(String str, NBTTagCompound nbt) {
			if (pid == -1) return;
			nbt.setString("_nt_", str);
			nbt.setInteger("pid", pid);
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
		public void setTooltip(String key, Vec3d vec, int duration, ItemStack stack,
				@Nullable Consumer<List<String>> hook) {
			TooltipInfo info = new TooltipInfo(vec, stack, duration);
			info.hook = hook;
			tooltipMap.put(key, info);
		}

		@Override
		public void exception(Throwable err) {
			if (err instanceof IComputerException) exception = (IComputerException) err;
			else {
				ESAPI.logger.warn("render error", err);
				exception = IComputerException.easy("render error ", err);
			}
		}

		@Override
		public boolean hasFeature(String feature) {
			IComputer computer = containerComputer.getComputer();
			if (computer == null) return false;
			if ("network".equals(feature)) {
				IDeviceNetwork network = computer.device().getNetwork();
				if (network instanceof DeviceNetworkLocal) return false;
			}
			return true;
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
		if (screenFront == null) screenFront = ComputerScreenRender.apply();
		screenFront.setAPPGui(appGUI);
		screenFront.setTaskAppGui(taskGUI);
		this.scene.setSize(this.width, this.height);
		this.scene.setDisplaySize(mc.displayWidth, mc.displayHeight);

		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		offsetX = offsetX + computerX;
		offsetY = offsetY + computerY;
		this.scene.setRootPosition(offsetX, offsetY);
		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		screenFront.release();
		Keyboard.enableRepeatEvents(false);
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
			else {
				currTooltipHook = info.hook;
				this.renderToolTip(stack, x, y);
				currTooltipHook = null;
			}
		}
	}

	Consumer<List<String>> currTooltipHook;

	@Override
	public List<String> getItemToolTip(ItemStack itemStack) {
		List<String> list = super.getItemToolTip(itemStack);
		if (currTooltipHook != null) currTooltipHook.accept(list);
		return list;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		partialTicks = mc.getRenderPartialTicks();

		if (this.inException()) {
			int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
			offsetX = offsetX + computerX;
			offsetY = offsetY + computerY;
			GlStateManager.disableTexture2D();
			int r = 3, g = 56, b = 122;
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
			bufferbuilder.pos(offsetX, offsetY, 0.0D).color(r, g, b, 255).endVertex();
			bufferbuilder.pos(offsetX, offsetY + computerHeight, 0.0D).color(r, g, b, 255).endVertex();
			bufferbuilder.pos(offsetX + computerWidth, offsetY + computerHeight, 0.0D).color(r, g, b, 255).endVertex();
			bufferbuilder.pos(offsetX + computerWidth, offsetY, 0.0D).color(r, g, b, 255).endVertex();
			tessellator.draw();
			GlStateManager.enableTexture2D();
		}

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		this.scene.draw(partialTicks);
		GlStateManager.popMatrix();

		if (this.inException()) return;

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

	public boolean inException() {
		return this.exception != null;
	}

	GNode openImage;

	@Override
	public void updateScreen() {
		super.updateScreen();
		this.scene.tick();

		if (exception != null) {
			if (this.exceptionNode == null) initExceptionView();
			IComputer computer = getComputer();
			if (computer != null) {
				if (!computer.isPowerOn()) {
					this.exception = null;
					isPowerOn = false;
				}
			}
			return;
		} else {
			if (this.exceptionNode != null) {
				this.exceptionNode.removeFromParent();
				this.exceptionNode = null;
			}
		}

		// tooltip
		Iterator<Entry<String, TooltipInfo>> iter = tooltipMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, TooltipInfo> entry = iter.next();
			TooltipInfo info = entry.getValue();
			if (info.duration-- <= 0) iter.remove();
		}

		// computer
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

		if (computer.getException() != null) {
			this.exception = computer.getException();
			return;
		}

		if (screenFrontAppear < 1) screenFrontAppear = Math.min(screenFrontAppear + 0.075f, 1);

		try {
			IOS ios = computer.getSystem();
			App app = ios.getAppInst(ios.getForeground());
			if (app != currApp) {
				App oldApp = currApp;
				currApp = app;
				onCurrAppChange(oldApp);
			}
			int topTask = ios.getTopTask();
			if (topTask > 0) {
				App task = ios.getAppInst(topTask);
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
		} catch (Exception e) {
			if (e instanceof IComputerException) this.exception = (IComputerException) e;
			else {
				ESAPI.logger.warn("update error", e);
				exception = IComputerException.easy("update error ", e);
			}
		}
	}

	private void initExceptionView() {
		GDragContainer dContainer = new GDragContainer(computerWidth, computerHeight);
		this.scene.addChild(exceptionNode = dContainer);
		dContainer.setPositionZ(1);

		GEasyLayoutContainer container = new GEasyLayoutContainer();
		container.setMaxWidth(computerWidth);
		dContainer.addContainer(container);

		GNode bar = new GNode();
		bar.setWidth(computerWidth);
		bar.setHeight(10);
		container.addChild(bar);

		GImage shutDownBtn = new GImage(SoftGuiCommon.TEXTURE_1, new RenderTexutreFrame(24, 11, 7, 6, 256, 256));
		shutDownBtn.setPosition(2, 2, 1);
		shutDownBtn.setName("shutDownBtn");
		bar.addChild(shutDownBtn);
		shutDownBtn.setInteractor(new BtnColorInteractor(new Color(0xffffff), new Color(0xff0000)) {
			@Override
			public void onClick() {
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setString("_nt_", "power-off");
				containerComputer.sendToServer(nbt);
			}
		});

		if (exception.isGameException()) {
			GDisplayObject node = new GDisplayObject();
			node.setEveryLine(true);
			node.setDisplayObject(exception.toDisplayObject());
			container.addChild(node);
		} else {
			final String originMsg = exception.toString();

			GImage copyBtn = new GImage(SoftGuiCommon.TEXTURE_1, SoftGuiCommon.FRAME_ICON_COPY);
			copyBtn.setPosition(bar.getWidth() - copyBtn.getWidth() - 2, 2, 1);
			copyBtn.setName("copyBtn");
			bar.addChild(copyBtn);
			copyBtn.setInteractor(new BtnColorInteractor(new Color(0xffffff), new Color(0x00ff00)) {
				@Override
				public void onClick() {
					JavaHelper.clipboardWrite(originMsg);
				}
			});

			String str = originMsg;
			str = str.replace("\t", "");
			str = str.replace("\r", "");
			str = str.replace("at ", "at:");
			GLabel label = new GLabel(str);
			label.setWrapWidth(computerWidth - 1);
			container.addChild(label);
		}

		container.layout();
	}

	protected void onCurrAppChange(App old) {
		appGUI = null;
		screenFront.setAPPGui(null);
		runtime = null;

		App app = this.currApp;
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

		if (this.inException()) return;

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
			this.exception = IComputerException.easy("click error ", e);
		}
	}

	@Override
	public void handleKeyboardInput() throws IOException {
		if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
			super.handleKeyboardInput();
			return;
		}

		if (this.inException()) return;

		try {
			this.screenFront.onKeyboardEvent();
		} catch (Exception e) {
			ESAPI.logger.warn("click error", e);
			this.exception = IComputerException.easy("keyboard error ", e);
		}
	}

	@Override
	public void handleInput() throws IOException {
		super.handleInput();
//		if (Keyboard.isCreated()) {
//			while (Keyboard.next()) {
//				System.out.println("??w");
//			}
//		}
	}

	protected void updateWaitingOpenScene() {
		if (openImage == null) return;

		screenFrontAppear = 0;

		int w = computerWidth / 2;
		int h = computerHeight / 2;

		openImage.setPosition(w, h, 0);
	}

	protected void onClickOpenScene() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("_nt_", "power-on");
		containerComputer.sendToServer(nbt);
	}

	protected void closeOpenScene() {
		openImage.runAction(new GActionEaseInBack(new GActionScaleBy(8, new Vec3d(-0.95, -0.95, 0))));
		openImage.runAction(new GActionSequence(new GActionFadeTo(12, 0),
				new GActionFunction((e) -> e.removeFromParent())));
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
