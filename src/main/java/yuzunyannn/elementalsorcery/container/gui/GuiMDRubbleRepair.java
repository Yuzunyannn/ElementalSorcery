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
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.drawDefault(offsetX, offsetY, 59, 10, partialTicks);

		float rate = this.container.tileEntity.getCurrentCapacity()
				/ (float) this.container.tileEntity.getMaxCapacity();
		drawMagicSign(this, offsetX + 6, offsetY + 42, 25, 15);
		drawMagicSign(this, offsetX + 6, offsetY + 27, 25, 15);

		float srate = this.container.tileEntity.getComplete() / (float) this.container.tileEntity.getTotalComplete();
		rate = srate * 2;
		if (rate > 0) {
			rate = rate > 1 ? 1 : rate;
			int heightDec = (int) ((1 - rate) * 15);
			drawMagicSign(this, offsetX + 6, offsetY + 42 + heightDec, 40 + heightDec, 15 - heightDec);
			rate = (srate - 0.5f) * 2;
			if (rate > 0) {
				rate = rate > 1 ? 1 : rate;
				heightDec = (int) ((1 - rate) * 15);
				drawMagicSign(this, offsetX + 6, offsetY + 27 + heightDec, 40 + heightDec, 15 - heightDec);
			}
		}
		this.drawTexturedModalRect(offsetX + 79, offsetY + 30, 7, 83, 18, 18);
	}

	public static void drawMagicSign(GuiNormal<?> gui, int xoff, int yoff, int texY, int texH) {
		gui.drawTexturedModalRect(xoff, yoff, 176, texY, 43, texH);
		gui.drawTexturedModalRect(xoff + 40, yoff, 176, texY, 43, texH);
		gui.drawTexturedModalRect(xoff + 80, yoff, 176, texY, 43, texH);
		gui.drawTexturedModalRect(xoff + 120, yoff, 176, texY, 43, texH);
	}

}
