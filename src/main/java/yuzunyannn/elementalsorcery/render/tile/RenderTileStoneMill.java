package yuzunyannn.elementalsorcery.render.tile;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.block.container.BlockStoneMill;
import yuzunyannn.elementalsorcery.render.model.ModelStoneMill;
import yuzunyannn.elementalsorcery.tile.TileStoneMill;
import yuzunyannn.elementalsorcery.tile.TileStoneMill.Milling;

@SideOnly(Side.CLIENT)
public class RenderTileStoneMill extends TileEntitySpecialRenderer<TileStoneMill> implements IRenderItem {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/stone_mill.png");
	public static final ModelStoneMill MODEL = new ModelStoneMill();

	@Override
	public void render(TileStoneMill tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
		int dusty = tile.getDusty();
		float lift = tile.prevLiftTick + (tile.liftTick - tile.prevLiftTick) * partialTicks;
		float rotate = tile.prevRotate + (tile.rotate - tile.prevRotate) * partialTicks;
		float playerRoate = tile.prevPlayerRoate + (tile.playerRoate - tile.prevPlayerRoate) * partialTicks;

		RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRender(x + 0.5, y, z + 0.5, 0.0625, alpha);
		MODEL.needRenderHammer = false;
		MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);

		if (tile.hasHammer()) {
			GlStateManager.pushMatrix();
			GlStateManager.rotate(playerRoate * lift / 3.1415926f * 180f, 0, 1, 0);
			MODEL.renderHammer(lift, rotate, dusty / 1000.0f, 1.0f);
			GlStateManager.popMatrix();
		}

		RenderFriend.endTileEntitySpecialRender();
		RenderFriend.bindDestoryTextureEnd(destroyStage);

		List<Milling> list = tile.getMillList();
		float dustyYoff = dusty / 1000.0f * 0.68f + 0.1875f;
		for (Milling m : list) {
			float ydown = m.degree / (float) tile.getDustyCount(m.stack) * tile.getHight(m.stack);
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + m.xoff, y + dustyYoff - ydown - 0.125f, z + m.zoff);
			GlStateManager.rotate(m.roate, 0, 1, 0);
			yuzunyannn.elementalsorcery.api.util.render.RenderFriend.layItemPositionFix(m.stack);
			Minecraft.getMinecraft().getRenderItem().renderItem(m.stack, ItemCameraTransforms.TransformType.FIXED);
			GlStateManager.popMatrix();
		}

		if (dusty > 0) {
			this.bindTexture(tile.getDustyTexture());
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y + dustyYoff, z);
			GlStateManager.disableLighting();
			this.drawDusty();
			GlStateManager.popMatrix();
		}

	}

	public void drawDusty() {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(0, 0, 0).tex(0, 0).endVertex();
		bufferbuilder.pos(0, 0, 1).tex(0, 1).endVertex();
		bufferbuilder.pos(1, 0, 1).tex(1, 1).endVertex();
		bufferbuilder.pos(1, 0, 0).tex(1, 0).endVertex();
		tessellator.draw();
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		MODEL.needRenderHammer = BlockStoneMill.hasHammer(stack);
		RenderFriend.renderSpecialItem(stack, TEXTURE, MODEL, true);
	}

}
