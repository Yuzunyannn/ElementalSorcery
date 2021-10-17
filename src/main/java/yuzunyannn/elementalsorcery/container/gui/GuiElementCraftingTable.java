package yuzunyannn.elementalsorcery.container.gui;

import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.ContainerElementCraftingTable;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.event.EventClient;

public class GuiElementCraftingTable extends GuiNormal<ContainerElementCraftingTable> {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/container/element_crafting_table.png");
	public static final ResourceLocation TEXTURE_BG = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/container/element_altar_bg.png");

	public GuiElementCraftingTable(ContainerElementCraftingTable inventorySlotsIn, InventoryPlayer playerInv) {
		super(inventorySlotsIn, playerInv);
		this.xSize = 230;
		this.ySize = 242;
	}

	@Override
	public String getUnlocalizedTitle() {
		return "tile.elementCraftingTable.name";
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String s = this.getDisplayTitle();
		this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
		this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 36,
				this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(TEXTURE);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
		this.drawTexturedModalRect(offsetX + 106, offsetY + 111, 230, 60, 18, 18);
		if (!container.isBig) {
			this.mc.getTextureManager().bindTexture(TEXTURE_BG);
			this.drawTexturedModalRect(offsetX + 52, offsetY + 21, 52, 21, 36, 36);
			this.drawTexturedModalRect(offsetX + 142, offsetY + 21, 142, 21, 36, 36);
			this.drawTexturedModalRect(offsetX + 52, offsetY + 111, 52, 111, 36, 36);
			this.drawTexturedModalRect(offsetX + 142, offsetY + 111, 142, 111, 36, 36);
		}
		ItemStack stack = container.tileEntity.getOutput();
		if (stack.isEmpty()) return;
		List<ElementStack> list = container.tileEntity.getNeedElements();
		if (list == null) return;
		RenderHelper.disableStandardItemLighting();
		GuiSupremeTable.drawElements(mc, offsetX, offsetY, list, 8, EventClient.tick / 40);

	}

}
