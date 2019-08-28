package yuzunyannn.elementalsorcery.crafting.altar;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.crafting.ICraftingCommit;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunchAnime;
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

	/** 获取结束时间 */
	default public int getEndTime(TileStaticMultiBlock tileSupremeCraftingTable) {
		return 20;
	}

	@SideOnly(Side.CLIENT)
	public ICraftingLaunchAnime getAnime();

}
