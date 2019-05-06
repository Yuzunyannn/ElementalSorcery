package yuzunyan.elementalsorcery.api.ability;

/** 只有继承这个接口的TileEntity并且拥有仓库，才能被祭坛所使用 */
public interface IAltarWake {

	/** 获取元素，元素送过来 */
	public final static int OBTAIN = 0;
	/** 给予元素，将元素送出去 */
	public final static int SEND = 1;

	/**
	 * 对祭坛指定位置上的TileEntity每进行一次元素交互时候，调用的函数，客户端可以开始动画
	 * 
	 * @param type
	 *            唤醒的原型
	 * @return 唤醒是否成功
	 */
	boolean wake(int type);

}
