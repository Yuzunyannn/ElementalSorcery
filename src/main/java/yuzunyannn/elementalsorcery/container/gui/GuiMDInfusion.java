package yuzunyannn.elementalsorcery.container.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.MathHelper;
import yuzunyannn.elementalsorcery.container.ContainerMDInfusion;

public class GuiMDInfusion extends GuiMDBase<ContainerMDInfusion> {

	public GuiMDInfusion(ContainerMDInfusion inventorySlotsIn, InventoryPlayer playerInv) {
		super(inventorySlotsIn, playerInv);
	}

	@Override
	public String getUnlocalizedTitle() {
		return "tile.MDInfusion.name";
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.mc.getTextureManager().bindTexture(GuiMDBase.TEXTURE1);
		this.drawTexturedModalRect(offsetX + 15, offsetY + 19, 0, 166, 144, 10);
		float rate = this.container.tileEntity.getCurrentCapacity()
				/ (float) this.container.tileEntity.getMaxCapacity();
		super.drawMagicVolume(offsetX + 15, offsetY + 19, 144, 10, rate, partialTicks);
		this.mc.getTextureManager().bindTexture(GuiMDBase.TEXTURE1);
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
		this.mc.getTextureManager().bindTexture(GuiMDBase.TEXTURE2);
		this.drawTexturedModalRect(offsetX + 14, offsetY + 30, 14, 30, 146, 40);
		this.mc.getTextureManager().bindTexture(GuiMDBase.TEXTURE1);
		this.drawTexturedModalRect(offsetX + 14, offsetY + 18 + 11, 14, 18 + 51, 146, 1);
		// 所有物品栏
		this.drawTexturedModalRect(offsetX + 35, offsetY + 41, 7, 83, 18, 18);
		this.drawTexturedModalRect(offsetX + 57, offsetY + 49, 7, 83, 18, 18);
		this.drawTexturedModalRect(offsetX + 79, offsetY + 57, 7, 83, 18, 18);
		this.drawTexturedModalRect(offsetX + 101, offsetY + 49, 7, 83, 18, 18);
		this.drawTexturedModalRect(offsetX + 123, offsetY + 41, 7, 83, 18, 18);
		// 进度背景
		this.drawTexturedModalRect(offsetX + 41, offsetY + 30, 176, 55, 5, 11);
		this.drawTexturedModalRect(offsetX + 63, offsetY + 30, 176, 55, 5, 19);
		this.drawTexturedModalRect(offsetX + 85, offsetY + 30, 176, 55, 5, 27);
		this.drawTexturedModalRect(offsetX + 107, offsetY + 30, 176, 55, 5, 19);
		this.drawTexturedModalRect(offsetX + 129, offsetY + 30, 176, 55, 5, 11);
		// 进度
		for (int i = 0; i < 5; i++) {
			rate = this.container.tileEntity.getInfusionPower(i)
					/ (float) this.container.tileEntity.getInfusionPowerMax(i);
			if (rate == 0)
				continue;
			int height = MathHelper.ceil(this.progressHeight(i) * rate);
			this.drawTexturedModalRect(offsetX + 41 + i * 22, offsetY + 30, 181, 55, 5, height);
		}

	}

	private final int progressHeight(int index) {
		switch (index) {
		case 0:
		case 4:
			return 11;
		case 1:
		case 3:
			return 19;
		case 2:
			return 27;
		default:
			return 0;
		}
	}

	@Override
	protected boolean showMagicInfo(int mouseX, int mouseY) {
		return this.isMouseIn(mouseX, mouseY, 15, 19, 144, 10);
	}

}
