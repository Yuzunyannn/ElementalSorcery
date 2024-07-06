package yuzunyannn.elementalsorcery.render.effect.batch;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.EffectBatchType;

@SideOnly(Side.CLIENT)
public class EffectTreat extends Effect {

	public final static TextureBinder TEXTURE = new TextureBinder("textures/effect/treat_flare.png");

	public final static EffectBatchType TREAT_BATCH_TYPE = new EffectBatchType() {

		@Override
		public void beginRender(Tessellator tessellator, BufferBuilder bufferbuilder) {
			TEXTURE.bind();
			bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		}

		@Override
		public void endRender(Tessellator tessellator, BufferBuilder bufferbuilder) {
			tessellator.draw();
		}

	};

	protected float r, g, b;
	public float alpha, prevAlpha;
	public float maxLifeTime;
	public float scale;
	public float scaleHeight = 2, prevScaleHeight = 2;

	public EffectTreat(World worldIn, double posXIn, double posYIn, double posZIn) {
		super(worldIn, posXIn, posYIn, posZIn);
		this.alpha = 0f;
		this.lifeTime = 30 + rand.nextInt(10);
		this.maxLifeTime = this.lifeTime;
		this.scale = rand.nextFloat() * 0.1f + 0.2f;
	}

	public void setColor(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public void setColor(int color) {
		this.r = ((color >> 16) & 0xff) / 255.0f;
		this.g = ((color >> 8) & 0xff) / 255.0f;
		this.b = ((color >> 0) & 0xff) / 255.0f;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.prevScaleHeight = this.scaleHeight;
		this.prevAlpha = this.alpha;
		this.alpha = MathHelper.sin(this.lifeTime / this.maxLifeTime * 3.1415f) * 0.75f;
		this.scaleHeight = MathHelper.cos((this.lifeTime / this.maxLifeTime - 1) * 3.1415f / 2) * 1 + 2;
	}

	@Override
	protected EffectBatchType typeBatch() {
		return TREAT_BATCH_TYPE;
	}

	@Override
	protected void doRender(float partialTicks) {

	}

	@Override
	protected void doRender(BufferBuilder bufferbuilder, float partialTicks) {
		double x = this.getRenderX(partialTicks);
		double y = this.getRenderY(partialTicks);
		double z = this.getRenderZ(partialTicks);
		doRender(bufferbuilder, x, y, z, partialTicks);
	}

	protected void doRender(BufferBuilder bufferbuilder, double x, double y, double z, float partialTicks) {
		float a = this.prevAlpha + (this.alpha - this.prevAlpha) * partialTicks;
		float sh = RenderFriend.getPartialTicks(scaleHeight, prevScaleHeight, partialTicks);

		float rX = ActiveRenderInfo.getRotationX();
		float rZ = ActiveRenderInfo.getRotationZ();
		float rXZ = ActiveRenderInfo.getRotationXZ();
		Vec3d v1 = new Vec3d(-rX, -rXZ, -rZ).scale(scale);
		Vec3d v2 = new Vec3d(-rX, rXZ, -rZ).scale(scale);
		Vec3d v3 = new Vec3d(rX, rXZ, rZ).scale(scale);
		Vec3d v4 = new Vec3d(rX, -rXZ, rZ).scale(scale);

		bufferbuilder.pos(x + v1.x, y + v1.y * sh, z + v1.z).tex(0, 0);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(x + v2.x, y + v2.y * sh, z + v2.z).tex(0, 1);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(x + v3.x, y + v3.y * sh, z + v3.z).tex(1, 1);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(x + v4.x, y + v4.y * sh, z + v4.z).tex(1, 0);
		bufferbuilder.color(r, g, b, a).endVertex();
	}

}
