package yuzunyannn.elementalsorcery.api.tile;

import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.element.ElementStack;

/** 只有继承这个接口的TileEntity并且拥有仓库，才能被祭坛所使用 */
public interface IAltarWake extends IElementInventoryPromote {

	/** 获取元素，对于继承者来说，是元素送过来了 */
	public final static int OBTAIN = 0;
	/** 给予元素，对于继承者来说，是元素送出去了 */
	public final static int SEND = 1;

	/**
	 * 对祭坛指定位置上的TileEntity每进行一次元素交互时候，调用的函数，客户端可以开始动画
	 * 
	 * @param type 唤醒的原型
	 * @param from 唤醒来源
	 * @param by   来源元素
	 * @return 唤醒是否成功 (目前没有用
	 */
	boolean wake(int type, @Nullable BlockPos from);

	/**
	 * 发送一个粒子效果
	 * 
	 */
	@SideOnly(Side.CLIENT)
	default void updateEffect(World world, int type, ElementStack estack, Vec3d pos) {
	}

}
