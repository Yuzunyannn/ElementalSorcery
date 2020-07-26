package yuzunyannn.elementalsorcery.container.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.ContainerSmeltBox;
import yuzunyannn.elementalsorcery.tile.TileSmeltBox;

public class GuiSmeltBox extends GuiNormal {
	private static final ResourceLocation TEXTURE = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/container/smelt_box.png");
	protected ContainerSmeltBox inventory;

	public GuiSmeltBox(ContainerSmeltBox inventorySlotsIn, InventoryPlayer playerInv) {
		super(inventorySlotsIn, playerInv);
		inventory = inventorySlotsIn;
	}

	public String getUnlocalizedTitle() {
		return inventory.getTileEntity().getBlockUnlocalizedName();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);

		this.mc.getTextureManager().bindTexture(TEXTURE);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);

		TileSmeltBox tile_entity = inventory.getTileEntity();
		float burn_time = tile_entity.getField(TileSmeltBox.FIELD_BURN_TIME);
		float total_burn_time = tile_entity.getCookTime();
		int tex_width = 1 + (int) Math.ceil(24.0 * burn_time / total_burn_time);
		this.drawTexturedModalRect(offsetX + 73, offsetY + 26, 176, 18, tex_width, 17);

		if (tile_entity.canUseExtraItem()) {
			this.drawTexturedModalRect(offsetX + 76, offsetY + 45, 176, 0, 18, 18);
		}
	}
}
