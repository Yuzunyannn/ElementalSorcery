package yuzunyannn.elementalsorcery.api.util.detecter;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Deprecated //use IDataDetectable 
public class ContainerArrayDetecter<T, U extends NBTBase> {

	public static interface ICanArrayDetected<T, U extends NBTBase> {

		int getSize();

		@SideOnly(Side.CLIENT)
		void setSize(int size);

		boolean hasChange(int index, T oldValue);

		T copyCurrValue(int index);

		U serializeCurrValueToSend(int index);

		@SideOnly(Side.CLIENT)
		void deserializeCurrValueFromSend(int index, U nbtData);
	}

	static final public byte T_SIZE_CHANGE = 1;
	static final public byte T_NEW_VALUE = 2;

	public Object[] lastObjs;

	public void setCount(int n) {
		Object[] ll = lastObjs;
		lastObjs = new Object[n];
		if (ll == null) return;
		for (int i = 0; i < Math.min(ll.length, lastObjs.length); i++) lastObjs[i] = ll[i];
	}

	public T[] getArray() {
		return (T[]) lastObjs;
	}

	public NBTTagList detecte(ICanArrayDetected<T, U> access) {
		NBTTagList changeList = new NBTTagList();

		int oldSize = lastObjs == null ? -1 : lastObjs.length;
		if (lastObjs == null) setCount(access.getSize());
		if (access.getSize() != lastObjs.length) setCount(access.getSize());

		if (oldSize != lastObjs.length) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setByte("T", T_SIZE_CHANGE);
			nbt.setInteger("D", lastObjs.length);
			changeList.appendTag(nbt);
		}

		for (int i = 0; i < access.getSize(); i++) {
			T lastObj = (T) lastObjs[i];
			boolean hasAnyChange = false;
			if (lastObj == null) hasAnyChange = true;
			else hasAnyChange = access.hasChange(i, lastObj);
			if (!hasAnyChange) continue;
			T obj = access.copyCurrValue(i);
			lastObjs[i] = obj;
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setByte("T", T_NEW_VALUE);
			nbt.setTag("D", access.serializeCurrValueToSend(i));
			nbt.setShort("I", (short) i);
			changeList.appendTag(nbt);
		}

		return changeList;
	}

	@SideOnly(Side.CLIENT)
	public void recvChangeList(NBTTagList changeList, ICanArrayDetected<T, U> access) {
		for (int i = 0; i < changeList.tagCount(); i++) {
			NBTTagCompound dat = changeList.getCompoundTagAt(i);
			byte type = dat.getByte("T");
			switch (type) {
			case T_SIZE_CHANGE:
				List<U> oldList = new ArrayList<>(access.getSize());
				for (int j = 0; j < access.getSize(); j++) oldList.add(access.serializeCurrValueToSend(i));
				access.setSize(dat.getInteger("D"));
				for (int j = 0; j < Math.min(access.getSize(), oldList.size()); j++)
					access.deserializeCurrValueFromSend(j, oldList.get(i));
				break;
			case T_NEW_VALUE:
				access.deserializeCurrValueFromSend(dat.getInteger("I"), (U) dat.getTag("D"));
				break;
			}
		}
	}
}
