package yuzunyannn.elementalsorcery.computer.softs;

import java.util.concurrent.CompletableFuture;

import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.computer.DNResultCode;
import yuzunyannn.elementalsorcery.api.util.detecter.IDataDetectable;
import yuzunyannn.elementalsorcery.api.util.detecter.IDataRef;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTSS;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;

public class CMDRecord implements INBTSS, IDataDetectable<Object, NBTTagCompound> {
	protected int id;
	protected String path;
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
		path = reader.string("pth");
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
	}

	@Override
	public void writeUpdateData(INBTWriter writer) {
		writeSaveData(writer);
		if (displayObject != null) writer.writeDisplay("dpl", displayObject);
	}

	public boolean isSustaining() {
		return false;
	}

	public NBTTagCompound detectChanges(IDataRef<Object> templateRef) {
		return null;
	}

	public void mergeChanges(NBTTagCompound nbt) {

	}

	public CompletableFuture<CMDRecord> getSustainFuture() {
		return future;
	}

	public Object getDisplayObject() {
		return displayObject;
	}
}
