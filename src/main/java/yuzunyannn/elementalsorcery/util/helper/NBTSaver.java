package yuzunyannn.elementalsorcery.util.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;

public class NBTSaver implements INBTReader, INBTWriter {

	public final NBTTagCompound nbt;

	public boolean isEmpty() {
		return nbt.isEmpty();
	}

	public NBTTagCompound tag() {
		return nbt;
	}

	public NBTSaver() {
		this.nbt = new NBTTagCompound();
	}

	public NBTSaver(NBTTagCompound nbt) {
		this.nbt = nbt;
	}

	@Override
	public void write(String key, UUID uuid) {
		nbt.setLong(key + "M", uuid.getMostSignificantBits());
		nbt.setLong(key + "L", uuid.getLeastSignificantBits());
	}

	@Override
	public UUID uuid(String key) {
		return new UUID(nbt.getLong(key + "M"), nbt.getLong(key + "L"));
	}

	@Override
	public void write(String key, String str) {
		nbt.setString(key, str);
	}

	@Override
	public String string(String string) {
		return nbt.getString(string);
	}

	@Override
	public void write(String key, INBTSerializable<?> serializable) {
		nbt.setTag(key, serializable.serializeNBT());
	}

	@Override
	public <U extends NBTBase, T extends INBTSerializable<U>> T obj(String key, T serializable) {
		try {
			serializable.deserializeNBT((U) nbt.getTag(key));
		} catch (Exception e) {}
		return serializable;
	}

	public <U extends NBTBase, T extends INBTSerializable<U>> T read(String key, Function<U, T> factory) {
		return factory.apply((U) nbt.getTag(key));
	}

	@Override
	public void write(String key, List<? extends INBTSerializable<?>> list) {
		NBTTagList tagList = new NBTTagList();
		for (INBTSerializable<?> serializable : list) tagList.appendTag(serializable.serializeNBT());
		nbt.setTag(key, tagList);
	}

	@Override
	public <U extends NBTBase, T extends INBTSerializable<U>> List<T> readList(String key, List<T> list) {
		try {
			NBTTagList tagList = (NBTTagList) nbt.getTag(key);
			int length = Math.min(list.size(), tagList.tagCount());
			for (int i = 0; i < length; i++) list.get(i).deserializeNBT((U) tagList.get(i));
		} catch (Exception e) {}
		return list;
	}

	@Override
	public <U extends NBTBase, T extends INBTSerializable<U>> List<T> list(String key, Function<U, T> factory) {
		List<T> list = new ArrayList<>();
		try {
			NBTTagList tagList = (NBTTagList) nbt.getTag(key);
			for (int i = 0; i < tagList.tagCount(); i++) list.add(factory.apply((U) tagList.get(i)));
		} catch (Exception e) {}
		return list;
	}
}
