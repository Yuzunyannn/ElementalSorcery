package yuzunyannn.elementalsorcery.elf.edifice;

import java.util.Random;

import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;
import yuzunyannn.elementalsorcery.util.NBTHelper;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public class BuilderHelper {

	public NBTTagCompound data;
	final public IBuilder builder;

	protected int randNum;

	public BuilderHelper(IBuilder builder) {
		data = builder.getFloorData();
		this.builder = builder;
		randNum = data.getInteger("rand");
	}

	public NBTTagCompound getNBT() {
		return data;
	}

	public EnumFacing toward() {
		return EnumFacing.getFront(data.getByte("toward"));
	}

	public BuilderHelper toward(EnumFacing face) {
		data.setByte("toward", (byte) face.getIndex());
		return this;
	}

	public BlockPos blockPos(String key) {
		return NBTHelper.getBlockPos(data, key);
	}

	public void blockPos(String key, BlockPos pos) {
		NBTHelper.setBlockPos(data, key, pos);
	}

	public BuilderHelper toward(Random rand) {
		data.setByte("toward", (byte) EnumFacing.HORIZONTALS[rand.nextInt(EnumFacing.HORIZONTALS.length)].getIndex());
		return this;
	}

	public BuilderHelper startRand(Random rand) {
		data.setInteger("rand", rand.nextInt());
		return this;
	}

	/** 假随机，有复原性 */
	public int randNextInt() {
		randNum = (137 * randNum + 11);
		return Math.abs(randNum);
	}

	public int randNextInt(int bound) {
		return this.randNextInt() % bound;
	}

	public TileElfTreeCore treeCore() {
		return BlockHelper.getTileEntity(builder.getWorld(), builder.getEdificeCore(), TileElfTreeCore.class);
	}

	public IBlockState blockPlank() {
		return Blocks.PLANKS.getDefaultState();
	}

	public IBlockState blockFence() {
		return Blocks.OAK_FENCE.getDefaultState();
	}

	public IBlockState blockStairs() {
		return Blocks.OAK_STAIRS.getDefaultState();
	}

	public IBlockState blockStairs(EnumFacing face) {
		return blockStairs().withProperty(BlockStairs.FACING, face);
	}

	public IBlockState blockCarpet(EnumDyeColor color) {
		IBlockState CARPET = Blocks.CARPET.getDefaultState();
		return CARPET.withProperty(BlockCarpet.COLOR, color);
	}

	public IBlockState blockStone(int type) {
		IBlockState STONE = Blocks.STONE.getDefaultState();
		switch (type) {
		case 10:
			STONE = STONE.withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE);
			break;
		default:
			STONE = STONE.withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE_SMOOTH);
			break;
		}
		return STONE;
	}

	// 地毯
	public void genCarpet(EnumDyeColor color) {
		BlockPos pos = builder.getFloorBasicPos();
		int treeSize = builder.getEdificeSize();
		int size = GenElfEdifice.getFakeCircleLen(treeSize, 0, 2);
		IBlockState CARPET = this.blockCarpet(color);
		for (int i = -size + 1; i < size; i++) {
			int n = GenElfEdifice.getFakeCircleLen(treeSize, i, 2);
			for (int a = 0; a < n; a++) {
				builder.setBlockState(pos.add(i, 0, a), CARPET);
				builder.setBlockState(pos.add(i, 0, -a), CARPET);
			}
		}
	}

	// 吊灯
	public void genLamp(BlockPos at, int len) {
		IBlockState TRAP_DOOR = Blocks.TRAPDOOR.getDefaultState();
		IBlockState GLOWSTONE = Blocks.GLOWSTONE.getDefaultState();
		TRAP_DOOR = TRAP_DOOR.withProperty(BlockTrapDoor.OPEN, true);
		for (int i = 0; i < len; i++) {
			builder.setBlockState(at, blockFence());
			at = at.down();
		}
		builder.setBlockState(at, GLOWSTONE);
		for (EnumFacing h : EnumFacing.HORIZONTALS) {
			TRAP_DOOR = TRAP_DOOR.withProperty(BlockTrapDoor.FACING, h);
			builder.setBlockState(at.offset(h), TRAP_DOOR);
		}
	}

	// 树叶灯
	public void genLeafLamp(BlockPos at) {
		IBlockState GLOWSTONE = Blocks.GLOWSTONE.getDefaultState();
		IBlockState LEAF = ESInit.BLOCKS.ELF_LEAF.getDefaultState().withProperty(BlockLeaves.DECAYABLE, false);
		builder.setBlockState(at, GLOWSTONE);
		for (EnumFacing h : EnumFacing.VALUES) builder.trySetBlockState(at.offset(h, 1), LEAF);
	}

	// 红石灯
	public void genRedstoneLamp(BlockPos at) {
		IBlockState REDSTONE_BLOCK = Blocks.REDSTONE_BLOCK.getDefaultState();
		IBlockState REDSTONE_LAMP = Blocks.REDSTONE_LAMP.getDefaultState();
		builder.setBlockState(at, REDSTONE_LAMP);
		builder.setBlockState(at.up(), REDSTONE_BLOCK);
		builder.setBlockState(at.up(2), REDSTONE_LAMP);
	}

}
