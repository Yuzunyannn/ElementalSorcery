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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.RenderRect;
import yuzunyannn.elementalsorcery.computer.render.APPGuiCommon;
import yuzunyannn.elementalsorcery.computer.render.BtnBaseInteractor;
import yuzunyannn.elementalsorcery.computer.render.BtnColorInteractor;
import yuzunyannn.elementalsorcery.computer.render.DragInteractor;
import yuzunyannn.elementalsorcery.computer.render.GItemFrame;
import yuzunyannn.elementalsorcery.nodegui.GImage;
import yuzunyannn.elementalsorcery.nodegui.GLabel;
import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.nodegui.GScissor;
import yuzunyannn.elementalsorcery.nodegui.IGInteractor;
import yuzunyannn.elementalsorcery.parchment.Tutorial;
import yuzunyannn.elementalsorcery.parchment.TutorialCraft;
import yuzunyannn.elementalsorcery.parchment.TutorialCraft.TutorialCraftNodeParams;

@SideOnly(Side.CLIENT)
public class APPTutorialGPad extends GImage {

	public static final int ACT_DESCRIBE = 0;
	public static final int ACT_CRAFT = 1;

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
		super(APPGuiCommon.TEXTURE_1, AppTutorialGui.FRAME_P1);

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
		});
		setName("Tutorial Pad");

		GImage line1 = new GImage(APPGuiCommon.TEXTURE_1, AppTutorialGui.FRAME_L2);
		line1.setSplit9();
		line1.setSize(3, getHeight());
		line1.setPosition(optionWidth, 0);
		line1.setColorRef(gui.detailColor);
		addChild(line1);
		GImage line2 = new GImage(APPGuiCommon.TEXTURE_1, AppTutorialGui.FRAME_L1);
		line2.setSplit9();
		line2.setSize(getWidth(), 3);
		line2.setPosition(0, 16);
		line2.setColorRef(gui.detailColor);
		addChild(line2);

		GImage close = new GImage(APPGuiCommon.TEXTURE_1, AppTutorialGui.FRAME_CLOSE);
		close.setColorRef(gui.detailObjColor);
		close.setPosition(8.5, 8.5, 0);
		close.setAnchor(0.5, 0.5, 2);
		close.setInteractor(new BtnBaseInteractor() {
			@Override
			public void onClick() {
				gui.closeTutorial();
			}
		});
		addChild(close);

		scissor = new GScissor(new RenderRect(0, getHeight() - 20, 0, getWidth() - optionWidth - 4));
		scissor.setPosition(optionWidth + 4, 16 + 4, 0);
		addChild(scissor);
		container = new GNode();
		scissor.addChild(container);

		optionScissor = new GScissor(new RenderRect(0, getHeight() - 20, 0, optionWidth - 1));
		optionScissor.setPosition(1, 16 + 3, 0);
		addChild(optionScissor);
		optionContainer = new GNode();
		optionScissor.addChild(optionContainer);
		optionScissor.setInteractor(
				new DragInteractor(optionContainer, new RenderRect(0, scissor.getHeight(), 0, scissor.getWidth())));

		addBtn(I18n.format("es.tutorialui.describe"), ACT_DESCRIBE);

		List<ItemStack> crafts = tutorial.getCrafts();
		if (crafts != null && !crafts.isEmpty()) addBtn(I18n.format("es.tutorialui.craft"), ACT_CRAFT);

		shift(tutorial.cacheAction);
	}

	protected class GTutorialBtn extends GImage {

		protected GLabel label;

		public GTutorialBtn() {
			super(APPGuiCommon.TEXTURE_1, AppTutorialGui.FRAME_P1);
			setSplit9();
			setInteractor(new BtnColorInteractor(gui.detailColor, gui.detailObjColor) {
				@Override
				public void onClick() {
					GTutorialBtn.this.onClick();
				}

				@Override
				public void onHoverChange(GNode node) {
					super.onHoverChange(node);
					label.setColorRef(gui.detailObjColor);
				}

				@Override
				public void onPressed(GNode node) {
					super.onPressed(node);
					label.setColorRef(gui.detailColor);
				}
			});
			label = new GLabel();
			label.setColor(gui.detailObjColor);
			addChild(label);
		}

		public void onClick() {

		}

		public void setString(String text) {
			label.setString(text);
			this.setWidth(label.getWidth() + 6);
			label.setPosition(3.5, -4);
		}

	}

	protected class GTutorialPageBtn extends GImage {
		public GTutorialPageBtn(boolean isLeft, Runnable click) {
			super(APPGuiCommon.TEXTURE_1,
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
		GTutorialBtn btn = new GTutorialBtn() {
			@Override
			public void onClick() {
				shift(action);
			}
		};
		btn.setString(name);
		btns.add(btn);
		btnOffset = btnOffset + 3;
		btn.setPosition(optionWidth + 2 + btnOffset, 8.5, 2);
		btn.setAnchor(0, 0.5, 0);
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
			if (history.isEmpty());
			else history.removeFirst();
		}
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
		default:
			break;
		}
	}

	public void toDescribe() {
		GLabel label = new GLabel(tutorial.getDescribeDisplay());
		label.setColorRef(gui.detailObjColor);
		label.setWrapWidth((int) scissor.getWidth());
		container.addChild(label);
	}

	public void toCraft() {
		List<ItemStack> crafts = tutorial.getCrafts();
		if (crafts == null) return;
		if (crafts.isEmpty()) return;

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
		crafts = TutorialCraft.create(stack);
		if (crafts.isEmpty()) return false;
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

}
