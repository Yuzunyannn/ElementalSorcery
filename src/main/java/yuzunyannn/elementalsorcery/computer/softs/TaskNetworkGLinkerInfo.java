package yuzunyannn.elementalsorcery.computer.softs;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceInfo;
import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGuiRuntime;
import yuzunyannn.elementalsorcery.api.util.client.RenderTexutreFrame;
import yuzunyannn.elementalsorcery.api.util.target.CapabilityObjectRef;
import yuzunyannn.elementalsorcery.computer.render.BtnBaseInteractor;
import yuzunyannn.elementalsorcery.computer.render.SoftGuiCommon;
import yuzunyannn.elementalsorcery.computer.render.SoftGuiThemePart;
import yuzunyannn.elementalsorcery.nodegui.GImage;
import yuzunyannn.elementalsorcery.nodegui.GLabel;
import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.util.helper.Color;

public class TaskNetworkGLinkerInfo extends GNode {

	final DeviceLinkUnitInfo oInfo;
	final TaskNetworkGui gui;
	protected TaskNetworkGMapNode mapNode;
	protected BtnBaseInteractor interactor;
	protected List<String> hoverTexts;
	protected GLabel title;
	protected boolean lastIsRefIsMiss;

	public TaskNetworkGLinkerInfo(TaskNetworkGui gui, DeviceLinkUnitInfo oInfo) {
		this.oInfo = oInfo;
		this.gui = gui;

		IDevice device = oInfo.getDevice();

		if (device == null && oInfo.isInLink()) {
			CapabilityObjectRef ref = oInfo.getRef();
			ref.restore(Minecraft.getMinecraft().world);
			device = oInfo.getDevice();
		}

		Color color2 = gui.getThemeColor(SoftGuiThemePart.BACKGROUND_2);
		Color colorObj2 = gui.getThemeColor(SoftGuiThemePart.OBJECT_2);
		Color colorObj2Hover = gui.getThemeColor(SoftGuiThemePart.OBJECT_2).copy().weight(new Color(0xffffff), 0.25f);
		Color colorObj2Active = gui.getThemeColor(SoftGuiThemePart.OBJECT_2_ACTIVE);

		RenderTexutreFrame iconFrame = SoftGuiCommon.FRAME_ICON_LINK;
		if (!oInfo.isInLink()) iconFrame = SoftGuiCommon.FRAME_ICON_UNLINK;

		GImage linkIcon = new GImage(SoftGuiCommon.TEXTURE_1, iconFrame);
		linkIcon.setColor(colorObj2);
		linkIcon.setPosition(0, 1);
		addChild(linkIcon);

		mapNode = new TaskNetworkGMapNode(this);
		mapNode.setFrame(iconFrame, true);

		title = new GLabel("");
		title.setColorRef(colorObj2);
		title.setPosition(linkIcon.getWidth() + 2, 0);
		addChild(title);
		updateTitle();

		GImage line = new GImage(SoftGuiCommon.TEXTURE_1, SoftGuiCommon.FRAME_L1_H);
		line.setSplit9();
		line.setSize(gui.lWidth - 4, 1);
		line.setPosition(0, linkIcon.getHeight() + 1);
		line.setColorRef(color2);
		addChild(line);

		this.width = gui.lWidth;
		this.height = linkIcon.getHeight() + 2;

		this.setInteractor(interactor = new BtnBaseInteractor() {

			@Override
			public boolean blockMouseEvent(GNode node, Vec3d worldPos) {
				return false;
			}

			@Override
			public void onHoverChange(GNode node) {
				if (isHover) {
					linkIcon.setColorRef(colorObj2Hover);
					title.setColorRef(colorObj2Hover);
				} else {
					linkIcon.setColorRef(colorObj2);
					title.setColorRef(colorObj2);
				}
			}

			@Override
			public void onPressed(GNode node) {
				super.onPressed(node);
				linkIcon.setColorRef(colorObj2Active);
				title.setColorRef(colorObj2Active);
			}

			@Override
			public void onClick() {
				checkAndDrop();
				gui.doSelect(oInfo);
			}
		});
	}

	public void updateTitle() {
		IDevice device = oInfo.getDevice();
		String udid = oInfo.udid.toString();
		String name = udid;
		String hoverText = name;

		if (device != null) {
			IDeviceInfo info = device.getInfo();
			name = info.getName();
			if (name == null || name.isEmpty()) {
				String workdName = info.getDisplayWorkName();
				if (workdName != null && !workdName.isEmpty()) name = workdName;
				else name = udid;
			}
		}

		if (name == null) name = udid;
		if (name != udid) hoverText = name + "\n" + udid;
		if (name.length() > 8) name = name.substring(0, 8) + "...";

		title.setString(name);
		hoverTexts = Arrays.asList(hoverText.split("\n"));
	}

	public GNode getMapNode() {
		return mapNode;
	}

	public DeviceLinkUnitInfo getUnitInfo() {
		return oInfo;
	}

	@Override
	public void update() {
		super.update();
		if (gui.checkTick % 20 == 0) checkAndDrop();
		if (interactor.isHover) gui.getGuiRuntime().setTooltip("item", ISoftGuiRuntime.MOUSE_FOLLOW_VEC, 0, () -> {
			return hoverTexts;
		});
	}

	public void checkAndDrop() {
		if (!oInfo.checkRefIsValid()) {
			if (oInfo.isInLink()) lastIsRefIsMiss = true;
			else gui.onDeviceRefIsLost(oInfo);
		}
	}

	public void refresh() {
		if (lastIsRefIsMiss) lastIsRefIsMiss = false;
		this.updateTitle();
	}

}
