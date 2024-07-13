package yuzunyannn.elementalsorcery.computer.soft.display;

import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;

public abstract class DTCBase implements ISoftDispalyTaskCondition {
	public int extra;

	@Override
	public int extraInt() {
		return extra;
	}

	public void setExtra(int extra) {
		this.extra = extra;
	}

	@Override
	public void writeSaveData(INBTWriter writer) {
		if (this.extra != 0) writer.write("e", this.extra);
	}

	@Override
	public void readSaveData(INBTReader reader) {
		this.extra = reader.nint("e");
	}

	@Override
	public void update(IOS os) {
	}

	public abstract Object getAbstractObject();

	@Override
	public <T> T cast(Class<?> to) {
		Object obj = getAbstractObject();
		if (obj == null) return null;
		if (to.isAssignableFrom(obj.getClass())) return (T) obj;
		return null;
	}

}
