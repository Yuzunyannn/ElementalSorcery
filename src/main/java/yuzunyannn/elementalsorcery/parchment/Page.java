package yuzunyannn.elementalsorcery.parchment;

import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.util.TextHelper;

public class Page {

	// 页面非文字状态，布局
	public static enum PageSate {
		EXCLUSIVE, SMALL, BIG, EMPTY
	}

	/** 当前page的id，添加的时候会被赋予 */
	int id = -10;
	/** 记录返回上一个页面使用的内容 */
	private int back = -1;

	public int getId() {
		return this.id;
	}

	/** 当打开 */
	public void open() {

	}

	/** 当关闭 */
	public void close() {

	}

	/** 页替换返回的值 */
	public int back() {
		return back;
	}

	/** 页替设置back */
	public void back(int back) {
		this.back = back;
	}

	/** 获取标题 */
	public String getTitle() {
		return null;
	}

	/** 获取物品上的信息 */
	public String getItemInfo() {
		return this.getTitle();
	}

	/** 获取页面展示的图标 */
	public ItemStack getIcon() {
		return this.getOutput();
	}

	/** 添加正文内容 */
	public void addContexts(List<String> contexts) {
		String str = this.getContext();
		if (str != null)
			TextHelper.addInfoCheckLine(contexts, str);
	}

	/** 获取一个正文 */
	public String getContext() {
		return null;
	}

	/** 获取页面状态 */
	public PageSate getState() {
		if (this.getBuilding() != null)
			return PageSate.EXCLUSIVE;
		NonNullList<Ingredient> list = this.getCrafting();
		if (list != null) {
			return list.size() > 9 ? PageSate.BIG : PageSate.SMALL;
		}
		if (!this.getOrigin().isEmpty()) {
			return PageSate.SMALL;
		}
		return PageSate.EMPTY;
	}

	/**
	 * 获取转化界面编号：0 为 熔炼
	 */
	public int getTransformGui() {
		return 0;
	}

	/** 获取原始物品物（如熔炼物品） */
	public ItemStack getOrigin() {
		return ItemStack.EMPTY;
	}

	/** 获取额外物品 */
	public ItemStack getExtra() {
		return ItemStack.EMPTY;
	}

	/** 获取物品列 */
	public List<ItemStack> getItemList() {
		return null;
	}

	/** 获取合成表 */
	public NonNullList<Ingredient> getCrafting() {
		return null;
	}

	/** 获取结果（合成结果，熔炼结果等） */
	public ItemStack getOutput() {
		return ItemStack.EMPTY;
	}

	/** 获取建筑 */
	public Building getBuilding() {
		return null;
	}

	/** 获取目录列表，此项仅作为book使用 */
	public int[] getCatalog() {
		return null;
	}

	/**
	 * 获取点击物品位置之后，转跳的位置 -1表示默认
	 */
	public int getCraftingTo(int index) {
		return -1;
	}

	/** 更新 */
	public void onUpdate() {

	}

	/** 下一个页面 */
	public int nextPage() {
		return -1;
	}

	/** 上一个页面 */
	public int prePage() {
		return -1;
	}

	/** 如果返回的id和自身一样，则调用 */
	public void nextPageUpdate() {

	}

	/** 如果返回的id和自身一样，则调用 */
	public void prePageUpdate() {

	}

	/**
	 * 画自定义背景
	 * 
	 * @param offsetX
	 *            x开始点
	 * @param offsetY
	 *            y开始点
	 */
	public void drawBackground(GuiContainer gui, int offsetX, int offsetY) {

	}

}
