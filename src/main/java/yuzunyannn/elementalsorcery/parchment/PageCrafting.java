package yuzunyannn.elementalsorcery.parchment;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.crafting.RecipeManagement;
import yuzunyannn.elementalsorcery.util.TextHelper;

public class PageCrafting extends Page {

	private final List<NonNullList<Ingredient>> ITEM_LIST = new ArrayList<NonNullList<Ingredient>>();
	private final List<ItemStack> ITEM_OUT = new ArrayList<ItemStack>();
	private final List<List<ElementStack>> ELEMENT_NEED = new ArrayList<List<ElementStack>>();
	private int now_index = 0;
	private int tick = 0;

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
		if (this.addESRecipe(stack))
			return;
		// 根据stack寻找合成表
		IRecipe irecipe = null;
		for (IRecipe ire : CraftingManager.REGISTRY) {
			if (ire.getRecipeOutput().isItemEqual(stack)) {
				irecipe = ire;
				break;
			}
		}
		if (irecipe == null)
			return;
		// 获取数组
		NonNullList<Ingredient> ingLIst = irecipe.getIngredients();
		ITEM_LIST.add(ingLIst);
		ITEM_OUT.add(stack);
		ELEMENT_NEED.add(null);
	}

	private boolean addESRecipe(ItemStack stack) {
		yuzunyannn.elementalsorcery.api.crafting.IRecipe irecipe = null;
		List<yuzunyannn.elementalsorcery.api.crafting.IRecipe> lsit = RecipeManagement.instance.getRecipes();
		for (yuzunyannn.elementalsorcery.api.crafting.IRecipe ire : lsit) {
			if (ire.getRecipeOutput().isItemEqual(stack)) {
				irecipe = ire;
				break;
			}
		}
		if (irecipe == null)
			return false;
		// 获取数组
		NonNullList<Ingredient> ingLIst = irecipe.getIngredients();
		List<ElementStack> eList = irecipe.getNeedElements();
		ITEM_LIST.add(ingLIst);
		ITEM_OUT.add(stack);
		ELEMENT_NEED.add(eList);
		return true;
	}

	// 测试是否拥有合成表，是否有效
	public boolean test() {
		return !ITEM_LIST.isEmpty();
	}

	@Override
	public NonNullList<Ingredient> getCrafting() {
		return ITEM_LIST.get(now_index);
	}

	@Override
	public ItemStack getOutput() {
		return ITEM_OUT.get(now_index);
	}

	@Override
	public void onUpdate() {
		tick++;
		if (tick > 20 * 3) {
			tick = 0;
			now_index++;
			now_index = now_index % ITEM_LIST.size();
		}
	}

	@Override
	public void addContexts(List<String> contexts) {
		super.addContexts(contexts);
		List<ElementStack> eList = ELEMENT_NEED.get(now_index);
		if (eList == null)
			return;
		TextHelper.addInfo(contexts, "page.crafting.need");
		for (ElementStack stack : eList) {
			if (stack.usePower())
				TextHelper.addInfo(contexts, "page.crafting.show", TextFormatting.GOLD,
						I18n.format(stack.getElementUnlocalizedName()), stack.getCount(), stack.getPower());
			else
				TextHelper.addInfo(contexts, "page.crafting.npshow", TextFormatting.GOLD,
						I18n.format(stack.getElementUnlocalizedName()), stack.getCount());
		}
	}
}
