package yuzunyannn.elementalsorcery.container.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.ContainerTranscribeInjection;

public class GuiTranscribeInjection extends GuiNormal {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/container/transcribe_injection.png");
	protected ContainerTranscribeInjection inventory;

	public GuiTranscribeInjection(ContainerTranscribeInjection inventorySlotsIn) {
		super(inventorySlotsIn, inventorySlotsIn.player.inventory);
		inventory = inventorySlotsIn;
	}

	public String getUnlocalizedTitle() {
		return "tile.transcribeInjection.name";
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String s = this.getDisplayTitle();
		this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
		this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8,
				this.ySize - 152 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(TEXTURE);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
	}

}
