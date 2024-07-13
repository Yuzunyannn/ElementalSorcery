package yuzunyannn.elementalsorcery.computer.soft.display;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceInfo;
import yuzunyannn.elementalsorcery.api.util.detecter.IDataRef;
import yuzunyannn.elementalsorcery.api.util.target.CapabilityObjectRef;
import yuzunyannn.elementalsorcery.computer.Device;
import yuzunyannn.elementalsorcery.computer.render.GDisplayObject;
import yuzunyannn.elementalsorcery.computer.softs.DDQueue;
import yuzunyannn.elementalsorcery.nodegui.GLabel;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTSSA;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;

public class DeviceScanDisplay extends SoftBlockDisplay<Integer, NBTTagList> {

	public final static String ID = "$ES:DSD";

	static class Info implements INBTSSA {
		UUID uuid;
		CapabilityObjectRef ref;

		@Override
		public void writeSaveData(INBTWriter writer) {
			writer.write("U", uuid);
			writer.write("ref", ref);
		}

		@Override
		public void readSaveData(INBTReader reader) {
			uuid = reader.uuid("U");
			ref = reader.capabilityObjectRef("ref");
		}
	}

	protected DDQueue<Info> ddq = new DDQueue();
	public LinkedList<Info> showList = new LinkedList<>();
	protected int persistentSize;

	public DeviceScanDisplay() {
		super(DeviceScanDisplay.ID);
		ddq.setQueue(showList, () -> new Info());
	}

	public void onFind(CapabilityObjectRef ref, IDevice device) {
		Info info = new Info();
		info.ref = ref;
		info.uuid = device.getUDID();
		showList.add(info);
		ddq.setPersistentSize(++persistentSize);
		if (showList.size() > 8) showList.removeFirst();
	}

	@Override
	public void writeSaveData(INBTWriter writer) {
		super.writeSaveData(writer);
		writer.write("ls", showList);
	}

	@Override
	public void readSaveData(INBTReader reader) {
		super.readSaveData(reader);
		showList = reader.list("ls", showList, () -> new Info());
	}

	@Override
	public void setDead(Side side) {
		super.setDead(side);
		if (side == Side.SERVER) showList.clear();
	}

	@Override
	public NBTTagList detectChanges(IDataRef<Integer> templateRef) {
		return ddq.detectChanges(templateRef);
	}

	@Override
	public void mergeChanges(NBTTagList list) {
		ddq.mergeChanges(list);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void initUI() {
		super.initUI();
		ddq.onElementAdd = info -> addNewElement(info);
		for (Info info : showList) addNewElement(info);
	}

	@SideOnly(Side.CLIENT)
	void addNewElement(Info info) {
		if (container == null) return;
		List<Object> objs = new ArrayList<>();

		if (Minecraft.getMinecraft().world != null) {
			info.ref.restore(Minecraft.getMinecraft().world);
			IDevice device = info.ref.getCapability(Device.DEVICE_CAPABILITY, null);
			IDeviceInfo dinfo = device == null ? null : device.getInfo();
			if (dinfo != null) objs.add(dinfo.getDisplayWorkName());
		}

		objs.add(info.uuid.toString());

		GLabel label = new GLabel("-------------------");
		label.setColorRef(currNode.getColor());
		objs.add(label);

		GDisplayObject dpg = new GDisplayObject();
		dpg.mask = dragContainer;
		dpg.setEveryLine(true);
		dpg.setColorRef(currNode.getColor());
		dpg.setEnableClick(true);
		dpg.setDisplayObject(objs);

		container.addChild(dpg);
		container.layout();
	}

}
