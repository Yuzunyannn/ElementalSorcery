package yuzunyannn.elementalsorcery.element;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.api.element.IStarFlowerCast;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public class ElementMetal extends ElementCommon implements IStarFlowerCast {

	public static final int COLOR = 0xFFD700;
	protected List<ItemStack> ores;

	public ElementMetal() {
		super(COLOR, "metal");
	}

	@Override
	public ElementStack starFlowerCasting(World world, BlockPos pos, ElementStack estack, int tick) {
		if (world.isRemote) return estack;
		if (tick % 100 != 0) return estack;
		int range = getStarFlowerRange(estack);
		BlockPos at = BlockHelper.tryFind(world, (w, p) -> {
			IBlockState state = w.getBlockState(p);
			return state.getBlock() == Blocks.STONE || state.getBlock() == Blocks.COBBLESTONE;
		}, pos, Math.min(estack.getPower() / 50, 16), range, range);
		if (at == null) return estack;
		if (ores == null) this.initCanUseOres();

		ItemStack ore = ores.get(world.rand.nextInt(ores.size()));
		IToElementInfo info = ElementMap.instance.toElement(ore);
		ElementStack[] elements = info.element();
		ElementStack meta = elements[0];

		int rCount = (int) (meta.getCount() * (0.7 + world.rand.nextFloat() * 0.5));
		int rPower = (int) (meta.getCount() * (0.5 + world.rand.nextFloat() * 1));
		if (estack.getCount() > rCount && estack.getCount() > rPower) {
			Block block = Block.getBlockFromItem(ore.getItem());
			IBlockState state = block.getStateFromMeta(ore.getMetadata());
			world.setBlockState(at, state);
			world.playEvent(2001, at, Block.getStateId(world.getBlockState(at)));
			estack.shrink(rCount);
		}

		return estack;
	}

	public void initCanUseOres() {
		ores = new ArrayList<>();
		for (String name : OreDictionary.getOreNames()) {
			if (!name.startsWith("ore")) continue;
			NonNullList<ItemStack> list = OreDictionary.getOres(name);
			for (ItemStack stack : list) {
				IToElementInfo info = ElementMap.instance.toElement(stack);
				if (info == null) continue;
				ElementStack[] elements = info.element();
				if (elements.length > 0 && elements[0].getElement() != this) break;
				if (Block.getBlockFromItem(stack.getItem()) != Blocks.AIR) ores.add(stack);
				break;
			}
		}
	}

}
