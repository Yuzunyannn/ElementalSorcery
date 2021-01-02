package yuzunyannn.elementalsorcery.explore;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IExploreHandle {

	/**
	 * 进行一次探索
	 * 
	 * @param state    表明这次探索是不是对于指定方块的，如果state不为空，则表明是对于指定方块的
	 * 
	 * @param portrait 表明这次探索是不是对于指定实体的，如果不为空，则表明是对指定实体的
	 * 
	 * @return 返回true表示结束，返回false表示未结束，记录时会卡在这里
	 */
	public boolean explore(NBTTagCompound data, World world, BlockPos pos, int level, @Nullable IBlockState state,
			@Nullable EntityLivingBase portrait);

	/** 是否记录过 */
	public boolean hasExplore(NBTTagCompound data);

	/**
	 * 获取探索的信息
	 *
	 */
	@Nullable
	@SideOnly(Side.CLIENT)
	public void addExploreInfo(NBTTagCompound data, List<String> tooltip);

}
