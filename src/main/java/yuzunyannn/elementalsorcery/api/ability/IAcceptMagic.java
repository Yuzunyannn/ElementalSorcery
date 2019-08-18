package yuzunyannn.elementalsorcery.api.ability;

import javax.annotation.Nonnull;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import yuzunyannn.elementalsorcery.api.element.ElementStack;

public interface IAcceptMagic {
	/**
	 * 接受魔力
	 * 
	 * @param magic
	 *            魔力的元素栈
	 * @param from
	 *            魔力的给予者位置
	 * @param facing
	 *            魔力获取的面
	 * @return 返回不需要的魔力，如果不接受，直接返回magic
	 */
	public @Nonnull ElementStack accpetMagic(ElementStack magic, BlockPos from, EnumFacing facing);
}
