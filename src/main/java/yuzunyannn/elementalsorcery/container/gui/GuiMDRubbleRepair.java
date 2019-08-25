package yuzunyannn.elementalsorcery.container.gui;

import net.minecraft.entity.player.InventoryPlayer;
import yuzunyannn.elementalsorcery.container.ContainerMDRubbleRepair;

public class GuiMDRubbleRepair extends GuiMDBase<ContainerMDRubbleRepair> {

	public GuiMDRubbleRepair(ContainerMDRubbleRepair inventorySlotsIn, InventoryPlayer playerInv) {
		super(inventorySlotsIn, playerInv);
	}

	@Override
	public String getUnlocalizedTitle() {
		return "tile.MDRubbleRepair.name";
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		this.mc.getTextureManager().bindTexture(GuiMDBase.TEXTURE1);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;

		this.drawTexturedModalRect(offsetX + 15, offsetY + 59, 0, 166, 144, 10);
		float rate = this.container.tileEntity.getCurrentCapacity()
				/ (float) this.container.tileEntity.getMaxCapacity();
		super.drawMagicVolume(offsetX + 15, offsetY + 59, 144, 10, rate, partialTicks);
		this.mc.getTextureManager().bindTexture(GuiMDBase.TEXTURE1);

		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
		this.drawTexturedModalRect(offsetX + 14, offsetY + 18, 0, 216, 146, 40);
		this.drawTexturedModalRect(offsetX + 14, offsetY + 18 + 40, 14, 18, 146, 1);

		drawMagicSign(offsetX + 6, offsetY + 42, 25, 15);
		drawMagicSign(offsetX + 6, offsetY + 27, 25, 15);

		float srate = this.container.tileEntity.getComplete() / (float) this.container.tileEntity.getTotalComplete();
		rate = srate * 2;
		if (rate > 0) {
			rate = rate > 1 ? 1 : rate;
			int heightDec = (int) ((1 - rate) * 15);
			drawMagicSign(offsetX + 6, offsetY + 42 + heightDec, 40 + heightDec, 15 - heightDec);
			rate = (srate - 0.5f) * 2;
			if (rate > 0) {
				rate = rate > 1 ? 1 : rate;
				heightDec = (int) ((1 - rate) * 15);
				drawMagicSign(offsetX + 6, offsetY + 27 + heightDec, 40 + heightDec, 15 - heightDec);
			}
		}
		this.drawTexturedModalRect(offsetX + 79, offsetY + 30, 7, 83, 18, 18);
	}

	private void drawMagicSign(int xoff, int yoff, int texY, int texH) {
		this.drawTexturedModalRect(xoff, yoff, 176, texY, 43, texH);
		this.drawTexturedModalRect(xoff + 40, yoff, 176, texY, 43, texH);
		this.drawTexturedModalRect(xoff + 80, yoff, 176, texY, 43, texH);
		this.drawTexturedModalRect(xoff + 120, yoff, 176, texY, 43, texH);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		if (this.isMouseIn(mouseX, mouseY, offsetX + 15, offsetY + 59, 144, 10)) {
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
