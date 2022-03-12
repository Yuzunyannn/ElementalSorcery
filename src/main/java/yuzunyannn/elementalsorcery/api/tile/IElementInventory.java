package yuzunyannn.elementalsorcery.api.tile;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.crafting.IItemCapbiltitySyn;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;

public interface IElementInventory extends IItemCapbiltitySyn, ICustomNBTSerialize {

	/**
	 * 获取最多有多少个槽位，每个槽位只能存放一种ElementStack
	 * 
	 * @return 储存槽位的个数
	 */
	int getSlots();

	/**
	 * 根据槽位下标获取ElementStack，取出来的不一定仓库里真正存储的引用
	 * 
	 * @param slot 访问的槽位
	 * @return 当前在solt的ElementStack
	 */
	@Nonnull
	ElementStack getStackInSlot(int slot);

	/**
	 * 将新的ElementStack放在槽位上，不代表会对estack进行引用，可能会对estack进行引用
	 * 
	 * @param slot   访问的槽位
	 * @param estack 要设置的ElementStack
	 * @return 原来槽位的ElementStack
	 */
	@Nonnull
	ElementStack setStackInSlot(int slot, @Nonnull ElementStack estack);

	/**
	 * 根据槽位下标获取槽位的最大容量
	 * 
	 * @param slot 访问的槽位
	 * @return 该槽位的最大容量，-1表示无限
	 */
	default int getMaxSizeInSlot(int slot) {
		return -1;
	}

	/**
	 * 插入一个ElementStack到仓库里，只要仓库中有位置
	 * 
	 * @param estack   要插入的ElementStack
	 * @param simulate 如果为true，插入结果仅为模拟的结果
	 * @return 是否成功插入
	 **/

	default boolean insertElement(ElementStack estack, boolean simulate) {
		for (int i = 0; i < this.getSlots(); i++) {
			if (insertElement(i, estack, simulate)) return true;
		}
		return false;
	}

	/**
	 * 从仓库里取来元素
	 *
	 * @param estack   要取出来如同estack一样的内容，该变量不应被任何修改
	 * @param simulate 如果为true，插入结果仅为模拟的结果
	 * @return 实际能取出来的内容
	 **/
	@Nonnull
	default ElementStack extractElement(ElementStack estack, boolean simulate) {
		if (estack.isEmpty()) return ElementStack.EMPTY.copy();
		ElementStack ret = ElementStack.EMPTY.copy();
		ElementStack tmp = estack.copy();
		for (int i = 0; i < this.getSlots(); i++) {
			ElementStack _new = extractElement(i, tmp, simulate);
			ret.growOrBecome(_new);
			if (ret.arePowerfulAndMoreThan(estack)) return ret;
			else tmp.grow(-_new.getCount());
		}
		return ret;
	}

	/**
	 * 插入一个ElementStack到仓库里的指定位置
	 *
	 * @param slot     要插入的槽位
	 * @param estack   要插入的ElementStack
	 * @param simulate 如果为true，插入结果仅为模拟的结果
	 * @return 是否成功插入
	 **/
	boolean insertElement(int slot, @Nonnull ElementStack estack, boolean simulate);

	/**
	 * 从仓库里取指定位置的元素
	 *
	 * @param slot     要取出的槽位
	 * @param estack   要取出来如同estack一样的内容，该变量不应被任何修改
	 * @param simulate 如果为true取出的结果仅为模拟的结果
	 * @return 实际能取出来的内容
	 **/
	@Nonnull
	ElementStack extractElement(int slot, @Nonnull ElementStack estack, boolean simulate);

	@SideOnly(Side.CLIENT)
	default void addInformation(@Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		ElementHelper.addElementInformation(this, worldIn, tooltip, flagIn);
	}

}
