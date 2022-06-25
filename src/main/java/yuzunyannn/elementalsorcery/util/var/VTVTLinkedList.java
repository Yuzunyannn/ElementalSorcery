package yuzunyannn.elementalsorcery.util.var;

import java.util.LinkedList;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import yuzunyannn.elementalsorcery.util.NBTTag;

public class VTVTLinkedList<T> implements IVariableType<LinkedList<T>> {

	public final IVariableType<T> type;

	public VTVTLinkedList(IVariableType<T> type) {
		this.type = type;
	}

	@Override
	public LinkedList<T> newInstance(NBTBase base) {
		if (base != null && base.getId() == NBTTag.TAG_LIST) {
			NBTTagList array = (NBTTagList) base;
			LinkedList<T> list = new LinkedList<T>();
			for (NBTBase b : array) list.add(type.newInstance(b));
			return list;
		}
		return new LinkedList<T>();
	}

	@Override
	public NBTBase serializable(LinkedList<T> objs) {
		NBTTagList array = new NBTTagList();
		for (T obj : objs) array.appendTag(type.serializableObject(obj));
		return array;
	}

}
