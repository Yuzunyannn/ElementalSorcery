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

		this.drawTexturedModalRect(offsetX + 14, offsetY + 18, 0, 216, 146, 21);
		this.drawTexturedModalRect(offsetX + 14, offsetY + 18 + 21, 14, 18, 146, 1);
		if (this.container.tileEntity.isFire())
			this.drawTexturedModalRect(offsetX + 81, offsetY + 23, 176, 13, 14, 13);
		else
			this.drawTexturedModalRect(offsetX + 81, offsetY + 23, 176, 0, 14, 13);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		if (this.isMouseIn(mouseX, mouseY, offsetX + 15, offsetY + 40, 144, 29)) {
			if (mouseX < offsetX + 79 || mouseX > offsetX + 96 || mouseY < offsetY + 60) {
				String str = this.container.tileEntity.getCurrentCapacity() + "/"
						+ this.container.tileEntity.getMaxCapacity();
				int x = mouseX - offsetX;
				int y = mouseY - offsetY;
				this.drawInfo(x, y, 0xffffff, str);
			}
		}
	}

}
