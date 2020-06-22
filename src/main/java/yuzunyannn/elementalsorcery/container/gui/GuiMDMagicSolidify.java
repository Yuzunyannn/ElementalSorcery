package yuzunyannn.elementalsorcery.container.gui;

import net.minecraft.entity.player.InventoryPlayer;
import yuzunyannn.elementalsorcery.container.ContainerMDMagicSolidify;

public class GuiMDMagicSolidify extends GuiMDBase<ContainerMDMagicSolidify> {

	public GuiMDMagicSolidify(ContainerMDMagicSolidify inventorySlotsIn, InventoryPlayer playerInv) {
		super(inventorySlotsIn, playerInv);
	}

	@Override
	public String getUnlocalizedTitle() {
		return "tile.MDMagicSolidify.name";
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.drawDefault(offsetX, offsetY, 59, 10, partialTicks);

		this.drawTexturedModalRect(offsetX + 68, offsetY + 24, 0, 216, 39, 34);
		this.drawTexturedModalRect(offsetX + 79, offsetY + 31, 7, 83, 18, 18);

		// this.drawTexturedModalRect(offsetX + 68, offsetY + 24, 190, 0, 10, 9);
	}

}
