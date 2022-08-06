package yuzunyannn.elementalsorcery.api.util.var;

import java.util.UUID;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.network.PacketBuffer;

public class VTUUID implements IVariableType<UUID> {

	@Override
	public UUID newInstance(NBTBase base) {
		if (base instanceof NBTTagByteArray) {
			try {
				byte[] bytes = ((NBTTagByteArray) base).getByteArray();
				PacketBuffer buf = new PacketBuffer(Unpooled.wrappedBuffer(bytes));
				return buf.readUniqueId();
			} catch (Exception e) {
				return new UUID(0, 0);
			}
		}
		return new UUID(0, 0);
	}

	@Override
	public NBTBase serializable(UUID obj) {
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
		buf.writeUniqueId(obj);
		byte[] bytes = new byte[buf.writerIndex()];
		buf.getBytes(0, bytes);
		return new NBTTagByteArray(bytes);
	}
}
