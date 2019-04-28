package yuzunyan.elementalsorcery.building;

import java.util.ArrayList;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MultiBlock {

	// 多方块结构对应的建筑
	private Building altar;

	// 检测坐标
	private BlockPos pos;

	// 记录的世界
	private World world;

	public MultiBlock(Building altar, TileEntity tile, BlockPos posOff) {
		this.altar = altar;
		this.pos = tile.getPos().add(posOff);
		this.world = tile.getWorld();
	}

	public MultiBlock(Building altar, World world, BlockPos pos) {
		this.altar = altar;
		this.pos = pos;
		this.world = world;
	}

	public boolean check() {
		Building.BuildingBlocks iter = this.altar.getBuildingBlocks();
		while (iter.next()) {
			BlockPos pos = this.pos.add(iter.getPos());
			IBlockState should = iter.getState();
			IBlockState real = world.getBlockState(pos);
			if (should.equals(real))
				continue;
			else if (should.getBlock() instanceof BlockStairs) {
				if (should.getBlock() != real.getBlock())
					return false;
				real = real.getBlock().getActualState(real, world, pos);
				BlockStairs.EnumShape shape = real.getValue(BlockStairs.SHAPE);
				if (shape == BlockStairs.EnumShape.OUTER_LEFT || shape == BlockStairs.EnumShape.OUTER_RIGHT)
					continue;
			}
			return false;
		}
		return true;
	}

	// 特殊方块位置
	private ArrayList<BlockPos> specialBlocks = new ArrayList<BlockPos>();

	// 添加一个新的方块检测处
	public int addSpecialBlock(BlockPos posOff) {
		specialBlocks.add(pos.add(posOff));
		return specialBlocks.size() - 1;
	}

	// 获取位置
	public BlockPos getSpecialBlockPos(int index) {
		return specialBlocks.get(index);
	}

	// 获取个数
	public int getSpecialBlockCount() {
		return specialBlocks.size();
	}

	// 根据索引获取检测的特殊方块
	public IBlockState getSpecialBlockState(int index) {
		return world.getBlockState(specialBlocks.get(index));
	}

	// 根据索引获取检测的特殊方块Tile
	public TileEntity getSpecialTileEntity(int index) {
		return world.getTileEntity(specialBlocks.get(index));
	}

	// 根据索引获取检测的特殊方块Tile
	public <T extends TileEntity> T getSpecialTileEntity(int index, Class<T> cls) {
		TileEntity tile = world.getTileEntity(specialBlocks.get(index));
		if (tile.getClass().isAssignableFrom(cls))
			return (T) tile;
		return null;
	}

}
