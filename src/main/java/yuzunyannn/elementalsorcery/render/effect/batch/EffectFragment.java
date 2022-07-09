package yuzunyannn.elementalsorcery.render.effect.batch;

import java.util.function.Function;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class EffectFragment extends EffectFacing {

	public final static EffectBatchTypeNormal BATCH_TYPE = new EffectBatchTypeNormal("textures/effect/fragment.png");

	public float defaultScale = 0;
	public float rotate, prevRotate;
	public Function<Void, Void> onEndCallback;

	public EffectFragment(World worldIn, Vec3d pos) {
		super(worldIn, pos.x, pos.y, pos.z);
		this.asParticle = true;
		this.prevAlpha = this.alpha = 1;
		this.prevScale = this.scale = this.defaultScale = rand.nextFloat() * 0.05f + 0.05f;
	}

	@Override
	public double getRotate(float partialTicks) {
		return this.prevRotate + (this.rotate - this.prevRotate) * partialTicks;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.prevRotate = rotate;
		if (this.lifeTime == 0 && onEndCallback != null) onEndCallback.apply(null);
	}

	@Override
	protected EffectBatchTypeNormal typeBatch() {
		return BATCH_TYPE;
	}

	@Override
	protected void bindTexture() {
		typeBatch().bind();
	}
}
