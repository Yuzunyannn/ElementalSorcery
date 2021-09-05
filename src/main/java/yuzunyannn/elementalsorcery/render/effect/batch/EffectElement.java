package yuzunyannn.elementalsorcery.render.effect.batch;

import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.EffectBatchType;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class EffectElement extends EffectFacing {

	public final static EffectBatchTypeNormal BATCH_TYPE = new EffectBatchTypeNormal(
			"textures/effect/element_flare.png");

	public float dalpha;

	public EffectElement(World worldIn, double posXIn, double posYIn, double posZIn) {
		super(worldIn, posXIn, posYIn, posZIn);
		this.asParticle = true;
		this.dalpha = 1.0f / this.lifeTime;
		this.prevAlpha = this.alpha = 1.0f;
		this.prevScale = this.scale = rand.nextFloat() * 0.2f + 0.1f;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.alpha -= this.dalpha;
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
