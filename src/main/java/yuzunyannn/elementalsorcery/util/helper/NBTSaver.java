package yuzunyannn.elementalsorcery.util.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.api.util.render.GameDisplayCast;
import yuzunyannn.elementalsorcery.api.util.target.CapabilityObjectRef;

public class NBTSaver implements INBTReader, INBTWriter {

	protected NBTTagCompound nbt;

	public boolean isEmpty() {
		return nbt.isEmpty();
	}

	public NBTTagCompound tag() {
		return nbt;
	}

	public NBTTagCompound spitOut() {
		if (nbt.isEmpty()) return null;
		NBTTagCompound out = nbt;
		nbt = new NBTTagCompound();
		return out;
	}

	public NBTSaver() {
		this.nbt = new NBTTagCompound();
	}

	public NBTSaver(NBTTagCompound nbt) {
		this.nbt = nbt;
	}

	@Override
	public boolean has(String key) {
		return nbt.hasKey(key);
	}
	
	@Override
	public void write(String key, int[] ints) {
		nbt.setIntArray(key, ints);
	}
	
	@Override
	public int[] nints(String key) {
		return nbt.getIntArray(key);
	}

	@Override
	public void write(String key, UUID uuid) {
		byte[] bytes = new byte[16];
		long mostSignificantBits = uuid.getMostSignificantBits();
		long leastSignificantBits = uuid.getLeastSignificantBits();
		for (int i = 0; i < 8; i++) {
			bytes[i] = (byte) (mostSignificantBits >> (8 * (7 - i)));
			bytes[i + 8] = (byte) (leastSignificantBits >> (8 * (7 - i)));
		}
		nbt.setByteArray(key, bytes);
	}

	@Override
	public UUID uuid(String key) {
		try {
			byte[] bytes = nbt.getByteArray(key);
			long mostSignificantBits = 0;
			long leastSignificantBits = 0;
			for (int i = 0; i < 8; i++) {
				mostSignificantBits |= (long) (bytes[i] & 0xFF) << ((7 - i) * 8);
				leastSignificantBits |= (long) (bytes[i + 8] & 0xFF) << ((7 - i) * 8);
			}
			return new UUID(mostSignificantBits, leastSignificantBits);
		} catch (Exception e) {
			return UUID.randomUUID();
		}
	}

	@Override
	public void writeUUIDs(String key, List<UUID> uuids) {
		byte[] bytes = new byte[16 * uuids.size()];
		int index = 0;
		for (UUID uuid : uuids) {
			JavaHelper.write(bytes, index * 16, uuid.getMostSignificantBits());
			JavaHelper.write(bytes, index * 16 + 8, uuid.getLeastSignificantBits());
			index++;
		}
		nbt.setByteArray(key, bytes);
	}

	@Override
	public List<UUID> uuids(String key) {
		try {
			byte[] bytes = nbt.getByteArray(key);
			int count = bytes.length / 16;
			List<UUID> list = new ArrayList<>(count);
			for (int i = 0; i < count; i++) {
				long mostSignificantBits = JavaHelper.readLong(bytes, i * 16);
				long leastSignificantBits = JavaHelper.readLong(bytes, i * 16 + 8);
				list.add(new UUID(mostSignificantBits, leastSignificantBits));
			}
			return list;
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	@Override
	public void write(String key, String str) {
		nbt.setString(key, str);
	}

	@Override
	public String string(String string) {
		return nbt.getString(string);
	}

	protected NBTBase serialize(INBTSerializable<?> serializable) {
		return serializable.serializeNBT();
	}

	protected <U extends NBTBase, T extends INBTSerializable<U>> void deserialize(U nbt, T obj) {
		obj.deserializeNBT(nbt);
	}

	@Override
	public void write(String key, INBTSerializable<?> serializable) {
		nbt.setTag(key, serialize(serializable));
	}

	@Override
	public <U extends NBTBase, T extends INBTSerializable<U>> T obj(String key, T obj) {
		if (obj == null) return null;
		try {
			deserialize((U) nbt.getTag(key), obj);
		} catch (Exception e) {}
		return obj;
	}

	@Override
	public <U extends NBTBase, T extends INBTSerializable<U>> T obj(String key, Supplier<T> factory) {
		T obj = factory.get();
		try {
			deserialize((U) nbt.getTag(key), obj);
		} catch (Exception e) {}
		return obj;
	}

	@Override
	public <U extends NBTBase, T extends INBTSerializable<U>> T obj(String key, Function<U, T> factory) {
		return factory.apply((U) nbt.getTag(key));
	}

	@Override
	public void write(String key, List<? extends INBTSerializable<?>> list) {
		NBTTagList tagList = new NBTTagList();
		for (INBTSerializable<?> serializable : list) tagList.appendTag(serialize(serializable));
		nbt.setTag(key, tagList);
	}

	@Override
	public <U extends NBTBase, T extends INBTSerializable<U>> List<T> list(String key, List<T> list) {
		try {
			NBTTagList tagList = (NBTTagList) nbt.getTag(key);
			int length = Math.min(list.size(), tagList.tagCount());
			for (int i = 0; i < length; i++) deserialize((U) tagList.get(i), list.get(i));
		} catch (Exception e) {}
		return list;
	}

	@Override
	public <U extends NBTBase, T extends INBTSerializable<U>> List<T> list(String key, Supplier<T> factory) {
		List<T> list = new ArrayList<>();
		try {
			NBTTagList tagList = (NBTTagList) nbt.getTag(key);
			for (int i = 0; i < tagList.tagCount(); i++) {
				T obj = factory.get();
				deserialize((U) tagList.get(i), obj);
				list.add(obj);
			}
		} catch (Exception e) {}
		return list;
	}

	@Override
	public <U extends NBTBase, T extends INBTSerializable<U>, L extends List<T>> L list(String key, L list,
			Supplier<T> factory) {
		try {
			list.clear();
			NBTTagList tagList = (NBTTagList) nbt.getTag(key);
			for (int i = 0; i < tagList.tagCount(); i++) {
				T obj = factory.get();
				deserialize((U) tagList.get(i), obj);
				list.add(obj);
			}
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

	@Override
	public void write(String key, EnumFacing facing) {
		nbt.setByte(key, (byte) facing.getIndex());
	}

	@Override
	public EnumFacing facing(String key) {
		return EnumFacing.byIndex(nbt.getByte(key));
	}

	@Override
	public void write(String key, ItemStack stack) {
		nbt.setTag(key, stack.serializeNBT());
	}

	@Override
	public ItemStack itemStack(String key) {
		return new ItemStack(nbt.getCompoundTag(key));
	}

	@Override
	public void write(String key, float val) {
		nbt.setFloat(key, val);
	}

	@Override
	public void write(String key, double val) {
		nbt.setDouble(key, val);
	}

	@Override
	public void write(String key, byte val) {
		nbt.setByte(key, val);
	}

	@Override
	public void write(String key, short val) {
		nbt.setShort(key, val);
	}

	@Override
	public void write(String key, int val) {
		nbt.setInteger(key, val);
	}

	@Override
	public void write(String key, long val) {
		nbt.setLong(key, val);
	}

	@Override
	public float nfloat(String key) {
		return nbt.getFloat(key);
	}

	@Override
	public double ndouble(String key) {
		return nbt.getDouble(key);
	}

	@Override
	public byte nbyte(String key) {
		return nbt.getByte(key);
	}

	@Override
	public short nshort(String key) {
		return nbt.getShort(key);
	}

	@Override
	public int nint(String key) {
		return nbt.getInteger(key);
	}

	@Override
	public long nlong(String key) {
		return nbt.getLong(key);
	}

	@Override
	public void write(String key, boolean val) {
		nbt.setBoolean(key, val);
	}

	@Override
	public boolean nboolean(String key) {
		return nbt.getBoolean(key);
	}

	@Override
	public void write(String key, NBTBase base) {
		nbt.setTag(key, base);
	}

	@Override
	public NBTTagCompound compoundTag(String key) {
		return nbt.getCompoundTag(key);
	}

	@Override
	public NBTTagList listTag(String key, int tag) {
		return nbt.getTagList(key, tag);
	}

	@Override
	public void write(String key, CapabilityObjectRef ref) {
		nbt.setByteArray(key, CapabilityObjectRef.write(ref));
	}

	@Override
	public CapabilityObjectRef capabilityObjectRef(String key) {
		if (nbt.hasKey(key, NBTTag.TAG_BYTE_ARRAY)) return CapabilityObjectRef.read(nbt.getByteArray(key));
		return CapabilityObjectRef.INVALID;
	}

	@Override
	public void writeDisplay(String key, Object displayObject) {
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
		GameDisplayCast.write(buf, displayObject);
		byte[] bytes = new byte[buf.writerIndex()];
		buf.getBytes(0, bytes);
		nbt.setByteArray(key, bytes);
	}

	@Override
	public Object display(String key) {
		try {
			PacketBuffer buf = new PacketBuffer(Unpooled.wrappedBuffer(nbt.getByteArray(key)));
			return GameDisplayCast.read(buf);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void write(String key, byte[] bytes) {
		nbt.setByteArray(key, bytes);
	}

	@Override
	public void write(String key, ByteBuf byteBuf) {
		byte[] bytes = new byte[byteBuf.writerIndex()];
		byteBuf.getBytes(0, bytes);
		nbt.setByteArray(key, bytes);
	}

	@Override
	public byte[] bytes(String key) {
		return nbt.getByteArray(key);
	}

	@Override
	public void writeStream(String key, Consumer<PacketBuffer> writer) {
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
		writer.accept(buf);
		byte[] bytes = new byte[buf.writerIndex()];
		buf.getBytes(0, bytes);
		nbt.setByteArray(key, bytes);
	}

	@Override
	public <T> T sobj(String key, Function<PacketBuffer, T> reader) {
		try {
			PacketBuffer buf = new PacketBuffer(Unpooled.wrappedBuffer(nbt.getByteArray(key)));
			return reader.apply(buf);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void write(String key, Mantra mantra) {
		nbt.setString(key, mantra == null ? "" : mantra.getRegistryName().toString());
	}

	@Override
	public Mantra mantra(String key) {
		return Mantra.REGISTRY.getValue(nbt.getString(key));
	}

	@Override
	public void write(String key, Vec3d vec) {
		if (vec == null) return;
		if (vec == Vec3d.ZERO) nbt.setIntArray(key, new int[0]);
		else {
			nbt.setIntArray(key, new int[] { Float.floatToIntBits((float) vec.x), Float.floatToIntBits((float) vec.y),
					Float.floatToIntBits((float) vec.z) });
		}
	}

	@Override
	public Vec3d vec3d(String key) {
		int[] array = nbt.getIntArray(key);
		if (array == null || array.length < 3) return Vec3d.ZERO;
		return new Vec3d(Float.intBitsToFloat(array[0]), Float.intBitsToFloat(array[1]),
				Float.intBitsToFloat(array[2]));
	}

}
