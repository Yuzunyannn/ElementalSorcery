package yuzunyannn.elementalsorcery.computer.files;

import java.util.Collection;

import yuzunyannn.elementalsorcery.api.computer.DeviceFilePath;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.computer.soft.IDeviceFile;

public abstract class DeviceFileAdapter implements IDeviceFile {

	protected final DeviceFilePath path;

	public DeviceFileAdapter(DeviceFilePath path) {
		this.path = path;
	}

	@Override
	public boolean isDirectory() {
		return false;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public boolean delete() {
		return false;
	}

	@Override
	public Collection<IDeviceFile> list() {
		return null;
	}

	@Override
	public IDeviceFile child(String key) {
		return IDeviceFile.invalid(path.append(key));
	}

	@Override
	public IDeviceStorage open() {
		return null;
	}

	@Override
	public DeviceFilePath getPath() {
		return path;
	}

	@Override
	public String getName() {
		return path.getName();
	}

}
