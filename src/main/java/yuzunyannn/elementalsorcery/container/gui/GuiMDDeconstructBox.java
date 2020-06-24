package yuzunyannn.elementalsorcery.container.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.MathHelper;
import yuzunyannn.elementalsorcery.container.ContainerMDDeconstructBox;
import yuzunyannn.elementalsorcery.tile.md.TileMDDeconstructBox;

public class GuiMDDeconstructBox extends GuiMDBase<ContainerMDDeconstructBox> {

	public GuiMDDeconstructBox(ContainerMDDeconstructBox inventorySlotsIn, InventoryPlayer playerInv) {
		super(inventorySlotsIn, playerInv);
	}

	@Override
	public String getUnlocalizedTitle() {
		return "tile.MDDeconstructBox.name";
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.drawDefault(offsetX, offsetY, 59, 10, partialTicks);

		this.drawTexturedModalRect(offsetX + 74, offsetY + 34, 176, 82, 29, 9);

		this.drawTexturedModalRect(offsetX + 55, offsetY + 30, 7, 83, 18, 18);
		this.drawTexturedModalRect(offsetX + 104, offsetY + 30, 7, 83, 18, 18);

		TileMDDeconstructBox tile = this.container.tileEntity;
		float rate = (float) tile.getProgress() / (float) tile.getMaxProgress();
		int color = tile.getColor();
		float b = (color & 0xff) / 255.0f;
		float g = (color >> 8 & 0xff) / 255.0f;
		float r = (color >> 16 & 0xff) / 255.0f;
		GlStateManager.color(r, g, b);
		this.drawTexturedModalRect(offsetX + 74, offsetY + 34, 176, 82 + 9, MathHelper.ceil(29 * rate), 9);
	}

}
