package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectFragmentMove;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.math.MathSupporter;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;

@SideOnly(Side.CLIENT)
public class EffectLaserMantra extends EffectLaser {

	public final Color magicCircleColor = new Color();
	public Vec3d toPos;
	public Vec3d toTarget;

	public EffectLaserMantra(World world, Vec3d from, Vec3d to) {
		super(world, from, to);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (toPos != null) {
			posX = toPos.x;
			posY = toPos.y;
			posZ = toPos.z;
		}

		if (toTarget != null) {
			targetX = toTarget.x;
			targetY = toTarget.y;
			targetZ = toTarget.z;
		}

		Vec3d to = new Vec3d(targetX, targetY, targetZ);
		Vec3d from = getPositionVector();
		Vec3d tar = to.subtract(from);
		Vec3d randSpeed = new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian()).normalize()
				.scale(0.04);
		Vec3d speed = tar.normalize().scale(-0.05).add(randSpeed);
		EffectFragmentMove effect = new EffectFragmentMove(world, to);
		effect.endLifeTick = effect.lifeTime;
		effect.prevScale = effect.scale = effect.defaultScale = rand.nextFloat() * 0.02f + 0.05f;
		effect.color.setColor(this.color);
		effect.motionX = speed.x;
		effect.motionY = speed.y;
		effect.motionZ = speed.z;
		addEffect(effect);
	}

	@Override
	protected void renderHeadTail(double len, float a, float partialTicks) {
		float r = EventClient.getGlobalRotateInRender(partialTicks);
		float scale = (float) MathSupporter.easeOutBack(a);
		GlStateManager.color(magicCircleColor.r, magicCircleColor.g, magicCircleColor.b, a / 0.75f);
		GlStateManager.translate(0, 0, -0.05);
		GlStateManager.scale(scale, scale, 1);
		GlStateManager.rotate(r, 0, 0, 1);

		EffectMagicEmit.TEXTURE.bind();
		RenderFriend.drawTextureRectInCenter(0, 0, 1, 1);
		TextureBinder.bindTexture(RenderObjects.MAGIC_CIRCLE_SUMMON);
		GlStateManager.rotate(-r * 2, 0, 0, 1);
		RenderFriend.drawTextureRectInCenter(0, 0, 0.4f, 0.4f);

		scale = 1 / scale;
		GlStateManager.scale(scale, scale, 1);
	}
}
