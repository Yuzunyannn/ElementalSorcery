package yuzunyannn.elementalsorcery.tile.altar;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import yuzunyannn.elementalsorcery.api.crafting.IItemStructure;
import yuzunyannn.elementalsorcery.api.crafting.IToElement;
import yuzunyannn.elementalsorcery.api.tile.IGetItemStack;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.crafting.element.ItemStructure;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class TileDeconstructAltarTableAdv extends TileDeconstructAltarTable {

	@Override
	public void initMultiBlock() {
		structure = new MultiBlock(Buildings.DECONSTRUCT_ALTAR_ADV, this, new BlockPos(0, -2, 0));
		structure.addSpecialBlock(new BlockPos(2, 2, 2));
		structure.addSpecialBlock(new BlockPos(2, 2, -2));
		structure.addSpecialBlock(new BlockPos(-2, 2, -2));
		structure.addSpecialBlock(new BlockPos(-2, 2, 2));
		structure.addSpecialBlock(new BlockPos(3, 2, 0));
		structure.addSpecialBlock(new BlockPos(-3, 2, 0));
		structure.addSpecialBlock(new BlockPos(0, 2, 3));
		structure.addSpecialBlock(new BlockPos(0, 2, -3));
	}

	@Override
	public int getAltarDecLevel() {
		return Element.DP_ALTAR_ADV;
	}

	@Override
	public IToElement getToElement() {
		ElementMap map = new ElementMap();
		for (int x = -3; x <= 3; x += 6) {
			for (int z = -3; z <= 3; z += 6) {
				BlockPos pos = this.pos.add(x, -1, z);
				if (world.isAirBlock(pos)) continue;
				IGetItemStack getStack = BlockHelper.getTileEntity(world, pos, IGetItemStack.class);
				if (getStack == null) continue;
				ItemStack stack = getStack.getStack();
				if (stack.isEmpty()) continue;
				IItemStructure itemStructure = ItemStructure.getItemStructure(stack);
				if (itemStructure.isEmpty()) continue;
				map.add(itemStructure);
			}
		}
		if (map.isEmpty()) return ElementMap.instance;
		map.add(ElementMap.instance);
		return map;
	}
}
