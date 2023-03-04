package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.tile.dungeon.TileDungeonHaystack;

@SideOnly(Side.CLIENT)
public class RenderTileDungeonHaystack extends TileEntitySpecialRenderer<TileDungeonHaystack> implements IRenderItem {

	static final public TextureBinder ITEM_WHATEVER = new TextureBinder("textures/blocks/dungeon_function.png");

	static final public TextureBinder HAY_BLOCK_SIDE = new TextureBinder("minecraft",
			"textures/blocks/hay_block_side.png");
	static final public TextureBinder HAY_BLOCK_TOP = new TextureBinder("minecraft",
			"textures/blocks/hay_block_top.png");

	@Override
	public void render(TileDungeonHaystack tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
		GlStateManager.translate(x + 0.5, y, z + 0.5);
		drawBlock(tile.getHightRate(), HAY_BLOCK_TOP, HAY_BLOCK_SIDE);
		GlStateManager.popMatrix();
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5, 0.25, 0.5);
		GlStateManager.scale(0.5, 0.5, 0.5);
		GlStateManager.rotate(EventClient.getGlobalRotateInRender(partialTicks), 0, 1, 0);
		drawBlock(1, ITEM_WHATEVER, ITEM_WHATEVER);
		GlStateManager.popMatrix();
	}

	public static void drawBlock(double high, TextureBinder tb, TextureBinder side) {
		if (high < 0.0001f) return;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();

		final double ax = 0;
		final double bx = 1;

		double ay = 1 - high;
		double by = 1;

		side.bind();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

		// four side
		bufferbuilder.pos(-0.5, high, 0.5).tex(ax, ay).normal(0, 0, 1).endVertex();
		bufferbuilder.pos(-0.5, 0, 0.5).tex(ax, by).normal(0, 0, 1).endVertex();
		bufferbuilder.pos(0.5, 0, 0.5).tex(bx, by).normal(0, 0, 1).endVertex();
		bufferbuilder.pos(0.5, high, 0.5).tex(bx, ay).normal(0, 0, 1).endVertex();

		bufferbuilder.pos(-0.5, 00, -0.5).tex(ax, by).normal(0, 0, -1).endVertex();
		bufferbuilder.pos(-0.5, high, -0.5).tex(ax, ay).normal(0, 0, -1).endVertex();
		bufferbuilder.pos(0.5, high, -0.5).tex(bx, ay).normal(0, 0, -1).endVertex();
		bufferbuilder.pos(0.5, 00, -0.5).tex(bx, by).normal(0, 0, -1).endVertex();

		bufferbuilder.pos(0.5, 00, -0.5).tex(ax, by).normal(1, 0, 0).endVertex();
		bufferbuilder.pos(0.5, high, -0.5).tex(ax, ay).normal(1, 0, 0).endVertex();
		bufferbuilder.pos(0.5, high, 0.5).tex(bx, ay).normal(1, 0, 0).endVertex();
		bufferbuilder.pos(0.5, 00, 0.5).tex(bx, by).normal(1, 0, 0).endVertex();

		bufferbuilder.pos(-0.5, high, -0.5).tex(ax, ay).normal(-1, 0, 0).endVertex();
		bufferbuilder.pos(-0.5, 0, -0.5).tex(ax, by).normal(-1, 0, 0).endVertex();
		bufferbuilder.pos(-0.5, 0, 0.5).tex(bx, by).normal(-1, 0, 0).endVertex();
		bufferbuilder.pos(-0.5, high, 0.5).tex(bx, ay).normal(-1, 0, 0).endVertex();

		tessellator.draw();

		ay = 0;

		tb.bind();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

		bufferbuilder.pos(-0.5, 0, -0.5).tex(ax, by).normal(0, -1, 0).endVertex();
		bufferbuilder.pos(0.5, 0, -0.5).tex(ax, ay).normal(0, -1, 0).endVertex();
		bufferbuilder.pos(0.5, 0, 0.5).tex(bx, ay).normal(0, -1, 0).endVertex();
		bufferbuilder.pos(-0.5, 0, 0.5).tex(bx, by).normal(0, -1, 0).endVertex();

		bufferbuilder.pos(-0.5, high + 00, -0.5).tex(ax, by).normal(0, 1, 0).endVertex();
		bufferbuilder.pos(-0.5, high + 00, 0.5).tex(ax, ay).normal(0, 1, 0).endVertex();
		bufferbuilder.pos(0.5, high + 00, 0.5).tex(bx, ay).normal(0, 1, 0).endVertex();
		bufferbuilder.pos(0.5, high + 00, -0.5).tex(bx, by).normal(0, 1, 0).endVertex();

		tessellator.draw();
	}
}
