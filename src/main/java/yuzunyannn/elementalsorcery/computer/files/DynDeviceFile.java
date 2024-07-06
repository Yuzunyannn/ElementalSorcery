package yuzunyannn.elementalsorcery.computer.files;

import java.util.Collection;
import java.util.function.Function;

import yuzunyannn.elementalsorcery.api.computer.DeviceFilePath;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.computer.soft.IDeviceFile;
import yuzunyannn.elementalsorcery.api.util.ICastable;

public class DynDeviceFile implements IDeviceFile, ICastable {

	protected IDeviceFile container;
	protected final DeviceFilePath path;
	protected final Function<IDeviceFile, IDeviceFile> getter;

	public DynDeviceFile(DeviceFilePath path, Function<IDeviceFile, IDeviceFile> getter) {
		this.path = path;
		this.getter = getter;
	}

	protected IDeviceFile getContainer() {
		if (this.getter == null) return null;
		return this.container = this.getter.apply(this.container);
	}

	@Override
	public boolean isDirectory() {
		IDeviceFile file = getContainer();
		if (file != null) return file.isDirectory();
		return false;
	}

	@Override
	public boolean exists() {
		return getContainer() != null;
	}

	@Override
	public boolean delete() {
		IDeviceFile file = getContainer();
		if (file != null) return file.delete();
		return false;
	}

	@Override
	public Collection<IDeviceFile> list() {
		IDeviceFile file = getContainer();
		if (file != null) return file.list();
		return null;
	}

	@Override
	public IDeviceFile child(String key) {
		IDeviceFile file = getContainer();
		if (file != null) return file.child(key);
		return IDeviceFile.invalid(path.append(key));
	}

	@Override
	public IDeviceStorage open() {
		IDeviceFile file = getContainer();
		if (file != null) return file.open();
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

	@Override
	public <T> T cast(Class<?> to) {
		IDeviceFile file = getContainer();
		if (file instanceof ICastable) return ((ICastable) file).cast(to);
		return null;
	}

	@Override
	public Object toDisplayObject() {
		IDeviceFile file = getContainer();
		if (file == null) return IDeviceFile.super.toDisplayObject();
		return file.toDisplayObject();
	}

}
