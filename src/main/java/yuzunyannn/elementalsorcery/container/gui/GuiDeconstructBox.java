package yuzunyannn.elementalsorcery.container.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.ContainerDeconstructBox;
import yuzunyannn.elementalsorcery.tile.TileDeconstructBox;

public class GuiDeconstructBox extends GuiNormal<ContainerDeconstructBox> {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/container/deconstruct_box.png");

	public GuiDeconstructBox(ContainerDeconstructBox inventorySlotsIn, InventoryPlayer playerInv) {
		super(inventorySlotsIn, playerInv);
	}

	@Override
	public String getUnlocalizedTitle() {
		return "tile.deconstructBox.name";
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);

		this.mc.getTextureManager().bindTexture(TEXTURE);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);

		int total_power = container.tileEntity.getField(TileDeconstructBox.FIELD_TOTAL_POWER);
		int power = container.tileEntity.getField(TileDeconstructBox.FIELD_POWER);
		int tex_width = (int) Math.ceil(29.0 * power / total_power);
		this.drawTexturedModalRect(offsetX + 75, offsetY + 40, 176, 0, tex_width, 9);
	}

}
