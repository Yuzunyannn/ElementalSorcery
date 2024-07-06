package yuzunyannn.elementalsorcery.render.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.container.gui.reactor.GuiElementReactor;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.logics.IRenderClient;
import yuzunyannn.elementalsorcery.tile.altar.TileElementReactor;
import yuzunyannn.elementalsorcery.tile.altar.TileElementReactor.ReactorStatus;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.render.ModelSphere;

@SideOnly(Side.CLIENT)
public class RenderTileElementReactor extends TileEntitySpecialRenderer<TileElementReactor> implements IRenderItem {

	public static final ModelSphere MODEL_SPHERE = new ModelSphere();
	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/element_reactor.png");

	public static final ModelBase MODEL = new ModelBase() {
		public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
				float headPitch, float scale) {
			MODEL_SPHERE.render();
		};
	};

	public RenderTileElementReactor() {

	}

	@Override
	public void render(TileElementReactor tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		TileElementReactor.ReactorStatus status = tile.getStatus();
		tile.isInRender = true;

		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
		RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRender(x + 0.5, y + 0.5, z + 0.5, 0.5f, alpha);

		GlStateManager.disableCull();

		if (status != ReactorStatus.OFF) {
			float rotation = EventClient.getGlobalRotateInRender(partialTicks);
			GlStateManager.rotate(rotation, 0, 1, 0);
		}

		MODEL_SPHERE.render();

		GlStateManager.enableCull();

		RenderFriend.endTileEntitySpecialRender();
		RenderFriend.bindDestoryTextureEnd(destroyStage);

		if (destroyStage > 0) return;
		if (status == ReactorStatus.OFF) return;

		EventClient.addRenderTask((p) -> {
			BlockPos pos = tile.getPos();
			double xoff = pos.getX(), yoff = pos.getY(), zoff = pos.getZ();
			GlStateManager.disableCull();
			GlStateManager.disableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.depthMask(false);
			GlStateManager.disableLighting();
			RenderFriend.disableLightmap(true);

			if (status.isRunning) renderEffectOn(tile, xoff, yoff, zoff, partialTicks);
			else if (status == ReactorStatus.STANDBY) renderEffectStandby(tile, xoff, yoff, zoff, partialTicks);

			GlStateManager.enableLighting();
			RenderFriend.disableLightmap(false);
			GlStateManager.enableCull();
			GlStateManager.enableAlpha();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
			return IRenderClient.END;
		});

	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.disableCull();
		GlStateManager.color(0.691f, 0.980f, 0.992f);
		RenderFriend.renderSpecialItem(stack, TEXTURE, MODEL, true, 0.4, 0.175, 0.3, 0.15);
		GlStateManager.enableCull();
	}

	public void renderEffectStandby(TileElementReactor tile, double x, double y, double z, float partialTicks) {
		GuiElementReactor.COMS.bind();
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5, y + 0.5f, z + 0.5);

		float s = 0.1f;
		float r2 = EventClient.getGlobalRotateInRender(partialTicks);

		GlStateManager.rotate(-r2, 0, 1, 0);
		GlStateManager.rotate(r2, 0, 0, 1);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		for (int i = 0; i < GuiElementReactor.fcolors.length; i++) {
			// 38, 54, 256, 256
			Color color = GuiElementReactor.fcolors[i];
			GlStateManager.rotate(90, 0, 0, 1);
			float r = color.r, g = color.g, b = color.b, a = 1;
			bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			bufferbuilder.pos(-s * 2, -s, 0).tex(0, 0);
			bufferbuilder.color(r, g, b, a).endVertex();
			bufferbuilder.pos(0, -s, 0).tex(0, 54 / 256f);
			bufferbuilder.color(r, g, b, a).endVertex();
			bufferbuilder.pos(0, s, 0).tex(38 / 256f, 54 / 256f);
			bufferbuilder.color(r, g, b, a).endVertex();
			bufferbuilder.pos(-s * 2, s, 0).tex(38 / 256f, 0);
			bufferbuilder.color(r, g, b, a).endVertex();
			tessellator.draw();
		}

		GlStateManager.popMatrix();
	}

	public void renderEffectOn(TileElementReactor tile, double x, double y, double z, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5, y + 0.5f, z + 0.5);

		float r2 = EventClient.getGlobalRotateInRender(partialTicks);
		Color color = tile.getRenderColor();
		Color color2 = null;
		if (tile.getRunningMantraPair() != null) color2 = new Color(tile.getRunningMantraPair().mantra.getColor(null));

		float l = (r2 / 160) % 1;
		if (l < 0.25) l = 0;
		else {
			GlStateManager.pushMatrix();
			l = (l - 0.25f) / 0.75f;
			float a = MathHelper.sin(l * 3.1415926f);
			GlStateManager.translate(0, MathHelper.sin(-l * 3.1415926f / 2) * 0.5f, 0);
			renderCircle(-r2, color, 0.5f * MathHelper.cos(-l * 3.1415926f / 2), a);
			GlStateManager.translate(0, MathHelper.sin(l * 3.1415926f / 2) * 0.5f * 2, 0);
			renderCircle(-r2, color, 0.5f * MathHelper.cos(l * 3.1415926f / 2), a);
			if (color2 != null) {
				GlStateManager.translate(0, MathHelper.sin(-l * 3.1415926f / 2) * 0.5f * 2, 0);
				renderCircle(-r2, color2, 0.75f * MathHelper.cos(-l * 3.1415926f / 2), a);
				GlStateManager.translate(0, MathHelper.sin(l * 3.1415926f / 2) * 0.5f * 2, 0);
				renderCircle(-r2, color2, 0.75f * MathHelper.cos(l * 3.1415926f / 2), a);
			}
			GlStateManager.popMatrix();
		}

		renderCircle(-r2, color, 0.75f, 1);

		GlStateManager.popMatrix();
	}

	public static void renderCircle(float rotation, Color color, float size, float aplha) {
		renderCircle(rotation, color, size, aplha, false);
	}

	public static void renderCircle(float rotation, Color color, float size, float aplha, boolean changeDepth) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();

		GlStateManager.rotate(rotation, 0, 1, 0);
		GuiElementReactor.HALO.bind();
		Color colorw = color.copy().weight(new Color(0xffffff), 0.8f);
		float a = 0.9f + 0.1f * MathHelper.sin(rotation / 180 * 3.14f * 10);
		GlStateManager.color(colorw.r, colorw.g, colorw.b, a * aplha);
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(-size, 0, -size).tex(0, 0).endVertex();
		bufferbuilder.pos(size, 0, -size).tex(0, 1).endVertex();
		bufferbuilder.pos(size, 0, size).tex(1, 1).endVertex();
		bufferbuilder.pos(-size, 0, size).tex(1, 0).endVertex();
		tessellator.draw();

		if (changeDepth) {
			GlStateManager.enableAlpha();
			GlStateManager.depthMask(true);
		}

		GuiElementReactor.RING.bind();
		GlStateManager.color(color.r, color.g, color.b, aplha);
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(-size, -0.01, -size).tex(0, 0).endVertex();
		bufferbuilder.pos(size, -0.01, -size).tex(0, 1).endVertex();
		bufferbuilder.pos(size, -0.01, size).tex(1, 1).endVertex();
		bufferbuilder.pos(-size, -0.01, size).tex(1, 0).endVertex();
		bufferbuilder.pos(-size, 0.01, -size).tex(0, 0).endVertex();
		bufferbuilder.pos(size, 0.01, -size).tex(0, 1).endVertex();
		bufferbuilder.pos(size, 0.01, size).tex(1, 1).endVertex();
		bufferbuilder.pos(-size, 0.01, size).tex(1, 0).endVertex();
		tessellator.draw();

		if (changeDepth) {
			GlStateManager.disableAlpha();
			GlStateManager.depthMask(false);
		}

		GlStateManager.rotate(-rotation, 0, 1, 0);
	}

}
