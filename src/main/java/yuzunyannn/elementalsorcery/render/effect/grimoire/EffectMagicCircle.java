package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.EffectElementMove;
import yuzunyannn.elementalsorcery.util.ColorHelper;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class EffectMagicCircle extends EffectCondition {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/magic_circles/element.png");
	public Entity binder;

	public float r = 0;
	public float g = 0;
	public float b = 0;

	public EffectMagicCircle(World world, Entity binder) {
		super(world);
		this.lifeTime = 1;
		this.binder = binder;
		this.setPosition(binder);
	}

	public void setColor(int color) {
		Vec3d c = ColorHelper.color(color);
		r = (float) c.x;
		g = (float) c.y;
		b = (float) c.z;
	}

	float rotate;
	float preRotate;
	float scale;
	float preScale = scale;
	float alpha = 1;
	float preAlpha = alpha;

	@Override
	public void onUpdate() {
		this.preRotate = this.rotate;
		this.preScale = this.scale;
		this.preAlpha = this.alpha;
		this.scale = Math.min(0.2f, scale + 0.03f);
		if (isEnd()) {
			this.lifeTime--;
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			this.alpha = Math.max(0, alpha - 0.05f);
		} else {
			this.lifeTime = 20;
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			this.posX = binder.posX;
			this.posY = binder.posY;
			this.posZ = binder.posZ;
			this.alpha = Math.min(1, alpha + 0.05f);
			float size = 32 * scale * 0.8f;
			float hSize = size / 2;
			Vec3d pos = this.getPositionVector().addVector(rand.nextDouble() * size - hSize, 0.1,
					rand.nextDouble() * size - hSize);
			EffectElementMove effect = new EffectElementMove(world, pos);
			effect.g = 0;
			effect.setVelocity(0, 0.05f, 0);
			effect.setColor(r, g, b);
			Effect.addEffect(effect);
		}
		this.rotate += 1f;
	}

	@Override
	protected void doRender(float partialTicks) {
		GlStateManager.pushMatrix();
		double posX = RenderHelper.getPartialTicks(this.posX, this.prevPosX, partialTicks);
		double posY = RenderHelper.getPartialTicks(this.posY, this.prevPosY, partialTicks);
		double posZ = RenderHelper.getPartialTicks(this.posZ, this.prevPosZ, partialTicks);
		GlStateManager.translate(posX, posY + 0.1f, posZ);
		GlStateManager.rotate(90, 1, 0, 0);
		float rotate = RenderHelper.getPartialTicks(this.rotate, this.preRotate, partialTicks);
		GlStateManager.rotate(rotate, 0, 0, 1);
		float scale = RenderHelper.getPartialTicks(this.scale, this.preScale, partialTicks);
		float alpha = RenderHelper.getPartialTicks(this.alpha, this.preAlpha, partialTicks);
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.color(r, g, b, alpha);
		TEXTURE.bind();
		RenderHelper.drawTexturedRectInCenter(0, 0, 32, 32);
		this.renderCenterIcon(partialTicks);
		GlStateManager.popMatrix();
	}

	protected void renderCenterIcon(float partialTicks) {

	}

}
