package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.EffectCondition;
import yuzunyannn.elementalsorcery.render.effect.IBinder;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.util.helper.ColorHelper;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class EffectTimeHourglass extends EffectCondition {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/magic_circles/time_hourglass.png");
	public static final TextureBinder TEXTURE_COMPASS = new TextureBinder(
			"textures/magic_circles/time_hourglass_compass.png");
	public static final TextureBinder TEXTURE_POINTER = new TextureBinder(
			"textures/magic_circles/time_hourglass_pointer.png");

	public IBinder binder;

	public float r = 0;
	public float g = 0;
	public float b = 0;

	public EffectTimeHourglass(World world, Entity binder) {
		super(world);
		this.lifeTime = 1;
		this.binder = new IBinder.EntityBinder(binder, 0);
		this.setPosition(this.binder);
	}

	public EffectTimeHourglass(World world, BlockPos pos) {
		super(world);
		this.lifeTime = 1;
		this.binder = new IBinder.VecBinder(new Vec3d(pos).add(0.5, 0, 0.5));
		this.setPosition(this.binder);
	}

	public void setPosition(IBinder binder) {
		this.setPosition(binder.getPosition());
	}

	public void setColor(int color) {
		Vec3d c = ColorHelper.color(color);
		r = (float) c.x;
		g = (float) c.y;
		b = (float) c.z;
	}

	public float rotate;
	public float preRotate;

	public float scale;
	public float preScale = scale;

	public float alpha = 1;
	public float preAlpha = alpha;

	public float nextPotinerRate;
	public float potinerRate;
	public float prePotinerRate;

	@Override
	public void onUpdate() {
		this.preRotate = this.rotate;
		this.preScale = this.scale;
		this.preAlpha = this.alpha;
		this.prePotinerRate = this.potinerRate;
		this.potinerRate = this.nextPotinerRate;

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
			float size = 64 * scale * 0.8f;
			onAddEffect(vec, size);
		}
		this.rotate += 1f * (1 - this.potinerRate);
	}

	protected void onAddEffect(Vec3d pos, float size) {
		float hSize = size / 2;
		int count = (int) (8 * (1 - this.potinerRate));
		for (int i = 0; i < count; i++) {
			pos = pos.add(rand.nextGaussian() * hSize, 0.1, rand.nextGaussian() * hSize);
			EffectElementMove effect = new EffectElementMove(world, pos);
			effect.setVelocity(0, 0.05f, 0);
			effect.setColor(r, g, b);
			Effect.addEffect(effect);
		}
	}

	@Override
	protected void doRender(float partialTicks) {
		GlStateManager.pushMatrix();

		double posX = RenderHelper.getPartialTicks(this.posX, this.prevPosX, partialTicks);
		double posY = RenderHelper.getPartialTicks(this.posY, this.prevPosY, partialTicks);
		double posZ = RenderHelper.getPartialTicks(this.posZ, this.prevPosZ, partialTicks);
		float scale = RenderHelper.getPartialTicks(this.scale, this.preScale, partialTicks);
		float alpha = RenderHelper.getPartialTicks(this.alpha, this.preAlpha, partialTicks);
		float rotate = RenderHelper.getPartialTicks(this.rotate, this.preRotate, partialTicks);
		float point = RenderHelper.getPartialTicks(this.potinerRate, this.prePotinerRate, partialTicks);

		GlStateManager.translate(posX, posY + 0.1f, posZ);
		GlStateManager.rotate(90, 1, 0, 0);
		GlStateManager.scale(scale, scale, scale);

		GlStateManager.rotate(rotate, 0, 0, 1);
		TEXTURE.bind();
		this.renderTexRectInCenter(0, 0, 128, 128, r, g, b, alpha);
		GlStateManager.rotate(-rotate, 0, 0, 1);

		TEXTURE_COMPASS.bind();
		this.renderTexRectInCenter(0, 0, 68, 68, r, g, b, alpha);

		TEXTURE_POINTER.bind();
		GlStateManager.rotate(360 * point, 0, 0, 1);
		this.renderTexRect(0, 0, 14, 30, 0, 0, 14, 30, 64, 64, r, g, b, alpha, 0.5f, 0.125f);
		GlStateManager.rotate(360 * point, 0, 0, 1);
		this.renderTexRect(0, 0, 14, 41, 0, 0, 14, 41, 64, 64, r, g, b, alpha, 0.5f, 0.2f);

		GlStateManager.popMatrix();
	}

}
