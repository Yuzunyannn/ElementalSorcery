package yuzunyannn.elementalsorcery.potion;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.event.EventServer;
import yuzunyannn.elementalsorcery.event.IWorldTickTask;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraEnderTeleport;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;

public class PotionEnderization extends PotionCommon {

	public PotionEnderization() {
		super(false, 0xaf08dd, "enderization");
		iconIndex = 0;
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public void performEffect(EntityLivingBase owner, int amplifier) {
		World world = owner.world;

		if (world.isRemote) {
			if (owner.ticksExisted % 5 != 0) return;
			Random rand = owner.getRNG();
			for (int i = 0; i < 2; ++i) {
				world.spawnParticle(EnumParticleTypes.PORTAL, owner.posX + (rand.nextDouble() - 0.5D) * owner.width,
						owner.posY + rand.nextDouble() * owner.height - 0.25D,
						owner.posZ + (rand.nextDouble() - 0.5D) * owner.width, (rand.nextDouble() - 0.5D) * 2.0D,
						-rand.nextDouble(), (rand.nextDouble() - 0.5D) * 2.0D);
			}
			return;
		}

		if (owner.isWet() && owner.ticksExisted % 10 == 0) {
			owner.attackEntityFrom(DamageSource.DROWN, 1.0F);
			return;
		}

		if (owner.ticksExisted % 20 == 0) {
			float distance = Math.min(16 * (amplifier + 1), 64);
			PotionEnderization.tryTeleportToTarget(world, owner, distance);
		}
	}

	public static boolean tryAttackEntityFrom(EntityLivingBase owner, DamageSource source, float amount) {
		if (!owner.isPotionActive(ESObjects.POTIONS.ENDERIZATION)) return false;
		if (EntityHelper.isCreative(owner)) return false;
		if (source instanceof EntityDamageSourceIndirect) {
			for (int i = 0; i < 64; ++i) if (teleportRandomly(owner)) return true;
			return false;
		} else {
			if (source.isUnblockable() && owner.getRNG().nextInt(10) != 0) teleportRandomly(owner);
			return false;
		}
	}

	public static boolean tryTeleportToTarget(World world, EntityLivingBase owner, float distance) {
		Vec3d look = owner.getLook(1.0F);
		AxisAlignedBB aabb = owner.getEntityBoundingBox()
				.expand(look.x * distance, look.y * distance, look.z * distance).grow(1.0D, 1.0D, 1.0D);
		List<Entity> list = world.getEntitiesInAABBexcluding(owner, aabb, check -> {
			return check instanceof EntityLivingBase && shouldAttack(owner, (EntityLivingBase) check);
		});
		if (list.isEmpty()) return false;
		EntityLivingBase target = (EntityLivingBase) list.get(0);

		look = new Vec3d(look.x, 0, look.z).normalize();
		if (look.lengthSquared() < 0.5f) return false;

		Vec3d vec = target.getPositionVector().add(look.scale(1.5));
		BlockPos pos = new BlockPos(vec);
		if (BlockHelper.isPassableBlock(world, pos) && BlockHelper.isPassableBlock(world, pos.up())) {
			if (world.isRemote) return true;
			IWorldTickTask.IWorldTickTaskOnce task = w -> {
				owner.rotationYaw = owner.rotationYaw + 180;
				MantraEnderTeleport.doEnderTeleport(world, owner, vec);
				owner.addPotionEffect(new PotionEffect(MobEffects.SPEED, 20 * 10, 1));
				world.playSound((EntityPlayer) null, vec.x, vec.y, vec.z, SoundEvents.ENTITY_ENDERMEN_TELEPORT,
						SoundCategory.HOSTILE, 1, 1);
			};
			// 为什么要分呢？ 解决 NetHandlerPlayServer#lastGoodX 报 move wrong! 问题
			if (owner instanceof EntityPlayer) EventServer.addWorldTask(world, task);
			else task.onTick(world);
			return true;
		}
		return false;
	}

	public static boolean shouldAttack(EntityLivingBase owner, EntityLivingBase target) {
		Vec3d vecLook = target.getLook(1.0F).normalize();
		Vec3d vecTargetToOwner = new Vec3d(owner.posX - target.posX,
				owner.getEntityBoundingBox().minY + owner.getEyeHeight() - (target.posY + target.getEyeHeight()),
				owner.posZ - target.posZ);
		double length = vecTargetToOwner.length();
		vecTargetToOwner = vecTargetToOwner.normalize();
		double dot = vecLook.dotProduct(vecTargetToOwner);
		return dot > 1.0D - 0.025D / length ? target.canEntityBeSeen(owner) : false;
	}

	public static boolean teleportRandomly(EntityLivingBase entity) {
		Random rand = entity.getRNG();
		double x = entity.posX + (rand.nextDouble() - 0.5D) * 64.0D;
		double y = entity.posY + (rand.nextInt(64) - 32);
		double z = entity.posZ + (rand.nextDouble() - 0.5D) * 64.0D;
		EnderTeleportEvent event = new EnderTeleportEvent(entity, x, y, z, 0);
		if (MinecraftForge.EVENT_BUS.post(event)) return false;

		BlockPos pos = getTeleportPosition(entity, event.getTargetX(), event.getTargetY(), event.getTargetZ());
		if (pos == null) return false;

		if (entity.world.isRemote) return true;

		IWorldTickTask.IWorldTickTaskOnce task = w -> {
			MantraEnderTeleport.doEnderTeleport(entity.world, entity, new Vec3d(pos).add(0.5, 0.1, 0.5));
			entity.world.playSound((EntityPlayer) null, entity.prevPosX, entity.prevPosY, entity.prevPosZ,
					SoundEvents.ENTITY_ENDERMEN_TELEPORT, entity.getSoundCategory(), 1.0F, 1.0F);
			entity.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
		};

		if (entity instanceof EntityPlayer) EventServer.addWorldTask(entity.world, task);
		else task.onTick(entity.world);

		return true;
	}

	private static boolean checkTeleportPosition(World world, BlockPos pos) {
		return BlockHelper.isPassableBlock(world, pos) && !BlockHelper.isFluid(world, pos);
	}

	public static BlockPos getTeleportPosition(EntityLivingBase entity, double x, double y, double z) {
		BlockPos pos = new BlockPos(x, y, z);
		World world = entity.world;

		if (!world.isBlockLoaded(pos)) return null;

		boolean isFinded = false;
		while (pos.getY() > 0) {
			BlockPos down = pos.down();
			if (BlockHelper.isSolidBlock(world, pos)) {
				isFinded = true;
				break;
			} else pos = down;
		}

		if (!isFinded) return null;

		if (checkTeleportPosition(world, pos.up(1)) && checkTeleportPosition(world, pos.up(2))) return pos;

		return null;
	}

}
