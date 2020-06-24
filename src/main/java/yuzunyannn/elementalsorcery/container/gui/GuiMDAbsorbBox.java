package yuzunyannn.elementalsorcery.container.gui;

import net.minecraft.entity.player.InventoryPlayer;
import yuzunyannn.elementalsorcery.container.ContainerMDAbsorbBox;

public class GuiMDAbsorbBox extends GuiMDBase<ContainerMDAbsorbBox> {

	public GuiMDAbsorbBox(ContainerMDAbsorbBox inventorySlotsIn, InventoryPlayer playerInv) {
		super(inventorySlotsIn, playerInv);
	}

	@Override
	public String getUnlocalizedTitle() {
		return "tile.MDAbsorbBox.name";
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.drawDefault(offsetX, offsetY, 50, 10, partialTicks);
		GuiMDRubbleRepair.drawMagicSign(offsetX + 6, offsetY + 31, 25, 15);
		this.drawTexturedModalRect(offsetX + 79, offsetY + 30, 7, 83, 18, 18);
	}

}
