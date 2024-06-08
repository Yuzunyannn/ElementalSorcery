package yuzunyannn.elementalsorcery.computer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.WeakHashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.computer.DNRequest;
import yuzunyannn.elementalsorcery.api.computer.DNResult;
import yuzunyannn.elementalsorcery.api.computer.DeviceNetworkRoute;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceEnv;
import yuzunyannn.elementalsorcery.api.computer.IDeviceLinker;
import yuzunyannn.elementalsorcery.api.computer.IDeviceNetwork;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.api.util.detecter.ISyncDetectable;
import yuzunyannn.elementalsorcery.api.util.detecter.ISyncWatcher;
import yuzunyannn.elementalsorcery.computer.exception.ComputerConnectException;

public class DeviceNetwork
		implements IDeviceNetwork, ISyncDetectable<NBTTagCompound>, INBTSerializable<NBTTagCompound> {

	public static int MAX_CONNECT_COUNT = 16;

	protected Map<UUID, IDeviceLinker> linkerMap = new HashMap<>();
	protected IDevice mySelf;
	protected IDeviceLinker selfLinker;
	protected WeakHashMap<UUID, Boolean> helplessMap = new WeakHashMap();
	protected int tick;

	public DeviceNetwork(IDevice device) {
		this.mySelf = device;
		setSelfLinker(new DeviceLinkerSelf(this));
	}

	protected void init(Collection<UUID> uuids) {
		linkerMap.clear();
		if (uuids != null) {
			for (UUID udid : uuids) linkerMap.put(udid, new DeviceLinker(this, udid));
		}
		resetSelfLinker();
	}

	public void resetSelfLinker() {
		setSelfLinker(this.selfLinker);
	}

	public void setSelfLinker(IDeviceLinker selfLinker) {
		this.selfLinker = selfLinker;
		if (selfLinker == null) linkerMap.remove(mySelf.getUDID());
		else linkerMap.put(mySelf.getUDID(), selfLinker);
	}

	public void clear() {
		linkerMap.clear();
		resetSelfLinker();
	}

	@Override
	public IDevice getDevice() {
		return mySelf;
	}

	@Override
	public boolean handshake(IDevice other, IDeviceEnv otherEnv, boolean simulate) throws ComputerConnectException {
		if (linkerMap.size() > MAX_CONNECT_COUNT) return false;
		if (simulate) return true;
		UUID udid = other.getUDID();
		helplessMap.remove(udid);
		IDeviceLinker linker = linkerMap.get(udid);
		if (linker != null && !linker.isClose()) return true;
		linkerMap.put(udid, linker = new DeviceLinker(this, otherEnv.createRef()));
		IDeviceEnv env = mySelf.getEnv();
		if (env != null) env.markDirty();
		return true;
	}

	@Override
	public DNResult notice(DeviceNetworkRoute route, String method, DNRequest request) {
		request.setSrcDevice(mySelf);
		if (route.isLocal()) return mySelf.notice(method, request);
		UUID uuid = route.next();
		IDeviceLinker linker = linkerMap.get(uuid);
		if (linker != null) {
			if (!linker.isConnecting()) return DNResult.unavailable();
			return linker.getRemoteDevice().getNetwork().notice(route, method, request);
		}
		// TODO find mod
		return DNResult.invalid();
	}

	@Override
	public boolean isDiscoverable() {
		return true;
	}

	@Override
	public IDeviceLinker getLinker(UUID uuid) {
		return linkerMap.get(uuid);
	}

	@Override
	public Collection<IDeviceLinker> getLinkers() {
		return linkerMap.values();
	}

	public void update(IDeviceEnv env) {
		tick++;

		if (tick % WideNetwork.SAY_HELLO_INTERVAL == 0) WideNetwork.instance.helloWorld(mySelf, env);
		if (env.isRemote()) return;

		if (tick % 20 != 0) return;

		int linkerSize = linkerMap.size();
		Iterator<Entry<UUID, IDeviceLinker>> iter = linkerMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<UUID, IDeviceLinker> entry = iter.next();
			IDeviceLinker linker = entry.getValue();
			if (linker.isClose()) {
				iter.remove();
				continue;
			}
			boolean isRemoved = false;
			try {
				if (!linker.isConnecting()) {
					boolean isContinue = linker.onDisconnectTick(env, 20);
					if (!isContinue) isRemoved = true;
				} else linker.onConnectTick(env, 20);
			} catch (ComputerConnectException e) {
				if (ESAPI.isDevelop) ESAPI.logger.warn("device connect warn", e);
				isRemoved = true;
			}
			if (isRemoved) {
				helplessMap.put(linker.getRemoteUUID(), true);
				linker.close();
				iter.remove();
				continue;
			} else if (linker.isClose()) {
				iter.remove();
				continue;
			}
		}

		if (linkerSize != linkerMap.size()) env.markDirty();
	}

	@Override
	public boolean isHelpless(UUID udid) {
		return helplessMap.containsKey(udid);
	}

	@Override
	public NBTTagCompound detectChanges(ISyncWatcher watcher) {
		return null;
	}

	@Override
	public void mergeChanges(NBTTagCompound nbt) {

	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagList list = new NBTTagList();
		for (IDeviceLinker linker : linkerMap.values()) {
			NBTTagCompound dat = linker.serializeNBT();
			if (dat != null) list.appendTag(dat);
		}
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("linkers", list);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		tick = 0;
		linkerMap.clear();
		NBTTagList list = nbt.getTagList("linkers", NBTTag.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			IDeviceLinker linker = new DeviceLinker(this, list.getCompoundTagAt(i));
			linkerMap.put(linker.getRemoteUUID(), linker);
		}
		if (this.selfLinker != null) linkerMap.put(selfLinker.getRemoteUUID(), selfLinker);
	}
}
