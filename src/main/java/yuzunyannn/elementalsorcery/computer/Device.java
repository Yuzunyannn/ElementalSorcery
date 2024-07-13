package yuzunyannn.elementalsorcery.computer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import yuzunyannn.elementalsorcery.api.computer.DNRequest;
import yuzunyannn.elementalsorcery.api.computer.DNResult;
import yuzunyannn.elementalsorcery.api.computer.DNResultCode;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceEnv;
import yuzunyannn.elementalsorcery.api.computer.IDeviceInfo;
import yuzunyannn.elementalsorcery.api.computer.IDeviceInitializable;
import yuzunyannn.elementalsorcery.api.computer.IDeviceLinker;
import yuzunyannn.elementalsorcery.api.computer.IDeviceNetwork;
import yuzunyannn.elementalsorcery.api.util.IAliveStatusable;
import yuzunyannn.elementalsorcery.api.util.detecter.ISyncDetectable;
import yuzunyannn.elementalsorcery.api.util.detecter.ISyncWatcher;
import yuzunyannn.elementalsorcery.api.util.render.IDisplayable;
import yuzunyannn.elementalsorcery.api.util.target.CapabilityObjectRef;
import yuzunyannn.elementalsorcery.api.util.target.WorldLocation;
import yuzunyannn.elementalsorcery.computer.soft.display.DTCReuntime;
import yuzunyannn.elementalsorcery.computer.soft.display.DeviceAskerDisplay;
import yuzunyannn.elementalsorcery.tile.device.DeviceFeature;
import yuzunyannn.elementalsorcery.tile.device.DeviceFeatureMap;
import yuzunyannn.elementalsorcery.util.LambdaReference;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTSS;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;
import yuzunyannn.elementalsorcery.util.helper.JavaHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTSender;

public class Device<U> implements IDeviceInitializable, ISyncDetectable<NBTTagCompound>, INBTSS {

	@CapabilityInject(IDevice.class)
	public static Capability<IDevice> DEVICE_CAPABILITY;

	protected UUID udid = UUID.randomUUID();
	protected DeviceNetwork network = new DeviceNetwork(this);
	protected DeviceInfo info;
	protected IDeviceEnv env;
	protected DeviceProcess process = new DeviceProcess(this);
	public final static DeviceFeatureMap dfeature = DeviceFeatureMap.getOrCreate(Device.class);
	protected final List<Entry<DeviceFeatureMap, Object>> features = new ArrayList<>();
	protected final U target;
	protected final ICapabilityProvider capabilityProvider;

	public Device(U target, DeviceInfo info) {
		this.info = info;
		this.target = target;
		this.addFeature(target);
		if (target instanceof ICapabilityProvider) capabilityProvider = (ICapabilityProvider) target;
		else capabilityProvider = null;
	}

	public void addFeature(Object obj) {
		features.add(JavaHelper.entry(DeviceFeatureMap.getOrCreate(obj.getClass()), obj));
	}

	@Override
	public void init(Init init) {
		this.udid = init.getUDID();
		if (init.network instanceof DeviceNetwork) this.network = (DeviceNetwork) init.network;
		if (init.info instanceof DeviceInfo) this.info = (DeviceInfo) init.info;
		else {
			this.info.setName(init.getName());
		}
		this.network.init(init.linkers);
	}

	public void setEnv(IDeviceEnv env) {
		this.env = env;
	}

	@Override
	public IDeviceEnv getEnv() {
		return this.env;
	}

	public DeviceProcess getProcess() {
		return process;
	}

	@Override
	public DNResult notice(String method, DNRequest params) {
		if (this.env == null) return DNResult.unavailable();

		DeviceFeatureMap feature = null;
		Object self = null;

		for (Entry<DeviceFeatureMap, Object> entry : features) {
			feature = entry.getKey();
			if (feature.has(method)) {
				self = entry.getValue();
				break;
			}
		}

		if (self == null) {
			if (!dfeature.has(method)) return DNResult.invalid();
			feature = dfeature;
			self = this;
		}

		DNRequest originParams = process.currParams;
		process.currParams = params;

		params.setWorld(this.env.getWorld());
		Object ret = feature.invoke(self, method, params);

		process.currParams = originParams;

		return DNResult.byRet(ret);
	}

	@Override
	public UUID getUDID() {
		return udid;
	}

	@Override
	public IDeviceNetwork getNetwork() {
		return network;
	}

	@Override
	@DeviceFeature(id = "info-get")
	public IDeviceInfo getInfo() {
		return info;
	}

	@DeviceFeature(id = "name-get")
	public String getName() {
		return info.getName();
	}

	@DeviceFeature(id = "name-set")
	public void setName(String name) {
		info.setName(name);
	}

	public void update() {
		network.update(env);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (Computer.DEVICE_CAPABILITY.equals(capability)) return true;
		return capabilityProvider == null ? false : capabilityProvider.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (Computer.DEVICE_CAPABILITY.equals(capability)) return (T) this;
		return capabilityProvider == null ? null : capabilityProvider.getCapability(capability, facing);
	}

	/* ------------ ------------** >_< **------------ ------------ */
	/* ------------ ------------** serialize **------------ ------------ */
	/* ------------ ------------** >_< **------------ ------------ */

	public void writeSaveData(INBTWriter writer) {
		writer.write("uuid", udid);
		writer.write("network", network);
		writer.write("info", info);
	}

	public void readSaveData(INBTReader reader) {
		udid = reader.uuid("uuid");
		network = reader.obj("network", network);
		info = reader.obj("info", info);
		if (reader.has("_copy_init_")) {
			udid = UUID.randomUUID();
			network.clear();
		}

	}

	public void writeUpdateData(INBTWriter writer) {
		writer.write("uuid", udid);
		writer.write("info", info);
	}

	public void readUpdateData(INBTReader reader) {
		udid = reader.uuid("uuid");
		info = reader.obj("info", info);
	}

	public static class DetectDataset {
		UUID udid;
	}

	@Override
	public NBTTagCompound detectChanges(ISyncWatcher watcher) {
		DetectDataset dataset = watcher.getOrCreateDetectObject(">device", DetectDataset.class, () -> new DetectDataset());

		NBTTagCompound networkChanges = network.detectChanges(watcher);
		if (networkChanges != null) NBTSender.SHARE.write("N", networkChanges);

		if (!udid.equals(dataset.udid)) {
			dataset.udid = udid;
			NBTSender.SHARE.write("U", udid);
		}

		return NBTSender.SHARE.spitOut();
	}

	@Override
	public void mergeChanges(NBTTagCompound nbt) {
		NBTSender sender = new NBTSender(nbt);
		if (sender.has("N")) network.mergeChanges(sender.compoundTag("N"));
		if (sender.has("U")) {
			this.udid = sender.uuid("U");
			this.network.resetSelfLinker();
		}
	}

	/* ------------ ------------** >_< **------------ ------------ */
	/* ------------ ------------** network **------------ ------------ */
	/* ------------ ------------** >_< **------------ ------------ */

	protected class Asker extends CompletableFuture<CapabilityObjectRef>
			implements IDeviceAsker, IDisplayable, IAliveStatusable {

		public final UUID udid;
		public WorldLocation wo;
		public boolean isUnconcerned;
		private boolean isEnd;

		public Asker(UUID udid) {
			IDeviceEnv env = getEnv();
			this.udid = udid;
			this.wo = new WorldLocation(env.getWorld(), env.getBlockPos());
		}

		@Override
		public WorldLocation where() {
			return wo;
		}

		@Override
		public void onFind(IDeviceEnv findedEnv) {
			this.complete(findedEnv.createRef());
			onEnd();
		}

		@Override
		public void onFindFailed() {
			this.complete(CapabilityObjectRef.INVALID);
			onEnd();
		}

		public void onEnd() {
			isEnd = true;
			if (asker == this) {
				asker = null;
				finder = null;
			}
		}

		@Override
		public boolean isUnconcerned() {
			return isUnconcerned;
		}

		@Override
		public UUID lookFor() {
			return udid;
		}

		@Override
		public boolean isAlive() {
			return !isEnd;
		}

		@Override
		public Object toDisplayObject() {
			DeviceAskerDisplay display = new DeviceAskerDisplay();
			display.setCondition(new DTCReuntime(this));
			display.setDigest("DS:" + System.identityHashCode(this));
			this.thenAccept(ref -> display.onFinish(ref, null));
			return display;
		}
	}

	protected DeviceFinder finder;
	protected Asker asker;

	@DeviceFeature(id = "network-scan")
	public Object networkScan() {
		return WideNetwork.instance.applyScanner(env.createWorldObj());
	}

	@DeviceFeature(id = "network-find")
	public Object networkFind(UUID uuid) {
		if (finder != null) {
			finder.leave(asker);
			asker.onFindFailed();
		}
		asker = new Asker(uuid);
		finder = WideNetwork.instance.applyFinder(env.getWorld(), asker);
		return asker;
	}

	@DeviceFeature(id = "network-conntect")
	public void networkConntect(UUID uuid) {

		IDeviceNetwork network = getNetwork();
		IDeviceLinker linker = network.getLinker(uuid);
		if (linker != null) {
			if (process.isLogEnabled()) {
				DeviceAskerDisplay display = new DeviceAskerDisplay();
				display.setDigest("DS:" + System.identityHashCode(asker));
				display.onFinish(linker.getRemoteRef(), DNResultCode.SUCCESS);
				process.log(display);
			}
			return;
		}

		Asker asker = (Asker) networkFind(uuid);
		LambdaReference<DNResultCode> code = LambdaReference.of(DNResultCode.INVALID);
		CompletableFuture<CapabilityObjectRef> feture = asker.thenApply(ref -> {
			code.set(networkHandshake(ref));
			return ref;
		});

		if (process.isLogEnabled()) {
			DeviceAskerDisplay display = new DeviceAskerDisplay();
			display.setCondition(new DTCReuntime(asker));
			display.setDigest("DS:" + System.identityHashCode(asker));
			feture.thenAccept(ref -> display.onFinish(ref, code.get()));
			process.log(display);
		}

	}

	@DeviceFeature(id = "network-close")
	public DNResultCode networkHandshake(UUID udid) {
		IDeviceNetwork network = this.getNetwork();
		IDeviceLinker linker = network.getLinker(udid);
		if (linker != null) {
			linker.close();
			if (process.isLogEnabled()) {
				Style style = new Style().setColor(TextFormatting.YELLOW);
				process.log(new TextComponentTranslation("es.app.disconnect").setStyle(style).appendText(":" + udid));
			}
		}
		return DNResultCode.SUCCESS;
	}

	@DeviceFeature(id = "network-handshake")
	public DNResultCode networkHandshake(CapabilityObjectRef ref) {

		if (!ref.checkReference()) {
			ref.restore(env.getWorld());
			if (!ref.checkReference()) return DNResultCode.FAIL;
		}

		IDevice other = ref.getCapability(Computer.DEVICE_CAPABILITY, null);
		if (other == null) return DNResultCode.FAIL;
		if (other.getEnv() == null) return DNResultCode.FAIL;

		IDeviceNetwork network = this.getNetwork();
		IDeviceNetwork otherNetwork = other.getNetwork();

		boolean s1 = network.handshake(other, other.getEnv(), true);
		boolean s2 = otherNetwork.handshake(this, this.getEnv(), true);

		if (!s1 || !s2) return DNResultCode.REFUSE;

		network.handshake(other, other.getEnv(), false);
		otherNetwork.handshake(this, this.getEnv(), false);

		return DNResultCode.SUCCESS;
	}

	@DeviceFeature(id = "network-ls")
	public Object networkList(String type) {
		Collection<IDeviceLinker> linkers = getNetwork().getLinkers();
		switch (type.toLowerCase()) {
		case "uuid":
			return JavaHelper.toList(linkers, liner -> liner.getRemoteUUID());
		case "*":
			return JavaHelper.toList(linkers, liner -> liner);
		default:
			return DNResultCode.REFUSE;
		}
	}

	@DeviceFeature(id = "network-ls")
	public Object networkList() {
		return this.networkList("*");
	}
}
