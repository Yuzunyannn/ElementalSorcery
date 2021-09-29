package yuzunyannn.elementalsorcery.element.explosion;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.element.ElementStack;

public class EEKnowledge extends ElementExplosion {

	public EEKnowledge(World world, Vec3d position, float strength, ElementStack estack) {
		super(world, position, strength, estack);
		passExplosionBlock = true;
	}

}
