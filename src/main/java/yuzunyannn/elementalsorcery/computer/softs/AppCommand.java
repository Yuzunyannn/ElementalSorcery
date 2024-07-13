package yuzunyannn.elementalsorcery.computer.softs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.computer.DNResult;
import yuzunyannn.elementalsorcery.api.computer.DNResultCode;
import yuzunyannn.elementalsorcery.api.computer.IDeviceInfo;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.computer.soft.App;
import yuzunyannn.elementalsorcery.api.computer.soft.DeviceShellBadInvoke;
import yuzunyannn.elementalsorcery.api.computer.soft.IDeviceFile;
import yuzunyannn.elementalsorcery.api.computer.soft.IDeviceShellExecutor;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGui;
import yuzunyannn.elementalsorcery.api.util.render.GameDisplayCast;
import yuzunyannn.elementalsorcery.api.util.var.Variable;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.computer.soft.AppBase;
import yuzunyannn.elementalsorcery.computer.soft.CommandParser;
import yuzunyannn.elementalsorcery.computer.soft.DeviceCommand;
import yuzunyannn.elementalsorcery.nodegui.SustainDisplayManager;
import yuzunyannn.elementalsorcery.tile.device.DeviceFeature;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;
import yuzunyannn.elementalsorcery.util.helper.JavaHelper;

public class AppCommand extends AppBase {

	public static final String ID = "command";
	public static final Variable<ArrayList<String>> CMD_CACHE = new Variable("hls", VariableSet.STRING_ARRAY_LIST);
	protected DDQueue<CMDRecord> ddq = new DDQueue();
	protected LinkedList<CMDRecord> list = new LinkedList<>();
	protected SustainDisplayManager sMgr;
	protected int persistentSize = 0;
	protected Side side;

	public AppCommand(IOS os, int pid) {
		super(os, pid);
		this.side = os.isRemote() ? Side.CLIENT : Side.SERVER;
		ddq.setPersistentSize(persistentSize);
		ddq.setQueue(list, () -> new CMDRecord(side));
		ddq.onElementAdd = rcd -> this.onRecordAdd(rcd);
		ddq.onElementRemove = rcd -> this.onRecordRemove(rcd);
		this.sMgr = new SustainDisplayManager(side);
		this.detecter.add("rcd", ddq);
		this.detecter.add("rcds", this.sMgr.getDataDetectable(), 4);
		this.sMgr.setEnv(IOS.class, os);
		this.sMgr.setEnv(App.class, this);
		this.sMgr.enableDigestDeduplication();
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
		this.list = new LinkedList<>(reader.list("record", () -> new CMDRecord(side)));
		this.persistentSize = reader.nint("ps");
		this.sMgr.clear();
		for (CMDRecord record : this.list) addSustainMap(record);
		ddq.setPersistentSize(persistentSize);
		ddq.setQueue(list, () -> new CMDRecord(side));
	}

	public void add(CMDRecord record) {
		record.setId(++this.persistentSize);
		this.list.add(record);
		this.ddq.setPersistentSize(persistentSize);
		this.detecter.markDirty("rcd");
		this.onRecordAdd(record);
		this.markDirty();
	}

	public void pop() {
		if (this.list.isEmpty()) return;
		CMDRecord record = this.list.removeFirst();
		sMgr.remove(record.getId());
	}

	public void clear() {
		this.persistentSize = 0;
		this.list.clear();
		this.ddq.setPersistentSize(0);
		this.sMgr.clear();
		this.detecter.markDirty("rcd");
		this.markDirty();
	}

	protected void onRecordAdd(CMDRecord record) {
		addSustainMap(record);
		IDeviceInfo info = getOS().getDeviceInfo();
		int popCount = info.isMobile() ? 8 : 32;
		if (this.list.size() > popCount) pop();
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
		sMgr.add(record.getSustainSet());
	}

	public LinkedList<CMDRecord> getRecordList() {
		return list;
	}

	public SustainDisplayManager getSustainManager() {
		return sMgr;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ISoftGui createGUIRender() {
		return new AppCommandGui(this);
	}

	@DeviceFeature(id = "exec")
	public void exec(String cmd) {
		CMDRecord record = new CMDRecord(cmd);
		Object displayObject = null;
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
			else displayObject = GameDisplayCast.cast(ret);
			if (logs != null) {
				if (displayObject != null) logs.add(displayObject);
				displayObject = logs;
			}
			record.code = result.code;
		} catch (IllegalArgumentException e) {
			record.code = DNResultCode.FAIL;
			displayObject = new TextComponentTranslation("es.app.errCmdFormat");
		}
		record.setDisplayObject(displayObject);
		this.add(record);
	}

}
