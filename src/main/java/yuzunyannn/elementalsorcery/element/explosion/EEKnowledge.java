package yuzunyannn.elementalsorcery.element.explosion;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.element.ElementStack;

public class EEKnowledge extends ElementExplosion {

	public EEKnowledge(World world, Vec3d position, float strength, ElementStack estack) {
		super(world, position, strength, estack);
		passExplosionBlock = true;
	}

	@Override
	protected void doExplosionEntityAt(Entity entity, Vec3d orient, double strength, double damage, double pound) {
		super.doExplosionEntityAt(entity, orient, strength, damage, pound);
		if (world.isRemote) doExplosionEntityAtEfect(world, eStack.getColor(), entity.getPositionEyes(0), orient);
	}
}
