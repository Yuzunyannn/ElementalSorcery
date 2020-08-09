package yuzunyannn.elementalsorcery.container.gui;

import java.io.IOException;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.ContainerElfTalk;
import yuzunyannn.elementalsorcery.elf.talk.TalkChapter;
import yuzunyannn.elementalsorcery.elf.talk.TalkType;
import yuzunyannn.elementalsorcery.elf.talk.Talker;

public class GuiElfTalk extends GuiContainer {

	private static final ResourceLocation TEXTURE = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/elf/elf_talk.png");
	private static final int SELECT_WIDTH = 198;
	private static final int SELECT_HEIGHT = 30;

	public final ContainerElfTalk container;

	public GuiElfTalk(EntityPlayer player) {
		super(new ContainerElfTalk(player));
		container = (ContainerElfTalk) inventorySlots;
		this.xSize = 400;
		this.ySize = 98;
	}

	@Override
	public void initGui() {
		super.initGui();
		this.guiTop = this.height - this.ySize;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	String drawValue = TalkChapter.NOTHING_TO_SAY;
	String currDrawValue = TalkChapter.NOTHING_TO_SAY;

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		int offsetX = this.guiLeft, offsetY = this.guiTop;
		TalkChapter.Iter iter = container.getChapterIter();
		if (iter == null || iter.getType() == TalkType.SAY) {
			boolean isPlayer = false;
			String talker;
			EntityLivingBase entity = null;
			if (iter == null) talker = I18n.format("entity.Elf.name");
			else if (iter.getTalker() == Talker.PLAYER) {
				talker = container.player.getName();
				entity = container.player;
				isPlayer = true;
			} else if (container.elf == null) talker = I18n.format("entity.Elf.name");
			else {
				talker = container.elf.getName();
				entity = container.elf;
			}
			if (entity != null) {
				int xoff = this.width / 2 - (isPlayer ? -125 : 125);
				int yoff = offsetY + 25;
				GuiInventory.drawEntityOnScreen(xoff, yoff, 75, isPlayer ? 100 : -100, -10, entity);
			}
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0, 100);
			mc.getTextureManager().bindTexture(TEXTURE);
			drawModalRectWithCustomSizedTexture(offsetX, offsetY, 0, 0, this.xSize, this.ySize, 512, 256);
			offsetY += 22;
			int width = (int) (this.xSize * 0.9);
			offsetX += (this.xSize - width) / 2;
			this.fontRenderer.drawSplitString(currDrawValue, offsetX, offsetY, width, 0);
			this.fontRenderer.drawString(talker, this.guiLeft + 6, this.guiTop + 5, 0);
			GlStateManager.popMatrix();
		} else {
			{
				int xoff = 25;
				int yoff = this.height - 10;
				GuiInventory.drawEntityOnScreen(xoff, yoff, 30, xoff - mouseX, yoff - mouseY, container.player);
			}
			mc.getTextureManager().bindTexture(TEXTURE);
			String[] says = (String[]) iter.getSaying();
			offsetX = (this.width - SELECT_WIDTH) / 2;
			for (int i = 0; i < says.length; i++) {
				int yoff = selectY(i, says.length);
				int nextYoff = selectY(i + 1, says.length);
				if (mouseX < offsetX || mouseX > offsetX + SELECT_WIDTH || mouseY < yoff
						|| mouseY > Math.min(nextYoff, yoff + SELECT_HEIGHT))
					drawModalRectWithCustomSizedTexture(offsetX, yoff, 0, 101, SELECT_WIDTH, SELECT_HEIGHT, 512, 256);
				else drawModalRectWithCustomSizedTexture(offsetX, yoff, 0, 131, SELECT_WIDTH, SELECT_HEIGHT, 512, 256);
			}
			for (int i = 0; i < says.length; i++) {
				int yoff = selectY(i, says.length) + (SELECT_HEIGHT - this.fontRenderer.FONT_HEIGHT) / 2;
				int xoff = offsetX + (SELECT_WIDTH - this.fontRenderer.getStringWidth(says[i])) / 2;
				this.fontRenderer.drawString(says[i], xoff, yoff, 0);
			}
		}

	}

	protected int selectY(int index, int totlal) {
		final int height = 225;
		return index * height / totlal + 10;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if (!currDrawValue.equals(drawValue)) {
			currDrawValue = drawValue;
			return;
		}
		TalkChapter.Iter iter = container.getChapterIter();
		if (iter == null) return;
		// 是说话
		if (iter.getType() == TalkType.SAY) {
			next(iter, 0);
			return;
		}
		Object saying = iter == null ? TalkChapter.NOTHING_TO_SAY : iter.getSaying();
		if (saying instanceof String[]) {
			// 判断选择的按钮
			String[] says = (String[]) saying;
			int offsetX = (this.width - SELECT_WIDTH) / 2;
			if (mouseX < offsetX) return;
			else if (mouseX > offsetX + SELECT_WIDTH) return;
			for (int i = 0; i < says.length; i++) {
				int yoff = selectY(i, says.length);
				int nextYoff = selectY(i + 1, says.length);
				if (mouseY < yoff) break;
				if (mouseY > Math.min(nextYoff, yoff + SELECT_HEIGHT)) continue;
				next(iter, i);
				return;
			}
		}
	}

	protected void next(TalkChapter.Iter iter, int select) {
		if (iter.isPoint()) container.sendToServer(iter.getIndex(), select);
		else if (iter.hasNext()) iter.next();
		// 结束消息
		else container.sendToServer(iter.getIndex(), select);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		TalkChapter.Iter iter = container.getChapterIter();
		if (iter != null && iter.getType() == TalkType.SAY) {
			String str = (String) iter.getSaying();
			if (str.equals(drawValue)) {
				if (!currDrawValue.equals(drawValue)) {
					currDrawValue = drawValue.substring(0, currDrawValue.length() + 1);
				}
			} else {
				drawValue = str;
				currDrawValue = "";
			}
		}
	}

}
