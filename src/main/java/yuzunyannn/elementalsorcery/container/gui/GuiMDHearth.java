package yuzunyannn.elementalsorcery.container.gui;

import net.minecraft.entity.player.InventoryPlayer;
import yuzunyannn.elementalsorcery.container.ContainerMDBase;
import yuzunyannn.elementalsorcery.tile.md.TileMDHearth;

public class GuiMDHearth extends GuiMDBase<ContainerMDBase<TileMDHearth>> {

	public GuiMDHearth(ContainerMDBase inventorySlotsIn, InventoryPlayer playerInv) {
		super(inventorySlotsIn, playerInv);
	}

	@Override
	public String getUnlocalizedTitle() {
		return "tile.MDHearth.name";
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.drawDefault(offsetX, offsetY, 40, 29, partialTicks);
		if (this.container.tileEntity.isFire()) this.drawTexturedModalRect(offsetX + 81, offsetY + 23, 176, 13, 14, 13);
		else this.drawTexturedModalRect(offsetX + 81, offsetY + 23, 176, 0, 14, 13);
	}

	@Override
	protected boolean showMagicInfo(int mouseX, int mouseY) {
		return isMouseIn(mouseX, mouseY, 15, 40, 144, 29);
	}

}
