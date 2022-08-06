package yuzunyannn.elementalsorcery.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.element.IISCraftHanlder;
import yuzunyannn.elementalsorcery.block.container.BlockSmeltBox;
import yuzunyannn.elementalsorcery.tile.TileItemStructureCraftCC;

public class ISSmeltCraftHandler implements IISCraftHanlder {

	@Override
	public boolean isKeyItem(ItemStack stack) {
		Item item = stack.getItem();
		Block block = Block.getBlockFromItem(item);
		return block == Blocks.FURNACE || block instanceof BlockSmeltBox;
	}

	@Override
	public List<Integer> getSlotIndexMap() {
		List<Integer> list = new ArrayList<>();
		list.add(TileItemStructureCraftCC.getSlotIndex(0, 0));
		return list;
	}

	@Override
	public ItemStack match(World world, BlockPos pos, Map<Integer, ItemStack> slotMap, List<ItemStack> inputs,
			List<ItemStack> remains) {
		ItemStack input = slotMap.get(TileItemStructureCraftCC.getSlotIndex(0, 0));
		input = input == null ? ItemStack.EMPTY : input;
		if (input.isEmpty()) return input;
		input = input.copy();
		input.setCount(1);
		ItemStack result = FurnaceRecipes.instance().getSmeltingResult(input);
		if (result.isEmpty()) return result;
		inputs.add(input);
		return result;
	}

}