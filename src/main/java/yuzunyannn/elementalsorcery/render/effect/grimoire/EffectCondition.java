package yuzunyannn.elementalsorcery.render.effect.grimoire;

import java.util.function.Function;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.Effect;

@SideOnly(Side.CLIENT)
public abstract class EffectCondition extends Effect {

	public Function<Void, Boolean> condition;

	public EffectCondition(World world, Function<Void, Boolean> condition, Vec3d pos) {
		super(world, pos.x, pos.y, pos.z);
		this.condition = condition;
	}

	public EffectCondition(World world, Vec3d pos) {
		super(world, pos.x, pos.y, pos.z);
	}

	public EffectCondition(World world, Function<Void, Boolean> condition) {
		this(world, condition, new Vec3d(0, 0, 0));
	}

	public EffectCondition(World world) {
		this(world, new Vec3d(0, 0, 0));
	}

	public void setCondition(Function<Void, Boolean> condition) {
		this.condition = condition;
	}

	public Function<Void, Boolean> getCondition() {
		return condition;
	}

	public boolean isEnd() {
		return condition == null ? true : (condition.apply(null) ? false : true);
	}

	@Override
	public void onUpdate() {
		this.lifeTime = this.isEnd() ? 0 : 1;
	}

	public static class ConditionEntityAction implements Function<Void, Boolean> {
		public final Entity entity;
		public boolean isFinish = false;

		public ConditionEntityAction(Entity entity) {
			this.entity = entity;
		}

		@Override
		public Boolean apply(Void t) {
			if (isFinish) return false;
			if (this.entity instanceof EntityLivingBase)
				return !(isFinish = !((EntityLivingBase) entity).isHandActive());
			else return !(isFinish = entity.isDead);
		}
	}

}
