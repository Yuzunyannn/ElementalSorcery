package yuzunyannn.elementalsorcery.elf.edifice;

import java.util.Map;

import net.minecraft.block.BlockCarpet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;

/** 描述的建造者 */
public interface IBuilder {

	public BlockPos getEdificeCore();
	
	public int getEdificeSize();

	public int getEdificeHigh();

	public BlockPos getFloorBasicPos();

	public int getFloorHigh();


	public NBTTagCompound getFloorData();

	public IBlockState getBlockState(BlockPos pos);

	/**
	 * 设置一个方块，并非及时，但对于getBlockState来说，是及时的
	 *
	 */
	public void setBlockState(BlockPos pos, IBlockState state);

	public void spawn(Entity entity);

	/** 将缓存的方块转化为building */
	public Map<BlockPos,IBlockState> asBlockMap();

	/** 获取需要建造的方块个数 */
	public int getBlockCount();

	/** 获取是否有缓存 */
	public boolean hasBlockCache(BlockPos pos);

	/** 这个函数是获取真实的世界，放置方块时应当直接使用setBlockState */
	public World getWorld();

	default boolean isAirBlock(BlockPos pos) {
		return getBlockState(pos) == Blocks.AIR.getDefaultState();
	}

	default void trySetBlockState(BlockPos pos, IBlockState state) {
		IBlockState origin = getBlockState(pos);
		if (origin.getBlock().isReplaceable(getWorld(), pos)) setBlockState(pos, state);
		else if (origin.getBlock() instanceof BlockCarpet) setBlockState(pos, state);
	}

	default public void spawn(EntityElfBase entity) {
		entity.setEdificeCore(this.getEdificeCore());
		spawn(entity);
	}

}
