package yuzunyannn.elementalsorcery.container.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.container.ContainerHearth;
import yuzunyannn.elementalsorcery.tile.TileHearth;

public class GuiHearth extends GuiNormal {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ESAPI.MODID,
			"textures/gui/container/hearth.png");
	protected ContainerHearth inventory;

	public GuiHearth(ContainerHearth inventorySlotsIn) {
		super(inventorySlotsIn, inventorySlotsIn.player.inventory);
		inventory = inventorySlotsIn;
	}

	public String getUnlocalizedTitle() {
		return inventory.getTranslationKey();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);

		this.mc.getTextureManager().bindTexture(TEXTURE);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);

		float burn_time = inventory.getField(TileHearth.FIELD_BURN_TIME);
		float total_burn_time = inventory.getField(TileHearth.FIELD_TOTAL_BURN_TIME);
		int tex_height = (int) Math.ceil(14.0 * burn_time / total_burn_time);
		int hoff = (int) Math.ceil(14.0 - tex_height);
		this.drawTexturedModalRect(offsetX + 80, offsetY + 22 + hoff, 176, hoff, 14, tex_height);
	}

}
