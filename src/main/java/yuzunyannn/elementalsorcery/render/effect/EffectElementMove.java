package yuzunyannn.elementalsorcery.render.effect;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EffectElementMove extends EffectElement {

	public double g = 0.01f;

	public EffectElementMove(World world, Vec3d pos) {
		super(world, pos.x, pos.y, pos.z);
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

		motionY -= g;

		motionX *= 0.96f;
		motionZ *= 0.96f;
	}

}
