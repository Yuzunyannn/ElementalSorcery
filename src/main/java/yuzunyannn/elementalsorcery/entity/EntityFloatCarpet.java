package yuzunyannn.elementalsorcery.entity;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.element.ElementTransition;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.network.MessageEntitySync;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class EntityFloatCarpet extends Entity implements MessageEntitySync.IRecvData {

	public static float CONSUMPTION_RATIO_OF_DISTANCE_PER_POINT = 10;

	private static final DataParameter<Boolean> HAS_FUEL = EntityDataManager.<Boolean>createKey(EntityFloatCarpet.class,
			DataSerializers.BOOLEAN);

	protected int lerpSteps;
	protected double lerpX;
	protected double lerpY;
	protected double lerpZ;
	protected double lerpYaw;
	protected double lerpPitch;

	protected float momentum;
	protected float outOfControlTicks;
	protected float deltaRotation;

	protected Vec3d lastCheckVec = null;
	protected double magicPower = 0;

	protected float hp = 8;

	public EntityFloatCarpet(World worldIn) {
		super(worldIn);
		setSize(1f, 0.3f);
	}

	@Override
	protected void entityInit() {
		this.dataManager.register(HAS_FUEL, true);
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entityIn) {
		return entityIn.canBePushed() ? entityIn.getEntityBoundingBox() : null;
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox() {
		return this.getEntityBoundingBox();
	}

	@Override
	public AxisAlignedBB getEntityBoundingBox() {
		Entity passenger = getControllingPassenger();
		if (passenger != null) {
			AxisAlignedBB aabb = passenger.getEntityBoundingBox();
			AxisAlignedBB myAABB = super.getEntityBoundingBox();
			if (aabb.maxY - myAABB.maxY > 0) return myAABB.expand(0, aabb.maxY - myAABB.maxY, 0);
		}
		return super.getEntityBoundingBox();
	}

	@Override
	public boolean canBePushed() {
		return true;
	}

	@Override
	public double getMountedYOffset() {
		return 0;
	}

	@Override
	public void applyEntityCollision(Entity entityIn) {
		if (entityIn instanceof EntityFloatCarpet) {
			if (entityIn.getEntityBoundingBox().minY < this.getEntityBoundingBox().maxY)
				super.applyEntityCollision(entityIn);
		} else if (entityIn.getEntityBoundingBox().minY <= this.getEntityBoundingBox().minY)
			super.applyEntityCollision(entityIn);
	}

	@Override
	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	public boolean hasFuel() {
		return this.dataManager.get(HAS_FUEL);
	}

	public void setFuel(boolean has) {
		this.dataManager.set(HAS_FUEL, has);
	}

	public void setMagicPower(double magicPower) {
		this.magicPower = magicPower;
	}

	public void addMagicPower(double magicPower) {
		this.magicPower += magicPower;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch,
			int posRotationIncrements, boolean teleport) {
		this.lerpX = x;
		this.lerpY = y;
		this.lerpZ = z;
		this.lerpYaw = (double) yaw;
		this.lerpPitch = (double) pitch;
		this.lerpSteps = 6;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isEntityInvulnerable(source)) return false;
		else {
			if (source.getTrueSource() instanceof EntityPlayer) this.hp /= 2;
			if ((this.hp -= amount) < 0) this.dropAsItem();
			return true;
		}
	}

	@Override
	public EnumFacing getAdjustedHorizontalFacing() {
		return super.getAdjustedHorizontalFacing();
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		magicPower = compound.getDouble("magicPower");
		if (compound.hasKey("randomMagicPower")) magicPower = Math.random() * 3000 + 1000;
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setDouble("magicPower", magicPower);
	}

	@Nullable
	@Override
	public Entity getControllingPassenger() {
		List<Entity> list = this.getPassengers();
		return list.isEmpty() ? null : (Entity) list.get(0);
	}

	public void dropMagicPower(double distance) {
		if (this.magicPower <= 0) return;
		this.magicPower = Math.max(0, this.magicPower - distance * CONSUMPTION_RATIO_OF_DISTANCE_PER_POINT);
	}

	protected void tickLerp() {
		if (this.lerpSteps > 0 && !this.canPassengerSteer()) {
			double dPosX = this.posX + (this.lerpX - this.posX) / (double) this.lerpSteps;
			double dPosY = this.posY + (this.lerpY - this.posY) / (double) this.lerpSteps;
			double dPosZ = this.posZ + (this.lerpZ - this.posZ) / (double) this.lerpSteps;
			double dYaw = MathHelper.wrapDegrees(this.lerpYaw - (double) this.rotationYaw);
			this.rotationYaw = (float) ((double) this.rotationYaw + dYaw / (double) this.lerpSteps);
			this.rotationPitch = (float) ((double) this.rotationPitch
					+ (this.lerpPitch - (double) this.rotationPitch) / (double) this.lerpSteps);
			--this.lerpSteps;
			this.setPosition(dPosX, dPosY, dPosZ);
			this.setRotation(this.rotationYaw, this.rotationPitch);
		}
	}

	protected void dropAsItem() {
		if (world.isRemote) return;
		this.setDead();
		ItemStack stack = new ItemStack(ESObjects.ITEMS.FLOAT_CARPET);
		IElementInventory inventory = ElementHelper.getElementInventory(stack);
		inventory.setStackInSlot(0, new ElementStack(ESObjects.ELEMENTS.MAGIC,
				(int) ElementTransition.fromFragmentByPower(ESObjects.ELEMENTS.MAGIC, magicPower, 1), 1));
		inventory.saveState(stack);
		ItemHelper.dropItem(world, getPosition(), stack);
	}

	protected void onDriverless() {

	}

	public void doEntityCollisions() {
		List<Entity> list = this.world.getEntitiesInAABBexcluding(this,
				this.getEntityBoundingBox().grow(0.20000000298023224D, -0.009999999776482582D, 0.20000000298023224D),
				EntitySelectors.getTeamCollisionPredicate(this));

		if (!list.isEmpty()) {
			boolean flag = !this.world.isRemote && !(this.getControllingPassenger() instanceof EntityPlayer);
			for (int j = 0; j < list.size(); ++j) {
				Entity entity = list.get(j);
				if (entity.isPassenger(this)) continue;
				if (flag && this.getPassengers().size() < 2 && !entity.isRiding() && entity.width < this.width
						&& entity instanceof EntityLivingBase && !(entity instanceof EntityPlayer)) {
					entity.startRiding(this);
				} else {
					this.applyEntityCollision(entity);
				}
			}
		}
	}

	protected void updateMotion() {
		this.momentum = 0.95F;

		this.motionX *= (double) this.momentum;
		this.motionZ *= (double) this.momentum;
		this.deltaRotation *= this.momentum;

		if (!hasFuel() || this.getPassengers().isEmpty()) {
			double acceleration = this.hasNoGravity() ? 0 : -0.01;
			this.motionY += acceleration;
		} else {

			double acceleration = 0;

			List<AxisAlignedBB> list = Lists.<AxisAlignedBB>newArrayList();
			Entity passenger = this.getPassengers().get(0);
			AxisAlignedBB aabb = passenger.getEntityBoundingBox().offset(0, 1, 0);
			for (BlockPos pos : BlockPos.getAllInBox(new BlockPos(aabb.minX, aabb.minY, aabb.minZ),
					new BlockPos(aabb.maxX, aabb.maxY, aabb.maxZ))) {
				world.getBlockState(pos).addCollisionBoxToList(world, pos, aabb, list, passenger, true);
				if (!list.isEmpty()) break;
			}

			if (list.isEmpty()) {
				BlockPos maxYPos = BlockPos.ORIGIN;
				for (int x = -1; x <= 1; x++) {
					for (int z = -1; z <= 1; z++) {
						BlockPos pos = new BlockPos(this.posX + x, this.posY - 0.25, this.posZ + z);
						for (int i = 0; i < 5 && world.isAirBlock(pos); i++) pos = pos.down();
						if (pos.getY() > maxYPos.getY()) maxYPos = pos;
					}
				}
				if (!world.isAirBlock(maxYPos)) {
					double distance = this.posY - maxYPos.getY();
					acceleration = 1.25 / Math.pow(distance + 4, 1.3) - 0.075;
				} else acceleration = -0.075D;
			} else acceleration -= 0.075D;

			this.motionY += acceleration;
			if (this.motionY > 0) this.motionY *= 0.75;
		}

	}

	@SideOnly(Side.CLIENT)
	public void onUpdateClient() {
		if (!hasFuel()) return;

		this.controlCarpet();
		this.updateEffect();

		if (this.ticksExisted % 20 == 0) {
			Vec3d currVec = this.getPositionVector();
			if (lastCheckVec == null) lastCheckVec = currVec;
			double distance = currVec.distanceTo(lastCheckVec);
			if (distance >= 1) {
				lastCheckVec = currVec;
				NBTTagCompound nbt = new NBTTagCompound();
				NBTHelper.setVec3d(nbt, "p", currVec);
				MessageEntitySync.sendToServer(this, nbt);
			}
		}
	}

	public void onUpdate() {

		if (!this.world.isRemote && this.outOfControlTicks >= 60.0F) {
			this.removePassengers();
		}

		if (!this.world.isRemote) {
			if (hasFuel()) {
				if (this.magicPower <= 0) this.setFuel(false);
			} else {
				if (this.magicPower > 0) this.setFuel(true);
			}
		}

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		super.onUpdate();

		this.tickLerp();

		if (this.canPassengerSteer()) {
			List<Entity> passengers = this.getPassengers();
			if (passengers.isEmpty() || !(this.getPassengers().get(0) instanceof EntityPlayer)) onDriverless();
			this.updateMotion();
			if (this.world.isRemote) onUpdateClient();
			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
		} else {
			this.motionX = 0.0D;
			this.motionY = 0.0D;
			this.motionZ = 0.0D;
		}

		this.doBlockCollisions();
		this.doEntityCollisions();
	}

	@Override
	public void updatePassenger(Entity passenger) {
		if (!this.isPassenger(passenger)) return;
		double f = 0.0F;
		double defaultHigh = (this.isDead ? 0.009999999776482582D : this.getMountedYOffset()) + passenger.getYOffset();

		if (this.getPassengers().size() > 1) {
			int i = this.getPassengers().indexOf(passenger);
			if (i == 0) f = 0.2F;
			else f = -0.6F;
			if (passenger instanceof EntityAnimal) f = (float) ((double) f + 0.2D);
		}

		Vec3d vec3d = (new Vec3d(f, 0.0D, 0.0D)).rotateYaw(-this.rotationYaw * 0.017453292F - ((float) Math.PI / 2F));
		passenger.setPosition(this.posX + vec3d.x, this.posY + defaultHigh, this.posZ + vec3d.z);
		passenger.rotationYaw += this.deltaRotation;
		passenger.setRotationYawHead(passenger.getRotationYawHead() + this.deltaRotation);
		this.applyYawToEntity(passenger);

		if (world.isRemote) updateControl(passenger);

		if (passenger instanceof EntityAnimal && this.getPassengers().size() > 1) {
			int j = passenger.getEntityId() % 2 == 0 ? 90 : 270;
			passenger.setRenderYawOffset(((EntityAnimal) passenger).renderYawOffset + (float) j);
			passenger.setRotationYawHead(passenger.getRotationYawHead() + (float) j);
		}
	}

	protected void applyYawToEntity(Entity entityToUpdate) {
		entityToUpdate.setRenderYawOffset(this.rotationYaw);
		float f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw);
		float f1 = MathHelper.clamp(f, -105.0F, 105.0F);
		entityToUpdate.prevRotationYaw += f1 - f;
		entityToUpdate.rotationYaw += f1 - f;
		entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void applyOrientationToEntity(Entity entityToUpdate) {
		this.applyYawToEntity(entityToUpdate);
	}

	protected boolean leftInputDown;
	protected boolean rightInputDown;
	protected boolean forwardInputDown;
	protected boolean backInputDown;
//	protected boolean spaceInputDown;

	@SideOnly(Side.CLIENT)
	protected void updateControl(Entity passenger) {
		if (passenger instanceof EntityPlayerSP && passenger == getControllingPassenger()) {
			MovementInput movementInput = ((EntityPlayerSP) passenger).movementInput;
			this.leftInputDown = movementInput.leftKeyDown;
			this.rightInputDown = movementInput.rightKeyDown;
			this.forwardInputDown = movementInput.forwardKeyDown;
			this.backInputDown = movementInput.backKeyDown;
//			this.spaceInputDown = movementInput.jump;
		}
	}

	@SideOnly(Side.CLIENT)
	protected void controlCarpet() {
		if (this.isBeingRidden()) {
			float f = 0.0F;
			if (this.leftInputDown) this.deltaRotation += -1.25;
			if (this.rightInputDown) this.deltaRotation += 1.25;
			if (this.rightInputDown != this.leftInputDown && !this.forwardInputDown && !this.backInputDown) {
				f += 0.005F;
			}
			this.rotationYaw += this.deltaRotation;
			if (this.forwardInputDown) f += 0.050F;
			if (this.backInputDown) f -= 0.01F;
			this.motionX += (double) (MathHelper.sin(-this.rotationYaw * 0.017453292F) * f);
			this.motionZ += (double) (MathHelper.cos(this.rotationYaw * 0.017453292F) * f);

//			if (this.spaceInputDown) {
//				this.motionY += 0.1;
//				this.motionY *= 0.75;
//			}
		}
	}

	@SideOnly(Side.CLIENT)
	protected void updateEffect() {
		if (getControllingPassenger() == null) return;
		Vec3d vec = new Vec3d(this.posX, this.posY, this.posZ);

		Color color = new Color(0x7d17e3).weight(new Color(0xe80027), rand.nextFloat() * 0.5f);

		EffectElementMove effect = new EffectElementMove(world,
				vec.add(rand.nextGaussian() * 0.2, -0.15, rand.nextGaussian() * 0.2));
		effect.setColor(color);
		effect.lifeTime = 4;
		effect.motionY = -0.25;
		effect.yDecay = 0.8;
		Effect.addEffect(effect);

		double theta = -rotationYaw / 180 * 3.1415926;
		Vec3d backVec = new Vec3d(MathHelper.sin((float) theta + 3.1415926f), 0,
				MathHelper.cos((float) theta + 3.1415926f));

		if (this.rightInputDown) {
			double mtheta = theta + 3.1415926 / 2;
			double x = MathHelper.sin((float) mtheta) * 0.75;
			double z = MathHelper.cos((float) mtheta) * 0.75;
			effect = new EffectElementMove(world, vec.add(x + backVec.x * 0.15, 0.25, z + backVec.z * 0.15));
			effect.setColor(color);
			effect.lifeTime = 4;
			effect.motionX = x / 5;
			effect.motionZ = z / 5;
			Effect.addEffect(effect);
		}
		if (this.leftInputDown) {
			double mtheta = theta - 3.1415926 / 2;
			double x = MathHelper.sin((float) mtheta) * 0.75;
			double z = MathHelper.cos((float) mtheta) * 0.75;
			effect = new EffectElementMove(world, vec.add(x + backVec.x * 0.15, 0.25, z + backVec.z * 0.15));
			effect.setColor(color);
			effect.lifeTime = 4;
			effect.motionX = x / 5;
			effect.motionZ = z / 5;
			Effect.addEffect(effect);
		}
		if (this.forwardInputDown) {
			double dtheta = theta + 3.1415926 / 2;
			double dx = MathHelper.sin((float) dtheta) * 0.25;
			double dz = MathHelper.cos((float) dtheta) * 0.25;

			double fator = rand.nextGaussian();
			effect = new EffectElementMove(world, vec.add(backVec.x + dx * fator, 0.25, backVec.z + dz * fator));
			effect.setColor(color);
			effect.lifeTime = 4;
			effect.motionX = backVec.x;
			effect.motionZ = backVec.z;
			Effect.addEffect(effect);
		}

	}

	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
		super.updateFallState(y, onGroundIn, state, pos);
		this.fallDistance = 0;
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		if (player.isSneaking()) {
			dropAsItem();
			return true;
		} else {
			if (!this.world.isRemote && this.outOfControlTicks < 60.0F) player.startRiding(this);
			return true;
		}
	}

	@Override
	public void onRecv(NBTTagCompound data) {
		if (world.isRemote) return;
		if (NBTHelper.hasVec3d(data, "p")) {
			Vec3d vec = NBTHelper.getVec3d(data, "p");
			if (lastCheckVec == null) lastCheckVec = this.getPositionVector();
			double distance = lastCheckVec.distanceTo(vec);
			lastCheckVec = vec;
			dropMagicPower(distance);
		}
	}

}
