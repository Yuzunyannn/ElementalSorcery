package yuzunyannn.elementalsorcery.computer.softs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.computer.DNResult;
import yuzunyannn.elementalsorcery.api.computer.DNResultCode;
import yuzunyannn.elementalsorcery.api.computer.IDeviceInfo;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.computer.soft.DeviceShellBadInvoke;
import yuzunyannn.elementalsorcery.api.computer.soft.IDeviceFile;
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
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;
import yuzunyannn.elementalsorcery.util.helper.JavaHelper;

public class AppCommand extends AppBase {

	public static final String ID = "command";
	public static final Variable<ArrayList<String>> CMD_CACHE = new Variable("hls", VariableSet.STRING_ARRAY_LIST);
	protected DDQueue<CMDRecord> ddq = new DDQueue();
	protected LinkedList<CMDRecord> list = new LinkedList<>();
	protected LinkedHashMap<Integer, CMDRecord> sustainMap = new LinkedHashMap<>();
	protected int persistentSize = 0;
	protected IDataDetectable<Map<Integer, IDataRef<Object>>, NBTTagList> dds = new IDataDetectable<Map<Integer, IDataRef<Object>>, NBTTagList>() {

		@Override
		public NBTTagList detectChanges(IDataRef<Map<Integer, IDataRef<Object>>> templateRef) {
			Map<Integer, IDataRef<Object>> tmp = templateRef.get();
			if (tmp == null) templateRef.set(tmp = new HashMap<>());
			NBTTagList list = new NBTTagList();
			for (CMDRecord record : sustainMap.values()) {
				IDataRef<Object> recordRef = tmp.get(record.getId());
				if (recordRef == null) {
					tmp.put(record.getId(), new IDataRef.Simple<Object>());
					continue;// 第一次直接走，第一次走默认的ddq
				}
				NBTTagCompound changes = record.detectChanges(recordRef);
				if (changes == null) continue;
				changes.setInteger("id", record.id);
				list.appendTag(changes);
			}
			return list.isEmpty() ? null : list;
		}

		@Override
		public void mergeChanges(NBTTagList list) {
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound nbt = list.getCompoundTagAt(i);
				int id = nbt.getInteger("id");
				CMDRecord record = sustainMap.get(id);
				if (record == null) continue;
				record.mergeChanges(nbt);
				onSustainRecordUpdate(record, nbt);
			}
		}

	};

	public AppCommand(IOS os, int pid) {
		super(os, pid);
		ddq.setPersistentSize(persistentSize);
		ddq.setQueue(list, () -> new CMDRecord());
		ddq.onElementAdd = rcd -> this.onRecordAdd(rcd);
		ddq.onElementRemove = rcd -> this.onRecordRemove(rcd);
		this.detecter.add("rcd", ddq);
		this.detecter.add("rcds", dds, 20);
	}

	@Override
	public void onStartup() {
		super.onStartup();
		IDeviceFile file = getOS().ioAppData("console", "history");
		IDeviceStorage storage = file.open();
		if (storage != null) {
			List<String> cmds = storage.get(CMD_CACHE);
			storage.close();
			for (String str : cmds) add(new CMDRecord(str));
			IDeviceInfo info = getOS().getDeviceInfo();
			if (info.isMobile()) file.delete();
		}
	}

	@Override
	public void onExit() {
		super.onExit();
		IDeviceFile file = getOS().ioAppData("console", "history");
		IDeviceStorage storage = file.open();
		if (storage != null) {
			storage.set(CMD_CACHE, JavaHelper.toList(list, record -> record.cmd));
			storage.markDirty();
			storage.close();
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

	public void clear() {
		this.persistentSize = 0;
		this.list.clear();
		this.ddq.setPersistentSize(0);
		this.detecter.markDirty("rcd");
		this.markDirty();
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

	protected void onRecordRemove(CMDRecord record) {
		IOS os = getOS();
		if (os.isRemote()) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("update", -1);
			os.message(this, nbt);
		}
	}

	protected void addSustainMap(CMDRecord record) {
		if (!record.isSustaining()) return;
		sustainMap.put(record.getId(), record);
		record.getSustainFuture().thenAccept(r -> sustainMap.remove(r.getId()));
	}

	protected void onSustainRecordUpdate(CMDRecord record, NBTTagCompound lastUpdate) {
		if (!record.isSustaining()) record.getSustainFuture().complete(record);
		IOS os = getOS();
		if (os.isRemote()) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("update", record.getId());
			nbt.setTag("last", lastUpdate);
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
			if (command.isClear()) {
				this.clear();
				return;
			}
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
			Object ret = result.getReturn();
			if (ret instanceof DNResultCode);
			else record.displayObject = GameDisplayCast.cast(ret);
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
