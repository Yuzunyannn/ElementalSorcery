package yuzunyannn.elementalsorcery.container.gui.elf;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.container.ContainerElfSendParcel;
import yuzunyannn.elementalsorcery.container.gui.GuiNormal;

@SideOnly(Side.CLIENT)
public class GuiElfSendParcel extends GuiNormal<ContainerElfSendParcel> {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ESAPI.MODID,
			"textures/gui/elf/send_parcel.png");

	protected GuiTextField address;

	public GuiElfSendParcel(EntityPlayer player) {
		super(new ContainerElfSendParcel(player), player);
	}

	@Override
	public void initGui() {
		super.initGui();
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		address = new GuiTextField(0, this.fontRenderer, offsetX + 51, offsetY + 28, 89, 12);
		address.setMaxStringLength(35);
		address.setEnableBackgroundDrawing(false);
		this.addButton(new SubmitButton(1, offsetX + 71, offsetY + 63));
	}

	public static class SubmitButton extends GuiButton {
		public SubmitButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 32, 17, I18n.format("say.submit"));
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			if (!this.visible) return;
			FontRenderer fontRenderer = mc.fontRenderer;
			int x = mouseX - this.x, y = mouseY - this.y;
			if (x >= 0 && y >= 0 && x < this.width && y < this.height)
				drawTexturedModalRect(this.x, this.y, 176, 17, this.width, this.height);
			else drawTexturedModalRect(this.x, this.y, 176, 0, this.width, this.height);
			this.drawCenteredString(fontRenderer, this.displayString, this.x + this.width / 2,
					this.y + (this.height - 8) / 2, 14737632);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
		address.drawTextBox();
	}

	@Override
	public String getUnlocalizedTitle() {
		return "say.send.parcel";
	}

	int sendCooldown;

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (sendCooldown > 0) return;
		if (button.id != 1) return;
		sendCooldown = 30;
		container.submit(address.getText());
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (sendCooldown > 0) sendCooldown--;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.mc.getTextureManager().bindTexture(TEXTURE);
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
		this.drawTexturedModalRect(offsetX + 33, offsetY + 21, 0, 166, 108, 40);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (this.address.textboxKeyTyped(typedChar, keyCode)) {

		} else super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		address.mouseClicked(mouseX, mouseY, mouseButton);
	}
}
