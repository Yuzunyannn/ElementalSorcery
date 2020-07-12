package yuzunyannn.elementalsorcery.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture.Mapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.init.ESInitInstance;

public class BlocksEStone {

	public static Block newEStone() {
		return new EStone().setUnlocalizedName("estone");
	}

	public static Block newEStoneChiseled() {
		return new EStone().setUnlocalizedName("estoneChiseled");
	}

	public static class EStone extends BlockQuartz implements Mapper {

		public EStone() {
			this.setUnlocalizedName("estone");
			this.setHarvestLevel("pickaxe", 1);
			this.setHardness(7.5F);
		}

		@Override
		public String apply(ItemStack stack) {
			return BlockQuartz.EnumType.byMetadata(stack.getMetadata()).toString();
		}
	}

	public static class EStoneSlab extends Block {

		public ItemBlock getItemBlock() {
			return new ItemBlock(this) {
				@Override
				public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
						EnumFacing facing, float hitX, float hitY, float hitZ) {
					IBlockState state = worldIn.getBlockState(pos);
					ItemStack itemstack = player.getHeldItem(hand);
					// 简单的方块重叠
					if (state.getBlock() == block && !itemstack.isEmpty()) {
						EnumBlockHalf half = state.getValue(HALF);
						if (half != EnumBlockHalf.FULL) {
							if (facing == EnumFacing.UP && half == EnumBlockHalf.BOTTOM) {
								worldIn.setBlockState(pos, state.withProperty(HALF, EnumBlockHalf.FULL));
								SoundType soundtype = block.getSoundType(state, worldIn, pos, player);
								worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS,
										(soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
								itemstack.shrink(1);
								return EnumActionResult.SUCCESS;
							} else if (facing == EnumFacing.DOWN && half == EnumBlockHalf.TOP) {
								worldIn.setBlockState(pos, state.withProperty(HALF, EnumBlockHalf.FULL));
								SoundType soundtype = block.getSoundType(state, worldIn, pos, player);
								worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS,
										(soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
								itemstack.shrink(1);
								return EnumActionResult.SUCCESS;
							}
						}
					}
					return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
				}
			};
		}

		public static final PropertyEnum<EnumBlockHalf> HALF = PropertyEnum.<EnumBlockHalf>create("half",
				EnumBlockHalf.class);
		protected static final AxisAlignedBB AABB_BOTTOM_HALF = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
		protected static final AxisAlignedBB AABB_TOP_HALF = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);

		public EStoneSlab() {
			super(Material.ROCK);
			this.setUnlocalizedName("estoneSlab");
			this.setDefaultState(this.blockState.getBaseState().withProperty(HALF, EnumBlockHalf.BOTTOM));
			this.setHarvestLevel("pickaxe", 1);
			this.setHardness(7.5F);
			this.setLightOpacity(255);
		}

		@Override
		public BlockStateContainer createBlockState() {
			return new BlockStateContainer(this, HALF);
		}

		@Override
		public IBlockState getStateFromMeta(int meta) {
			boolean isFull = (meta & 2) == 1;
			boolean isTop = (meta & 1) == 1;
			return this.getDefaultState().withProperty(HALF,
					isFull ? EnumBlockHalf.FULL : (isTop ? EnumBlockHalf.TOP : EnumBlockHalf.BOTTOM));
		}

		@Override
		public int getMetaFromState(IBlockState state) {
			return state.getValue(HALF).ordinal();
		}

		// 放下去！
		@Override
		public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
				float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
			IBlockState iblockstate = super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer,
					hand).withProperty(HALF, EnumBlockHalf.BOTTOM);
			return facing != EnumFacing.DOWN && (facing == EnumFacing.UP || (double) hitY <= 0.5D) ? iblockstate
					: iblockstate.withProperty(HALF, EnumBlockHalf.TOP);
		}

		// 形态
		@Override
		public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos,
				EnumFacing face) {
			if (this.isFullCube(state)) {
				return BlockFaceShape.SOLID;
			} else if (face == EnumFacing.UP && state.getValue(HALF) == EnumBlockHalf.TOP) {
				return BlockFaceShape.SOLID;
			} else {
				return face == EnumFacing.DOWN && state.getValue(HALF) == EnumBlockHalf.BOTTOM ? BlockFaceShape.SOLID
						: BlockFaceShape.UNDEFINED;
			}
		}

		@Override
		public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
			switch (state.getValue(HALF)) {
			case TOP:
				return AABB_TOP_HALF;
			case BOTTOM:
				return AABB_BOTTOM_HALF;
			case FULL:
				return FULL_BLOCK_AABB;
			}
			return FULL_BLOCK_AABB;
		}

		// 上方是否能放东西
		@Override
		public boolean isTopSolid(IBlockState state) {
			return state.getValue(HALF) != EnumBlockHalf.BOTTOM;
		}

		@Override
		public boolean isOpaqueCube(IBlockState state) {
			return state.getValue(HALF) == EnumBlockHalf.FULL;
		}

		@Override
		public boolean isFullCube(IBlockState state) {
			return state.getValue(HALF) == EnumBlockHalf.FULL;
		}

		@Override
		public int quantityDropped(IBlockState state, int fortune, Random random) {
			return this.isFullCube(state) ? 2 : 1;
		}

		// 测试渲染
		@Override
		public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
			if (net.minecraftforge.common.ForgeModContainer.disableStairSlabCulling)
				return super.doesSideBlockRendering(state, world, pos, face);

			if (state.isOpaqueCube()) return true;

			EnumBlockHalf side = state.getValue(HALF);
			return (side == EnumBlockHalf.TOP && face == EnumFacing.UP)
					|| (side == EnumBlockHalf.BOTTOM && face == EnumFacing.DOWN);
		}

		public static enum EnumBlockHalf implements IStringSerializable {
			BOTTOM("bottom"), TOP("top"), FULL("full");

			private final String name;

			private EnumBlockHalf(String name) {
				this.name = name;
			}

			public String toString() {
				return this.name;
			}

			public String getName() {
				return this.name;
			}
		}

	}

	public static class EStoneStairs extends BlockStairs {

		public EStoneStairs() {
			super(ESInitInstance.BLOCKS.ESTONE.getDefaultState());
			this.setUnlocalizedName("estoneStairs");
			this.setHarvestLevel("pickaxe", 1);
			this.setHardness(7.5F);
		}

	}
}
