package yuzunyannn.elementalsorcery.render.effect;

import java.util.function.Function;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EffectConditionPacking extends EffectCondition {

	public final Effect effect;

	public EffectConditionPacking(Effect other) {
		this(other, v -> true);
	}

	public EffectConditionPacking(Effect other, Function<Void, Boolean> condition) {
		super(other.world, condition, other.getPositionVector());
		this.asParticle = other.asParticle;
		effect = other;
		setCondition(condition);
	}

	@Override
	public void onUpdate() {
		effect.onUpdate();
	}

	@Override
	public double getRenderX(float partialTicks) {
		return effect.getRenderX(partialTicks);
	}

	@Override
	public double getRenderY(float partialTicks) {
		return effect.getRenderY(partialTicks);
	}

	@Override
	public double getRenderZ(float partialTicks) {
		return effect.getRenderZ(partialTicks);
	}

	@Override
	public Vec3d getPositionVector() {
		return effect.getPositionVector();
	}

	@Override
	public void setPosition(double x, double y, double z) {
		effect.setPosition(x, y, z);
	}

	@Override
	public void setPosition(Entity pos) {
		effect.setPosition(pos);
	}

	@Override
	public void setPosition(Vec3d pos) {
		effect.setPosition(pos);
	}

	@Override
	protected void doRender(float partialTicks) {
		effect.doRender(partialTicks);
	}

	@Override
	protected void doRender(BufferBuilder bufferbuilder, float partialTicks) {
		effect.doRender(bufferbuilder, partialTicks);
	}

	@Override
	protected String myGroup() {
		return effect.myGroup();
	}

	@Override
	protected EffectBatchType typeBatch() {
		return effect.typeBatch();
	}

	@Override
	protected boolean canPassSpawn() {
		return effect.canPassSpawn();
	}

}
