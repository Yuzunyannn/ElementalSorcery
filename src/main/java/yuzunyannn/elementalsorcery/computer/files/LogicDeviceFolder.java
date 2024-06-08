package yuzunyannn.elementalsorcery.computer.files;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import yuzunyannn.elementalsorcery.api.computer.DeviceFilePath;
import yuzunyannn.elementalsorcery.api.computer.soft.IDeviceFile;

public class LogicDeviceFolder extends DeviceFileAdapter {

	protected final Map<String, Function<DeviceFilePath, IDeviceFile>> folderMap = new HashMap<>();

	public LogicDeviceFolder(DeviceFilePath path) {
		super(path);
	}

	public void set(String folder, Function<DeviceFilePath, IDeviceFile> factory) {
		folderMap.put(folder, factory);
	}

	@Override
	public boolean isDirectory() {
		return true;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public Collection<IDeviceFile> list() {
		LinkedList<IDeviceFile> children = new LinkedList<>();
		for (Entry<String, Function<DeviceFilePath, IDeviceFile>> entry : folderMap.entrySet()) {
			IDeviceFile file = entry.getValue().apply(path.append(entry.getKey()));
			if (file != null) children.add(file);
		}
		return children;
	}

	@Override
	public IDeviceFile child(String key) {
		DeviceFilePath childPath = path.append(key);
		Function<DeviceFilePath, IDeviceFile> factory = folderMap.get(key);
		IDeviceFile child = factory != null ? factory.apply(childPath) : null;
		if (child == null) child = IDeviceFile.invalid(childPath);
		return child;
	}

}
