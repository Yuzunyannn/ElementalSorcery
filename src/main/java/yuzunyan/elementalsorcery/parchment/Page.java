package yuzunyan.elementalsorcery.parchment;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import yuzunyan.elementalsorcery.ElementalSorcery;
import yuzunyan.elementalsorcery.building.Building;
import yuzunyan.elementalsorcery.util.TextHelper;

public class Page {

	// 页面非文字状态
	public static enum PageSate {
		EXCLUSIVE, SMALL, BIG, EMPTY
	}

	// 所有注册的页面
	static protected ArrayList<Page> pages = new ArrayList<Page>();

	static public int addPage(Page page) {
		pages.add(page);
		page.id = pages.size() - 1;
		return page.id;
	}

	// 获取页数量，该函数客户端和服务端返回不一样
	static public int getCount() {
		return pages.size();
	}

	// 获取真实最大页数
	static public int getMax() {
		return Pages.REAL_PAGE_COUNT;
	}

	// 获取页面，客户端使用，服务端永远是0页
	static public Page getPage(int index) {
		if (index < 0 || index >= pages.size()) {
			ElementalSorcery.logger.warn("getPage异常的页数进入:" + index);
			return Page.getErrorPage();
		}
		return pages.get(index);
	}

	// 获取页面，客户端使用，服务端永远是0页
	static public Page getPage(ItemStack stack) {
		NBTTagCompound nbt = stack.getSubCompound("page");
		if (nbt == null)
			return Page.getErrorPage();
		return Page.getPage(nbt.getInteger("id"));
	}

	// 获取页面ID
	static public int getPageId(ItemStack stack) {
		NBTTagCompound nbt = stack.getSubCompound("page");
		if (nbt == null)
			return 0;
		return nbt.getInteger("id");
	}

	// 获取错误页面
	static public Page getErrorPage() {
		return pages.get(Pages.ERROR);
	}

	// 是有有效id
	static public boolean isVaild(int id) {
		return id >= 0 && id < Page.getMax();
	}

	// 设置
	static public ItemStack setPageAt(ItemStack stack, int id) {
		NBTTagCompound nbt = stack.getOrCreateSubCompound("page");
		nbt.setInteger("id", id);
		return stack;
	}

	int id = -1;
	int back = -1;

	public int getId() {
		return this.id;
	}

	/** 当打开 */
	public void open() {

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

}
