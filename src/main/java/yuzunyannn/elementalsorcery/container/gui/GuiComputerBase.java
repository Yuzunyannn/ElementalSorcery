package yuzunyannn.elementalsorcery.container.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.container.ContainerComputer;

@SideOnly(Side.CLIENT)
public abstract class GuiComputerBase extends GuiContainer {

	protected final ContainerComputer containerComputer;

	public GuiComputerBase(ContainerComputer containerComputer) {
		super(containerComputer);
		this.containerComputer = containerComputer;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
//		super.drawScreen(mouseX, mouseY, partialTicks);
//		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

	}

	@Override
	public void onResize(Minecraft mcIn, int w, int h) {
		super.onResize(mcIn, w, h);
	}

}
