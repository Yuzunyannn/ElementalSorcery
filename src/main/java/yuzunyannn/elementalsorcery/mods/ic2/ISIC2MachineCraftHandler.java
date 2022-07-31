package yuzunyannn.elementalsorcery.mods.ic2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import ic2.api.recipe.IBasicMachineRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.MachineRecipeResult;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.tile.TileItemStructureCraft;
import yuzunyannn.elementalsorcery.tile.TileItemStructureCraft.IISCCCraftHanlder;

public abstract class ISIC2MachineCraftHandler implements IISCCCraftHanlder {

	@Override
	public abstract boolean isKeyItem(ItemStack stack);

	public abstract IBasicMachineRecipeManager getRecipeManager();

	@Override
	public List<Integer> getSlotIndexMap() {
		List<Integer> list = new ArrayList<>();
		list.add(TileItemStructureCraft.getSlotIndex(0, 0));
		return list;
	}

	@Override
	public ItemStack match(World world, BlockPos pos, Map<Integer, ItemStack> slotMap, List<ItemStack> inputs,
			List<ItemStack> remains) {
		ItemStack input = IISCCCraftHanlder.getInput(slotMap, 0, 0);
		if (input.isEmpty()) return input;
		input = input.copy();
		MachineRecipeResult<IRecipeInput, Collection<ItemStack>, ItemStack> mrResult = getRecipeManager().apply(input,
				false);
		if (mrResult == null) return ItemStack.EMPTY;
		ItemStack adjust = mrResult.getAdjustedInput();
		if (adjust.getCount() > 0) input.setCount(input.getCount() - adjust.getCount());
		Collection<ItemStack> outputs = mrResult.getOutput();
		if (outputs == null || outputs.isEmpty()) return ItemStack.EMPTY;
		if (outputs.size() != 1) return ItemStack.EMPTY;
		inputs.add(input);
		return outputs.iterator().next();
	}

}