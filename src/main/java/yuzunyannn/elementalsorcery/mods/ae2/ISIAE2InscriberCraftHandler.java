package yuzunyannn.elementalsorcery.mods.ae2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import appeng.api.AEApi;
import appeng.api.features.IInscriberRecipe;
import appeng.api.features.IInscriberRegistry;
import appeng.api.features.InscriberProcessType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.element.IISCraftHanlder;
import yuzunyannn.elementalsorcery.api.util.MatchHelper;
import yuzunyannn.elementalsorcery.tile.TileItemStructureCraft;

public class ISIAE2InscriberCraftHandler implements IISCraftHanlder {

	public final IInscriberRegistry reg = AEApi.instance().registries().inscriber();
	public final Item item = AEApi.instance().definitions().blocks().inscriber().maybeItem().get();

	@Override
	public boolean isKeyItem(ItemStack stack) {
		return stack.getItem() == item;
	}

	@Override
	public List<Integer> getSlotIndexMap() {
		List<Integer> list = new ArrayList<>();
		list.add(TileItemStructureCraft.getSlotIndex(0, 0));
		list.add(TileItemStructureCraft.getSlotIndex(0, 1));
		list.add(TileItemStructureCraft.getSlotIndex(0, 2));
		return list;
	}

	@Override
	public ItemStack match(World world, BlockPos pos, Map<Integer, ItemStack> slotMap, List<ItemStack> inputs,
			List<ItemStack> remains) {

		ItemStack inputTop = TileItemStructureCraft.getInput(slotMap, 0, 0).copy();
		ItemStack inputMid = TileItemStructureCraft.getInput(slotMap, 0, 1).copy();
		ItemStack inputBottom = TileItemStructureCraft.getInput(slotMap, 0, 2).copy();

		inputTop.setCount(1);
		inputMid.setCount(1);
		inputBottom.setCount(1);

		Collection<IInscriberRecipe> recipes = reg.getRecipes();
		for (IInscriberRecipe recipe : recipes) {
			ItemStack rTop = recipe.getTopOptional().orElse(ItemStack.EMPTY);
			ItemStack rBottom = recipe.getBottomOptional().orElse(ItemStack.EMPTY);
			InscriberProcessType type = recipe.getProcessType();
			if (type == InscriberProcessType.INSCRIBE) {
				if (MatchHelper.isItemMatch(rTop, inputTop) || MatchHelper.isItemMatch(rTop, inputBottom)) {
					Collection<ItemStack> intputs = recipe.getInputs();
					for (ItemStack input : intputs) {
						if (MatchHelper.isItemMatch(input, inputMid)) {
							inputs.add(inputMid);
							return recipe.getOutput();
						}
					}
				}
			} else {
				if (MatchHelper.isItemMatch(rTop, inputTop) && MatchHelper.isItemMatch(rBottom, inputBottom)
						|| MatchHelper.isItemMatch(rTop, inputBottom) && MatchHelper.isItemMatch(rBottom, inputTop)) {
					Collection<ItemStack> intputs = recipe.getInputs();
					for (ItemStack input : intputs) {
						if (MatchHelper.isItemMatch(input, inputMid)) {
							inputs.add(inputTop);
							inputs.add(inputMid);
							inputs.add(inputBottom);
							return recipe.getOutput();
						}
					}
				}
			}
		}

		return ItemStack.EMPTY;
	}

}