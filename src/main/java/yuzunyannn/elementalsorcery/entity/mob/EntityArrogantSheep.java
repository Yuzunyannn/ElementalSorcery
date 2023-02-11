package yuzunyannn.elementalsorcery.entity.mob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.block.env.BlockGoatGoldBrick;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.render.effect.scrappy.FireworkEffect;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.DamageHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.helper.SilentWorld;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class EntityArrogantSheep extends EntityMob {

	protected static final DataParameter<Boolean> IS_SHEARED = EntityDataManager
			.<Boolean>createKey(EntityArrogantSheep.class, DataSerializers.BOOLEAN);

	protected int shareTims = 0;

	// 描述迷宫内玩家的状态
	public static class PlayerInMazeStatus {
		boolean isTowFloor;
		boolean isFristInTwoFloor = true;
		int findTimes = 0;
		int overTimes = 60;
	}

	protected Map<UUID, PlayerInMazeStatus> playerInMaze = new HashMap<>();
	protected List<EntityMob> zombieCache;

	public EntityArrogantSheep(World worldIn) {
		super(worldIn);
		this.setSize(0.9F, 1.3F);
	}

	public EntityArrogantSheep(World worldIn, BlockPos center) {
		this(worldIn);
		this.setStandPosition(center);
		this.setPosition(center.getX() + 0.5, center.getY(), center.getZ() + 0.5);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIPanic(this, 1.25D));
		this.tasks.addTask(8, new EntityAILookIdle(this));
	}

	public void dyingSay() {
		List<EntityPlayer> list = getPlayerInMaze(true);
		ITextComponent text = createSheepSay("say.arrogantSheep.myFur");
		for (EntityPlayer player : list) player.sendMessage(text);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		this.extinguish();
		if (world.isRemote) return;

		if (getSheared()) {
			EntityLivingBase revenge = getRevengeTarget();
			setRevengeTarget(revenge);
			if (this.ticksExisted % 20 == 0) {
				setHealth(getHealth() - getMaxHealth() / 16);
				if (getHealth() <= 0) BlockGoatGoldBrick.tryWitherGoatGoldBrickArea(world, this.getPosition(), true);
				if (rand.nextFloat() < 0.25f) dyingSay();
			}
			return;
		}

		if (world.isAirBlock(new BlockPos(this.posX, this.posY - 0.2, this.posZ))) {
			List<EntityPlayer> list = getPlayerInMaze(true);
			for (EntityPlayer player : list) player.sendMessage(createSheepSay("say.arrogantSheep.escape"));
			BlockGoatGoldBrick.tryWitherGoatGoldBrickArea(world, this.getPosition(), true);
			NBTTagCompound nbt = FireworkEffect.fastNBT(10, 2, 0.1f, new int[] { 0xf8d302, 0xfff03d }, new int[] {});
			Effects.spawnEffect(world, Effects.FIREWROK, this.getPositionVector().add(0, this.height / 2, 0), nbt);
			this.setDead();
			return;
		}

		if (this.ticksExisted % (20 * 3) != 0) return;

		zombieCache = null;
		List<EntityPlayer> list = getPlayerInMaze(false);

		for (EntityPlayer player : list) {
			UUID playerUUID = player.getUniqueID();
			PlayerInMazeStatus status = playerInMaze.get(playerUUID);
			boolean isFirst = status == null;
			if (status == null) status = new PlayerInMazeStatus();
			playerInMaze.put(playerUUID, status);
			status.overTimes = 20 * 2;

			if (isFirst) {
				player.sendMessage(createSheepSay("say.arrogantSheep.welcome"));
				player.sendMessage(createSheepSay("say.arrogantSheep.explain"));
				continue;
			}
			status.isTowFloor = (this.posY - player.posY) <= 6;
			if (!status.isTowFloor) status.findTimes++;
			interactWithPlayer(player, status);
			if (status.isTowFloor) status.isFristInTwoFloor = false;
		}

		Iterator<Entry<UUID, PlayerInMazeStatus>> iter = playerInMaze.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<UUID, PlayerInMazeStatus> entry = iter.next();
			if (--entry.getValue().overTimes <= 0) iter.remove();
		}

	}

	protected ITextComponent createSheepSay(String str) {
		String s = EntityList.getEntityString(this);
		return new TextComponentTranslation("entity." + s + ".name").setStyle(new Style().setColor(TextFormatting.GOLD))
				.appendText(": ").appendSibling(new TextComponentTranslation(str));
	}

	@Override
	public void setFire(int seconds) {

	}

	public List<EntityPlayer> getPlayerInMaze(boolean around) {

		if (around) {
			BlockPos pos = this.getPosition();
			AxisAlignedBB aabb = new AxisAlignedBB(pos.getX() - 8 + 0.5, pos.getY() + 8, pos.getZ() - 8 + 0.5,
					pos.getX() + 8 + 0.5, pos.getY() - 8, pos.getZ() + 8 + 0.5);
			return world.getPlayers(EntityPlayer.class, (player) -> {
				return aabb.intersects(player.getEntityBoundingBox());
			});
		}

		BlockPos pos = getStandPosition();
		if (pos.getX() + pos.getY() + pos.getZ() == 0) {
			this.setStandPosition(this.getPosition());
			return new ArrayList<>();
		}

		List<EntityPlayer> list = Lists.<EntityPlayer>newArrayList();
		float x = pos.getX() + 0.5f;
		float z = pos.getZ() + 0.5f;

		AxisAlignedBB aabb = new AxisAlignedBB(x - 16, pos.getY() - 6, z - 16, x + 16, pos.getY() - 9, z + 16);
		AxisAlignedBB aabb2 = new AxisAlignedBB(x - 13, pos.getY() - 3, z - 13, x + 13, pos.getY() - 6, z + 13);
		AxisAlignedBB aabb3 = new AxisAlignedBB(x - 12, pos.getY() - 2, z - 12, x + 12, pos.getY() - 3, z + 12);
		AxisAlignedBB aabb4 = new AxisAlignedBB(x - 11, pos.getY() - 1, z - 11, x + 11, pos.getY() - 2, z + 11);
		AxisAlignedBB aabb5 = new AxisAlignedBB(x - 10, pos.getY() - 0, z - 10, x + 10, pos.getY() - 1, z + 10);
		AxisAlignedBB aabb6 = new AxisAlignedBB(x - 9, pos.getY() + 1, z - 9, x + 9, pos.getY() - 0, z + 9);
		AxisAlignedBB aabb7 = new AxisAlignedBB(x - 8, pos.getY() + 2, z - 8, x + 8, pos.getY() + 1, z + 8);

		for (EntityPlayer entity4 : world.playerEntities) {
			AxisAlignedBB playerBox = entity4.getEntityBoundingBox();
			if (aabb.intersects(playerBox)) list.add(entity4);
			else if (aabb2.intersects(playerBox)) list.add(entity4);
			else if (aabb3.intersects(playerBox)) list.add(entity4);
			else if (aabb4.intersects(playerBox)) list.add(entity4);
			else if (aabb5.intersects(playerBox)) list.add(entity4);
			else if (aabb6.intersects(playerBox)) list.add(entity4);
			else if (aabb7.intersects(playerBox)) list.add(entity4);
		}

		return list;
	}

	protected void interactWithPlayer(EntityPlayer player, PlayerInMazeStatus status) {
		if (!status.isTowFloor) {
			// 3 min
			if (status.findTimes == (20 * 3)) player.sendMessage(createSheepSay("say.arrogantSheep.longFind"));
			if (status.findTimes >= (20 * 3) && status.findTimes % 5 == 0 && rand.nextDouble() < 0.5) {
				BlockPos pos = player.getPosition().offset(player.getHorizontalFacing());
				if (BlockHelper.isPassableBlock(world, pos)) {
					int p = (status.findTimes - (20 * 3)) / 20;
					summonZombie(pos, player, p);
					player.sendMessage(createSheepSay("say.arrogantSheep.summonZombie"));
				}
			}
			return;
		}

		if (status.isFristInTwoFloor) {
			player.sendMessage(createSheepSay("say.arrogantSheep.toHere"));
		}

		if (summonZombieInTwoFloor(player)) player.sendMessage(createSheepSay("say.arrogantSheep.summonZombie"));

	}

	protected EntityZombie summonZombie(BlockPos pos, EntityPlayer player, int power) {
		EntityZombie zombie = new EntityZombie(world);
		zombie.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

		power = MathHelper.clamp(power, 0, 8);

		ItemStack helmet = new ItemStack(Items.GOLDEN_HELMET);
		zombie.setItemStackToSlot(EntityEquipmentSlot.HEAD, helmet);
		if (power > 2) helmet.addEnchantment(Enchantments.PROTECTION, power - 4);

		ItemStack chestplate = new ItemStack(Items.GOLDEN_CHESTPLATE);
		zombie.setItemStackToSlot(EntityEquipmentSlot.CHEST, chestplate);
		if (power > 3) chestplate.addEnchantment(Enchantments.PROTECTION, power - 3);

		ItemStack leggings = new ItemStack(Items.GOLDEN_LEGGINGS);
		zombie.setItemStackToSlot(EntityEquipmentSlot.LEGS, leggings);
		if (power > 4) leggings.addEnchantment(Enchantments.PROTECTION, power - 4);

		ItemStack boots = new ItemStack(Items.GOLDEN_BOOTS);
		zombie.setItemStackToSlot(EntityEquipmentSlot.FEET, boots);
		if (power > 5) boots.addEnchantment(Enchantments.PROTECTION, power - 5);

		if (power > 2) {
			ItemStack sword = new ItemStack(Items.GOLDEN_SWORD);
			zombie.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, sword);
			if (power > 3) boots.addEnchantment(Enchantments.SHARPNESS, power - 3);
			if (power > 4) boots.addEnchantment(Enchantments.KNOCKBACK, power - 5);
		}

		if (power > 5) zombie.addPotionEffect(new PotionEffect(MobEffects.SPEED, 20 * 60 * 30, Math.min(power - 6, 3)));

		for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) zombie.setDropChance(slot, 0);

		world.spawnEntity(zombie);
		zombie.setAttackTarget(player);
		Effects.spawnSummonEntity(zombie, new int[] { 0x101313, 0xf6c905 });
		return zombie;
	}

	protected boolean summonZombieInTwoFloor(EntityPlayer player) {
		BlockPos pos = getStandPosition();
		if (zombieCache == null) {
			float x = pos.getX() + 0.5f;
			float z = pos.getZ() + 0.5f;
			AxisAlignedBB aabb = new AxisAlignedBB(x - 13, pos.getY() - 3, z - 13, x + 13, pos.getY() - 6, z + 13);
			zombieCache = world.getEntitiesWithinAABB(EntityMob.class, aabb);
		}

		int maxCount = Math.min(this.shareTims * 2 + 6, 16);
		if (zombieCache.size() > maxCount) return false;

		BlockPos at = pos.add(rand.nextGaussian() * 13, -5, rand.nextGaussian() * 13);
		if (!BlockHelper.isReplaceBlock(world, at)) at = at.up();
		if (!BlockHelper.isReplaceBlock(world, at)) at = at.up();
		EntityZombie zombie = summonZombie(at, player, (int) Math.round(this.shareTims * 1.5) - 1);
		zombieCache.add(zombie);

		return true;
	}

	@Override
	public void applyEntityCollision(Entity entityIn) {
		super.applyEntityCollision(entityIn);
		if (getSheared()) return;
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
	}

	@Override
	protected void collideWithEntity(Entity entityIn) {
		super.collideWithEntity(entityIn);
		if (getSheared()) return;
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;

		entityIn.motionX *= 1.2;
		entityIn.motionY += 0.2f;
		entityIn.motionZ *= 1.2;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (DamageHelper.isRuleDamage(source) || getSheared()) return super.attackEntityFrom(source, amount);
		Entity entity = source.getTrueSource();
		if (entity instanceof EntityPlayer) {
			if (rand.nextDouble() < 0.2) entity.sendMessage(createSheepSay("say.arrogantSheep.uselessAttack"));
		}
		return false;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1048576D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(IS_SHEARED, Boolean.FALSE);
	}

	public float getEyeHeight() {
		return 0.95F * this.height;
	}

	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);
	}

	/** 获取出生时站的中心点 */
	public BlockPos getStandPosition() {
		return getHomePosition();
	}

	public void setStandPosition(BlockPos pos) {
		setHomePosAndDistance(pos, 16);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setBoolean("Sheared", this.getSheared());
		NBTHelper.setBlockPos(compound, "StandPos", getStandPosition());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		this.setSheared(compound.getBoolean("Sheared"));
		this.setStandPosition(NBTHelper.getBlockPos(compound, "StandPos"));
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_SHEEP_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_SHEEP_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SHEEP_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {
		this.playSound(SoundEvents.ENTITY_SHEEP_STEP, 0.15F, 1.0F);
	}

	public boolean getSheared() {
		return dataManager.get(IS_SHEARED);
	}

	public void setSheared(boolean sheared) {
		dataManager.set(IS_SHEARED, sheared);
	}

	@Override
	protected boolean processInteract(EntityPlayer player, EnumHand hand) {
		if (this.getSheared()) return false;
		ItemStack stack = player.getHeldItem(hand);
		if (stack.isEmpty()) return false;
		Item item = stack.getItem();

		if (item != Items.SHEARS) return false;

		this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);

		if (shareTims++ < 4) {
			this.emitting(player);
			return false;
		}

		double pro = 1.4f - 1f / ((shareTims - 3) * 0.75f);
		if (rand.nextDouble() > pro) {
			this.emitting(player);
			return false;
		}

		if (world.isRemote) return true;

		this.setSheared(true);
		this.setRevengeTarget(player);

		dyingSay();

		float luck = player.getLuck();

		ItemStack wool = new ItemStack(ESObjects.ITEMS.ARROGANT_WOOL,
				1 + MathHelper.floor(rand.nextFloat() * luck / 2));
		ItemHelper.dropItem(world, this.getPosition(), wool);

		return true;
	}

	@Override
	protected Item getDropItem() {
		return Items.MUTTON;
	}

	protected void emitting(EntityLivingBase player) {
		Vec3d thisVec = new Vec3d(posX, posY + height, posZ);
		Vec3d tar = player.getPositionEyes(0).subtract(thisVec).normalize();
		Vec3d speed = new Vec3d(tar.x, 0, tar.z).normalize().scale(1.5);
		player.motionX += speed.x;
		player.motionY += 0.5;
		player.motionZ += speed.z;

		for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
			ItemStack stack = player.getItemStackFromSlot(slot);
			if (!stack.isEmpty()) {
				player.setItemStackToSlot(slot, ItemStack.EMPTY);
				if (!world.isRemote) player.entityDropItem(stack, player.getEyeHeight());
			}
		}

		if (world.isRemote) {
			double dis = player.getDistance(this);
			showLineEffect(thisVec, tar, dis);
			player.sendMessage(createSheepSay("say.arrogantSheep.stayAway"));
		} else SilentWorld.shutup(player, 30);
	}

	@SideOnly(Side.CLIENT)
	private void showLineEffect(Vec3d thisVec, Vec3d tar, double dis) {
		for (int i = 0; i < 16; i++) {
			Vec3d at = thisVec.add(tar.scale(dis / 16 * i));
			EffectElementMove effect = new EffectElementMove(world, at);
			effect.setColor(0xf6c905);
			effect.setVelocity(rand.nextGaussian() * 0.01, rand.nextGaussian() * 0.01, rand.nextGaussian() * 0.01);
			Effect.addEffect(effect);
		}
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}
}
