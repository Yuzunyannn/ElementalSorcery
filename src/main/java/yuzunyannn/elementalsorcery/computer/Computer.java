package yuzunyannn.elementalsorcery.computer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.computer.IComputEnv;
import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.IComputerWatcher;
import yuzunyannn.elementalsorcery.api.computer.IDeviceModifiable;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.computer.IDisk;
import yuzunyannn.elementalsorcery.api.computer.IMemory;
import yuzunyannn.elementalsorcery.api.computer.IStorageMonitor;
import yuzunyannn.elementalsorcery.api.computer.soft.APP;
import yuzunyannn.elementalsorcery.api.computer.soft.IComputerException;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.computer.exception.ComputerException;
import yuzunyannn.elementalsorcery.computer.soft.EOSClient;
import yuzunyannn.elementalsorcery.computer.soft.EOSServer;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class Computer implements IComputer, IDeviceModifiable {

	static public IComputer from(ItemStack stack) {
		return stack.getCapability(Computer.COMPUTER_CAPABILITY, null);
	}

	static public IComputer from(TileEntity tile) {
		IComputer computer = tile.getCapability(Computer.COMPUTER_CAPABILITY, null);
		if (computer == null) {
			if (tile instanceof IComputer) computer = (IComputer) tile;
		}
		return computer;
	}

	static protected class AppData {

		int pid;
		NBTTagCompound nbt;

		public AppData(int pid2, NBTTagCompound data) {
			this.pid = pid2;
			this.nbt = data;
		}

	}

	@CapabilityInject(IComputer.class)
	public static Capability<IComputer> COMPUTER_CAPABILITY;

	protected IOS os;
	protected List<Disk> disks = new ArrayList<>();
	protected Memory memory = new Memory();
	protected String name = "";
	protected UUID uuid = UUID.randomUUID();
	protected boolean inRunning = false;
	protected StorageMonitor memoryStorageMonitor = new StorageMonitor();

	protected LinkedList<AppData> operations = new LinkedList<>();
	protected boolean closeFlag = false;
	protected boolean isInit = false;
	protected final String appearance;

	public Computer(String appearance) {
		os = new EOSServer(this);
		this.appearance = appearance;
	}

	@Override
	public String getAppearance() {
		return appearance;
	}

	@SideOnly(Side.CLIENT)
	protected void initClient() {
		os = new EOSClient(this);
	}

	@Override
	public List<IDisk> getDisks() {
		return (List<IDisk>) ((Object) disks);
	}

	@Override
	public IMemory getMemory() {
		return memory;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String string) {
		name = string == null ? "" : string;
	}

	@Override
	public UUID getUDID() {
		return uuid;
	}

	@Override
	public void addDisk(Disk disk) {
		disks.add(disk);
		os.onDiskChange();
	}

	@Override
	public IDisk removeDisk(int index) {
		IDisk disk = disks.remove(index);
		if (disk != null) os.onDiskChange();
		return disk;
	}

	@Override
	public void setMemory(Memory newMemory) {
		if (memory == newMemory) return;
		memory = newMemory;
		this.os.onMemoryChange();
	}

	@Override
	public IOS getSystem() {
		return os;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("#N", name);
		nbt.setUniqueId("#U", uuid);
		nbt.setBoolean("#R", inRunning);
		nbt.setTag("#M~M", memoryStorageMonitor.serializeNBT());
		if (memory != null) nbt.setTag("#M", memory.serializeNBT());
		NBTHelper.setNBTSerializableList(nbt, "#D", disks);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		name = nbt.getString("#N");
		uuid = nbt.getUniqueId("#U");
		inRunning = nbt.getBoolean("#R");
		memoryStorageMonitor.deserializeNBT(nbt.getCompoundTag("#M~M"));
		if (nbt.hasKey("#M", NBTTag.TAG_COMPOUND)) memory.deserializeNBT(nbt.getCompoundTag("#M"));
		else memory = null;
		disks = NBTHelper.getNBTSerializableList(nbt, "#D", Disk.class, NBTTagCompound.class);
	}

	@Override
	public void notice(String method, Object... objects) {
		if (!inRunning) return;

		if ("power-off".equals(method)) {
			closeFlag = true;
			return;
		}

		if ("op".equals(method)) {
			try {
				int pid = (int) objects[1];
				NBTTagCompound data = (NBTTagCompound) objects[2];
				operations.add(new AppData(pid, data));
			} catch (ArrayIndexOutOfBoundsException | ClassCastException e) {}
			return;
		}
	}

	@Override
	public void notice(IComputEnv env, String method, Object... objects) {

		if (!inRunning) {
			if ("power-on".equals(method)) {
				powerOn(env);
				return;
			}
		}

		notice(method, objects);
	}

	@Override
	public void powerOn(IComputEnv env) {
		if (inRunning) return;
		closeFlag = false;
		inRunning = true;
		memory.clear();
		memoryStorageMonitor.clear();
		if (env.isRemote()) return;
		os.onStarting();
	}

	@Override
	public void powerOff(IComputEnv env) {
		if (!inRunning) return;
		inRunning = false;
		memory.clear();
		memoryStorageMonitor.clear();
		if (env.isRemote()) return;
		os.onClosing();
	}

	@Override
	public boolean isPowerOn() {
		return inRunning;
	}

	@Override
	public IStorageMonitor getStorageMonitor(IDeviceStorage storage) {
		if (storage == this.memory) return memoryStorageMonitor;
		return null;
	}

	public void tryInit(IComputEnv env) {
		if (isInit) return;
		isInit = true;

		World world = env.getWorld();

		if (world.isRemote) {
			initClient();
			isInit = true;
			return;
		}

	}

	@Override
	public void onPlayerInteraction(EntityPlayer player, IComputEnv env) {
		this.tryInit(env);
	}

	@Override
	public void recvMessage(NBTTagCompound nbt, IComputEnv env) {
		if (env.isRemote()) {
			if (nbt.hasUniqueId("#U")) this.uuid = nbt.getUniqueId("#U");
			if (nbt.hasKey("#R")) this.inRunning = nbt.getBoolean("#R");
			if (nbt.hasKey("#M~C")) this.memoryStorageMonitor.mergeChanges(nbt.getCompoundTag("#M~C"), memory);
			return;
		}
	}

	@Override
	public NBTTagCompound detectChanges(IComputerWatcher watcher, IComputEnv env) {
		DetectDataset dataset = watcher.getDetectObject(DetectDataset.class);
		if (dataset == null) watcher.setDetectObject(dataset = new DetectDataset());

		NBTTagCompound sendData = null;

		if (!dataset.isInited) {
			dataset.isInited = true;
			dataset.inRunning = this.inRunning;
			sendData = insertInitSend(sendData);
		} else {
			if (dataset.inRunning != this.inRunning) {
				dataset.inRunning = this.inRunning;
				sendData = insertRunningSend(sendData);
			}
		}

		NBTTagCompound changes = memoryStorageMonitor.detectChanges(dataset.memoryDataset, memory);
		if (changes != null) {
			if (sendData == null) sendData = new NBTTagCompound();
			sendData.setTag("#M~C", changes);
		}

		return sendData;
	}

	protected NBTTagCompound insertInitSend(NBTTagCompound sendData) {
		if (sendData == null) sendData = new NBTTagCompound();
		sendData.setUniqueId("#U", uuid);
		sendData.setBoolean("#R", inRunning);
		return sendData;
	}

	protected NBTTagCompound insertRunningSend(NBTTagCompound sendData) {
		if (sendData == null) sendData = new NBTTagCompound();
		sendData.setBoolean("#R", inRunning);
		return sendData;
	}

	@Override
	public void detectChangesAndSend(IComputerWatcher watcher, IComputEnv env) {
		if (watcher.isLeave()) return;
		NBTTagCompound nbt = detectChanges(watcher, env);
		if (nbt != null) env.sendMessageToClient(watcher, nbt);
	}

	@Override
	public void update(IComputEnv env) {
		World world = env.getWorld();

		if (world.isRemote) {
			updateClient(env);
			return;
		}

		if (!inRunning) return;

		if (closeFlag) {
			powerOff(env);
			return;
		}

		updateOS();
	}

	@SideOnly(Side.CLIENT)
	protected void updateClient(IComputEnv env) {
		if (!isInit) tryInit(env);
		if (!inRunning) return;

		updateOS();
	}

	protected void updateOS() {

		while (!operations.isEmpty()) {
			AppData appdat = operations.removeFirst();
			try {
				APP app = os.getAppInst(appdat.pid);
				app.handleOperation(appdat.nbt);
			} catch (Exception e) {
				if (e instanceof IComputerException) os.abort(appdat.pid, (IComputerException) e);
				else ESAPI.logger.warn("系統崩潰", e);
			}
		}

		try {
			os.onUpdate();
		} catch (ComputerException e) {

		} catch (Exception e) {
			ESAPI.logger.warn("系統崩潰", e);
		}
	}

}
