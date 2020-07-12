package yuzunyannn.elementalsorcery.api.tile;

public interface IAcceptBurnPower {
	/**
	 * 接受燃烧能量
	 * 
	 * @param amount
	 *            能量大小
	 * @param level
	 *            能量等级，可能传入0，表示测试是否需要接受能量
	 * @return True 表示需要能量，并成功接受
	 */
	boolean acceptBurnPower(int amount, int level);
}
