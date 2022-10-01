package yuzunyannn.elementalsorcery.render.effect.batch;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.util.helper.Color;

public abstract class EffectFacing extends Effect {

	public boolean isGlow;

	public final Color color = new Color();
	public float alpha, prevAlpha;

	public float scale, prevScale;

	public double motionX;
	public double motionY;
	public double motionZ;

	public float texX = 0, texY = 0;
	public float texW = 1, texH = 1;

	public EffectFacing(World worldIn, double posXIn, double posYIn, double posZIn) {
		super(worldIn, posXIn, posYIn, posZIn);
	}

	public void setColor(float r, float g, float b) {
		color.setColor(r, g, b);
	}

	public void setColor(int c) {
		color.setColor(c);
	}

	public void setColor(Color c) {
		color.setColor(c);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.prevAlpha = this.alpha;
		this.prevScale = this.scale;
	}

	abstract protected void bindTexture();

	public double getRotate(float partialTicks) {
		return 0;
	}

	@Override
	protected void doRender(float partialTicks) {
		double x = this.getRenderX(partialTicks);
		double y = this.getRenderY(partialTicks);
		double z = this.getRenderZ(partialTicks);
		doRender(x, y, z, partialTicks);
	}

	@Override
	protected void doRender(BufferBuilder bufferbuilder, float partialTicks) {
		double x = this.getRenderX(partialTicks);
		double y = this.getRenderY(partialTicks);
		double z = this.getRenderZ(partialTicks);
		doRender(bufferbuilder, x, y, z, partialTicks);
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

		float r = color.r;
		float g = color.g;
		float b = color.b;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.pos(x + v1.x, y + v1.y, z + v1.z).tex(texX, texY);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(x + v2.x, y + v2.y, z + v2.z).tex(texX, texY + texH);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(x + v3.x, y + v3.y, z + v3.z).tex(texX + texW, texY + texH);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(x + v4.x, y + v4.y, z + v4.z).tex(texX + texW, texY);
		bufferbuilder.color(r, g, b, a).endVertex();
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

		float r = color.r;
		float g = color.g;
		float b = color.b;

		bufferbuilder.pos(x + v1.x, y + v1.y, z + v1.z).tex(texX, texY);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(x + v2.x, y + v2.y, z + v2.z).tex(texX, texY + texH);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(x + v3.x, y + v3.y, z + v3.z).tex(texX + texW, texY + texH);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(x + v4.x, y + v4.y, z + v4.z).tex(texX + texW, texY);
		bufferbuilder.color(r, g, b, a).endVertex();
	}

	public static Vec3d normalPlane(Vec3d v1, Vec3d v2, Vec3d v3) {
		double nx = (v2.y - v1.y) * (v3.z - v1.z) - (v2.z - v1.z) * (v3.y - v1.y);
		double ny = (v2.z - v1.z) * (v3.x - v1.x) - (v2.x - v1.x) * (v3.z - v1.z);
		double nz = (v2.x - v1.x) * (v3.y - v1.y) - (v2.y - v1.y) * (v3.x - v1.x);
		return new Vec3d(nx, ny, nz).normalize();
	}

	public static Vec3d rotate(Vec3d point, Vec3d raxis, double theta) {
		float r = (float) (theta * 3.1415926 / 180);
		double c = MathHelper.cos(r);
		double s = MathHelper.sin(r);
		double _x = (raxis.x * raxis.x * (1 - c) + c) * point.x + (raxis.x * raxis.y * (1 - c) - raxis.z * s) * point.y
				+ (raxis.x * raxis.z * (1 - c) + raxis.y * s) * point.z;
		double _y = (raxis.y * raxis.x * (1 - c) + raxis.z * s) * point.x + (raxis.y * raxis.y * (1 - c) + c) * point.y
				+ (raxis.y * raxis.z * (1 - c) - raxis.x * s) * point.z;
		double _z = (raxis.x * raxis.z * (1 - c) - raxis.y * s) * point.x
				+ (raxis.y * raxis.z * (1 - c) + raxis.x * s) * point.y + (raxis.z * raxis.z * (1 - c) + c) * point.z;
		return new Vec3d(_x, _y, _z);
	}

}
