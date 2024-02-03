package yuzunyannn.elementalsorcery.api.util.detecter;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.nbt.NBTTagString;

public class DDString extends BaseDataDetectable<String, NBTTagString> {

	public DDString(Consumer<String> setter, Supplier<String> getter) {
		super(setter, getter);
	}

	@Override
	public NBTTagString detectChanges(IDataRef<String> templateRef) {
		String temp = templateRef.get();
		String str = get();
		if (str == null || str.isEmpty()) {
			if (temp != null && !temp.isEmpty()) {
				templateRef.set(null);
				return new NBTTagString("");
			}
		} else if (!str.equals(temp)) {
			templateRef.set(str);
			return new NBTTagString(str);
		}
		return null;
	}

	@Override
	public void mergeChanges(NBTTagString nbt) {
		set(nbt.getString());
	}

}
