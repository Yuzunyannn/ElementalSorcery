package yuzunyannn.elementalsorcery.render.effect.grimoire;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class EffectLaser extends Effect {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/effect/laser.png");

	public float alpha, prevAlpha;

	public float laserStart;
	public float laserWeak;

	public double prevTargetX;
	public double prevTargetY;
	public double prevTargetZ;
	public double targetX;
	public double targetY;
	public double targetZ;

	public final Color color = new Color();

	public EffectLaser(World world, Vec3d from, Vec3d to) {
		super(world, from.x, from.y, from.z);
		this.setTargetPosition(to);
		this.lifeTime = 100;
	}

	public EffectLaser setTargetPosition(Vec3d pos) {
		this.prevTargetX = this.targetX = pos.x;
		this.prevTargetY = this.targetY = pos.y;
		this.prevTargetZ = this.targetZ = pos.z;
		return this;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.prevTargetX = this.targetX;
		this.prevTargetY = this.targetY;
		this.prevTargetZ = this.targetZ;
		updateAlpha();
	}

	public void updateAlpha() {
		this.prevAlpha = this.alpha;
		if (lifeTime < 20) {
			alpha = (laserStart - laserWeak * 0.35f) * lifeTime / 20f;
		} else if (laserStart < 1) {
			alpha = laserStart = Math.min(1, laserStart + 0.1f);
		} else if (laserWeak < 1) {
			laserWeak = Math.min(1, laserWeak + 0.02f);
			alpha = 1 - laserWeak * 0.35f;
		}
	}

	public void hold(int tick) {
		this.lifeTime = tick + 1;
	}

	@Override
	protected void doRender(float partialTicks) {
		GlStateManager.pushMatrix();
		double x = getRenderX(partialTicks);
		double y = getRenderY(partialTicks);
		double z = getRenderZ(partialTicks);
		double tx = this.prevTargetX + (this.targetX - this.prevTargetX) * partialTicks;
		double ty = this.prevTargetY + (this.targetY - this.prevTargetY) * partialTicks;
		double tz = this.prevTargetZ + (this.targetZ - this.prevTargetZ) * partialTicks;
		GlStateManager.translate(x, y, z);

		Vec3d from = new Vec3d(x, y, z);
		Vec3d to = new Vec3d(tx, ty, tz);
		Vec3d tar = to.subtract(from);
		double len = tar.length();
		float projection = MathHelper.sqrt(tar.x * tar.x + tar.z * tar.z);
		float yR = (float) MathHelper.atan2(tar.x, tar.z) / 3.1415926f * 180;
		float xR = (float) MathHelper.atan2(projection, tar.y) / 3.1415926f * 180;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		float a = RenderHelper.getPartialTicks(alpha, prevAlpha, partialTicks);

		GlStateManager.rotate(yR, 0, 1, 0);
		GlStateManager.rotate(xR - 90, 1, 0, 0);
		renderHeadTail(len, a, partialTicks);
		float rotation = (EventClient.tickRender + partialTicks) * 60;
		GlStateManager.rotate(rotation, 0, 0, 1);
		GlStateManager.color(color.r, color.g, color.b, a);
		TEXTURE.bind();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		for (int i = 0; i < 16; i++) {
			float theta = 3.1415926f / 16 * i;
			double xf = 0.4 * MathHelper.sin(theta);
			double yf = 0.4 * MathHelper.cos(theta);
			bufferbuilder.pos(-xf, -yf, 0).tex(0, 0).endVertex();
			bufferbuilder.pos(xf, yf, 0).tex(1, 0).endVertex();
			bufferbuilder.pos(xf, yf, len).tex(1, 1).endVertex();
			bufferbuilder.pos(-xf, -yf, len).tex(0, 1).endVertex();
		}
		tessellator.draw();
		GlStateManager.rotate(-rotation, 0, 0, 1);
		renderHeadTailPost(len, a, partialTicks);

		GlStateManager.popMatrix();
	}

	protected void renderHeadTail(double len, float a, float partialTicks) {

	}

	protected void renderHeadTailPost(double len, float a, float partialTicks) {

	}

}
