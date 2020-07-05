package yuzunyannn.elementalsorcery.parchment;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PageMult extends Page {

	protected Page[] pages;
	protected int pageAt;
	protected Integer itemShowAt = null;

	public PageMult(Page... pages) {
		this.pages = pages;
		this.pageAt = 0;
	}

	public Page getCurrPage() {
		return pages[pageAt];
	}

	public void lockShowAt(Integer i) {
		if (i == null) itemShowAt = null;
		else {
			if (i < 0 || i >= pages.length) return;
			itemShowAt = i;
		}
	}

	public Page[] getPages() {
		return pages;
	}

	@Override
	public void open(IPageManager pageManager) {
		pageAt = 0;
	}

	@Override
	public void init(IPageManager pageManager) {
		this.getCurrPage().init(pageManager);
		if (this.pageAt < this.pages.length - 1) pageManager.setNextButton(true);
		else pageManager.setNextButton(false);
		if (this.pageAt > 0) pageManager.setPrevButton(true);
		else pageManager.setPrevButton(false);

	}

	@Override
	public void slotAction(int slot, IPageManager pageManager) {
		this.getCurrPage().slotAction(slot, pageManager);
	}

	@Override
	public void pageAction(boolean next, IPageManager pageManager) {
		if (next) {
			if (this.pageAt < pages.length - 1) {
				this.pageAt++;
				pageManager.reinit();
			}
		} else {
			if (this.pageAt > 0) {
				this.pageAt--;
				pageManager.reinit();
			}
		}

	}

	@Override
	public void customButtonAction(GuiButton button, IPageManager pageManager) {
		this.getCurrPage().customButtonAction(button, pageManager);
	}

	@Override
	public void mouseClick(int mouseX, int mouseY, int mouseButton, IPageManager pageManager) {
		this.getCurrPage().mouseClick(mouseX, mouseY, mouseButton, pageManager);
	}

	@Override
	public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick,
			IPageManager pageManager) {
		this.getCurrPage().mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick, pageManager);
	}

	@Override
	public void update(IPageManager pageManager) {
		this.getCurrPage().update(pageManager);
	}

	@Override
	public void drawBackground(int xoff, int yoff, IPageManager pageManager) {
		this.getCurrPage().drawBackground(xoff, yoff, pageManager);
	}

	@Override
	public void drawValue(IPageManager pageManager) {
		this.getCurrPage().drawValue(pageManager);
	}

	@Override
	public void addItemInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (itemShowAt != null) pages[itemShowAt].addItemInformation(stack, worldIn, tooltip, flagIn);
		else this.getCurrPage().addItemInformation(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public void drawIcon(int xoff, int yoff, IPageManager pageManager) {
		if (itemShowAt != null) pages[itemShowAt].drawIcon(xoff, yoff, pageManager);
		else this.getCurrPage().drawIcon(xoff, yoff, pageManager);
	}

	@Override
	public void drawString(int xoff, int yoff, IPageManager pageManager) {
		if (itemShowAt != null) pages[itemShowAt].drawString(xoff, yoff, pageManager);
		else this.getCurrPage().drawString(xoff, yoff, pageManager);
	}
}
