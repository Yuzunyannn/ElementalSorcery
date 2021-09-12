package yuzunyannn.elementalsorcery.render.effect.batch;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EffectElementMove extends EffectElement {

	public double xDecay = 0.96;
	public double yDecay = 1;
	public double zDecay = 0.96;

	public double xAccelerate = 0;
	public double yAccelerate = 0;
	public double zAccelerate = 0;

	public EffectElementMove(World world, Vec3d pos) {
		super(world, pos.x, pos.y, pos.z);
	}

	public void setAccelerate(double x, double y, double z) {
		xAccelerate = x;
		yAccelerate = y;
		zAccelerate = z;
	}

	public void setAccelerate(Vec3d acce) {
		xAccelerate = acce.x;
		yAccelerate = acce.y;
		zAccelerate = acce.z;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.posX += motionX;
		this.posY += motionY;
		this.posZ += motionZ;

		motionX += xAccelerate;
		motionY += yAccelerate;
		motionZ += zAccelerate;

		motionX *= xDecay;
		motionY *= yDecay;
		motionZ *= zDecay;
	}

}
