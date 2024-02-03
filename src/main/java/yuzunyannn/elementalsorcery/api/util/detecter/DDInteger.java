package yuzunyannn.elementalsorcery.api.util.detecter;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.nbt.NBTTagInt;

public class DDInteger extends BaseDataDetectable<Integer, NBTTagInt> {

	public DDInteger(Consumer<Integer> setter, Supplier<Integer> getter) {
		super(setter, getter);
	}

	@Override
	public NBTTagInt detectChanges(IDataRef<Integer> templateRef) {
		Integer temp = templateRef.get();
		Integer i = get();
		if (!i.equals(temp)) {
			templateRef.set(i);
			return new NBTTagInt(i);
		}
		return null;
	}

	@Override
	public void mergeChanges(NBTTagInt nbt) {
		set(nbt.getInt());
	}

}
