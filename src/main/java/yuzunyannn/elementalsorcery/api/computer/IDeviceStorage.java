package yuzunyannn.elementalsorcery.api.computer;

import yuzunyannn.elementalsorcery.api.util.var.IVariableSet;

public interface IDeviceStorage extends IVariableSet, ICalculatorObject {

	boolean isClose();

	void close();

	IDeviceStorage markDirty();

//	public static final int FLAG_READ_ONLY = 0x01;

//	public DeviceStorage setFlag(int flag, boolean has);

//	public void markDirty(StoragePath path);
//
//	default public void markDirty(String... strings) {
//		markDirty(StoragePath.of(strings));
//	}
//
//	default public void markDirty(Variable var) {
//		markDirty(var.key);
//	}

//	public boolean hasFlag(int flag);

//	default boolean isWriteable() {
//		return !hasFlag(FLAG_READ_ONLY);
//	}
//
//	default boolean isReadable() {
//		return true;
//	}

}
