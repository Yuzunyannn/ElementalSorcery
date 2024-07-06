package yuzunyannn.elementalsorcery.computer.files;

import yuzunyannn.elementalsorcery.api.computer.DeviceFilePath;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;
import yuzunyannn.elementalsorcery.api.util.ICastable;
import yuzunyannn.elementalsorcery.api.util.render.GameDisplayCast;

public class MantraDeviceFile extends DeviceFileAdapter implements ICastable {

	protected final Mantra mantra;

	public MantraDeviceFile(DeviceFilePath path, Mantra mantra) {
		super(path);
		this.mantra = mantra;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public <T> T cast(Class<?> to) {
		if (mantra == null) return null;
		if (Mantra.class.isAssignableFrom(mantra.getClass())) return (T) mantra;
		return null;
	}

	@Override
	public Object toDisplayObject() {
		if (mantra == null) return "filedata:unknow mantra";
		return new Object[] { "filedata:", GameDisplayCast.cast(mantra) };
	}

}
