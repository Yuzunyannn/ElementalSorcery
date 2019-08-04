package yuzunyannn.elementalsorcery.container.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class GuiSimple extends GuiNormal {
	public final String unlocalizedTitle;
	public final ResourceLocation texture;

	public GuiSimple(Container inventorySlotsIn, InventoryPlayer playerInv, String unlocalizedTitle,
			ResourceLocation texture) {
		super(inventorySlotsIn, playerInv);
		this.unlocalizedTitle = unlocalizedTitle;
		this.texture = texture;
	}

	@Override
	public String getUnlocalizedTitle() {
		return unlocalizedTitle;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		this.mc.getTextureManager().bindTexture(texture);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
	}

}
