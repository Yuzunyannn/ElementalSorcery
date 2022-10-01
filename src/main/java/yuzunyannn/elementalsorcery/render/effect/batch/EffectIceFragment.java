package yuzunyannn.elementalsorcery.render.effect.batch;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectIceCrystalBomb;

@SideOnly(Side.CLIENT)
public class EffectIceFragment extends EffectFacing {

	public final static EffectBatchTypeNormal BATCH_TYPE = new EffectBatchTypeNormal(EffectIceCrystalBomb.TEXTURE);

	public float rotate, prevRotate;
	public float dRotate;

	public double xDecay = 0.99;
	public double yDecay = 0.99;
	public double zDecay = 0.99;

	public double xAccelerate = 0;
	public double yAccelerate = 0;
	public double zAccelerate = 0;

	public EffectIceFragment(World worldIn, Vec3d vec) {
		super(worldIn, vec.x, vec.y, vec.z);
		this.color.setColor(0xffffff);
		this.asParticle = true;
		this.prevAlpha = this.alpha = 0;
		this.prevScale = this.scale = rand.nextFloat() * 0.01f + 0.005f;
		this.dRotate = rand.nextFloat() * 3;
		randomUV(rand.nextFloat() * 0.1f + 0.1f);
	}

	public void randomUV(float size) {
		this.texX = rand.nextFloat() * (1 - size);
		this.texY = rand.nextFloat() * (1 - size);
		this.texW = this.texH = size;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.prevRotate = rotate;

		if (this.lifeTime > 20) this.alpha = this.alpha + (1 - this.alpha) * 0.05f;
		else this.alpha = Math.max(0, this.alpha - 0.05f);

		rotate += dRotate;

		this.posX += motionX;
		this.posY += motionY;
		this.posZ += motionZ;

		motionX += xAccelerate;
		motionY += yAccelerate;
		motionZ += zAccelerate;

		motionX *= xDecay;
		motionY *= yDecay;
		motionZ *= zDecay;
	}

	public void setAccelerate(Vec3d acce) {
		xAccelerate = acce.x;
		yAccelerate = acce.y;
		zAccelerate = acce.z;
	}

	public void setVelocity(Vec3d speed) {
		motionX = speed.x;
		motionY = speed.y;
		motionZ = speed.z;
	}

	public void setDecay(double d) {
		this.xDecay = this.yDecay = this.zDecay = d;
	}

	@Override
	public double getRotate(float partialTicks) {
		return this.prevRotate + (this.rotate - this.prevRotate) * partialTicks;
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
