package yuzunyannn.elementalsorcery.render.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.logics.IRenderClient;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockSendRecv;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockSendRecv.FaceStatus;

@SideOnly(Side.CLIENT)
public class RenderTileIceRockSendRecv<T extends TileIceRockSendRecv> extends TileEntitySpecialRenderer<T> {

	static public final TextureBinder TEXTURE_FACE_IN = new TextureBinder("textures/blocks/ice_rock/face_in.png");
	static public final TextureBinder TEXTURE_FACE_OUT = new TextureBinder("textures/blocks/ice_rock/face_out.png");
	static public final TextureBinder TEXTURE_FACE_P = new TextureBinder("textures/blocks/ice_rock/face_p.png");

	static public final EnumFacing[] UP_DOWN = { EnumFacing.UP, EnumFacing.DOWN };

	public RenderTileIceRockSendRecv() {

	}

	@Override
	public void render(T tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (!tile.needRenderFaceEffect()) return;
		EventClient.addRenderTask((p) -> {
			BlockPos pos = tile.getPos();
			realRender(tile, pos.getX(), pos.getY(), pos.getZ(), p);
			return IRenderClient.END;
		});
	}

	public void realRender(TileIceRockSendRecv tile, double x, double y, double z, float partialTicks) {
		GlStateManager.disableCull();
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
		GlStateManager.disableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.depthMask(false);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderHelper.disableStandardItemLighting();
		yuzunyannn.elementalsorcery.api.util.render.RenderFriend.disableLightmap(true);

		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			float r = tile.getFaceAnimeRatio(facing, partialTicks);
			if (r < 0.0001) continue;
			float rotation = -facing.getHorizontalAngle() - 180;
			GlStateManager.pushMatrix();
			GlStateManager.rotate(rotation, 0, 1, 0);
			GlStateManager.translate(0, 0, -0.499);
			float changeRotate = 360 * r;
			float changeScale = r;
			if (r < 1 - 0.00001) {
				GlStateManager.rotate(changeRotate, 0, 0, 1);
				GlStateManager.scale(changeScale, changeScale, changeScale);
			}
			drawWithStatus(tile.getFaceStatus(facing), 1);
			GlStateManager.popMatrix();
		}

		if (tile.hasUpDownFace()) {
			for (EnumFacing facing : UP_DOWN) {
				float r = tile.getFaceAnimeRatio(facing, partialTicks);
				if (r < 0.0001) continue;
				GlStateManager.pushMatrix();
				GlStateManager.rotate(facing == EnumFacing.UP ? 90 : -90, 1, 0, 0);
				GlStateManager.translate(0, 0, -0.499);
				float changeRotate = 360 * r;
				float changeScale = r;
				if (r < 1 - 0.00001) {
					GlStateManager.rotate(changeRotate, 0, 0, 1);
					GlStateManager.scale(changeScale, changeScale, changeScale);
				}
				drawWithStatus(tile.getFaceStatus(facing), 1);
				GlStateManager.popMatrix();
			}
		}

		RenderHelper.enableStandardItemLighting();
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.popMatrix();
		GlStateManager.enableCull();
	}

	public static void drawWithStatus(FaceStatus status, float a) {
		switch (status) {
		case OUT:
			GlStateManager.color(85 / 255f, 160 / 255f, 254 / 255f, a);
			TEXTURE_FACE_OUT.bind();
			renderCull();
			break;
		case IN:
			GlStateManager.color(254 / 255f, 104 / 255f, 104 / 255f, a);
			TEXTURE_FACE_IN.bind();
			renderCull();
			break;
		default:
			GlStateManager.color(149 / 255f, 81 / 255f, 205 / 255f, a);
			break;
		}
		TEXTURE_FACE_P.bind();
		renderCull();
	}

	public static void renderCull() {
		float l = 0.45f;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(-l, -l, 0).tex(0, 0).endVertex();
		bufferbuilder.pos(-l, l, 0).tex(0, 1).endVertex();
		bufferbuilder.pos(l, l, 0).tex(1, 1).endVertex();
		bufferbuilder.pos(l, -l, 0).tex(1, 0).endVertex();
		tessellator.draw();
	}

}
