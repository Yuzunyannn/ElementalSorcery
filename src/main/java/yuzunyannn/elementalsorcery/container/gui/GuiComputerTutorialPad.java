package yuzunyannn.elementalsorcery.container.gui;

import java.io.IOException;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.container.ContainerComputer;

@SideOnly(Side.CLIENT)
public class GuiComputerTutorialPad extends GuiComputerBase {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ESAPI.MODID,
			"textures/gui/computer/tutorial_pad.png");

	public GuiComputerTutorialPad(ContainerComputer containerComputer) {
		super(containerComputer);
		this.xSize = 256;
		this.ySize = 159;
		this.computerX = 8;
		this.computerY = 8;
		this.computerWidth = 240;
		this.computerHeight = 135;
	}

	@Override
	public void initGui() {
		super.initGui();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(TEXTURE);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
	}

	@Override
	protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
		super.handleMouseClick(slotIn, slotId, mouseButton, type);

	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
//		this.sendOpenComputer();
	}

}
