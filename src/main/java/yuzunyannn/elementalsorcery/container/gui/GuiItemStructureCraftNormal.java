package yuzunyannn.elementalsorcery.container.gui;

import net.minecraft.client.renderer.GlStateManager;
import yuzunyannn.elementalsorcery.container.ContainerItemStructureCraftNormal;

public class GuiItemStructureCraftNormal extends GuiNormal<ContainerItemStructureCraftNormal> {

	public GuiItemStructureCraftNormal(ContainerItemStructureCraftNormal inventorySlotsIn) {
		super(inventorySlotsIn, inventorySlotsIn.player.inventory);
	}

	@Override
	public String getUnlocalizedTitle() {
		return "";
	}

	@Override
	public String getDisplayTitle() {
		return container.tileEntity.getDisplayTitle();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		container.tileEntity.drawGui(this, partialTicks, mouseX, mouseY);
	}

}
