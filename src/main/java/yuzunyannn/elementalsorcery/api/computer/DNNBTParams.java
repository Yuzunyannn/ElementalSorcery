package yuzunyannn.elementalsorcery.api.computer;

import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.util.GameCast;
import yuzunyannn.elementalsorcery.api.util.var.IVariableType;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;

public class DNNBTParams extends DNRequest {

	protected NBTTagCompound nbt;

	public DNNBTParams(NBTTagCompound nbt) {
		this.nbt = nbt;
	}

	@Override
	public int size() {
		return super.size() + nbt.getSize();
	}

	@Override
	public <T> void set(String key, T obj) {
		super.set(key, obj);
		this.nbt.removeTag(key);
	}

	@Override
	public <T> T get(String key, Class<T> cls) {
		T obj = super.get(key, cls);
		if (obj != null) return obj;
		return askNBT(key, cls);
	}

	@Override
	public <T> T ask(String key, Class<T> cls) {
		T obj = super.ask(key, cls);
		if (obj != null) return obj;
		return askNBT(key, cls);
	}

	protected <T> T askNBT(String key, Class<T> cls) {
		return GameCast.cast(this, nbt.getTag(key), cls);
	}
}