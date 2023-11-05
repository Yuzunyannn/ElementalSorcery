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
import yuzunyannn.elementalsorcery.api.util.client.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.model.ModelIceRockStand;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockStand;
import yuzunyannn.elementalsorcery.util.TextHelper;
import yuzunyannn.elementalsorcery.util.render.FrameHelper;
import yuzunyannn.elementalsorcery.util.render.Shaders;

@SideOnly(Side.CLIENT)
public class RenderTileIceRockStand extends TileEntitySpecialRenderer<TileIceRockStand> implements IRenderItem {

	static public final TextureBinder TEXTURE_CRYSTAL = new TextureBinder(
			"textures/blocks/ice_rock/ice_rock_crystal.png");
	static public final TextureBinder TEXTURE_CRYSTAL_FULL = new TextureBinder(
			"textures/blocks/ice_rock/ice_rock_crystal_full.png");
	static public final TextureBinder TEXTURE_CRYSTAL_MASK = new TextureBinder("textures/blocks/ice_rock/could.png");

	static public final TextureBinder TEXTURE = new TextureBinder("textures/blocks/ice_rock/ice_rock_stand.png");
	static public final ModelIceRockStand MODEL = new ModelIceRockStand();

	public RenderTileIceRockStand() {

	}

	@Override
	public void render(TileIceRockStand tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {

		RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRender(x + 0.5, y, z + 0.5, 0.0625, alpha);
		MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		RenderFriend.endTileEntitySpecialRender();
		RenderFriend.bindDestoryTextureEnd(destroyStage);

		int count = tile.getLinkCount();
		if (count > 0 && destroyStage < 0) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y + 1 - 0.8536, z);
			float rotation = EventClient.getGlobalRotateInRender(partialTicks);
			GlStateManager.translate(0.5, 0.5, 0.5);
			GlStateManager.rotate(rotation / 3, 0, 1, 0);
			GlStateManager.translate(-0.5, -0.5, -0.5);
			double ratio = 0;
			double fragment = tile.getMagicFragment();
			double fragmentCapacity = tile.getMagicFragmentCapacity();
			if (fragment < 100000) ratio = fragment / 100000 * 0.15;
			else ratio = 0.15 + 0.85 * (fragment - 100000) / (fragmentCapacity - 100000);
			renderCrystalTexture(Math.min(ratio, 1), rotation / 20);
			FrameHelper.bindOffscreenTexture128();
			renderCrystal(count + 1);
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
		RenderFriend.renderSpecialItem(stack, TEXTURE, MODEL, true, 0.038, 0.0175, 0, 0);
		GlStateManager.enableCull();
	}

	public static void renderCrystalTexture(double ratio, float offset) {
		FrameHelper.renderOffscreenTexture128(e -> {
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();

			Shaders.BlockIceRockCrystal.bind();
			TEXTURE_CRYSTAL.bind();
			TEXTURE_CRYSTAL_FULL.bindAtive(3);
			TEXTURE_CRYSTAL_MASK.bindAtive(4);
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

			TEXTURE_CRYSTAL_FULL.unbindAtive(3);
			TEXTURE_CRYSTAL_MASK.unbindAtive(4);
			Shaders.BlockIceRockCrystal.unbind();
		});
	}

	public static void renderCrystal(int high) {

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();

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
