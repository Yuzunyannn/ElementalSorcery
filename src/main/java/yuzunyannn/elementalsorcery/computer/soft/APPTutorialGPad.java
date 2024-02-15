package yuzunyannn.elementalsorcery.computer.soft;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.RenderRect;
import yuzunyannn.elementalsorcery.api.util.client.RenderTexutreFrame;
import yuzunyannn.elementalsorcery.computer.render.BtnBaseInteractor;
import yuzunyannn.elementalsorcery.computer.render.BtnColorInteractor;
import yuzunyannn.elementalsorcery.computer.render.DragInteractor;
import yuzunyannn.elementalsorcery.computer.render.GItemFrame;
import yuzunyannn.elementalsorcery.computer.render.GStringBtn;
import yuzunyannn.elementalsorcery.computer.render.SoftGuiCommon;
import yuzunyannn.elementalsorcery.nodegui.GImage;
import yuzunyannn.elementalsorcery.nodegui.GLabel;
import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.nodegui.GScissor;
import yuzunyannn.elementalsorcery.nodegui.GTutorialBuilding;
import yuzunyannn.elementalsorcery.nodegui.IGInteractor;
import yuzunyannn.elementalsorcery.parchment.Tutorial;
import yuzunyannn.elementalsorcery.parchment.TutorialBuilding;
import yuzunyannn.elementalsorcery.parchment.TutorialCraft;
import yuzunyannn.elementalsorcery.parchment.TutorialCraft.TutorialCraftNodeParams;
import yuzunyannn.elementalsorcery.util.LambdaReference;
import yuzunyannn.elementalsorcery.util.item.BigItemStack;

@SideOnly(Side.CLIENT)
public class APPTutorialGPad extends GImage {

	public static final int ACT_DESCRIBE = 0;
	public static final int ACT_CRAFT = 1;
	public static final int ACT_BUILDING = 2;

	public final Tutorial tutorial;
	public final AppTutorialGui gui;
	protected List<GTutorialBtn> btns = new ArrayList<>();
	protected double btnOffset = 0;

	protected GNode scissor;
	protected GNode container;

	protected GNode optionScissor;
	protected GNode optionContainer;
	protected double optionWidth = 26;

	public APPTutorialGPad(Tutorial tutorial, AppTutorialGui gui, int w, int h) {
		super(SoftGuiCommon.TEXTURE_1, AppTutorialGui.FRAME_P1);

		this.gui = gui;
		this.tutorial = tutorial;

		setSize(w, h);
		setSplit9();
		setColorRef(gui.detailColor);
		setInteractor(new IGInteractor() {
			@Override
			public boolean onMousePressed(GNode node, Vec3d worldPos) {
				if (Mouse.getEventButton() == 1) onRightClick();
				return IGInteractor.super.onMousePressed(node, worldPos);
			}

			@Override
			public boolean isBlockHover(GNode node) {
				return true;
			}
		});
		setName("Tutorial Pad");
		double topLength = 14;

		GImage line1 = new GImage(SoftGuiCommon.TEXTURE_1, AppTutorialGui.FRAME_L2);
		line1.setSplit9();
		line1.setSize(3, getHeight());
		line1.setPosition(optionWidth, 0);
		line1.setColorRef(gui.detailColor);
		addChild(line1);
		GImage line2 = new GImage(SoftGuiCommon.TEXTURE_1, AppTutorialGui.FRAME_L1);
		line2.setSplit9();
		line2.setSize(getWidth(), 3);
		line2.setPosition(0, topLength);
		line2.setColorRef(gui.detailColor);
		addChild(line2);

		GImage close = new GImage(SoftGuiCommon.TEXTURE_1, AppTutorialGui.FRAME_CLOSE);
		close.setColorRef(gui.detailObjColor);
		close.setPosition(13, topLength / 2 + 0.5, 0);
		close.setAnchor(0.5, 0.5, 2);
		close.setSize(9, 9);
		close.setInteractor(new BtnBaseInteractor() {
			@Override
			public void onClick() {
				gui.closeTutorial();
			}
		});
		addChild(close);

		scissor = new GScissor(new RenderRect(0, getHeight() - topLength - 4, 0, getWidth() - optionWidth - 4));
		scissor.setPosition(optionWidth + 3, topLength + 3, 0);
		addChild(scissor);
		container = new GNode();
		scissor.addChild(container);
		scissor.setInteractor(new DragInteractor(container));

		optionScissor = new GScissor(new RenderRect(0, getHeight() - topLength - 4, 0, optionWidth - 1));
		optionScissor.setPosition(1, topLength + 3, 0);
		addChild(optionScissor);
		optionContainer = new GNode();
		optionScissor.addChild(optionContainer);
		optionScissor.setInteractor(new DragInteractor(optionContainer));

		addBtn(I18n.format("es.tutorialui.describe"), ACT_DESCRIBE);

		List<ItemStack> crafts = tutorial.getCrafts();
		if (crafts != null && !crafts.isEmpty()) addBtn(I18n.format("es.tutorialui.craft"), ACT_CRAFT);
		if (tutorial.getBuilding() != null) addBtn(I18n.format("es.tutorialui.building"), ACT_BUILDING);

		shift(tutorial.cacheAction);
	}

	protected class GTutorialBtn extends GStringBtn {

		public GTutorialBtn(Runnable onClick) {
			super(onClick, gui.detailColor, gui.detailObjColor);
		}

	}

	protected class GTutorialPageBtn extends GImage {
		public GTutorialPageBtn(boolean isLeft, Runnable click) {
			super(SoftGuiCommon.TEXTURE_1,
					isLeft ? AppTutorialGui.FRAME_ARROW_1_LEFT : AppTutorialGui.FRAME_ARROW_1_RIGHT);
			setAnchor(0.5, 0.5);
			setInteractor(new BtnColorInteractor(gui.detailColor, gui.detailObjColor) {
				@Override
				public void onClick() {
					if (click != null) click.run();
				}
			});
		}
	}

	public void addBtn(String name, int action) {
		GTutorialBtn btn = new GTutorialBtn(() -> shift(action));
		btn.setString(name);
		btns.add(btn);
		btnOffset = btnOffset + 3;
		btn.setPosition(optionWidth + 2 + btnOffset, 14 / 2 + 0.5, 2);
		addChild(btn);
		btnOffset = btnOffset + btn.getWidth();
	}

	public void onRightClick() {
		historyBack = true;
		Minecraft.getMinecraft().getSoundHandler()
				.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	protected LinkedList<Object> history = new LinkedList();
	protected boolean historyBack = false;

	@Override
	public void update() {
		super.update();
		this.updateCraft();
		if (historyBack) {
			historyBack = false;
			if (history.isEmpty()) gui.closeTutorial();
			else history.removeFirst();
		}
		if (tutorial.cacheAction == ACT_DESCRIBE) tutorial.cacheOffsetY = container.getPostionY();
	}

	protected void addHistory(Object obj) {
		if (history.size() > 8) return;
		history.addFirst(obj);
	}

	protected <T> T getLastHistory(Class<T> cls) {
		if (history.isEmpty()) return null;
		Object obj = history.getFirst();
		if (cls.isAssignableFrom(obj.getClass())) return (T) obj;
		return null;
	}

	public void shift(int action) {
		container.removeAllChild();
		container.setPositionY(0);
		optionContainer.removeAllChild();
		optionContainer.setPositionY(0);
		tutorial.cacheAction = action;
		switch (action) {
		case ACT_DESCRIBE:
			toDescribe();
			break;
		case ACT_CRAFT:
			toCraft();
			break;
		case ACT_BUILDING:
			toBuilding();
			break;
		default:
			break;
		}
	}

	public void toDescribe() {
		GLabel label = new GLabel(tutorial.getDescribeDisplay());
		label.setColorRef(gui.detailObjColor);
		label.setWrapWidth((int) scissor.getWidth());
		label.setPositionY(1);
		container.setHeight(label.getHeight() + 1);
		container.addChild(label);
		if (tutorial.cacheOffsetY < 0) container.setPositionY(tutorial.cacheOffsetY);
	}

	public void toCraft() {
		List<ItemStack> crafts = tutorial.getCrafts();
		if (crafts == null) return;
		if (crafts.isEmpty()) return;
		container.setHeight(0);

		double yoffset = 10;
		double xoffset = optionScissor.getWidth() / 2 - 0.5f;
		ItemStack startStack = crafts.get(0);
		for (ItemStack stack : crafts) {
			if (stack.isItemEqual(AppTutorialGui.tutorialCraftItemStack)) startStack = stack;
			GItemFrame frame = new GItemFrame(stack);
			frame.setColorRef(gui.detailColor);
			frame.setPosition(xoffset, yoffset, 2);
			frame.setName("craft " + tutorial.getId());
			frame.enableClick(() -> {
				AppTutorialGui.tutorialCraftItemStack = stack;
				showCraft(stack);
			}, optionScissor);
			optionContainer.addChild(frame);
			yoffset = yoffset + 18 + 2;
		}
		optionContainer.setHeight(yoffset - 9 - 1);

		showCraft(startStack);
	}

	List<TutorialCraft> crafts = new ArrayList<>();
	int craftIndex = 0;
	ItemStack currShow = ItemStack.EMPTY;

	protected boolean showCraft(ItemStack stack) {
		List<TutorialCraft> crafts = TutorialCraft.create(stack);
		if (crafts.isEmpty()) return false;
		this.crafts = crafts;
		currShow = stack;
		craftIndex = 0;
		showCurrCraft();
		return true;
	}

	protected void showCurrCraft() {
		if (crafts.isEmpty()) return;

		container.removeAllChild();
		craftIndex = MathHelper.clamp(craftIndex, 0, crafts.size() - 1);

		if (crafts.size() > 1) {
			GTutorialPageBtn left = new GTutorialPageBtn(true, () -> {
				craftIndex--;
				showCurrCraft();
			});
			GTutorialPageBtn right = new GTutorialPageBtn(false, () -> {
				craftIndex++;
				showCurrCraft();
			});

			double yoffset = scissor.getHeight() / 2;
			left.setPosition(5, yoffset, 1);
			right.setPosition(scissor.getWidth() - 7, yoffset, 1);

			if (craftIndex <= 0) left.setVisible(false);
			if (craftIndex >= crafts.size() - 1) right.setVisible(false);

			container.addChild(left);
			container.addChild(right);
		}

		TutorialCraft craft = crafts.get(craftIndex);

		TutorialCraftNodeParams params = new TutorialCraftNodeParams();
		params.width = scissor.getWidth();
		params.height = scissor.getHeight();
		params.color = gui.detailColor;
		params.gui = gui.getGuiRuntime();
		params.click = (itemStack) -> {
			if (ItemStack.areItemsEqual(currShow, itemStack)) return;
			ItemStack lastShow = currShow;
			if (showCraft(itemStack)) addHistory(lastShow);
		};
		params.tutorial = tutorial;
		GNode node = craft.createNodeContainer(params);
		if (node == null) return;

		container.addChild(node);
	}

	protected void updateCraft() {
		if (historyBack) history: {
			ItemStack itemStack = getLastHistory(ItemStack.class);
			if (itemStack == null) break history;
			showCraft(itemStack);
		}
	}

	protected class GTutorialIconBtn extends GImage {

		protected Runnable onClick;

		public GTutorialIconBtn(ResourceLocation textureResource, RenderTexutreFrame frame, Runnable onClick) {
			super(textureResource, frame);
			this.onClick = onClick;
			setInteractor(new BtnColorInteractor(gui.detailColor, gui.detailObjColor) {
				@Override
				public void onClick() {
					if (GTutorialIconBtn.this.onClick != null) onClick.run();
				}
			});
			setColorRef(gui.detailColor);
		}

	}

	public void toBuilding() {
		TutorialBuilding building = tutorial.getBuilding();
		if (building == null) return;

		double width = scissor.getWidth();
		double height = scissor.getHeight();

		final GTutorialBuilding bnode = new GTutorialBuilding();
		building.export(bnode);
		bnode.setSize(width, height);
		bnode.setPosition(width / 2, height / 2, 200);

		double centerLayer = (bnode.getMinLayer() + bnode.getMaxLayer()) / 2.0;
		bnode.offsetY = centerLayer;

		container.addChild(bnode);

		double yOffset = 8 + 2;
		GTutorialIconBtn exportBtn = new GTutorialIconBtn(AppTutorialGui.TEXTURE, RenderTexutreFrame.ofGUI(56, 159, 16),
				() -> printBuilding());
		exportBtn.setAnchor(0.5, 0.5);
		exportBtn.setPosition(optionScissor.getWidth() / 2, yOffset, 10);
		optionContainer.addChild(exportBtn);

		LambdaReference<Runnable> updateStackView = LambdaReference.of(null);

		yOffset = yOffset + 16 + 2;
		LambdaReference<Integer> showIndex = LambdaReference.of(0);
		GTutorialIconBtn layerBtn = new GTutorialIconBtn(AppTutorialGui.TEXTURE, RenderTexutreFrame.ofGUI(72, 159, 16),
				() -> {
					List<Integer> list = bnode.getLayerList();
					List<Integer> showList = bnode.getShowLayerList();
					if (showIndex.get() >= list.size()) {
						showList.clear();
						showIndex.set(0);
					} else {
						showList.clear();
						showList.add(list.get(showIndex.get()));
						showIndex.set(showIndex.get() + 1);
					}
					updateStackView.get().run();
				});
		layerBtn.setAnchor(0.5, 0.5);
		layerBtn.setPosition(optionScissor.getWidth() / 2 - 0.5, yOffset, 10);
		optionContainer.addChild(layerBtn);

		GNode sContainer = new GNode();
		sContainer.setPosition(0, yOffset + 16 + 4, 1);
		optionContainer.addChild(sContainer);

		updateStackView.set(() -> {
			sContainer.removeAllChild();
			List<BigItemStack> list = bnode.getItemStackByShowLayer();
			double xoffset = optionScissor.getWidth() / 2, yoffset = 0;
			for (BigItemStack stack : list) {
				GItemFrame frame = new GItemFrame(stack.getItemStack());
				frame.setColorRef(gui.detailColor);
				frame.setPosition(xoffset, yoffset, 20);
				frame.setName("build " + tutorial.getId());
				frame.setRuntime(gui.getGuiRuntime());
				frame.enableClick(() -> {}, optionScissor);
				sContainer.addChild(frame);
				yoffset = yoffset + 18 + 2;
			}
			sContainer.setHeight(yoffset);
			optionContainer.setHeight(sContainer.getPostionY() + sContainer.getHeight() - 8);
		});

		updateStackView.get().run();
	}

	protected void printBuilding() {
		TutorialBuilding building = tutorial.getBuilding();
		if (building == null) return;
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("ptbd", tutorial.getId());
		gui.trySendOperation(nbt);
	}

}
