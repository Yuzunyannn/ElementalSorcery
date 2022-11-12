package yuzunyannn.elementalsorcery.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.entity.IHasMaster;
import yuzunyannn.elementalsorcery.util.MasterBinder;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.helper.DamageHelper;

public class EntityRelicGuard extends EntityCreature implements IRangedAttackMob, IHasMaster {

	public static final int STATUS_DORMANCY = 0;
	public static final int STATUS_ACTIVE = 1;
	public static final int STATUS_WORN = 2;

	public static final int TYPE_FIRE = 0;
	public static final int TYPE_WATER = 1;

	protected static final DataParameter<Byte> STATUS = EntityDataManager.<Byte>createKey(EntityRelicGuard.class,
			DataSerializers.BYTE);
	protected static final DataParameter<Byte> TYPE = EntityDataManager.<Byte>createKey(EntityRelicGuard.class,
			DataSerializers.BYTE);
	protected static final DataParameter<Boolean> IS_SPELLING = EntityDataManager
			.<Boolean>createKey(EntityRelicGuard.class, DataSerializers.BOOLEAN);

	protected MasterBinder master = new MasterBinder();

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
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		master.writeEntityToNBT(compound);
		compound.setByte("status", getStatus());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		master.readEntityFromNBT(compound);
		setStatus(compound.getByte("status"));
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIAttackRanged(this, 1.0D, 40, 32.0F));
		this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		// this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
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
//		System.out.println(status);
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

	public void setType(int status) {
		this.dataManager.set(TYPE, (byte) status);
	}

	public boolean isSpelling() {
		return this.dataManager.get(IS_SPELLING);
	}

	public void setSpelling(boolean spelling) {
		this.dataManager.set(IS_SPELLING, spelling);
	}

	public boolean hasCore() {
		return true;
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public void fall(float distance, float damageMultiplier) {

	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (DamageHelper.isRuleDamage(source)) return super.attackEntityFrom(source, amount);
		return super.attackEntityFrom(source, Math.min(amount, 4));
	}

	@Override
	protected void damageEntity(DamageSource damageSrc, float damageAmount) {
		super.damageEntity(damageSrc, damageAmount);
		if (getStatus() == STATUS_WORN) return;
		if (this.getHealth() <= 10) this.setStatus(STATUS_WORN);
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
		this.setLastAttackedEntity(target);
//		this.setSpelling(false);
		this.swingArm(EnumHand.MAIN_HAND);
		target.attackEntityFrom(DamageHelper.getMagicButNotDamageSource(this, null), 10);
	}

	@Override
	public void setSwingingArms(boolean swingingArms) {

	}

	@Override
	protected void onDeathUpdate() {
		if (hasCore()) {
			if (!world.isRemote) {
				world.createExplosion(this, posX, posY, posZ, 10, true);
				this.setDead();
			}
		} else super.onDeathUpdate();
	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		updateArmSwingProgress();
		if (world.isRemote) {
			updateClient();
			return;
		}

		int status = getStatus();
		if (status == STATUS_WORN) {
			if (hasCore()) this.setHealth(this.getHealth() - 0.1f);
			return;
		}

		if (status == STATUS_DORMANCY) {
			if (this.aiTarget.shouldExecute()) this.aiTarget.startExecuting();
			if (this.getAttackTarget() != null) {
				this.setStatus(STATUS_ACTIVE);
				this.setLastAttackedEntity(null);
			}
			return;
		}

		if (this.getAttackTarget() != null) return;

		if (status == STATUS_ACTIVE) {
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
