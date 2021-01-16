package yuzunyannn.elementalsorcery.crafting;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ICraftingLaunchAnime {

	/**
	 * 动画tick更新
	 * 
	 * @param endTick
	 *            -1表示正常运行，暂未结束，正数表明结束剩余的时间
	 */
	@SideOnly(Side.CLIENT)
	void update(ICraftingCommit commit, World world, BlockPos pos, int endTick);

	/** 进行自定义渲染 */
	@SideOnly(Side.CLIENT)
	void doRender(ICraftingCommit commit, double x, double y, double z, float roate, float partialTicks);

	/** 结束时候特效 */
	@SideOnly(Side.CLIENT)
	void endEffect(ICraftingCommit commit, World world, BlockPos pos, int flag);

}
