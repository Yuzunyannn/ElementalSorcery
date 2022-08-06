package yuzunyannn.elementalsorcery.potion;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class PotionEndercorps extends PotionCommon {

	public PotionEndercorps() {
		super(false, 0x450058, "endercorps");
		iconIndex = 7;
	}

	public static boolean tryAttackEntityFrom(EntityLivingBase target, EntityLivingBase attacker, DamageSource source,
			float amount) {
		if (!attacker.isPotionActive(ESObjects.POTIONS.ENDERCORPS)) return false;
		if (target instanceof EntityEnderman) return true;
		if (attacker.getRNG().nextFloat() >= 0.25f) return false;

		World world = attacker.world;
		if (world.isRemote) return false;
		if (isEnd(world)) return false;
		if (world.isRaining()) return false;

		int amplifier = attacker.getActivePotionEffect(ESObjects.POTIONS.ENDERCORPS).getAmplifier();
		float length = Math.min(6 + amplifier * 3, 16);
		List<EntityEnderman> endermans = getEnderman(attacker, length);
		int n = amplifier + 1;

		if (endermans.size() < n) {
			BlockPos pos = WorldHelper.tryFindPlaceToSpawn(world, world.rand, target.getPosition(), 8);
			if (pos != null) {
				pos = pos.up();
				EntityEnderman enderman = new EntityEnderman(world);
				enderman.setPosition(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ());
				world.spawnEntity(enderman);
				endermans.add(enderman);
			}
		}

		for (EntityEnderman enderman : endermans) {
			Entity att = enderman.getAttackTarget();
			if (att == null || att.isDead) enderman.setAttackTarget(target);
		}

		return false;
	}

	public static boolean isEnd(World world) {
		return world.provider.getDimension() == 1;
	}

	public static List<EntityEnderman> getEnderman(EntityLivingBase owner, float size) {
		AxisAlignedBB aabb = WorldHelper.createAABB(owner, size, size, size);
		return owner.world.getEntitiesWithinAABB(EntityEnderman.class, aabb);
	}

}
