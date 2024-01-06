package yuzunyannn.elementalsorcery.computer.soft;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.computer.soft.IAPPGuiRuntime;
import yuzunyannn.elementalsorcery.api.util.client.RenderRect;
import yuzunyannn.elementalsorcery.api.util.client.RenderTexutreFrame;
import yuzunyannn.elementalsorcery.computer.render.APPGuiCommon;
import yuzunyannn.elementalsorcery.computer.render.AppGuiThemePart;
import yuzunyannn.elementalsorcery.computer.render.BtnBaseInteractor;
import yuzunyannn.elementalsorcery.computer.render.DragInteractor;
import yuzunyannn.elementalsorcery.container.gui.GuiComputerTutorialPad;
import yuzunyannn.elementalsorcery.nodegui.GImage;
import yuzunyannn.elementalsorcery.nodegui.GItemStack;
import yuzunyannn.elementalsorcery.nodegui.GLabel;
import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.nodegui.GScissor;
import yuzunyannn.elementalsorcery.parchment.Tutorial;
import yuzunyannn.elementalsorcery.parchment.Tutorials;
import yuzunyannn.elementalsorcery.parchment.Tutorials.TutorialLevelInfo;
import yuzunyannn.elementalsorcery.parchment.Tutorials.TutorialUnlockInfo;
import yuzunyannn.elementalsorcery.util.helper.Color;

public class AppTutorialGui extends APPGuiCommon {

	public static double tutorialXOffset = 0;
	public static ItemStack tutorialCraftItemStack = ItemStack.EMPTY;

	protected GNode avigationContainer;
	protected GNode selectNode;
	protected GNode detailContainer;
	protected GNode detailScissor;
	protected GNode detailHighlight;
	protected GImage detailNode;
	protected GLabel detailTitle;
	protected GLabel detailTutorialTitle;
	protected Color detailColor;
	protected Color detailObjColor;
	protected int currIndex;
	protected final AppTutorial app;
	protected APPTutorialGPad currTutorial;

	protected class LevelBtn extends GImage {

		int levelIndex;
		int cd;

		BtnBaseInteractor interactor = new BtnBaseInteractor() {
			public void onClick() {
				if (cd > 0) return;
				cd = COMMON_CLICK_CD;
				AppTutorialGui.this.onLevelBtnClick(levelIndex);
			};
		};

		public LevelBtn(ResourceLocation texture, RenderTexutreFrame renderTexutreFrame) {
			super(texture, renderTexutreFrame);
			this.setInteractor(interactor);
		}

		@Override
		public void update() {
			super.update();
			if (cd > 0) cd--;
			if (app.isPartLocked(levelIndex)) {
				setAlpha(1);
				return;
			}
			float alpha = getAlpha();
			if (interactor.isHover || app.getSelectPartIndex() == this.levelIndex) {
				if (alpha < 1) this.setAlpha(Math.min(alpha + 0.2f, 1));
			} else {
				if (alpha > 0.3) this.setAlpha(Math.max(alpha - 0.1f, 0.3f));
			}
		}

	}

	public AppTutorialGui(AppTutorial appInst) {
		super(appInst);
		this.app = appInst;
	}

	@Override
	protected void onInit(IAPPGuiRuntime runtime) {
		super.onInit(runtime);

		Color color = this.getThemeColor(AppGuiThemePart.BACKGROUND_1);
		int height = runtime.getHeight();

		int naviWidth = 28;

		GImage avigation = new GImage(APPGuiCommon.TEXTURE_1, FRAME_P1);
		avigation.setColorRef(color);
		avigation.setSplit9();
		avigation.setSize(naviWidth, height - bar.getHeight());
		avigation.setPosition(0, bar.getHeight());
		scene.addChild(avigation);

		GImage line = new GImage(APPGuiCommon.TEXTURE_1, FRAME_L1);
		line.setSplit9();
		line.setSize(naviWidth, 3);
		line.setPosition(0, 8);
		line.setColorRef(color);
		avigation.addChild(line);

		avigationContainer = new GScissor(new RenderRect(0, avigation.getHeight() - 14, 2, naviWidth - 2));
		avigationContainer.setPosition(0, 12, 2);
		avigation.addChild(avigationContainer);

		currIndex = app.getSelectPartIndex();

		for (int i = 0; i < 6; i++) {
			LevelBtn icon = new LevelBtn(GuiComputerTutorialPad.TEXTURE,
					new RenderTexutreFrame(1 + i * 9, 159, 9, 9, 256, 256));
			icon.levelIndex = i;
			icon.setName("LevelBtn " + i);
			icon.setSize(18, 18);
			icon.setAnchor(0.5, 0, 0);
			icon.setPosition(naviWidth / 2, i * 20 + 1);
			if (app.isPartLocked(i)) icon.setColor(getLockedColorWithLevel(i));
			else {
				icon.setColor(getColorWithLevel(i));
				if (currIndex != i) icon.setAlpha(0.3f);
			}
			avigationContainer.addChild(icon);
		}

		detailColor = getDetailBackgroundColor(currIndex);

		selectNode = new GImage(GuiComputerTutorialPad.TEXTURE, new RenderTexutreFrame(10, 169, 9, 9, 256, 256));
		selectNode.setSize(18, 18);
		selectNode.setAnchor(0.5, 0, 0);
		selectNode.setPosition(naviWidth / 2, 0, 1);
		selectNode.setColorRef(detailColor);
		avigationContainer.addChild(selectNode);

		detailNode = new GImage(APPGuiCommon.TEXTURE_1, FRAME_P1);
		detailNode.setColorRef(detailColor);
		detailNode.setSplit9();
		detailNode.setSize(runtime.getWidth() - naviWidth, height - bar.getHeight() - 16);
		detailNode.setPosition(naviWidth, bar.getHeight() + 12);
		scene.addChild(detailNode);

		line = new GImage(APPGuiCommon.TEXTURE_1, FRAME_L1);
		line.setSplit9();
		line.setSize(detailNode.getWidth(), 3);
		line.setPosition(0, 9);
		line.setColorRef(detailColor);
		detailNode.addChild(line);

		detailContainer = new GNode();

		detailScissor = new GScissor(new RenderRect(0, detailNode.getHeight() - 14, 1, detailNode.getWidth() - 2));
		detailScissor.setPosition(0, 12, 2);
		detailNode.addChild(detailScissor);
		detailScissor.addChild(detailContainer);

		detailContainer.setHeight(detailScissor.getHeight());

		detailObjColor = this.getDetailObjectColor(currIndex);

		detailTitle = new GLabel("");
		detailTitle.setColorRef(detailObjColor);
		detailTitle.setPosition(4, 1);
		detailNode.addChild(detailTitle);

		detailTutorialTitle = new GLabel("");
		detailTutorialTitle.setColorRef(detailObjColor);
		detailTutorialTitle.setPosition(26, 1);
		detailNode.addChild(detailTutorialTitle);

		detailScissor.setName("detailScissor");
		detailScissor.setInteractor(new DragInteractor(detailContainer,
				new RenderRect(0, detailScissor.getHeight(), 0, detailScissor.getWidth())));

		setSelectAt(currIndex);

		String id = app.getTutorialId();
		Tutorial tutorial = Tutorials.getTutorial(id);
		if (tutorial != null) {
			detailContainer.setPositionX(tutorialXOffset);
			showTutorial(tutorial);
		}
	}

	public void setSelectAt(int index) {
		currIndex = index;
		detailTitle.setString("Lev." + index);
		selectNode.setPositionY(1 + index * 20);
		toColor = getDetailBackgroundColor(index);
		toColorObj = getDetailObjectColor(index);
		selectNode.setColorRef(toColor);
		toColorRate = 0;

		shiftDetail(index);
		detailHighlight = new GImage(APPGuiCommon.TEXTURE_1, FRAME_ITEM_HOVER);
		detailHighlight.setPosition(0, 0, 80);
		detailHighlight.setAlpha(0);
		detailHighlight.setAnchor(0, 0.5, 0);
		detailContainer.addChild(detailHighlight);
	}

	public float toColorRate = 1;
	public Color toColor = null;
	public Color toColorObj = null;
	public GNode highlightFrame = null;

	@Override
	public void update() {
		super.update();
		if (toColor != null) {
			toColorRate = toColorRate + 0.2f;
			detailColor.weight(toColor, toColorRate);
			detailObjColor.weight(toColorObj, toColorRate);
			if (toColorRate >= 1) {
				detailColor.setColor(toColor);
				toColor = null;
				toColorObj = null;
			}
		}
		if (currIndex != app.getSelectPartIndex()) setSelectAt(app.getSelectPartIndex());
		if (detailHighlight != null) {
			if (highlightFrame == null) detailHighlight.setAlpha(0);
			else {
				detailHighlight.setAlpha(0.5f);
				Vec3d vec = highlightFrame.getPostion();
				detailHighlight.setPosition(vec.x, vec.y);
			}
		}

	}

	protected void onLevelBtnClick(int index) {
		if (currIndex == index) return;
		if (app.isPartLocked(index)) return;
		app.changeSelectIndex(index);
	}

	protected void shiftDetail(int index) {
		detailContainer.removeAllChild();

		TutorialLevelInfo linfo = Tutorials.getTutorialInfoByLevel(index);
		if (linfo == null) return;

		double progress = app.getProgress() - linfo.getAccTotalUnlock();

		final double padding = 5;
		detailContainer.setWidth(padding);
		double height = detailContainer.getHeight() / 2;
		GNode progressNode = null;

		List<TutorialUnlockInfo> list = linfo.list;
		for (int i = 0; i < list.size(); i++) {
			double xBase = detailContainer.getWidth();
			double yBase = height;

			if (i > 0) {
				GImage line = new GImage(APPGuiCommon.TEXTURE_1, FRAME_L3);
				line.setSize(1, detailContainer.getHeight() / 1.5);
				line.setAnchor(0.5, 0.5, 0);
				line.setPosition(xBase - padding, yBase);
				line.setColorRef(detailColor);
				detailContainer.addChild(line);
			}
			TutorialUnlockInfo uinfo = list.get(i);
			int lineCount = (uinfo.list.size() - 1) / 3 + 1;
			double width = lineCount * 18 + (lineCount - 1) * padding;

			for (int j = 0; j < uinfo.list.size(); j++) {
				Tutorial tutorial = uinfo.list.get(j);

				int yLocal = j / lineCount;
				int xLocal = j % lineCount;

				double x = xBase + xLocal * (18 + padding);
				double y = yBase;

				if (yLocal > 0) {
					if (yLocal % 2 == 0) y = y + (yLocal / 2) * (18 + padding);
					else y = y - (yLocal / 2 + 1) * (18 + padding);
				}

				addTutorial(tutorial, x, y);
			}

			TutorialUnlockInfo nextInfo = i + 1 < list.size() ? list.get(i + 1) : null;
			if (nextInfo == null || progress < nextInfo.getUnlock()) {
				if (progressNode == null) {
					// todo
				}
			}

			detailContainer.setWidth(xBase + width + padding * 2);
		}

		detailContainer.setWidth(detailContainer.getWidth() - padding);
	}

	protected void addTutorial(Tutorial tutorial, double x, double y) {
		GImage frame = new GImage(APPGuiCommon.TEXTURE_1, FRAME_ITEM);
		frame.setAnchor(0, 0.5, 0);
		frame.setColorRef(detailColor);
		frame.setPosition(x, y, 1);
		frame.setName("tutorial " + tutorial.getId());
		frame.setInteractor(new BtnBaseInteractor() {

			@Override
			public boolean testHit(GNode node, Vec3d worldPos) {
				if (!detailScissor.testHit(worldPos)) return false;
				return super.testHit(node, worldPos);
			}

			@Override
			public boolean blockMousePressed(GNode node, Vec3d worldPos) {
				return false;
			}

			@Override
			public void onHoverChange(GNode node) {
				if (isHover) {
					highlightFrame = node;
					detailTutorialTitle.setString(tutorial.getTitleDisplay());
				} else if (highlightFrame == node) {
					highlightFrame = null;
					detailTutorialTitle.setString("");
				}
			}

			@Override
			public void onClick() {
				showTutorial(tutorial);
			}
		});
		detailContainer.addChild(frame);

		ItemStack itemStack = tutorial.getCoverItem();
		if (!itemStack.isEmpty()) {
			GItemStack stackNode = new GItemStack(itemStack);
			stackNode.setPosition(x + 9, y, 50);
			detailContainer.addChild(stackNode);
		}

	}

	protected void closeTutorial() {
		if (currTutorial != null) {
			this.app.changeShowTutorialId(null);
			currTutorial.removeFromParent();
			currTutorial = null;
		}
	}

	protected void showTutorial(Tutorial tutorial) {
		closeTutorial();
		int w = runtime.getWidth() - 4;
		int h = runtime.getHeight() - 4;
		currTutorial = new APPTutorialGPad(tutorial, this, w, h);
		currTutorial.setPosition(2, 2, 500);
		this.scene.addChild(currTutorial);
		this.app.changeShowTutorialId(tutorial.getId());
		tutorialXOffset = detailContainer.getPostionX();
	}

	protected Color getDetailObjectColor(int level) {
		Color color = this.getColorWithLevel(level);
		return color.weight(new Color(0), 0.2f);
	}

	protected Color getDetailBackgroundColor(int level) {
		Color color = this.getColorWithLevel(level);
		return color.weight(new Color(0xffffff), 0.75f);
	}

	protected Color getLockedColorWithLevel(int level) {
		return new Color(0x888888);
	}

	protected Color getColorWithLevel(int level) {
		switch (level) {
		case 0:
			return new Color(240 / 255f, 240 / 255f, 240 / 255f);
		case 1:
			return new Color(206 / 255f, 56 / 255f, 47 / 255f);
		case 2:
			return new Color(246 / 255f, 167 / 255f, 80 / 255f);
		case 3:
			return new Color(1 / 255f, 150 / 255f, 240 / 255f);
		case 4:
			return new Color(209 / 255f, 0 / 255f, 202 / 255f);
		case 5:
			return new Color(10 / 255f, 243 / 255f, 170 / 255f);
		default:
			return new Color(130 / 255f, 191 / 255f, 113 / 255f);
		}
	}

}
