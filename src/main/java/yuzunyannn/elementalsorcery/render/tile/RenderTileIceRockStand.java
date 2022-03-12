package yuzunyannn.elementalsorcery.render.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.model.ModelIceRockStand;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockStand;
import yuzunyannn.elementalsorcery.util.TextHelper;
import yuzunyannn.elementalsorcery.util.render.Framebuffer;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.Shaders;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class RenderTileIceRockStand extends TileEntitySpecialRenderer<TileIceRockStand> implements IRenderItem {

	static private Framebuffer frameBuff = null;

	static public Framebuffer getFrameBuff() {
		if (frameBuff == null) frameBuff = new Framebuffer(128, 128, false);
		return frameBuff;
	}

	static public final TextureBinder TEXTUREL_CRYSTAL = new TextureBinder(
			"textures/blocks/ice_rock/ice_rock_crystal.png");
	static public final TextureBinder TEXTUREL_CRYSTAL_FULL = new TextureBinder(
			"textures/blocks/ice_rock/ice_rock_crystal_full.png");
	static public final TextureBinder TEXTUREL_CRYSTAL_MASK = new TextureBinder("textures/blocks/ice_rock/could.png");

	static public final TextureBinder TEXTUREL = new TextureBinder("textures/blocks/ice_rock/ice_rock_stand.png");
	static public final ModelIceRockStand MODEL = new ModelIceRockStand();

	public RenderTileIceRockStand() {

	}

	@Override
	public void render(TileIceRockStand tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {

		RenderHelper.bindDestoryTexture(TEXTUREL, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderHelper.startRender(x + 0.5, y, z + 0.5, 0.0625, alpha);
		MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		RenderHelper.endRender();
		RenderHelper.bindDestoryTextureEnd(destroyStage);

		int count = tile.getLinkCount();
		if (count > 0 && destroyStage < 0) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y + 1 - 0.8536, z);
			float rotation = EventClient.getGlobalRotateInRender(partialTicks);
			GlStateManager.translate(0.5, 0.5, 0.5);
			GlStateManager.rotate(rotation / 3, 0, 1, 0);
			GlStateManager.translate(-0.5, -0.5, -0.5);
			double ratio = tile.getMagicFragment() / tile.getMagicFragmentCapacity();
			renderCrystal(tile, count + 1, Math.min(ratio, 1), rotation / 20);
			GlStateManager.popMatrix();

			if (this.rendererDispatcher.cameraHitResult != null
					&& tile.getPos().equals(this.rendererDispatcher.cameraHitResult.getBlockPos())) {
				this.setLightmapDisabled(true);
				this.drawNameplate(tile, TextHelper.toAbbreviatedNumber(tile.getMagicFragment()), x, y - 1.25, z, 12);
				this.setLightmapDisabled(false);
			}
		}

	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.disableCull();
		RenderHelper.render(stack, TEXTUREL, MODEL, false, 0.038, 0.0175, 0, 0);
		GlStateManager.enableCull();
	}

	public static void renderCrystal(TileIceRockStand tile, int high, double ratio, float offset) {

		getFrameBuff().bindFrame(false);

		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();

		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0D, 128, 128, 0.0D, 0, 128);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.viewport(0, 0, 128, 128);
		GlStateManager.depthMask(false);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();

		Shaders.BlockIceRockCrystal.bind();
		TEXTUREL_CRYSTAL.bind();
		TEXTUREL_CRYSTAL_FULL.bindAtive(3);
		TEXTUREL_CRYSTAL_MASK.bindAtive(4);
		Shaders.BlockIceRockCrystal.setUniform("texA", 0);
		Shaders.BlockIceRockCrystal.setUniform("texB", 3);
		Shaders.BlockIceRockCrystal.setUniform("mask", 4);
		Shaders.BlockIceRockCrystal.setUniform("r", ratio);
		Shaders.BlockIceRockCrystal.setUniform("yoffset", offset);

		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(0, 0, -64).tex(0, 0).endVertex();
		bufferbuilder.pos(0, 128, -64).tex(0, 1).endVertex();
		bufferbuilder.pos(128, 128, -64).tex(1, 1).endVertex();
		bufferbuilder.pos(128, 0, -64).tex(1, 0).endVertex();
		tessellator.draw();

		TEXTUREL_CRYSTAL_FULL.unbindAtive(3);
		TEXTUREL_CRYSTAL_MASK.unbindAtive(4);
		Shaders.BlockIceRockCrystal.unbind();

		GlStateManager.depthMask(true);
		GlStateManager.viewport(0, 0, Effect.mc.displayWidth, Effect.mc.displayHeight);
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.popMatrix();

		net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();

		getFrameBuff().unbindFrame();
		getFrameBuff().bindTexture();

		double len = 0.8536;
		double rlen = 1 - len;

		for (int i = 1; i <= high; i++) {
			double yoff = i - 1;
			if (i == 1) {
				bufferbuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);

				bufferbuilder.pos(0.5, rlen + yoff, 0.5).tex(0.5, 0).endVertex();
				bufferbuilder.pos(rlen, 1 + yoff, rlen).tex(0, 1).endVertex();
				bufferbuilder.pos(len, 1 + yoff, rlen).tex(1, 1).endVertex();

				bufferbuilder.pos(0.5, rlen + yoff, 0.5).tex(0.5, 0).endVertex();
				bufferbuilder.pos(rlen, 1 + yoff, len).tex(1, 1).endVertex();
				bufferbuilder.pos(rlen, 1 + yoff, rlen).tex(0, 1).endVertex();

				bufferbuilder.pos(0.5, rlen + yoff, 0.5).tex(0.5, 0).endVertex();
				bufferbuilder.pos(len, 1 + yoff, rlen).tex(1, 1).endVertex();
				bufferbuilder.pos(len, 1 + yoff, len).tex(0, 1).endVertex();

				bufferbuilder.pos(0.5, rlen + yoff, 0.5).tex(0.5, 0).endVertex();
				bufferbuilder.pos(len, 1 + yoff, len).tex(0, 1).endVertex();
				bufferbuilder.pos(rlen, 1 + yoff, len).tex(1, 1).endVertex();

				tessellator.draw();
			} else if (i == high) {
				bufferbuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);

				bufferbuilder.pos(0.5, len + yoff, 0.5).tex(0.5, 1).endVertex();
				bufferbuilder.pos(len, 0 + yoff, rlen).tex(1, 0).endVertex();
				bufferbuilder.pos(rlen, 0 + yoff, rlen).tex(0, 0).endVertex();

				bufferbuilder.pos(0.5, len + yoff, 0.5).tex(0.5, 1).endVertex();
				bufferbuilder.pos(rlen, 0 + yoff, rlen).tex(0, 0).endVertex();
				bufferbuilder.pos(rlen, 0 + yoff, len).tex(1, 0).endVertex();

				bufferbuilder.pos(0.5, len + yoff, 0.5).tex(0.5, 1).endVertex();
				bufferbuilder.pos(len, 0 + yoff, len).tex(0, 0).endVertex();
				bufferbuilder.pos(len, 0 + yoff, rlen).tex(1, 0).endVertex();

				bufferbuilder.pos(0.5, len + yoff, 0.5).tex(0.5, 1).endVertex();
				bufferbuilder.pos(rlen, 0 + yoff, len).tex(1, 0).endVertex();
				bufferbuilder.pos(len, 0 + yoff, len).tex(0, 0).endVertex();

				tessellator.draw();
			} else {
				bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				bufferbuilder.pos(rlen, 0 + yoff, rlen).tex(0, 0).endVertex();
				bufferbuilder.pos(rlen, 1 + yoff, rlen).tex(0, 1).endVertex();
				bufferbuilder.pos(len, 1 + yoff, rlen).tex(1, 1).endVertex();
				bufferbuilder.pos(len, 0 + yoff, rlen).tex(1, 0).endVertex();

				bufferbuilder.pos(rlen, 0 + yoff, rlen).tex(0, 0).endVertex();
				bufferbuilder.pos(rlen, 0 + yoff, len).tex(1, 0).endVertex();
				bufferbuilder.pos(rlen, 1 + yoff, len).tex(1, 1).endVertex();
				bufferbuilder.pos(rlen, 1 + yoff, rlen).tex(0, 1).endVertex();

				bufferbuilder.pos(len, 0 + yoff, len).tex(0, 0).endVertex();
				bufferbuilder.pos(len, 1 + yoff, len).tex(0, 1).endVertex();
				bufferbuilder.pos(rlen, 1 + yoff, len).tex(1, 1).endVertex();
				bufferbuilder.pos(rlen, 0 + yoff, len).tex(1, 0).endVertex();

				bufferbuilder.pos(len, 0 + yoff, len).tex(0, 0).endVertex();
				bufferbuilder.pos(len, 0 + yoff, rlen).tex(1, 0).endVertex();
				bufferbuilder.pos(len, 1 + yoff, rlen).tex(1, 1).endVertex();
				bufferbuilder.pos(len, 1 + yoff, len).tex(0, 1).endVertex();

				tessellator.draw();
			}
		}

	}

}
