package yuzunyannn.elementalsorcery.entity.mob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class EntityArrogantSheep extends EntityMob {

	protected static final DataParameter<Boolean> IS_SHEARED = EntityDataManager
			.<Boolean>createKey(EntityArrogantSheep.class, DataSerializers.BOOLEAN);

	// 描述迷宫内玩家的状态
	public static class PlayerInMazeStatus {
		boolean isTowFloor;
		boolean isFristInTwoFloor = true;
		int findTimes = 0;
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

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (world.isRemote) return;

		if (getSheared()) {
			EntityLivingBase revenge = getRevengeTarget();
			setRevengeTarget(revenge);
			if (this.ticksExisted % 20 == 0) {
				setHealth(getHealth() - 64);
				if (rand.nextFloat() < 0.25f) {
					List<EntityPlayer> list = getPlayerInMaze(true);
					ITextComponent text = createSheepSay("say.arrogantSheep.myFur");
					for (EntityPlayer player : list) player.sendMessage(text);
				}
			}
			return;
		}

		if (this.ticksExisted % (20 * 3) != 0) return;

		Map<UUID, PlayerInMazeStatus> lastPlayerInMaze = playerInMaze;
		playerInMaze = new HashMap<>();
		zombieCache = null;
		List<EntityPlayer> list = getPlayerInMaze(false);

		for (EntityPlayer player : list) {
			UUID playerUUID = player.getUniqueID();
			PlayerInMazeStatus status = lastPlayerInMaze.get(playerUUID);
			boolean isFirst = status == null;
			if (status == null) status = new PlayerInMazeStatus();
			playerInMaze.put(playerUUID, status);

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

	}

	protected ITextComponent createSheepSay(String str) {
		return new TextComponentString(this.getName()).setStyle(new Style().setColor(TextFormatting.GOLD))
				.appendText(": ").appendSibling(new TextComponentTranslation(str));
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
					summonZombie(pos, player);
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

	protected EntityZombie summonZombie(BlockPos pos, EntityPlayer player) {
		EntityZombie zombie = new EntityZombie(world);
		zombie.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		zombie.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
		zombie.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
		zombie.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS));
		zombie.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Items.GOLDEN_BOOTS));
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

		if (zombieCache.size() > 12) return false;

		BlockPos at = pos.add(rand.nextGaussian() * 13, -5, rand.nextGaussian() * 13);
		EntityZombie zombie = summonZombie(at, player);
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
		if (source.canHarmInCreative() || getSheared()) return super.attackEntityFrom(source, amount);
		Entity entity = source.getTrueSource();
		if (entity instanceof EntityPlayer) {
			if (rand.nextDouble() < 0.2) entity.sendMessage(createSheepSay("say.arrogantSheep.uselessAttack"));
		}
		return false;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1024D);
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

		if (player.getDistance(this) > 2) return false;

		this.setSheared(true);
		this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);
		this.setRevengeTarget(player);

		return true;
	}
}
