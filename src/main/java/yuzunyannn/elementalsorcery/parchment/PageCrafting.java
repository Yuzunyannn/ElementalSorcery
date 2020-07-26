package yuzunyannn.elementalsorcery.parchment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import yuzunyannn.elementalsorcery.container.ContainerSupremeTable;
import yuzunyannn.elementalsorcery.crafting.RecipeManagement;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.util.TextHelper;

public class PageCrafting extends PageEasy {
	private final List<NonNullList<Ingredient>> itemList = new ArrayList<NonNullList<Ingredient>>();
	private final List<ItemStack> itemOut = new ArrayList<ItemStack>();
	private final List<List<ElementStack>> elementNeed = new ArrayList<List<ElementStack>>();
	private int nowIndex = 0;
	private int tick = 0;
	private int size = 9;

	public PageCrafting(ItemStack... stacks) {
		for (ItemStack stack : stacks) {
			this.addNewRecipe(stack);
		}
	}

	public PageCrafting(Block block) {
		this(new ItemStack(block));
	}

	public PageCrafting(Item item) {
		this(new ItemStack(item));
	}

	public void addNewRecipe(ItemStack stack) {
		// 寻找元素合成表
		if (this.addESRecipe(stack)) return;
		// 根据stack寻找合成表
		List<IRecipe> allRecipe = new LinkedList();
		for (IRecipe ire : CraftingManager.REGISTRY) {
			if (ire.getRecipeOutput().isItemEqual(stack)) allRecipe.add(ire);
		}
		if (allRecipe.isEmpty()) return;
		// 获取数组
		for (IRecipe irecipe : allRecipe) {
			NonNullList<Ingredient> ingLIst;
			ItemStack output;
			if (irecipe instanceof IPageCraftDynamicIngredients) {
				ingLIst = ((IPageCraftDynamicIngredients) irecipe).getIngredients(stack);
				output = stack;
			} else {
				ingLIst = irecipe.getIngredients();
				output = irecipe.getRecipeOutput();
			}
			itemList.add(ingLIst);
			itemOut.add(output);
			elementNeed.add(null);
		}
	}

	// 添加es合成表
	private boolean addESRecipe(ItemStack stack) {
		yuzunyannn.elementalsorcery.api.crafting.IRecipe irecipe = null;
		List<yuzunyannn.elementalsorcery.api.crafting.IRecipe> lsit = RecipeManagement.instance.getValues();
		for (yuzunyannn.elementalsorcery.api.crafting.IRecipe ire : lsit) {
			if (ire.getRecipeOutput().isItemEqual(stack)) {
				irecipe = ire;
				break;
			}
		}
		if (irecipe == null) return false;
		// 获取数组
		NonNullList<Ingredient> ingLIst = irecipe.getIngredients();
		List<ElementStack> eList = irecipe.getNeedElements();
		itemList.add(ingLIst);
		itemOut.add(irecipe.getRecipeOutput());
		elementNeed.add(eList);
		return true;
	}

	// 测试是否拥有合成表，是否有效
	public boolean test() {
		return !itemList.isEmpty();
	}

	public NonNullList<Ingredient> getCrafting() {
		return itemList.get(nowIndex);
	}

	public ItemStack getOutput() {
		return itemOut.get(nowIndex);
	}

	@Override
	public ItemStack getIcon() {
		return this.getOutput();
	}

	protected int getCX() {
		return 162;
	}

	protected int getCY() {
		return 44;
	}

	@Override
	public void init(IPageManager pageManager) {
		if (itemList.isEmpty()) return;
		size = this.getCrafting().size() > 9 ? 25 : 9;
		int cX = this.getCX();
		int cY = this.getCY();
		for (int i = 0; i < size; i++) {
			int x = ContainerSupremeTable.craftingRelative[i * 2] + cX + 1;
			int y = ContainerSupremeTable.craftingRelative[i * 2 + 1] + cY + 1;
			pageManager.addSlot(x, y, ItemStack.EMPTY);
		}
		pageManager.addSlot(cX + 18 + 1, cY + 64 + 1, ItemStack.EMPTY);
		this.reflushStack(pageManager);
	}

	@Override
	public void update(IPageManager pageManager) {
		tick++;
		if (tick > 20) {
			tick = 0;
			nowIndex++;
			nowIndex = nowIndex % itemList.size();
			this.reflushStack(pageManager);
		}
	}

	// 更换物品栈
	private void reflushStack(IPageManager pageManager) {
		if (itemList.isEmpty()) return;
		NonNullList<Ingredient> list = this.getCrafting();
		for (int i = 0; i < list.size(); i++) {
			ItemStack[] stacks = list.get(i).getMatchingStacks();
			if (stacks.length == 0) {
				pageManager.setSlot(i, ItemStack.EMPTY);
				continue;
			}
			ItemStack stack = stacks[EventClient.randInt % stacks.length];
			pageManager.setSlot(i, stack);
		}
		for (int i = list.size(); i < pageManager.getSlots(); i++) pageManager.setSlot(i, ItemStack.EMPTY);
		pageManager.setSlot(size, this.getOutput());
	}

	@Override
	public int getWidthSize(IPageManager pageManager) {
		return (int) (super.getWidthSize(pageManager) * 0.65f);
	}

	@Override
	public void addContexts(List<String> contexts) {
		super.addContexts(contexts);
		List<ElementStack> eList = elementNeed.get(nowIndex);
		if (eList == null) return;
		TextHelper.addInfo(contexts, "page.crafting.need");
		for (ElementStack stack : eList) {
			if (stack.usePower()) TextHelper.addInfo(contexts, "page.crafting.show", TextFormatting.GOLD,
					I18n.format(stack.getElementUnlocalizedName()), stack.getCount(), stack.getPower());
			else TextHelper.addInfo(contexts, "page.crafting.npshow", TextFormatting.GOLD,
					I18n.format(stack.getElementUnlocalizedName()), stack.getCount());
		}
	}

	@Override
	public void drawBackground(int xoff, int yoff, IPageManager pageManager) {
		int cX = xoff + this.getCX();
		int cY = yoff + this.getCY();
		GuiContainer gui = pageManager.getGui();
		if (size > 9) {
			gui.drawTexturedModalRect(cX, cY, 41, 166, 54, 54);
			gui.drawTexturedModalRect(cX - 36, cY - 36, 41, 166, 36, 36);
			gui.drawTexturedModalRect(cX + 54, cY - 36, 41, 166, 36, 36);
			gui.drawTexturedModalRect(cX - 36, cY + 54, 41, 166, 36, 36);
			gui.drawTexturedModalRect(cX + 54, cY + 54, 41, 166, 36, 36);
		} else gui.drawTexturedModalRect(cX, cY, 41, 166, 54, 54);
		gui.drawTexturedModalRect(cX + 18, cY + 64, 41, 166, 18, 18);
	}
}
