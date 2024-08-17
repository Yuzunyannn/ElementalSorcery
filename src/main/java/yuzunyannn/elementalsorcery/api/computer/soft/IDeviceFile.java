package yuzunyannn.elementalsorcery.api.computer.soft;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import yuzunyannn.elementalsorcery.api.computer.DeviceFilePath;
import yuzunyannn.elementalsorcery.api.computer.ICalculatorObject;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.util.ICastable;
import yuzunyannn.elementalsorcery.api.util.render.IDisplayable;

public interface IDeviceFile extends ICalculatorObject, IDisplayable, ICastable {

	boolean isDirectory();

	boolean exists();

	boolean delete();

	@Nullable
	Collection<IDeviceFile> list();

	@Nonnull
	IDeviceFile child(String key);

	@Nullable
	IDeviceStorage open();

	@Nonnull
	DeviceFilePath getPath();

	@Nonnull
	String getName();

	@Override
	default <T> T cast(Class<?> to) {
		return null;
	}

	@Override
	default Object toDisplayObject() {
		if (isDirectory()) return "folder:" + getPath().toString();
		if (exists()) return "file:" + getPath().toString();
		return "noexist:" + getPath().toString();
	}

	@Nullable
	public static IDeviceFile route(IDeviceFile root, DeviceFilePath path) {
		if (path.isEmpty()) return root;
		for (String key : path) if (!key.isEmpty()) root = root.child(key);
		return root;
	}

	public static IDeviceFile invalid(DeviceFilePath path) {
		return new DeviceFileInvalid(path);
	}

}
