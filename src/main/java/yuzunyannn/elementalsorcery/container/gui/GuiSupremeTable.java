package yuzunyannn.elementalsorcery.container.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.ContainerSupremeTable;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.event.EventClient;

public class GuiSupremeTable extends GuiNormal<ContainerSupremeTable> {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/container/element_crafting_table.png");

	public GuiSupremeTable(ContainerSupremeTable inventorySlotsIn, InventoryPlayer playerInv) {
		super(inventorySlotsIn, playerInv);
		this.xSize = 230;
		this.ySize = 242;
	}

	@Override
	public String getUnlocalizedTitle() {
		return "tile.supremeTable.name";
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
		case ContainerSupremeTable.MODE_NONE:
			return;
		case ContainerSupremeTable.MODE_NATIVE_CRAFTING:
			this.drawTexturedModalRect(offsetX + 106, offsetY + 111, 230, 24, 18, 18);
			return;
		case ContainerSupremeTable.MODE_ELEMENT_CRAFTING:
			this.drawTexturedModalRect(offsetX + 106, offsetY + 111, 230, 60, 18, 18);
			break;
		case ContainerSupremeTable.MODE_PLATFORM_NONE:
			this.drawTexturedModalRect(offsetX + 104, offsetY + 147, 230, 0, 23, 6);
			return;
		case ContainerSupremeTable.MODE_DECONSTRUCT:
			this.drawTexturedModalRectMirrorHeight(offsetX + 106, offsetY + 111, 230, 42, 18, 18);
			this.drawTexturedModalRect(offsetX + 88, offsetY + 75, 230, 6, 18, 18);
			this.drawTexturedModalRectMirrorWidth(offsetX + 124, offsetY + 75, 230, 6, 18, 18);
			this.drawTexturedModalRect(offsetX + 104, offsetY + 147, 230, 0, 23, 6);
			break;
		case ContainerSupremeTable.MODE_CONSTRUCT:
			this.drawTexturedModalRect(offsetX + 106, offsetY + 111, 230, 78, 18, 18);
			this.drawTexturedModalRect(offsetX + 104, offsetY + 147, 230, 0, 23, 6);
			break;
		}
		List<ElementStack> list = container.tileEntity.getNeedElements();
		if (list == null) return;
		RenderHelper.disableStandardItemLighting();
		drawElements(mc, offsetX, offsetY, list, 8, (EventClient.tick / 40) % 8);
	}

	public static void drawElements(Minecraft mc, int offsetX, int offsetY, List<ElementStack> list, int size,
			int cycle) {
		if (list.size() > size) {
			int length = list.size();
			for (int i = 0; i < size; i++) {
				ElementStack estack = list.get((cycle + i) % length);
				int x;
				int y = offsetY + 75;
				if (i < size / 2) x = offsetX + 34 + i * 18;
				else x = offsetX + 142 + (i - size / 2) * 18;
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
