package yuzunyannn.elementalsorcery.render.effect.batch;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EffectTreatMove extends EffectTreat {

	public double xDecay = 0.9;
	public double yDecay = 0.93;
	public double zDecay = 0.9;

	public EffectTreatMove(World world, Vec3d pos) {
		super(world, pos.x, pos.y, pos.z);
		this.motionY = 0.02 + rand.nextGaussian() * 0.02;
	}

	public void setVelocity(double x, double y, double z) {
		motionX = x;
		motionY = y;
		motionZ = z;
	}

	public void setVelocity(Vec3d v) {
		motionX = v.x;
		motionY = v.y;
		motionZ = v.z;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.posX += motionX;
		this.posY += motionY;
		this.posZ += motionZ;

		motionX *= xDecay;
		motionY *= yDecay;
		motionZ *= zDecay;
	}

}
