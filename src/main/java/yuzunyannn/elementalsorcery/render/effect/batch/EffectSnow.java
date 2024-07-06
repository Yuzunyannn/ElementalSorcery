package yuzunyannn.elementalsorcery.render.effect.batch;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class EffectSnow extends EffectFacing {

	public final static EffectBatchTypeNormal BATCH_TYPE = new EffectBatchTypeNormal(
			new TextureBinder("minecraft", "textures/environment/snow.png"));

	public float rotate, prevRotate;
	public float dRotate;

	public double xDecay = 0.99;
	public double yDecay = 0.99;
	public double zDecay = 0.99;

	public double xAccelerate = 0;
	public double yAccelerate = 0;
	public double zAccelerate = 0;

	public EffectSnow(World worldIn, Vec3d vec) {
		super(worldIn, vec.x, vec.y, vec.z);
		this.color.setColor(0xffffff);
		this.asParticle = true;
		this.prevAlpha = this.alpha = 0;
		this.prevScale = this.scale = rand.nextFloat() * 0.4f + 0.3f;
		this.texY = rand.nextFloat() * 0.75f;
		this.texH = 0.25f;
		this.dRotate = rand.nextFloat() * 3;
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
