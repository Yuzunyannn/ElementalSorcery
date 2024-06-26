package yuzunyannn.elementalsorcery.computer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

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
import yuzunyannn.elementalsorcery.api.computer.DNRequest;
import yuzunyannn.elementalsorcery.api.computer.DNResult;
import yuzunyannn.elementalsorcery.api.computer.DeviceFilePath;
import yuzunyannn.elementalsorcery.api.computer.IComputEnv;
import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceListener;
import yuzunyannn.elementalsorcery.api.computer.IDisk;
import yuzunyannn.elementalsorcery.api.computer.soft.App;
import yuzunyannn.elementalsorcery.api.computer.soft.IComputerException;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.util.detecter.ISyncWatcher;
import yuzunyannn.elementalsorcery.computer.exception.ComputerException;
import yuzunyannn.elementalsorcery.computer.exception.ComputerProcessNotExistException;
import yuzunyannn.elementalsorcery.computer.soft.EOSClient;
import yuzunyannn.elementalsorcery.computer.soft.EOSServer;
import yuzunyannn.elementalsorcery.tile.device.DeviceFeature;
import yuzunyannn.elementalsorcery.tile.device.DeviceFeatureMap;
import yuzunyannn.elementalsorcery.util.helper.NBTSender;

public abstract class Computer implements IComputer {

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

	public final static DeviceFeatureMap cfeature = DeviceFeatureMap.getOrCreate(Computer.class);

	protected IOS os;
	protected List<IDisk> disks = new ArrayList<>();
	protected boolean inRunning = false;

	protected final LinkedList<AppData> operations = new LinkedList<>();
	protected final LinkedList<IDeviceListener> listeners = new LinkedList<>();
	protected boolean closeFlag = false;
	protected boolean isInit = false;
	protected final String appearance;
	protected IComputerException exception;

	public Computer(String appearance) {
		os = new EOSServer(this);
		this.appearance = appearance;
	}

	@Override
	public String getAppearance() {
		return appearance;
	}

	@Override
	public IComputerException getException() {
		return exception;
	}

	@SideOnly(Side.CLIENT)
	protected void initClient() {
		os = new EOSClient(this);
	}

	@Override
	public void addListener(IDeviceListener listener) {
		listeners.add(listener);
	}

	public void noticeAllListener(String method, DNRequest params) {
		Iterator<IDeviceListener> iter = listeners.iterator();
		while (iter.hasNext()) {
			IDeviceListener listener = iter.next();
			try {
				if (!listener.isAlive()) iter.remove();
				else listener.notice(method, params);
			} catch (Exception e) {
				iter.remove();
				ESAPI.logger.warn("listener error", e);
			}
		}
	}

	@Override
	public List<IDisk> getDisks() {
		return Collections.unmodifiableList(disks);
	}

	@Override
	public void markDiskValueDirty() {
		osRun(os -> os.onDiskChange(true));
	}

	public void markDiskValueDirty(boolean onlyData) {
		osRun(os -> os.onDiskChange(onlyData));
	}

	@Override
	public IOS getSystem() {
		return os;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("#R", inRunning);
		nbt.setTag("#OS", os.serializeNBT());
		if (inRunning && exception != null) nbt.setTag("#Error", IComputerException.serialize(exception));
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		inRunning = nbt.getBoolean("#R");
		os.deserializeNBT(nbt.getCompoundTag("#OS"));
		if (inRunning && nbt.hasKey("#Error")) exception = IComputerException.deserialize(nbt.getCompoundTag("#Error"));
	}

	@Override
	public DNResult notice(String method, DNRequest params) {
		if (!inRunning) return DNResult.refuse();
		if (cfeature.has(method)) {
			IComputEnv env = getEnv();
			if (env != null) params.setWorld(env.getWorld());
			Object obj = cfeature.invoke(this, method, params);
			return DNResult.byRet(obj);
		}
		if (device() != this) return device().notice(method, params);
		return DNResult.invalid();
	}

	@Override
	public void notice(IComputEnv env, String method, DNRequest params) {

		if (!inRunning) {
			if ("power-on".equals(method)) {
				powerOn();
				return;
			}
		}

		try {
			if ("msg".equals(method)) {
				if (env.isRemote()) {
					try {
						int pid = params.get("pid", Integer.class);
						NBTTagCompound data = params.get("data");
						App app = os.getAppInst(pid);
						app.onRecvMessage(data);
					} catch (Exception e) {
						ESAPI.logger.warn("client msg error", e);
					}
				}
				return;
			}

			notice(method, params);
		} catch (Exception e) {
			abort(e);
		}
	}

	@DeviceFeature(id = "power-off")
	public void setPowerFlag() {
		closeFlag = true;
	}

	@DeviceFeature(id = "io")
	public Object io(DeviceFilePath path) {
		return getSystem().io(path);
	}

	@DeviceFeature(id = "exit")
	public DNResult exitApp(DNRequest params) {
		Integer pid = params.get("pid", Integer.class);
		if (pid == null || pid < 0) return DNResult.refuse();
		if (pid == 0) return DNResult.refuse();
		App app;
		try {
			app = os.getAppInst(pid);
			if (app == null) return DNResult.fail();
		} catch (ComputerProcessNotExistException e) {
			return DNResult.fail();
		}
		app.exit();
		return DNResult.success();
	}

	@DeviceFeature(id = "op")
	public DNResult execAppOp(DNRequest params) {
		try {
			int pid = params.get("pid", Integer.class);
			NBTTagCompound data = params.get("data");
			operations.add(new AppData(pid, data));
		} catch (Exception e) {
			return DNResult.fail();
		}
		return DNResult.success();
	}

	@DeviceFeature(id = "app-message")
	public DNResult execAppMessage(DNRequest params) {
		this.noticeAllListener("app-message", params);
		return DNResult.success();
	}

	@Override
	public void powerOn() {
		if (inRunning) return;
		IComputEnv env = getEnv();
		if (env == null) return;
		exception = null;
		closeFlag = false;
		inRunning = true;
		if (env.isRemote()) return;
		try {
			os.onStarting();
		} catch (Exception e) {
			abort(e);
		}
	}

	@Override
	public void powerOff() {
		if (!inRunning) return;
		exception = null;
		IComputEnv env = getEnv();
		if (env == null) return;
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

//	public void detectChangesAndSend(IComputerWatcher watcher, IComputEnv env) {
//		if (watcher.isLeave()) return;
//		NBTTagCompound nbt = detectChanges(watcher);
//		if (nbt != null) env.sendMessageToClient(watcher, nbt);
//	}

	public static class DetectDataset {
		public boolean inRunning = false;
		public boolean inInited = false;
		public boolean hasException = false;
	}

	@Override
	public NBTTagCompound detectChanges(ISyncWatcher watcher) {
		DetectDataset dataset = watcher.getOrCreateDetectObject(">computer", DetectDataset.class, () -> new DetectDataset());

		if (!dataset.inInited) {
			dataset.inInited = true;
			dataset.inRunning = this.inRunning;
			NBTSender.SHARE.write("#R", inRunning);
		} else if (dataset.inRunning != this.inRunning) {
			dataset.inRunning = this.inRunning;
			NBTSender.SHARE.write("#R", inRunning);
		}

		if (this.inRunning) {
			if (this.exception != null && !dataset.hasException) {
				dataset.hasException = true;
				NBTSender.SHARE.write("#Error", IComputerException.serialize(this.exception));
			}
		} else dataset.hasException = false;

		try {
			if (dataset.hasException) return NBTSender.SHARE.spitOut();
			NBTTagCompound osChanges = os.detectChanges(watcher);
			if (osChanges != null) NBTSender.SHARE.write("#OS", osChanges);
		} catch (Exception e) {
			abort(e);
			NBTSender.SHARE.spitOut();
			return null;
		}

		return NBTSender.SHARE.spitOut();
	}

	@Override
	public void mergeChanges(NBTTagCompound nbt) {
		if (nbt.hasKey("#R")) this.inRunning = nbt.getBoolean("#R");
		if (nbt.hasKey("#OS")) os.mergeChanges(nbt.getCompoundTag("#OS"));
		if (!this.inRunning) this.exception = null;
		if (nbt.hasKey("#Error")) this.exception = IComputerException.deserialize(nbt.getCompoundTag("#Error"));
	}

	@Override
	public void update() {
		IComputEnv env = this.getEnv();
		if (env == null) return;

		World world = env.getWorld();

		if (!inRunning) return;

		if (closeFlag) {
			powerOff();
			return;
		}

		if (exception != null) return;

		if (world.isRemote) {
			updateClient(env);
			return;
		}

		try {
			doUpdate(env);
		} catch (Exception e) {
			abort(e);
			return;
		}

		if (!os.isRunning()) {
			notice("power-off", DNRequest.empty());
			//
			/*
			 * 数据错的原因是 创造模式的背包会从client回传数据到server，server使用client的数据，GG 还没有找到任何的介入点，内发修，GG*2
			 * NetHandlerPlayServer.processCreativeInventoryAction
			 * 
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

	@SideOnly(Side.CLIENT)
	protected void updateClient(IComputEnv env) {
		if (!isInit) tryInit(env);
		if (!inRunning) return;
		try {
			doUpdate(env);
		} catch (Exception e) {
			ESAPI.logger.warn("系統崩潰client", e);
			exception = IComputerException.easy(e);
			return;
		}
	}

	protected void abort(Throwable err) {
		if (err instanceof ComputerException) {
			if (ESAPI.isDevelop) ESAPI.logger.info("有异常~~~~|" + err.getMessage());
			exception = IComputerException.easy(err);
			return;
		} else if (err instanceof RuntimeException) {
			Throwable cause = err.getCause();
			if (cause instanceof ComputerException) {
				if (ESAPI.isDevelop) ESAPI.logger.info("有异常~~~~|" + err.getMessage());
				exception = IComputerException.easy(cause);
				return;
			}
		}
		exception = IComputerException.easy(err);
		ESAPI.logger.warn("系統崩潰", err);
	}

	protected void doUpdate(IComputEnv env) {
		updateOS();
	}

	protected void osRun(Consumer<IOS> run) {
		try {
			run.accept(os);
		} catch (Exception e) {
			ESAPI.logger.warn("系統崩潰client", e);
			exception = IComputerException.easy(e);
		}
	}

	protected void updateOS() {
		while (!operations.isEmpty()) {
			AppData appdat = operations.removeFirst();
			try {
				App app = os.getAppInst(appdat.pid);
				if (app != null) app.handleOperation(appdat.nbt);
			} catch (Exception e) {
				if (e instanceof IComputerException) os.abort(appdat.pid, (IComputerException) e);
				else throw e;
			}
		}
		osRun(os -> os.onUpdate());
	}

}
