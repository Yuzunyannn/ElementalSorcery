package yuzunyannn.elementalsorcery.parchment;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import yuzunyannn.elementalsorcery.container.gui.GuiParchment;
import yuzunyannn.elementalsorcery.util.NBTHelper;

public class PageBook extends Page {

	public static final int SIZE_PRE_PAGE = 5;
	public final static int CATALOG_LOCAL_INTERVAL = 25;
	public final static int CATALOG_LOCAL_X = 20;
	public final static int CATALOG_LOCAL_Y = 20;

	private List<String> pageIds = new LinkedList<>();
	private Page[] showPage = new Page[SIZE_PRE_PAGE];
	private int pageAt = 0;
	private int maxPage = 0;

	public void setIds(NBTTagList list) {
		pageIds = NBTHelper.getStringListForNBTTagList(list);
	}

	@Override
	public void open(IPageManager pageManager) {
		pageAt = 0;
	}

	@Override
	public void init(IPageManager pageManager) {
		maxPage = (pageIds.size() - 1) / SIZE_PRE_PAGE + 1;
		if (this.pageAt < maxPage - 1)
			pageManager.setNextButton(true);
		else
			pageManager.setNextButton(false);
		if (this.pageAt > 0)
			pageManager.setPrevButton(true);
		else
			pageManager.setPrevButton(false);
		int xoff = CATALOG_LOCAL_X;
		int yoff = CATALOG_LOCAL_Y;
		for (int i = 0; i < SIZE_PRE_PAGE; i++) {
			pageManager.addSlot(xoff + 1, yoff + 1, ItemStack.EMPTY);
			yoff += CATALOG_LOCAL_INTERVAL;
		}
		this.reflushShowPage(pageManager);
	}

	@Override
	public void pageAction(boolean next, IPageManager pageManager) {
		if (next) {
			if (this.pageAt < maxPage - 1) {
				this.pageAt++;
				this.reflushShowPage(pageManager);
			}
		} else {
			if (this.pageAt > 0) {
				this.pageAt--;
				this.reflushShowPage(pageManager);
			}
		}
	}

	private void reflushShowPage(IPageManager pageManager) {

		if (this.pageAt < maxPage - 1)
			pageManager.setNextButton(true);
		else
			pageManager.setNextButton(false);
		if (this.pageAt > 0)
			pageManager.setPrevButton(true);
		else
			pageManager.setPrevButton(false);

		for (int i = 0; i < showPage.length; i++)
			showPage[i] = null;
		int at = pageAt * SIZE_PRE_PAGE;
		for (int i = at; i < at + SIZE_PRE_PAGE && i < pageIds.size(); i++) {
			showPage[i - at] = Pages.getPage(pageIds.get(i));
		}
		for (int i = 0; i < showPage.length; i++)
			pageManager.setSlotState(i, showPage[i] != null);

	}

	@Override
	public void slotAction(int slot, IPageManager pageManager) {
		pageManager.toPage(showPage[slot]);
	}

	@Override
	public void drawBackground(int xoff, int yoff, IPageManager pageManager) {
		GuiContainer gui = pageManager.getGui();
		gui.mc.getTextureManager().bindTexture(GuiParchment.TEXTURE_EXTRA);
		gui.drawTexturedModalRect(xoff + 88, yoff, 0, 0, 77, 166);
		xoff += CATALOG_LOCAL_X;
		yoff += CATALOG_LOCAL_Y;
		for (Page page : showPage) {
			if (page == null)
				break;
			page.drawIcon(xoff, yoff, pageManager);
			yoff += CATALOG_LOCAL_INTERVAL;
		}
	}

	@Override
	public void drawValue(IPageManager pageManager) {
		int width = (int) (pageManager.getGui().getXSize() * 0.525f);
		pageManager.drawTitle(I18n.format("page.catalog"), 0, 6, width, 4210752);
		if (pageIds.isEmpty()) {
			pageManager.drawTitle(I18n.format("page.catalog.none"), 0, 32, width, 4210752);
			return;
		}
		int xoff = CATALOG_LOCAL_X;
		int yoff = CATALOG_LOCAL_Y;
		GuiContainer gui = pageManager.getGui();
		for (Page page : showPage) {
			if (page == null)
				break;
			page.drawString(xoff, yoff, pageManager);
			yoff += CATALOG_LOCAL_INTERVAL;
		}
	}
}
