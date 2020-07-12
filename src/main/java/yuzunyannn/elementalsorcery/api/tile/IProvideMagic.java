package yuzunyannn.elementalsorcery.api.tile;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public interface IProvideMagic extends IAcceptMagic {

	/**
	 * 打招呼，可以接受魔力的设备向魔力发送设备打招呼，希望能收到魔力
	 * 
	 * @param from 打招呼的魔力接收设备位置
	 */
	void hi(BlockPos from, EnumFacing facing);

}
