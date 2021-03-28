package yuzunyannn.elementalsorcery.element;

import java.util.List;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.element.IStarFlowerCast;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ElementAir extends ElementCommon implements IStarFlowerCast {

	public ElementAir() {
		super(rgb(242, 243, 243), "air");
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
}
