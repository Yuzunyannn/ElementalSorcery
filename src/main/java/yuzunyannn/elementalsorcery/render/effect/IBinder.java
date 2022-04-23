package yuzunyannn.elementalsorcery.render.effect;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public interface IBinder {

	Vec3d getPosition();

	Vec3d getDirection();

	public final static class EntityBinder implements IBinder {

		final public Entity binder;
		final public float yoff;

		public EntityBinder(Entity binder, float yoff) {
			this.binder = binder;
			this.yoff = yoff;
		}

		@Override
		public Vec3d getPosition() {
			return this.binder.getPositionVector().add(0, yoff, 0);
		}

		@Override
		public Vec3d getDirection() {
			return binder.getLookVec();
		}

	}

	public final static class VecBinder implements IBinder {

		final public Vec3d vec;

		public VecBinder(Vec3d pos) {
			this.vec = pos;
		}

		@Override
		public Vec3d getPosition() {
			return vec;
		}

		@Override
		public Vec3d getDirection() {
			return new Vec3d(1, 0, 0);
		}

	}

}
