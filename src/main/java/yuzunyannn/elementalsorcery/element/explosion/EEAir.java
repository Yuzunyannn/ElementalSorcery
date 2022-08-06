package yuzunyannn.elementalsorcery.element.explosion;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.element.ElementStack;

public class EEAir extends ElementExplosion {

	public EEAir(World world, Vec3d position, float strength, ElementStack estack) {
		super(world, position, strength, estack);
	}

	@Override
	protected void doExplosionBlockAt(BlockPos pos) {
		super.doExplosionBlockAt(pos);
		if (world.isRemote && !world.isAirBlock(pos)) spawnEffectFromBlock(pos);
	}

	@Override
	protected void doExplosionEntityAt(Entity entity, Vec3d orient, double strength, double damage, double pound) {
		float power = eStack.getPower();
		double scale = MathHelper.clamp(MathHelper.sqrt(power / 10f), 1.5, 6) / 6;
		super.doExplosionEntityAt(entity, orient, strength, damage * 0.1f, pound * scale);
	}

}
