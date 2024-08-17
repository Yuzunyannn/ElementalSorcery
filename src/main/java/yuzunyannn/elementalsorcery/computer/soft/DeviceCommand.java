package yuzunyannn.elementalsorcery.computer.soft;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import yuzunyannn.elementalsorcery.api.computer.DNRequest;
import yuzunyannn.elementalsorcery.api.computer.DNResult;
import yuzunyannn.elementalsorcery.api.computer.DeviceFilePath;
import yuzunyannn.elementalsorcery.api.computer.DeviceNetworkRoute;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.soft.DeviceShellBadInvoke;
import yuzunyannn.elementalsorcery.api.computer.soft.IDeviceFile;
import yuzunyannn.elementalsorcery.api.computer.soft.IDeviceShell;
import yuzunyannn.elementalsorcery.api.computer.soft.IDeviceShellExecutor;
import yuzunyannn.elementalsorcery.api.util.StateCode;
import yuzunyannn.elementalsorcery.tile.device.DeviceFeature;
import yuzunyannn.elementalsorcery.tile.device.DeviceFeatureMap;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTSS;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;
import yuzunyannn.elementalsorcery.util.helper.JavaHelper;

public class DeviceCommand implements INBTSS, IDeviceShell {

	public static final int TAG_STR = 0;
	public static final int TAG_CMD = 1;
	public static final int TAG_REF = 2;

	protected static abstract class Element {

		public boolean isExecable() {
			return false;
		}

		public abstract Object toArgs(IDeviceShellExecutor executor);

		public abstract int tag();

		public abstract void write(PacketBuffer buf);

		public abstract void read(PacketBuffer buf);
	}

	protected static class CMDElement extends Element {
		String orient = "";
		String method;
		Element[] args;
		boolean inner;

		@Override
		public boolean isExecable() {
			return !inner;
		}

		@Override
		public Object toArgs(IDeviceShellExecutor executor) {
			return new DeviceCommand(this);
		}

		@Override
		public int tag() {
			return TAG_CMD;
		}

		@Override
		public void read(PacketBuffer buf) {
			inner = buf.readBoolean();
			orient = buf.readString(32767);
			method = buf.readString(32767);
			int count = buf.readShort();
			args = new Element[count];
			for (int i = 0; i < count; i++) args[i] = DeviceCommand.read(buf);
		}

		@Override
		public void write(PacketBuffer buf) {
			buf.writeBoolean(inner);
			buf.writeString(orient);
			buf.writeString(method);
			buf.writeShort(args.length);
			for (int i = 0; i < args.length; i++) DeviceCommand.write(buf, args[i]);
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			if (!orient.isEmpty()) builder.append('|').append(orient).append('|');
			builder.append(method);
			List<String> args = new LinkedList<>();
			for (Element e : this.args) args.add(e.toString());
			builder.append(String.join(" ", args));
			return builder.toString();
		}
	}

	protected static class StringElement extends Element {
		String val;

		public StringElement() {
		}

		public StringElement(String string) {
			val = string;
		}

		@Override
		public Object toArgs(IDeviceShellExecutor executor) {
			return val;
		}

		@Override
		public int tag() {
			return TAG_STR;
		}

		@Override
		public void read(PacketBuffer buf) {
			val = buf.readString(32767);
		}

		@Override
		public void write(PacketBuffer buf) {
			buf.writeString(val);
		}

		@Override
		public String toString() {
			return String.format("'%s'", val);
		}
	}

	protected static class RefElement extends Element {
		String ref;

		public RefElement(String string) {
			ref = string;
		}

		public RefElement() {
		}

		@Override
		public Object toArgs(IDeviceShellExecutor executor) {
			throw new DeviceShellBadInvoke("unsupport");
		}

		@Override
		public int tag() {
			return TAG_REF;
		}

		@Override
		public void read(PacketBuffer buf) {
			ref = buf.readString(32767);
		}

		@Override
		public void write(PacketBuffer buf) {
			buf.writeString(ref);
		}

		@Override
		public String toString() {
			return "$" + ref;
		}
	}

	protected Element root;

	protected static void write(PacketBuffer buf, Element e) {
		buf.writeByte(e.tag());
		e.write(buf);
	}

	protected static Element read(PacketBuffer buf) {
		int tag = buf.readByte();
		Element node;
		if (tag == TAG_STR) node = new StringElement();
		else if (tag == TAG_REF) node = new RefElement();
		else if (tag == TAG_CMD) node = new CMDElement();
		else throw new RuntimeException("read cmd element error tag " + tag);
		node.read(buf);
		return node;
	}

	public final static DeviceFeatureMap dfeature = DeviceFeatureMap.getOrCreate(DeviceCommand.class);
	protected Object[] args = new Object[0];

	public DeviceCommand() {

	}

	protected DeviceCommand(Element root) {
		this.root = root;
	}

	public DeviceCommand(CommandParser paser) {
		load(paser);
	}

	@Override
	public void setArgs(Object... args) {
		this.args = args;
	}

	public <T> T getArg(int index, Class<T> cls) {
		if (index < 0 || index >= args.length) return null;
		Object obj = this.args[index];
		if (obj != null && cls.isAssignableFrom(obj.getClass())) return (T) obj;
		return null;
	}

	@Override
	public boolean isEmpty() {
		return root == null;
	}

	protected void load(CommandParser parser) {
		CommandParser.Element cmdElement = parser.getRoot();
		this.root = load(cmdElement);
	}

	private Element load(CommandParser.Element pElement) {
		if (pElement.isCommand()) {
			CMDElement node = new CMDElement();
			node.inner = pElement.isInnerCommand();
			List<Element> args = new LinkedList<>();
			for (CommandParser.Element p : pElement.getElements()) {
				if (p.isArgs()) {
					if (p.getArgsIndex() == 0) node.method = p.getString();
					else args.add(load(p));
					continue;
				}
				if (p.isOrient()) node.orient = p.getString();
			}
			if (node.method == null) throw new IllegalArgumentException("method is null");
			node.args = args.toArray(new Element[args.size()]);
			return node;
		} else {
			if (pElement.isRef()) return new RefElement(pElement.getString());
			else return new StringElement(pElement.getString());
		}
	}

	@Override
	public void readSaveData(INBTReader reader) {
		this.root = reader.sobj("_", buf -> read(buf));
	}

	@Override
	public void writeSaveData(INBTWriter writer) {
		writer.writeStream("_", buf -> write(buf, root));
	}

	@Override
	public void invoke(IDeviceShellExecutor executor) throws DeviceShellBadInvoke {
		if (this.isEmpty()) {
			executor.pushResult(this, DNResult.invalid());
			return;
		}
		DNResult result = invoke(executor, root);
		executor.pushResult(this, result);
	}

	public boolean isClear() {
		DeviceCommand.CMDElement cmd = (CMDElement) root;
		return "clear".equals(cmd.method);
	}

	public static UUID finedUUID(IDevice device, String hint) {
		DNRequest params = new DNRequest();
		params.set(DNRequest.args(1), "uuid");
		DNResult result = device.notice("network-ls", params);
		List<UUID> list = result.getReturn(List.class);
		if (list == null) return null;
		hint = hint.toLowerCase();
		for (UUID uuid : list) {
			String uStr = uuid.toString().toLowerCase();
			if (uStr.startsWith(hint)) return uuid;
		}
		return null;
	}

	protected DNResult invoke(IDeviceShellExecutor executor, Element element) throws DeviceShellBadInvoke {
		final IDevice device = executor.getDevice();
		DNRequest request = new DNRequest();
		request.setLogList(executor.getLogs());
		request.setFinder(UUID.class, hint -> finedUUID(device, hint));

		if (!element.isExecable()) throw new DeviceShellBadInvoke(String.format("'%s' cannot exec", element));

		DeviceCommand.CMDElement cmd = (CMDElement) element;
		for (int i = 0; i < cmd.args.length; i++) {
			DeviceCommand.Element e = cmd.args[i];
			String key = DNRequest.args(i + 1);
			if (e.isExecable()) {
				DNResult result = invoke(executor, e);
				if (!result.isSuccess()) return result;
				request.set(key, result.getReturn());
			} else request.set(key, e.toArgs(executor));
		}

		UUID uuid = null;
		if (!cmd.orient.isEmpty()) {
			uuid = finedUUID(device, cmd.orient);
			if (uuid == null) {
				request.log(new TextComponentTranslation("es.app.cannotFind", cmd.orient));
				return DNResult.invalid();
			}
		}

		DeviceNetworkRoute route = new DeviceNetworkRoute(uuid);

		if (dfeature.has(cmd.method)) {
			request.setSrcDevice(device);
			request.set("route", route);
			request.set("executor", executor);
			Object obj = dfeature.invoke(this, cmd.method, request);
			return DNResult.byRet(obj);
		}

		return device.getNetwork().notice(route, cmd.method, request);
	}

	@DeviceFeature(id = "ls")
	public Object cmd_ls(DNRequest request) {
		DeviceFilePath path = request.ask(DNResult.args(1), DeviceFilePath.class);
		if (path == null) path = DeviceFilePath.of();

		IDevice device = request.getSrcDevice();
		DeviceNetworkRoute route = request.get("route", DeviceNetworkRoute.class);
		if (path == null || device == null || route == null) {
			request.log(new TextComponentTranslation("es.app.errPath"));
			return StateCode.FAIL;
		}

		DNRequest ioRequest = new DNRequest();
		ioRequest.set(DNResult.args(1), path);
		ioRequest.setLogList(request.getLogList());
		DNResult result = device.getNetwork().notice(route, "io", ioRequest);
		if (!result.isSuccess()) return result.code;

		IDeviceFile file = result.getReturn(IDeviceFile.class);
		if (!file.exists() || file == null) {
			request.log(new TextComponentTranslation("es.app.errPath"));
			return StateCode.FAIL;
		}

		Collection<IDeviceFile> files = file.list();
		if (files == null) return StateCode.REFUSE;

		return JavaHelper.toList(files, p -> {
			if (p.isDirectory()) return TextFormatting.BLUE + "/" + p.getName();
			return TextFormatting.YELLOW + p.getName();
		});
	}

}
