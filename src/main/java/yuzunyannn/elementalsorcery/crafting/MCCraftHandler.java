package yuzunyannn.elementalsorcery.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.tile.TileItemStructureCraftCC;
import yuzunyannn.elementalsorcery.tile.TileItemStructureCraftCC.IISCCCraftHanlder;
import yuzunyannn.elementalsorcery.util.item.InventoryCraftingUseInventory;
import yuzunyannn.elementalsorcery.util.item.ItemStackHandlerInventory;

public class MCCraftHandler implements IISCCCraftHanlder {

	@Override
	public List<Integer> getSlotIndexMap() {
		List<Integer> list = new ArrayList<>();
		for (int x = 0; x < 3; x++) for (int y = 0; y < 3; y++) list.add(TileItemStructureCraftCC.getSlotIndex(x, y));
		return list;
	}

	@Override
	public ItemStack match(World world, BlockPos pos, Map<Integer, ItemStack> slotMap, List<ItemStack> inputs,
			List<ItemStack> remains) {
		ItemStackHandlerInventory inputInv = new ItemStackHandlerInventory(9);
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				ItemStack itemStack = slotMap.get(TileItemStructureCraftCC.getSlotIndex(x, y));
				if (itemStack == null) continue;
				inputInv.setStackInSlot(x + y * 3, itemStack.copy());
			}
		}
		InventoryCraftingUseInventory craftMatrix = new InventoryCraftingUseInventory(null, inputInv, 3);
		IRecipe irecipe = CraftingManager.findMatchingRecipe(craftMatrix, world);
		if (irecipe == null) return ItemStack.EMPTY;

		ItemStack result = irecipe.getCraftingResult(craftMatrix);
		if (result.isEmpty()) return ItemStack.EMPTY;

		for (int i = 0; i < inputInv.getSlots(); i++) {
			ItemStack s = inputInv.getStackInSlot(i);
			if (!s.isEmpty()) s.setCount(1);
			inputs.add(s);
		}

		NonNullList<ItemStack> craftRemains = irecipe.getRemainingItems(craftMatrix);
		for (ItemStack remain : craftRemains) {
			if (remain.isEmpty()) continue;
			remains.add(remain);
		}

		return result;
	}

}