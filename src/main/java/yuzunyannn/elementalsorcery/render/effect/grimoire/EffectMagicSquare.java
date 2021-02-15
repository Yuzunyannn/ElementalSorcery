package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.element.EffectElementMove;
import yuzunyannn.elementalsorcery.util.ColorHelper;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class EffectMagicSquare extends EffectCondition {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/magic_circles/square.png");
	public Entity binder;
	public ResourceLocation icon;

	public float r = 0;
	public float g = 0;
	public float b = 0;

	public float size = 8;

	public Vec3d[] effectColors = null;

	public EffectMagicSquare(World world, Entity binder, float size, int color) {
		super(world);
		this.lifeTime = 1;
		this.binder = binder;
		this.size = size;
		Vec3d c = ColorHelper.color(color);
		r = (float) c.x;
		g = (float) c.y;
		b = (float) c.z;
		this.setPosition(binder);
	}

	public void setIcon(ResourceLocation icon) {
		this.icon = icon;
	}

	float alpha = 0;
	float preAlpha = alpha;

	float eScale = 1;
	float ePreScale = 1;
	float eAlpha = 0;
	float ePreAlpha = eAlpha;

	@Override
	public void onUpdate() {
		this.preAlpha = this.alpha;
		ePreScale = eScale;
		ePreAlpha = eAlpha;
		if (isEnd()) {
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			if (eAlpha < 0) {
				ePreScale = eScale = 1;
				ePreAlpha = eAlpha = 0.75f;
			} else {
				this.alpha = eAlpha;
				eAlpha = eAlpha - 0.02f;
				eScale = eScale + 0.0075f;
				if (eAlpha <= 0) {
					eAlpha = 0;
					this.lifeTime = 0;
				}
			}
		} else {
			this.lifeTime = 20;
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			this.posX = binder.posX;
			this.posY = binder.posY;
			this.posZ = binder.posZ;
			this.alpha += (1 - alpha) * 0.1f;

			if (eAlpha <= 0) {
				eAlpha = eAlpha - 0.01f;
				if (eAlpha < -0.2) {
					ePreScale = eScale = 1;
					ePreAlpha = eAlpha = 0.75f;
				}
			} else {
				eAlpha = eAlpha - 0.02f;
				eScale = eScale + 0.0075f;
			}

			int times = (int) (size / 16 + 1);
			for (int i = 0; i < times; i++) {
				float r = this.r, g = this.g, b = this.b;
				if (effectColors != null && effectColors.length > 0) {
					Vec3d c = effectColors[rand.nextInt(effectColors.length)];
					r = (float) c.x;
					g = (float) c.y;
					b = (float) c.z;
				}
				float hSize = size / 2;
				Vec3d pos = this.getPositionVector().addVector(rand.nextDouble() * size - hSize, 0.1,
						rand.nextDouble() * size - hSize);
				EffectElementMove effect = new EffectElementMove(world, pos);
				effect.g = 0;
				effect.setVelocity(0, 0.05f, 0);
				effect.setColor(r, g, b);
				Effect.addEffect(effect);
			}
		}
	}

	@Override
	protected void doRender(float partialTicks) {
		GlStateManager.pushMatrix();
		double posX = RenderHelper.getPartialTicks(this.posX, this.prevPosX, partialTicks);
		double posY = RenderHelper.getPartialTicks(this.posY, this.prevPosY, partialTicks);
		double posZ = RenderHelper.getPartialTicks(this.posZ, this.prevPosZ, partialTicks);
		GlStateManager.translate(posX, posY + 0.1f, posZ);
		GlStateManager.rotate(90, 1, 0, 0);
		TEXTURE.bind();

		float alpha = RenderHelper.getPartialTicks(this.alpha, this.preAlpha, partialTicks);
		GlStateManager.scale(1, 1, 1);
		this.renderTexRectInCenter(0, 0, size, size, partialTicks, r, g, b, alpha);
		if (icon != null) {
			mc.getTextureManager().bindTexture(icon);
			this.renderTexRectInCenter(0, 0, size * 0.15f, size * 0.15f, partialTicks, r, g, b, alpha);
		}

		if (this.eAlpha >= 0) {
			TEXTURE.bind();
			float eAlpha = RenderHelper.getPartialTicks(this.eAlpha, this.ePreAlpha, partialTicks);
			float eScale = RenderHelper.getPartialTicks(this.eScale, this.ePreScale, partialTicks);
			GlStateManager.translate(0, 0, -eScale + 1);
			GlStateManager.scale(eScale, eScale, eScale);
			this.renderTexRectInCenter(0, 0, size, size, partialTicks, r, g, b, eAlpha);
			if (icon != null) {
				mc.getTextureManager().bindTexture(icon);
				this.renderTexRectInCenter(0, 0, size * 0.15f, size * 0.15f, partialTicks, r, g, b, eAlpha);
			}
		}

		GlStateManager.popMatrix();

	}

}
