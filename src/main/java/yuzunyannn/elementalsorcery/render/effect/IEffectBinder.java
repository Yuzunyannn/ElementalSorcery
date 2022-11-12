package yuzunyannn.elementalsorcery.render.effect;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;

public interface IEffectBinder {

	Vec3d getPosition();

	Vec3d getDirection();

	IEffectBinder fixToSpell();

	interface IEffectBinderGetter {
		IEffectBinder getEffectBinder();
	}

	public static IEffectBinder asBinder(IWorldObject wo) {
		if (wo instanceof IEffectBinderGetter) return ((IEffectBinderGetter) wo).getEffectBinder();
		return new WorldObjectBinder(wo, Vec3d.ZERO);
	}

	public static IEffectBinder asBinder(BlockPos pos) {
		return new VecBinder(new Vec3d(pos).add(0.5, 0, 0.5));
	}

	public final static class WorldObjectBinder implements IEffectBinder {

		final public IWorldObject wo;
		final public Vec3d offset;

		public WorldObjectBinder(IWorldObject wo, Vec3d offset) {
			this.wo = wo;
			this.offset = offset;
		}

		@Override
		public Vec3d getPosition() {
			return this.wo.getObjectPosition().add(offset);
		}

		@Override
		public Vec3d getDirection() {
			Entity entity = wo.asEntity();
			if (entity == null) return new Vec3d(0, 1, 0);
			else return entity.getLookVec();
		}

		@Override
		public IEffectBinder fixToSpell() {
			return new WorldObjectBinder(wo, offset.add(wo.getEyePosition().subtract(wo.getObjectPosition())));
		}

	}

	public final static class EntityBinder implements IEffectBinder {

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

		@Override
		public IEffectBinder fixToSpell() {
			return new EntityBinder(this.binder, this.yoff + this.binder.getEyeHeight());
		}

	}

	public final static class VecBinder implements IEffectBinder {

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

		@Override
		public IEffectBinder fixToSpell() {
			return new VecBinder(vec.add(0, 0.5, 0));
		}

	}

}
