package yuzunyannn.elementalsorcery.container.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public abstract class GuiNormal extends GuiContainer {

	protected final InventoryPlayer playerInventory;

	public GuiNormal(Container inventorySlotsIn, InventoryPlayer playerInv) {
		super(inventorySlotsIn);
		this.playerInventory = playerInv;
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
		this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2,
				4210752);
	}

	// 开始画物品
	protected void startDrawItem() {
		RenderHelper.enableGUIStandardItemLighting();
		this.zLevel = 200.0F;
		this.itemRender.zLevel = 200.0F;
	}

	// 结束画物品
	protected void endDrawItem() {
		this.zLevel = 0.0F;
		this.itemRender.zLevel = 0F;
	}

	// 画一次物品
	protected void drawOnceItem(ItemStack stack, int x, int y) {
		String s = null;
		if (stack.getCount() > 1) {
			s = TextFormatting.WHITE.toString() + stack.getCount();
		}
		this.itemRender.renderItemAndEffectIntoGUI(this.mc.player, stack, x, y);
		this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, stack, x, y, s);
	}

}
