package yuzunyannn.elementalsorcery.entity.elf;

import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityFlyHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.block.BlockElfFruit;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfession;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;
import yuzunyannn.elementalsorcery.util.NBTHelper;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public abstract class EntityElfBase extends EntityCreature {

	protected ElfProfession profession = ElfProfession.NONE;
	/** nbt数据不同步 */
	protected NBTTagCompound tempNBT;
	/** 职业数据更新 */
	public static final DataParameter<Integer> PROFESSION_UPDATE = EntityDataManager.createKey(EntityElfBase.class,
			DataSerializers.VARINT);
	/** 说或者 */
	public EntityLivingBase talker = null;
	/** 精灵时钟，非持久化 */
	public int tick;

	public EntityElfBase(World worldIn) {
		super(worldIn);
		this.experienceValue = 10;
		this.setSize(0.6f, 1.9f);
		this.setCanPickUpLoot(true);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(PROFESSION_UPDATE, 0);
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(1.0D);

		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.24D);
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();

		this.tasks.addTask(0, new EntityAISwimming(this));

		this.tasks.addTask(1, new EntityAILookTalker(this));
		this.tasks.addTask(3, new EntityAIAttackElf(this));
		this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false, new Class[0]));
		this.targetTasks.addTask(3, new EntityAINearestAttackTarget(this));
	}

	public void removeTask(Class<? extends EntityAIBase> cls) {
		Iterator<EntityAITasks.EntityAITaskEntry> iterator = this.tasks.taskEntries.iterator();
		while (iterator.hasNext()) {
			EntityAITasks.EntityAITaskEntry entityaitasks$entityaitaskentry = iterator.next();
			EntityAIBase entityaibase = entityaitasks$entityaitaskentry.action;
			if (entityaibase.getClass().equals(cls)) {
				this.tasks.removeTask(entityaibase);
				return;
			}
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		ResourceLocation name = this.getProfession().getRegistryName();
		compound.setString("professionId", name.toString());
		BlockPos homePos = this.getHomePosition();
		if (homePos != null && !homePos.equals(BlockPos.ORIGIN)) NBTHelper.setBlockPos(compound, "homePos", homePos);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		String id = compound.getString("professionId");
		this.setProfession(ElfProfession.REGISTRY.getValue(new ResourceLocation(id)));
		if (NBTHelper.hasBlockPos(compound, "homePos"))
			this.setHomePosAndDistance(NBTHelper.getBlockPos(compound, "homePos"), 2);
	}

	/** 获取临时数据NBT，不会被保存，不会同步 */
	public NBTTagCompound getTempNBT() {
		if (tempNBT == null) tempNBT = new NBTTagCompound();
		return tempNBT;
	}

	public NBTTagCompound clearTempNBT() {
		tempNBT = new NBTTagCompound();
		return tempNBT;
	}

	public void setFlyMode(boolean fly) {
		if (fly) {
			this.navigator = new PathNavigateFlying(this, this.world);
			this.moveHelper = new EntityFlyHelper(this);
		} else {
			this.navigator = new PathNavigateGround(this, this.world);
			this.moveHelper = new EntityMoveHelper(this);
		}

	}

	public void setTalker(EntityLivingBase talker) {
		this.talker = talker;
	}

	public EntityLivingBase getTalker() {
		return talker;
	}

	/** 设置所属的精灵大厦核心 */
	public void setEdificeCore(BlockPos pos) {
		NBTTagCompound nbt = this.getEntityData();
		NBTHelper.setBlockPos(nbt, "edifice", pos);
	}

	/**
	 * 获取所属的精灵大厦核心
	 * 
	 * @return 获取不到的情况下，可能不属属于任何，或者属于的核心被拆毁
	 */
	@Nullable
	public TileElfTreeCore getEdificeCore() {
		NBTTagCompound nbt = this.getEntityData();
		BlockPos at = NBTHelper.getBlockPos(nbt, "edifice");
		return BlockHelper.getTileEntity(world, at, TileElfTreeCore.class);
	}

	// 环境音
	protected SoundEvent getAmbientSound() {
		// return SoundEvents.ENTITY_CAT_AMBIENT;
		return null;
	}

	// 受伤音
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_PLAYER_HURT;
	}

	// 死亡音
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_PLAYER_DEATH;
	}

	public String getElfName() {
		return this.getCustomNameTag();
	}

	@Override
	public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
		if (hand != EnumHand.MAIN_HAND) return EnumActionResult.PASS;
		if (this.getTalker() != null) return EnumActionResult.FAIL;
		ElfProfession pro = this.getProfession();
		return pro.interact(this, player) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
	}

	/** 掉落一些物品 */
	@Override
	protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
		if (this.rand.nextInt(100) < 50) return;
		int i = this.rand.nextInt(3);
		if (lootingModifier > 0) i += this.rand.nextInt(lootingModifier + 1);
		for (int j = 0; j < i; ++j) if (this.rand.nextInt(3) == 0)
			this.entityDropItem(new ItemStack(ESInitInstance.ITEMS.ELF_COIN, this.rand.nextInt(7) + 1), 0);
		else this.entityDropItem(new ItemStack(ESInitInstance.BLOCKS.ELF_FRUIT, 1, BlockElfFruit.MAX_STATE), 0);
	}

	/** 捡起物品 */
	@Override
	protected void updateEquipmentIfNeeded(EntityItem itemEntity) {
		ItemStack itemstack = itemEntity.getItem();
		EntityEquipmentSlot entityequipmentslot = getSlotForItemStack(itemstack);
		// 测试装备装备
		if (entityequipmentslot != EntityEquipmentSlot.MAINHAND || itemstack.getItem() instanceof ItemSword) {
			if (this.getProfession().canEquip(this, itemstack, entityequipmentslot)) {
				super.updateEquipmentIfNeeded(itemEntity);
				return;
			}
		}
		// 普通建起
		if (itemstack.isEmpty()) return;
		if (this.getProfession().needPickup(this, itemstack)) {
			int n = itemstack.getCount();
			ItemStack remain = this.pickupItem(itemstack);
			if (remain.getCount() < n) {
				this.onItemPickup(itemEntity, n - remain.getCount());
				if (remain.isEmpty()) itemEntity.setDead();
			}
		}

	}

	@Override
	public void onDeath(DamageSource cause) {
		super.onDeath(cause);
		// 记录
		if (cause.getTrueSource() instanceof EntityPlayerMP) {
			ESCriteriaTriggers.PLAYER_KILLED_ELF.trigger((EntityPlayerMP) cause.getTrueSource(), this, cause);
		}
	}

	/** 受到攻击，精灵的反应 */
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		int flag = this.getProfession().attackedFrom(this, source, amount);
		if (flag == -1) return false;
		else if (flag == 1) return true;
		return super.attackEntityFrom(source, amount);
	}

	/** 攻击敌人 */
	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		boolean flag = this.getProfession().attackEntity(this, entityIn);
		if (flag && entityIn instanceof EntityPlayerMP)
			ESCriteriaTriggers.ELF_HURT_PLAYER.trigger((EntityPlayerMP) entityIn, this);
		return flag;
	}

	/** 传送到某处 */
	public void teleportTo(Entity entity, BlockPos to) {
		if (to == null) return;
		world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT,
				SoundCategory.HOSTILE, 1.0F, 1.0F);
		entity.setPositionAndUpdate(to.getX(), to.getY(), to.getZ());
		world.playSound(null, to.getX(), to.getY(), to.getZ(), SoundEvents.ENTITY_ENDERMEN_TELEPORT,
				SoundCategory.HOSTILE, 1.0F, 1.0F);
	}

	/** 寻找一个随机的位置进行传送 */
	public BlockPos getRandomTeleportPos(int tryTimes, int far, int size, BlockPos originPos) {
		for (int t = 0; t < tryTimes; t++) {
			int x = size - rand.nextInt(size) * 2;
			x = x < 0 ? (x - far) : (x + far);
			int z = size - rand.nextInt(size) * 2;
			z = z < 0 ? (z - far) : (z + far);
			for (int y = 0; y < 2; y++) {
				BlockPos pos = originPos.add(x, y, z);
				if (world.isAirBlock(pos)) return pos;
			}
		}
		return null;
	}

	/** 攻击敌人(普通) */
	public boolean attackEntityAsMobDefault(Entity target) {
		float dmg = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
		int i = 0;
		if (target instanceof EntityLivingBase) {
			dmg += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(),
					((EntityLivingBase) target).getCreatureAttribute());
			i += EnchantmentHelper.getKnockbackModifier(this);
		}
		boolean flag = target.attackEntityFrom(DamageSource.causeMobDamage(this), dmg);
		if (flag) {
			if (i > 0 && target instanceof EntityLivingBase) {
				((EntityLivingBase) target).knockBack(this, (float) i * 0.5F,
						(double) MathHelper.sin(this.rotationYaw * 0.017453292F),
						(double) (-MathHelper.cos(this.rotationYaw * 0.017453292F)));
				this.motionX *= 0.6D;
				this.motionZ *= 0.6D;
			}
			int j = EnchantmentHelper.getFireAspectModifier(this);
			if (j > 0) target.setFire(j * 4);
			if (target instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer) target;
				ItemStack itemstack = this.getHeldItemMainhand();
				ItemStack itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack()
						: ItemStack.EMPTY;
				if (!itemstack.isEmpty() && !itemstack1.isEmpty()
						&& itemstack.getItem().canDisableShield(itemstack, itemstack1, entityplayer, this)
						&& itemstack1.getItem().isShield(itemstack1, entityplayer)) {
					float f1 = 0.25F + (float) EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;
					if (this.rand.nextFloat() < f1) {
						entityplayer.getCooldownTracker().setCooldown(itemstack1.getItem(), 100);
						this.world.setEntityState(entityplayer, (byte) 30);
					}
				}
			}
			this.applyEnchantments(this, target);
		}
		return flag;
	}

	@Override
	public void onUpdate() {
		try {
			super.onUpdate();
		} catch (Exception e) {
			ElementalSorcery.logger.warn("这只精灵出现了异常！" + this, e);
			this.setDead();
		}
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		this.updateArmSwingProgress();
		tick++;
		this.getProfession().tick(this);
	}

	/** 获取精灵职业 */
	@Nonnull
	public ElfProfession getProfession() {
		return profession;
	}

	/** 获取设置精灵 职业 */
	public void setProfession(ElfProfession profession) {
		if (profession == null) profession = ElfProfession.NONE;
		if (this.profession == profession) return;
		ElfProfession origin = this.profession;
		this.profession = profession;
		if (world.isRemote) return;
		if (origin != null) origin.transferElf(this, this.profession);
		this.profession.initElf(this, origin);
		dataManager.set(PROFESSION_UPDATE, ElfProfession.REGISTRY.getId(this.profession));
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		super.notifyDataManagerChange(key);
		if (key.getId() == PROFESSION_UPDATE.getId() && world.isRemote) {
			this.profession = ElfProfession.REGISTRY.getValue(dataManager.get(PROFESSION_UPDATE));
			if (this.profession == null) this.profession = ElfProfession.NONE;
		}
	}

	abstract protected ItemStack pickupItem(ItemStack stack);

	abstract public void tryHarvestBlock(BlockPos pos);

	abstract public boolean tryPlaceBlock(BlockPos pos, IBlockState block);
}
