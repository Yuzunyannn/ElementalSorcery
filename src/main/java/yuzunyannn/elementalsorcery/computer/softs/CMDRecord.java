package yuzunyannn.elementalsorcery.computer.softs;

import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.api.computer.DNResultCode;
import yuzunyannn.elementalsorcery.nodegui.SustainSet;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTSS;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;

public class CMDRecord implements INBTSS {

	private int id;
	protected String path;
	protected String cmd;
	protected DNResultCode code;
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
		if (reader.has("code")) code = DNResultCode.fromMeta(reader.nint("code"));
	}

	@Override
	public void writeSaveData(INBTWriter writer) {
		writer.write("id", id);
		writer.write("cmd", cmd);
		if (path != null && !path.isEmpty()) writer.write("pth", path);
		if (code != null) writer.write("code", (byte) code.getMeta());
	}

	@Override
	public void readUpdateData(INBTReader reader) {
		readSaveData(reader);
		if (reader.has("dpl")) displayObject = reader.display("dpl");
		else displayObject = null;
		sSet.readUpdateData(reader);
		setDisplayObject(displayObject);
	}

	@Override
	public void writeUpdateData(INBTWriter writer) {
		writeSaveData(writer);
		sSet.writeUpdateData(writer);
		if (displayObject != null) writer.writeDisplay("dpl", displayObject);
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
