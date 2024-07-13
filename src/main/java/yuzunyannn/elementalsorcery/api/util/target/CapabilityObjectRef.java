package yuzunyannn.elementalsorcery.api.util.target;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import yuzunyannn.elementalsorcery.api.IGetItemStack;
import yuzunyannn.elementalsorcery.api.util.GameCast;
import yuzunyannn.elementalsorcery.api.util.ICastEnv;
import yuzunyannn.elementalsorcery.api.util.ICastHandler;
import yuzunyannn.elementalsorcery.api.util.render.IDisplayable;

public abstract class CapabilityObjectRef implements ICapabilityProvider, IDisplayable {

	public static final int TAG_INVALID = 0;
	public static final int TAG_PLAYER = 1;
	public static final int TAG_PLAYER_ITEM = 2;
	public static final int TAG_ENTITY = 3;
	public static final int TAG_ENTITY_ITEM = 4;
	public static final int TAG_TILE = 5;

	public static final CapabilityObjectRef INVALID = new CORInvalid();

	public static CapabilityObjectRef iof(EntityPlayer player, int slot) {
		return new CORPlayerItem(player, slot);
	}

	public static CapabilityObjectRef iof(EntityItem itemEntity) {
		return new CORItemEntity(itemEntity);
	}

	public static <T extends Entity & IGetItemStack> CapabilityObjectRef iof(T itemEntity) {
		return new CORItemEntity(itemEntity);
	}

	public static CapabilityObjectRef of(EntityPlayer player) {
		return new CORPlayer(player);
	}

	public static CapabilityObjectRef of(Entity entity) {
		return new COREntity(entity);
	}

	public static CapabilityObjectRef of(TileEntity tileEntity) {
		return new CORTile(tileEntity);
	}

	public static interface ICapabilityRefStorage<T extends CapabilityObjectRef> {
		public void write(ByteBuf buf, T obj);

		public T read(ByteBuf buf);
	}

	public static final ICapabilityRefStorage[] storages;

	static {
		storages = new ICapabilityRefStorage[6];
		storages[TAG_INVALID] = new CORInvalid.Storage();
		storages[TAG_PLAYER] = new CORPlayer.Storage();
		storages[TAG_PLAYER_ITEM] = new CORPlayerItem.Storage();
		storages[TAG_ENTITY] = new COREntity.Storage();
		storages[TAG_ENTITY_ITEM] = new CORItemEntity.Storage();
		storages[TAG_TILE] = new CORTile.Storage();
		GameCast.CAST_MAP.put(CapabilityObjectRef.class, new CastRef());
	}

	public static class CastRef implements ICastHandler<CapabilityObjectRef> {
		@Override
		public CapabilityObjectRef cast(Object obj, ICastEnv env) {
			if (obj instanceof NBTTagByteArray) {
				try {
					return read(((NBTTagByteArray) obj).getByteArray());
				} catch (Exception e) {
					return null;
				}
			}
			if (obj instanceof IWorldObject) {
				IWorldObject wo = (IWorldObject) obj;
				if (!wo.isAlive()) return INVALID;
				TileEntity tile = wo.toTileEntity();
				if (tile != null) return of(tile);
				Entity entity = wo.toEntity();
				if (entity != null) return of(entity);
				return INVALID;
			}
			return null;
		}
	}

	public static byte[] write(CapabilityObjectRef ref) {
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
		buf.writeByte(ref.tagId());
		if (ref.tagId() != TAG_INVALID) {
			buf.writeInt(ref.worldId);
			storages[ref.tagId()].write(buf, ref);
		}
		byte[] bytes = new byte[buf.writerIndex()];
		buf.getBytes(0, bytes);
		return bytes;
	}

	public static CapabilityObjectRef read(byte[] bytes) {
		try {
			PacketBuffer buf = new PacketBuffer(Unpooled.wrappedBuffer(bytes));
			int tag = buf.readByte();
			if (tag == TAG_INVALID) return INVALID;
			int worldId = buf.readInt();
			CapabilityObjectRef ref = storages[tag].read(buf);
			ref.worldId = worldId;
			return ref;
		} catch (Exception e) {
			return INVALID;
		}
	}

	protected int worldId;

	public boolean isInvalid() {
		return this == INVALID;
	}

	public abstract boolean checkReference();

	public abstract int tagId();

	public abstract void restore(World world);

	@Nullable
	public abstract IWorldObject toWorldObject();

	@Nonnull
	public ItemStack toItemStack() {
		return ItemStack.EMPTY;
	}

	@Nullable
	public TileEntity toTileEntity() {
		return null;
	}

	@Nullable
	public Entity toEntity() {
		return null;
	}

	public EntityLivingBase toEntityLiving() {
		Entity entity = toEntity();
		if (entity instanceof EntityLivingBase) return (EntityLivingBase) entity;
		return null;
	}

	public EntityPlayer toEntityPlayer() {
		Entity entity = toEntity();
		if (entity instanceof EntityPlayer) return (EntityPlayer) entity;
		return null;
	}

	@Override
	public String toString() {
		return String.format("COR<%s>", tagId());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof CapabilityObjectRef) {
			CapabilityObjectRef other = (CapabilityObjectRef) obj;
			if (this.isInvalid() && other.isInvalid()) return true;
			if (this.tagId() != other.tagId()) return false;
			if (this.worldId != other.worldId) return false;
			return equals(other);
		}
		return super.equals(obj);
	}

	public abstract boolean equals(CapabilityObjectRef other);

}
