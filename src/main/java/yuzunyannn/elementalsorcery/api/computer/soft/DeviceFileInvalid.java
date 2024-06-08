package yuzunyannn.elementalsorcery.api.computer.soft;

import java.util.Collection;

import yuzunyannn.elementalsorcery.api.computer.DeviceFilePath;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;

public final class DeviceFileInvalid implements IDeviceFile {

	final DeviceFilePath path;

	DeviceFileInvalid(DeviceFilePath path) {
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
		return new DeviceFileInvalid(path.append(key));
	}

	@Override
	public IDeviceStorage open() {
		return null;
	}

	@Override
	public DeviceFilePath getPath() {
		return this.path;
	}

	@Override
	public String getName() {
		return this.path.getName();
	}

}
