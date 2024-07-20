package yuzunyannn.elementalsorcery.tile;

import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTSS;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;

public class TileTask implements INBTSS {

	TileTask next;
	TileTask prev;
	TileTaskManager mgr;
	int tid;
	int rid;

	public boolean inManager() {
		return this.mgr != null;
	}

	public int getTypeId() {
		return tid;
	}

	public int getRuntimeId() {
		return rid;
	}

	protected boolean isDead;

	public void onEnter() {
		isDead = false;
	}

	public void onExit() {

	}

	public void onUpdate() {

	}

	// 0 create sync 1 dead sync
	public boolean needSyncToClient(int mode) {
		return mode == 0;
	}


	public void setDead() {
		this.isDead = true;
	}

	@Override
	public void writeSaveData(INBTWriter writer) {
		writer.write("tid", (byte) tid);
		writer.write("rid", (short) rid);
	}

	@Override
	public void readSaveData(INBTReader reader) {
		tid = reader.nint("tid");
		rid = reader.nint("rid");
	}

	@Override
	public void writeUpdateData(INBTWriter writer) {
		writer.write("tid", (byte) tid);
		writer.write("rid", (short) rid);
	}

	@Override
	public void readUpdateData(INBTReader reader) {
		tid = reader.nint("tid");
		rid = reader.nint("rid");
	}

}
