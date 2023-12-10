package yuzunyannn.elementalsorcery.api.computer;

import yuzunyannn.elementalsorcery.computer.Disk;
import yuzunyannn.elementalsorcery.computer.Memory;

public interface IDeviceModifiable extends IDevice {

	void addDisk(Disk disk);

	IDisk removeDisk(int index);

	void setMemory(Memory memory);

	void setName(String string);

}
