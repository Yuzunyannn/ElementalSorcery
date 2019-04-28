package yuzunyan.elementalsorcery.crafting;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyan.elementalsorcery.entity.EntityCrafting;

@SideOnly(Side.CLIENT)
public interface ICraftingLaunchAnime {

	/**
	 * 动画tick更新
	 * 
	 * @param endTick
	 *            -1表示正常运行，暂未结束，正数表明结束剩余的时间
	 */
	@SideOnly(Side.CLIENT)
	void update(EntityCrafting entity, int endTick);

	/** 进行自定义渲染 */
	@SideOnly(Side.CLIENT)
	void deRender(EntityCrafting entity, double x, double y, double z, float entityYaw, float partialTicks);

	/** 结束时候特效 */
	@SideOnly(Side.CLIENT)
	void endEffect(EntityCrafting entity, World world, BlockPos pos);

}
