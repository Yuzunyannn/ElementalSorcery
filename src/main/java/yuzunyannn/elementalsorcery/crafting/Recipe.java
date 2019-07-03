package yuzunyannn.elementalsorcery.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import scala.actors.threadpool.Arrays;
import yuzunyannn.elementalsorcery.api.crafting.IRecipe;
import yuzunyannn.elementalsorcery.api.element.ElementStack;

public class Recipe implements IRecipe {

	// 信息
	private int first_on_empty = -1;
	private ItemStack output;
	private List<ElementStack> element_list = new ArrayList<ElementStack>();
	private List<ItemStack> match_list = new ArrayList<ItemStack>();

	public Recipe(ItemStack output) {
		this.output = output;
	}

	public Recipe(ItemStack output, Object... args) {
		this.output = output;
		LinkedList<String> ss = new LinkedList<String>();
		Map<String, ItemStack> map = new HashMap<String, ItemStack>();
		// 处理参数
		for (Object obj : args) {
			if (obj instanceof String) {
				ss.addLast((String) obj);
			} else if (obj instanceof ItemStack || obj instanceof Item || obj instanceof Block) {
				if (ss.isEmpty())
					throw new IllegalArgumentException("ItemStack can't find mark!");
				String mark = ss.getLast();
				ss.removeLast();
				if (mark.length() != 1)
					throw new IllegalArgumentException("Mark can only use one char!");
				if (obj instanceof Item)
					map.put(mark, new ItemStack((Item) obj));
				else if (obj instanceof Block)
					map.put(mark, new ItemStack((Block) obj));
				else
					map.put(mark, (ItemStack) obj);
			} else if (obj instanceof ElementStack) {
				element_list.add((ElementStack) obj);
			} else
				throw new IllegalArgumentException("Illegal args!");
		}
		if (ss.isEmpty())
			throw new IllegalArgumentException("Recipe empty!");
		// 开始的行数
		int i = 0;
		// 首先是中间三行
		for (; i < ss.size(); i++) {
			String str = ss.get(i);
			if (str.length() > 3)
				break;
			if (str.length() != 3)
				throw new IllegalArgumentException("Incorrect input!");
			for (int j = 0; j < str.length(); j++) {
				String ch = str.substring(j, j + 1);
				if (ch.charAt(0) != ' ') {
					if (!map.containsKey(ch))
						throw new IllegalArgumentException("Can't find mark!");
					ItemStack stack = map.get(ch);
					int index = i * 3 + j;
					this.setItemStack(index, stack);
				}
			}
		}
		int lock = i;
		// 然后其余四行
		for (; i < ss.size(); i++) {
			String str = ss.get(i);
			if (str.length() <= 3)
				throw new IllegalArgumentException("Incorrect input!");
			if (str.length() != 4)
				throw new IllegalArgumentException("Incorrect input!");
			for (int j = 0; j < str.length(); j++) {
				String ch = str.substring(j, j + 1);
				if (ch.charAt(0) != ' ') {
					if (!map.containsKey(ch))
						throw new IllegalArgumentException("Can't find mark!");
					ItemStack stack = map.get(ch);
					int index = (i - lock) * 4 + 9 + j;
					this.setItemStack(index, stack);
				}
			}
		}
		// 测试用s
		// for (int a = 0; a < match_list.size(); a++) {
		// if (match_list.get(a).isEmpty())
		// continue;
		// System.out.println(a + " ?? " + match_list.get(a).toString());
		// }
	}

	Recipe(ItemStack output, List<ElementStack> elist, List<ItemStack> mlist) {
		this.output = output;
		this.element_list = elist;
		this.match_list = mlist;

	}

	// 设置指定位置的物品栈
	public void setItemStack(int index, ItemStack stack) {
		if (stack.isEmpty())
			return;
		if (index >= 25 || index < 0)
			throw new IllegalArgumentException("index must be less than 25 and greater or equal to 0!");
		if (!match_list.isEmpty()) {
			if (match_list.size() - 1 >= index)
				throw new IllegalArgumentException("index must be increase progressively!");
		}
		if (first_on_empty == -1)
			first_on_empty = index;
		while (match_list.size() != index)
			match_list.add(ItemStack.EMPTY);
		match_list.add(stack);
	}

	// 设置所需的元素
	public void setElementStack(ElementStack[] need) {
		element_list = Arrays.asList(need);
	}

	// 设置所需的元素
	public void setElementStack(List<ElementStack> need) {
		element_list = need;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn) {
		// 寻找inv的第一个非空位置
		int inv_fne = 0;
		for (; inv_fne < inv.getSizeInventory(); inv_fne++)
			if (!inv.getStackInSlot(inv_fne).isEmpty())
				break;
		// 开始比对
		for (int i = this.first_on_empty; i < match_list.size(); i++) {
			int j = i - this.first_on_empty + inv_fne;
			if (inv.getSizeInventory() <= j)
				return false;
			ItemStack stack = inv.getStackInSlot(j);
			ItemStack origin = match_list.get(i);
			if (stack.isEmpty() && origin.isEmpty())
				continue;
			if (!origin.isItemEqual(stack))
				return false;
			if (origin.getCount() > stack.getCount())
				return false;
			if (origin.hasTagCompound() && !ItemStack.areItemStackTagsEqual(origin, stack))
				return false;
		}
		// 末尾查询
		for (int i = match_list.size() - this.first_on_empty + inv_fne; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty())
				return false;
		}
		return true;
	}

	@Override
	public void shrink(IInventory inv) {
		// 寻找inv的第一个非空位置
		int inv_fne = 0;
		for (; inv_fne < inv.getSizeInventory(); inv_fne++)
			if (!inv.getStackInSlot(inv_fne).isEmpty())
				break;
		// 开始减少
		for (int i = this.first_on_empty; i < match_list.size(); i++) {
			ItemStack origin = match_list.get(i);
			if (origin.isEmpty())
				continue;
			int j = i - this.first_on_empty + inv_fne;
			ItemStack stack = inv.getStackInSlot(j);
			stack.grow(-origin.getCount());
			inv.setInventorySlotContents(j, stack);
		}
	}

	@Override
	public ItemStack getRecipeOutput() {
		return output;
	}

	@Override
	public List<ElementStack> getNeedElements() {
		return element_list;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> iList = NonNullList.<Ingredient>create();
		for (ItemStack stack : match_list) {
			if (stack.isEmpty())
				iList.add(Ingredient.EMPTY);
			else {
				iList.add(Ingredient.fromStacks(stack));
			}
		}
		return iList;
	}

}
