package yuzunyannn.elementalsorcery.util.detecter;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.nbt.NBTTagFloat;

public class DDFloat extends DataDetectable<Float, NBTTagFloat> {

	public DDFloat(Consumer<Float> setter, Supplier<Float> getter) {
		super(setter, getter);
	}

	@Override
	public Float copy() {
		return getter.get();
	}

	@Override
	public NBTTagFloat detectChanges(Float temp) {
		Float i = get();
		if (!i.equals(temp)) return new NBTTagFloat(i);
		return null;
	}

	@Override
	public void mergeChanges(NBTTagFloat nbt) {
		set(nbt.getFloat());
	}

}
