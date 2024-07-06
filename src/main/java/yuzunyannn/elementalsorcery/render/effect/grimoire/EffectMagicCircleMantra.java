package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.render.effect.IEffectBinder;
import yuzunyannn.elementalsorcery.render.effect.particle.ParticleSpellScrew;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;

@SideOnly(Side.CLIENT)
public class EffectMagicCircleMantra extends EffectMagicCircleIcon {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/magic_circles/launch.png");

	public EffectMagicCircleMantra(World world, IEffectBinder binder, ResourceLocation icon) {
		super(world, binder, icon);
	}

	@Override
	protected void renderCircle(float partialTicks, float alpha) {
		TEXTURE.bind();
		this.renderTexRectInCenter(0, 0, 32, 32, r, g, b, alpha);
	}

	@Override
	protected void onAddEffect(Vec3d pos, float size) {
		float hSize = size / 5 * 2;
		float theta = (float) rand.nextGaussian() * 3.14f;
		Vec3d at = pos.add(MathHelper.sin(theta) * hSize, 0.1, MathHelper.cos(theta) * hSize);
		Vec3d tar = at.subtract(pos).normalize();
		tar = new Vec3d(-tar.z, 0, tar.x);
		ParticleSpellScrew effect = new ParticleSpellScrew(world, at);
		effect.setRBGColorF(r, g, b);
		effect.setSpeedH(tar.scale(0.05));
		mc.effectRenderer.addEffect(effect);
	}

	@Override
	protected void renderCenterIcon(float partialTicks, float alpha) {
		mc.getTextureManager().bindTexture(RenderObjects.MC_PARTICLE);

		for (int i = 0; i < 12; i++) {
			GlStateManager.pushMatrix();
			GlStateManager.rotate(30 * i, 0, 0, 1);
			GlStateManager.translate(12, 0, 0);
			this.drawTexturedRectInCenter(0, 0, 4, 4, 8 + 8 * i, 112, 8, 8, 128, 128, r, g, b, alpha);
			GlStateManager.popMatrix();
		}
		GlStateManager.scale(1.75, 1.75, 1.75);
		super.renderCenterIcon(partialTicks, alpha);

	}

}
