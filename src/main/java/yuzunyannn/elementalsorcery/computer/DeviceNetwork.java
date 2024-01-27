package yuzunyannn.elementalsorcery.computer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceEnv;
import yuzunyannn.elementalsorcery.api.computer.IDeviceLinkTimeoutable;
import yuzunyannn.elementalsorcery.api.computer.IDeviceLinker;
import yuzunyannn.elementalsorcery.api.computer.IDeviceNetwork;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.api.util.detecter.ISyncDetectable;
import yuzunyannn.elementalsorcery.api.util.detecter.ISyncWatcher;
import yuzunyannn.elementalsorcery.computer.exception.ComputerConnectException;

public class DeviceNetwork
		implements IDeviceNetwork, ISyncDetectable<NBTTagCompound>, INBTSerializable<NBTTagCompound> {

	protected Map<UUID, IDeviceLinker> linkerMap = new HashMap<>();
	protected IDevice mySelf;
	protected IDeviceLinker selfLinker;
	protected int tick;

	public DeviceNetwork(IDevice device) {
		this.mySelf = device;
		setSelfLinker(new DeviceLinkerSelf(this));
	}

	public void setSelfLinker(IDeviceLinker selfLinker) {
		this.selfLinker = selfLinker;
		if (selfLinker == null) linkerMap.remove(mySelf.getUDID());
		else linkerMap.put(mySelf.getUDID(), selfLinker);
	}

	@Override
	public IDevice getDevice() {
		return mySelf;
	}

	@Override
	public boolean handshake(IDeviceLinker other) throws ComputerConnectException {
		UUID uuid = other.getRemoteUUID();
		IDeviceLinker linker = linkerMap.get(uuid);
		if (linker != null && !linker.isClose()) return true;
		linkerMap.put(uuid, linker);
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
		if (tick++ % 10 != 0) return;

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
					if (linker instanceof IDeviceLinkTimeoutable) {
						boolean isContinue = ((IDeviceLinkTimeoutable) linker).tryReconnect(env, 10);
						if (!isContinue) isRemoved = true;
					} else {
						boolean isLink = linker.reconnect(env);
						if (!isLink) isRemoved = true;
					}
				}
			} catch (ComputerConnectException e) {
				if (ESAPI.isDevelop) ESAPI.logger.warn("device connect warn", e);
				isRemoved = true;
			}
			if (isRemoved) {
				linker.close();
				iter.remove();
				continue;
			}
		}
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
			IDeviceLinker linker = new DeviceLinker(this, nbt);
			linkerMap.put(linker.getRemoteUUID(), linker);
		}
		if (this.selfLinker != null) linkerMap.put(selfLinker.getRemoteUUID(), selfLinker);
	}
}
