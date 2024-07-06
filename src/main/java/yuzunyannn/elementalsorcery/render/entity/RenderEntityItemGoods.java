package yuzunyannn.elementalsorcery.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.entity.EntityItemGoods;

public class RenderEntityItemGoods extends Render<EntityItemGoods> {

	public static final ResourceLocation TEXTURE_GREEN = new ResourceLocation(ESAPI.MODID,
			"textures/entity/item_goods_label_green.png");
	public static final ResourceLocation TEXTURE_RED = new ResourceLocation(ESAPI.MODID,
			"textures/entity/item_goods_label_red.png");

	public final RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();

	public RenderEntityItemGoods(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityItemGoods entity) {
		return null;
	}

	@Override
	public void doRender(EntityItemGoods entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x, (float) y, (float) z);

		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		}

		ItemStack itemStack = entity.getItem();
		GlStateManager.translate(0, -0.385, 0);
		GlStateManager.rotate(entity.rotationYaw, 0, 1, 0);
		RenderFriend.layItemPositionFix(itemStack);
		this.itemRenderer.renderItem(itemStack, ItemCameraTransforms.TransformType.FIXED);

		if (this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.popMatrix();
		if (!this.renderOutlines) {
			RayTraceResult omo = Minecraft.getMinecraft().objectMouseOver;
			if (omo != null && omo.entityHit == entity) {
				boolean isGreen = entity.isSold();
				int price = entity.getPriceForShow();
				boolean hasPrice = isGreen && price > 0;
				
				FontRenderer fontRender = getFontRendererFromRenderManager();
				String nameStr = entity.getDisplayName().getFormattedText();
				if (itemStack.getCount() > 1) nameStr = nameStr + "x" + itemStack.getCount();
				String priceStr = hasPrice ? I18n.format("info.elf.goods.coin", price) : "";
				int priceWidth = fontRender.getStringWidth(priceStr) + 2;
				int nameWidth = fontRender.getStringWidth(nameStr) + 2;
				int height = hasPrice ? 18 : 9;
				int w = Math.max(nameWidth, priceWidth);

				GlStateManager.pushMatrix();
				GlStateManager.depthFunc(519);
				GlStateManager.color(1, 1, 1, 1);
				GlStateManager.translate((float) x, (float) y, (float) z);
				GlStateManager.translate(0, -0.385, 0);
				GlStateManager.rotate(entity.rotationYaw, 0, 1, 0);
				GlStateManager.translate(-0.1, 0.75, 0.5);
				GlStateManager.scale(0.02, 0.02, 0.02);
				GlStateManager.rotate(180, 0, 1, 0);
				GlStateManager.rotate(100, 1, 0, 0);
				if (isGreen) this.bindTexture(TEXTURE_GREEN);
				else this.bindTexture(TEXTURE_RED);
				GlStateManager.disableCull();
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferbuilder = tessellator.getBuffer();
				bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
				bufferbuilder.pos(-w / 2 - 1, -1, 0).tex(0, 0).endVertex();
				bufferbuilder.pos(w / 2 - 1, -1, 0).tex(w * 0.1, 0).endVertex();
				bufferbuilder.pos(w / 2 - 1, height, 0).tex(w * 0.1, 1 * height / 9).endVertex();
				bufferbuilder.pos(-w / 2 - 1, height, 0).tex(0, 1 * height / 9).endVertex();
				tessellator.draw();

				int stringColor = 0x87f59c;
				if (!isGreen) stringColor = 0xf58794;

				GlStateManager.translate(0, 0, -0.5);
				GlStateManager.disableLighting();
				fontRender.drawString(nameStr, -w / 2, 0, stringColor);
				if (isGreen) fontRender.drawString(priceStr, -w / 2, height / 2, stringColor);
				GlStateManager.enableLighting();
				GlStateManager.translate(0, 0, 0.5);
				GlStateManager.depthFunc(515);
				
				GlStateManager.rotate(-100, 1, 0, 0);
				GlStateManager.rotate(-180, 0, 1, 0);
				GlStateManager.scale(1 / 0.02, 1 / 0.02, 1 / 0.02);
				GlStateManager.translate(0.1, -0.75, -0.5);
				GlStateManager.disableTexture2D();
				GlStateManager.glLineWidth(3);
				bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
				bufferbuilder.pos(0.0, 0.75, 0.5).color(0.2509f, 0.2509f, 0.2509f, 1).endVertex();
				bufferbuilder.pos(0, 0.385, 0).color(0.2509f, 0.2509f, 0.2509f, 1).endVertex();
				tessellator.draw();
				GlStateManager.enableTexture2D();
				GlStateManager.popMatrix();
			}
		}
	}

}
