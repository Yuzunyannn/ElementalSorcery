package yuzunyannn.elementalsorcery.building;

import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MultiBlock {

	// 多方块结构对应的建筑
	private Building altar;

	// 检测坐标
	private BlockPos pos;
	// 偏移坐标
	private BlockPos posOff;

	// 记录使用的真实坐标
	private BlockPos realPos;
	// 建筑的朝向
	private EnumFacing facing = EnumFacing.NORTH;

	// 记录的世界
	private World world;

	public MultiBlock(Building altar, TileEntity tile, BlockPos posOff) {
		this.altar = altar;
		this.pos = tile.getPos();
		this.posOff = posOff;
		this.world = tile.getWorld();
		this.realPos = this.pos.add(this.posOff);
	}

	public MultiBlock(Building altar, World world, BlockPos pos) {
		this.altar = altar;
		this.pos = pos;
		this.posOff = BlockPos.ORIGIN;
		this.world = world;
		this.realPos = this.pos.add(this.posOff);
	}

	public MultiBlock moveTo(BlockPos pos) {
		this.pos = pos;
		return this;
	}

	public MultiBlock setPosOffset(BlockPos posOffset) {
		this.posOff = posOffset;
		return this;
	}

	public boolean check(EnumFacing face) {
		BuildingBlocks iter = this.altar.getBuildingIterator().setFace(face);
		iter.setPosOff(BuildingFace.face(this.posOff, face).add(this.pos));
		while (iter.next()) {
			BlockPos pos = iter.getPos();
			IBlockState should = iter.getState();
			IBlockState real = world.getBlockState(pos);
			if (should.equals(real)) continue;
			else if (should.getBlock() instanceof BlockStairs) {
				if (should.getBlock() != real.getBlock()) return false;
				real = real.getBlock().getActualState(real, world, pos);
				BlockStairs.EnumShape shape = real.getValue(BlockStairs.SHAPE);
				if (shape != BlockStairs.EnumShape.STRAIGHT) continue;
			}
			return false;
		}
		this.realPos = BuildingFace.face(this.posOff, face).add(this.pos);
		this.facing = face;
		return true;
	}

	/** 获取当前祭坛方向 */
	public EnumFacing face() {
		return this.facing;
	}

	/** 设置祭坛方向 */
	public void face(EnumFacing facing) {
		this.facing = facing;
	}

	// 特殊方块位置
	private ArrayList<BlockPos> specialBlocks = new ArrayList<BlockPos>();

	// 添加一个新的方块检测处
	public int addSpecialBlock(BlockPos posOff) {
		specialBlocks.add(posOff);
		return specialBlocks.size() - 1;
	}

	// 获取位置
	@Nullable
	public BlockPos getSpecialBlockPos(int index) {
		try {
			if (facing == EnumFacing.NORTH) return realPos.add(specialBlocks.get(index));
			BlockPos pos = specialBlocks.get(index);
			pos = realPos.add(BuildingFace.face(pos, facing));
			return pos;
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	// 获取个数
	public int getSpecialBlockCount() {
		return specialBlocks.size();
	}

	// 根据索引获取检测的特殊方块
	public IBlockState getSpecialBlockState(int index) {
		return world.getBlockState(this.getSpecialBlockPos(index));
	}

	// 根据索引获取检测的特殊方块Tile
	public TileEntity getSpecialTileEntity(int index) {
		return world.getTileEntity(this.getSpecialBlockPos(index));
	}

	// 根据索引获取检测的特殊方块Tile
	public <T extends TileEntity> T getSpecialTileEntity(int index, Class<T> cls) {
		TileEntity tile = world.getTileEntity(this.getSpecialBlockPos(index));
		if (tile.getClass().isAssignableFrom(cls)) return (T) tile;
		return null;
	}

}
