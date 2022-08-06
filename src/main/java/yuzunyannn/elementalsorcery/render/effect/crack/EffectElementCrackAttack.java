package yuzunyannn.elementalsorcery.render.effect.crack;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectFacing;
import yuzunyannn.elementalsorcery.render.item.RenderItemElementCrack;
import yuzunyannn.elementalsorcery.util.helper.Color;

public class EffectElementCrackAttack extends Effect {

	public static void show(World world, Vec3d pos, NBTTagCompound nbt) {
		EffectElementCrackAttack effect = new EffectElementCrackAttack(world, pos);
		effect.endEffect = nbt.getBoolean("T");
		addEffect(effect);
	}

	public static void endEffect(World world, Vec3d pos) {
		final int[] COLORS = new int[] { 0xBFBFFF, 0xFFBFBF, 0xBFFFBF };
		for (int i = 0; i < 256; i++) {
			EffectFragmentCrackMove effect = new EffectFragmentCrackMove(world, pos);
			effect.lifeTime = 64;
			effect.prevScale = effect.scale = 0.1f;
			int c = COLORS[i % COLORS.length];
			effect.setColor(c);
			float sin = MathHelper.sin(i * 3.1415926f / 64);
			float cos = MathHelper.cos(i * 3.1415926f / 64);
			float sin2 = MathHelper.sin(i * 3.1415926f / 13);
			float cos2 = MathHelper.cos(i * 3.1415926f / 13);
			Vec3d speed = new Vec3d(sin, sin2 * cos2, cos).scale(1.5);
			Vec3d acce;
			if (c == 0xFFBFBF) {
				acce = new Vec3d(0, Effect.rand.nextDouble(), 0);
				speed = speed.scale(0.9);
			} else if (c == 0xBFBFFF) {
				acce = new Vec3d(0, -Effect.rand.nextDouble(), 0);
				speed = speed.scale(1);
			} else {
				double rand = Effect.rand.nextDouble();
				acce = new Vec3d(sin * rand, 0, cos * rand);
				speed = speed.scale(1.1);
			}
			effect.setVelocity(speed);
			effect.setAccelerate(acce.scale(0.075));
			effect.xDecay = effect.zDecay = effect.yDecay = 0.5;
			effect.isGlow = true;
			Effect.addEffect(effect);
		}
	}

	public final Color color = new Color();

	public float vRate, prevVRate;
	public float hRate, prevHRate;
	public float alpha, prevAlpha;

	public boolean endEffect = false;

	public float rotation = 0;

	public EffectElementCrackAttack(World world, Vec3d pos) {
		super(world, pos.x, pos.y, pos.z);
		color.setColor(0xFFFFFF);
		rotation = (float) (rand.nextGaussian() * 60);
		prevVRate = vRate = 4;
		prevAlpha = alpha = 1;
		this.lifeTime = 20;
	}

	@Override
	protected String myGroup() {
		return EffectBlockConfusion.GROUP_CONFUSION;
	}

	@Override
	public void onUpdate() {
		this.lifeTime--;
		if (this.lifeTime == 10) {
			if (endEffect) endEffect(world, getPositionVector());
		}
		prevVRate = vRate;
		prevHRate = hRate;
		prevAlpha = alpha;

		float r = 1 - (this.lifeTime - 10) / 10f;
		float s = (float) Math.pow(30, r + 1) / 900f;
		if (s < 1) vRate = 4 - 3.95f * s * r;
		else {
			vRate = 0.05f / s;
			alpha = 2 - r;
		}
		hRate = 4 * s * r;
	}

	@Override
	protected void doRender(float partialTicks) {
		double x = this.getRenderX(partialTicks);
		double y = this.getRenderY(partialTicks);
		double z = this.getRenderZ(partialTicks);

		GlStateManager.disableBlend();
		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();

		RenderItemElementCrack.bindCrackTexture();
		GlStateManager.depthFunc(519);
		RenderItemElementCrack.startTexGen(2);
		float a = RenderFriend.getPartialTicks(alpha, prevAlpha, partialTicks);
		float v = RenderFriend.getPartialTicks(vRate, prevVRate, partialTicks);
		float h = RenderFriend.getPartialTicks(hRate, prevHRate, partialTicks);

		float rX = ActiveRenderInfo.getRotationX();
		float rZ = ActiveRenderInfo.getRotationZ();
		float rYZ = ActiveRenderInfo.getRotationYZ();
		float rXY = ActiveRenderInfo.getRotationXY();
		float rXZ = ActiveRenderInfo.getRotationXZ();
		Vec3d v1 = new Vec3d(-rYZ * v, -rXZ * v, -rXY * v);
		Vec3d v2 = new Vec3d(-rX * h, 0, -rZ * h);
		Vec3d v3 = new Vec3d(rYZ * v, rXZ * v, rXY * v);
		Vec3d v4 = new Vec3d(rX * h, 0, rZ * h);

		Vec3d nor = EffectFacing.normalPlane(v1, v2, v3);
		v1 = EffectFacing.rotate(v1, nor, rotation);
		v2 = EffectFacing.rotate(v2, nor, rotation);
		v3 = EffectFacing.rotate(v3, nor, rotation);
		v4 = EffectFacing.rotate(v4, nor, rotation);

		GlStateManager.color(1, 1, 1, a);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x + v1.x, y + v1.y, z + v1.z).tex(0, 0).endVertex();
		bufferbuilder.pos(x + v2.x, y + v2.y, z + v2.z).tex(0, 1).endVertex();
		bufferbuilder.pos(x + v3.x, y + v3.y, z + v3.z).tex(1, 1).endVertex();
		bufferbuilder.pos(x + v4.x, y + v4.y, z + v4.z).tex(1, 0).endVertex();
		tessellator.draw();
		RenderItemElementCrack.endTexGen();
		GlStateManager.depthFunc(515);

	}
}
