package yuzunyannn.elementalsorcery.api.element;

import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IISCraftHanlder {

	public List<Integer> getSlotIndexMap();

	public ItemStack match(World world, BlockPos pos, Map<Integer, ItemStack> slotMap, List<ItemStack> inputs,
			List<ItemStack> remains);

	public default int complexIncr() {
		return 0;
	};

	public boolean isKeyItem(ItemStack stack);;
}