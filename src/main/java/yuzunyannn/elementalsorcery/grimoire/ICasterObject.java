package yuzunyannn.elementalsorcery.grimoire;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ICasterObject {

	Vec3d getPositionVector();

	World getWorld();

	TileEntity asTileEntity();

	Entity asEntity();

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

}
