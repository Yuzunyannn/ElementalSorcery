package yuzunyannn.elementalsorcery.api.tile;

import net.minecraft.util.EnumFacing;

public interface IAcceptMagicPesky extends IAcceptMagic {

	/** 需求每次魔力最小的能量，小于该能量的魔力，不应被给予，如果power不作为power使用的时候，该函数无意义 */
	public int requireMinMagicPower();

	/** 需求每次魔力最小的量，小于该量的魔力，不应被给予 */
	public int requireMinMagicCount();

	/** 某个面是否可以接受 */
	public boolean canRecvMagic(EnumFacing facing);

	/** 需求等级（0-10），需求等级越高，理应越优先获得更多的魔力，但可根据实际情况，自行定义分配方式 */
	public int requireLevel();

	/** 获取当前容量 */
	public int getCurrentCapacity();

	/** 获取最大容量 */
	public int getMaxCapacity();
}
