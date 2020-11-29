package yuzunyannn.elementalsorcery.util.json;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

/** 描述物品记录，是stack还是单单是物品 */
public class ItemRecord {

	public static Ingredient asIngredient(List<ItemRecord> itemRecords) {
		ItemStack[] stacks = new ItemStack[itemRecords.size()];
		for (int i = 0; i < stacks.length; i++) stacks[i] = itemRecords.get(i).getStack();
		return Ingredient.fromStacks(stacks);
	}

	private final ItemStack stack;
	private final Item item;

	public ItemRecord(ItemStack stack) {
		this.stack = stack;
		this.item = null;
	}

	public ItemRecord(Item item) {
		this.item = item;
		this.stack = new ItemStack(item);
	}

	public boolean isJustItem() {
		return item != null;
	}

	public ItemStack getStack() {
		return stack;
	}

	public Item getItem() {
		return item;
	}

	@Override
	public String toString() {
		return item == null ? stack.toString() : item.getUnlocalizedName();
	}
}
