package yuzunyannn.elementalsorcery.summon;

import java.lang.reflect.Constructor;
import java.util.Random;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.elf.ElfTime;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.util.EntityHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class SummonMob extends SummonCommon {

	protected Class<? extends Entity> creatureClass;
	protected int mobCount;

	protected UUID attackTargetUUID;
	protected EntityLivingBase attackTarget;
	protected boolean isChild = false;

	public SummonMob(World world, BlockPos pos) {
		this(world, pos, 0x192b13, EntityZombie.class, 1);
	}

	public SummonMob(World world, BlockPos pos, int color, Class<? extends Entity> type, int count) {
		super(world, pos, color);
		this.creatureClass = type;
		this.mobCount = count;
	}

	public SummonMob setAttackTargetUUID(UUID attackTargetUUID) {
		this.attackTargetUUID = attackTargetUUID;
		return this;
	}

	public SummonMob setChild(boolean isChild) {
		this.isChild = isChild;
		return this;
	}

	@Override
	public void initData() {
		this.size = 3;
		this.height = 3;
	}

	@Override
	public boolean update() {
		if (tick++ % 2 != 0) return true;
		if (mobCount <= 0) return tick < 40;

		if (world.isRemote) return true;

		Vec3d pos = new Vec3d(this.pos).addVector(0.5, 0.1, 0.5);
		Random rand = world.rand;
		EntityCreature entity = this.createMob();

		final float size = 2;
		pos = pos.addVector(rand.nextDouble() * size * 2 - size, 0, rand.nextDouble() * size * 2 - size);
		entity.setPosition(pos.x, pos.y, pos.z);
		world.spawnEntity(entity);

		if (attackTargetUUID != null) {
			attackTarget = WorldHelper.restoreLiving(world, attackTargetUUID);
			attackTargetUUID = null;
		}

		if (attackTarget != null) {
			boolean isCreative = EntityHelper.isCreative(attackTarget);
			if (!isCreative) entity.setAttackTarget(attackTarget);
		}

		Effects.spawnSummonEntity(entity, null);

		mobCount--;
		return mobCount > 0 ? true : tick < 40;
	}

	@SideOnly(Side.CLIENT)
	public void addSummonEffect(Entity entity) {
//		EffectSummonEntity
	}

	public EntityCreature createMob() {
		EntityCreature entity;
		try {
			Constructor<EntityCreature> constructor = (Constructor<EntityCreature>) creatureClass
					.getConstructor(World.class);
			entity = constructor.newInstance(world);
		} catch (Exception e) {
			ElementalSorcery.logger.warn("召唤仪式mob反射异常", e);
			entity = new EntityZombie(world);
		}
		if (isChild) {
			if (entity instanceof EntityAgeable) ((EntityAgeable) entity).setGrowingAge(-24000);
			else if (entity instanceof EntityZombie) ((EntityZombie) entity).setChild(true);
		}
		ElfTime time = new ElfTime(world);
		if (!time.at(ElfTime.Period.NIGHT)) {
			if (entity instanceof EntityZombie || entity instanceof EntitySkeleton) {
				if (!entity.isChild())
					entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
			}
		}
		return entity;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setByte("count", (byte) this.mobCount);
		if (attackTargetUUID != null) nbt.setUniqueId("target", attackTargetUUID);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.mobCount = nbt.getInteger("count");
		attackTargetUUID = nbt.getUniqueId("target");
	}

}
