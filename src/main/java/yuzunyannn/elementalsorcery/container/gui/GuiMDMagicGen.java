package yuzunyannn.elementalsorcery.container.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.ContainerMDMagicGen;
import yuzunyannn.elementalsorcery.event.EventClient;

public class GuiMDMagicGen extends GuiNormal {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/container/magic_gen.png");
	final ContainerMDMagicGen container;

	public GuiMDMagicGen(ContainerMDMagicGen inventorySlotsIn, InventoryPlayer playerInv) {
		super(inventorySlotsIn, playerInv);
		this.container = inventorySlotsIn;
	}

	@Override
	public String getUnlocalizedTitle() {
		return "tile.magicGen.name";
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		this.mc.getTextureManager().bindTexture(TEXTURE);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		GlStateManager.disableDepth();
		this.drawTexturedModalRect(offsetX + 15, offsetY + 19, 0, 166, 144, 50);
		this.drawT(offsetX, offsetY);
		this.drawMagicVolume(offsetX, offsetY, partialTicks);
		this.drawMagicStoneMelt(offsetX, offsetY, partialTicks);
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
		this.drawTexturedModalRect(offsetX + 79, offsetY + 60, 7, 83, 18, 18);
	}

	protected void drawT(int offsetX, int offsetY) {
		float T = Math.min(this.container.tileEntity.getTemperature(), 500);
		if (T < 1.0f)
			return;
		GlStateManager.disableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.color(1.0f, 0f, 0f, T / 600.0f);
		this.drawTexturedModalRect(offsetX + 15, offsetY + 19, 144, 166, 72, 50);
		this.drawTexturedModalRect(offsetX + 15 + 72, offsetY + 19, 144, 166, 72, 50);
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
	}

	protected void drawMagicVolume(int offsetX, int offsetY, float partialTicks) {
		final int WIDTH = 32;
		final int HEAD_HEIGHT = 10 / 2;
		final int HEIGHT = HEAD_HEIGHT * 2 + 50;
		float y = 69 + HEAD_HEIGHT - (50 + HEAD_HEIGHT * 2) * this.container.tileEntity.getCurrentCapacity()
				/ (float) this.container.tileEntity.getMaxCapacity();
		y = y < 19 - HEAD_HEIGHT ? 19 - HEAD_HEIGHT : y;
		float roate = EventClient.getGlobalRotateInRender(partialTicks) / 180 * 3.1415926f;
		for (int i = 0; i < 144; i++) {
			float yoff = MathHelper.sin(roate + i * 0.15f) * HEAD_HEIGHT;
			final int ux = 176 + i % WIDTH;
			this.drawTexturedModalRect(offsetX + 15 + i, offsetY + y + yoff, ux, 0, 1, HEIGHT);
		}
	}

	protected float moveX;

	protected void drawMagicStoneMelt(int offsetX, int offsetY, float partialTicks) {
		float rate = this.container.tileEntity.getMeltRate();
		if (rate == 0)
			return;
		else if (rate == 1)
			moveX = (float) (Math.random() * 138 + 3);
		int x = (int) (offsetX + 15 + moveX);
		int y = (int) (offsetY + 19 + 50 - 1 - rate * 13);
		this.drawItem(this.container.tileEntity.renderItem, x, y);
		this.mc.getTextureManager().bindTexture(TEXTURE);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		if (mouseX > offsetX + 14 && mouseX < +offsetX + 159 && mouseY > offsetY + 18 && mouseY < offsetY + 69) {
			if (mouseX < offsetX + 79 || mouseX > offsetX + 96 || mouseY < offsetY + 60) {
				String str = this.container.tileEntity.getCurrentCapacity() + "/"
						+ this.container.tileEntity.getMaxCapacity();
				int width = this.fontRenderer.getStringWidth(str);
				int x = mouseX - offsetX;
				int y = mouseY - offsetY;
				this.drawToolTipBackground(x, y, width, this.fontRenderer.FONT_HEIGHT * 2);
				this.fontRenderer.drawString(str, x, y, 0xffffff);
				this.fontRenderer.drawString(this.container.tileEntity.getTemperature() + "℃", x,
						y + this.fontRenderer.FONT_HEIGHT, 0xffffff);

			}
		}
	}

}
