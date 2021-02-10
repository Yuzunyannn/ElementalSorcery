package yuzunyannn.elementalsorcery.building;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;

/** 遍历用类 */
public class BuildingBlocks {

	private Iterator<Map.Entry<BlockPos, Integer>> iter = null;
	private List<Map.Entry<BlockPos, Integer>> after = null;
	private Map.Entry<BlockPos, Integer> entry = null;
	private final Building building;
	private BlockPos off = BlockPos.ORIGIN;
	private EnumFacing facing = EnumFacing.NORTH;

	public BuildingBlocks(Building building) {
		this.building = building;
	}

	public boolean next() {
		if (iter == null) {
			iter = building.blockMap.entrySet().iterator();
			after = new LinkedList<>();
		}
		// 是否拥有下一个
		while (iter.hasNext()) {
			entry = iter.next();
			if (after != null && needToLater()) {
				after.add(entry);
				continue;
			}
			return true;
		}
		// 处理after
		if (after != null && !after.isEmpty()) {
			iter = after.iterator();
			entry = iter.next();
			after = null;
			return true;
		}
		iter = null;
		entry = null;
		after = null;
		return false;
	}

	protected boolean needToLater() {
		IBlockState state = this.getState();
		return needToLater(state);
	}

	public static boolean needToLater(IBlockState state) {
		Block block = state.getBlock();
		if (block instanceof BlockCarpet || block instanceof BlockTorch) return true;
		if (block instanceof BlockFluidBase) return true;
		if (block instanceof IGrowable) return true;
		if (block instanceof BlockLadder) return true;
		if (block instanceof BlockButton) return true;
		return false;
	}

	/** 根据方向修正pos */
	static public BlockPos facePos(BlockPos pos, EnumFacing facing) {
		switch (facing) {
		case SOUTH:
			pos = new BlockPos(-pos.getX(), pos.getY(), -pos.getZ());
			break;
		case EAST:
			pos = new BlockPos(-pos.getZ(), pos.getY(), pos.getX());
			break;
		case WEST:
			pos = new BlockPos(pos.getZ(), pos.getY(), -pos.getX());
			break;
		default:
			break;
		}
		return pos;
	}

	public BlockPos getPos() {
		if (entry == null) return null;
		BlockPos pos = entry.getKey();
		return facePos(pos, this.facing).add(off);
	}

	/** 根据方向修正state */
	static public IBlockState faceSate(IBlockState state, EnumFacing facing) {
		switch (facing) {
		case SOUTH:
			state = state.withRotation(Rotation.CLOCKWISE_180);
			break;
		case EAST:
			state = state.withRotation(Rotation.CLOCKWISE_90);
			break;
		case WEST:
			state = state.withRotation(Rotation.COUNTERCLOCKWISE_90);
			break;
		default:
			break;
		}
		return state;
	}

	public void buildState(World world, BlockPos pos) {
		Building.BlockInfo info = building.infoList.get(entry.getValue());
		IBlockState state = faceSate(info.getState(), this.facing);
		if (state.getBlock() instanceof BlockDoor) {
			world.setBlockState(pos, state, 2);
			IBlockState s = state.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER);
			world.setBlockState(pos.up(), s, 2);
			return;
		}
		world.setBlockState(pos, state);
		NBTTagCompound nbt = info.getNBTData();
		if (nbt == null) return;
		TileEntity tile = world.getTileEntity(pos);
		if (tile == null) return;
		nbt = info.getNBTData(tile);
		tile.deserializeNBT(nbt);
	}

	public IBlockState getState() {
		if (entry == null) return null;
		IBlockState state = building.infoList.get(entry.getValue()).getState();
		return faceSate(state, this.facing);
	}

	public ItemStack getItemStack() {
		return entry == null ? ItemStack.EMPTY
				: building.typeInfoList.get(building.infoList.get(entry.getValue()).typeIndex).blockStack.copy();
	}

	public BuildingBlocks setPosOff(BlockPos pos) {
		off = pos;
		return this;
	}

	public BuildingBlocks setFace(EnumFacing facing) {
		this.facing = facing;
		return this;
	}

	public EnumFacing getFacing() {
		return facing;
	}
}
