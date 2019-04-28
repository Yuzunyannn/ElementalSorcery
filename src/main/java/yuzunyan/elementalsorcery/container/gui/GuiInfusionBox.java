package yuzunyan.elementalsorcery.container.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import yuzunyan.elementalsorcery.ElementalSorcery;

public class GuiInfusionBox extends GuiNormal {

	private static final ResourceLocation TEXTURE = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/container/infusion_box.png");

	public GuiInfusionBox(Container inventorySlotsIn, InventoryPlayer playerInv) {
		super(inventorySlotsIn, playerInv);
	}

	@Override
	public String getUnlocalizedTitle() {
		return "tile.infusionBox.name";
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);

		this.mc.getTextureManager().bindTexture(TEXTURE);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);

	}

}
