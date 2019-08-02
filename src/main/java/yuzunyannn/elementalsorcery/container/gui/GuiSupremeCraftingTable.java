package yuzunyannn.elementalsorcery.container.gui;

import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.container.ContainerSupremeCraftingTable;
import yuzunyannn.elementalsorcery.event.EventClient;

public class GuiSupremeCraftingTable extends GuiNormal {

	final ContainerSupremeCraftingTable container;

	public static final ResourceLocation TEXTURE = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/container/element_crafting_table.png");

	public GuiSupremeCraftingTable(ContainerSupremeCraftingTable inventorySlotsIn, InventoryPlayer playerInv) {
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
		// 画其他合成
		switch (this.container.showMode) {
		case ContainerSupremeCraftingTable.MODE_NONE:
			return;
		case ContainerSupremeCraftingTable.MODE_NATIVE_CRAFTING:
			this.drawTexturedModalRect(offsetX + 106, offsetY + 111, 230, 24, 18, 18);
			return;
		case ContainerSupremeCraftingTable.MODE_ELEMENT_CRAFTING:
			this.drawTexturedModalRect(offsetX + 106, offsetY + 111, 230, 60, 18, 18);
			break;
		case ContainerSupremeCraftingTable.MODE_PLATFORM_NONE:
			this.drawTexturedModalRect(offsetX + 104, offsetY + 147, 230, 0, 23, 6);
			break;
		}
		List<ElementStack> list = container.tileEntity.getNeedElements();
		if (list == null)
			return;
		RenderHelper.disableStandardItemLighting();
		this.drawElements(offsetX, offsetY, list);
	}

	private int cycle = 0;

	private void drawElements(int offsetX, int offsetY, List<ElementStack> list) {
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
