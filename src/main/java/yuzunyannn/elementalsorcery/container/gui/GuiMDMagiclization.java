package yuzunyannn.elementalsorcery.container.gui;

import net.minecraft.entity.player.InventoryPlayer;
import yuzunyannn.elementalsorcery.container.ContainerMDMagiclization;

public class GuiMDMagiclization extends GuiMDBase<ContainerMDMagiclization> {

	public GuiMDMagiclization(ContainerMDMagiclization inventorySlotsIn, InventoryPlayer playerInv) {
		super(inventorySlotsIn, playerInv);
	}

	@Override
	public String getUnlocalizedTitle() {
		return "tile.MDMagiclization.name";
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.drawDefault(offsetX, offsetY, 44, 25, partialTicks);
		GuiMDRubbleRepair.drawMagicSign(this, offsetX + 6, offsetY + 26, 25, 15);
		this.drawTexturedModalRect(offsetX + 79, offsetY + 24, 7, 83, 18, 18);
	}
	
	@Override
	protected boolean showMagicInfo(int mouseX, int mouseY) {
		return this.isMouseIn(mouseX, mouseY, 15, 44, 144, 25);
	}

}
