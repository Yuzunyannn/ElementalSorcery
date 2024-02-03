package yuzunyannn.elementalsorcery.computer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.computer.DNParams;
import yuzunyannn.elementalsorcery.api.computer.DNResult;
import yuzunyannn.elementalsorcery.api.computer.IComputEnv;
import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.IComputerWatcher;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceListener;
import yuzunyannn.elementalsorcery.api.computer.IDeviceModifiable;
import yuzunyannn.elementalsorcery.api.computer.IDeviceNetwork;
import yuzunyannn.elementalsorcery.api.computer.IDisk;
import yuzunyannn.elementalsorcery.api.computer.soft.APP;
import yuzunyannn.elementalsorcery.api.computer.soft.IComputerException;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.api.util.detecter.ISyncWatcher;
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

	@CapabilityInject(IDevice.class)
	public static Capability<IDevice> DEVICE_CAPABILITY;

	protected IOS os;
	protected List<Disk> disks = new ArrayList<>();
	protected String name = "";
	protected UUID uuid = UUID.randomUUID();
	protected boolean inRunning = false;
	protected DeviceNetwork networkd = new DeviceNetwork(this);

	protected final LinkedList<AppData> operations = new LinkedList<>();
	protected final LinkedList<Consumer<IComputEnv>> updateFuncs = new LinkedList<>();
	protected final LinkedList<IDeviceListener> listeners = new LinkedList<>();
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
	public void addListener(IDeviceListener listener) {
		listeners.add(listener);
	}

	public void noticeAllListener(String method, DNParams params) {
		Iterator<IDeviceListener> iter = listeners.iterator();
		while (iter.hasNext()) {
			IDeviceListener listener = iter.next();
			try {
				if (listener.absent()) iter.remove();
				else listener.notice(method, params);
			} catch (Exception e) {
				iter.remove();
				ESAPI.logger.warn("listener error", e);
			}
		}
	}

	protected void addUpdateFunc(Consumer<IComputEnv> func) {
		updateFuncs.add(func);
	}

	@Override
	public List<IDisk> getDisks() {
		return (List<IDisk>) ((Object) disks);
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
	public IDeviceNetwork getNetwork() {
		return networkd;
	}

	@Override
	public void addDisk(Disk disk) {
		disks.add(disk);
		os.onDiskChange(false);
	}

	@Override
	public IDisk removeDisk(int index) {
		IDisk disk = disks.remove(index);
		if (disk != null) os.onDiskChange(false);
		return disk;
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
		nbt.setTag("#OS", os.serializeNBT());
		nbt.setTag("#NW", networkd.serializeNBT());
		NBTHelper.setNBTSerializableList(nbt, "#D", disks);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		name = nbt.getString("#N");
		uuid = nbt.getUniqueId("#U");
		inRunning = nbt.getBoolean("#R");
		os.deserializeNBT(nbt.getCompoundTag("#OS"));
		networkd.deserializeNBT(nbt.getCompoundTag("#NW"));
		disks.clear();
		NBTTagList list = nbt.getTagList("#D", NBTTag.TAG_COMPOUND);
		for (NBTBase n : list) disks.add(new Disk(((NBTTagCompound) n).copy()));
		os.onDiskChange(false);
	}

	@Override
	public CompletableFuture<DNResult> notice(String method, DNParams params) {
		if (!inRunning) return DNResult.refuse();

		if ("power-off".equals(method)) {
			closeFlag = true;
			return DNResult.success();
		} else if ("exit".equals(method)) {
			Integer pid = params.get("pid", Integer.class);
			if (pid == null || pid < 0) return DNResult.refuse();
			if (pid == 0) return DNResult.refuse();
			APP app = os.getAppInst(pid);
			if (app == null) return DNResult.fail();
			app.exit();
			return DNResult.success();
		} else if ("op".equals(method)) {
			try {
				int pid = params.get("pid", Integer.class);
				NBTTagCompound data = params.get("data");
				operations.add(new AppData(pid, data));
			} catch (ArrayIndexOutOfBoundsException | ClassCastException | NullPointerException e) {
				return DNResult.fail();
			}
			return DNResult.success();
		} else if ("app-message".equals(method)) {
			this.noticeAllListener(method, params);
			return DNResult.success();
		}

		return DNResult.invalid();
	}

	@Override
	public void notice(IComputEnv env, String method, DNParams params) {

		if (!inRunning) {
			if ("power-on".equals(method)) {
				powerOn(env);
				return;
			}
		}

		if ("msg".equals(method)) {
			if (env.isRemote()) {
				try {
					int pid = params.get("pid", Integer.class);
					NBTTagCompound data = params.get("data");
					APP app = os.getAppInst(pid);
					app.onRecvMessage(data);
				} catch (Exception e) {
					ESAPI.logger.warn("client msg error", e);
				}
			}
			return;
		}

		notice(method, params);
	}

	@Override
	public void powerOn(IComputEnv env) {
		if (inRunning) return;
		closeFlag = false;
		inRunning = true;
		if (env.isRemote()) return;
		os.onStarting();
	}

	@Override
	public void powerOff(IComputEnv env) {
		if (!inRunning) return;
		inRunning = false;
		if (env.isRemote()) return;
		os.onClosing();
	}

	@Override
	public boolean isPowerOn() {
		return inRunning;
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
			mergeChanges(nbt);
			return;
		}
	}

	public void detectChangesAndSend(IComputerWatcher watcher, IComputEnv env) {
		if (watcher.isLeave()) return;
		NBTTagCompound nbt = detectChanges(watcher);
		if (nbt != null) env.sendMessageToClient(watcher, nbt);
	}

	@Override
	public NBTTagCompound detectChanges(ISyncWatcher watcher) {
		DetectDataset dataset = watcher.getDetectObject(">computer", DetectDataset.class);
		if (dataset == null) watcher.setDetectObject(">computer", dataset = new DetectDataset());

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

		NBTTagCompound osChanges = os.detectChanges(watcher);
		if (osChanges != null) {
			if (sendData == null) sendData = new NBTTagCompound();
			sendData.setTag("#OS", osChanges);
		}

		NBTTagCompound networkChanges = networkd.detectChanges(watcher);
		if (networkChanges != null) {
			if (sendData == null) sendData = new NBTTagCompound();
			sendData.setTag("#NW", networkChanges);
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
	public void mergeChanges(NBTTagCompound nbt) {
		if (nbt.hasUniqueId("#U")) this.uuid = nbt.getUniqueId("#U");
		if (nbt.hasKey("#R")) this.inRunning = nbt.getBoolean("#R");
		if (nbt.hasKey("#OS")) os.mergeChanges(nbt.getCompoundTag("#OS"));
		if (nbt.hasKey("#NW")) networkd.mergeChanges(nbt.getCompoundTag("#NW"));
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

		try {
			networkd.update(env);
			updateOS();
			if (!updateFuncs.isEmpty()) {
				for (Consumer<IComputEnv> func : updateFuncs) func.accept(env);
				updateFuncs.clear();
			}
		} catch (Exception e) {
			ESAPI.logger.warn("系統崩潰", e);
			notice("power-off", DNParams.EMPTY);
			return;
		}

		if (!os.isRunning()) {
			notice("power-off", DNParams.EMPTY);
			//
			/*
			 * 数据错的原因是 创造模式的背包会从client回传数据到server，server使用client的数据，GG 还没有找到任何的介入点，内发修，GG*2
			 * at net.minecraft.network.play.client.CPacketCreativeInventoryAction.<init>(
			 * CPacketCreativeInventoryActio at
			 * net.minecraft.client.multiplayer.PlayerControllerMP.sendSlotPacket(
			 * PlayerControllerMP.java:636) at
			 * net.minecraft.client.gui.inventory.CreativeCrafting.sendSlotContents(
			 * CreativeCrafting.java:35) at
			 * net.minecraft.inventory.Container.detectAndSendChanges(Container.java:109) at
			 * net.minecraft.inventory.Container.addListener(Container.java:62) at
			 * net.minecraft.client.gui.inventory.GuiContainerCreative.initGui(
			 * GuiContainerCreative.java:306)
			 */
			// if (ESAPI.isDevelop) {
			// ESAPI.logger.warn("非预期的OS找不到任何APP os:" + os, new RuntimeException("error"));
			// }
		}
	}

	protected void osRun(Consumer<IOS> run) {
		try {
			run.accept(os);
		} catch (ComputerException e) {
			if (ESAPI.isDevelop) ESAPI.logger.warn("dev warnning", e);
		} catch (Exception e) {
			ESAPI.logger.warn("系統崩潰", e);
		}
	}

	@Override
	public void markDiskValueDirty() {
		osRun(os -> os.onDiskChange(true));
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
				if (app != null) app.handleOperation(appdat.nbt);
			} catch (Exception e) {
				if (e instanceof IComputerException) os.abort(appdat.pid, (IComputerException) e);
				else throw e;
			}
		}
		osRun(os -> os.onUpdate());
	}

}
