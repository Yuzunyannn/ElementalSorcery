package yuzunyannn.elementalsorcery.render.effect.batch;

import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.EffectBatchType;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public abstract class EffectSpiral extends EffectFacing {

	public final static EffectBatchTypeNormal BATCH_TYPE = new EffectBatchTypeNormal("textures/effect/spiral.png");

	public float rotate, prevRotate;

	public EffectSpiral(World worldIn, double posXIn, double posYIn, double posZIn) {
		super(worldIn, posXIn, posYIn, posZIn);
		this.asParticle = true;
		this.prevAlpha = this.alpha = 1;
		this.prevScale = this.scale = rand.nextFloat() * 0.2f + 0.1f;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.prevRotate = rotate;
	}

	@Override
	public double getRotate(float partialTicks) {
		return this.prevRotate + (this.rotate - this.prevRotate) * partialTicks;
	}

	@Override
	protected EffectBatchType typeBatch() {
		return BATCH_TYPE;
	}

	@Override
	protected TextureBinder getTexture() {
		return BATCH_TYPE.TEXTURE;
	}
}
