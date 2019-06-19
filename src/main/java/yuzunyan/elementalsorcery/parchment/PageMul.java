package yuzunyan.elementalsorcery.parchment;

import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import yuzunyan.elementalsorcery.building.Building;

public class PageMul extends Page {

	protected Page[] pages;
	private int at;
	private Page atPage;

	public PageMul(Page... pages) {
		this.setPages(pages);
	}

	protected void setPages(Page... pages) {
		this.at = 0;
		this.pages = pages;
		if (this.pages.length == 0)
			atPage = Pages.getErrorPage();
		else
			atPage = this.pages[this.at];
	}

	public Page getAtPage() {
		return atPage;
	}

	@Override
	public void open() {
		this.close();
	}

	@Override
	public void close() {
		at = 0;
		if (pages.length != 0)
			atPage = pages[0];
	}

	@Override
	public String getTitle() {
		return atPage.getTitle();
	}

	@Override
	public ItemStack getIcon() {
		return atPage.getIcon();
	}

	@Override
	public void addContexts(List<String> contexts) {
		atPage.addContexts(contexts);
	}

	@Override
	public PageSate getState() {
		return atPage.getState();
	}

	@Override
	public int getTransformGui() {
		return atPage.getTransformGui();
	}

	@Override
	public ItemStack getOrigin() {
		return atPage.getOrigin();
	}

	@Override
	public ItemStack getExtra() {
		return atPage.getExtra();
	}

	@Override
	public List<ItemStack> getItemList() {
		return atPage.getItemList();
	}

	@Override
	public NonNullList<Ingredient> getCrafting() {
		return atPage.getCrafting();
	}

	@Override
	public ItemStack getOutput() {
		return atPage.getOutput();
	}

	@Override
	public Building getBuilding() {
		return atPage.getBuilding();
	}

	@Override
	public int getCraftingTo(int index) {
		return atPage.getCraftingTo(index);
	}

	@Override
	public void onUpdate() {
		atPage.onUpdate();
	}

	@Override
	public void drawBackground(GuiContainer gui, int offsetX, int offsetY) {
		atPage.drawBackground(gui, offsetX, offsetY);
	}

	@Override
	public int prePage() {
		if (atPage.prePage() == atPage.getId()) {
			return this.getId();
		} else {
			if (this.at <= 0)
				return -1;
			else
				return this.getId();
		}
	}

	@Override
	public int nextPage() {
		if (atPage.nextPage() == atPage.getId()) {
			return this.getId();
		} else {
			if (this.at >= pages.length - 1)
				return -1;
			else
				return this.getId();
		}
	}

	@Override
	public void prePageUpdate() {
		if (atPage.prePage() == atPage.getId()) {
			atPage.prePageUpdate();
			return;
		}
		at = at - 1;
		if (at >= 0)
			atPage = pages[at];
	}

	@Override
	public void nextPageUpdate() {
		if (atPage.nextPage() == atPage.getId()) {
			atPage.nextPageUpdate();
			return;
		}
		at = at + 1;
		if (at < pages.length)
			atPage = pages[at];
	}
}
