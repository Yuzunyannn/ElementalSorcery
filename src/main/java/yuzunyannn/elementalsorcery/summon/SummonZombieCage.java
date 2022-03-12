package yuzunyannn.elementalsorcery.summon;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.elf.ElfTime;

public class SummonZombieCage extends SummonCommon {

	public static final int MAX_SIZE = 8;

	protected Class<? extends Entity> creatureClass;
	protected int zombie;
	protected int offsetX, offsetZ;

	public SummonZombieCage(World world, BlockPos pos) {
		this(world, pos, 0x192b13, EntityZombie.class);

	}

	public SummonZombieCage(World world, BlockPos pos, int color, Class<? extends Entity> type) {
		super(world, pos, color);
		this.creatureClass = type;
		this.zombie = world.rand.nextInt(16) + 8;
		this.offsetX = -MAX_SIZE;
		this.offsetZ = -MAX_SIZE;
	}

	@Override
	public void initData() {
		this.size = 4;
		this.height = 4;
	}

	@Override
	public boolean update() {
		if (tick++ % 2 != 0) return true;
		if (zombie <= 0) return false;
		if (updateOffset()) updateOffset();
		else genZombie();

		Vec3d center = new Vec3d(this.pos).add(0.5, 0.5, 0.5);
		final int size = MAX_SIZE;
		AxisAlignedBB aabb = new AxisAlignedBB(pos.getX() - size, pos.getY() - 1, pos.getZ() - size, pos.getX() + size,
				pos.getY() + 1, pos.getZ() + size);
		List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
		for (EntityLivingBase entity : entities) {
			if (entity instanceof IMob) continue;
			Vec3d tar = center.subtract(entity.getPositionVector()).normalize().scale(0.125);
			entity.addVelocity(tar.x, tar.y, tar.z);
		}

		return this.zombie > 0;
	}

	public boolean updateOffset() {
		if (offsetX <= MAX_SIZE) {
			if (Math.abs(offsetX) != MAX_SIZE && Math.abs(offsetZ) != MAX_SIZE) {
				offsetX++;
				return updateOffset();
			}
			genBlock();
			offsetX++;
		} else {
			if (offsetZ < MAX_SIZE) {
				offsetZ++;
				offsetX = -MAX_SIZE;
			} else return false;
		}
		return true;
	}

	public void genBlock() {
		if (world.isRemote) return;
		BlockPos pos = this.pos.add(offsetX, 5, offsetZ);
		for (int i = 0; i <= 10; i++) {
			pos = pos.down();
			if (world.isAirBlock(pos)) continue;
			break;
		}
		IBlockState state = Blocks.COBBLESTONE.getDefaultState();
		if (world.provider.getDimensionType() == DimensionType.NETHER) state = Blocks.NETHERRACK.getDefaultState();

		for (int i = 1; i <= 2; i++) this.genBlock(pos.up(i), state);
	}

	public void genZombie() {
		if (world.isRemote) return;
		Vec3d pos = new Vec3d(this.pos).add(0.5, 0.1, 0.5);
		Random rand = world.rand;
		EntityCreature entity = this.createZombie();
		final int size = MAX_SIZE - 1;
		entity.setPosition(pos.x, pos.y, pos.z);
		world.spawnEntity(entity);
		pos = pos.add(rand.nextDouble() * size * 2 - size, 0, rand.nextDouble() * size * 2 - size);
		Vec3d tar = pos.subtract(entity.getPositionVector()).normalize().scale(0.5 + rand.nextDouble() * 0.5);
		entity.addVelocity(tar.x, 0, tar.z);
		this.zombie--;
	}

	public EntityCreature createZombie() {
		EntityCreature entity;
		try {
			Constructor<EntityCreature> constructor = (Constructor<EntityCreature>) creatureClass
					.getConstructor(World.class);
			entity = constructor.newInstance(world);
		} catch (Exception e) {
			ElementalSorcery.logger.warn("召唤仪式僵尸牢笼反射异常", e);
			entity = new EntityZombie(world);
		}
		ElfTime time = new ElfTime(world);
		if (!time.at(ElfTime.Period.NIGHT)) {
			if (entity instanceof EntityZombie) ((EntityZombie) entity).setChild(true);
			else if (entity instanceof EntitySkeleton) {
				entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
			}
		}
		return entity;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setByte("zombie", (byte) this.zombie);
		nbt.setInteger("x", offsetX);
		nbt.setInteger("z", offsetZ);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.zombie = nbt.getInteger("zombie");
		offsetX = nbt.getInteger("x");
		offsetZ = nbt.getInteger("z");
	}

}
