package yuzunyannn.elementalsorcery.container.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.MathHelper;
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
		this.mc.getTextureManager().bindTexture(GuiMDBase.TEXTURE1);
		this.drawTexturedModalRect(offsetX + 15, offsetY + 59, 0, 166, 144, 10);
		float rate = this.container.tileEntity.getCurrentCapacity()
				/ (float) this.container.tileEntity.getMaxCapacity();
		super.drawMagicVolume(offsetX + 15, offsetY + 59, 144, 10, rate, partialTicks);
		this.mc.getTextureManager().bindTexture(GuiMDBase.TEXTURE1);
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
		this.mc.getTextureManager().bindTexture(GuiMDBase.TEXTURE2);
		this.drawTexturedModalRect(offsetX + 14, offsetY + 18, 14, 18, 146, 40);
		this.mc.getTextureManager().bindTexture(GuiMDBase.TEXTURE1);
		this.drawTexturedModalRect(offsetX + 14, offsetY + 18 + 40, 14, 18, 146, 1);
		
		this.drawTexturedModalRect(offsetX + 68, offsetY + 24, 0, 216, 39, 34);
		this.drawTexturedModalRect(offsetX + 79, offsetY + 31, 7, 83, 18, 18);
		
		//this.drawTexturedModalRect(offsetX + 68, offsetY + 24, 190, 0, 10, 9);
	}

	@Override
	protected boolean showMagicInfo(int mouseX, int mouseY) {
		return this.isMouseIn(mouseX, mouseY, 15, 59, 144, 10);
	}

}
