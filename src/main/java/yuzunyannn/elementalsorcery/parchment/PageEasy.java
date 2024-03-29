package yuzunyannn.elementalsorcery.parchment;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import yuzunyannn.elementalsorcery.util.TextHelper;

public class PageEasy extends Page {

	public int valueYOffset;
	public int maxValueHeight;

	@Override
	public String getName() {
		return this.getTitle();
	}

	/** 获取标题 */
	public String getTitle() {
		return "";
	}

	/** 获取一个正文 */
	public String getContext() {
		return "";
	}

	/** 获取该页面展示所用的图标 */
	public ItemStack getIcon() {
		return ItemStack.EMPTY;
	}

	/** 添加正文内容 */
	public void addContexts(List<String> contexts) {
		String str = this.getContext();
		if (str != null) TextHelper.addInfoCheckLine(contexts, str);
	}

	/** 获取文字位于屏幕左侧大小 */
	public int getWidthSize(IPageManager pageManager) {
		return pageManager.getGui().getXSize();
	}

	@Override
	public void slotAction(int slot, IPageManager pageManager) {
		ItemStack stack = pageManager.getSlot(slot);
		if (stack.isEmpty()) return;
		Page page = Pages.itemToPage(stack);
		if (page != null) {
			pageManager.toPage(page);
			return;
		}
		// 如果不是默认转跳，就查询合成表
		PageCraftingTemp tmp = new PageCraftingTemp(stack);
		if (tmp.test()) pageManager.toPage(tmp);
	}

	@Override
	public void init(IPageManager pageManager) {
		super.init(pageManager);
		this.valueYOffset = 0;
	}

	@Override
	public void drawValue(IPageManager pageManager) {
		GlStateManager.disableLighting();
		int width = this.getWidthSize(pageManager);
		pageManager.drawTitle(I18n.format(this.getTitle()), 0, 6, width, 4210752);

		int yoff = 12 + pageManager.getFontHeight();
		int xoff = width / 10;
		LinkedList<String> list = new LinkedList<String>();
		this.addContexts(list);

		pageManager.enableScissor(xoff, yoff - 5, width, 138);
		yoff -= valueYOffset;
		for (String s : list) yoff = pageManager.drawString(s, xoff, yoff, xoff * 8, 4210752);
		pageManager.disableScissor();
		maxValueHeight = Math.max(maxValueHeight, yoff + valueYOffset);
	}

	int lMouseY = 0;

	@Override
	public void mouseClick(int mouseX, int mouseY, int mouseButton, IPageManager pageManager) {
		lMouseY = mouseY;
	}

	@Override
	public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick,
			IPageManager pageManager) {
		int dy = mouseY - lMouseY;
		lMouseY = mouseY;
		if (maxValueHeight - 153 < 0) {
			valueYOffset = 0;
			return;
		}
		valueYOffset -= dy;
		valueYOffset = MathHelper.clamp(valueYOffset, 0, maxValueHeight - 153);
	}

	@Override
	public void drawIcon(int xoff, int yoff, IPageManager pageManager) {
		ItemStack stack = this.getIcon();
		if (stack.isEmpty()) return;
		pageManager.drawItem(stack, xoff + 1, yoff + 1);
	}

	@Override
	public void drawString(int xoff, int yoff, IPageManager pageManager) {
		String str = I18n.format(this.getTitle());
		pageManager.drawString(str, xoff + 21, yoff + 6, 4210752);
	}
}
