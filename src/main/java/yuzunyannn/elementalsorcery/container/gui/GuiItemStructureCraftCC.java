package yuzunyannn.elementalsorcery.container.gui;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.container.ContainerItemStructureCraft;

@SideOnly(Side.CLIENT)
public class GuiItemStructureCraftCC extends GuiItemStructureCraft {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ESAPI.MODID,
			"textures/gui/container/is_craft_cc.png");

	public GuiItemStructureCraftCC(ContainerItemStructureCraft inventorySlotsIn) {
		super(inventorySlotsIn);
		this.xSize = 230;
		this.ySize = 242;
	}

	@Override
	protected void initSlot() {
		this.slotMapCenterXOffset = this.xSize / 2;
		this.slotMapCenterYOffset = 152 / 2;
		this.typeStackXOffset = 4;
		this.typeStackYOffset = 4;
		this.ouputXOffset = this.slotMapCenterXOffset + 5 * 18 + 2;
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
		RenderFriend.drawTextureRectInCenter(0, 0, 18, 18, 230, 0, 18, 18, 256, 256);
		if (hasSelect) RenderFriend.drawTextureRectInCenter(0, 0, 18, 18, 230, 18, 18, 18, 256, 256);
	}

}
