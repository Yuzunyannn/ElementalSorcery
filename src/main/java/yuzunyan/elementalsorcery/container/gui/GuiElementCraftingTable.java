package yuzunyan.elementalsorcery.container.gui;

import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import yuzunyan.elementalsorcery.ElementalSorcery;
import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.container.ContainerElementCraftingTable;
import yuzunyan.elementalsorcery.event.EventClient;

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

	private int cycle = 0;

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(TEXTURE);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
		if (!container.is_big) {
			this.drawTexturedModalRect(offsetX + 52, offsetY + 21, 3, 21, 36, 36);
			this.drawTexturedModalRect(offsetX + 142, offsetY + 21, 3, 5, 36, 36);
			this.drawTexturedModalRect(offsetX + 52, offsetY + 111, 3, 21, 36, 36);
			this.drawTexturedModalRect(offsetX + 142, offsetY + 111, 3, 5, 36, 36);
		}
		ItemStack stack = container.tile_entity.getOutput();
		if (stack.isEmpty())
			return;
		this.startDrawItem();
		this.drawOnceItem(stack, offsetX + 107, offsetY + 120);
		this.endDrawItem();
		List<ElementStack> list = container.tile_entity.getNeedElements();
		if (list == null)
			return;
		RenderHelper.disableStandardItemLighting();
		if (list.size() > 8) {
			int length = list.size();
			if (EventClient.tick % 40 == 0)
				cycle = (cycle + 1) % 8;
			for (int i = 0; i < 8; i++) {
				ElementStack estack = list.get((cycle + i) % length);
				int x;
				int y = offsetY + 75;
				if (i < 3) {
					x = offsetX + 16 + i * 18;
				} else {
					x = offsetX + 142 + (i - 3) * 18;
				}
				estack.getElement().drawElemntIconInGUI(estack, x + 1, y + 1, mc);
			}
		} else {
			byte left = 0;
			byte right = 0;
			for (ElementStack estack : list) {
				int x;
				int y = offsetY + 75;
				if (left < right) {
					x = offsetX + 70 - 18 * left;
					left++;
				} else {
					x = offsetX + 142 + 18 * right;
					right++;
				}
				estack.getElement().drawElemntIconInGUI(estack, x + 1, y + 1, mc);
			}
		}

	}

}
