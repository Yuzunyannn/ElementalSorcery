package yuzunyannn.elementalsorcery.computer.softs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import net.minecraft.nbt.NBTTagList;
import yuzunyannn.elementalsorcery.api.computer.IDeviceLinker;
import yuzunyannn.elementalsorcery.api.computer.IDeviceNetwork;
import yuzunyannn.elementalsorcery.api.util.detecter.IDataDetectable;
import yuzunyannn.elementalsorcery.api.util.detecter.IDataRef;
import yuzunyannn.elementalsorcery.util.helper.NBTSender;

public class DDDeviceNetwork implements IDataDetectable<Map<UUID, DeviceLinkUnitInfo>, NBTTagList> {

	protected IDeviceNetwork network;
	protected Map<UUID, DeviceLinkUnitInfo> mMap = new HashMap();
	public Consumer<UUID> onDeviceLinkCangeCallback;

	public Map<UUID, DeviceLinkUnitInfo> getDataMap() {
		return mMap;
	}

	public void setNetwork(IDeviceNetwork network) {
		this.network = network;
	}

	@Override
	public NBTTagList detectChanges(IDataRef<Map<UUID, DeviceLinkUnitInfo>> templateRef) {
		if (network == null) return null;
		Map<UUID, DeviceLinkUnitInfo> map = templateRef.get();
		if (map == null) templateRef.set(map = new HashMap());

		NBTTagList changeList = new NBTTagList();

		Iterator<UUID> iter = map.keySet().iterator();
		while (iter.hasNext()) {
			UUID udid = iter.next();
			IDeviceLinker linker = network.getLinker(udid);
			if (linker == null || linker.isClose()) {
				NBTSender.SHARE.write("D", udid);
				changeList.appendTag(NBTSender.SHARE.spitOut());
				iter.remove();
			}
		}

		for (IDeviceLinker linker : network.getLinkers()) {
			if (linker.isClose()) continue;
			if (linker.isLocal()) continue;
			DeviceLinkUnitInfo newInfo = new DeviceLinkUnitInfo(linker);
			DeviceLinkUnitInfo info = map.get(linker.getRemoteUUID());
			if (info == null || newInfo.status != info.status) {
				map.put(newInfo.udid, newInfo);
				NBTSender.SHARE.write("S", (byte) newInfo.status);
				NBTSender.SHARE.write("U", newInfo.udid);
				if (newInfo.status == DeviceLinkUnitInfo.STATUS_CONNECT) NBTSender.SHARE.write("R", newInfo.ref);
				changeList.appendTag(NBTSender.SHARE.spitOut());
			}
		}

		return changeList.isEmpty() ? null : changeList;
	}

	@Override
	public void mergeChanges(NBTTagList changeList) {
		for (int i = 0; i < changeList.tagCount(); i++) {
			NBTSender reader = new NBTSender(changeList.getCompoundTagAt(i));
			if (reader.has("D")) {
				UUID udid = reader.uuid("D");
				mMap.remove(udid);
				if (onDeviceLinkCangeCallback != null) onDeviceLinkCangeCallback.accept(udid);
				continue;
			}
			UUID udid = reader.uuid("U");
			DeviceLinkUnitInfo info = mMap.get(udid);
			if (info == null) mMap.put(udid, info = new DeviceLinkUnitInfo(udid));
			info.status = reader.nbyte("S");
			if (reader.has("R")) info.ref = reader.capabilityObjectRef("R");
			if (onDeviceLinkCangeCallback != null) onDeviceLinkCangeCallback.accept(udid);
		}
	}
}
