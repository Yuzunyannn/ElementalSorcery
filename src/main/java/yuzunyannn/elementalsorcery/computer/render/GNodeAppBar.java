package yuzunyannn.elementalsorcery.computer.render;

import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.RenderTexutreFrame;
import yuzunyannn.elementalsorcery.computer.soft.AppGuiThemePart;
import yuzunyannn.elementalsorcery.nodegui.GImage;
import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.util.helper.Color;

public class GNodeAppBar extends GNode {

	protected GImage batteryOuter;
	protected GImage batteryInner;
	protected GImage closeBtn;
	protected GImage shutDownBtn;

	public GNodeAppBar(APPGuiCommon gui, int width) {
		GImage image = new GImage(APPGuiCommon.TEXTURE_1, new RenderTexutreFrame(0, 0, 256, 10, 256, 256));
		image.setColorRef(gui.getThemeColor(AppGuiThemePart.BACKGROUND_2));
		image.setSplit9(RenderFriend.SPLIT9_AVERAGE_RECT);
		image.setSize(width, 9);
		this.addChild(image);

		Color color1 = gui.getThemeColor(AppGuiThemePart.OBJECT_2);
		Color color2 = gui.getThemeColor(AppGuiThemePart.OBJECT_2_ACTIVE);

		batteryOuter = new GImage(APPGuiCommon.TEXTURE_1, new RenderTexutreFrame(0, 11, 6, 7, 256, 256));
		batteryInner = new GImage(APPGuiCommon.TEXTURE_1, new RenderTexutreFrame(6, 11, 6, 7, 256, 256));
		batteryOuter.setColorRef(color1);
		batteryInner.setColorRef(color1);
		batteryOuter.setPosition(width - 10, 0.5, 2);
		batteryInner.setPosition(width - 10, 0.5 + 7, 1);
		batteryInner.setAnchor(0, 1, 0);
		batteryOuter.setSize(6, 7);
		batteryInner.setSize(6, 5);
		this.addChild(batteryInner);
		this.addChild(batteryOuter);

		GImage link = new GImage(APPGuiCommon.TEXTURE_1, new RenderTexutreFrame(12, 11, 11, 7, 256, 256));
		link.setPosition(width - 25, 0.5, 1);
		link.setColorRef(color1);
		link.setSize(11, 7);
		this.addChild(link);

		closeBtn = new GImage(APPGuiCommon.TEXTURE_1, new RenderTexutreFrame(32, 11, 6, 6, 256, 256));
		closeBtn.setColorRef(color1);
		closeBtn.setPosition(4, 1, 1);

		shutDownBtn = new GImage(APPGuiCommon.TEXTURE_1, new RenderTexutreFrame(24, 11, 7, 6, 256, 256));
		shutDownBtn.setColorRef(color1);
		shutDownBtn.setPosition(4, 1, 1);

		closeBtn.setInteractor(new BtnInteractor(color1, color2) {
			@Override
			public void onClick() {
				gui.onCloseCurrAPP();
			}
		});
		shutDownBtn.setInteractor(new BtnInteractor(color1, color2) {
			@Override
			public void onClick() {
				gui.onCloseComputer();
			}
		});

		if (gui.isRootApp()) this.addChild(shutDownBtn);
		else this.addChild(closeBtn);

	}

}
