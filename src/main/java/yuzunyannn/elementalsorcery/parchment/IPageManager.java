package yuzunyannn.elementalsorcery.parchment;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.building.Building;

public interface IPageManager {
	/** 获取GUI */
	public GuiContainer getGui();

	/** 重新更新 */
	public void reinit();

	/** 获取界面左上角坐标 */
	int getAxisOff(boolean isX);

	/** 添加一个自定义按钮 */
	void addCustomButton(GuiButton button);

	/** 加一个物品槽 */
	public int addSlot(int x, int y, ItemStack stack);

	/** 设置物品槽的的物品 */
	public void setSlot(int slot, ItemStack stack);

	/** 获取物品槽物品 */
	public ItemStack getSlot(int slot);
	
	/**获取物品曹数量*/
	public int getSlots();
	
	/**设置物品槽状态*/
	public void setSlotState(int slot,boolean visible);

	/** 更换页面 */
	public void toPage(Page page);

	/** 设置是否有下一页的按钮 */
	public void setNextButton(boolean has);

	/** 设置是否有上一页的按钮 */
	public void setPrevButton(boolean has);

	/** 获取字体高度 */
	public int getFontHeight();

	/** 画字符串 */
	public void drawString(String str, int x, int y, int color);

	/** 画字符串 */
	public int drawString(String str, int x, int y, int width, int color);

	/** 画标题 */
	public void drawTitle(String str, int x, int y, int xSize, int color);

	/** 画一个物品 */
	public void drawItem(ItemStack stack, int x, int y);

	/** 画一个建筑 */
	public void drawBuilding(Building building, int x, int y, float roateX, float roateY, float roateZ, float scale);
}
