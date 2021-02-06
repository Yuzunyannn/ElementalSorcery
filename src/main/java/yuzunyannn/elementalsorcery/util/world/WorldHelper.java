package yuzunyannn.elementalsorcery.util.world;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfession;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;

public class WorldHelper {

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

		if (rt != null) distance = rt.hitVec.distanceTo(eye);

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

	static public void newLightning(World world, BlockPos pos) {
		newLightning(world, pos, false);
	}

	static public void newLightning(World world, BlockPos pos, boolean effectOnly) {
		world.spawnEntity(new EntityLightningBolt(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, effectOnly));
	}

	static public void createExpBall(World world, Vec3d pos, int exp) {
		while (exp > 0) {
			int k = EntityXPOrb.getXPSplit(exp);
			exp -= k;
			world.spawnEntity(new EntityXPOrb(world, pos.x, pos.y + 0.5D, pos.z, k));
		}
	}

	static public void createExpBall(EntityPlayer player, int exp) {
		createExpBall(player.world, player.getPositionVector(), exp);
	}

}
