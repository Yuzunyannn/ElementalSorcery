package yuzunyannn.elementalsorcery.api.element;

import javax.annotation.Nonnull;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.element.ElementStack;

public interface IStarFlowerCast {

	/**
	 * 星之花释放，每tick调用
	 * 
	 * @param tick 每一tick增加1，不会持久化
	 * 
	 * @return 返回一个元素，通常是传入的estack，如不是estack元素之花则会更新到返回的元素
	 */
	@Nonnull
	ElementStack starFlowerCasting(World world, BlockPos pos, ElementStack estack, int tick);

}
