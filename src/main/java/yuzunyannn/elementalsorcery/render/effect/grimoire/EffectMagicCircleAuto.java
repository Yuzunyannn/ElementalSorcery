package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.IEffectBinder;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;

@SideOnly(Side.CLIENT)
public class EffectMagicCircleAuto extends EffectMagicCircle {

	public ResourceLocation icon;
	public Vec3d direction, prevDirection;

	public EffectMagicCircleAuto(World world, IEffectBinder binder, ResourceLocation icon) {
		super(world, binder);
		this.icon = icon;
		direction = prevDirection = binder.getDirection();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		prevDirection = direction;
		direction = binder.getDirection();
	}

	@Override
	protected void onAddEffect(Vec3d pos, float size) {
		size = size * 0.1f;
		pos = pos.add(rand.nextGaussian() * size, rand.nextGaussian() * size, rand.nextGaussian() * size);
		EffectElementMove effect = new EffectElementMove(world, pos);
		effect.setVelocity(direction.scale(0.05));
		effect.setColor(r, g, b);
		Effect.addEffect(effect);
	}

	@Override
	protected void doRotate(float partialTicks) {
		Vec3d vec = RenderFriend.getPartialTicks(direction, prevDirection, partialTicks);
		GlStateManager.rotate((float) MathHelper.atan2(vec.x, vec.z) / 3.14f * 180, 0, 1, 0);
		GlStateManager.rotate((float) -vec.y * 90, 1, 0, 0);
		float rotate = RenderFriend.getPartialTicks(this.rotate, this.preRotate, partialTicks);
		GlStateManager.rotate(rotate, 0, 0, 1);
	}

	@Override
	protected void renderCircle(float partialTicks, float alpha) {
		GlStateManager.scale(0.25, 0.25, 0.25);
		super.renderCircle(partialTicks, alpha);
	}

	@Override
	protected void renderCenterIcon(float partialTicks, float alpha) {
		if (icon == null) return;
		GlStateManager.translate(0, 0, 1);
		GlStateManager.scale(2, 2, 2);
		mc.getTextureManager().bindTexture(icon);
		this.renderTexRectInCenter(0, 0, 12, 12, r * 0.9f, g * 0.9f, b * 0.9f, alpha);
		GlStateManager.translate(0, 0, 0.02);
		GlStateManager.scale(0.9, 0.9, 0.9);
		this.renderTexRectInCenter(0, 0, 12, 12, r, g, b, alpha);
	}

}
