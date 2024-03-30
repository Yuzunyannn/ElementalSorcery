package yuzunyannn.elementalsorcery.util.helper;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.util.INBTSerializable;

public interface INBTReader {

	UUID uuid(String key);
	
	String string(String string);
	
	<U extends NBTBase, T extends INBTSerializable<U>> T obj(String key, T serializable);

	<U extends NBTBase, T extends INBTSerializable<U>> T read(String key, Function<U, T> factory);

	<U extends NBTBase, T extends INBTSerializable<U>> List<T> readList(String key, List<T> list);

	<U extends NBTBase, T extends INBTSerializable<U>> List<T> list(String key, Function<U, T> factory);


}
