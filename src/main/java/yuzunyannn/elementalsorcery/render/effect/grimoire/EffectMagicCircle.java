package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.EffectCondition;
import yuzunyannn.elementalsorcery.render.effect.IEffectBinder;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.util.helper.ColorHelper;

@SideOnly(Side.CLIENT)
public class EffectMagicCircle extends EffectCondition {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/magic_circles/element.png");
	public IEffectBinder binder;

	public float r = 0;
	public float g = 0;
	public float b = 0;

	public EffectMagicCircle(World world, IEffectBinder binder) {
		super(world);
		this.lifeTime = 1;
		this.binder = binder;
		this.setPosition(this.binder);
	}

	public void setPosition(IEffectBinder binder) {
		this.setPosition(binder.getPosition());
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
	public boolean isDead() {
		return this.lifeTime <= 0;
	}

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
			Vec3d vec = binder.getPosition();
			this.posX = vec.x;
			this.posY = vec.y;
			this.posZ = vec.z;
			this.alpha = Math.min(1, alpha + 0.05f);
			float size = 32 * scale * 0.8f;
			this.onAddEffect(this.getPositionVector(), size);
		}
		this.rotate += 1f;
	}

	protected void onAddEffect(Vec3d pos, float size) {
		float hSize = size / 2;
		pos = pos.add(rand.nextDouble() * size - hSize, 0.1, rand.nextDouble() * size - hSize);
		EffectElementMove effect = new EffectElementMove(world, pos);
		effect.setVelocity(0, 0.05f, 0);
		effect.setColor(r, g, b);
		Effect.addEffect(effect);
	}

	@Override
	protected void doRender(float partialTicks) {
		GlStateManager.pushMatrix();
		double posX = RenderFriend.getPartialTicks(this.posX, this.prevPosX, partialTicks);
		double posY = RenderFriend.getPartialTicks(this.posY, this.prevPosY, partialTicks);
		double posZ = RenderFriend.getPartialTicks(this.posZ, this.prevPosZ, partialTicks);
		GlStateManager.translate(posX, posY + 0.1f, posZ);
		this.doRotate(partialTicks);
		float scale = RenderFriend.getPartialTicks(this.scale, this.preScale, partialTicks);
		float alpha = RenderFriend.getPartialTicks(this.alpha, this.preAlpha, partialTicks);
		GlStateManager.scale(scale, scale, scale);
		this.renderCircle(partialTicks, alpha);
		this.renderCenterIcon(partialTicks, alpha);
		GlStateManager.popMatrix();
	}

	protected void doRotate(float partialTicks) {
		GlStateManager.rotate(90, 1, 0, 0);
		float rotate = RenderFriend.getPartialTicks(this.rotate, this.preRotate, partialTicks);
		GlStateManager.rotate(rotate, 0, 0, 1);
	}

	protected void renderCircle(float partialTicks, float alpha) {
		TEXTURE.bind();
		this.renderTexRectInCenter(0, 0, 32, 32, r, g, b, alpha);
	}

	protected void renderCenterIcon(float partialTicks, float alpha) {

	}

}
