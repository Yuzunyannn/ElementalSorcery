package yuzunyannn.elementalsorcery.render.effect.scrappy;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.render.effect.Effect;

@SideOnly(Side.CLIENT)
public class EffectResonance extends Effect {

	public final static TextureBinder TEXTURE = new TextureBinder("textures/effect/resonance.png");

	protected float r, g, b;
	protected float alpha, prevAlpha;
	protected float size, prevSize;
	protected float dalpha;

	protected double motionX;
	protected double motionY;
	protected double motionZ;

	public EffectResonance(World worldIn, double posXIn, double posYIn, double posZIn) {
		super(worldIn, posXIn, posYIn, posZIn);
		this.lifeTime = 15 + rand.nextInt(5);
		this.alpha = 1.0f;
		this.dalpha = 1.0f / this.lifeTime;
		this.size = rand.nextFloat() * 0.2f + 0.1f;
	}

	public void setColor(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public EffectResonance setColor(int color) {
		this.r = ((color >> 16) & 0xff) / 255.0f;
		this.g = ((color >> 8) & 0xff) / 255.0f;
		this.b = ((color >> 0) & 0xff) / 255.0f;
		return this;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.prevAlpha = this.alpha;
		this.prevSize = this.size;
		this.alpha -= this.dalpha;
		this.size += 0.5f;
	}

	@Override
	protected void doRender(float partialTicks) {
		double x = this.getRenderX(partialTicks);
		double y = this.getRenderY(partialTicks);
		double z = this.getRenderZ(partialTicks);
		float a = this.prevAlpha + (this.alpha - this.prevAlpha) * partialTicks;
		float size = this.prevSize + (this.size - this.prevSize) * partialTicks;
		TEXTURE.bind();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.color(r, g, b, a);
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x - size, y, z - size).tex(0, 0).endVertex();
		bufferbuilder.pos(x + size, y, z - size).tex(0, 1).endVertex();
		bufferbuilder.pos(x + size, y, z + size).tex(1, 1).endVertex();
		bufferbuilder.pos(x - size, y, z + size).tex(1, 0).endVertex();
		tessellator.draw();
	}

}
