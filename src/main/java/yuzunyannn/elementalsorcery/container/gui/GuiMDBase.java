package yuzunyannn.elementalsorcery.container.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.container.ContainerMDBase;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.render.tile.RenderTileMeltCauldron;

public abstract class GuiMDBase<T extends ContainerMDBase<?>> extends GuiNormal<T> {

	public static final ResourceLocation TEXTURE_MAGIC_VOLUME = new ResourceLocation(ESAPI.MODID,
			"textures/gui/container/magic_volume.png");
	public static final ResourceLocation TEXTURE1 = new ResourceLocation(ESAPI.MODID,
			"textures/gui/container/md1.png");
	public static final ResourceLocation TEXTURE2 = new ResourceLocation(ESAPI.MODID,
			"textures/gui/container/md2.png");

	public GuiMDBase(T inventorySlotsIn, InventoryPlayer playerInv) {
		super(inventorySlotsIn, playerInv);
	}

	public static void drawMagicVolume(float xoff, float yoff, int width, int height, float rate, float partialTicks) {
		RenderTileMeltCauldron.TEXTURE_FLUID.bind();
		final int PEAK = 10 / 2;
		final int HEIGHT = (PEAK * 2 + height);
		rate = rate > 1.0f ? 1.0f : rate;
		yoff = yoff + height + PEAK - (height + PEAK * 2) * rate;
		float roate = EventClient.getGlobalRotateInRender(partialTicks) / 180 * 3.1415926f;
		for (int i = 0; i < width; i++) {
			float y = MathHelper.sin(roate + i * 0.15f) * PEAK;
			RenderFriend.drawTextureModalRect(xoff + i, yoff + y, i % 16, HEIGHT * ((i / 16) % 2), 1, HEIGHT, 16, 512);
		}
	}

	protected void drawInfo(int x, int y, int color, List<String> strs) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 500);
		int maxWidth = 0;
		for (String str : strs) {
			int width = this.fontRenderer.getStringWidth(str);
			if (width > maxWidth) maxWidth = width;
		}
		int height = this.fontRenderer.FONT_HEIGHT * strs.size();
		this.drawToolTipBackground(x, y, maxWidth, height);
		for (int i = 0; i < strs.size(); i++)
			this.fontRenderer.drawString(strs.get(i), x, y + this.fontRenderer.FONT_HEIGHT * i, color);
		GlStateManager.popMatrix();
	}

	List<String> infosTmp = new ArrayList<String>();

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		if (this.showMagicInfo(mouseX - offsetX, mouseY - offsetY)) {
			int x = mouseX - offsetX + 8;
			int y = mouseY - offsetY;
			infosTmp.clear();
			this.getShowMagicInfos(infosTmp);
			this.drawInfo(x, y, 0xffffff, infosTmp);
		}
	}

	/** 添加信息 */
	protected void getShowMagicInfos(List<String> infos) {
		infos.add(this.container.tileEntity.getCurrentCapacity() + "/" + this.container.tileEntity.getMaxCapacity());
		infos.add(this.container.tileEntity.getCurrentPower() + "P");
	}

	/** 是否展示魔力数据 */
	protected boolean showMagicInfo(int mouseX, int mouseY) {
		return GuiNormal.isMouseIn(mouseX, mouseY, 15, 59, 144, 10);
	}

	/** 基础绘画 */
	protected void drawDefault(int offsetX, int offsetY, int volumeY, int volumeHeight, float partialTicks) {
		float rate = this.container.tileEntity.getCurrentCapacity()
				/ (float) this.container.tileEntity.getMaxCapacity();
		drawDefault(mc, rate, xSize, ySize, offsetX, offsetY, volumeY, volumeHeight, partialTicks, TEXTURE1, TEXTURE2);
	}

	/** 基础绘画 */
	public static void drawDefault(Minecraft mc, float rate, int xSize, int ySize, int offsetX, int offsetY,
			int volumeY, int volumeHeight, float partialTicks, ResourceLocation TEXTURE1, ResourceLocation TEXTURE2) {
		volumeY = 19 + 50 - volumeHeight;
		mc.getTextureManager().bindTexture(TEXTURE1);
		RenderFriend.drawTextureModalRect(offsetX + 15, offsetY + volumeY, 0, 166, 144, volumeHeight, 256, 256);
		drawMagicVolume(offsetX + 15, offsetY + volumeY, 144, volumeHeight, rate, partialTicks);
		mc.getTextureManager().bindTexture(TEXTURE1);
		RenderFriend.drawTextureModalRect(offsetX, offsetY, 0, 0, xSize, ySize, 256, 256);
		mc.getTextureManager().bindTexture(TEXTURE2);
		RenderFriend.drawTextureModalRect(offsetX + 14, offsetY + 18, 14, 18, 146, 50 - volumeHeight, 256, 256);
		mc.getTextureManager().bindTexture(TEXTURE1);
		RenderFriend.drawTextureModalRect(offsetX + 14, offsetY + 18 + 50 - volumeHeight, 14, 18, 146, 1, 256, 256);
	}

}
