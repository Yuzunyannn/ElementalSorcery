package yuzunyannn.elementalsorcery.render.effect;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public interface IBinder {

	Vec3d getPosition();

	public static class EntityBinder implements IBinder {

		final public Entity binder;

		public EntityBinder(Entity binder) {
			this.binder = binder;
		}

		@Override
		public Vec3d getPosition() {
			return this.binder == null ? Vec3d.ZERO
					: this.binder.getPositionVector().addVector(0, this.binder.height / 2, 0);
		}

	}

	public static class VecBinder implements IBinder {

		final public Vec3d pos;

		public VecBinder(Vec3d pos) {
			this.pos = pos;
		}

		@Override
		public Vec3d getPosition() {
			return pos;
		}

	}

}
