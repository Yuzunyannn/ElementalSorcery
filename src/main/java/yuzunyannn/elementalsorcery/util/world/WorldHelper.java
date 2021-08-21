package yuzunyannn.elementalsorcery.util.world;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfession;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;

public class WorldHelper {

	public static boolean isBlockPassable(World world, BlockPos pos) {
		if (world.isAirBlock(pos)) return true;
		IBlockState state = world.getBlockState(pos);
		return state.getBlock().isReplaceable(world, pos);
	}

	@Nullable
	public static BlockPos tryFindPlaceToSpawn(World world, Random rand, BlockPos center, float range) {
		float theta = rand.nextFloat() * 3.1415926f * 2;
		final int tryUp = 8;
		for (int tryTimes = 0; tryTimes < 6; tryTimes++) {
			double x = center.getX() + 0.5 + MathHelper.sin(theta) * range;
			double z = center.getZ() + 0.5 + MathHelper.cos(theta) * range;
			BlockPos pos = new BlockPos(x, center.getY() + tryUp, z);
			for (int k = 0; k < tryUp * 2 && pos.getY() > 0; k++, pos = pos.down())
				if (!isBlockPassable(world, pos)) break;

			if (isBlockPassable(world, pos)) return null;

			if (!isBlockPassable(world, pos.up(1))) return null;
			if (!isBlockPassable(world, pos.up(2))) return null;

			return pos;
		}
		return null;
	}

	static public List<EntityElfBase> getElfWithAABB(World world, AxisAlignedBB aabb, ElfProfession profession) {
		return world.getEntitiesWithinAABB(EntityElfBase.class, aabb, (entity) -> {
			if (profession == null) return true;
			return entity.getProfession() == profession;
		});
	}

	static public boolean canChangeRender() {
		return Minecraft.getMinecraft().isSingleplayer()
				|| (Minecraft.getMinecraft().world != null && Minecraft.getMinecraft().world.isRemote);
	}

	/** 获取生物正在看的方块 */
	@Nullable
	static public RayTraceResult getLookAtBlock(World world, EntityLivingBase entity, float distance,
			boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
		Vec3d vstart = entity.getPositionEyes(1.0F);
		Vec3d vend = entity.getLookVec().scale(distance).add(vstart);
		RayTraceResult reulst = world.rayTraceBlocks(vstart, vend, stopOnLiquid, ignoreBlockWithoutBoundingBox,
				returnLastUncollidableBlock);
		if (reulst == null || reulst.typeOfHit != RayTraceResult.Type.BLOCK) return null;
		return reulst;
	}

	static public RayTraceResult getLookAtBlock(World world, EntityLivingBase entity, float distance) {
		return getLookAtBlock(world, entity, distance, false, false, false);
	}

	/** 获取生物正在看的实体 */
	static public <T extends Entity> RayTraceResult getLookAtEntity(World world, EntityLivingBase entity,
			double distance, Class<T> entityType) {

		Vec3d eye = entity.getPositionEyes(1.0f);
		Vec3d look = entity.getLook(1.0F);
		Vec3d lookEnd = eye.addVector(look.x * distance, look.y * distance, look.z * distance);
		RayTraceResult rt = world.rayTraceBlocks(eye, lookEnd, false, false, true);

		if (rt != null) distance = rt.hitVec.distanceTo(eye) + 1;

		Entity pointedEntity = null;
		Vec3d hitVec = null;
		AxisAlignedBB aabb = entity.getEntityBoundingBox()
				.expand(look.x * distance, look.y * distance, look.z * distance).grow(1.0D, 1.0D, 1.0D);
		List<Entity> list = world.getEntitiesInAABBexcluding(entity, aabb, new Predicate<Entity>() {
			public boolean apply(@Nullable Entity check) {
				return check != null && check != entity && entityType.isAssignableFrom(check.getClass());
			}
		});
		double minDistance = distance;
		for (int i = 0; i < list.size(); i++) {
			Entity entity1 = list.get(i);
			AxisAlignedBB entityAABB = entity1.getEntityBoundingBox().grow(entity1.getCollisionBorderSize());
			RayTraceResult raytraceresult = entityAABB.calculateIntercept(eye, lookEnd);

			if (entityAABB.contains(eye)) {
				if (minDistance >= 0.0D) {
					pointedEntity = entity1;
					hitVec = raytraceresult == null ? eye : raytraceresult.hitVec;
					minDistance = 0.0D;
				}
				continue;
			}
			if (raytraceresult == null) continue;

			double len = eye.distanceTo(raytraceresult.hitVec);

			if (len < minDistance || minDistance == 0) {
				if (entity1.getLowestRidingEntity() == entity.getLowestRidingEntity() && !entity1.canRiderInteract()) {
					if (minDistance == 0.0D) {
						pointedEntity = entity1;
						hitVec = raytraceresult.hitVec;
					}
				} else {
					pointedEntity = entity1;
					hitVec = raytraceresult.hitVec;
					minDistance = len;
				}
			}
		}

		if (pointedEntity != null) return new RayTraceResult(pointedEntity, hitVec);

		return null;
	}

	static public void newLightning(World world, Vec3d pos) {
		newLightning(world, pos, false);
	}

	static public void newLightning(World world, Vec3d pos, boolean effectOnly) {
		world.addWeatherEffect(new EntityLightningBolt(world, pos.x + 0.5, pos.y, pos.z + 0.5, effectOnly));
	}

	static public void createExpBall(World world, Vec3d pos, int exp) {
		while (exp > 0) {
			int k = EntityXPOrb.getXPSplit(exp);
			exp -= k;
			world.spawnEntity(new EntityXPOrb(world, pos.x, pos.y + 0.5D, pos.z, k));
		}
	}

	static public void createExpBall(EntityLivingBase player, int exp) {
		createExpBall(player.world, player.getPositionVector(), exp);
	}

	static public AxisAlignedBB createAABB(BlockPos pos, double range, double yUp, double yDown) {
		return createAABB(new Vec3d(pos).addVector(0.5, 0.5, 0.5), range, yUp, yDown);
	}

	static public AxisAlignedBB createAABB(Vec3d pos, double range, double yUp, double yDown) {
		return new AxisAlignedBB(pos.x - range, pos.y - yDown, pos.z - range, pos.x + range, pos.y + yUp,
				pos.z + range);
	}

	static public EntityLivingBase restoreLiving(World world, UUID playerUUID) {
		List<EntityLivingBase> list = world.getEntities(EntityLivingBase.class, (e) -> {
			return e.getUniqueID().equals(playerUUID);
		});
		if (!list.isEmpty()) return list.get(0);
		if (world instanceof WorldServer) {
			WorldServer ws = (WorldServer) world;
			PlayerList pl = ws.getMinecraftServer().getPlayerList();
			return pl.getPlayerByUUID(playerUUID);
		}
		return null;
	}

}
