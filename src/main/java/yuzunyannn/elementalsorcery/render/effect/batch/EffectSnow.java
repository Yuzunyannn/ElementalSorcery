package yuzunyannn.elementalsorcery.render.effect.batch;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;

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

	public float texOffset = 0;

	public EffectSnow(World worldIn, Vec3d vec) {
		super(worldIn, vec.x, vec.y, vec.z);
		this.color.setColor(0xffffff);
		this.asParticle = true;
		this.prevAlpha = this.alpha = 0;
		this.prevScale = this.scale = rand.nextFloat() * 0.4f + 0.3f;
		this.texOffset = rand.nextFloat() * 0.75f;
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

	protected void doRender(double x, double y, double z, float partialTicks) {
		float a = this.prevAlpha + (this.alpha - this.prevAlpha) * partialTicks;
		float s = this.prevScale + (this.scale - this.prevScale) * partialTicks;
		double rotate = this.getRotate(partialTicks);

		bindTexture();
		float rX = ActiveRenderInfo.getRotationX();
		float rZ = ActiveRenderInfo.getRotationZ();
		float rYZ = ActiveRenderInfo.getRotationYZ();
		float rXY = ActiveRenderInfo.getRotationXY();
		float rXZ = ActiveRenderInfo.getRotationXZ();
		Vec3d v1 = new Vec3d(-rX - rYZ, -rXZ, -rZ - rXY).scale(s);
		Vec3d v2 = new Vec3d(-rX + rYZ, rXZ, -rZ + rXY).scale(s);
		Vec3d v3 = new Vec3d(rX + rYZ, rXZ, rZ + rXY).scale(s);
		Vec3d v4 = new Vec3d(rX - rYZ, -rXZ, rZ - rXY).scale(s);
		if (rotate != 0) {
			Vec3d nor = normalPlane(v1, v2, v3);
			v1 = rotate(v1, nor, rotate);
			v2 = rotate(v2, nor, rotate);
			v3 = rotate(v3, nor, rotate);
			v4 = rotate(v4, nor, rotate);
		}
		if (isGlow) GlStateManager.depthFunc(519);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		doRenderOnceBlock(bufferbuilder, x, y, z, a, v1, v2, v3, v4);
		tessellator.draw();
		if (isGlow) GlStateManager.depthFunc(515);
	}

	protected void doRender(BufferBuilder bufferbuilder, double x, double y, double z, float partialTicks) {
		float a = this.prevAlpha + (this.alpha - this.prevAlpha) * partialTicks;
		float s = this.prevScale + (this.scale - this.prevScale) * partialTicks;
		double rotate = this.getRotate(partialTicks);

		float rX = ActiveRenderInfo.getRotationX();
		float rZ = ActiveRenderInfo.getRotationZ();
		float rYZ = ActiveRenderInfo.getRotationYZ();
		float rXY = ActiveRenderInfo.getRotationXY();
		float rXZ = ActiveRenderInfo.getRotationXZ();
		Vec3d v1 = new Vec3d(-rX - rYZ, -rXZ, -rZ - rXY).scale(s);
		Vec3d v2 = new Vec3d(-rX + rYZ, rXZ, -rZ + rXY).scale(s);
		Vec3d v3 = new Vec3d(rX + rYZ, rXZ, rZ + rXY).scale(s);
		Vec3d v4 = new Vec3d(rX - rYZ, -rXZ, rZ - rXY).scale(s);

		if (rotate != 0) {
			Vec3d nor = normalPlane(v1, v2, v3);
			v1 = rotate(v1, nor, rotate);
			v2 = rotate(v2, nor, rotate);
			v3 = rotate(v3, nor, rotate);
			v4 = rotate(v4, nor, rotate);
		}

		doRenderOnceBlock(bufferbuilder, x, y, z, a, v1, v2, v3, v4);
	}

	protected void doRenderOnceBlock(BufferBuilder bufferbuilder, double x, double y, double z, float a, Vec3d v1,
			Vec3d v2, Vec3d v3, Vec3d v4) {

		float r = color.r;
		float g = color.g;
		float b = color.b;

		float texA = texOffset;
		float texB = texOffset + 0.25f;

		bufferbuilder.pos(x + v1.x, y + v1.y, z + v1.z).tex(0, texA);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(x + v2.x, y + v2.y, z + v2.z).tex(0, texB);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(x + v3.x, y + v3.y, z + v3.z).tex(1, texB);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(x + v4.x, y + v4.y, z + v4.z).tex(1, texA);
		bufferbuilder.color(r, g, b, a).endVertex();
	}

}
