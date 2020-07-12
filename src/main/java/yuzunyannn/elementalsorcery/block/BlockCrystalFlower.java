package yuzunyannn.elementalsorcery.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.tile.TileCrystalFlower;

public class BlockCrystalFlower extends BlockBush implements ITileEntityProvider, IGrowable {

	public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 4);

	public BlockCrystalFlower() {
		super(Material.PLANTS, MapColor.GREEN);
		this.setSoundType(SoundType.PLANT);
		this.setUnlocalizedName("crystalFlower");
		Blocks.FIRE.setFireInfo(this, 20, 5);
		this.hasTileEntity = true;
		this.setDefaultState(this.blockState.getBaseState().withProperty(STAGE, 0));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { STAGE });
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(STAGE, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(STAGE);
	}

	@Override
	protected boolean canSustainBush(IBlockState state) {
		return state.getBlock() == ESInitInstance.BLOCKS.LIFE_DIRT;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		// 最高级掉落物品
		if (state.getValue(STAGE) == 4) {
			TileEntity tile = worldIn.getTileEntity(pos);
			if (tile instanceof TileCrystalFlower) {
				ItemStack plant = ((TileCrystalFlower) tile).getCrystal();
				if (!plant.isEmpty()) Block.spawnAsEntity(worldIn, pos, plant);
			}
		}
		// 移除
		worldIn.removeTileEntity(pos);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Items.AIR;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileCrystalFlower();
	}

	@Override
	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
		return state.getValue(STAGE) < 4;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return false;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		int stage = state.getValue(STAGE);
		if (stage < 4) {
			TileEntity tile = worldIn.getTileEntity(pos);
			if (tile instanceof TileCrystalFlower) ((TileCrystalFlower) tile).tryGrow(state);
			else worldIn.setBlockState(pos, state.withProperty(STAGE, stage + 1));
		}
	}

}