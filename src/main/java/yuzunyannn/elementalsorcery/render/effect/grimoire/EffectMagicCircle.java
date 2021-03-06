package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.IBinder;
import yuzunyannn.elementalsorcery.render.effect.element.EffectElementMove;
import yuzunyannn.elementalsorcery.util.ColorHelper;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class EffectMagicCircle extends EffectCondition {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/magic_circles/element.png");
	public IBinder binder;

	public float r = 0;
	public float g = 0;
	public float b = 0;

	public EffectMagicCircle(World world, Entity binder) {
		super(world);
		this.lifeTime = 1;
		this.binder = new IBinder.EntityBinder(binder, 0);
		this.setPosition(this.binder);
	}

	public EffectMagicCircle(World world, BlockPos pos) {
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
		pos = pos.addVector(rand.nextDouble() * size - hSize, 0.1, rand.nextDouble() * size - hSize);
		EffectElementMove effect = new EffectElementMove(world, pos);
		effect.g = 0;
		effect.setVelocity(0, 0.05f, 0);
		effect.setColor(r, g, b);
		Effect.addEffect(effect);
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
		this.bindCircleIcon();
		this.renderTexRectInCenter(0, 0, 32, 32, partialTicks, r, g, b, alpha);
		this.renderCenterIcon(partialTicks, alpha);
		GlStateManager.popMatrix();
	}

	protected void bindCircleIcon() {
		TEXTURE.bind();
	}

	protected void renderCenterIcon(float partialTicks, float alpha) {

	}

}
