package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.EffectCondition;
import yuzunyannn.elementalsorcery.render.effect.IEffectBinder;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.util.helper.Color;

@SideOnly(Side.CLIENT)
public class EffectMagicSquareNoEntry extends EffectCondition {

	public IEffectBinder binder;

	public final Color color = new Color();

	public float size = 8;
	public float hight = 3;

	public EffectMagicSquareNoEntry(World world, IEffectBinder binder, float size, int color) {
		super(world);
		this.lifeTime = 1;
		this.binder = binder;
		this.size = size;
		this.color.setColor(color);
		this.setPosition(binder.getPosition());
	}

	float alpha = 0;
	float preAlpha = alpha;

	@Override
	public void onUpdate() {
		this.preAlpha = this.alpha;
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.setPosition(binder.getPosition());
		if (isEnd()) {
			if (alpha <= 0) this.lifeTime = 0;
			else this.alpha = Math.max(this.alpha - 0.1f, 0);
		} else {
			this.lifeTime = 20;
			this.alpha = Math.min(this.alpha + 0.1f, 1);

			int times = (int) (size / 16 + 1);
			for (int i = 0; i < times; i++) {
				float hSize = size / 2 + 0.01f;
				double rLength = rand.nextDouble() * hSize * 2 - hSize;
				double cLength = rand.nextBoolean() ? hSize : -hSize;
				Vec3d pos = rand.nextBoolean() ? new Vec3d(rLength, 0, cLength) : new Vec3d(cLength, 0, rLength);
				EffectElementMove effect = new EffectElementMove(world, this.getPositionVector().add(pos));
				effect.setVelocity(0, 0.15f * hSize, 0);
				effect.yDecay = 1f - MathHelper.sqrt(hSize) * 0.07f;
				effect.setColor(color);
				Effect.addEffect(effect);
			}
		}
	}

	@Override
	public boolean isDead() {
		return this.lifeTime <= 0;
	}

	@Override
	protected void doRender(float partialTicks) {
		GlStateManager.pushMatrix();
		double posX = RenderFriend.getPartialTicks(this.posX, this.prevPosX, partialTicks);
		double posY = RenderFriend.getPartialTicks(this.posY, this.prevPosY, partialTicks);
		double posZ = RenderFriend.getPartialTicks(this.posZ, this.prevPosZ, partialTicks);
		float a = RenderFriend.getPartialTicks(this.alpha, this.preAlpha, partialTicks) * 0.25f;
		float rr = EventClient.getGlobalRotateInRender(partialTicks);
		a = a * 0.75f * ((MathHelper.sin(rr / 10f) + 1) / 2) + a * 0.25f;
		GlStateManager.translate(posX, posY + 0.05f, posZ);
		GlStateManager.disableTexture2D();

		float r = this.color.r;
		float g = this.color.g;
		float b = this.color.b;

		float hs = this.size / 2;
		float h = this.hight;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);

		bufferbuilder.pos(hs, -h, hs).color(r, g, b, a).endVertex();
		bufferbuilder.pos(hs, h, hs).color(r, g, b, a).endVertex();
		bufferbuilder.pos(-hs, h, hs).color(r, g, b, a).endVertex();
		bufferbuilder.pos(-hs, -h, hs).color(r, g, b, a).endVertex();

		bufferbuilder.pos(-hs, -h, -hs).color(r, g, b, a).endVertex();
		bufferbuilder.pos(-hs, h, -hs).color(r, g, b, a).endVertex();
		bufferbuilder.pos(hs, h, -hs).color(r, g, b, a).endVertex();
		bufferbuilder.pos(hs, -h, -hs).color(r, g, b, a).endVertex();

		bufferbuilder.pos(hs, -h, -hs).color(r, g, b, a).endVertex();
		bufferbuilder.pos(hs, h, -hs).color(r, g, b, a).endVertex();
		bufferbuilder.pos(hs, h, hs).color(r, g, b, a).endVertex();
		bufferbuilder.pos(hs, -h, hs).color(r, g, b, a).endVertex();

		bufferbuilder.pos(-hs, -h, hs).color(r, g, b, a).endVertex();
		bufferbuilder.pos(-hs, h, hs).color(r, g, b, a).endVertex();
		bufferbuilder.pos(-hs, h, -hs).color(r, g, b, a).endVertex();
		bufferbuilder.pos(-hs, -h, -hs).color(r, g, b, a).endVertex();

		tessellator.draw();

		GlStateManager.enableTexture2D();
		GlStateManager.popMatrix();

	}

}
