package yuzunyannn.elementalsorcery.util.json;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import yuzunyannn.elementalsorcery.util.item.BigItemStack;

/** 描述物品记录，是stack还是单单是物品，同时还可以作为一个物品池 */
public class ItemRecord {

	public static Ingredient asIngredient(List<ItemRecord> itemRecords) {
		ItemStack[] stacks = new ItemStack[itemRecords.size()];
		for (int i = 0; i < stacks.length; i++) stacks[i] = itemRecords.get(i).getStack();
		return Ingredient.fromStacks(stacks);
	}

	public static List<ItemStack> asItemStackList(List<ItemRecord> itemRecords) {
		List<ItemStack> list = new ArrayList<>();
		for (ItemRecord ir : itemRecords) list.add(ir.getStack());
		return list;
	}

	public static ItemStack[] asItemStackArray(List<ItemRecord> itemRecords) {
		ItemStack[] array = new ItemStack[itemRecords.size()];
		int i = 0;
		for (ItemRecord ir : itemRecords) array[i++] = ir.getStack();
		return array;
	}

	public static List<BigItemStack> asItemRecList(List<ItemRecord> itemRecords) {
		List<BigItemStack> list = new ArrayList<>();
		for (ItemRecord ir : itemRecords) list.add(new BigItemStack(ir.getStack()));
		return list;
	}

	private final ItemStack stack;
	private final Item item;
	private final boolean justItem;

	public ItemRecord(ItemStack stack) {
		this.stack = stack;
		this.item = stack.getItem();
		this.justItem = false;
	}

	public ItemRecord(Item item) {
		this.item = item;
		this.stack = new ItemStack(item);
		this.justItem = true;
	}

	public boolean isJustItem() {
		return justItem;
	}

	public ItemStack getStack() {
		return stack;
	}

	public Item getItem() {
		return item;
	}

	@Override
	public String toString() {
		return item == null ? stack.toString() : item.getTranslationKey();
	}
}
