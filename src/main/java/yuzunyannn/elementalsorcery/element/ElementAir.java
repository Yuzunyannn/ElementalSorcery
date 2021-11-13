package yuzunyannn.elementalsorcery.element;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.element.explosion.EEAir;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.element.DrinkJuiceEffectAdder;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;
import yuzunyannn.elementalsorcery.world.JuiceMaterial;

public class ElementAir extends ElementCommon {

	public ElementAir() {
		super(0xe5ffff, "air");
	}

	@Override
	public ElementStack starFlowerCasting(World world, BlockPos pos, ElementStack estack, int tick) {
		if (tick % 5 != 0) return estack;
		int range = getStarFlowerRange(estack);
		AxisAlignedBB aabb = WorldHelper.createAABB(pos, range, range, 0.5);
		List<EntityMob> entities = world.getEntitiesWithinAABB(EntityMob.class, aabb);
		for (EntityMob entity : entities) {
			Vec3d c = new Vec3d(pos).addVector(0.5, 0, 0.5);
			Vec3d tar = entity.getPositionVector().addVector(0, 0.5, 0).subtract(c);
//			double l = entity.getDistance(c.x, c.y, c.z) / 10;
			tar = tar.normalize().scale(1);// .scale(Math.min(1 / Math.max(0.01, l), 0.1));
			entity.addVelocity(tar.x, 0, tar.z);
		}
		if (entities.isEmpty()) return estack;
		estack.shrink(Math.max(1, entities.size() / 5));
		return estack;
	}

	@Override
	public ElementExplosion newExplosion(World world, Vec3d pos, ElementStack eStack, EntityLivingBase attacker) {
		return new EEAir(world, pos, ElementExplosion.getStrength(eStack), eStack);
	}

	@Override
	protected void addDrinkJuiceEffect(DrinkJuiceEffectAdder helper) {

		helper.preparatory(MobEffects.SPEED, 32, 75);
		helper.check(JuiceMaterial.ELF_FRUIT, 65).join();
		helper.descend(JuiceMaterial.ELF_FRUIT, 10, 1);

		helper.preparatory(MobEffects.JUMP_BOOST, 32, 75);
		helper.check(JuiceMaterial.ELF_FRUIT, 65).join();
		helper.descend(JuiceMaterial.ELF_FRUIT, 10, 1);

		helper.preparatory(ESInit.POTIONS.WIND_WALKER, 32, 85);
		helper.check(JuiceMaterial.APPLE, 85).checkRatio(JuiceMaterial.MELON, 0.5f, 1.5f).join();
		helper.descend(JuiceMaterial.MELON, 20, 0.8f);

		helper.preparatory(ESInit.POTIONS.WIND_SHIELD, 32, 85);
		helper.check(JuiceMaterial.MELON, 85).checkRatio(JuiceMaterial.APPLE, 0.5f, 1.5f).join();
		helper.descend(JuiceMaterial.APPLE, 20, 0.8f);

	}
}
