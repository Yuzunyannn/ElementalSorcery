package yuzunyannn.elementalsorcery.computer;

import yuzunyannn.elementalsorcery.api.computer.IMemory;

public class Memory extends DeviceStorage implements IMemory {

	@Override
	public Memory copy() {
		Memory memory = new Memory();
		memory.deserializeNBT(this.serializeNBT());
		return memory;
	}

}
