package yuzunyannn.elementalsorcery.entity.mob;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraSummon;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.init.LootRegister;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.effect.scrappy.FireworkEffect;
import yuzunyannn.elementalsorcery.summon.recipe.SummonRecipe;
import yuzunyannn.elementalsorcery.summon.recipe.SummonRecipeMob;
import yuzunyannn.elementalsorcery.util.TextHelper;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.DamageHelper;

public class EntityDreadCube extends EntityMob {

	public static final DataParameter<Boolean> IS_ACTIVE = EntityDataManager.<Boolean>createKey(EntityDreadCube.class,
			DataSerializers.BOOLEAN);

	public float activeRate = 0;
	public float prevActiveRate = 0;

	protected float soul = 0;
	protected int nextActiveTick = 0;
	protected int activeTick = 0;

	public EntityDreadCube(World worldIn) {
		super(worldIn);
		this.setSize(1, 1);
		this.setActive(false);
	}

	@Override
	public float getEyeHeight() {
		return this.height * 0.5f;
	}

	@Override
	protected void initEntityAI() {
		ObfuscationReflectionHelper.setPrivateValue(EntityLiving.class, this,
				new EntityAITasks(world != null && world.profiler != null ? world.profiler : null) {
					@Override
					public void onUpdateTasks() {
						if (!isActive()) return;
						super.onUpdateTasks();
					}
				}, "field_70714_bg");
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(16D);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(IS_ACTIVE, false);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootRegister.DREAD_CUBE;
	}

	public boolean isActive() {
		return this.dataManager.get(IS_ACTIVE).booleanValue();
	}

	public void setActive(boolean active) {
		this.dataManager.set(IS_ACTIVE, active);
		this.setNoGravity(active);
		if (!active) activeTick = 0;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		if (this.isActive()) compound.setBoolean("isActive", true);
		else compound.setInteger("nextActiveTick", nextActiveTick);
		compound.setFloat("soul", soul);
		compound.setInteger("activeTick", activeTick);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		this.setActive(compound.getBoolean("isActive"));
		nextActiveTick = compound.getInteger("nextActiveTick");
		soul = compound.getFloat("soul");
		activeTick = compound.getInteger("activeTick");
	}

	protected void inLevelHeal() {
		int size = 3;
		int times = 0;
		BlockPos pos = this.getPosition();
		for (int x = -size; x <= size; x++) {
			for (int y = -size; y <= size; y++) {
				for (int z = -size; z <= size; z++) {
					BlockPos at = pos.add(x, y, z);
					IBlockState state = world.getBlockState(at);
					if (state.getBlock() == Blocks.LAVA || state.getBlock() == Blocks.FLOWING_LAVA) {
						world.setBlockToAir(at);
						times++;
					}
				}
			}
		}
		if (times > 0) world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F,
				2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
		this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 40 * times, 1));
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		this.prevActiveRate = this.activeRate;
		if (this.isActive()) {
			this.activeRate = Math.min(this.activeRate + 0.05f, 1);
			if (this.activeRate < 1) this.motionY = MathHelper.cos(this.activeRate * 3.1415f / 2) * 0.1f;
			this.motionX = this.motionZ = 0;

			if (world.isRemote) return;

			if (this.isRiding()) this.dismountRidingEntity();

			activeTick = activeTick + 1;
			if (this.isInLava()) this.inLevelHeal();
			if (this.activeTick % 100 == 0) {
				soul = soul + 0.05f;
				this.destroyTheEnvironment();
			}
			if (this.activeTick % 120 == 0) {
				int soulCount = MathHelper.floor(soul);
				if (soulCount > 0) {
					int dropCount = rand.nextInt(soulCount) + 1;
					this.dropItem(ESInit.ITEMS.SOUL_FRAGMENT, dropCount);
					soul -= dropCount;
				}
			}
		} else {
			this.activeRate = Math.max(this.activeRate - 0.05f, 0);
//			if (this.motionY < -0.05) this.motionY = -0.05f;
			this.motionX *= 0.5;
			this.motionZ *= 0.5;
			if (world.isRemote) return;
			if (nextActiveTick-- <= 0) this.setActive(true);
		}
		this.extinguish();
	}

	protected void destroyTheEnvironment() {
		int changeCount = 0;
		int size = Math.min(6, activeTick / 2400 + 2);
		int unluckySize = Math.min(16, activeTick / 1200 + 1);
		BlockPos unlucky = this.getPosition().add(rand.nextGaussian() * unluckySize,
				rand.nextGaussian() * unluckySize * 2 - unluckySize, rand.nextGaussian() * unluckySize);
		for (int x = -size; x <= size; x++) {
			for (int y = -size; y <= size; y++) {
				for (int z = -size; z <= size; z++) {
					BlockPos at = unlucky.add(x, y, z);
					if (rand.nextFloat() < 0.1f) {
						if (this.changeAt(at)) changeCount++;
					}
				}
			}
		}
		if (changeCount > 0) {
			Effects.spawnElementAbsorb(new Vec3d(unlucky).add(0.5, 0.5, 0.5), this, Math.min(32, changeCount * 2),
					new int[] { 0x101313, 0x570000, 0x6b0e0e, 0xb43232 });
		}
	}

	public boolean changeAt(BlockPos pos) {
		if (pos.getY() < 0) return false;
		if (world.isAirBlock(pos)) return false;

		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block == Blocks.GRAVEL) return false;
		if (block == Blocks.STONE) return false;
		if (block == Blocks.COBBLESTONE) return false;
		if (block == Blocks.COBBLESTONE_WALL) return false;
		if (block == Blocks.SAND) return false;
		if (block == Blocks.SANDSTONE) return false;
		if (block == Blocks.SANDSTONE_STAIRS) return false;
		if (BlockHelper.isBedrock(world, pos)) return false;

		if (world.getTileEntity(pos) != null) {
			world.destroyBlock(pos, true);
			return true;
		}
		if (state.getBlock() instanceof BlockLiquid) {
			world.setBlockState(pos, Blocks.GRAVEL.getDefaultState());
			world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 0.75f,
					(float) world.rand.nextGaussian() * 0.8F);
			soul += 0.05f;
			return true;
		}
		if (state.getBlock().isReplaceable(world, pos)) {
			soul += 0.15f;
			world.destroyBlock(pos, false);
			return true;
		}
		if (state.getBlockHardness(world, pos) > 50) return false;

		soul += 0.1f;
		world.setBlockState(pos, Blocks.GRAVEL.getDefaultState());
		world.playEvent(2001, pos, Block.getStateId(state));

		return true;
	}

	@Override
	protected void collideWithEntity(Entity entityIn) {
		super.collideWithEntity(entityIn);
		if (!this.isActive()) return;

		if (entityIn instanceof EntityLivingBase) {
			soul += 0.01f;
			entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 1);
			EntityLivingBase living = ((EntityLivingBase) entityIn);
			living.addPotionEffect(new PotionEffect(MobEffects.WITHER, 40, 1));
			living.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 60, 1));
			living.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 80, 1));
		}

	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (DamageHelper.isRuleDamage(source)) return super.attackEntityFrom(source, amount);
		if (!this.isActive()) return false;
		if (this.activeRate < 1) return false;
		Entity entity = source.getTrueSource();
		if (!(entity instanceof EntityLivingBase)) return false;
		if (!"player".equals(source.damageType)) return false;

		nextActiveTick = 20 * (10 + rand.nextInt(30));
		this.setActive(false);

		if (world.isRemote) return false;

		float hp = this.getHealth();
		EntityLivingBase living = ((EntityLivingBase) entity);

		// 创造模式下，shift攻击 直接击杀
		if (living instanceof EntityPlayer) {
			if (((EntityPlayer) living).isCreative() && living.isSneaking()) {
				return super.attackEntityFrom(source, 9999);
			}
		}

		if (!this.summonMonster(living)) return false;

		if (hp < 6) living.addPotionEffect(new PotionEffect(MobEffects.WITHER, (int) (60 * (6 - hp)), 2));
		if (hp < 12) living.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, (int) (40 * (12 - hp)), 1));
		if (hp < 16) living.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, (int) (20 * (16 - hp)),
				MathHelper.clamp((int) ((16 - hp) / 4), 1, 3)));

		NBTTagCompound nbt = FireworkEffect.fastNBT(0, 1, 0.1f, new int[] { 0x101313, 0x570000, 0x6b0e0e, 0xb43232 },
				new int[] { 0xb43232 });
		Effects.spawnEffect(world, Effects.FIREWROK, this.getPositionVector().add(0, 0.5, 0), nbt);

		return super.attackEntityFrom(source, 1);
	}

	public boolean summonMonster(EntityLivingBase target) {
		if (world.isRemote) return true;

		float hp = this.getHealth();
		int point = 16 - (int) hp + 1;
		point = point * point / 5;
		if (point <= 0) return true;

		if (!doSummonMonster(target, point)) return false;

		world.playSound(null, this.getPosition(), SoundEvents.E_PARROT_IM_WITHER, SoundCategory.AMBIENT, 0.5F,
				2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
		return true;
	}

	private static class MonsterRecord {
		public Class<? extends EntityLiving> clazz;
		public int times;
		public int count;
		public boolean isChild;
		public int cost;
		public BlockPos[] spawnPos;

		public static MonsterRecord create(int cost, Class<? extends EntityLiving> clazz, int times, int count,
				boolean isChild) {
			MonsterRecord r = new MonsterRecord();
			r.cost = cost;
			r.clazz = clazz;
			r.times = times;
			r.count = count;
			r.isChild = isChild;
			r.spawnPos = new BlockPos[times];
			return r;
		}
	}

	protected boolean doSummonMonster(EntityLivingBase target, int point) {
		int split = rand.nextInt(point);
		int point1 = split;
		int point2 = point - split;

		List<MonsterRecord> records = new LinkedList<>();
		collectSummonMonsters(point1, records);
		collectSummonMonsters(point2, records);

		// 必须全部找到
		for (MonsterRecord rec : records) {
			for (int i = 0; i < rec.times; i++) {
				rec.spawnPos[i] = this.findPlace();
				if (rec.spawnPos[i] == null) return false;
			}
		}

		for (MonsterRecord rec : records) {
			ItemStack keepsake = SummonRecipeMob.createKeepsake(rec.clazz, rec.count, target, rec.isChild);
			for (int i = 0; i < rec.times; i++) {
				MantraSummon.summon(world, rec.spawnPos[i], this, keepsake,
						SummonRecipe.get(TextHelper.toESResourceLocation("mob_create")));
			}
		}

		return true;
	}

	protected void collectSummonMonsters(int point, List<MonsterRecord> records) {
		while (point > 0) {
			MonsterRecord rec = createSummonMonster(point);
			records.add(rec);
			point -= rec.cost;
		}
	}

	protected MonsterRecord createSummonMonster(int point) {
		if (point >= 40) return MonsterRecord.create(40, EntityWither.class, 1, 1, false);
		else if (point >= 26) return MonsterRecord.create(26, EntityRabidRabbit.class, 6, 2, false);
		else if (point >= 20) return MonsterRecord.create(20, EntityCreeper.class, 3, 2, false);
		else if (point >= 16) return MonsterRecord.create(16, EntityEnderman.class, 4, 2, true);
		else if (point >= 13) return MonsterRecord.create(13, EntityZombie.class, 2, 3, true);
		else if (point >= 10) return MonsterRecord.create(10, EntityCaveSpider.class, 2, 2, false);
		else if (point >= 8) return MonsterRecord.create(8, EntityCreeper.class, 1, 1, false);
		else if (point >= 5) return MonsterRecord.create(5, EntityEnderman.class, 1, 1, false);
		else if (point >= 4) return MonsterRecord.create(4, EntitySilverfish.class, 1, 4, true);
		else if (point >= 3) return MonsterRecord.create(3, EntityZombie.class, 1, 1, true);
		else if (point >= 2) return MonsterRecord.create(2, EntitySkeleton.class, 1, 1, false);
		else return MonsterRecord.create(1, EntityZombie.class, 1, 1, false);
	}

	protected BlockPos findPlace() {
		BlockPos pos = null;
		float theta = rand.nextFloat() * 3.1415926f * 2;
		Vec3d vec = this.getPositionVector();
		for (int tryTimes = 0; tryTimes < 6; tryTimes++) {
			float r = (6 + rand.nextInt(4));
			double x = vec.x + MathHelper.sin(theta) * r;
			double z = vec.z + MathHelper.cos(theta) * r;
			pos = new BlockPos(x, vec.y + 8, z);
			for (int k = 0; k < 16 && pos.getY() > 0; k++, pos = pos.down()) if (!world.isAirBlock(pos)) break;
			if (!world.isAirBlock(pos)) {
				IBlockState state = world.getBlockState(pos);
				if (state.getBlock().isReplaceable(world, pos)) {
					if (world.isAirBlock(pos.up())) return pos;
				} else {
					pos = pos.up();
					if (world.isAirBlock(pos) && world.isAirBlock(pos.up())) return pos;
				}
			}
		}
		return null;
	}

	@Override
	protected void despawnEntity() {

	}

	@Override
	public boolean isNonBoss() {
		return true;
	}
}
