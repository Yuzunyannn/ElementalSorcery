package yuzunyannn.elementalsorcery.element;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.element.explosion.EEFire;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFireArea;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ElementFire extends ElementCommon {

	public ElementFire() {
		super(rgb(255, 153, 2), "fire");
	}

	@Override
	public ElementStack starFlowerCasting(World world, BlockPos pos, ElementStack estack, int tick) {
		if (tick % 20 != 0) return estack;
		int range = getStarFlowerRange(estack);
		AxisAlignedBB aabb = WorldHelper.createAABB(pos, range, range, 2);
		int power = estack.getPower();
		float addDamage = power > 25 ? MathHelper.sqrt((power - 25) / 50) : 0;
		List<Entity> entities = world.getEntitiesWithinAABB(EntityMob.class, aabb);
		for (Entity entity : entities) {
			entity.setFire(3);
			if (addDamage > 0) entity.attackEntityFrom(DamageSource.IN_FIRE, addDamage);
			if (world.isRemote)
				MantraFireArea.addEffect(world, entity.getPositionVector().addVector(0, entity.height / 2, 0));
		}
		if (entities.isEmpty()) return estack;
		estack.shrink(Math.max(1, entities.size() / 5));
		return estack;
	}

	@Override
	public ElementExplosion newExplosion(World world, Vec3d pos, ElementStack eStack, EntityLivingBase attacker) {
		return new EEFire(world, pos, ElementExplosion.getStrength(eStack), eStack);
	}

}
