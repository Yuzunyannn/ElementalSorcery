package yuzunyannn.elementalsorcery.render.effect;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.tile.RenderTileElfTreeCore;

@SideOnly(Side.CLIENT)
public class EffectShineBlock extends Effect {

	protected float r, g, b;
	protected float alpha, prevAlpha;

	public EffectShineBlock(World worldIn, BlockPos pos, int sec) {
		super(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
		this.lifeTime = 20 * sec;
		this.alpha = 1;
		this.setColor(0xc6ac22);
	}

	public void setColor(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public EffectShineBlock setColor(int color) {
		this.r = ((color >> 16) & 0xff) / 255.0f;
		this.g = ((color >> 8) & 0xff) / 255.0f;
		this.b = ((color >> 0) & 0xff) / 255.0f;
		return this;
	}

	@Override
	public void onUpdate() {
		if (world.isAirBlock(new BlockPos(getPositionVector()))) {
			this.lifeTime = 0;
			return;
		}
		super.onUpdate();
		this.prevAlpha = this.alpha;
		if (this.lifeTime > 20) {
			int count = this.lifeTime - 20;
			this.alpha = Math.abs(MathHelper.cos(count * 0.05f));
		} else this.alpha = this.lifeTime / 20f;
	}

	@Override
	protected void doRender(float partialTicks) {
		double x = this.getRenderX(partialTicks);
		double y = this.getRenderY(partialTicks);
		double z = this.getRenderZ(partialTicks);
		float alpha = this.prevAlpha + (this.alpha - this.prevAlpha) * partialTicks;

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.depthFunc(519);
		GlStateManager.disableTexture2D();
		GlStateManager.color(r, g, b, alpha * 0.1f);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);

		GlStateManager.scale(0.0625, 0.0625, 0.0625);
		RenderTileElfTreeCore.MODEL.render(null, 0, 0, 0, 0, 0, 1);

//		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableTexture2D();
		GlStateManager.depthFunc(515);
		GlStateManager.popMatrix();
	}

}
