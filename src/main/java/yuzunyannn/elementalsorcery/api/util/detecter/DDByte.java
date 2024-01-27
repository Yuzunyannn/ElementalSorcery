package yuzunyannn.elementalsorcery.api.util.detecter;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.nbt.NBTTagByte;

public class DDByte extends DataDetectable<Byte, NBTTagByte> {

	public DDByte(Consumer<Byte> setter, Supplier<Byte> getter) {
		super(setter, getter);
	}

	@Override
	public Byte copy() {
		return getter.get();
	}

	@Override
	public NBTTagByte detectChanges(Byte temp) {
		Byte i = get();
		if (!i.equals(temp)) return new NBTTagByte(i);
		return null;
	}

	@Override
	public void mergeChanges(NBTTagByte nbt) {
		set(nbt.getByte());
	}

}
