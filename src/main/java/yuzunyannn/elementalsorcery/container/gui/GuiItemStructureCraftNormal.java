package yuzunyannn.elementalsorcery.container.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.container.ContainerItemStructureCraft;

public class GuiItemStructureCraftNormal extends GuiItemStructureCraft {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ESAPI.MODID,
			"textures/gui/container/is_craft_normal.png");

	public GuiItemStructureCraftNormal(ContainerItemStructureCraft inventorySlotsIn) {
		super(inventorySlotsIn);
	}

	@Override
	protected void initSlot() {
		this.hasTypeStack = false;
		this.hasAnime = false;

		this.slotMapCenterXOffset = this.xSize / 2 - 18;
		this.slotMapCenterYOffset = 82 / 2 + 5;
		this.typeStackXOffset = 4;
		this.typeStackYOffset = 4;
		this.ouputXOffset = this.slotMapCenterXOffset + 3 * 18;
		this.ouputYOffset = this.slotMapCenterYOffset;
	}

	@Override
	protected void drawBackground() {
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
	}

	@Override
	protected void drawSlot(boolean hasSelect) {
		RenderFriend.drawTexturedRectInCenter(0, 0, 18, 18, 176, 0, 18, 18, 256, 256);
		if (hasSelect) RenderFriend.drawTexturedRectInCenter(0, 0, 18, 18, 176, 18, 18, 18, 256, 256);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String s = I18n.format("tile.ISCraftNormal.name");
		this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
	}
}
