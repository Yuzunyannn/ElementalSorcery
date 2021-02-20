package yuzunyannn.elementalsorcery.block;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.element.EffectElement;
import yuzunyannn.elementalsorcery.tile.TileCrystalFlower;
import yuzunyannn.elementalsorcery.tile.TileLifeDirt;
import yuzunyannn.elementalsorcery.util.RandomHelper;

public class BlockLifeFlower extends Block {

	public static final AxisAlignedBB AABB = new AxisAlignedBB(0.09999999403953552D, 0.0D, 0.09999999403953552D,
			0.8999999761581421D, 0.800000011920929D, 0.8999999761581421D);

	public BlockLifeFlower() {
		super(Material.PLANTS);
		this.setUnlocalizedName("lifeFlower");
		this.setSoundType(SoundType.PLANT);
		this.setTickRandomly(true);
		this.setLightLevel(1);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (!pos.down().equals(fromPos)) return;
		if (world.getBlockState(fromPos).getBlock() != ESInit.BLOCKS.MAGIC_POT) {
			this.dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		if (!this.isIntact(worldIn, pos)) return;
		if (placer instanceof EntityPlayerMP)
			ESCriteriaTriggers.ES_TRING.trigger((EntityPlayerMP) placer, "build:garden");
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return worldIn.getBlockState(pos.down()).getBlock() == ESInit.BLOCKS.MAGIC_POT;
	}

	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	public boolean isFullCube(IBlockState state) {
		return false;
	}

	private boolean hasPower(World worldIn, BlockPos pos) {
		IBlockState state = worldIn.getBlockState(pos.down());
		if (state.getBlock() != ESInit.BLOCKS.MAGIC_POT) return false;
		return ((BlockMagicPot) state.getBlock()).hasPower(worldIn, state, pos.down());
	}

	private boolean isIntact(World worldIn, BlockPos pos) {
		IBlockState state = worldIn.getBlockState(pos.down());
		if (state.getBlock() != ESInit.BLOCKS.MAGIC_POT) return false;
		return ((BlockMagicPot) state.getBlock()).isIntact(worldIn, pos.down());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (!this.hasPower(worldIn, pos)) return;
		Vec3d v3d = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
		v3d = v3d.addVector(Math.random() - 0.5, Math.random() - 0.25, Math.random() - 0.5);
		EffectElement effect = new EffectElement(worldIn, v3d.x, v3d.y, v3d.z);
		effect.setColor(RandomHelper.rand.nextInt());
		Effect.addEffect(effect);
	}

	@Override
	public void updateTick(World world, BlockPos flowerPos, IBlockState state, Random rand) {
		if (!this.hasPower(world, flowerPos)) return;
		growAll(world, flowerPos);

		BlockPos potPos = flowerPos.down();
		IBlockState potState = world.getBlockState(potPos);
		int magic = potState.getValue(BlockMagicPot.MAGIC);
		if (magic <= 0) return;
		if (rand.nextDouble() < 0.02)
			world.setBlockState(potPos, potState.withProperty(BlockMagicPot.MAGIC, magic - 1));
	}

	public void tryGrowAll(World world, BlockPos flowerPos) {
		if (!this.hasPower(world, flowerPos)) return;
		growAll(world, flowerPos);
	}

	/** 成长所有内容一次 */
	public void growAll(World world, BlockPos flowerPos) {
		BlockPos center = flowerPos.down(2);
		for (int x = -4; x <= 4; x++) {
			for (int z = -4; z <= 4; z++) {
				BlockPos pos = center.add(x, 0, z);
				if (world.getBlockState(pos).getBlock() != ESInit.BLOCKS.LIFE_DIRT) continue;
				grow(world, pos);
			}
		}
	}

	protected void grow(World world, BlockPos pos) {
		if (world.rand.nextInt(2) == 0) return;
		// 检查上方是否有已经的植物
		if (world.isAirBlock(pos.up())) {
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof TileLifeDirt) newGrow(world, pos, (TileLifeDirt) tile);
			return;
		}
		// 上方已有方块，检测是否可以成长
		pos = pos.up();
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block instanceof IGrowable == false) {
			if (world.isRemote) return;
			if (block == Blocks.REEDS || block == Blocks.CACTUS) growUp(world, pos, 2);
			return;
		}
		// 长大
		IGrowable growable = ((IGrowable) block);
		if (growable.canGrow(world, pos, state, world.isRemote)) {
			if (world.isRemote) return;
			if (block == ESInit.BLOCKS.CRYSTAL_FLOWER) {
				TileEntity tile = world.getTileEntity(pos);
				if (tile instanceof TileCrystalFlower) {
					// 这里可以做一些操作=-=-=-=-=-=-=-=-=
				}
			}
			growable.grow(world, world.rand, pos, state);
		}
	}

	public static void growUp(World world, BlockPos pos, int max) {
		if (world.isAirBlock(pos)) return;
		IBlockState state = world.getBlockState(pos);
		for (int i = 0; i < max; i++) {
			pos = pos.up();
			if (world.isAirBlock(pos)) {
				world.setBlockState(pos, state);
				return;
			}
			if (world.getBlockState(pos).getBlock() != state.getBlock()) return;
		}
	}

	private void newGrow(World world, BlockPos pos, TileLifeDirt tile) {
		if (world.isRemote) return;
		// 生成新的
		ItemStack plant = tile.getPlant();
		// 没有植物索性移除tileentity
		if (plant.isEmpty()) {
			world.removeTileEntity(pos);
			return;
		}
		Item item = plant.getItem();
		Block block = Block.getBlockFromItem(item);
		if (item instanceof IPlantable) {
			IBlockState state = ((IPlantable) item).getPlant(world, pos);
			world.setBlockState(pos.up(), state);
		} else if (block instanceof IGrowable || block == Blocks.CACTUS) {
			world.setBlockState(pos.up(), block.getDefaultState());
		} else if (item == Items.REEDS) {
			world.setBlockState(pos.up(), Blocks.REEDS.getDefaultState());
		} else if (block instanceof BlockBush) {
			world.setBlockState(pos.up(), block.getStateFromMeta(plant.getMetadata()));
		} else {
			world.setBlockState(pos.up(), ESInit.BLOCKS.CRYSTAL_FLOWER.getDefaultState());
			TileEntity t = world.getTileEntity(pos.up());
			if (t instanceof TileCrystalFlower) ((TileCrystalFlower) t).setCrystal(tile.getPlant());
		}
	}
}
