package yuzunyannn.elementalsorcery.computer.soft.display;

import java.util.UUID;

import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.util.target.IObjectGetter;
import yuzunyannn.elementalsorcery.computer.Computer;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;

public class DTCDevice extends DTCBase {

	public final static int ID = 1;

	protected IObjectGetter<IDevice> getter;
	protected UUID uuid;
	protected int dieDelay = 10;

	public DTCDevice() {

	}

	public DTCDevice(IDevice device) {
		this.uuid = device.getUDID();
	}

	@Override
	public int cid() {
		return DTCDevice.ID;
	}

	@Override
	public IDevice getAbstractObject() {
		if (getter != null) return getter.softGet();
		return null;
	}

	@Override
	public boolean isAlive() {
		if (getAbstractObject() == null) {
			dieDelay--;
			return dieDelay > 0;
		}
		dieDelay = 10;
		return true;
	}

	@Override
	public void update(IOS os) {
		if (this.uuid == null) return;
		if (getter == null) {
			getter = os.askCapability(uuid, Computer.DEVICE_CAPABILITY, null);
			getter.toughGet();
		}
	}

	@Override
	public void writeSaveData(INBTWriter writer) {
		super.writeSaveData(writer);
		if (this.uuid != null) writer.write("uuid", this.uuid);
	}

	@Override
	public void readSaveData(INBTReader reader) {
		super.readSaveData(reader);
		this.uuid = reader.has("uuid") ? reader.uuid("uuid") : null;
	}

}
