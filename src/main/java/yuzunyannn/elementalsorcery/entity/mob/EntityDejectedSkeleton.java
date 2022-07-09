package yuzunyannn.elementalsorcery.entity.mob;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.init.LootRegister;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.util.helper.ColorHelper;
import yuzunyannn.elementalsorcery.util.helper.DamageHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class EntityDejectedSkeleton extends EntitySkeleton {

	/** 骷髅的状态 */
	public static final DataParameter<Byte> STATE = EntityDataManager.<Byte>createKey(EntityDejectedSkeleton.class,
			DataSerializers.BYTE);
	/** 下一次切换状态的剩余时间 */
	protected int shiftStateTickRemain = 0;

	public int particleCd = 0;

	public EntityLivingBase lastAttacker;

	public EntityDejectedSkeleton(World worldIn) {
		super(worldIn);
	}

	public EntityDejectedSkeleton(EntityLivingBase otherSkeleton) {
		super(otherSkeleton.world);
		for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
			ItemStack itemStack = otherSkeleton.getItemStackFromSlot(slot).copy();
			this.setItemStackToSlot(slot, itemStack);
			this.setDropChance(slot, 0);
		}
		this.setCombatTask();
		this.setCanPickUpLoot(false);
		this.setPosition(otherSkeleton.posX, otherSkeleton.posY, otherSkeleton.posZ);
		this.rotationPitch = otherSkeleton.rotationPitch;
		this.rotationYaw = otherSkeleton.rotationYaw;
		this.rotationYawHead = otherSkeleton.rotationYawHead;
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIRestrictSun(this));
		this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(6, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(STATE, Byte.valueOf((byte) 0));
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootRegister.DEJECTED_SKELETON;
	}

	public State getState() {
		return State.getStateById(dataManager.get(STATE));
	}

	public void setState(State state) {
		dataManager.set(STATE, state == null ? (byte) 0 : (byte) state.getId());
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(128D);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setByte("skState", (byte) this.getState().getId());
		nbt.setInteger("sstr", shiftStateTickRemain);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.setState(State.getStateById(nbt.getByte("skState")));
		shiftStateTickRemain = nbt.getInteger("sstr");
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		this.extinguish();
		if (world.isRemote) return;

		if (shiftStateTickRemain <= 0) this.tryShiftState();
		else shiftStateTickRemain--;
		if (particleCd > 0) particleCd--;
	}

	// 转化状态
	public void tryShiftState() {
		// 重置数据
		lastAttacker = null;
		// 被攻击
		DamageSource ds = this.getLastDamageSource();
		if (ds != null) {
			List<State> states = State.getStates();
			if (DamageHelper.isMagicalDamage(ds)) states.remove(State.MAGICAL);
			if (DamageHelper.isNormalAttackDamage(ds)) states.remove(State.ATTACK);
			if (ds.isFireDamage() || ds.isExplosion()) states.remove(State.FIRE);
			if (DamageHelper.isRangedDamage(ds) && DamageHelper.isPhysicalDamage(ds))
				states.remove(State.RANGED_PHYSICAL);
			if (states.isEmpty()) this.setState(State.INVINCIBLE);
			else this.setState(states.get(rand.nextInt(states.size())));
			shiftStateTickRemain = 20 * 5 + 20 * rand.nextInt(10);
			return;
		}
		// 发动了攻击
		Entity target = this.getAttackTarget();
		if (target == null) target = this.getRevengeTarget();
		if (target != null) {
			List<State> states = State.getStates();
			this.setState(states.get(rand.nextInt(states.size())));
			shiftStateTickRemain = 20 * 5 + 20 * rand.nextInt(10);
			return;
		}

		this.setState(State.NONE);
	}

	@Override
	protected EntityArrow getArrow(float distanceFactor) {
		return super.getArrow(distanceFactor);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (DamageHelper.isRuleDamage(source)) return super.attackEntityFrom(source, amount);
		State state = this.getState();

		Entity entity = source.getTrueSource();
		if (entity instanceof EntityDejectedSkeleton) return false;

		// 创造模式下，shift攻击 直接击杀
		if (entity instanceof EntityPlayer) {
			if (((EntityPlayer) entity).isCreative() && entity.isSneaking()) {
				return super.attackEntityFrom(source, 9999);
			}
		}

		EntityLivingBase living = null;
		if (entity instanceof EntityLivingBase) {
			living = (EntityLivingBase) entity;
			// 受到多人攻击，直接转化为无敌模式
			if (lastAttacker == null) lastAttacker = living;
			else if (lastAttacker != entity) {
				lastAttacker = (EntityLivingBase) entity;
				if (state != State.INVINCIBLE) this.setState(State.INVINCIBLE);
				this.shiftStateTickRemain = 20 * 10;
				return false;
			}
			// 设置复仇目标
			if (getRevengeTarget() == null) setRevengeTarget(living);
		}

		if (living == null) living = lastAttacker;

		switch (state) {
		case INVINCIBLE:
			if (!world.isRemote && particleCd <= 0) {
				Effects.spawnTreatEntity(this, null);
				particleCd = 40;
			}
			// 延长持续时间
			this.shiftStateTickRemain = Math.min(this.shiftStateTickRemain + 5, 20 * 10);
			// 治愈自身
			this.heal(amount);
			if (living == null) return false;
			living.attackEntityFrom(source, amount);
			return false;
		case ATTACK:
			if (!DamageHelper.isNormalAttackDamage(source)) {
				if (!world.isRemote) {
					int time = 5 * 20 + rand.nextInt(10) * 20;
					this.addPotionEffect(new PotionEffect(MobEffects.SPEED, time, 3));
					this.addPotionEffect(new PotionEffect(MobEffects.HASTE, time, 3));
					this.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, time, 3));
					this.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, time, 3));
					if (particleCd <= 0) {
						Effects.spawnTreatEntity(this, null);
						particleCd = 40;
					}
				}
				this.heal(amount);
				return false;
			}
			break;
		case MAGICAL:
			if (!DamageHelper.isMagicalDamage(source)) {
				if (living == null) return false;
				living.attackEntityFrom(source, amount);
				return false;
			}
			break;
		case FIRE:
			if (!(source.isFireDamage() || source.isExplosion())) {
				if (living == null) return false;
				if (!world.isRemote) {
					living.setFire(20 * 5 + rand.nextInt(20) * 15);
					world.createExplosion(null, living.posX, living.posY, living.posZ, rand.nextFloat() + 0.5f, false);
				}
				return false;
			}
			break;
		case RANGED_PHYSICAL:
			if (!(DamageHelper.isRangedDamage(source) && DamageHelper.isPhysicalDamage(source))) {
				if (!world.isRemote) {
					if (living == null) return false;
					int time = 5 * 20 + rand.nextInt(10) * 20;
					living.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, time, 3));
					living.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, time, 3));
					living.addPotionEffect(new PotionEffect(MobEffects.HUNGER, time, 3));
					living.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, time, 3));
				}
				return false;
			}
			break;
		case RANDOM:
			if (!world.isRemote) {
				AxisAlignedBB aabb = WorldHelper.createAABB(this.getPositionVector(), 8, 4, 2);
				List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
				EntityLivingBase victim = entities.get(rand.nextInt(entities.size()));
				if (victim != this && !(victim instanceof EntityDejectedSkeleton)) {
					victim.attackEntityFrom(source, amount);
					return false;
				}
			}
		default:
		}
		// 超过10点，立即切换
		if (amount >= 0) this.shiftStateTickRemain = 0;
		// 允许受到攻击
		return super.attackEntityFrom(source, amount);
	}

	public static enum State {
		NONE(0),

		/** 无敌 */
		INVINCIBLE(0x5100e5),

		/** 仅接受魔法伤害 */
		MAGICAL(0x007ee8),

		/** 仅接受近战普通攻击 */
		ATTACK(0xe80000),

		/** 仅接受远程物理伤害 */
		RANGED_PHYSICAL(0x00da3e),

		/** 仅接受火焰和爆炸 */
		FIRE(0xffc000),

		/** 任意伤害随机分配 */
		RANDOM(0x959595);

		public final Vec3d color;

		State(int color) {
			this.color = ColorHelper.color(color);
		}

		public Vec3d getColor() {
			return color;
		}

		public int getId() {
			return this.ordinal();
		}

		public static State getStateById(int id) {
			State[] values = State.values();
			if (id < 0 || id >= values.length) return State.NONE;
			return values[id];
		}

		public static List<State> getStates() {
			List<State> states = new ArrayList<>(State.values().length - 2);
			for (int i = 2; i < State.values().length; i++) states.add(State.values()[i]);
			return states;
		}
	}
}
