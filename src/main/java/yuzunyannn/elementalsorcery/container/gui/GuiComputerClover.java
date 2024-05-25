package yuzunyannn.elementalsorcery.container.gui;

import java.io.IOException;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.RenderRect;
import yuzunyannn.elementalsorcery.api.util.client.RenderTexutreFrame;
import yuzunyannn.elementalsorcery.container.ContainerComputer;

@SideOnly(Side.CLIENT)
public class GuiComputerClover extends GuiComputerBase {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ESAPI.MODID,
			"textures/gui/computer/clover_pad.png");
	public static final RenderTexutreFrame BG_FRAME = RenderTexutreFrame.ofGUI(0, 0, 256, 158);
	public static final RenderTexutreFrame BG_HALF_FRAME = RenderTexutreFrame.ofGUI(0, 0, 256, 147);
	public static final RenderRect SPLIT9_UP_RECT = new RenderRect(1, 1, 1 / 3f, 2 / 3f);

	public final boolean isPadMode;

	public GuiComputerClover(ContainerComputer containerComputer, String appearance) {
		super(containerComputer);
		isPadMode = appearance.endsWith("Pad");
		if (isPadMode) {
			this.xSize = 256;
			this.ySize = 158;
		} else {
			this.xSize = 256;
			this.ySize = 147;
		}
		this.computerX = 0;
		this.computerY = 0;
		this.computerWidth = 256;
		this.computerHeight = 144;
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
		if (isPadMode) {
			RenderFriend.drawSplit9FrameInCenter(offsetX + this.xSize / 2, offsetY + this.ySize / 2 - 3.5, this.xSize + 16, this.ySize + 9.875, BG_FRAME, RenderFriend.SPLIT9_AVERAGE_RECT);
		} else {
			int x = offsetX + this.xSize / 2;
			double y = offsetY + this.ySize / 2 - 3.125;
			double width = this.xSize + 16;
			RenderFriend.drawSplit9FrameInCenter(x, y, width, this.ySize * 1.0625, BG_HALF_FRAME, RenderFriend.SPLIT9_AVERAGE_RECT);
			RenderFriend.drawSplit9FrameInCenter(x, y + 80, width, 4 * 1.0625, RenderTexutreFrame.ofGUI(0, 154, 256, 4), SPLIT9_UP_RECT);
		}

		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
	}

	@Override
	protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
		super.handleMouseClick(slotIn, slotId, mouseButton, type);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

}
