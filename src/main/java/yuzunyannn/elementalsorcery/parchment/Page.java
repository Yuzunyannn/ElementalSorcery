package yuzunyannn.elementalsorcery.parchment;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class Page {

	/** 页面的id */
	protected String id;

	/** 获取id */
	public String getId() {
		return id;
	}

	/** 上一个指向的页面 */
	public Page prevPage;

	/** 显示在物品(羊皮卷)上的信息 */
	public void addItemInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip,
			ITooltipFlag flagIn) {

	}

	/** 页面首次被打开 */
	public void open(IPageManager pageManager) {

	}

	/** 初始化，切换到该页面时候调用 */
	public void init(IPageManager pageManager) {

	}

	/** 用户自定义按钮响应 */
	public void customButtonAction(GuiButton button, IPageManager pageManager) {

	}

	/** 点击slot动作 */
	public void slotAction(int slot, IPageManager pageManager) {

	}

	/** 切换页面按钮点击动作 */
	public void pageAction(boolean next, IPageManager pageManager) {

	}

	/** 鼠标点击 */
	public void mouseClick(int mouseX, int mouseY, int mouseButton, IPageManager pageManager) {

	}

	/** 鼠标点击移动 */
	public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick,
			IPageManager pageManager) {

	}

	/** 更新 */
	public void update(IPageManager pageManager) {

	}

	/** 画Page */
	public void drawBackground(int xoff, int yoff, IPageManager pageManager) {

	}

	/** 画文字 */
	public void drawValue(IPageManager pageManager) {

	}

	/** 画图标在书中 */
	public void drawIcon(int xoff, int yoff, IPageManager pageManager) {

	}

	/** 画文字，在书中 */
	public void drawString(int xoff, int yoff, IPageManager pageManager) {

	}
}
