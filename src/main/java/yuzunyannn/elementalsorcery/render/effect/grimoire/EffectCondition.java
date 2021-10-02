package yuzunyannn.elementalsorcery.render.effect.grimoire;

import java.util.function.Function;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.Effect;

@SideOnly(Side.CLIENT)
public abstract class EffectCondition extends Effect {

	public Function<Void, Boolean> condition;

	public EffectCondition(World world, Function<Void, Boolean> condition, Vec3d pos) {
		super(world, pos.x, pos.y, pos.z);
		this.condition = condition;
	}

	public EffectCondition(World world, Vec3d pos) {
		super(world, pos.x, pos.y, pos.z);
	}

	public EffectCondition(World world, Function<Void, Boolean> condition) {
		this(world, condition, new Vec3d(0, 0, 0));
	}

	public EffectCondition(World world) {
		this(world, new Vec3d(0, 0, 0));
	}

	public void setCondition(Function<Void, Boolean> condition) {
		this.condition = condition;
	}

	public Function<Void, Boolean> getCondition() {
		return condition;
	}

	public boolean isEnd() {
		return condition == null ? true : (condition.apply(null) ? false : true);
	}

	@Override
	public void onUpdate() {
		this.lifeTime = this.isEnd() ? 0 : 1;
	}

	public static class ConditionEntityAction implements Function<Void, Boolean> {
		public final Entity entity;
		public boolean isFinish = false;

		public ConditionEntityAction(Entity entity) {
			this.entity = entity;
		}

		@Override
		public Boolean apply(Void t) {
			if (isFinish) return false;
			if (this.entity instanceof EntityLivingBase)
				return !(isFinish = !((EntityLivingBase) entity).isHandActive());
			else return !(isFinish = entity.isDead);
		}
	}

	protected void renderTexRect(float x, float y, float width, float height, float u, float v, float texWidth,
			float texHeight, float textureWidth, float textureHeight, float r, float g, float b, float a, float anchorX,
			float anchorY) {

		float f = 1.0F / textureWidth;
		float f1 = 1.0F / textureHeight;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		float hw = width * anchorX;
		float hh = height * anchorY;

		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.pos(x - hw, y + hh, 0.0D).tex(u * f, (v + texHeight) * f1);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(x + (width - hw), y + hh, 0.0D).tex((u + texWidth) * f, (v + texHeight) * f1);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(x + (width - hw), y - (height - hh), 0.0D).tex((u + texWidth) * f, v * f1);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(x - hw, y - (height - hh), 0.0D).tex(u * f, v * f1);
		bufferbuilder.color(r, g, b, a).endVertex();
		tessellator.draw();
	}

	protected void renderTexRectInCenter(float x, float y, float width, float height, float u, float v, float texWidth,
			float texHeight, float textureWidth, float textureHeight, float r, float g, float b, float a) {

		float f = 1.0F / textureWidth;
		float f1 = 1.0F / textureHeight;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		float hw = width / 2;
		float hh = height / 2;

		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.pos(x - hw, y + hh, 0.0D).tex(u * f, (v + texHeight) * f1);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(x + hw, y + hh, 0.0D).tex((u + texWidth) * f, (v + texHeight) * f1);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(x + hw, y - hh, 0.0D).tex((u + texWidth) * f, v * f1);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(x - hw, y - hh, 0.0D).tex(u * f, v * f1);
		bufferbuilder.color(r, g, b, a).endVertex();
		tessellator.draw();
	}

	protected void renderTexRectInCenter(float x, float y, float width, float height, float r, float g, float b,
			float a) {
		renderTexRectInCenter(x, y, width, height, 0, 0, 1, 1, 1, 1, r, g, b, a);
	}

}
