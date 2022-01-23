package yuzunyannn.elementalsorcery.building;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fluids.BlockFluidBase;
import yuzunyannn.elementalsorcery.entity.EntityBlockMove;

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
		if (block instanceof BlockBush) return true;
		if (block instanceof BlockRailBase) return true;
		if (block instanceof BlockFluidBase) return true;
		if (block instanceof BlockRedstoneWire) return true;
		if (block instanceof BlockSign) return true;
		if (block instanceof IGrowable || block instanceof IPlantable) return true;
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

	static public NBTTagCompound tryFaceTile(IBlockState state, NBTTagCompound save, EnumFacing facing,
			boolean needCopy) {
		if (state.getBlock() instanceof BlockSkull) {
			if (needCopy) save = save.copy();
			save.setByte("Rot", (byte) faceRot(save.getByte("Rot"), facing));
		}
		return save;
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

	/** 根据方向修正skull数值 */
	static public int faceRot(int rot, EnumFacing facing) {
		switch (facing) {
		case SOUTH:
			return (rot + 8) % 16;
		case EAST:
			return (rot + 4) % 16;
		case WEST:
			return (rot - 4 + 16) % 16;
		default:
			return rot;
		}
	}

	public void buildState(World world, BlockPos pos) {
		IBlockState state = this.getState();
		NBTTagCompound nbtSave = this.getTileNBTSave();
		EntityBlockMove.putBlock(world, null, pos, getItemStack(), state, null, nbtSave, true);
	}

	@Nullable
	public IBlockState getState() {
		if (entry == null) return null;
		IBlockState state = building.infoList.get(entry.getValue()).getState();
		return faceSate(state, this.facing);
	}

	@Nullable
	public NBTTagCompound getTileNBTSave() {
		Building.BlockInfo info = building.infoList.get(entry.getValue());
		NBTTagCompound nbt = info.getNBTData();
		if (nbt == null) return null;
		IBlockState state = info.getState();
		return tryFaceTile(state, nbt, facing, true);
	}

	@Nonnull
	public ItemStack getItemStack() {
		if (entry == null) return ItemStack.EMPTY;
		BlockItemTypeInfo iti = building.typeInfoList.get(building.infoList.get(entry.getValue()).typeIndex);
		ItemStack stack = iti.blockStack.copy();
		stack.setCount(BlockItemTypeInfo.getCountFromState(getState()));
		return stack;
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
