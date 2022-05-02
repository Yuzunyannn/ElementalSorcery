package yuzunyannn.elementalsorcery.container.gui.elf;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.container.ContainerElfApplyAddressPlate;
import yuzunyannn.elementalsorcery.container.gui.GuiNormal;

@SideOnly(Side.CLIENT)
public class GuiElfApplyAddressPlate extends GuiNormal<ContainerElfApplyAddressPlate> {

	protected GuiTextField address;

	public GuiElfApplyAddressPlate(EntityPlayer player) {
		super(new ContainerElfApplyAddressPlate(player), player);
	}

	@Override
	public void initGui() {
		super.initGui();
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		address = new GuiTextField(0, this.fontRenderer, offsetX + 51, offsetY + 28 + 10, 89, 12);
		address.setMaxStringLength(35);
		address.setEnableBackgroundDrawing(false);
		this.addButton(new GuiElfSendParcel.SubmitButton(1, offsetX + 71, offsetY + 63));
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
		return "say.apply.address.plate";
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
		this.mc.getTextureManager().bindTexture(GuiElfSendParcel.TEXTURE);
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
		this.drawTexturedModalRect(offsetX + 33, offsetY + 21 + 10, 0, 166, 108, 17);
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
