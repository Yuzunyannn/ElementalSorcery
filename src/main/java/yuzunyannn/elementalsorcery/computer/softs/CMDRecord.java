package yuzunyannn.elementalsorcery.computer.softs;

import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.api.util.StateCode;
import yuzunyannn.elementalsorcery.nodegui.SustainSet;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTSS;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;

public class CMDRecord implements INBTSS {

	private int id;
	protected String path;
	protected String cmd;
	protected StateCode code;
	private Object displayObject;
	protected SustainSet sSet = new SustainSet();

	public CMDRecord(Side side) {
		this.cmd = "";
		this.sSet.setSide(side);
	}

	public CMDRecord(String cmd) {
		this.cmd = cmd;
	}

	public final void setId(int id) {
		this.id = id;
		this.sSet.setId(id);
	}

	public final int getId() {
		return id;
	}

	public SustainSet getSustainSet() {
		return sSet;
	}

	@Override
	public void readSaveData(INBTReader reader) {
		id = reader.nint("id");
		cmd = reader.string("cmd");
		path = reader.string("pth");
		sSet.setId(id);
		if (reader.has("code")) code = StateCode.fromMeta(reader.nint("code"));
		setDisplayObject(reader.display("dpo"));
	}

	@Override
	public void writeSaveData(INBTWriter writer) {
		writer.write("id", id);
		writer.write("cmd", cmd);
		if (path != null && !path.isEmpty()) writer.write("pth", path);
		if (code != null) writer.write("code", (byte) code.getMeta());
		writer.writeDisplay("dpo", displayObject);
	}

	@Override
	public void readUpdateData(INBTReader reader) {
		readSaveData(reader);
		sSet.readUpdateData(reader);
	}

	@Override
	public void writeUpdateData(INBTWriter writer) {
		writeSaveData(writer);
		sSet.writeUpdateData(writer);
	}

	public void setDisplayObject(Object obj) {
		this.displayObject = obj;
		this.sSet.clear();
		this.sSet.seek(obj);
	}

	public Object getDisplayObject() {
		return displayObject;
	}
}
