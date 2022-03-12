package yuzunyannn.elementalsorcery.block;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.elf.ElfTime;
import yuzunyannn.elementalsorcery.init.ESInit;

public class BlockElfLeaf extends BlockLeaves {

	@SideOnly(Side.CLIENT)
	public IBlockColor getBlockColor() {
		return new IBlockColor() {
			public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos,
					int tintIndex) {
				if (worldIn == null || pos == null) return ColorizerFoliage.getFoliageColorBasic();
				int color = BiomeColorHelper.getFoliageColorAtPos(worldIn, pos);
				int r = (color >> 16) & 0xff;
				int g = (color >> 8) & 0xff;
				int b = (color >> 0) & 0xff;
				int count = 0;
				int miss = 4;
				do {
					pos = pos.down();
					Block block = worldIn.getBlockState(pos).getBlock();
					if (block == BlockElfLeaf.this) count++;
					else {
						miss--;
						if (miss <= 0) break;
					}
				} while (true);
				r = (int) (r * 1.25f + count * 25) % 510;
				g = (int) (g * 1.35f - count * 25) % 255;
				b = (int) (b * 0.75f + count * 12.5) % 510;
				if (r > 255) r = 510 - r;
				if (g < 0) g = 0;
				if (b > 255) b = 510 - b;
				return (r << 16) | (g << 8) | (b << 0);
			}
		};
	}

	public BlockElfLeaf() {
		Blocks.FIRE.setFireInfo(this, 30, 60);
		this.setTranslationKey("elfLeaf");
		this.leavesFancy = true;
		this.setDefaultState(this.blockState.getBaseState().withProperty(CHECK_DECAY, Boolean.valueOf(true))
				.withProperty(DECAYABLE, Boolean.valueOf(true)));
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		if (worldIn.isRemote) return;
		if (state.getValue(BlockLeaves.CHECK_DECAY)) return;
		if (rand.nextFloat() > BlockElfFruit.ELF_FRUIT_GEN_PROBABILITY) return;
		ElfTime time = new ElfTime(worldIn);
		if (!time.at(ElfTime.Period.MORNING)) return;
		// 检测下方是否满足
		BlockPos dPos = pos.down();
		if (!worldIn.isAirBlock(dPos)) return;
		// 必须距离地面5格
		for (int i = 0; i < 5; i++) {
			if (!worldIn.isAirBlock(dPos)) return;
			dPos = dPos.down();
		}
		// 检测上方是否有2个叶子
		BlockPos uPos = pos.up();
		for (int i = 0; i < 2; i++) {
			if (worldIn.getBlockState(uPos) != state) return;
			uPos = uPos.up();
		}
		// 检测周围
		int size = 2;
		dPos = pos.down();
		for (int x = -size; x <= size; x++) {
			for (int z = -size; z <= size; z++) {
				BlockPos at = dPos.add(x, 0, z);
				IBlockState checkState = worldIn.getBlockState(at);
				if (checkState.getBlock() == ESInit.BLOCKS.ELF_FRUIT) return;
			}
		}

		// 生成
		final IBlockState fruitState = ESInit.BLOCKS.ELF_FRUIT.getDefaultState();
		worldIn.setBlockState(pos.down(), fruitState);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		super.randomDisplayTick(stateIn, worldIn, pos, rand);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(ESInit.BLOCKS.ELF_SAPLING);
	}

	@Override
	public EnumType getWoodType(int meta) {
		return BlockPlanks.EnumType.OAK;
	}

	@Override
	public NonNullList<ItemStack> onSheared(ItemStack item, net.minecraft.world.IBlockAccess world, BlockPos pos,
			int fortune) {
		return NonNullList.withSize(1, new ItemStack(this));
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state,
			@Nullable TileEntity te, ItemStack stack) {
		if (!worldIn.isRemote && stack.getItem() == Items.SHEARS) player.addStat(StatList.getBlockStats(this));
		else super.harvestBlock(worldIn, player, pos, state, te, stack);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { CHECK_DECAY, DECAYABLE });
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(DECAYABLE, Boolean.valueOf((meta & 4) == 0))
				.withProperty(CHECK_DECAY, Boolean.valueOf((meta & 8) > 0));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;
		if (!((Boolean) state.getValue(DECAYABLE)).booleanValue()) i |= 4;
		if (((Boolean) state.getValue(CHECK_DECAY)).booleanValue()) i |= 8;
		return i;
	}

	@Override
	protected ItemStack getSilkTouchDrop(IBlockState state) {
		return new ItemStack(this);
	}

}
