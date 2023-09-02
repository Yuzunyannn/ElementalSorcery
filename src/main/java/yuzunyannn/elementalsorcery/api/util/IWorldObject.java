package yuzunyannn.elementalsorcery.api.util;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IWorldObject extends ICapabilityProvider {

	public static IWorldObject of(IBlockAccess world, BlockPos pos) {
		if (world instanceof World) return new WorldObjectBlock((World) world, pos);
		return null;
	}

	public static IWorldObject of(World world, BlockPos pos) {
		return new WorldObjectBlock(world, pos);
	}

	public static IWorldObject of(Entity entity) {
		return new WorldObjectEntity(entity);
	}

	@Nonnull
	Vec3d getObjectPosition();

	@Nonnull
	World getWorld();

	/** as Tile */
	@Nullable
	TileEntity asTileEntity();

	/** as Entity */
	@Nullable
	Entity asEntity();

	/** as BlockState */
	default IBlockState asBlockState() {
		return getWorld().getBlockState(getPosition());
	}

	default Random getRNG() {
		EntityLivingBase living = asEntityLivingBase();
		if (living != null) return living.getRNG();
		return new Random(getWorld().getWorldTime() * getObjectPosition().hashCode());
	}

	default Vec3d getEyePosition() {
		Entity entity = asEntity();
		if (entity == null) return getObjectPosition().add(0.5, 0.5, 0.5);
		else return entity.getPositionVector().add(0, entity.getEyeHeight(), 0);
	}

	default BlockPos getPosition() {
		return new BlockPos(getObjectPosition());
	}

	default EntityLivingBase asEntityLivingBase() {
		Entity entity = asEntity();
		if (entity instanceof EntityLivingBase) return (EntityLivingBase) entity;
		return null;
	}

	default EntityPlayer asPlayer() {
		Entity entity = asEntity();
		if (entity instanceof EntityPlayer) return (EntityPlayer) entity;
		return null;
	}

	default boolean isCreative() {
		EntityPlayer player = asPlayer();
		return player == null ? false : player.isCreative();
	}

	@SideOnly(Side.CLIENT)
	default boolean isClientPlayer() {
		return asEntity() == Minecraft.getMinecraft().player;
	}

	default void markDirty() {
		TileEntity tile = this.asTileEntity();
		if (tile != null) tile.markDirty();
	}

	static public void writeSendToBuf(ByteBuf buf, IWorldObject caster) {
		if (caster.asEntity() != null) {
			buf.writeByte((byte) 2);
			buf.writeInt(caster.asEntity().getEntityId());
		} else if (caster.getPosition() != null) {
			BlockPos pos = caster.getPosition();
			buf.writeByte((byte) 1);
			buf.writeInt(pos.getX());
			buf.writeInt(pos.getY());
			buf.writeInt(pos.getZ());
		}
	}

	@Nullable
	static public IWorldObject readSendFromBuf(ByteBuf buf, World world) {
		try {
			byte type = buf.readByte();
			if (type == 1) {
				int x = buf.readInt();
				int y = buf.readInt();
				int z = buf.readInt();
				BlockPos pos = new BlockPos(x, y, z);
				return new WorldObjectBlock(world, pos);
			} else if (type == 2) {
				Entity entity = world.getEntityByID(buf.readInt());
				if (entity == null) return null;
				return new WorldObjectEntity(entity);
			}
		} catch (Exception e) {}
		return null;
	}

}
