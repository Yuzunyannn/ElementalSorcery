package yuzunyannn.elementalsorcery.container.gui;

import net.minecraft.client.renderer.GlStateManager;
import yuzunyannn.elementalsorcery.container.ContainerItemStructureCraft;

public class GuiItemStructureCraft extends GuiNormal<ContainerItemStructureCraft> {

	public GuiItemStructureCraft(ContainerItemStructureCraft inventorySlotsIn) {
		super(inventorySlotsIn, inventorySlotsIn.player.inventory);
		container.tileEntity.initGui(this);
	}

	
	@Override
	public String getUnlocalizedTitle() {
		return container.tileEntity.getTitle();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		container.tileEntity.drawGui(this, partialTicks, mouseX, mouseY);
	}

}
