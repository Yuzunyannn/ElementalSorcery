package yuzunyannn.elementalsorcery.container.gui;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.container.ContainerComputerEditor;
import yuzunyannn.elementalsorcery.tile.device.TileComputer;

@SideOnly(Side.CLIENT)
public class GuiComputerEditor extends GuiNormal<ContainerComputerEditor> {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ESAPI.MODID,
			"textures/gui/container/craft_player_inventory.png");
//	public static final ResourceLocation TEXTURE = GuiComputerCloverPad.TEXTURE;

	public String displayName;
	public int cXSize = 176;

	public GuiComputerEditor(ContainerComputerEditor inventorySlotsIn) {
		super(inventorySlotsIn, inventorySlotsIn.player.inventory);
		TileComputer tileComputer = inventorySlotsIn.tileEntity;
		Block block = tileComputer.getWorld().getBlockState(tileComputer.getPos()).getBlock();
		displayName = block.getLocalizedName();
		this.xSize = 256;
	}

	public String getUnlocalizedTitle() {
		return displayName;
	}

	@Override
	public String getDisplayTitle() {
		return displayName;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String s = this.getDisplayTitle();
		int color = 0xffffff;
		this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, color);
		this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 48, this.ySize - 99, color);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(TEXTURE);
		int offsetX = (this.width - this.cXSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.cXSize, this.ySize);
		this.drawTexturedModalRect(offsetX + 91, offsetY + 52, 29, 16, 18, 18);

		int type = container.slotType;
		if (type == ContainerComputerEditor.SLOT_COLVER_FRONT) {
			this.mc.getTextureManager().bindTexture(GuiComputerClover.TEXTURE);
			int xoffset = offsetX + (this.cXSize - this.xSize) / 2;
			int yoffset = offsetY + 28;
			this.drawTexturedModalRect(xoffset, yoffset, 0, 188, 256, 27);
			IComputer computer = container.getComputer();
			if (computer != null) {
				if (computer.isPowerOn()) {
					this.drawTexturedModalRect(xoffset + 213, yoffset + 5, 212, 216, 16, 16);
				}
			}
		}
	}

}
