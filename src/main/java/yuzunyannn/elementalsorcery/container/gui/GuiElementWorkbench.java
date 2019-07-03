package yuzunyannn.elementalsorcery.container.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class GuiElementWorkbench extends GuiNormal {

	private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft",
			"textures/gui/container/crafting_table.png");

	public GuiElementWorkbench(Container inventorySlotsIn, InventoryPlayer playerInv) {
		super(inventorySlotsIn, playerInv);
	}

	public String getUnlocalizedTitle() {
		return "tile.elementWorkbench.name";
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);

		this.mc.getTextureManager().bindTexture(TEXTURE);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
		
		this.drawTexturedModalRect(offsetX + 91, offsetY + 52, 29, 16, 18, 18);
	}

}
