package yuzunyannn.elementalsorcery.entity;

import java.util.Random;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.util.ESFakePlayer;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;

public class EntityThrow extends EntityThrowable implements IEntityAdditionalSpawnData {

	/** 物品掉落 */
	public static final int FLAG_ITEM_DROP = 0x01;
	/** 方块自动摆放 */
	public static final int FLAG_BLOCK_PLACE = 0x02;
	/** 药水爆炸 */
	public static final int FLAG_POTION_BREAK = 0x04;

	public static EntityThrow shoot(EntityLivingBase entity, ItemStack stack) {
		return shoot(entity, stack, 0);
	}

	public static EntityThrow shoot(EntityLivingBase entity, ItemStack stack, int flag) {
		World world = entity.world;
		EntityThrow eThrow = null;
		if (!world.isRemote) {
			ItemStack shootStack = stack.copy();
			shootStack.setCount(1);
			eThrow = new EntityThrow(world, shootStack, entity);
			eThrow.shoot(entity, entity.rotationPitch, entity.rotationYaw, 0.0F, 1.5F, 1.0F);
			eThrow.flag = flag;
			world.spawnEntity(eThrow);
			world.playSound((EntityPlayer) null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_EGG_THROW,
					SoundCategory.PLAYERS, 0.5F, 0.4F / (entity.getRNG().nextFloat() * 0.4F + 0.8F));
		}
		if (!EntityHelper.isCreative(entity)) stack.shrink(1);
		return eThrow;
	}

	public interface IItemThrowAction {
		void onImpact(EntityThrow entity, RayTraceResult result);

		@SideOnly(Side.CLIENT)
		default void handleStatusUpdate(EntityThrow entity, byte id) {
		}
	}

	protected ItemStack stack = ItemStack.EMPTY;
	protected int flag = 0;

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
			else onImpact(world, getThrower(), result, stack, flag);
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
		compound.setByte("flag", (byte) flag);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		stack = new ItemStack(compound.getCompoundTag("item"));
		flag = compound.getInteger("flag");
	}

	public static void onImpact(World world, @Nullable EntityLivingBase thrower, RayTraceResult ray, ItemStack stack,
			int flag) {
		if (world.isRemote) return;

		EntityPlayer player = null;
		if (thrower instanceof EntityPlayer) player = (EntityPlayer) thrower;

		if ((flag & FLAG_BLOCK_PLACE) != 0 && ray.typeOfHit == Type.BLOCK) {
			stack = EntityBlockMove.putBlock(world, player, ray.getBlockPos().offset(ray.sideHit), stack, null, null,
					null);
			if (stack.isEmpty()) return;
		}

		if ((flag & FLAG_POTION_BREAK) != 0) {
			Item item = stack.getItem();
			if (item == Items.SPLASH_POTION || item == Items.LINGERING_POTION) {
				Vec3d centerVec = ray.typeOfHit == Type.BLOCK ? new Vec3d(ray.getBlockPos())
						: ray.entityHit.getPositionVector();
				thrower = thrower == null ? ESFakePlayer.get((WorldServer) world) : thrower;
				EntityPotion entitypotion = new EntityPotion(world, thrower, stack);
				entitypotion.setPosition(ray.hitVec.x, ray.hitVec.y, ray.hitVec.z);
				Vec3d speed = centerVec.subtract(ray.hitVec).normalize().scale(0.5);
				entitypotion.motionX = speed.x;
				entitypotion.motionY = speed.y;
				entitypotion.motionZ = speed.z;
				world.spawnEntity(entitypotion);
				return;
			}
		}

		if ((flag & FLAG_ITEM_DROP) != 0) {
			Block.spawnAsEntity(world, new BlockPos(ray.hitVec), stack);
			return;
		}

	}

}
