package yuzunyannn.elementalsorcery.crafting;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;

public class RecipeInheritElement extends Recipe {

	public RecipeInheritElement(ItemStack output) {
		super(output);
	}

	public RecipeInheritElement(ItemStack output, Object[] args) {
		super(output, args);
	}

	@Override
	public ItemStack getCraftingResult(IInventory inv) {
		if (inv == null) return super.getCraftingResult(inv);
		ItemStack output = super.getCraftingResult(inv).copy();
		IElementInventory outEinv = output.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
		if (outEinv == null) return output;
		// 扫描仓库
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			IElementInventory einv = ElementHelper.getElementInventory(stack);
			if (einv == null) continue;
			ElementHelper.merge(outEinv, einv);
		}
		outEinv.saveState(output);
		return output;
	}

}
