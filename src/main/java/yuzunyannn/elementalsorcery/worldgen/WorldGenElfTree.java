package yuzunyannn.elementalsorcery.worldgen;

import java.util.Random;

import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenMegaJungle;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraftforge.common.IPlantable;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.init.ESInitInstance;

public class WorldGenElfTree extends WorldGenAbstractTree {

	protected final boolean doBlockNotify;
	protected int type;
	public static final IBlockState WOOD = ESInitInstance.BLOCKS.ELF_LOG.getDefaultState();
	public static final IBlockState LEAVE = ESInitInstance.BLOCKS.ELF_LEAF.getDefaultState();

	/** 获取对应的树样子 */
	static public WorldGenElfTree getGenTreeFromBiome(boolean notify, Biome biome) {
		if (biome == Biomes.JUNGLE) {
			return new WorldGenElfTree(notify, 1);
		} else return new WorldGenElfTree(notify, 0);
	}

	public WorldGenElfTree(boolean notify) {
		this(notify, 0);
	}

	public WorldGenElfTree(boolean notify, int type) {
		super(notify);
		doBlockNotify = notify;
		this.type = type;
	}

	@Override
	public void setDecorationDefaults() {

	}

	@Override
	public boolean generate(World worldIn, Random rand, BlockPos position) {
		WorldGenAbstractTree genTree;
		switch (type) {
		case 1:
			genTree = new WorldGenMegaJungle(true, 8, 4, WOOD, LEAVE);
			return genTree.generate(worldIn, rand, position);
		case 2:
			genTree = new WorldGenLargeElfTree(true, rand.nextInt(10) + 24);
			return genTree.generate(worldIn, rand, position);
		case 3:
			genTree = new WorldGenLargeElfTree(true, rand.nextInt(10) + 24);
			((WorldGenLargeElfTree) genTree).clearGround(worldIn, position);
			return genTree.generate(worldIn, rand, position);
		default:
			int treeSize = rand.nextInt(6) + 4;
			genTree = new WorldGenTrees(doBlockNotify, treeSize, WOOD, LEAVE, false);
			return genTree.generate(worldIn, rand, position);
		}
	}

	static public class WorldGenLargeElfTree extends WorldGenAbstractTree {
		// 树干期望高度
		final int height;
		// 树的半长
		final int size = 3;

		public WorldGenLargeElfTree(boolean notify, int height) {
			super(notify);
			this.height = height;
		}

		@Override
		public boolean generate(World world, Random rand, BlockPos pos) {
			if (!(this.canGrowable(world, pos) && this.ensureDirtsUnderneath(pos, world))) return false;
			// 测试
			// for (int y = pos.getY(); y <= pos.getY() + 34 + 16; y++)
			// for (int x = pos.getX() - size * 4; x <= pos.getX() + size * 4;
			// x++)
			// for (int z = pos.getZ() - size * 4; z <= pos.getZ() + size * 4;
			// z++)
			// this.setBlockAndNotifyAdequately(world, new BlockPos(x, y, z),
			// Blocks.AIR.getDefaultState());
			// 生成树根
			genRootOnce(world, pos, (size + 1), (size + 1), size);
			genRootOnce(world, pos, (size + 1), -(size + 1), size);
			genRootOnce(world, pos, -(size + 1), -(size + 1), size);
			genRootOnce(world, pos, -(size + 1), (size + 1), size);
			// 生成树干
			for (int y = pos.getY(); y <= pos.getY() + height; y++) {
				for (int x = pos.getX() - size; x <= pos.getX() + size; x++) {
					this.setBlockAndNotifyAdequately(world, new BlockPos(x, y, pos.getZ() - size),
							WorldGenElfTree.WOOD);
					this.setBlockAndNotifyAdequately(world, new BlockPos(x, y, pos.getZ() + size),
							WorldGenElfTree.WOOD);
				}
				for (int z = pos.getZ() - size + 1; z <= pos.getZ() + size - 1; z++) {
					this.setBlockAndNotifyAdequately(world, new BlockPos(pos.getX() - size, y, z),
							WorldGenElfTree.WOOD);
					this.setBlockAndNotifyAdequately(world, new BlockPos(pos.getX() + size, y, z),
							WorldGenElfTree.WOOD);
				}
			}
			// 生成树叶
			genLeavs(world, rand, pos);
			// 生成树枝
			genBranchs(world, rand, pos, this.height / 4, this.height / 4, rand.nextInt(3) + 3, 0);
			genBranchs(world, rand, pos, this.height / 2, this.height / 3, rand.nextInt(3) + 2, 2);
			// 生成小屋
			int heightOff = height / 4;
			for (int i = 0; i < 10; i++) {
				heightOff += rand.nextInt(height / 10 / 2 + 1) + 1;
				this.genElfCabin(world, rand, pos, heightOff,
						EnumFacing.HORIZONTALS[rand.nextInt(EnumFacing.HORIZONTALS.length)]);
			}
			return true;
		}

		/** 刷小屋外部内容 */
		protected void genCabinOuter(World world, Random rand, BlockPos centerPos, EnumFacing facing) {

		}

		/** 刷小屋内部内容 */
		protected void genCabinInner(World world, Random rand, BlockPos centerPos, EnumFacing facing) {
			this.setBlockAndNotifyAdequately(world, centerPos,
					ESInitInstance.BLOCKS.ELF_LOG_CABIN_CENTER.getDefaultState());
		}

		protected BlockPos lastCenterPos = null;

		// 生成小屋
		private boolean genElfCabin(World world, Random rand, BlockPos pos, int height, EnumFacing facing) {
			int halfWidth = size - 1;
			if (!this.checkCabin(world, pos, height, halfWidth, facing)) return false;
			BlockPos centerPos = pos.add(0, height, 0);
			BlockPos offset = Building.BuildingBlocks.facePos(new BlockPos(size, 0, 0), facing);
			pos = pos.add(offset.getX(), height, offset.getZ());
			this.setBlockAndNotifyAdequately(world, pos.up(), Blocks.AIR.getDefaultState());
			this.setBlockAndNotifyAdequately(world, pos.up(2), Blocks.AIR.getDefaultState());
			// 底板
			for (int x = 0; x < halfWidth * 2; x++) {
				for (int z = -halfWidth; z <= halfWidth; z++) {
					BlockPos blockPos = pos.add(Building.BuildingBlocks.facePos(new BlockPos(x, 0, z), facing));
					this.setBlockAndNotifyAdequately(world, blockPos, WorldGenElfTree.WOOD);
				}
			}
			// 墙
			for (int y = 1; y < halfWidth * 2; y++) {
				for (int x = 0; x < halfWidth * 2; x++) {
					BlockPos blockPos = pos
							.add(Building.BuildingBlocks.facePos(new BlockPos(x, y, halfWidth + 1), facing));
					this.setBlockAndNotifyAdequately(world, blockPos, WorldGenElfTree.WOOD);
					blockPos = pos.add(Building.BuildingBlocks.facePos(new BlockPos(x, y, -halfWidth - 1), facing));
					this.setBlockAndNotifyAdequately(world, blockPos, WorldGenElfTree.WOOD);
				}
			}
			// 叶子树顶
			for (int x = 1; x < halfWidth * 2; x++) {
				for (int z = -halfWidth - 1; z <= halfWidth + 1; z++) {
					BlockPos blockPos = pos
							.add(Building.BuildingBlocks.facePos(new BlockPos(x, halfWidth * 2, z), facing));
					this.setBlockAndNotifyAdequately(world, blockPos, WorldGenElfTree.LEAVE);
				}
			}
			for (int z = -halfWidth - 1; z <= halfWidth + 1; z++) {
				BlockPos blockPos = pos.add(Building.BuildingBlocks.facePos(new BlockPos(halfWidth * 2, 0, z), facing));
				this.setBlockAndNotifyAdequately(world, blockPos, WorldGenElfTree.LEAVE);
			}
			// 内部地板
			IBlockState ladder = Blocks.LADDER.getDefaultState().withProperty(BlockLadder.FACING,
					facing.rotateY().getOpposite());
			BlockPos lastCenterPos = this.lastCenterPos;
			this.lastCenterPos = centerPos;
			if (lastCenterPos != null && centerPos.getY() - lastCenterPos.getY() < 3) {
				BlockPos blockPos = pos.add(Building.BuildingBlocks.facePos(new BlockPos(-1, 0, 0), facing));
				this.setBlockAndNotifyAdequately(world, blockPos, ladder);
				if (world.isAirBlock(centerPos.down(1)))
					this.setBlockAndNotifyAdequately(world, blockPos.down(), ladder);
				// 只刷外部
				this.genCabinOuter(world, rand, centerPos, facing);
			} else {
				for (int x = -size + 1; x <= size - 1; x++) {
					for (int z = -size + 1; z <= size - 1; z++) {
						this.setBlockAndNotifyAdequately(world, centerPos.add(x, 0, z), WorldGenElfTree.WOOD);
					}
				}
				// 刷外部和内部
				this.genCabinOuter(world, rand, centerPos, facing);
				this.genCabinInner(world, rand, centerPos, facing);
			}
			return true;
		}

		// 检测小屋是否可以生成
		private boolean checkCabin(World world, BlockPos pos, int height, int halfWidth, EnumFacing facing) {
			BlockPos offset = Building.BuildingBlocks.facePos(new BlockPos(size, 0, 0), facing);
			pos = pos.add(offset.getX(), height, offset.getZ());
			for (int y = 0; y <= halfWidth * 2; y++)
				for (int x = 1; x <= halfWidth * 2; x++) for (int z = -halfWidth - 1; z <= halfWidth + 1; z++) {
					BlockPos blockPos = pos.add(Building.BuildingBlocks.facePos(new BlockPos(x, y, z), facing));
					if (!world.isAirBlock(blockPos)) return false;
				}
			return true;
		}

		// 生成多个树枝
		private void genBranchs(World world, Random rand, BlockPos pos, int min, int range, int branchCount,
				int facingStart) {
			int facing = facingStart;
			for (int i = 0; i < branchCount; i++) {
				BlockPos at;
				int height = min + rand.nextInt(range);
				int rsize = size - 1;
				rsize = rand.nextInt(rsize) - rsize / 2;
				switch (facing % 4) {
				case 0:
					at = pos.add(rsize, height, size);
					break;
				case 1:
					at = pos.add(rsize, height, -size);
					break;
				case 2:
					at = pos.add(size, height, rsize);
					break;
				case 3:
					at = pos.add(-size, height, rsize);
					break;
				default:
					at = pos.add(rsize, height, size);
				}
				facing++;
				genBranch(world, rand, pos, at, rand.nextInt(4) + 4);
			}
		}

		// 生成树枝
		private void genBranch(World world, Random rand, BlockPos pos, BlockPos at, int size) {
			int xoff = pos.getX() > at.getX() ? -1 : 1;
			int zoff = pos.getZ() > at.getZ() ? -1 : 1;
			boolean px = Math.abs(pos.getX() - at.getX()) > Math.abs(pos.getZ() - at.getZ());
			for (int i = 0; i < size; i++) {
				int yup = rand.nextInt(3 - size % 3) == 0 ? 1 : 0;
				boolean pup = rand.nextInt(5) == 0;
				if (px) {
					at = at.add(xoff, yup, pup ? zoff : 0);
				} else {
					at = at.add(pup ? xoff : 0, yup, zoff);
				}
				if (this.isReplaceable(world, at)) {
					this.setBlockAndNotifyAdequately(world, at, WorldGenElfTree.WOOD);
				}
			}
			int yoff = 0;
			for (int i = 0; i < 4; i++) {
				int xsize = size / 2 - i;
				int zsize = size / 2 - i;
				for (xoff = -xsize; xoff <= xsize; xoff++) {
					for (zoff = -zsize; zoff <= zsize; zoff++) {
						BlockPos blockpos = at.add(xoff, yoff, zoff);
						IBlockState state = world.getBlockState(blockpos);
						if (state.getBlock().isAir(state, world, blockpos)
								|| state.getBlock().isLeaves(state, world, blockpos)) {
							this.setBlockAndNotifyAdequately(world, blockpos, WorldGenElfTree.LEAVE);
						}
					}
				}
				yoff++;
			}
		}

		// 生成最上方叶子
		private void genLeavs(World world, Random rand, BlockPos pos) {
			int yoff = 0;
			for (int i = 0; i < 16; i++) {
				int xsize = size * 3 - i / 2;
				int zsize = size * 3 - i / 2;
				if (4 - i % 5 == 0) continue;
				for (int xoff = -xsize; xoff <= xsize; xoff++) {
					for (int zoff = -zsize; zoff <= zsize; zoff++) {
						IBlockState to = WorldGenElfTree.LEAVE;
						if (xoff > -xsize + 1 && xoff < xsize - 1 && zoff > -zsize + 1 && zoff < zsize - 1) {
							if (rand.nextInt(yoff % 4 + 1) == 0 && rand.nextInt(Math.abs(xoff * zoff) % 4 + 3) == 0)
								to = WorldGenElfTree.WOOD;
						}
						// 设置树叶或者木头
						BlockPos blockpos = pos.add(xoff, height + yoff, zoff);
						IBlockState state = world.getBlockState(blockpos);
						if (state.getBlock().isAir(state, world, blockpos)
								|| state.getBlock().isLeaves(state, world, blockpos)) {
							if (to == WorldGenElfTree.WOOD) {
								this.setBlockAndNotifyAdequately(world, blockpos, to);
								blockpos = blockpos.offset(EnumFacing.DOWN);
								if (world.isAirBlock(blockpos))
									this.setBlockAndNotifyAdequately(world, blockpos, WorldGenElfTree.LEAVE);
							} else this.setBlockAndNotifyAdequately(world, blockpos, to);
						}
					}
				}
				yoff++;
			}
		}

		// 生成一次根
		private void genRootOnce(World world, BlockPos pos, int xoff, int zoff, int rootHeight) {
			BlockPos rootPos = new BlockPos(pos.getX() + xoff, pos.getY(), pos.getZ() + zoff);
			xoff = pos.getX() > rootPos.getX() ? 1 : -1;
			zoff = pos.getZ() > rootPos.getZ() ? 1 : -1;
			for (int n = rootHeight; n >= 0; n--) {
				for (int y = pos.getY(); y <= pos.getY() + n; y++) {
					for (int i = 0; i < rootHeight; i++) {
						// 向下张
						if (y == pos.getY()) {
							int down = 1;
							BlockPos p;
							do {
								p = rootPos.down(down++);
								this.setBlockAndNotifyAdequately(world,
										new BlockPos(rootPos.getX() + xoff * i, p.getY(), rootPos.getZ()),
										WorldGenElfTree.WOOD);
								this.setBlockAndNotifyAdequately(world,
										new BlockPos(rootPos.getX(), p.getY(), rootPos.getZ() + zoff * i),
										WorldGenElfTree.WOOD);
							} while (world.getBlockState(p).getBlock().isReplaceable(world, p));
						}
						// 根
						this.setBlockAndNotifyAdequately(world,
								new BlockPos(rootPos.getX() + xoff * i, y, rootPos.getZ()), WorldGenElfTree.WOOD);
						this.setBlockAndNotifyAdequately(world,
								new BlockPos(rootPos.getX(), y, rootPos.getZ() + zoff * i), WorldGenElfTree.WOOD);
					}
				}
				rootPos = rootPos.add(-xoff, 0, -zoff);
			}
		}

		/** 将地表清空 */
		public void clearGround(World world, BlockPos pos) {
			final int rootHeight = size;
			final int rootWidth = rootHeight + 1;
			BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();
			for (int x = pos.getX() - size - rootWidth; x <= pos.getX() + size + rootWidth; x++) {
				for (int z = pos.getZ() - size - rootWidth; z <= pos.getZ() + size + rootWidth; z++) {
					IBlockState state = world.getBlockState(mPos.setPos(x, pos.getY(), z));
					if (state.getBlock() instanceof BlockBush || state.getBlock() == Blocks.STONE
							|| state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.DIRT) {
						this.setBlockAndNotifyAdequately(world, mPos, Blocks.AIR.getDefaultState());
					}
				}
			}

		}

		/** 是否可以生长 */
		protected boolean canGrowable(World world, BlockPos pos) {
			if (pos.getY() > 1 && pos.getY() + height + 1 < 256) {
				BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();
				final int rootHeight = size;
				final int rootWidth = rootHeight + 1;
				// 遍历树根部分
				for (int y = pos.getY(); y <= pos.getY() + rootHeight; y++) {
					for (int x = pos.getX() - size - rootWidth; x <= pos.getX() + size + rootWidth; x++) {
						for (int z = pos.getZ() - size - rootWidth; z <= pos.getZ() + size + rootWidth; z++) {
							if (!this.isReplaceable(world, mPos.setPos(x, y, z))) return false;
						}
					}
				}
				// 遍历树干部分
				for (int y = pos.getY(); y <= pos.getY() + height + rootHeight; y++) {
					for (int x = pos.getX() - size; x <= pos.getX() + size; x++) {
						for (int z = pos.getZ() - size; z <= pos.getZ() + size; z++) {
							if (!this.isReplaceable(world, mPos.setPos(x, y, z))) return false;
						}
					}
				}
			}
			return true;
		}

		private void onPlantGrow(World world, BlockPos pos, BlockPos source) {

		}

		private boolean ensureDirtsUnderneath(BlockPos pos, World worldIn) {
			BlockPos blockpos = pos.down();
			IBlockState state = worldIn.getBlockState(blockpos);
			boolean isSoil = state.getBlock().canSustainPlant(state, worldIn, blockpos, EnumFacing.UP,
					(IPlantable) Blocks.SAPLING);
			if (isSoil && pos.getY() >= 2) {
				BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();
				for (int x = pos.getX() - size; x <= pos.getX() + size; x++) {
					for (int z = pos.getZ() - size; z <= pos.getZ() + size; z++) {
						this.onPlantGrow(worldIn, mPos.setPos(x, blockpos.getY(), z), pos);
					}
				}
				return true;
			} else {
				return false;
			}
		}
	}

}
