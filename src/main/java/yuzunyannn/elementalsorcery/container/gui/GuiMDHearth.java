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
		this.mc.getTextureManager().bindTexture(GuiMDBase.TEXTURE1);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;

		this.drawTexturedModalRect(offsetX + 15, offsetY + 40, 0, 166, 144, 29);
		float rate = this.container.tileEntity.getCurrentCapacity()
				/ (float) this.container.tileEntity.getMaxCapacity();
		super.drawMagicVolume(offsetX + 15, offsetY + 40, 144, 29, rate, partialTicks);
		this.mc.getTextureManager().bindTexture(GuiMDBase.TEXTURE1);
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
		this.mc.getTextureManager().bindTexture(GuiMDBase.TEXTURE2);
		this.drawTexturedModalRect(offsetX + 14, offsetY + 18, 14, 18, 146, 21);
		this.mc.getTextureManager().bindTexture(GuiMDBase.TEXTURE1);
		this.drawTexturedModalRect(offsetX + 14, offsetY + 18 + 21, 14, 18, 146, 1);
		if (this.container.tileEntity.isFire())
			this.drawTexturedModalRect(offsetX + 81, offsetY + 23, 176, 13, 14, 13);
		else
			this.drawTexturedModalRect(offsetX + 81, offsetY + 23, 176, 0, 14, 13);
	}

	@Override
	protected boolean showMagicInfo(int mouseX, int mouseY) {
		return this.isMouseIn(mouseX, mouseY,  15, 40, 144, 29);
	}

}
