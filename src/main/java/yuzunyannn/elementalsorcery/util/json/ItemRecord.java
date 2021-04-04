package yuzunyannn.elementalsorcery.util.json;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import yuzunyannn.elementalsorcery.util.item.ItemRec;

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

	public static List<ItemRec> asItemRecList(List<ItemRecord> itemRecords) {
		List<ItemRec> list = new ArrayList<>();
		for (ItemRecord ir : itemRecords) list.add(new ItemRec(ir.getStack()));
		return list;
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
