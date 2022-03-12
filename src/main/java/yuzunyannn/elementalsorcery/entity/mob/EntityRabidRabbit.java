package yuzunyannn.elementalsorcery.entity.mob;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.init.LootRegister;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class EntityRabidRabbit extends EntityRabbit implements IMob {

	public EntityRabidRabbit(World worldIn) {
		super(worldIn);
	}

	protected void initEntityAI() {
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIMate(this, 1));
		this.tasks.addTask(3, new EntityAIAttackMelee(this, 1, false));
		this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 0.6D));
//		this.tasks.addTask(8, new EntityAILookIdle(this));
		this.tasks.addTask(11, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootRegister.RABID_RABBIT;
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(6.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
	}

	public boolean isBreedingItem(ItemStack stack) {
		return false;
	}

	@Override
	protected float getJumpUpwardsMotion() {
		float point = super.getJumpUpwardsMotion();
		Entity target = this.getAttackingEntity();
		if (target != null && target.posY > this.posY + 1) return point * 2;
		return point;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		int age = this.getGrowingAge();
		if (age > 0) this.setGrowingAge(0);

		Entity target = this.getAttackTarget();
		if (target == null || target.isDead) {
			if (this.ticksExisted % 20 == 0) {
				AxisAlignedBB aabb = WorldHelper.createAABB(this.getPositionVector(), 8, 3, 2);
				List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
				EntityLivingBase selectTarget = null;
				int selectPriority = -1;
				for (EntityLivingBase living : entities) {
					int priority = getAttackPriority(living);
					if (priority > selectPriority) {
						selectPriority = priority;
						selectTarget = living;
					}
				}
				this.setAttackTarget(selectTarget);
			}
		} else {
			if (this.onGround && !this.isJumping && this.rand.nextInt(10) == 0) {
				Vec3d look = this.getLookVec();
				Vec3d pos = this.getPositionVector();
				Vec3d tar = target.getPositionVector().subtract(pos);
				if (tar.lengthSquared() > 2 * 2) {
					tar = tar.normalize();
					double cos = look.dotProduct(tar) / look.length() * tar.length();
					if (cos > 0.8) {
						tar = tar.scale(0.75);
						this.motionX += tar.x;
						this.motionY += 0.5;
						this.motionZ += tar.z;
					}
				}
			}
		}
	}

	@Override
	public boolean isWithinHomeDistanceFromPosition(BlockPos pos) {
		return true;
	}

	public static int getAttackPriority(EntityLivingBase living) {
		if (living instanceof EntityRabbit) return -1;
		if (living instanceof EntityPlayer) return ((EntityPlayer) living).isCreative() ? -1 : 10;
		if (living instanceof INpc) return 8;
		if (living instanceof EntityAnimal) return 5;
		if (living instanceof IMob) {
			if (living instanceof EntityDreadCube) return -1;
			return 2;
		}
		return -1;
	}

	@Override
	public EntityRabbit createChild(EntityAgeable ageable) {
		EntityRabbit entityrabbit = new EntityRabidRabbit(this.world);
		entityrabbit.setRabbitType(this.rand.nextInt(5));
		return entityrabbit;
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		return false;
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {

		boolean succ = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 6.0F);
		this.playSound(SoundEvents.ENTITY_RABBIT_ATTACK, 1.0F,
				(this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);

		if (!succ) return false;

		if (this.isChild()) this.addGrowth(60 * 20);
		else if (!this.isInLove()) {
			EntityPlayer player = entityIn instanceof EntityPlayer ? (EntityPlayer) entityIn : null;
			this.setInLove(player);
		}

		this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 30, 3));
		return succ;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (source == DamageSource.FALL) return false;
		if (source.isExplosion()) amount = amount * 0.1f;
		if (!this.isChild()) amount = amount * 0.5f;
		return super.attackEntityFrom(source, amount);
	}

}
