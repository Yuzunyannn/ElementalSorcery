package yuzunyannn.elementalsorcery.util.helper;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.api.util.target.CapabilityObjectRef;

public interface INBTWriter {

	void write(String key, float val);

	void write(String key, double val);

	void write(String key, byte val);

	void write(String key, int val);

	void write(String key, short val);

	void write(String key, long val);

	void write(String key, boolean val);

	void write(String key, UUID uuid);

	void write(String key, String str);

	void write(String key, byte[] bytes);

	void write(String key, ByteBuf byteBuf);

	void write(String key, EnumFacing facing);

	void write(String key, ItemStack stack);

	void write(String key, NBTBase base);

	void write(String key, CapabilityObjectRef ref);

	void write(String key, INBTSerializable<?> serializable);

	void write(String key, List<? extends INBTSerializable<?>> list);

	void writeStream(String key, Consumer<PacketBuffer> writer);

	void writeUUIDs(String key, List<UUID> uuids);

	void writeDisplay(String key, Object displayObject);
}
