package yuzunyannn.elementalsorcery.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityFlyHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.util.helper.DamageHelper;
import yuzunyannn.elementalsorcery.util.math.MathSupporter;
import yuzunyannn.elementalsorcery.util.world.EntityMasterBinder;

public class EntityPuppet extends EntityCreature {

	protected int lifeTick = 120;
	protected EntityMasterBinder myMaster = new EntityMasterBinder();
	protected EntityMasterBinder myTarget = new EntityMasterBinder().setDataKey("mTar");
	protected float pDamage;
	protected float nDamage;

	public EntityPuppet(World worldIn) {
		super(worldIn);
		initSelf();
	}

	public EntityPuppet(World worldIn, int lifeTick, EntityLivingBase master, EntityLivingBase target) {
		super(worldIn);
		initSelf();
		this.myMaster.setMaster(master);
		this.myTarget.setMaster(target);
		this.lifeTick = lifeTick;
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, false));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(6, new EntityAILookIdle(this));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
		this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(0.5);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.setSize(0.5f, 0.5f);
		this.setNoGravity(true);
	}

	protected void initSelf() {
		this.moveHelper = new EntityFlyHelper(this);
	}

	@Override
	protected PathNavigate createNavigator(World worldIn) {
		return new PathNavigateFlying(this, this.world);
	}

	public EntityPuppet setDamage(float pDmg, float nDmg) {
		this.pDamage = pDmg;
		this.nDamage = nDmg;
		return this;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		this.myMaster.writeDataToNBT(compound);
		this.myTarget.writeDataToNBT(compound);
		compound.setInteger("lifeTick", lifeTick);
		compound.setFloat("pD", pDamage);
		compound.setFloat("nD", nDamage);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		this.myMaster.readDataFromNBT(compound);
		this.myTarget.readDataFromNBT(compound);
		this.lifeTick = compound.getInteger("lifeTick");
		this.pDamage = compound.getFloat("pD");
		this.nDamage = compound.getFloat("nD");
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_PARROT_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_PARROT_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_PARROT_DEATH;
	}

	@Override
	public void applyEntityCollision(Entity entityIn) {
		super.applyEntityCollision(entityIn);
		this.motionX *= 0.25;
		this.motionZ *= 0.25;
	}

	@Override
	protected void collideWithEntity(Entity entityIn) {
		super.collideWithEntity(entityIn);
		this.motionX *= 0.25;
		this.motionZ *= 0.25;
	}

	@Override
	protected void updateEquipmentIfNeeded(EntityItem itemEntity) {

	}

	@Override
	protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
		super.dropFewItems(wasRecentlyHit, lootingModifier);
	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		if (world.isRemote) this.onEntityUpdateClient();
		else {
			if (this.lifeTick-- <= 0) this.setHealth(0);
			if (this.getAttackTarget() != myTarget.tryGetMaster(world)) this.setAttackTarget(myTarget.getMaster());
		}
	}

	@SideOnly(Side.CLIENT)
	public void onEntityUpdateClient() {

	}

	@Override
	public boolean isPotionApplicable(PotionEffect potioneffectIn) {
		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (source.isFireDamage()) amount *= 2;
		if (!super.attackEntityFrom(source, amount)) return false;
		this.motionX *= 0.75;
		this.motionZ *= 0.75;
		return true;
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		this.setLastAttackedEntity(entityIn);

		this.swingArm(EnumHand.MAIN_HAND);

		EntityLivingBase master = myMaster.tryGetMaster(world);
		DamageSource ds = DamageHelper.getDamageSource(new ElementStack(ESObjects.ELEMENTS.WOOD),
				master == null ? this : master, this);
		float amount = nDamage;
		if (entityIn instanceof EntityLivingBase) {
			float maxHP = ((EntityLivingBase) entityIn).getMaxHealth();
			amount += maxHP * pDamage;
		}

		return entityIn.attackEntityFrom(ds, amount);
	}

	@Override
	protected void onDeathUpdate() {
		++this.deathTime;
		if (this.deathTime == 20) {
			this.setDead();
		}

		double r = (deathTime - 1.0F) / 20.0F;
		r = 1 - MathSupporter.easeOutBack(1 - r * r);
		double d2 = this.rand.nextGaussian() * 0.02D;
		double d0 = this.rand.nextGaussian() * 0.02D;
		double d1 = this.rand.nextGaussian() * 0.02D;
		this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL,
				this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width,
				this.posY + (double) (this.rand.nextFloat() * this.height) + Math.max(0, r * 10),
				this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, d2, d0, d1);
	}

}
