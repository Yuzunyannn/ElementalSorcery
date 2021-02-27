package yuzunyannn.elementalsorcery.entity;

import java.util.Random;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;

public class EntityThrow extends EntityThrowable implements IEntityAdditionalSpawnData {

	public static void shoot(EntityLivingBase entity, ItemStack stack) {
		World world = entity.world;
		if (!world.isRemote) {
			EntityThrow t = new EntityThrow(world, stack, entity);
			t.shoot(entity, entity.rotationPitch, entity.rotationYaw, 0.0F, 1.5F, 1.0F);
			world.spawnEntity(t);
		}
		if (entity instanceof EntityPlayer) {
			if (!((EntityPlayer) entity).isCreative()) stack.shrink(1);
		}
	}

	public interface IItemThrowAction {
		void onImpact(EntityThrow entity, RayTraceResult result);

		@SideOnly(Side.CLIENT)
		default void handleStatusUpdate(EntityThrow entity, byte id) {

		}

	}

	protected ItemStack stack = ItemStack.EMPTY;

	public EntityThrow(World worldIn) {
		super(worldIn);
	}

	public EntityThrow(World worldIn, ItemStack stack, EntityLivingBase throwerIn) {
		super(worldIn, throwerIn);
		this.ignoreEntity = throwerIn;
		this.stack = stack.copy();
	}

	public ItemStack getItemStack() {
		return stack;
	}

	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id) {
		try {
			Item item = this.stack.getItem();
			if (item instanceof IItemThrowAction) ((IItemThrowAction) item).handleStatusUpdate(this, id);
		} catch (Exception e) {
			ElementalSorcery.logger.warn("客户端处理实体状态异常", e);
		}
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		try {
			Item item = this.stack.getItem();
			if (item instanceof IItemThrowAction) ((IItemThrowAction) item).onImpact(this, result);
		} catch (Exception e) {
			ElementalSorcery.logger.warn("投掷物品出现异常", e);
		}
		if (!this.world.isRemote) this.setDead();
	}

	public Random getRandom() {
		return rand;
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		ByteBufUtils.writeItemStack(buffer, stack);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		stack = ByteBufUtils.readItemStack(additionalData);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setTag("item", stack.serializeNBT());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		stack = new ItemStack(compound.getCompoundTag("item"));
	}

}
