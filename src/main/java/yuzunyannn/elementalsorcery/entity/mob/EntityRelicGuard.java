package yuzunyannn.elementalsorcery.entity.mob;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.entity.IHasMaster;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;
import yuzunyannn.elementalsorcery.entity.skill.EntitySkilFrozenDefense;
import yuzunyannn.elementalsorcery.entity.skill.EntitySkilFrozenStorm;
import yuzunyannn.elementalsorcery.entity.skill.EntitySkillAirBlast;
import yuzunyannn.elementalsorcery.entity.skill.EntitySkillBeam;
import yuzunyannn.elementalsorcery.entity.skill.EntitySkillBeamTrack;
import yuzunyannn.elementalsorcery.entity.skill.EntitySkillBeamTrackRain;
import yuzunyannn.elementalsorcery.entity.skill.EntitySkillFireArea;
import yuzunyannn.elementalsorcery.entity.skill.EntitySkillFireAreaMatrix;
import yuzunyannn.elementalsorcery.entity.skill.EntitySkillFireBall;
import yuzunyannn.elementalsorcery.entity.skill.EntitySkillFireCharge;
import yuzunyannn.elementalsorcery.entity.skill.EntitySkillFireChargeRain;
import yuzunyannn.elementalsorcery.entity.skill.EntitySkillIceBomb;
import yuzunyannn.elementalsorcery.entity.skill.EntitySkillIceBombTrack;
import yuzunyannn.elementalsorcery.entity.skill.EntitySkillIceTrapMatrix;
import yuzunyannn.elementalsorcery.entity.skill.EntitySkillJump;
import yuzunyannn.elementalsorcery.entity.skill.EntitySkillPotion;
import yuzunyannn.elementalsorcery.entity.skill.EntitySkillPotionRain;
import yuzunyannn.elementalsorcery.entity.skill.EntitySkillSet;
import yuzunyannn.elementalsorcery.item.prop.ItemRelicGuardCore;
import yuzunyannn.elementalsorcery.util.MasterBinder;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.helper.DamageHelper;

public class EntityRelicGuard extends EntityCreature implements IRangedAttackMob, IHasMaster {

	public static final int STATUS_DORMANCY = 0;
	public static final int STATUS_ACTIVE = 1;
	public static final int STATUS_WORN = 2;

	public static final int TYPE_FIRE = 0;
	public static final int TYPE_WATER = 1;

	public static final int WORN_LOW_HP = 10;

	protected static final DataParameter<Byte> STATUS = EntityDataManager.<Byte>createKey(EntityRelicGuard.class,
			DataSerializers.BYTE);
	protected static final DataParameter<Byte> TYPE = EntityDataManager.<Byte>createKey(EntityRelicGuard.class,
			DataSerializers.BYTE);
	protected static final DataParameter<Boolean> IS_SPELLING = EntityDataManager
			.<Boolean>createKey(EntityRelicGuard.class, DataSerializers.BOOLEAN);
	protected static final DataParameter<Boolean> CORE = EntityDataManager.<Boolean>createKey(EntityRelicGuard.class,
			DataSerializers.BOOLEAN);

	protected MasterBinder master = new MasterBinder();

	protected EntitySkillSet magician = new EntitySkillSet(this);
	protected EntitySkillJump jumpSkill = new EntitySkillJump(this);

	protected EntityAIMastersEnemyTarget aiTarget = new EntityAIMastersEnemyTarget(this, 20, (e) -> {
		if (e instanceof EntityRelicGuard) return false;
		return true;
	});

	public EntityRelicGuard(World worldIn) {
		super(worldIn);
		this.targetTasks.addTask(2, aiTarget);
		setStatus(STATUS_DORMANCY);
		setType(Math.random() < 0.5 ? TYPE_WATER : TYPE_FIRE);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(STATUS, (byte) 0);
		this.dataManager.register(TYPE, (byte) 0);
		this.dataManager.register(IS_SPELLING, false);
		this.dataManager.register(CORE, true);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		master.writeEntityToNBT(compound);
		compound.setByte("status", getStatus());
		compound.setByte("type", (byte) getType());
		compound.setBoolean("hCore", hasCore());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		master.readEntityFromNBT(compound);
		setStatus(compound.getByte("status"));
		setType(compound.getByte("type"));
		setCore(compound.getBoolean("hCore"));
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIAttackRanged(this, 1.0D, 40, 32.0F));
		this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
//		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);

		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(42);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(128);
	}

	@Override
	public EntityLivingBase getMaster() {
		return master.getMaster();
	}

	@Override
	public boolean isOwnerless() {
		return master.isOwnerless();
	}

	public void setStatus(int status) {
		this.dataManager.set(STATUS, (byte) status);
		switch (status) {
		case STATUS_ACTIVE:
			setNoAI(false);
			break;
		default:
			setNoAI(true);
			getNavigator().clearPath();
		}
	}

	public byte getStatus() {
		return this.dataManager.get(STATUS);
	}

	public int getType() {
		return this.dataManager.get(TYPE);
	}

	public void setType(int type) {
		this.dataManager.set(TYPE, (byte) type);
		this.magician = new EntitySkillSet(this);
		magician.addSkill(new EntitySkillAirBlast(this).setPriority(100));

		if (type == TYPE_FIRE) {
			magician.addSkill(new EntitySkillFireCharge(this).setPriority(10));
			magician.addSkill(new EntitySkillFireChargeRain(this).setPriority(20));
			magician.addSkill(new EntitySkillBeam(this, ESObjects.ELEMENTS.FIRE, 750).setPriority(18));
			magician.addSkill(new EntitySkillBeamTrack(this, ESObjects.ELEMENTS.FIRE, 750).setPriority(16));
			magician.addSkill(new EntitySkillBeamTrackRain(this, ESObjects.ELEMENTS.FIRE, 800).setPriority(15));
			magician.addSkill(new EntitySkillFireBall(this).setPriority(20));
			magician.addSkill(new EntitySkillFireArea(this).setPriority(16));
			magician.addSkill(new EntitySkillFireAreaMatrix(this).setPriority(25));
		} else if (type == TYPE_WATER) {
			magician.addSkill(new EntitySkillPotion(this).setPriority(10));
			magician.addSkill(new EntitySkillPotionRain(this).setPriority(15));
			magician.addSkill(new EntitySkillIceBomb(this).setPriority(20));
			magician.addSkill(new EntitySkillIceBombTrack(this).setPriority(14));
			magician.addSkill(new EntitySkillBeam(this, ESObjects.ELEMENTS.WATER, 500).setPriority(18));
			magician.addSkill(new EntitySkillBeamTrack(this, ESObjects.ELEMENTS.WATER, 500).setPriority(14));
			magician.addSkill(new EntitySkillBeamTrackRain(this, ESObjects.ELEMENTS.WATER, 75).setPriority(14));
			magician.addSkill(new EntitySkilFrozenDefense(this).setPriority(25));
			magician.addSkill(new EntitySkilFrozenStorm(this).setPriority(30));
			magician.addSkill(new EntitySkillIceTrapMatrix(this).setPriority(21));
		}
	}

	public boolean isSpelling() {
		return this.dataManager.get(IS_SPELLING);
	}

	public void setSpelling(boolean spelling) {
		this.dataManager.set(IS_SPELLING, spelling);
	}

	public void onShock(EntityLivingBase shocker) {
		int status = getStatus();
		if (status == STATUS_WORN) {
			if (hasCore()) {
				if (world.isRemote) return;
				this.setCore(false);
				ItemStack stack = new ItemStack(ESObjects.ITEMS.RELIC_GUARD_CORE);
				if (!master.isOwnerless()) ItemRelicGuardCore.setCoreMaster(stack, master.getMaster());
				ItemRelicGuardCore.setCoreType(stack, this.getType());
				this.entityDropItem(stack, 1);
				if (shocker instanceof EntityPlayerMP)
					ESCriteriaTriggers.ES_TRING.trigger((EntityPlayerMP) shocker, "action:takeGuardCore");

			}
		}
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (stack.getItem() == ESObjects.ITEMS.RELIC_DISC) {

			if (ESAPI.isDevelop && player.isSneaking() && player.isCreative()) {
				this.setHealth(WORN_LOW_HP);
				this.setStatus(STATUS_WORN);
				return EnumActionResult.SUCCESS;
			}

			int point = (int) Math.min(stack.getMaxDamage() - stack.getItemDamage(),
					this.getMaxHealth() - this.getHealth());
			if (point <= 0) return EnumActionResult.FAIL;
			if (world.isRemote) {
				for (int i = -1; i < Math.min(8, point / 10); i++) {
					world.spawnParticle(EnumParticleTypes.HEART, posX + rand.nextGaussian() * 0.25,
							posY + 1 + rand.nextGaussian() * 0.25, posZ + rand.nextGaussian() * 0.25, 0, 0.5, 0);
				}
				return EnumActionResult.SUCCESS;
			}
			this.heal(point);
			stack.damageItem(point, player);
			return EnumActionResult.SUCCESS;
		}

		int status = getStatus();
		if (status == STATUS_WORN) return EnumActionResult.PASS;
		if (hasCore()) return EnumActionResult.PASS;

		UUID uuid = ItemRelicGuardCore.getCoreMaster(stack);
		if (uuid == null) return EnumActionResult.PASS;
		if (world.isRemote) return EnumActionResult.SUCCESS;
		this.setAttackTarget(null);
		int type = ItemRelicGuardCore.getCoreType(stack);
		if (type >= 0) this.setType(type);
		this.setCore(true);
		this.setMaster(uuid);
		stack.shrink(1);
		return EnumActionResult.SUCCESS;
	}

	public boolean hasCore() {
		return this.dataManager.get(CORE);
	}

	public void setCore(boolean core) {
		this.dataManager.set(CORE, core);
	}

	public void setMaster(UUID uuid) {
		this.master.setMaster(uuid);
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public void fall(float distance, float damageMultiplier) {

	}

	@Override
	public void heal(float healAmount) {
		super.heal(healAmount);
		int status = getStatus();
		if (status == STATUS_WORN && this.getHealth() > WORN_LOW_HP) this.setStatus(STATUS_DORMANCY);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (DamageHelper.isRuleDamage(source)) return super.attackEntityFrom(source, amount);
		if (source.isFireDamage()) return false;
		if (source.getTrueSource() instanceof EntityRelicGuard) return false;
		if (source == DamageSource.FALL) return false;
		if (master.isOwnerless() && source.getTrueSource() instanceof EntityPlayer)
			this.setAttackTarget((EntityLivingBase) source.getTrueSource());
		if (source instanceof EntityDamageSourceIndirect && !DamageHelper.isMagicalDamage(source))
			amount = Math.min(amount, 2);
		else amount = Math.min(amount, 4);
		return super.attackEntityFrom(source, amount);
	}

	@Override
	protected void damageEntity(DamageSource damageSrc, float damageAmount) {
		super.damageEntity(damageSrc, damageAmount);
		if (getStatus() == STATUS_WORN) return;
		if (this.getHealth() <= WORN_LOW_HP) this.setStatus(STATUS_WORN);
	}

	@Override
	public void knockBack(Entity entityIn, float strength, double xRatio, double zRatio) {
		if (getStatus() == STATUS_WORN) {
			super.knockBack(entityIn, strength * 1.25f, xRatio, zRatio);
			return;
		}
		super.knockBack(entityIn, Math.min(strength, 1) * 0.1f, xRatio, zRatio);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		return super.attackEntityAsMob(entityIn);
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
		int status = getStatus();
		if (status != STATUS_ACTIVE) return;
		if (this.isSpelling()) return;

		this.setLastAttackedEntity(target);
		magician.findPrepareSkill();
		if (magician.getPrepareSkill() == null) return;

		magician.usePrepareSkill();
		if (magician.getUsingSkill() != null) this.setSpelling(true);
		else this.swingArm(EnumHand.MAIN_HAND);

		this.setHealth(getHealth() - getMaxHealth() * 0.0025f);
	}

	@Override
	public void setSwingingArms(boolean swingingArms) {

	}

	@Override
	protected void onDeathUpdate() {
		if (hasCore()) {
			if (!world.isRemote) {
				Vec3d vec = this.getPositionVector().add(0, 0.5, 0);
				int type = this.getType();
				switch (type) {
				case TYPE_FIRE:
					ElementExplosion.doExplosion(world, vec, new ElementStack(ESObjects.ELEMENTS.FIRE, 1000, 1000),
							this);
					break;
				case TYPE_WATER:
					ElementExplosion.doExplosion(world, vec, new ElementStack(ESObjects.ELEMENTS.WATER, 1000, 1000),
							this);
					break;
				default:
					world.createExplosion(this, vec.x, vec.y, vec.z, 10, true);
					break;
				}

				this.setDead();
			}
		} else super.onDeathUpdate();
	}

	@Override
	public boolean isOnSameTeam(Entity entityIn) {
		if (this.getMaster() == entityIn) return true;
		return super.isOnSameTeam(entityIn);
	}

	@Override
	public boolean isPotionApplicable(PotionEffect potioneffectIn) {
		Potion potion = potioneffectIn.getPotion();
		if (potion == MobEffects.POISON || potion == MobEffects.WITHER) return false;
		return super.isPotionApplicable(potioneffectIn);
	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		updateArmSwingProgress();
		this.extinguish();

		if (world.isRemote) {
			updateClient();
			return;
		}

		if (this.isAIDisabled()) {
			if (!this.hasNoGravity()) {
				this.motionY -= 0.08D;
				this.motionX *= 0.8;
				this.motionZ *= 0.8;
				this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			}
		}

		int status = getStatus();
		if (status == STATUS_WORN) {
			if (this.isSpelling()) this.setSpelling(false);
			if (hasCore()) this.setHealth(this.getHealth() - 0.05f);
			return;
		}

		if (!master.isOwnerless()) master.tryGetMaster(world);

		if (status == STATUS_DORMANCY) {
			if (this.isSpelling()) this.setSpelling(false);
			if (!hasCore()) return;
			if (this.aiTarget.shouldExecute()) this.aiTarget.startExecuting();
			if (this.getAttackTarget() != null) {
				this.setStatus(STATUS_ACTIVE);
				this.setLastAttackedEntity(null);
			}
			return;
		}

		if (status == STATUS_ACTIVE) {
			if (!hasCore()) {
				this.setStatus(STATUS_DORMANCY);
				return;
			}
			EntityLivingBase target = this.getAttackTarget();
			if (this.motionY < -0.25) {
				this.motionY = -0.25;
				if (target != null) {
					Vec3d tar = target.getPositionEyes(0).subtract(this.getPositionVector()).normalize();
					this.motionX = tar.x * 0.25;
					this.motionZ = tar.z * 0.25;
					this.velocityChanged = true;
				}
			}
			if (jumpSkill.checkCanUse()) jumpSkill.doSkill();
			jumpSkill.update(ticksExisted);
			magician.update();
			if (this.isSpelling() && magician.getUsingSkill() == null) this.setSpelling(false);

			if (target != null) return;
			int time = this.ticksExisted - getLastAttackedEntityTime();
			if (time > 20 * 30) this.setStatus(STATUS_DORMANCY);
			return;
		}
	}

	@SideOnly(Side.CLIENT)
	public float activeRate, prevActiveRate;
	public float activeTick, prevActiveTick;

	@SideOnly(Side.CLIENT)
	public void updateClient() {
		int status = getStatus();
		prevActiveRate = activeRate;
		prevActiveTick = activeTick;
		if (status == STATUS_ACTIVE) activeRate = Math.min(1, (activeRate + 0.01f) * 1.3f);
		else activeRate = activeRate + (0 - activeRate) * 0.2f;
		activeTick += activeRate;
	}

	@SideOnly(Side.CLIENT)
	public Color getColor() {
		int type = this.getType();
		if (type == TYPE_FIRE) return new Color(0xff6b08);
		else return new Color(0x00baff);
	}

}
