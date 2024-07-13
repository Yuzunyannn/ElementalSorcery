package yuzunyannn.elementalsorcery.computer.softs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.computer.DNRequest;
import yuzunyannn.elementalsorcery.api.computer.DNResult;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceInfo;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGuiRuntime;
import yuzunyannn.elementalsorcery.api.util.render.RenderRect;
import yuzunyannn.elementalsorcery.api.util.render.RenderTexutreFrame;
import yuzunyannn.elementalsorcery.api.util.target.CapabilityObjectRef;
import yuzunyannn.elementalsorcery.computer.DeviceScanner;
import yuzunyannn.elementalsorcery.computer.render.GCloseBtn;
import yuzunyannn.elementalsorcery.computer.render.GDisplayObject;
import yuzunyannn.elementalsorcery.computer.render.GDragContainer;
import yuzunyannn.elementalsorcery.computer.render.GEasyLayoutContainer;
import yuzunyannn.elementalsorcery.computer.render.GImgBtn;
import yuzunyannn.elementalsorcery.computer.render.GLabelHover;
import yuzunyannn.elementalsorcery.computer.render.GRefreshBtn;
import yuzunyannn.elementalsorcery.computer.render.SoftGuiCommon;
import yuzunyannn.elementalsorcery.computer.render.SoftGuiThemePart;
import yuzunyannn.elementalsorcery.computer.render.TaskGuiCommon;
import yuzunyannn.elementalsorcery.computer.soft.OPSender;
import yuzunyannn.elementalsorcery.nodegui.GImage;
import yuzunyannn.elementalsorcery.nodegui.GImageBatch;
import yuzunyannn.elementalsorcery.nodegui.GLabel;
import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.nodegui.GScissor;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.helper.NBTSender;

@SideOnly(Side.CLIENT)
public class TaskNetworkGui extends TaskGuiCommon<TaskNetwork> {

	public static UUID lastCacheUDID;
	public static TaskNetwork.ScanCache globalCache = new TaskNetwork.ScanCache();

	protected double lWidth = 60;
	protected GEasyLayoutContainer linkersContainer;
	protected GEasyLayoutContainer lcLinked;
	protected GEasyLayoutContainer lcOther;
	protected GImageBatch screenBatch;
	protected double screenScale = 2;
	protected GEasyLayoutContainer detailContainer;
	protected GRefreshBtn refreshBtn;
	protected final Map<UUID, TaskNetworkGLinkerInfo> linkerNodeMap = new HashMap<>();
	protected final TaskNetwork.ScanCache cache;

	public TaskNetworkGui(TaskNetwork appInst) {
		super(appInst);
		// 这样写是为了app实例在item，导致经常变化的处理
		if (appInst.scanCache != null) cache = appInst.scanCache;
		else {
//			lastCacheUDID = null;
			UUID uuid = appInst.getOS().getDeviceUUID();
			if (uuid.equals(lastCacheUDID)) cache = globalCache;
			else {
				cache = globalCache = new TaskNetwork.ScanCache();
				lastCacheUDID = uuid;
			}
		}
		appInst.scanCache = cache;
	}

	@Override
	protected void onInit(ISoftGuiRuntime runtime) {
		super.onInit(runtime);

		Color color1 = this.getThemeColor(SoftGuiThemePart.BACKGROUND_1);

		Color color2 = this.getThemeColor(SoftGuiThemePart.BACKGROUND_2);
		Color colorObj2 = this.getThemeColor(SoftGuiThemePart.OBJECT_2);
		Color colorObj2Active = this.getThemeColor(SoftGuiThemePart.OBJECT_2_ACTIVE);

		int rightMore = 16;
		GImage screen = new GImage(SoftGuiCommon.TEXTURE_1, new RenderTexutreFrame(42, 31, 10, 10, 256, 256));
		screen.setSize(cWidth - lWidth * 2 - rightMore, cHeight - 2);
		screen.setPosition(lWidth, 1);
		bg.addChild(screen);

		GScissor scissor = new GScissor(new RenderRect(0, screen.getHeight(), 0, screen.getWidth()));
		screen.addChild(scissor);

		screenBatch = new GImageBatch(SoftGuiCommon.TEXTURE_1);
		scissor.addChild(screenBatch);
		screenBatch.setSize(scissor.getSize());

		GImage compass = new GImage(SoftGuiCommon.TEXTURE_1, SoftGuiCommon.FRAME_ICON_COMPASS);
		scissor.addChild(compass);
		compass.setPosition(1, scissor.getHeight() - 14, 200);
		compass.setSize(13, 13);

		GImage rightBar = new GImage(SoftGuiCommon.TEXTURE_1, FRAME_P2_RIGHT);
		rightBar.setColorRef(color1);
		rightBar.setSplit9();
		rightBar.setSize(lWidth + rightMore, cHeight);
		rightBar.setPosition(cWidth - lWidth - rightMore, 0);
		bg.addChild(rightBar);

		GDragContainer infoScorll = new GDragContainer(lWidth - 4 + rightMore, cHeight - 2);
		rightBar.addChild(infoScorll);
		infoScorll.setPosition(2, 1);

		detailContainer = new GEasyLayoutContainer();
		detailContainer.setMaxWidth(infoScorll.getWidth());
		detailContainer.setMargin(new RenderRect(2, 0, 0, 0));
		infoScorll.addContainer(detailContainer);

		GImage leftBar = new GImage(SoftGuiCommon.TEXTURE_1, FRAME_P2_LEFT);
		leftBar.setColorRef(color2);
		leftBar.setSplit9();
		leftBar.setSize(lWidth, cHeight);
		bg.addChild(leftBar);

		GImage closeBtn = new GCloseBtn(() -> onCloseCurrAPP(), colorObj2, colorObj2Active);
		closeBtn.setPosition(6, 6, 1);
		bg.addChild(closeBtn);

		refreshBtn = new GRefreshBtn(() -> onRefreshBtnClick(), colorObj2, colorObj2Active);
		refreshBtn.setPosition(lWidth - 6, 6, 1);
		bg.addChild(refreshBtn);

		GDragContainer linkScorll = new GDragContainer(lWidth - 4, cHeight - 10 - 1);
		leftBar.addChild(linkScorll);
		linkScorll.setPosition(2, 10);

		GImage line = new GImage(SoftGuiCommon.TEXTURE_1, FRAME_L1_H);
		line.setSplit9();
		line.setSize(linkScorll.getWidth(), 1);
		line.setPosition(0, 0);
		line.setColorRef(color2);
		linkScorll.addChild(line);

		linkersContainer = new GEasyLayoutContainer();
		linkersContainer.setMaxWidth(linkScorll.getWidth());
		linkScorll.addContainer(linkersContainer);

		lcLinked = new GEasyLayoutContainer();
		lcOther = new GEasyLayoutContainer();
		lcLinked.setMaxWidth(linkScorll.getWidth());
		lcOther.setMaxWidth(linkScorll.getWidth());
		lcLinked.setMargin(new RenderRect(2, 0, 0, 0));
		lcOther.setMargin(new RenderRect(2, 0, 0, 0));
		linkersContainer.addChild(lcLinked);
		linkersContainer.addChild(lcOther);

		Map<UUID, CapabilityObjectRef> deviceMap = cache.getDeviceMap();
		for (UUID udid : appInst.getNetworkLinkedMap().keySet()) addLinkerShow(getDeviceUnitInfo(udid));
		for (UUID udid : deviceMap.keySet()) addLinkerShow(getDeviceUnitInfo(udid));

		linkersContainer.layout();

		if (cache.getScanner() == null) onRefreshBtnClick();
	}

	@Nullable
	protected DeviceLinkUnitInfo getDeviceUnitInfo(UUID udid) {
		DeviceLinkUnitInfo info = appInst.getNetworkLinkedMap().get(udid);
		if (info != null) return info.copy();
		Map<UUID, CapabilityObjectRef> deviceMap = cache.getDeviceMap();
		CapabilityObjectRef ref = deviceMap.get(udid);
		return ref == null ? null : new DeviceLinkUnitInfo(udid, ref);
	}

	protected boolean addLinkerShow(DeviceLinkUnitInfo info) {
		if (linkerNodeMap.containsKey(info.udid)) return false;

		IDevice device = info.getDevice();
		if (device == null && !info.isInLink()) return false;

		TaskNetworkGLinkerInfo node = new TaskNetworkGLinkerInfo(this, info);
		if (info.isInLink()) lcLinked.addChild(node);
		else lcOther.addChild(node);
		linkerNodeMap.put(info.udid, node);
		GNode mapNode = node.getMapNode();
		mapNode.setSize(screenScale, screenScale);
		screenBatch.addChild(mapNode);

		return true;

	}

	protected void onDeviceRefIsLost(DeviceLinkUnitInfo oInfo) {
		if (oInfo.isInLink()) return;
		Map<UUID, CapabilityObjectRef> deviceMap = cache.getDeviceMap();
		deviceMap.remove(oInfo.udid);
		TaskNetworkGLinkerInfo node = linkerNodeMap.get(oInfo.udid);
		if (node == null) return;
		node.removeFromParent();
		linkerNodeMap.remove(oInfo.udid);
		linkersContainer.layout();
	}

	protected void onRefreshBtnClick() {
		if (nextRefreshTick > checkTick) return;
		nextRefreshTick = checkTick + 40;
		refreshBtn.setRefreshing(true);

		IOS os = appInst.getOS();
		DNResult result = os.notice(null, "network-scan", DNRequest.empty());

		refreshBtn.setRefreshing(false);
		if (!result.isSuccess()) {
			tip("es.app.scanFail");
			return;
		}
		DeviceScanner scanner = cache.getScanner();
		if (scanner != null) scanner.close();
		scanner = result.getReturn(DeviceScanner.class);
		if (scanner == null) {
			tip("es.app.scanFail");
			return;
		}

		cache.setScanner(scanner);
		scanner.addListener((ref, d) -> onScannerNewRef(ref, d));
	}

	protected void onScannerNewRef(CapabilityObjectRef ref, IDevice device) {
		IOS os = appInst.getOS();
		UUID myUdid = os.getDeviceUUID();
		UUID udid = device.getUDID();
		if (myUdid.equals(udid)) return;

		Map<UUID, CapabilityObjectRef> deviceMap = cache.getDeviceMap();
		if (deviceMap.containsKey(udid)) {
			DeviceLinkUnitInfo info = getDeviceUnitInfo(udid);
			addLinkerShow(info);
			return;
		}
		if (deviceMap.size() > 128) return;

		deviceMap.put(udid, ref);
		DeviceLinkUnitInfo info = getDeviceUnitInfo(udid);

		addLinkerShow(info);
		linkersContainer.layout();
	}

	protected DeviceLinkUnitInfo currShowUnit;
	protected UUID prevShowUUID;
	protected GNode currShowNode;
	protected GLabel currShowStatusNode;

	public void doSelect(DeviceLinkUnitInfo oInfo) {
		DeviceLinkUnitInfo info = getDeviceUnitInfo(oInfo.udid);
		if (info == null) return;

		if (info.isInLink()) {
			currShowUnit = oInfo;
			return;
		}

		IDevice device = info.getDevice();
		if (device == null) return;

		currShowUnit = oInfo;
		prevShowUUID = null;
	}

	protected void onLinkBtnClick() {
		if (currShowUnit == null) return;
		if (currShowUnit.isInLink()) {
			if (currShowUnit.status == DeviceLinkUnitInfo.STATUS_DISCONNECTING) return;
			currShowUnit.status = DeviceLinkUnitInfo.STATUS_DISCONNECTING;
			updateShowStatus();
			OPSender sender = new OPSender("disconnect");
			sender.write(sender.args(1), currShowUnit.udid);
			runtime.sendOperation(sender.tag());
		} else {
			if (currShowUnit.status == DeviceLinkUnitInfo.STATUS_WAITING) return;
			currShowUnit.status = DeviceLinkUnitInfo.STATUS_WAITING;
			updateShowStatus();
			OPSender sender = new OPSender("handshake");
			sender.write(sender.args(1), currShowUnit.udid);
			sender.write(sender.args(2), currShowUnit.ref);
			runtime.sendOperation(sender.tag());
		}
	}

	private void updateShowStatus() {
		String statusString;
		int status = currShowUnit.getStatus();
		if (status == DeviceLinkUnitInfo.STATUS_CONNECT)
			statusString = TextFormatting.DARK_GREEN + I18n.format("es.app.status.linked");
		else if (status == DeviceLinkUnitInfo.STATUS_RECONNECTING)
			statusString = TextFormatting.GOLD + I18n.format("es.app.status.relinked");
		else if (status == DeviceLinkUnitInfo.STATUS_DISCONNECTING)
			statusString = TextFormatting.GOLD + I18n.format("es.app.status.disconnecting");
		else if (status == DeviceLinkUnitInfo.STATUS_WAITING)
			statusString = TextFormatting.GOLD + I18n.format("es.app.status.waitLinked");
		else statusString = I18n.format("es.app.status.unlinked");
		currShowStatusNode.setString(I18n.format("es.app.status", statusString));
	}

	protected void onInitSelectDetail() {
		Color colorObj1 = this.getThemeColor(SoftGuiThemePart.OBJECT_1);
		Color colorObj1Active = this.getThemeColor(SoftGuiThemePart.OBJECT_1_ACTIVE);
		int wrapWidth = (int) detailContainer.getMaxWidth();

		{
			currShowStatusNode = new GLabel();
			currShowStatusNode.setWrapWidth(wrapWidth);
			currShowStatusNode.setColorRef(colorObj1);
			detailContainer.addChild(currShowStatusNode);
			updateShowStatus();
		}

		{
			String fullUDIDStr = currShowUnit.udid.toString();
			String udidStr = fullUDIDStr.substring(0, 8) + "...";
			GLabelHover label = new GLabelHover(I18n.format("es.app.id.device") + udidStr);
			label.setWrapWidth(wrapWidth);
			label.setColorRef(colorObj1);
			label.setHoverText(fullUDIDStr);
			label.setRuntime(runtime);
			detailContainer.addChild(label);
		}

		List<Object> list = new LinkedList<>();

		IDevice device = currShowUnit.getDevice();
		if (device != null) {
			IDeviceInfo info = device.getInfo();
			String workName = info.getDisplayWorkName();
			if (workName == null || workName.isEmpty()) workName = "???";
			list.add("<" + workName + ">");
			info.addInformation(list);
		}

		GDisplayObject gdo = new GDisplayObject();
		gdo.setEveryLine(true);
		gdo.setColorRef(colorObj1);
		gdo.setDisplayObject(list);
		gdo.setPositionZ(10);
		detailContainer.addChild(gdo);

		detailContainer.layout();

		GImgBtn imgBtn;
		RenderTexutreFrame frame = SoftGuiCommon.FRAME_ICON_UNLINK;
		if (currShowUnit.isInLink()) frame = SoftGuiCommon.FRAME_ICON_LINK;
		imgBtn = new GImgBtn(TEXTURE_1, frame, () -> onLinkBtnClick(), colorObj1, colorObj1Active);
		currShowNode = imgBtn;
		imgBtn.setRuntime(runtime);
		if (currShowUnit.isInLink()) imgBtn.setHoverText(I18n.format("es.app.disconnect"));
		else imgBtn.setHoverText(I18n.format("es.app.connect"));
		currShowNode.setPosition(detailContainer.getMaxWidth() - 6, 6);
		detailContainer.addChild(imgBtn);
	}

	protected void onCloseSelectDetail() {
		if (currShowNode != null) {
			currShowNode.removeFromParent();
			currShowNode = null;
		}
		detailContainer.removeAllChild();
		detailContainer.layout();
	}

	protected int nextRefreshTick = 0;
	protected int checkTick = 0;

	@Override
	public void update() {
		super.update();
		checkTick++;
		updateScanner();
		updateDetailShow();
	}

	protected final void updateDetailShow() {
		if (currShowUnit != null) {
			if (currShowUnit.isInLink()) {

			} else if (!currShowUnit.checkRefIsValid()) {
				onDeviceRefIsLost(currShowUnit);
				currShowUnit = null;
				return;
			}

			if (!currShowUnit.udid.equals(prevShowUUID)) {
				prevShowUUID = currShowUnit.udid;
				onCloseSelectDetail();
				onInitSelectDetail();
			}
			return;
		}

		if (currShowNode != null) onCloseSelectDetail();
	}

	protected final void updateScanner() {
		DeviceScanner scanner = cache.getScanner();
		if (scanner == null) return;
		if (scanner.isFinish()) {
			cache.setScanner(null);
			refreshBtn.setClickEnabled(true);
			refreshBtn.setRefreshing(false);
		} else {
			refreshBtn.setClickEnabled(false);
			refreshBtn.setRefreshing(true);
		}
	}

	public void changeStatus(UUID udid, int status) {
		TaskNetworkGLinkerInfo node = linkerNodeMap.get(udid);
		boolean isLinkGroupChange = false;
		DeviceLinkUnitInfo info;
		if (node == null) {
			info = getDeviceUnitInfo(udid);
			if (info == null) return;
			info.status = status;
			if (!info.isInLink() && !info.checkRefIsValid()) {
				onDeviceRefIsLost(info);
				return;
			}
			isLinkGroupChange = true;
			addLinkerShow(info);
			linkersContainer.layout();
		} else {
			info = node.oInfo;
			boolean isInLink = info.isInLink();
			info.status = status;
			if (isInLink != info.isInLink()) {
				isLinkGroupChange = true;
				node.removeFromParent();
				linkerNodeMap.remove(info.udid);
				addLinkerShow(info);
				linkersContainer.layout();
			}
			if (info.isInLink()) {
				DeviceLinkUnitInfo nifo = getDeviceUnitInfo(udid);
				isLinkGroupChange = isLinkGroupChange || info.ref.isInvalid();
				info.ref = nifo.ref;
				info.ref.restore(Minecraft.getMinecraft().world);
				node = linkerNodeMap.get(info.udid);
				if (node != null) node.refresh();
			}
		}
		if (currShowUnit != null && currShowUnit.udid.equals(udid)) {
			if (isLinkGroupChange) {
				currShowUnit = info;
				onCloseSelectDetail();
				onInitSelectDetail();
			} else updateShowStatus();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onRecvMessage(NBTTagCompound nbt) {
		super.onRecvMessage(nbt);
		NBTSender sender = new NBTSender(nbt);
		int code = sender.nbyte("code");
		UUID udid = sender.uuid("udid");
		if (code == -1) {
			changeStatus(udid, DeviceLinkUnitInfo.STATUS_UNLINK);
			tip(I18n.format("es.app.connect.fail") + " " + udid.toString());
		} else if (code == -2) {
			changeStatus(udid, DeviceLinkUnitInfo.STATUS_CONNECT);
			tip(I18n.format("es.app.connect.fail") + " " + udid.toString());
		} else if (code == 2) {
			DeviceLinkUnitInfo info = getDeviceUnitInfo(udid);
			if (info != null) changeStatus(info.udid, info.getStatus());
			else changeStatus(udid, DeviceLinkUnitInfo.STATUS_UNLINK);
		}
	}

}
