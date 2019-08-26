package yuzunyannn.elementalsorcery.container.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.ContainerMDBase;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.tile.RenderTileMeltCauldron;

public abstract class GuiMDBase<T extends ContainerMDBase<?>> extends GuiNormal<T> {

	public static final ResourceLocation TEXTURE_MAGIC_VOLUME = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/container/magic_volume.png");
	public static final ResourceLocation TEXTURE1 = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/container/md1.png");
	public static final ResourceLocation TEXTURE2 = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/container/md2.png");

	public GuiMDBase(T inventorySlotsIn, InventoryPlayer playerInv) {
		super(inventorySlotsIn, playerInv);
	}

	protected void drawMagicVolume(float xoff, float yoff, int width, int height, float rate, float partialTicks) {
		RenderTileMeltCauldron.TEXTURE_FLUID.bind();
		final int PEAK = 10 / 2;
		final int HEIGHT = (PEAK * 2 + height);
		rate = rate > 1.0f ? 1.0f : rate;
		yoff = yoff + height + PEAK - (height + PEAK * 2) * rate;
		float roate = EventClient.getGlobalRotateInRender(partialTicks) / 180 * 3.1415926f;
		for (int i = 0; i < width; i++) {
			float y = MathHelper.sin(roate + i * 0.15f) * PEAK;
			this.drawTexturedModalRect(xoff + i, yoff + y, i % 16, HEIGHT * ((i / 16) % 2), 1, HEIGHT, 16, 512);
		}
	}

	protected boolean isMouseIn(int mouseX, int mouseY, int x, int y, int width, int height) {
		if (mouseX < x || mouseX >= x + width)
			return false;
		if (mouseY < y || mouseY >= y + height)
			return false;
		return true;
	}

	protected void drawInfo(int x, int y, int color, List<String> strs) {
		int maxWidth = 0;
		for (String str : strs) {
			int width = this.fontRenderer.getStringWidth(str);
			if (width > maxWidth)
				maxWidth = width;
		}
		this.drawToolTipBackground(x, y, maxWidth, this.fontRenderer.FONT_HEIGHT * strs.size());
		for (int i = 0; i < strs.size(); i++)
			this.fontRenderer.drawString(strs.get(i), x, y + this.fontRenderer.FONT_HEIGHT * i, color);
	}

	List<String> infosTmp = new ArrayList<String>();

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		if (this.showMagicInfo(mouseX - offsetX, mouseY - offsetY)) {
			if (mouseX < offsetX + 79 || mouseX > offsetX + 96 || mouseY < offsetY + 60) {
				int x = mouseX - offsetX;
				int y = mouseY - offsetY;
				infosTmp.clear();
				this.getShowMagicInfos(infosTmp);
				this.drawInfo(x, y, 0xffffff, infosTmp);
			}
		}
	}

	/** 添加信息 */
	protected void getShowMagicInfos(List<String> infos) {
		infos.add(this.container.tileEntity.getCurrentCapacity() + "/" + this.container.tileEntity.getMaxCapacity());
		infos.add(this.container.tileEntity.getCurrentPower() + "P");
	}

	/** 是否展示魔力数据 */
	protected boolean showMagicInfo(int mouseX, int mouseY) {
		return this.isMouseIn(mouseX, mouseY, 15, 19, 144, 50);
	}

}
