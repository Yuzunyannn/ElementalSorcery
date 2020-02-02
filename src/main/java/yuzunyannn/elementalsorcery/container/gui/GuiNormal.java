package yuzunyannn.elementalsorcery.container.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public abstract class GuiNormal<T extends Container> extends GuiContainer {

	protected final InventoryPlayer playerInventory;
	protected final T container;

	public GuiNormal(T inventorySlotsIn, InventoryPlayer playerInv) {
		super(inventorySlotsIn);
		this.playerInventory = playerInv;
		this.container = inventorySlotsIn;
	}

	public abstract String getUnlocalizedTitle();

	// 黑色背景和鼠标移动过去的显示名字
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	// 名称和物品栏信息
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String s = I18n.format(this.getUnlocalizedTitle());
		this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
		this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
	}

	// 画一次物品
	public void drawItem(ItemStack stack, int x, int y) {
		RenderHelper.enableGUIStandardItemLighting();
		this.zLevel = 100.0F;
		this.itemRender.zLevel = 100.0F;
		String s = null;
		if (stack.getCount() > 1) {
			s = TextFormatting.WHITE.toString() + stack.getCount();
		}
		this.itemRender.renderItemAndEffectIntoGUI(this.mc.player, stack, x, y);
		this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, stack, x, y, s);
		this.zLevel = 0F;
		this.itemRender.zLevel = 0F;
	}

	// 画一个物品文字的背景
	protected void drawToolTipBackground(int x, int y, int width, int height) {
		final int left = x;
		final int top = y;
		int right = x + width;
		int bottom = y + height;
		this.drawGradientRect(left - 3, top - 4, right + 3, top - 3, -267386864, -267386864);
		this.drawGradientRect(left - 3, bottom + 3, right + 3, bottom + 4, -267386864, -267386864);
		this.drawGradientRect(left - 3, top - 3, right + 3, bottom + 3, -267386864, -267386864);
		this.drawGradientRect(left - 4, top - 3, left - 3, bottom + 3, -267386864, -267386864);
		this.drawGradientRect(right + 3, top - 3, right + 4, bottom + 3, -267386864, -267386864);
		this.drawGradientRect(left - 3, top - 3 + 1, left - 3 + 1, bottom + 3 - 1, 1347420415, 1344798847);
		this.drawGradientRect(right + 2, top - 3 + 1, right + 3, bottom + 3 - 1, 1347420415, 1344798847);
		this.drawGradientRect(left - 3, top - 3, right + 3, top - 3 + 1, 1347420415, 1347420415);
		this.drawGradientRect(left - 3, bottom + 2, right + 3, bottom + 3, 1344798847, 1344798847);
	}

	public void drawTexturedModalRect(float x, float y, int u, int v, int width, int height, float textureWidth,
			float textureHeight) {
		float f = 1.0F / textureWidth;
		float f1 = 1.0F / textureHeight;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double) x, (double) (y + height), 0.0D).tex((double) (u * f), (double) ((v + (float) height) * f1)).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + height), 0.0D).tex((double) ((u + (float) width) * f), (double) ((v + (float) height) * f1)).endVertex();
		bufferbuilder.pos((double) (x + width), (double) y, 0.0D).tex((double) ((u + (float) width) * f), (double) (v * f1)).endVertex();
		bufferbuilder.pos((double) x, (double) y, 0.0D).tex((double) (u * f), (double) (v * f1)).endVertex();
		tessellator.draw();
	}

	// 画一个可以镜像width的图标
	public void drawTexturedModalRectMirrorWidth(int x, int y, int textureX, int textureY, int width, int height) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double) (x + 0), (double) (y + height), (double) this.zLevel).tex((double) ((float) (textureX + width) * 0.00390625F), (double) ((float) (textureY + height) * 0.00390625F)).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + height), (double) this.zLevel).tex((double) ((float) (textureX + 0) * 0.00390625F), (double) ((float) (textureY + height) * 0.00390625F)).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + 0), (double) this.zLevel).tex((double) ((float) (textureX + 0) * 0.00390625F), (double) ((float) (textureY + 0) * 0.00390625F)).endVertex();
		bufferbuilder.pos((double) (x + 0), (double) (y + 0), (double) this.zLevel).tex((double) ((float) (textureX + width) * 0.00390625F), (double) ((float) (textureY + 0) * 0.00390625F)).endVertex();
		tessellator.draw();
	}

	// 画一个可以镜像height的图标
	public void drawTexturedModalRectMirrorHeight(int x, int y, int textureX, int textureY, int width, int height) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double) (x + 0), (double) (y + height), (double) this.zLevel).tex((double) ((float) (textureX + 0) * 0.00390625F), (double) ((float) (textureY + 0) * 0.00390625F)).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + height), (double) this.zLevel).tex((double) ((float) (textureX + width) * 0.00390625F), (double) ((float) (textureY + 0) * 0.00390625F)).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + 0), (double) this.zLevel).tex((double) ((float) (textureX + width) * 0.00390625F), (double) ((float) (textureY + height) * 0.00390625F)).endVertex();
		bufferbuilder.pos((double) (x + 0), (double) (y + 0), (double) this.zLevel).tex((double) ((float) (textureX + 0) * 0.00390625F), (double) ((float) (textureY + height) * 0.00390625F)).endVertex();
		tessellator.draw();
	}

}
