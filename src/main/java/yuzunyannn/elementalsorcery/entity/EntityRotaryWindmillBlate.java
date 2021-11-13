package yuzunyannn.elementalsorcery.entity;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.api.item.IWindmillBlade;
import yuzunyannn.elementalsorcery.util.MasterBinder;
import yuzunyannn.elementalsorcery.util.helper.ExceptionHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class EntityRotaryWindmillBlate extends EntityObject implements IEntityAdditionalSpawnData {

	// 只回复重要的两个数据，其他数据如果重启了，就直接结束旋转吧
	protected ItemStack blate = ItemStack.EMPTY;
	protected MasterBinder masterBinder = new MasterBinder();

	protected int amplifier;
	protected Vec3d targetVec;
	protected int remainTick = 0;
	protected int endTick = -1;

	public int tick;

	public EntityRotaryWindmillBlate(World worldIn) {
		super(worldIn);
	}

	public EntityRotaryWindmillBlate(World worldIn, ItemStack blate, EntityLivingBase thrower, int amplifier) {
		super(worldIn);
		this.amplifier = amplifier;
		this.blate = blate;
		masterBinder.setMaster(thrower);
		this.setPosition(thrower.posX, thrower.posY + thrower.getEyeHeight() - 0.5, thrower.posZ);
		if (thrower instanceof EntityPlayerMP)
			ESCriteriaTriggers.ES_TRING.trigger((EntityPlayerMP) thrower, "pitcher:windmillBlade");
	}

	public void shoot(Vec3d vec, int remainTick) {
		this.targetVec = vec;
		this.remainTick = remainTick;
	}

	@Nullable
	public EntityLivingBase getMaster() {
		return masterBinder.tryGetMaster(world);
	}

	public int getRemainTick() {
		return remainTick;
	}

	public int getAmplifier() {
		return amplifier;
	}

	@Override
	protected void entityInit() {
		width = 2;
		height = 0.25f;
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		ByteBufUtils.writeItemStack(buffer, blate);
		masterBinder.writeSpawnData(buffer);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		blate = ByteBufUtils.readItemStack(additionalData);
		masterBinder.readSpawnData(additionalData);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		compound.setTag("item", blate.serializeNBT());
		masterBinder.writeEntityToNBT(compound);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		blate = new ItemStack(compound.getCompoundTag("item"));
		masterBinder.readEntityFromNBT(compound);
	}

	public IWindmillBlade getWindmillBlade() {
		if (blate.isEmpty()) return null;
		Item item = blate.getItem();
		if (item instanceof IWindmillBlade) return (IWindmillBlade) item;
		return null;
	}

	@Override
	public void handleStatusUpdate(byte id) {
		if (id == 123) endTick = 200;
	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();

		if (blate.isEmpty()) {
			if (world.isRemote) return;
			this.setDead();
			return;
		}

		this.doBlateAttack();

		if (world.isRemote) {
			this.onEntityUpdateClient();
			return;
		}

		this.onGround = true;

		if (remainTick > 0) {
			remainTick--;
			if (targetVec != null) {
				this.posX = this.posX + (targetVec.x - this.posX) * 0.1;
				this.posY = this.posY + (targetVec.y - this.posY) * 0.1;
				this.posZ = this.posZ + (targetVec.z - this.posZ) * 0.1;
			}
			return;
		}

		EntityLivingBase master = getMaster();
		if (master != null) {
			Vec3d masterVec = new Vec3d(master.posX, master.posY + master.getEyeHeight() - 0.5f, master.posZ);
			Vec3d thisVec = new Vec3d(this.posX, this.posY + this.height / 2, this.posZ);
			double dis = masterVec.distanceTo(thisVec);
			double rate = 0.2;
			if (dis < 2) rate = 0.2 + 0.8 * (2 - dis) / 2;

			this.posX = this.posX + (masterVec.x - this.posX) * rate;
			this.posY = this.posY + (masterVec.y - this.posY) * rate;
			this.posZ = this.posZ + (masterVec.z - this.posZ) * rate;

			if (dis <= 0.5) endTick = 1;
		}

		if (endTick >= 0) {
			endTick--;
			if (endTick == 0) this.onEnd();
		} else {
			world.setEntityState(this, (byte) 123);
			endTick = 200;
		}
	}

	public void onEnd() {
		this.setDead();
		EntityLivingBase master = getMaster();
		if (master == null) {
			Block.spawnAsEntity(world, this.getPosition(), blate);
			return;
		}
		Vec3d masterVec = new Vec3d(master.posX, master.posY + master.height / 2, master.posZ);
		Vec3d thisVec = this.getPositionVector();
		if (thisVec.squareDistanceTo(masterVec) <= 1.25) {
			if (master instanceof EntityPlayer) {
				ItemHelper.addItemStackToPlayer((EntityPlayer) master, blate);
				return;
			}
			if (master.getHeldItemMainhand().isEmpty()) {
				master.setHeldItem(EnumHand.MAIN_HAND, blate);
				return;
			}
		}
		Block.spawnAsEntity(world, this.getPosition(), blate);
	}

	public void doBlateAttack() {
		try {
			tick++;
			IWindmillBlade blade = getWindmillBlade();
			if (blade == null) return;
			Vec3d thisVec = new Vec3d(this.posX, this.posY + this.height / 2, this.posZ);
			blade.bladePitch(world, thisVec, blate, this);
		} catch (Exception e) {
			ElementalSorcery.logger.warn("旋转风轮出现异常", e);
			ExceptionHelper.warnSend(world, "旋转风轮出现异常");
			this.setDead();
		}
	}

	@SideOnly(Side.CLIENT)
	public float bladeRotate;
	@SideOnly(Side.CLIENT)
	public float prevBladeRotate;
	@SideOnly(Side.CLIENT)
	public float bladeScale = 0;
	@SideOnly(Side.CLIENT)
	public float prevBladeScale;

	@SideOnly(Side.CLIENT)
	public void onEntityUpdateClient() {
		this.prevBladeScale = this.bladeScale;
		this.prevBladeRotate = this.bladeRotate;

		if (endTick >= 0) {
			endTick = Math.max(0, endTick - 1);
			EntityLivingBase master = getMaster();
			if (master != null) {
				Vec3d masterVec = new Vec3d(master.posX, master.posY + master.getEyeHeight() - 0.5f, master.posZ);
				Vec3d thisVec = new Vec3d(this.posX, this.posY + this.height / 2, this.posZ);
				double dis = thisVec.distanceTo(masterVec);
				if (dis <= 0.75) bladeScale = 0;
				else if (dis <= 3) bladeScale = (float) ((dis - 0.75) / 2.25);
			} else if (endTick <= 8) bladeScale = endTick / 8f;
		} else bladeScale = bladeScale + (1 - bladeScale) * 0.2f;

		this.bladeRotate = bladeRotate + bladeScale * 0.35f;
	}

}
