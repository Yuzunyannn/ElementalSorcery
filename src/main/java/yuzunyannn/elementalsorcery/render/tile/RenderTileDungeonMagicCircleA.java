package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.Minecraft;
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
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.tile.dungeon.TileDungeonMagicCircleA;
import yuzunyannn.elementalsorcery.util.helper.Color;

@SideOnly(Side.CLIENT)
public class RenderTileDungeonMagicCircleA extends TileEntitySpecialRenderer<TileDungeonMagicCircleA>
		implements IRenderItem {

	static final public TextureBinder TEXTURE = new TextureBinder("textures/magic_circles/dungeon.png");

	@Override
	public void render(TileDungeonMagicCircleA tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {

		float baseAlpha = 1;
		Entity viewEntity = Minecraft.getMinecraft().getRenderViewEntity();
		if (viewEntity != null) {
			BlockPos pos = tile.getPos();
			double dis = viewEntity.getDistance(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
			baseAlpha = (float) (dis >= 4 ? 1 / (dis - 3) : 1);
		}

		if (baseAlpha < 0.01) return;

		GlStateManager.pushMatrix();
		GlStateManager.depthMask(false);
		GlStateManager.enableRescaleNormal();
		GlStateManager.disableCull();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		RenderFriend.disableLightmap(true);
		GlStateManager.disableLighting();

		float roation = EventClient.getGlobalRotateInRender(partialTicks) + tile.hashCode() % 360;
		float dAlpha = (0.75f + MathHelper.sin(roation / 180 * 3.14159f * 4) * 0.25f);

		Color color = tile.getColor();
		GlStateManager.color(color.r, color.g, color.b, alpha * baseAlpha * dAlpha);
		GlStateManager.translate(x + 0.5, y + 0.01, z + 0.5);
		GlStateManager.rotate(roation, 0, 1, 0);

		TEXTURE.bind();

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();

		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(-0.5, 0, -0.5).tex(0, 0).endVertex();
		bufferbuilder.pos(-0.5, 0, 0.5).tex(0, 1).endVertex();
		bufferbuilder.pos(0.5, 0, 0.5).tex(1, 1).endVertex();
		bufferbuilder.pos(0.5, 0, -0.5).tex(1, 0).endVertex();
		tessellator.draw();

		GlStateManager.enableAlpha();
		GlStateManager.disableBlend();
		GlStateManager.enableLighting();
		RenderFriend.disableLightmap(false);
		GlStateManager.enableCull();
		GlStateManager.depthMask(true);
		GlStateManager.popMatrix();
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		RenderTileDungeonHaystack.drawFakeItem(partialTicks);
	}

}
