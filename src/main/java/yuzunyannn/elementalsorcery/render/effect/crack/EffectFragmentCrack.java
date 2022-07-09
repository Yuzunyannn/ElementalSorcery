package yuzunyannn.elementalsorcery.render.effect.crack;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.EffectBatchType;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectFacing;
import yuzunyannn.elementalsorcery.render.item.RenderItemElementCrack;

@SideOnly(Side.CLIENT)
public abstract class EffectFragmentCrack extends EffectFacing {

	protected static class BatchType extends EffectBatchType {

		final boolean isGlow;

		public BatchType(boolean isGrow) {
			this.isGlow = isGrow;
		}

		@Override
		public void beginRender(Tessellator tessellator, BufferBuilder bufferbuilder) {
			RenderItemElementCrack.bindCrackTexture();
			RenderItemElementCrack.startTexGen(8);
			GlStateManager.disableBlend();
			RenderHelper.disableStandardItemLighting();
			if (isGlow) GlStateManager.depthFunc(519);
			bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		}

		public void endRender(Tessellator tessellator, BufferBuilder bufferbuilder) {
			tessellator.draw();
			if (isGlow) GlStateManager.depthFunc(515);
			RenderItemElementCrack.endTexGen();
		};
	}

	public final static EffectBatchType BATCH_TYPE = new BatchType(false);
	public final static EffectBatchType BATCH_TYPE_GLOW = new BatchType(true);

	public float defaultScale = 0;
	public float rotate, prevRotate;

	public EffectFragmentCrack(World worldIn, Vec3d pos) {
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
	}

	@Override
	protected EffectBatchType typeBatch() {
		return isGlow ? BATCH_TYPE_GLOW : BATCH_TYPE;
	}

	@Override
	protected void bindTexture() {
		RenderItemElementCrack.bindCrackTexture();
	}

	@Override
	protected String myGroup() {
		return EffectBlockConfusion.GROUP_CONFUSION;
	}

	@Override
	protected void doRender(BufferBuilder bufferbuilder, double x, double y, double z, float partialTicks) {
		super.doRender(bufferbuilder, x, y, z, partialTicks);
	}

	@Override
	protected void doRender(double x, double y, double z, float partialTicks) {
		super.doRender(x, y, z, partialTicks);
	}

}
