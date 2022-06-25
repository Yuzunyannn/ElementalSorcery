package yuzunyannn.elementalsorcery.util.var;

import java.util.ArrayList;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import yuzunyannn.elementalsorcery.util.NBTTag;

public class VTVTArrayList<T> implements IVariableType<ArrayList<T>> {

	public final IVariableType<T> type;

	public VTVTArrayList(IVariableType<T> type) {
		this.type = type;
	}

	@Override
	public ArrayList<T> newInstance(NBTBase base) {
		if (base != null && base.getId() == NBTTag.TAG_LIST) {
			NBTTagList array = (NBTTagList) base;
			ArrayList<T> list = new ArrayList<T>();
			for (NBTBase b : array) list.add(type.newInstance(b));
			return list;
		}
		return new ArrayList<T>();
	}

	@Override
	public NBTBase serializable(ArrayList<T> objs) {
		NBTTagList array = new NBTTagList();
		for (T obj : objs) array.appendTag(type.serializableObject(obj));
		return array;
	}

}
