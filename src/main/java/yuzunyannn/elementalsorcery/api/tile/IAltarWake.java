package yuzunyannn.elementalsorcery.api.tile;

import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.ElementStack;

/** 只有继承这个接口的TileEntity并且拥有仓库，才能被祭坛所使用 */
public interface IAltarWake {

	/** 获取元素，元素送过来 */
	public final static int OBTAIN = 0;
	/** 给予元素，将元素送出去 */
	public final static int SEND = 1;

	/**
	 * 对祭坛指定位置上的TileEntity每进行一次元素交互时候，调用的函数，客户端可以开始动画
	 * 
	 * @param type 唤醒的原型
	 * @param from 唤醒来源
	 * @return 唤醒是否成功
	 */
	boolean wake(int type, @Nullable BlockPos from);

	/** 当TileEntity的元素被拿光时或者从无到有时，可以进行C/S同步 */
	void onEmptyStatusChange();

	/**
	 * 发送一个粒子效果
	 * 
	 */
	@SideOnly(Side.CLIENT)
	void updateEffect(World world, int type, ElementStack estack, Vec3d pos);

}
