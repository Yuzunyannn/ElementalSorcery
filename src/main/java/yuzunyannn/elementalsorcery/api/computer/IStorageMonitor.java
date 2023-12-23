package yuzunyannn.elementalsorcery.api.computer;

public interface IStorageMonitor {
	void add(StoragePath path);

	void remove(StoragePath path);

	void markDirty(StoragePath path);
}
