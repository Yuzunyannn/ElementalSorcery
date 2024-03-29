package yuzunyannn.elementalsorcery.parchment;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.container.gui.GuiParchment;

public class PageTransform extends PageEasy {

	static final int SMELTING = 0;
	static final int INFUSION = 1;
	static final int SEPARATE = 2;
	static final int SPELLALTAR = 3;
	static final int RITE = 4;
	static final int RESEARCH = 5;

	protected ItemStack getOrigin() {
		return ItemStack.EMPTY;
	}

	protected ItemStack getOutput() {
		return ItemStack.EMPTY;
	}

	protected ItemStack getExtra() {
		return ItemStack.EMPTY;
	}

	protected List<ItemStack> getItemList() {
		return null;
	}

	protected int getType() {
		return 0;
	}

	protected int getCX() {
		return 160;
	}

	protected int getCY() {
		return 54;
	}

	@Override
	public ItemStack getIcon() {
		return this.getOutput();
	}

	@Override
	public int getWidthSize(IPageManager pageManager) {
		return (int) (super.getWidthSize(pageManager) * 0.65f);
	}

	int listAt = 0;

	@Override
	@SideOnly(Side.CLIENT)
	public void init(IPageManager pageManager) {
		super.init(pageManager);
		int cX = this.getCX() + 1;
		int cY = this.getCY() + 1;
		pageManager.addSlot(cX, cY, this.getOrigin());
		pageManager.addSlot(cX + 45, cY, this.getOutput());
		if (this.getItemList() != null) this.initList(pageManager, cX, cY);
		if (!this.getExtra().isEmpty()) this.initExtra(pageManager, cX, cY);
	}

	@SideOnly(Side.CLIENT)
	protected void initExtra(IPageManager pageManager, int cX, int cY) {
		if (this.getType() == RITE) {
			pageManager.addSlot(cX + 22 - 22, cY + 18 + 22, this.getExtra());
		} else pageManager.addSlot(cX + 22, cY + 18, this.getExtra());

	}

	@SideOnly(Side.CLIENT)
	protected void initList(IPageManager pageManager, int cX, int cY) {
		int xoff = pageManager.getAxisOff(true);
		int yoff = pageManager.getAxisOff(false);
		pageManager.addCustomButton(new ChangeButton(0, xoff + cX - 10 - 1, yoff + cY + 41, false));
		pageManager.addCustomButton(new ChangeButton(1, xoff + cX - 10 + 5 + 18 * 4, yoff + cY + 41, true));
		for (int i = 0; i < 4; i++) pageManager.addSlot(cX - 9 + 5 + i * 18, cY + 37, ItemStack.EMPTY);
		this.reflushListShow(pageManager);
	}

	@Override
	public void customButtonAction(GuiButton button, IPageManager pageManager) {
		List<ItemStack> list = this.getItemList();
		if (list.isEmpty()) return;
		if (button.id == 0) {
			if (listAt > 0) {
				listAt--;
				this.reflushListShow(pageManager);
			}
		} else {
			if (listAt < list.size() - 4) {
				listAt++;
				this.reflushListShow(pageManager);
			}
		}
	}

	protected void reflushListShow(IPageManager pageManager) {
		List<ItemStack> list = this.getItemList();
		for (int i = 0; i < 4 && listAt + i < list.size(); i++) {
			pageManager.setSlot(i + 2, list.get(listAt + i));
		}
	}

	@Override
	public void drawBackground(int xoff, int yoff, IPageManager pageManager) {
		int cX = this.getCX() + xoff;
		int cY = this.getCY() + yoff;
		int type = this.getType();
		GuiContainer gui = pageManager.getGui();
		gui.drawTexturedModalRect(cX, cY, 95, 166, 63, 18);
		if (type == INFUSION) gui.drawTexturedModalRect(cX + 18, cY, 95 + 18, 166 + 18, 27, 18);
		else if (type == SEPARATE) gui.drawTexturedModalRect(cX + 18, cY, 95 + 18, 166 + 54, 27, 18);
		else if (type == SPELLALTAR) gui.drawTexturedModalRect(cX + 18, cY, 95 + 18, 166 + 72, 27, 18);
		else if (type == RITE) gui.drawTexturedModalRect(cX + 18, cY, 95 + 18 + 27, 166 + 18, 27, 18);
		else {
			gui.drawTexturedModalRect(cX, cY + 18, 95, 166 + 18, 18, 18);
			gui.drawTexturedModalRect(cX + 18, cY, 95 + 18, 166 + 36, 27, 18);
		}
		if (!this.getExtra().isEmpty()) {
			if (type == RITE) {
				gui.drawTexturedModalRect(cX + 18 + 4 - 22, cY + 18 + 22, 95, 166, 18, 18);
				gui.drawTexturedModalRect(cX + 18 + 4 - 22, cY + 18, 41, 220, 18, 27);
			} else gui.drawTexturedModalRect(cX + 18 + 4, cY + 18, 95, 166, 18, 18);
		}
		if (this.getItemList() != null) gui.drawTexturedModalRect(cX - 9 + 5, cY + 37, 41, 166, 72, 18);
	}

	/** 物品翻页按钮 */
	@SideOnly(Side.CLIENT)
	static protected class ChangeButton extends GuiButton {
		final int textureOffsetX;

		public ChangeButton(int buttonId, int x, int y, boolean next) {
			super(buttonId, x, y, 5, 9, null);
			if (next) textureOffsetX = 15;
			else textureOffsetX = 10;
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			if (this.visible) {
				GlStateManager.disableLighting();
				GlStateManager.color(1, 1, 1);
				mc.getTextureManager().bindTexture(GuiParchment.TEXTURE);
				int x = mouseX - this.x, y = mouseY - this.y;
				if (x >= 0 && y >= 0 && x < this.width && y < this.height) {
					this.drawTexturedModalRect(this.x, this.y, textureOffsetX, 186, this.width, this.height);
				} else {
					this.drawTexturedModalRect(this.x, this.y, textureOffsetX - 10, 186, this.width, this.height);
				}
			}
		}
	}
}
