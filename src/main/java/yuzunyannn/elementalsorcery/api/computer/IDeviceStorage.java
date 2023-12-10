package yuzunyannn.elementalsorcery.api.computer;

import yuzunyannn.elementalsorcery.api.util.var.IVariableSet;
import yuzunyannn.elementalsorcery.computer.DeviceStorage;

public interface IDeviceStorage extends IVariableSet, ICalculatorObject {

	public static final int FLAG_READ_ONLY = 0x01;

	public DeviceStorage setFlag(int flag, boolean has);

	public boolean hasFlag(int flag);

	default boolean isWriteable() {
		return !hasFlag(FLAG_READ_ONLY);
	}

	default boolean isReadable() {
		return true;
	}

}
