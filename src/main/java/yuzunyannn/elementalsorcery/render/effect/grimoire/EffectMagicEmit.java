package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.IBinder;
import yuzunyannn.elementalsorcery.util.helper.ColorHelper;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class EffectMagicEmit extends EffectCondition {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/magic_circles/emit.png");
	public IBinder binder;

	public float r = 0;
	public float g = 0;
	public float b = 0;

	public EffectMagicEmit(World world, Entity binder) {
		super(world);
		this.lifeTime = 1;
		this.binder = new IBinder.EntityBinder(binder, binder.getEyeHeight());
		this.setPosition(this.binder);
	}

	public EffectMagicEmit(World world, BlockPos pos) {
		super(world);
		this.lifeTime = 1;
		this.binder = new IBinder.VecBinder(new Vec3d(pos).addVector(0.5, 0, 0.5));
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
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if (isEnd()) {
			this.lifeTime--;

			this.alpha = Math.max(0, alpha - 0.1f);
			this.scale = Math.min(0.2f, scale + 0.01f);
		} else {
			this.lifeTime = 10;
			Vec3d vec = binder.getPosition();
			Vec3d look = binder.getDirection().scale(1);
			this.posX = vec.x + look.x;
			this.posY = vec.y + look.y;
			this.posZ = vec.z + look.z;
			this.alpha = Math.min(1, alpha + 0.05f);
			this.scale = Math.min(0.1f, scale + 0.02f);
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
		// GlStateManager.rotate(90, 1, 0, 0);
		// GlStateManager.rotate(rotate, 0, 0, 1);
		float rotate = RenderHelper.getPartialTicks(this.rotate, this.preRotate, partialTicks);
		float scale = RenderHelper.getPartialTicks(this.scale, this.preScale, partialTicks);
		float alpha = RenderHelper.getPartialTicks(this.alpha, this.preAlpha, partialTicks);
		GlStateManager.scale(scale, scale, scale);

		Vec3d vec = binder.getDirection();

		GlStateManager.rotate((float) MathHelper.atan2(vec.x, vec.z) / 3.14f * 180, 0, 1, 0);
		GlStateManager.rotate((float) -vec.y * 90, 1, 0, 0);
		GlStateManager.rotate(rotate, 0, 0, 1);

		TEXTURE.bind();
		this.renderTexRectInCenter(0, 0, 32, 32, r, g, b, alpha);

		GlStateManager.popMatrix();
	}

}
