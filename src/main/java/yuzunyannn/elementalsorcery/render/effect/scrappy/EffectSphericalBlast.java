package yuzunyannn.elementalsorcery.render.effect.scrappy;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.tile.RenderTileElementReactor;
import yuzunyannn.elementalsorcery.util.MathSupporter;
import yuzunyannn.elementalsorcery.util.helper.Color;

@SideOnly(Side.CLIENT)
public class EffectSphericalBlast extends Effect {

	public final static TextureBinder TEXTURE_0 = new TextureBinder("textures/effect/spherical_blast_ring.png");
	public final static TextureBinder TEXTURE_1 = new TextureBinder("textures/effect/spherical_blast.png");
	public final static TextureBinder TEXTURE_2 = new TextureBinder("textures/effect/spherical_blast_1.png");

	protected int tick;

	public Color color = new Color();
	public float size, prevSize;
	public int maxLifeTime = 0;
	public float maxSize;

	public EffectSphericalBlast(World worldIn, Vec3d vec, float maxSize) {
		super(worldIn, vec.x, vec.y, vec.z);
		this.maxLifeTime = lifeTime = 80;
		this.maxSize = maxSize;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.tick++;
		this.prevSize = this.size;
		float r = (maxLifeTime - lifeTime) / (float) maxLifeTime;
		if (r <= 0.4) size = (float) MathSupporter.easeInOutElastic(r / 0.4f) * this.maxSize;
		else if (r >= 0.9) size = (float) MathSupporter.easeOutBack((1 - r) / 0.1f) * this.maxSize;
		else size = this.maxSize;
	}

	@Override
	protected void doRender(float partialTicks) {
		double x = this.getRenderX(partialTicks);
		double y = this.getRenderY(partialTicks);
		double z = this.getRenderZ(partialTicks);
		float size = this.prevSize + (this.size - this.prevSize) * partialTicks;

		float dtick = (this.tick + partialTicks) * 0.1f;
		float ration = (MathHelper.sin(dtick) + MathHelper.cos(dtick) + 2) / 4 * size / maxSize;
		float a = ration * 0.8f + 0.2f;
		float r = color.r;
		float g = color.g;
		float b = color.b;
		float ringSize = size * 1.2f;

		TEXTURE_0.bind();
		float rX = ActiveRenderInfo.getRotationX();
		float rZ = ActiveRenderInfo.getRotationZ();
		float rYZ = ActiveRenderInfo.getRotationYZ();
		float rXY = ActiveRenderInfo.getRotationXY();
		float rXZ = ActiveRenderInfo.getRotationXZ();
		Vec3d v1 = new Vec3d(-rX - rYZ, -rXZ, -rZ - rXY).scale(ringSize);
		Vec3d v2 = new Vec3d(-rX + rYZ, rXZ, -rZ + rXY).scale(ringSize);
		Vec3d v3 = new Vec3d(rX + rYZ, rXZ, rZ + rXY).scale(ringSize);
		Vec3d v4 = new Vec3d(rX - rYZ, -rXZ, rZ - rXY).scale(ringSize);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.pos(x + v1.x, y + v1.y, z + v1.z).tex(0, 0);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(x + v2.x, y + v2.y, z + v2.z).tex(0, 1);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(x + v3.x, y + v3.y, z + v3.z).tex(1, 1);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(x + v4.x, y + v4.y, z + v4.z).tex(1, 0);
		bufferbuilder.color(r, g, b, a).endVertex();
		tessellator.draw();

		float rotate = EventClient.getGlobalRotateInRender(partialTicks) * 12;
		GlStateManager.depthMask(true);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.scale(size, size, size);
		GlStateManager.rotate(rotate, 0, 1, 0);
		GlStateManager.color(0, 0, 0, 1f);
		GlStateManager.disableTexture2D();
		RenderTileElementReactor.MODEL_SPHERE.render();
		GlStateManager.enableTexture2D();
		GlStateManager.color(r, g, b, a);
		TEXTURE_1.bind();
		RenderTileElementReactor.MODEL_SPHERE.render();
		GlStateManager.rotate(-rotate * 0.5f, 0, 1, 0);
		GlStateManager.color(r, g, b, a - 0.1f);
		GlStateManager.scale(1.01, 1.01, 1.01);
		TEXTURE_2.bind();
		RenderTileElementReactor.MODEL_SPHERE.render();
		GlStateManager.popMatrix();
		GlStateManager.depthMask(false);
	}

}
