package yuzunyannn.elementalsorcery.api.util.detecter;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.nbt.NBTTagByte;

public class DDByte extends BaseDataDetectable<Byte, NBTTagByte> {

	public DDByte(Consumer<Byte> setter, Supplier<Byte> getter) {
		super(setter, getter);
	}

	@Override
	public NBTTagByte detectChanges(IDataRef<Byte> templateRef) {
		Byte temp = templateRef.get();
		Byte i = get();
		if (!i.equals(temp)) {
			templateRef.set(i);
			return new NBTTagByte(i);
		}
		return null;
	}

	@Override
	public void mergeChanges(NBTTagByte nbt) {
		set(nbt.getByte());
	}

}
