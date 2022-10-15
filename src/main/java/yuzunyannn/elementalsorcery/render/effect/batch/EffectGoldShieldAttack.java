package yuzunyannn.elementalsorcery.render.effect.batch;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;

@SideOnly(Side.CLIENT)
public class EffectGoldShieldAttack extends Effect {

	public static void doEffect(World world, Vec3d pos, NBTTagCompound nbt) {
		int lev = nbt.getInteger("lev");
		float range = lev / 2f + 0.25f;
		for (int i = 0; i < 5 + (lev * 2); i++) {
			Vec3d at = pos.add(rand.nextGaussian() * range * 0.75, rand.nextGaussian() * 0.5,
					rand.nextGaussian() * range * 0.75);
			EffectGoldShieldAttack effect = new EffectGoldShieldAttack(world, at);
			Effect.addEffect(effect);
		}
	}

	public final static EffectBatchTypeNormal BATCH_TYPE = new EffectBatchTypeNormal(RenderObjects.EFFECT_BUFF);

	public float rotate, prevRotate;
	public float dRotate;

	public float alpha, prevAlpha;
	public float scale, prevScale;

	public EffectGoldShieldAttack(World worldIn, Vec3d vec) {
		super(worldIn, vec.x, vec.y, vec.z);
		this.asParticle = true;
		this.prevAlpha = this.alpha = 0;
		this.prevScale = this.scale = rand.nextFloat() * 0.125f + 0.05f;
		this.dRotate = rand.nextFloat() * 3 * 0.1f;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.prevRotate = rotate;
		this.prevAlpha = this.alpha;
		if (this.lifeTime > 20) this.alpha = this.alpha + (1 - this.alpha) * 0.1f;
		else this.alpha = Math.max(0, this.alpha - 0.05f);
		rotate += dRotate;
		this.posY += 0.01;
	}

	@Override
	protected EffectBatchTypeNormal typeBatch() {
		return BATCH_TYPE;
	}

	@Override
	protected void doRender(float partialTicks) {
		GlStateManager.color(1, 1, 1);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		RenderObjects.EFFECT_BUFF.bind();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		doRender(bufferbuilder, partialTicks);
		tessellator.draw();
	}

	@Override
	protected void doRender(BufferBuilder bufferbuilder, float partialTicks) {
		double x = this.getRenderX(partialTicks);
		double y = this.getRenderY(partialTicks);
		double z = this.getRenderZ(partialTicks);
		float roate = this.prevRotate + (this.rotate - this.prevRotate) * partialTicks;
		float scale = this.prevScale + (this.scale - this.prevScale) * partialTicks;
		float a = this.prevAlpha + (this.alpha - this.prevAlpha) * partialTicks;

		final float c = ((25 % 7) * 18) / 128f;
		final float r = ((25 / 7) * 18) / 128f;
		final float n = 18 / 128f;

		float l = scale;
		float dx = MathHelper.sin(roate) * l / 2;
		float dz = MathHelper.cos(roate) * l / 2;

		bufferbuilder.pos(x + dx, y, z + dz).tex(c, r).color(1, 1, 1, a).endVertex();
		bufferbuilder.pos(x - dx, y, z - dz).tex(c + n, r).color(1, 1, 1, a).endVertex();
		bufferbuilder.pos(x - dx, y + l, z - dz).tex(c + n, r + n).color(1, 1, 1, a).endVertex();
		bufferbuilder.pos(x + dx, y + l, z + dz).tex(c, r + n).color(1, 1, 1, a).endVertex();
	}

}
