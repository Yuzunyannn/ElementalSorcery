package yuzunyannn.elementalsorcery.elf.edifice;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BuilderWithInfo implements IBuilder {

	final World world;
	final BlockPos edificeCore;
	final int size, high;
	final FloorInfo info;
	final Map<BlockPos, IBlockState> blockCache = new HashMap<>();

	public BuilderWithInfo(World world, FloorInfo info, int size, int high, BlockPos edificeCore) {
		this.world = world;
		this.info = info;
		this.size = size;
		this.high = high;
		this.edificeCore = edificeCore;
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public BlockPos getFloorBasicPos() {
		return info.basicPos;
	}

	@Override
	public BlockPos getEdificeCore() {
		return edificeCore;
	}

	@Override
	public int getBlockCount() {
		return blockCache.size();
	}

	@Override
	public IBlockState getBlockState(BlockPos pos) {
		IBlockState state = blockCache.get(pos);
		return state == null ? world.getBlockState(pos) : state;
	}

	@Override
	public void setBlockState(BlockPos pos, IBlockState state) {
		blockCache.put(pos, state);
	}

	@Override
	public boolean hasBlockCache(BlockPos pos) {
		return blockCache.containsKey(pos);
	}

	@Override
	public int getFloorHigh() {
		return info.high;
	}

	@Override
	public NBTTagCompound getFloorData() {
		return info.floorData;
	}

	@Override
	public int getEdificeSize() {
		return size;
	}

	@Override
	public int getEdificeHigh() {
		return 0;
	}

	public void buildAll() {
		for (Entry<BlockPos, IBlockState> entry : blockCache.entrySet())
			world.setBlockState(entry.getKey(), entry.getValue());
	}

	@Override
	public void spawn(Entity entity) {
		world.spawnEntity(entity);
	}

	@Override
	public Map<BlockPos, IBlockState> asBlockMap() {
		return blockCache;
	}

}
