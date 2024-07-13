package yuzunyannn.elementalsorcery.util.helper;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;
import yuzunyannn.elementalsorcery.api.util.target.CapabilityObjectRef;

public interface INBTReader {

	boolean has(String key);

	float nfloat(String key);

	double ndouble(String key);

	byte nbyte(String key);

	short nshort(String key);

	int nint(String key);

	long nlong(String key);

	boolean nboolean(String key);

	byte[] bytes(String key);

	<T> T sobj(String key, Function<PacketBuffer, T> reader);

	<U extends NBTBase, T extends INBTSerializable<U>> T obj(String key, T serializable);

	<U extends NBTBase, T extends INBTSerializable<U>> T obj(String key, Function<U, T> factory);

	<U extends NBTBase, T extends INBTSerializable<U>> T obj(String key, Supplier<T> factory);

	<U extends NBTBase, T extends INBTSerializable<U>> List<T> list(String key, List<T> list);

	<U extends NBTBase, T extends INBTSerializable<U>> List<T> list(String key, Function<U, T> factory);

	<U extends NBTBase, T extends INBTSerializable<U>> List<T> list(String key, Supplier<T> factory);

	<U extends NBTBase, T extends INBTSerializable<U>, L extends List<T>> L list(String key, L list,
			Supplier<T> factory);

	UUID uuid(String key);

	List<UUID> uuids(String key);

	String string(String key);

	EnumFacing facing(String key);

	ItemStack itemStack(String key);

	Mantra mantra(String key);

	NBTTagCompound compoundTag(String key);

	CapabilityObjectRef capabilityObjectRef(String key);

	Object display(String key);
}
