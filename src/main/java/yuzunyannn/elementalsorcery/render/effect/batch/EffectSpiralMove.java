package yuzunyannn.elementalsorcery.render.effect.batch;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EffectSpiralMove extends EffectSpiral {

	public double xDecay = 0.96;
	public double yDecay = 1;
	public double zDecay = 0.96;
	public float dRotate = 0;

	public EffectSpiralMove(World world, Vec3d pos) {
		super(world, pos.x, pos.y, pos.z);
		this.dRotate = 2 + rand.nextInt(8);
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
		if (this.lifeTime <= 20) this.alpha = lifeTime / 20f;
		else if (this.alpha < 1) this.alpha = Math.min(1, alpha + 0.1f);

		this.rotate = this.rotate - dRotate * this.alpha;

		this.posX += motionX;
		this.posY += motionY;
		this.posZ += motionZ;

		motionX *= xDecay;
		motionY *= yDecay;
		motionZ *= zDecay;
	}

}
