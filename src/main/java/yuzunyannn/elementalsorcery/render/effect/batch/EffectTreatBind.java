package yuzunyannn.elementalsorcery.render.effect.batch;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.render.effect.IBinder;

public class EffectTreatBind extends EffectTreat {

	public IBinder binder;

	public double rise;
	public double motionY = 0.04;

	public double offsetX;
	public double offsetY;
	public double offsetZ;

	public EffectTreatBind(World world, Vec3d pos) {
		super(world, pos.x, pos.y, pos.z);
	}

	public void setBinder(IBinder binder) {
		this.binder = binder;
	}

	public void setOffsetPosition(Vec3d vec) {
		offsetX = vec.x;
		offsetY = vec.y;
		offsetZ = vec.z;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (this.binder != null) {
			Vec3d vec = this.binder.getPosition();
			this.posX = vec.x + offsetX;
			this.posY = vec.y + offsetY + rise;
			this.posZ = vec.z + offsetZ;

			rise = rise + motionY;
			motionY = motionY * 0.92;

		} else {
			this.binder = new IBinder.VecBinder(this.getPositionVector());
		}
	}

}
