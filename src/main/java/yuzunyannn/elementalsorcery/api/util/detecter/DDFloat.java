package yuzunyannn.elementalsorcery.api.util.detecter;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.nbt.NBTTagFloat;

public class DDFloat extends BaseDataDetectable<Float, NBTTagFloat> {

	public DDFloat(Consumer<Float> setter, Supplier<Float> getter) {
		super(setter, getter);
	}

	@Override
	public NBTTagFloat detectChanges(IDataRef<Float> templateRef) {
		Float temp = templateRef.get();
		Float i = get();
		if (!i.equals(temp)) {
			templateRef.set(i);
			return new NBTTagFloat(i);
		}
		return null;
	}

	@Override
	public void mergeChanges(NBTTagFloat nbt) {
		set(nbt.getFloat());
	}

}
