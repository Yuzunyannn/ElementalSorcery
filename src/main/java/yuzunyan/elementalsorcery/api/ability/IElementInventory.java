package yuzunyan.elementalsorcery.api.ability;

import javax.annotation.Nonnull;

import yuzunyan.elementalsorcery.api.element.ElementStack;

public interface IElementInventory extends IItemCapbiltitySyn {

	/**
	 * 设置槽位个数
	 * 
	 * @param slots 储存槽位的个数
	 */
	void setSlots(int slots);

	/**
	 * 获取最多有多少个槽位，每个槽位只能存放一种ElementStack
	 * 
	 * @return 储存槽位的个数
	 */
	int getSlots();

	/**
	 * 根据槽位下标获取ElementStack
	 * 
	 * @param slot 访问的槽位
	 * @return 当前在solt的ElementStack
	 */
	@Nonnull
	ElementStack getStackInSlot(int slot);

	/**
	 * 将新的ElementStack放在槽位上
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
	int getMaxSizeInSlot(int slot);

	/**
	 * 插入一个ElementStack到仓库里，只要仓库中有位置
	 * 
	 * @param estack   要插入的ElementStack
	 * @param simulate 如果为true，插入结果仅为模拟的结果
	 * @return 是否成功插入
	 **/
	@Nonnull
	boolean insertElement(@Nonnull ElementStack estack, boolean simulate);

	/**
	 * 从仓库里取来元素
	 *
	 * @param estack   要取出来如同estack一样的内容，该变量不应被任何修改
	 * @param simulate 如果为true，插入结果仅为模拟的结果
	 * @return 实际能取出来的内容
	 **/

	@Nonnull
	ElementStack extractElement(@Nonnull ElementStack estack, boolean simulate);

	/**
	 * 插入一个ElementStack到仓库里的指定位置
	 *
	 * @param slot     要插入的槽位
	 * @param estack   要插入的ElementStack
	 * @param simulate 如果为true，插入结果仅为模拟的结果
	 * @return 是否成功插入
	 **/
	@Nonnull
	boolean insertElement(int slot, @Nonnull ElementStack estack, boolean simulate);

	/**
	 * 从仓库里取指定位置的元素
	 *
	 * @param slot     要取出的槽位
	 * @param estack   要取出来如同estack一样的内容，该变量不应被任何修改
	 * @param simulate 如果为true，插入结果仅为模拟的结果
	 * @return 实际能取出来的内容
	 **/
	@Nonnull
	ElementStack extractElement(int slot, @Nonnull ElementStack estack, boolean simulate);

}
