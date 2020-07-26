package yuzunyannn.elementalsorcery.container.gui;

import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import yuzunyannn.elementalsorcery.container.ContainerMDMagicGen;

public class GuiMDMagicGen extends GuiMDBase<ContainerMDMagicGen> {

	public GuiMDMagicGen(ContainerMDMagicGen inventorySlotsIn, InventoryPlayer playerInv) {
		super(inventorySlotsIn, playerInv);
	}

	@Override
	public String getUnlocalizedTitle() {
		return "tile.MDMagicGen.name";
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		this.mc.getTextureManager().bindTexture(GuiMDBase.TEXTURE1);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		GlStateManager.disableDepth();
		this.drawTexturedModalRect(offsetX + 15, offsetY + 19, 0, 166, 144, 50);
		this.drawT(offsetX, offsetY);
		float rate = this.container.tileEntity.getCurrentCapacity()
				/ (float) this.container.tileEntity.getMaxCapacity();
		super.drawMagicVolume(offsetX + 15, offsetY + 19, 144, 50, rate, partialTicks);
		this.mc.getTextureManager().bindTexture(GuiMDBase.TEXTURE1);
		this.drawMagicStoneMelt(offsetX, offsetY, partialTicks);
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
		this.drawTexturedModalRect(offsetX + 79, offsetY + 60, 7, 83, 18, 18);
	}

	protected void drawT(int offsetX, int offsetY) {
		float T = Math.min(this.container.tileEntity.getTemperature(), 500);
		if (T < 1.0f) return;
		GlStateManager.disableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.color(1.0f, 0f, 0f, T / 600.0f);
		this.drawTexturedModalRect(offsetX + 15, offsetY + 19, 144, 166, 72, 50);
		this.drawTexturedModalRect(offsetX + 15 + 72, offsetY + 19, 144, 166, 72, 50);
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
	}

	protected float moveX;

	protected void drawMagicStoneMelt(int offsetX, int offsetY, float partialTicks) {
		float rate = this.container.tileEntity.getMeltRate();
		if (rate == 0) return;
		else if (rate == 1) moveX = (float) (Math.random() * 138 + 3);
		int x = (int) (offsetX + 15 + moveX);
		int y = (int) (offsetY + 19 + 50 - 1 - rate * 13);
		this.drawItem(this.container.tileEntity.getRenderItem(), x, y);
		this.mc.getTextureManager().bindTexture(GuiMDBase.TEXTURE1);
	}

	@Override
	protected void getShowMagicInfos(List<String> infos) {
		super.getShowMagicInfos(infos);
		infos.add((int) this.container.tileEntity.getTemperature() + "℃");
	}

	protected boolean showMagicInfo(int mouseX, int mouseY) {
		return isMouseIn(mouseX, mouseY, 15, 19, 144, 50) && !isMouseIn(mouseX, mouseY, 79, 60, 18, 18);
	}
}
