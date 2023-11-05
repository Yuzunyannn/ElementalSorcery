package yuzunyannn.elementalsorcery.entity.mob;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.mantra.SilentLevel;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFireBall;
import yuzunyannn.elementalsorcery.item.prop.ItemKeepsake;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.render.effect.scrappy.FireworkEffect;
import yuzunyannn.elementalsorcery.util.helper.DamageHelper;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;

public class EntityRelicZombie extends EntityMob {

	protected static final DataParameter<Byte> TYPE = EntityDataManager.<Byte>createKey(EntityRelicZombie.class,
			DataSerializers.BYTE);

	protected int skillType;

	public boolean hasCore = true;
	public boolean dungeonMode = false;

	public EntityRelicZombie(World worldIn) {
		this(worldIn, RelicZombieType.randomType(worldIn.rand));
	}

	public EntityRelicZombie(World worldIn, RelicZombieType type) {
		super(worldIn);
		this.setSize(0.6F, 1.95F);
		this.dataManager.set(TYPE, (byte) type.getId());
		this.experienceValue = 300;
		switch (type) {
		case WARRIOR:
			setHeldItem(Items.WOODEN_SWORD);
			break;
		case PRIEST:
			setHeldItem(Items.LINGERING_POTION);
			break;
		case WIZARD:
			setHeldItem(ESObjects.ITEMS.SPELLBOOK);
			break;
		}
		this.setDropChance(EntityEquipmentSlot.MAINHAND, 0);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		switch (getType()) {
		case WARRIOR:
			setHeldItem(Items.WOODEN_SWORD);
			break;
		case PRIEST:
			setHeldItem(Items.LINGERING_POTION);
			break;
		case WIZARD:
			setHeldItem(ESObjects.ITEMS.SPELLBOOK);
			break;
		}
		this.setDropChance(EntityEquipmentSlot.MAINHAND, 0);
		return super.onInitialSpawn(difficulty, livingdata);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(5, new EntityAIRelicZombieAttack(this, 1.0D));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[] { EntityPigZombie.class }));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVillager.class, false));
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(TYPE, (byte) 0);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setByte("rzType", this.dataManager.get(TYPE).byteValue());
		if (!hasCore) compound.setBoolean("noCore", true);
		if (dungeonMode) compound.setBoolean("dungeonMode", true);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		this.dataManager.set(TYPE, compound.getByte("rzType"));
		hasCore = !compound.getBoolean("noCore");
		dungeonMode = compound.getBoolean("dungeonMode");
	}

	protected void setHeldItem(Item item) {
		this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(item));
	}

	protected void setHeldItem(ItemStack item) {
		this.setHeldItem(EnumHand.MAIN_HAND, item);
	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_ZOMBIE_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_ZOMBIE_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_ZOMBIE_DEATH;
	}

	protected SoundEvent getStepSound() {
		return SoundEvents.ENTITY_ZOMBIE_STEP;
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	protected void updateEquipmentIfNeeded(EntityItem itemEntity) {
		ItemStack itemstack = itemEntity.getItem();
		EntityEquipmentSlot entityequipmentslot = getSlotForItemStack(itemstack);
		// 主手不允许替换
		if (entityequipmentslot == EntityEquipmentSlot.MAINHAND) return;
		super.updateEquipmentIfNeeded(itemEntity);
	}

	@Override
	protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
		DamageSource ds = this.getLastDamageSource();

		if (ds != null && "player".equals(ds.damageType) && hasCore && !dungeonMode) {
			int n = 1 + this.rand.nextInt(lootingModifier / 2 + 1);
			for (int i = 0; i < n; i++) {
				if (this.rand.nextFloat() >= 0.85f) break;
				this.dropItem(ESObjects.ITEMS.RELIC_GEM, 1);
			}
		}

		int n = this.rand.nextInt(4);
		if (lootingModifier > 0) n += this.rand.nextInt(lootingModifier + 1);
		RandomHelper.WeightRandom<Item> wr = new RandomHelper.WeightRandom<>();
		wr.add(Items.ROTTEN_FLESH, 50);
		wr.add(ESObjects.ITEMS.MAGIC_CRYSTAL, 20);
		wr.add(Items.IRON_INGOT, 5);
		for (int i = 0; i < n; i++) this.dropItem(wr.get(), 1);
		this.entityDropItem(ItemKeepsake.create(ItemKeepsake.EnumType.RELIC_FRAGMENT, 1), 0);
	}

	public void onShock() {
		if (!hasCore) return;
		if (dungeonMode) return;
		if (world.isRemote) return;
		float hpRate = this.getHealth() / this.getMaxHealth();
		if (hpRate > 0.5) return;
		if (hpRate < 0.1 || 0.2 / Math.max(this.getHealth(), 0.0001f) > this.rand.nextDouble()) {
			hasCore = false;
			world.setEntityState(this, (byte) 44);
			this.setHealth(0);
			this.dropItem(ESObjects.ITEMS.RELIC_GEM, 1);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id) {
		if (id == 44) hasCore = false;
	}

	// 效果的cd时间，防止播放太多
	public int effectCD;

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		if (world.isRemote) this.onEntityUpdateClient();
		if (effectCD > 0) effectCD--;
	}

	private RelicZombieType typeCache;

	public RelicZombieType getType() {
		if (typeCache != null) return typeCache;
		return typeCache = RelicZombieType.getTypeFromId(this.dataManager.get(TYPE));
	}

	@SideOnly(Side.CLIENT)
	public void onEntityUpdateClient() {
		if (this.ticksExisted % 10 == 0) {
			Vec3d pos = this.getPositionVector().add(0, 1.22 - MathHelper.cos(ticksExisted * 0.03F) * 0.075, 0);
			EffectElementMove effect = new EffectElementMove(world, pos);
			effect.lifeTime = 20;
			effect.dalpha = 1.0f / effect.lifeTime;
			effect.scale = 0.2f;
			effect.setColor(this.getType().getColor());
			Effect.addEffect(effect);
		}
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		if (world.isRemote) {
			typeCache = null;
			if (HAND_STATES.equals(key)) {
				if (this.isHandActive()) this.activeItemStackUseCount = 20 * 60;
				else this.activeItemStackUseCount = 0;
			}
		}
	}

	@Override
	public void setActiveHand(EnumHand hand) {
		if (!this.isHandActive()) {
			int duration = 20 * 60;
			this.activeItemStackUseCount = duration;
			if (!this.world.isRemote) {
				int i = 1;
				if (hand == EnumHand.OFF_HAND) i |= 2;
				this.dataManager.set(HAND_STATES, Byte.valueOf((byte) i));
			}
		}
	}

	@Override
	protected void updateActiveHand() {
		if (!this.isHandActive()) return;
		if (--this.activeItemStackUseCount <= 0 && !this.world.isRemote) this.resetActiveHand();
	}

	@Override
	public boolean isPotionApplicable(PotionEffect potioneffectIn) {
		Potion potion = potioneffectIn.getPotion();
		if (potion == MobEffects.POISON) return false;
		return super.isPotionApplicable(potioneffectIn);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		switch (this.getType()) {
		case PRIEST:
			return this.attackEntityAsMobPriest(entityIn);
		case WIZARD:
			return this.attackEntityAsMobWizard(entityIn);
		case WARRIOR:
		}
		return super.attackEntityAsMob(entityIn);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (source.isMagicDamage()) {
			// 同类型单位伤害不计算
			Entity src = source.getTrueSource();
			if (src != null && src.getClass() == EntityRelicZombie.class) return false;
		}

		RelicZombieType type = this.getType();

		// 地牢模式的简单
		if (dungeonMode) {
			amount *= 2.5f;
			if (type == RelicZombieType.WARRIOR) amount *= 1.25;
		}

		if (DamageHelper.isRuleDamage(source)) return super.attackEntityFrom(source, amount);

		switch (type) {
		case WARRIOR:
			return this.attackEntityFromWarrior(source, amount);
		case PRIEST:
			return this.attackEntityFromPriest(source, amount);
		case WIZARD:
			return this.attackEntityFromWizard(source, amount);
		}
		return super.attackEntityFrom(source, amount);
	}

	// 巫师攻击
	protected boolean attackEntityAsMobWizard(Entity entityIn) {
		if (this.isHandActive()) {
			if (ESAPI.silent.isSilent(this, SilentLevel.SPELL)) return false;
			int count = 20 * 60 - this.activeItemStackUseCount;
			if (count % 10 == 0) {
				Entity target = this.getAttackTarget();
				if (target != null) this.getLookHelper().setLookPositionWithEntity(target, 40.0F, 40.0F);
			}
			switch (skillType) {
			default: {
				if (count < 20 * 4) return false;
				EntityLightningBolt lightning = new EntityLightningBolt(world, entityIn.posX, entityIn.posY,
						entityIn.posZ, true);
				world.addWeatherEffect(lightning);
				entityIn.attackEntityFrom(DamageSource.LIGHTNING_BOLT, 6);
				this.resetActiveHand();
				return true;
			}
			case 1: {
				if (count < 20 * 8) return false;
				MantraFireBall.fire(world, this, 16 + rand.nextInt(16), true);
				this.resetActiveHand();
				return true;
			}
			case 2: {
				if (count < 20 * 5) return false;
				EntityZombie ez = new EntityZombie(world);
				ez.setChild(true);
				ez.setPosition(this.posX, this.posY, this.posZ);
				world.spawnEntity(ez);
				this.resetActiveHand();
				return true;
			}
			}
		}
		if (this.rand.nextFloat() < 0.5) return false;
		this.setActiveHand(EnumHand.MAIN_HAND);
		skillType = rand.nextInt(3);
		switch (skillType) {
		default:
			this.setHeldItem(ESObjects.ITEMS.ORDER_CRYSTAL);
			break;
		case 1:
			this.setHeldItem(Item.getItemFromBlock(Blocks.TORCH));
			break;
		case 2:
			this.setHeldItem(new ItemStack(Items.SKULL, 1, 2));
			break;
		}
		return true;
	}

	// 巫师受到攻击
	protected boolean attackEntityFromWizard(DamageSource source, float amount) {
		if (DamageHelper.isRangedDamage(source)) {
			if (source.getTrueSource() instanceof EntityLivingBase) {
				Entity src = source.getImmediateSource();
				Vec3d vec;
				if (src != null) vec = src.getPositionVector();
				else vec = this.getPositionVector().add(0, 1, 0);
				if (!world.isRemote && effectCD <= 0) {
					effectCD = 30;
					NBTTagCompound nbt = FireworkEffect.fastNBT(0, 1, 0.1f, new int[] { 0x0000ff },
							new int[] { 0x93f6ff });
					Effects.spawnEffect(world, Effects.FIREWROK, vec, nbt);
				}
				return false;
			}
		}
		return super.attackEntityFrom(source, amount);
	}

	// 战士受到攻击
	protected boolean attackEntityFromWarrior(DamageSource source, float amount) {
		boolean flag = super.attackEntityFrom(source, amount);
		if (flag) {
			double maxHealth = this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue();
			double health = this.getHealth();
			double rate = health / maxHealth;
			if (rate < 0.4) {
				this.addPotionEffect(new PotionEffect(MobEffects.SPEED, 20 * 10, 3));
				this.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 20 * 10, 3));
				this.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 20 * 10, 1));
				this.addPotionEffect(new PotionEffect(MobEffects.HASTE, 20 * 10, 1));
				this.setHeldItem(ESObjects.ITEMS.MAGIC_GOLD_SWORD);
			} else if (rate < 0.6) this.setHeldItem(Items.DIAMOND_SWORD);
			else if (rate < 0.8) this.setHeldItem(Items.IRON_SWORD);
		}
		return flag;
	}

	protected void priestResetEnd(Entity entity) {
		world.playSound(null, entity.getPosition(), SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.PLAYERS, 1,
				1);
		this.resetActiveHand();
	}

	// 牧师攻击
	protected boolean attackEntityAsMobPriest(Entity entityIn) {
		if (this.isHandActive()) {
			int count = 20 * 60 - this.activeItemStackUseCount;
			switch (skillType) {
			default: {
				if (count < 20 * 4) return false;
				if (entityIn instanceof EntityLivingBase) {
					EntityLivingBase living = ((EntityLivingBase) entityIn);
					living.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20 * 10, rand.nextInt(3) + 1));
					living.addPotionEffect(new PotionEffect(MobEffects.POISON, 20 * 5, rand.nextInt(3) + 1));
					living.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 20 * 4, 1));
				}
				this.priestResetEnd(entityIn);
				return true;
			}
			case 1: {
				if (count < 20 * 4) return false;
				AxisAlignedBB aabb = new AxisAlignedBB(entityIn.posX - 8, entityIn.posY, entityIn.posZ - 8,
						entityIn.posX + 8, entityIn.posY + 4, entityIn.posZ + 8);
				List<EntityMob> mobs = world.getEntitiesWithinAABB(EntityMob.class, aabb);
				for (EntityMob mob : mobs) {
					mob.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 20 * 10, 1 + rand.nextInt(2)));
					mob.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 20 * 10, 1 + rand.nextInt(2)));
					mob.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 20 * 10, 1));
				}
				this.priestResetEnd(entityIn);
				return true;
			}
			case 2: {
				if (count < 20 * 4) return false;
				AxisAlignedBB aabb = new AxisAlignedBB(entityIn.posX - 8, entityIn.posY, entityIn.posZ - 8,
						entityIn.posX + 8, entityIn.posY + 4, entityIn.posZ + 8);
				List<EntityMob> mobs = world.getEntitiesWithinAABB(EntityMob.class, aabb);
				for (EntityMob mob : mobs) {
					mob.addPotionEffect(new PotionEffect(MobEffects.HEALTH_BOOST, 20 * 10, 1 + rand.nextInt(2)));
					mob.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 20 * 10, 1 + rand.nextInt(2)));
					mob.addPotionEffect(new PotionEffect(MobEffects.SPEED, 20 * 10, 1 + rand.nextInt(2)));
					mob.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 20 * 10, 2 + rand.nextInt(2)));
				}
				this.priestResetEnd(entityIn);
				return true;
			}
			}
		}
		if (this.rand.nextFloat() < 0.8) return false;
		this.setActiveHand(EnumHand.MAIN_HAND);
		skillType = rand.nextInt(3);
		ItemStack handShow = new ItemStack(Items.POTIONITEM);
		switch (skillType) {
		default:
			PotionUtils.addPotionToItemStack(handShow, PotionTypes.POISON);
			break;
		case 1:
			PotionUtils.addPotionToItemStack(handShow, PotionTypes.REGENERATION);
			break;
		case 2:
			PotionUtils.addPotionToItemStack(handShow, PotionTypes.STRENGTH);
			break;
		}
		this.setHeldItem(handShow);
		return true;
	}

	// 牧师受到攻击
	protected boolean attackEntityFromPriest(DamageSource source, float amount) {
		if (this.isHandActive()) return super.attackEntityFrom(source, amount);
		boolean flag = super.attackEntityFrom(source, amount);
		if (flag) {
			double maxHealth = this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue();
			double health = this.getHealth();
			double rate = health / maxHealth;
			if (rate < 0.5) {
				this.addPotionEffect(new PotionEffect(MobEffects.SPEED, 20 * 10, 1));
				this.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 20 * 10, 2));
				this.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 20 * 10, 1));
				this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 20 * 10, 1));
			}
		}
		return flag;
	}

}
