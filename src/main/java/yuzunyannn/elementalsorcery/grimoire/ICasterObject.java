package yuzunyannn.elementalsorcery.grimoire;

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

public interface ICasterObject extends ICapabilityProvider {

	Vec3d getPositionVector();

	World getWorld();

	TileEntity asTileEntity();

	Entity asEntity();

	default Vec3d getEyePosition() {
		Entity entity = asEntity();
		if (entity == null) return getPositionVector().addVector(0.5, 0.5, 0.5);
		else return entity.getPositionVector().addVector(0, entity.getEyeHeight(), 0);
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

}
