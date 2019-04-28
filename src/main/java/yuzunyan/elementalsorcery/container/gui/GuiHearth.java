package yuzunyan.elementalsorcery.container.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import yuzunyan.elementalsorcery.ElementalSorcery;
import yuzunyan.elementalsorcery.container.ContainerHearth;
import yuzunyan.elementalsorcery.tile.TileHearth;

public class GuiHearth extends GuiNormal {

	private static final ResourceLocation TEXTURE = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/container/hearth.png");
	protected ContainerHearth inventory;
	private final InventoryPlayer playerInventory;

	public GuiHearth(ContainerHearth inventorySlotsIn, InventoryPlayer playerInv) {
		super(inventorySlotsIn, playerInv);
		inventory = inventorySlotsIn;
		playerInventory = playerInv;
	}

	public String getUnlocalizedTitle() {
		return inventory.getUnlocalizedName();
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
