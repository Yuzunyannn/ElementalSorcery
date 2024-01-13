package yuzunyannn.elementalsorcery.api.computer;

import yuzunyannn.elementalsorcery.computer.Disk;

public interface IDeviceModifiable extends IDevice {

	void addDisk(Disk disk);

	IDisk removeDisk(int index);

	void setName(String string);

}
