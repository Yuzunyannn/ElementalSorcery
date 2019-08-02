package yuzunyannn.elementalsorcery.crafting.altar;

import yuzunyannn.elementalsorcery.crafting.ICraftingCommit;
import yuzunyannn.elementalsorcery.tile.altar.TileStaticMultiBlock;

public interface ICraftingAltar extends ICraftingCommit {

	public void update(TileStaticMultiBlock tileMul);

	public boolean canContinue(TileStaticMultiBlock tileMul);

	/**
	 * 结束
	 * 
	 * @return 返回是否成功,true成功
	 */
	public boolean end(TileStaticMultiBlock tileMul);

}
