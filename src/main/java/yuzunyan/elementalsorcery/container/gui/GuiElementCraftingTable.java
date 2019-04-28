package yuzunyan.elementalsorcery.container.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import yuzunyan.elementalsorcery.ElementalSorcery;
import yuzunyan.elementalsorcery.container.ContainerElementCraftingTable;

public class GuiElementCraftingTable extends GuiNormal {

	final ContainerElementCraftingTable container;

	public static final ResourceLocation TEXTURE = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/container/element_crafting_table.png");

	public GuiElementCraftingTable(ContainerElementCraftingTable inventorySlotsIn, InventoryPlayer playerInv) {
		super(inventorySlotsIn, playerInv);
		this.xSize = 230;
		this.ySize = 242;
		this.container = inventorySlotsIn;
	}

	@Override
	public String getUnlocalizedTitle() {
		return "tile.elementCraftingTable.name";
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String s = I18n.format(this.getUnlocalizedTitle());
		this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
		this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 36,
				this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(TEXTURE);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
		if (!container.is_big) {
			this.drawTexturedModalRect(offsetX + 52, offsetY + 21, 5, 5, 36, 126);
			this.drawTexturedModalRect(offsetX + 142, offsetY + 21, 5, 5, 36, 126);
		}
		ItemStack stack = container.tile_entity.getOutput();
		if (!stack.isEmpty()) {
			this.startDrawItem();
			this.drawOnceItem(stack, offsetX + 107, offsetY + 120);
			this.endDrawItem();
		}
	}

}
