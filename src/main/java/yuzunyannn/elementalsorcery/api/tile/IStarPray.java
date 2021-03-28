package yuzunyannn.elementalsorcery.api.tile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IStarPray {

	/** default 1 */
	default public float priority(World world, BlockPos pos, EntityLivingBase player) {
		return 1;
	}

	/** 0~1 超出1可以表示迫切 */
	public float hopeDegree(World world, BlockPos pos, EntityLivingBase player);

	public void doPray(World world, BlockPos pos, EntityLivingBase player);

	@SideOnly(Side.CLIENT)
	default public void doPrayClient(World world, BlockPos pos, EntityLivingBase player) {

	}

}
