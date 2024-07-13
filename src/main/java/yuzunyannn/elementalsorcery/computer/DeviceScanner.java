package yuzunyannn.elementalsorcery.computer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceEnv;
import yuzunyannn.elementalsorcery.api.util.IAliveStatusable;
import yuzunyannn.elementalsorcery.api.util.render.IDisplayable;
import yuzunyannn.elementalsorcery.api.util.target.CapabilityObjectRef;
import yuzunyannn.elementalsorcery.api.util.target.IWorldObject;
import yuzunyannn.elementalsorcery.computer.soft.display.DTCReuntime;
import yuzunyannn.elementalsorcery.computer.soft.display.DeviceScanDisplay;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.logics.EventServer;
import yuzunyannn.elementalsorcery.util.Stopwatch;

public class DeviceScanner implements IDisplayable, IAliveStatusable {

	protected static final int MAX_SCAN_TICK = 80 + WideNetwork.SAY_HELLO_INTERVAL;
	protected int tick;
	protected int scanRange = 16;
	protected boolean isFinish;
	protected final IWorldObject wo;
	protected int checkIndex = 0;
	protected int cooldown = 0;
	protected final List<ChunkPos> checkChunks = new ArrayList<>();
	protected final Set<UUID> findedSet = new HashSet<>();
	protected final List<BiConsumer<CapabilityObjectRef, IDevice>> listeners = new LinkedList<>();

	public DeviceScanner(IWorldObject wo) {
		this.wo = wo;
		Vec3d vec = this.wo.getObjectPosition();
		ChunkPos pos = new ChunkPos(new BlockPos(vec));
		int thunkRange = scanRange >> 4;
		for (int x = -thunkRange; x <= thunkRange; x++) {
			for (int z = -thunkRange; z <= thunkRange; z++) {
				checkChunks.add(new ChunkPos(pos.x + x, pos.z + z));
			}
		}
		Collections.shuffle(checkChunks);
	}

	public void addListener(BiConsumer<CapabilityObjectRef, IDevice> consumer) {
		listeners.add(consumer);
	}

	public boolean isFinish() {
		return isFinish;
	}

	public void close() {
		this.isFinish = true;
	}

	@Override
	public boolean isAlive() {
		return !this.isFinish;
	}

	public void helloWorld(IDeviceEnv env) {
		BlockPos pos = env.getBlockPos();
		World world = env.getWorld();
		if (world != wo.getWorld()) return;
		if (pos.distanceSq(wo.getPosition()) > scanRange * scanRange) return;
		addRef(env.createRef());
	}

	public void addRef(CapabilityObjectRef ref) {
		IDevice device = ref.getCapability(Computer.DEVICE_CAPABILITY, null);
		if (device == null) return;

		UUID udid = device.getUDID();
		if (findedSet.contains(udid)) return;

		findedSet.add(udid);
		for (BiConsumer<CapabilityObjectRef, IDevice> listener : listeners) listener.accept(ref, device);
	}

	public boolean update(int dt) {
		if (isFinish) return false;
		if (!wo.isAlive()) return false;
		tick = tick + dt;
		for (int i = 0; i < dt; i++) onTickUpdate();
		return tick < MAX_SCAN_TICK;
	}

	public Set<UUID> getFindedSet() {
		return findedSet;
	}

	Iterator<TileEntity> iter;

	public void onTickUpdate() {
		if (checkChunks.isEmpty()) return;

		if (cooldown > 0) {
			cooldown--;
			return;
		}

		World world = wo.getWorld();

		if (iter == null) {
			if (checkIndex >= checkChunks.size()) {
				cooldown = 40;
				checkIndex = 0;
				return;
			}
			ChunkPos pos = checkChunks.get(checkIndex);
			checkIndex = checkIndex + 1;
			Chunk chunk = world.getChunk(pos.x, pos.z);
			if (chunk != null) iter = chunk.getTileEntityMap().values().iterator();
		}

		Stopwatch bigComputeWatch;
		if (world.isRemote) bigComputeWatch = EventClient.bigComputeWatch;
		else bigComputeWatch = EventServer.bigComputeWatch;
		bigComputeWatch.start();

		try {
			int maxCheck = 32;
			while (iter.hasNext()) {
				if (maxCheck-- <= 0) break;
				TileEntity tile = iter.next();
				if (bigComputeWatch.msBiggerThan(0.1)) break;
				IDevice device = tile.getCapability(Computer.DEVICE_CAPABILITY, null);
				if (device == null) continue;
				WideNetwork.instance.helloWorld(device, new ComputerEnvTile(tile));
			}
			if (!iter.hasNext()) iter = null;
		} catch (ConcurrentModificationException e) {

		} catch (Exception e) {
			ESAPI.logger.warn("DeviceScanner error", e);
		}

		bigComputeWatch.stop();

//		world.getChunk(0, 0).getTileEntityMap();
		// todo
	}

	@Override
	public Object toDisplayObject() {
		DeviceScanDisplay display = new DeviceScanDisplay();
		display.setCondition(new DTCReuntime(this));
		display.setDigest("DS:" + System.identityHashCode(this));
		this.addListener((ref, device) -> display.onFind(ref, device));
		return display;
	}
}
