package yuzunyannn.elementalsorcery.elf.edifice;

import java.util.Random;

import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class BuilderHelper {

	public NBTTagCompound data;
	final public IBuilder builder;

	public BuilderHelper(IBuilder builder) {
		data = builder.getFloorData();
		this.builder = builder;
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

	public BuilderHelper toward(Random rand) {
		data.setByte("toward", (byte) EnumFacing.HORIZONTALS[rand.nextInt(EnumFacing.HORIZONTALS.length)].getIndex());
		return this;
	}

//	public void pushSurprisePos(BlockPos pos) {
//		NBTTagList list = data.getTagList("suprisePoints", NBTTag.TAG_COMPOUND);
//		NBTTagCompound p = new NBTTagCompound();
//		p.setInteger("x", pos.getX());
//		p.setInteger("y", pos.getY());
//		p.setInteger("z", pos.getZ());
//		list.appendTag(p);
//	}
//
//	public BlockPos popSurprisePos() {
//		NBTTagList list = data.getTagList("suprisePoints", NBTTag.TAG_COMPOUND);
//		if (list.hasNoTags()) return BlockPos.ORIGIN;
//		NBTTagCompound p = list.getCompoundTagAt(list.tagCount() - 1);
//		list.removeTag(list.tagCount() - 1);
//		BlockPos pos = new BlockPos(p.getInteger("x"), p.getInteger("y"), p.getInteger("z"));
//		if (list.hasNoTags()) data.removeTag("suprisePoints");
//		return pos;
//	}

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

}
