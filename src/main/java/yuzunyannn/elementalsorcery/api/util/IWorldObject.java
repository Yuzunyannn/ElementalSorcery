package yuzunyannn.elementalsorcery.api.util;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IWorldObject extends ICapabilityProvider {

	Vec3d getPositionVector();

	World getWorld();

	TileEntity asTileEntity();

	Entity asEntity();

	default Vec3d getEyePosition() {
		Entity entity = asEntity();
		if (entity == null) return getPositionVector().add(0.5, 0.5, 0.5);
		else return entity.getPositionVector().add(0, entity.getEyeHeight(), 0);
	}

	default BlockPos getPosition() {
		return new BlockPos(getPositionVector());
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
		if (caster.asTileEntity() != null) {
			BlockPos pos = caster.asTileEntity().getPos();
			buf.writeByte((byte) 1);
			buf.writeInt(pos.getX());
			buf.writeInt(pos.getY());
			buf.writeInt(pos.getZ());
		} else if (caster.asEntity() != null) {
			buf.writeByte((byte) 2);
			buf.writeInt(caster.asEntity().getEntityId());
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
				TileEntity tile = world.getTileEntity(pos);
				if (tile == null) return null;
				return new WorldObjectTileEntity(tile);
			} else if (type == 2) {
				Entity entity = world.getEntityByID(buf.readInt());
				if (entity == null) return null;
				return new WorldObjectEntity(entity);
			}
		} catch (Exception e) {}
		return null;
	}

}