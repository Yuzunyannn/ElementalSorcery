package yuzunyannn.elementalsorcery.api.tile;

public interface IElementInventoryPromote {

	/** 当TileEntity状态改变时，通常是元素被拿光时或者从无到有时，可以进行C/S同步 */
	void onInventoryStatusChange();

}
