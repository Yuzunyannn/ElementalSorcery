package yuzunyannn.elementalsorcery.api.util.detecter;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.nbt.NBTTagInt;

public class DDInteger extends DataDetectable<Integer, NBTTagInt> {

	public DDInteger(Consumer<Integer> setter, Supplier<Integer> getter) {
		super(setter, getter);
	}

	@Override
	public Integer copy() {
		return getter.get();
	}

	@Override
	public NBTTagInt detectChanges(Integer temp) {
		Integer i = get();
		if (!i.equals(temp)) return new NBTTagInt(i);
		return null;
	}

	@Override
	public void mergeChanges(NBTTagInt nbt) {
		set(nbt.getInt());
	}

}
