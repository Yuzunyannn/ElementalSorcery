package yuzunyannn.elementalsorcery.crafting;

import java.util.ArrayList;
import java.util.Arrays;
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
import yuzunyannn.elementalsorcery.api.crafting.IRecipe;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.util.json.Json;
import yuzunyannn.elementalsorcery.util.json.Json.ParseExceptionCode;

public class Recipe extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	// 信息
	private int firstNotEmpty = -1;
	private ItemStack output;
	private List<ElementStack> elementList = new ArrayList<ElementStack>();
	private NonNullList<Ingredient> matchList = NonNullList.<Ingredient>create();

	public Recipe(ItemStack output) {
		this.output = output;
	}

	public Recipe(ItemStack output, Object... args) {
		this.output = output;
		LinkedList<String> ss = new LinkedList<String>();
		Map<String, ItemStack[]> map = new HashMap<String, ItemStack[]>();
		// 处理参数
		for (Object obj : args) {
			if (obj instanceof String) {
				ss.addLast((String) obj);
			} else if (obj instanceof ItemStack || obj instanceof Item || obj instanceof Block) {
				if (ss.isEmpty()) throw new IllegalArgumentException("ItemStack can't find mark!");
				String mark = ss.getLast();
				ss.removeLast();
				if (mark.length() != 1) throw new IllegalArgumentException("Mark can only use one char!");
				if (obj instanceof Item) map.put(mark, new ItemStack[] { new ItemStack((Item) obj) });
				else if (obj instanceof Block) map.put(mark, new ItemStack[] { new ItemStack((Block) obj) });
				else map.put(mark, new ItemStack[] { (ItemStack) obj });
			} else if (obj instanceof ElementStack) {
				elementList.add((ElementStack) obj);
			} else throw new IllegalArgumentException("Illegal args!");
		}
		this.parse(output, ss, map, elementList);
	}

	public void parse(ItemStack output, List<String> pattern, Map<String, ItemStack[]> map,
			List<ElementStack> needElement) {
		if (pattern.isEmpty()) throw Json.exception(ParseExceptionCode.EMPTY, "pattern");
		this.elementList = needElement;
		// 开始的行数
		int i = 0;
		// 首先是中间三行
		for (; i < pattern.size(); i++) {
			String str = pattern.get(i);
			if (str.length() > 3) break;
			if (str.length() != 3) throw Json.exception(ParseExceptionCode.PATTERN_ERROR, "pattern", str);
			for (int j = 0; j < str.length(); j++) {
				String ch = str.substring(j, j + 1);
				if (ch.charAt(0) != ' ') {
					if (!map.containsKey(ch)) throw Json.exception(ParseExceptionCode.NOT_HAVE, "key");
					ItemStack[] stacks = map.get(ch);
					int index = i * 3 + j;
					this.setItemStack(index, stacks);
				}
			}
		}
		int lock = i;
		// 然后其余四行
		for (; i < pattern.size(); i++) {
			String str = pattern.get(i);
			if (str.length() <= 3) throw Json.exception(ParseExceptionCode.PATTERN_ERROR, "pattern", str);
			if (str.length() != 4) throw Json.exception(ParseExceptionCode.PATTERN_ERROR, "pattern", str);
			for (int j = 0; j < str.length(); j++) {
				String ch = str.substring(j, j + 1);
				if (ch.charAt(0) != ' ') {
					if (!map.containsKey(ch)) throw Json.exception(ParseExceptionCode.NOT_HAVE, "key");
					ItemStack[] stacks = map.get(ch);
					int index = (i - lock) * 4 + 9 + j;
					this.setItemStack(index, stacks);
				}
			}
		}
	}

	Recipe(ItemStack output, List<ElementStack> elist, NonNullList<Ingredient> mlist) {
		this.output = output;
		this.elementList = elist;
		this.matchList = mlist;

	}

	// 设置指定位置的物品栈
	public void setItemStack(int index, ItemStack... stacks) {
		if (stacks.length == 0) return;
		if (index >= 25 || index < 0)
			throw new IllegalArgumentException("index must be less than 25 and greater or equal to 0!");
		if (!matchList.isEmpty()) {
			if (matchList.size() - 1 >= index)
				throw new IllegalArgumentException("index must be increase progressively!");
		}
		if (firstNotEmpty == -1) firstNotEmpty = index;
		while (matchList.size() != index) matchList.add(Ingredient.EMPTY);
		matchList.add(Ingredient.fromStacks(stacks));
	}

	// 设置所需的元素
	public void setElementStack(ElementStack[] need) {
		elementList = Arrays.asList(need);
	}

	// 设置所需的元素
	public void setElementStack(List<ElementStack> need) {
		elementList = need;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn) {
		// 寻找inv的第一个非空位置
		int invFne = 0;
		for (; invFne < inv.getSizeInventory(); invFne++) if (!inv.getStackInSlot(invFne).isEmpty()) break;
		// 开始比对
		for (int i = this.firstNotEmpty; i < matchList.size(); i++) {
			int j = i - this.firstNotEmpty + invFne;
			if (inv.getSizeInventory() <= j) return false;
			ItemStack stack = inv.getStackInSlot(j);
			Ingredient origin = matchList.get(i);
			if (stack.isEmpty() && origin == Ingredient.EMPTY) continue;
			ItemStack match = ItemStack.EMPTY;
			for (ItemStack e : origin.getMatchingStacks()) {
				if (!e.isItemEqual(stack)) continue;
				match = e;
				break;
			}
			if (match.isEmpty()) return false;
			if (match.getCount() > stack.getCount()) return false;
			if (match.hasTagCompound() && !ItemStack.areItemStackTagsEqual(match, stack)) return false;
		}
		// 末尾查询
		for (int i = matchList.size() - this.firstNotEmpty + invFne; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) return false;
		}
		return true;
	}

	@Override
	public void shrink(IInventory inv) {
		// 寻找inv的第一个非空位置
		int invFne = 0;
		for (; invFne < inv.getSizeInventory(); invFne++) if (!inv.getStackInSlot(invFne).isEmpty()) break;
		// 开始减少
		for (int i = this.firstNotEmpty; i < matchList.size(); i++) {
			Ingredient origin = matchList.get(i);
			if (origin == Ingredient.EMPTY) continue;
			int j = i - this.firstNotEmpty + invFne;
			ItemStack stack = inv.getStackInSlot(j);
			for (ItemStack e : origin.getMatchingStacks()) {
				if (e.isItemEqual(stack)) {
					stack.grow(-e.getCount());
					break;
				}
			}
			inv.setInventorySlotContents(j, stack);
		}
	}

	@Override
	public ItemStack getRecipeOutput() {
		return output;
	}

	@Override
	public List<ElementStack> getNeedElements() {
		return elementList;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return matchList;
	}

}
