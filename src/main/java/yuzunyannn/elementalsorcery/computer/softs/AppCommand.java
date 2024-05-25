package yuzunyannn.elementalsorcery.computer.softs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.computer.DNResult;
import yuzunyannn.elementalsorcery.api.computer.DNResultCode;
import yuzunyannn.elementalsorcery.api.computer.IDeviceInfo;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.computer.soft.AppDiskType;
import yuzunyannn.elementalsorcery.api.computer.soft.DeviceShellBadInvoke;
import yuzunyannn.elementalsorcery.api.computer.soft.IDeviceShellExecutor;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGui;
import yuzunyannn.elementalsorcery.api.util.GameDisplayCast;
import yuzunyannn.elementalsorcery.api.util.detecter.IDataDetectable;
import yuzunyannn.elementalsorcery.api.util.detecter.IDataRef;
import yuzunyannn.elementalsorcery.api.util.var.Variable;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.computer.soft.AppBase;
import yuzunyannn.elementalsorcery.computer.soft.CommandParser;
import yuzunyannn.elementalsorcery.computer.soft.DeviceCommand;
import yuzunyannn.elementalsorcery.tile.device.DeviceFeature;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTSS;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;
import yuzunyannn.elementalsorcery.util.helper.JavaHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTSender;

public class AppCommand extends AppBase {

	public static class CMDRecord implements INBTSS {
		protected int id;
		protected String cmd;
		protected DNResultCode code;
		protected Object displayObject;

		protected CompletableFuture<CMDRecord> future = new CompletableFuture();

		public CMDRecord() {
			this.cmd = "";
		}

		public CMDRecord(String cmd) {
			this.cmd = cmd;
		}

		public final int getId() {
			return id;
		}

		@Override
		public void readSaveData(INBTReader reader) {
			id = reader.nint("id");
			cmd = reader.string("cmd");
			if (reader.has("code")) code = DNResultCode.fromMeta(reader.nint("code"));
		}

		@Override
		public void writeSaveData(INBTWriter writer) {
			writer.write("id", id);
			writer.write("cmd", cmd);
			if (code != null) writer.write("code", (byte) code.getMeta());
		}

		@Override
		public void readUpdateData(INBTReader reader) {
			readSaveData(reader);
			if (reader.has("dpl")) displayObject = reader.display("dpl");
			else displayObject = null;
		}

		@Override
		public void writeUpdateData(INBTWriter writer) {
			writeSaveData(writer);
			if (displayObject != null) writer.writeDisplay("dpl", displayObject);
		}

		public boolean isSustaining() {
			return false;
		}

		public CompletableFuture<CMDRecord> getSustainFuture() {
			return future;
		}

		public Object getDisplayObject() {
			return displayObject;
		}
	}

	public static final String ID = "command";
	public static final Variable<ArrayList<String>> CMD_CACHE = new Variable("cmdCache", VariableSet.STRING_ARRAY_LIST);
	protected DDQueue<CMDRecord> ddq = new DDQueue();
	protected LinkedList<CMDRecord> list = new LinkedList<>();
	protected LinkedHashMap<Integer, CMDRecord> sustainMap = new LinkedHashMap<>();
	protected int persistentSize = 0;
	protected IDataDetectable<Map<Integer, NBTTagCompound>, NBTTagList> dds = new IDataDetectable<Map<Integer, NBTTagCompound>, NBTTagList>() {

		@Override
		public NBTTagList detectChanges(IDataRef<Map<Integer, NBTTagCompound>> templateRef) {
			Map<Integer, NBTTagCompound> tmp = templateRef.get();
			if (tmp == null) templateRef.set(tmp = new HashMap<>());
			NBTTagList list = new NBTTagList();
			for (CMDRecord record : sustainMap.values()) {
				NBTTagCompound lastNBT = tmp.get(record.getId());
				if (lastNBT == null) {
					tmp.put(record.getId(), lastNBT);
					continue;
				}
				NBTSender sender = new NBTSender();
				record.writeUpdateData(sender);
				if (lastNBT.equals(sender.tag())) continue;
				tmp.put(record.getId(), sender.tag());
				list.appendTag(sender.tag());
			}
			return list.isEmpty() ? null : list;
		}

		@Override
		public void mergeChanges(NBTTagList list) {
			for (int i = 0; i < list.tagCount(); i++) {
				NBTSender sender = new NBTSender(list.getCompoundTagAt(i));
				int id = sender.nint("id");
				CMDRecord record = sustainMap.get(id);
				if (record == null) continue;
				record.readUpdateData(sender);
				onSustainRecordUpdate(record);
			}
		}

	};

	public AppCommand(IOS os, int pid) {
		super(os, pid);
		ddq.setPersistentSize(persistentSize);
		ddq.setQueue(list, () -> new CMDRecord());
		ddq.onElementAdd = rcd -> this.onRecordAdd(rcd);
		this.detecter.add("rcd", ddq);
		this.detecter.add("rcds", dds, 20);
	}

	@Override
	public void onStartup() {
		super.onStartup();
		IDeviceStorage disk = getOS().getDisk(this, AppDiskType.USER_DATA);
		if (disk != null) {
			List<String> cmds = disk.get(CMD_CACHE);
			for (String str : cmds) add(new CMDRecord(str));
			IDeviceInfo info = getOS().getDeviceInfo();
			if (info.isMobile()) disk.remove(CMD_CACHE);
		}
	}

	@Override
	public void onExit() {
		super.onExit();
		IDeviceStorage disk = getOS().getDisk(this, AppDiskType.USER_DATA);
		if (disk != null && disk.isWriteable()) {
			disk.set(CMD_CACHE, JavaHelper.toList(list, record -> record.cmd));
			disk.markDirty(CMD_CACHE);
		}
	}

	@Override
	public void writeSaveData(INBTWriter writer) {
		super.writeSaveData(writer);
		writer.write("record", list);
		writer.write("ps", persistentSize);
	}

	@Override
	public void readSaveData(INBTReader reader) {
		super.readSaveData(reader);
		this.list = new LinkedList<>(reader.list("record", () -> new CMDRecord()));
		this.persistentSize = reader.nint("ps");
		this.sustainMap.clear();
		for (CMDRecord record : this.list) addSustainMap(record);
		ddq.setPersistentSize(persistentSize);
		ddq.setQueue(list, () -> new CMDRecord());
	}

	public void add(CMDRecord record) {
		record.id = ++this.persistentSize;
		this.list.add(record);
		this.ddq.setPersistentSize(persistentSize);
		this.detecter.markDirty("rcd");
		this.onRecordAdd(record);
		this.markDirty();
	}

	public void pop() {
		if (this.list.isEmpty()) return;
		CMDRecord record = this.list.removeFirst();
		sustainMap.remove(record.getId());
	}

	protected void onRecordAdd(CMDRecord record) {
		addSustainMap(record);
		if (this.list.size() > 32) pop();
		IOS os = getOS();
		if (os.isRemote()) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("update", 0);
			os.message(this, nbt);
		}
	}

	protected void addSustainMap(CMDRecord record) {
		if (!record.isSustaining()) return;
		sustainMap.put(record.getId(), record);
		record.getSustainFuture().thenAccept(r -> sustainMap.remove(r.getId()));
	}

	protected void onSustainRecordUpdate(CMDRecord record) {
		if (!record.isSustaining()) record.getSustainFuture().complete(record);
		IOS os = getOS();
		if (os.isRemote()) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("update", record.getId());
			os.message(this, nbt);
		}
	}

	public LinkedList<CMDRecord> getRecordList() {
		return list;
	}

	public LinkedHashMap<Integer, CMDRecord> getSustainMap() {
		return sustainMap;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ISoftGui createGUIRender() {
		return new AppCommandGui(this);
	}

	@DeviceFeature(id = "exec")
	public void exec(String cmd) {
		CMDRecord record = new CMDRecord(cmd);
		try {
			CommandParser parser = new CommandParser(cmd);
			DeviceCommand command = new DeviceCommand(parser);
			IDeviceShellExecutor executor = getOS().createShellExecutor();
			executor.setLogicEnabled(true);
			DNResult result;
			List<Object> logs = null;
			try {
				command.invoke(executor);
				result = executor.getResult();
				logs = executor.getLogs();
			} catch (DeviceShellBadInvoke e) {
				result = DNResult.invalid();
				result.setReturn(e.getMessage());
			}
			record.displayObject = GameDisplayCast.cast(result.getReturn());
			if (logs != null) {
				if (record.displayObject != null) logs.add(record.displayObject);
				record.displayObject = logs;
			}

			record.code = result.code;
		} catch (IllegalArgumentException e) {
			record.code = DNResultCode.FAIL;
			record.displayObject = new TextComponentTranslation("es.app.errCmdFormat");
		}

		this.add(record);
	}

}
