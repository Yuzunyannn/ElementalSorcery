package yuzunyannn.elementalsorcery.container.gui;

import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
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
		String s = I18n.format(this.getUnlocalizedTitle());
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
			this.drawTexturedModalRect(offsetX + 52, offsetY + 21, 3, 21, 36, 36);
			this.drawTexturedModalRect(offsetX + 142, offsetY + 21, 3, 5, 36, 36);
			this.drawTexturedModalRect(offsetX + 52, offsetY + 111, 3, 21, 36, 36);
			this.drawTexturedModalRect(offsetX + 142, offsetY + 111, 3, 5, 36, 36);
		}
		ItemStack stack = container.tileEntity.getOutput();
		if (stack.isEmpty()) return;
		List<ElementStack> list = container.tileEntity.getNeedElements();
		if (list == null) return;
		RenderHelper.disableStandardItemLighting();
		GuiSupremeTable.drawElements(mc, offsetX, offsetY, list, 8, EventClient.tick / 40);

	}

}
