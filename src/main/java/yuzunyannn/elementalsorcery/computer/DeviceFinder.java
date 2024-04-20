package yuzunyannn.elementalsorcery.computer;

import java.util.ConcurrentModificationException;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceEnv;
import yuzunyannn.elementalsorcery.api.util.target.WorldLocation;
import yuzunyannn.elementalsorcery.util.Stopwatch;

public class DeviceFinder {

	protected static class Runtime {
		Iterator<TileEntity> iter;
		int offsetIndex;
		int cooldown;

		void nextChunk(WorldLocation location, World world) {
			do {
				int offsetIndex = this.offsetIndex;
				this.offsetIndex = this.offsetIndex + 1;
				if (this.offsetIndex >= 9) {
					this.offsetIndex = 0;
					this.cooldown = 5;
				}
				ChunkPos cPos = location.getChunkPos();
				int x = 0;
				int z = 0;
				if (offsetIndex >= 1 && offsetIndex <= 4) {
					x = (offsetIndex - 3) % 2;// 0 -1 0 1
					z = (2 - offsetIndex) % 2;// 1 0 -1 0
				} else if (offsetIndex >= 5 && offsetIndex <= 8) {
					x = offsetIndex >= 7 ? -1 : 1;// 1 1 -1 -1
					z = -1 * (offsetIndex + 1) % 2 + offsetIndex % 2;// 1 -1 1 -1
				}
//				System.out.println(x + ":" + z);
				Chunk chunk = world.getChunkProvider().getLoadedChunk(cPos.x + x, cPos.z + z);
				if (chunk == null) continue;
				if (chunk.getTileEntityMap().isEmpty()) continue;
				iter = chunk.getTileEntityMap().values().iterator();
				break;
			} while (cooldown <= 0);
		}

		public TileEntity checkAndGetNext() {
			if (iter == null) return null;
			try {
				if (iter.hasNext()) return iter.next();
				iter = null;
				return null;
			} catch (ConcurrentModificationException e) {
				iter = null;
				return null;
			}
		}

	}

	public final UUID uuid;
	public long ts = System.currentTimeMillis();
	public int tick;
	protected final LinkedList<IDeviceAsker> askers = new LinkedList<>();
	protected final Map<IDeviceAsker, Runtime> rMap = new IdentityHashMap<>();
	protected boolean isClose;

	public DeviceFinder(UUID target) {
		this.uuid = target;
	}

	public DeviceFinder join(IDeviceAsker asker) {
		askers.add(asker);
		return this;
	}

	public void leave(IDeviceAsker asker) {
		askers.remove(asker);
		rMap.remove(asker);
	}

	public final boolean isEmpty() {
		return askers.isEmpty();
	}

	public final boolean isClose() {
		return isClose;
	}

	public long getWaitedTime() {
		return System.currentTimeMillis() - ts;
	}

	public boolean isOvertime() {
		if (getWaitedTime() >= 16 * 1000) return true;
		return tick >= 30 * 20;
	}

	public void update(int dt, Stopwatch stopwatch) {
		tick += dt;
		Iterator<IDeviceAsker> iter = askers.iterator();
		while (iter.hasNext()) {
			IDeviceAsker asker = iter.next();
			if (asker.isUnconcerned()) {
				iter.remove();
				if (askers.isEmpty()) close();
				continue;
			}
			stopwatch.start();
			update(dt, asker, stopwatch);
			stopwatch.stop();
			if (isClose) break;
			if (stopwatch.msBiggerThan(5)) break;
		}
	}

	protected void update(int dt, IDeviceAsker asker, Stopwatch stopwatch) {

		Runtime runtime = rMap.get(asker);
		if (runtime == null) rMap.put(asker, runtime = new Runtime());

		if (runtime.cooldown > 0) {
			runtime.cooldown -= dt;
			return;
		}

		WorldLocation localtion = asker.where();
		if (localtion == null) return;

		WorldServer world = DimensionManager.getWorld(localtion.getDimension());
		if (world == null) return;

		while (true) {
			if (stopwatch.msLessThan(5, 0.2)) break;
			TileEntity tile = runtime.checkAndGetNext();
			if (tile == null) {
				runtime.nextChunk(localtion, world);
				tile = runtime.checkAndGetNext();
				if (tile == null) break;
			}
			IDevice device = tile.getCapability(Computer.DEVICE_CAPABILITY, null);
			if (device == null) continue;
			if (!this.uuid.equals(device.getUDID())) continue;
			IDeviceEnv env = device.getEnv();
			if (env == null) env = new ComputerEnvTile(tile);
			finsh(env);
			break;
		}

	}

	public void finsh(IDeviceEnv env) {
		for (IDeviceAsker asker : askers) {
			if (asker.isUnconcerned()) continue;
			asker.onFind(env);
		}
		askers.clear();
		isClose = true;
	}

	public void close() {
		for (IDeviceAsker asker : askers) {
			if (asker.isUnconcerned()) continue;
			asker.onFindFailed();
		}
		askers.clear();
		isClose = true;
	}

}
