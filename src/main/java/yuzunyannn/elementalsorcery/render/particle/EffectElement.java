package yuzunyannn.elementalsorcery.render.particle;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class EffectElement extends Effect {

	private TextureBinder TEXTURE = new TextureBinder("textures/particle/element_flare.png");

	protected float r, g, b;
	protected float alpha, prevAlpha;
	protected float dalpha;
	protected float scale;

	protected double motionX;
	protected double motionY;
	protected double motionZ;

	public EffectElement(World worldIn, double posXIn, double posYIn, double posZIn) {
		super(worldIn, posXIn, posYIn, posZIn);
		this.alpha = 1.0f;
		this.dalpha = 1.0f / this.lifeTime;
		this.scale = rand.nextFloat() * 0.25f + 0.1f;
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
		this.prevAlpha = this.alpha;
		this.alpha -= this.dalpha;
		this.posY -= 0.002;
	}

	@Override
	void doRender(float partialTicks) {
		double x = this.getRenderX(partialTicks);
		double y = this.getRenderY(partialTicks);
		double z = this.getRenderZ(partialTicks);
		float a = this.prevAlpha + (this.alpha - this.prevAlpha) * partialTicks;
		TEXTURE.bind();
		float rX = ActiveRenderInfo.getRotationX();
		float rZ = ActiveRenderInfo.getRotationZ();
		float rYZ = ActiveRenderInfo.getRotationYZ();
		float rXY = ActiveRenderInfo.getRotationXY();
		float rXZ = ActiveRenderInfo.getRotationXZ();
		Vec3d v1 = new Vec3d(-rX - rYZ, -rXZ, -rZ - rXY).scale(scale);
		Vec3d v2 = new Vec3d(-rX + rYZ, rXZ, -rZ + rXY).scale(scale);
		Vec3d v3 = new Vec3d(rX + rYZ, rXZ, rZ + rXY).scale(scale);
		Vec3d v4 = new Vec3d(rX - rYZ, -rXZ, rZ - rXY).scale(scale);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.color(r, g, b, a);
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x + v1.x, y + v1.y, z + v1.z).tex(0, 0).endVertex();
		bufferbuilder.pos(x + v2.x, y + v2.y, z + v2.z).tex(0, 1).endVertex();
		bufferbuilder.pos(x + v3.x, y + v3.y, z + v3.z).tex(1, 1).endVertex();
		bufferbuilder.pos(x + v4.x, y + v4.y, z + v4.z).tex(1, 0).endVertex();
		tessellator.draw();
	}

}
