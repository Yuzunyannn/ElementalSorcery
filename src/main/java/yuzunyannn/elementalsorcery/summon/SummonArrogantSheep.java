package yuzunyannn.elementalsorcery.summon;

import java.util.Random;

import net.minecraft.block.BlockColored;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.block.BlockGoatGoldBrick;
import yuzunyannn.elementalsorcery.building.BuildingBlocks;
import yuzunyannn.elementalsorcery.entity.mob.EntityArrogantSheep;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.MazeCreator;
import yuzunyannn.elementalsorcery.util.MazeCreator.Grid;

public class SummonArrogantSheep extends SummonMaze {

	protected int xRoof, zRoof;
	protected int roofSize;
	protected MazeCreator.Grid elevatorGrid;
	protected int goatStatue;

	protected boolean protectFinish;
	protected int moveBrickTimes;

	public SummonArrogantSheep(World world, BlockPos pos) {
		super(world, pos, 0xffc000, 16);
		int gridSize = pathSize + 1;
		int maxSize = mazeSize * gridSize / 2;
		roofSize = maxSize + 1;
		xRoof = -roofSize;
		zRoof = -roofSize;
		defaultBuildBlockState = ESInit.BLOCKS.GOAT_GOLD_BRICK.getDefaultState();
		this.initElevatorGrid(world.rand);
	}

	@Override
	public void initData() {
		super.initData();
		this.height = 14;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("xr", xRoof);
		nbt.setInteger("zr", zRoof);
		nbt.setInteger("rs", roofSize);
		nbt.setInteger("xe", doorGrid.x);
		nbt.setInteger("ze", doorGrid.z);
		nbt.setInteger("gs", goatStatue);
		return super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		xRoof = nbt.getInteger("xr");
		zRoof = nbt.getInteger("zr");
		roofSize = nbt.getInteger("rs");
		goatStatue = nbt.getInteger("gs");
		elevatorGrid = new MazeCreator.Grid(nbt.getInteger("xe"), nbt.getInteger("ze"));
		super.readFromNBT(nbt);
	}

	protected void initElevatorGrid(Random rand) {
		float maxDistance = -1;
		int gridSize = pathSize + 1;
		int halfSize = mazeSize / 2;
		for (int i = 0; i < 16; i++) {
			float theta = rand.nextFloat() * 3.1415926f * 2f;
			MazeCreator.Grid randGrid = new MazeCreator.Grid(
					(int) (halfSize + MathHelper.sin(theta) * (rand.nextInt(halfSize - 4) + 3)),
					(int) (halfSize + MathHelper.cos(theta) * (rand.nextInt(halfSize - 4) + 3)));
			float dx = randGrid.x - doorGrid.x / gridSize;
			float dz = randGrid.z - doorGrid.z / gridSize;
			float dis = dx * dx + dz * dz;
			if (dis > maxDistance) {
				maxDistance = dis;
				elevatorGrid = new MazeCreator.Grid(randGrid.x * gridSize, randGrid.z * gridSize);
			}
		}
	}

	@Override
	public boolean updateBuild() {
		EntityPlayer player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 34, false);
		if (player != null) return true;
		return super.updateBuild();
	}

	@Override
	public boolean updateBuildAfter() {

		if (goatStatue >= GOAT_STATUE.length) {
			if (!protectFinish) {
				protectFinish = true;
				onFinish();
			}
			return false;
		}

		EnumFacing facing = EnumFacing.NORTH;

		int gridSize = pathSize + 1;
		if (doorGrid.x < 0) facing = EnumFacing.SOUTH;
		else if (doorGrid.x >= mazeSize * gridSize - 1) facing = EnumFacing.NORTH;
		else if (doorGrid.z < 0) facing = EnumFacing.WEST;
		else if (doorGrid.z >= mazeSize * gridSize - 1) facing = EnumFacing.EAST;

		int i = goatStatue;
		goatStatue = goatStatue + 3;
		BlockPos pos = new BlockPos(GOAT_STATUE[i], GOAT_STATUE[i + 1], GOAT_STATUE[i + 2]);
		pos = BuildingBlocks.facePos(pos, facing);
		BlockPos at = pos.add(this.pos).add(0, floorRise + 14, 0);

		int eyeIndex = (GOAT_STATUE[i] == 8 && GOAT_STATUE[i + 1] == 9) ? 1 : -1;
		if (eyeIndex == 1) {
			int n = Math.abs(GOAT_STATUE[i + 2]);
			if (n == 2 || n == 1) eyeIndex = n;
			else eyeIndex = -1;
		}
		if (eyeIndex > 0) {
			IBlockState state = Blocks.WOOL.getDefaultState();
			if (eyeIndex == 2) state = state.withProperty(BlockColored.COLOR, EnumDyeColor.BLACK);
			genMazeBlock(at, 1, state);
		} else buildRoof(at, true);

		return true;
	}

	@Override
	public boolean updateBuildAfterAround() {
		if (mazeGrids.size() > 128) return true;
		if (updateBuildRoof()) return updateBuildRoof();
		return false;
	}

	public boolean updateBuildRoof() {
		int gridSize = pathSize + 1;
		int maxSize = mazeSize * gridSize / 2;
		int roofHigh = maxSize + 1 - roofSize;

		boolean notGlow = false;
		boolean mustGlow = false;
		boolean isFullLayer = roofHigh == 1 || roofHigh == 10;
		if (roofHigh > 1 && roofHigh <= 6) {
			if (roofHigh == 2) {
				notGlow = (Math.abs(xRoof) <= 2 && Math.abs(zRoof) <= 2);
				mustGlow = true; 
			} else notGlow = (Math.abs(xRoof) <= 1 && Math.abs(zRoof) <= 1);
			isFullLayer = isFullLayer || notGlow;
		}
		if (xRoof <= roofSize) {
			BlockPos pos = this.pos.add(xRoof, roofHigh + floorRise + 3, zRoof);
			if (Math.abs(xRoof) != roofSize && Math.abs(zRoof) != roofSize) {
				xRoof++;
				if (isFullLayer) {
					if (roofHigh == 1) {
						int gx = xRoof - 1 + maxSize - 1;
						int gz = zRoof + maxSize - 1;
						if (elevatorGrid.x == gx && elevatorGrid.z == gz) return true;
					}
					if (mustGlow) genMazeBlock(pos, 1, defaultBuildBlockState.withProperty(BlockGoatGoldBrick.VARIANT,
							BlockGoatGoldBrick.EnumType.GLOW));
					else buildRoof(pos, !notGlow);
					return true;
				} else if (moveBrickTimes > 0 && world.rand.nextFloat() < 0.01) {
					moveBrickTimes--;
					buildMoveBrick(pos);
					return true;
				}
				return updateBuildRoof();
			}
			buildRoof(pos, !notGlow);
			xRoof++;
		} else {
			if (zRoof < roofSize) {
				zRoof++;
				xRoof = -roofSize;
			} else {
				if (roofHigh >= 10) return false;
				roofSize = roofSize - 1;
				xRoof = -roofSize;
				zRoof = -roofSize;
				if (roofHigh >= 2) moveBrickTimes = (24 - roofHigh * 4);
			}
		}
		return true;
	}

	public void onFinish() {
		if (world.isRemote) return;
		IBlockState state = defaultBuildBlockState;
		IBlockState stateJump = state.withProperty(BlockGoatGoldBrick.VARIANT, BlockGoatGoldBrick.EnumType.JUMP);
		{
			BlockPos at = elevatorGrid.add(this.pos).add(-mazeSize + 1, floorRise, -mazeSize + 1);
			world.setBlockState(at.up(0), stateJump);
		}
		{
			EntityArrogantSheep sheep = new EntityArrogantSheep(world, this.pos.add(0, 10, 0));
			world.spawnEntity(sheep);
		}
	}

	@Override
	public void buildFloor(Grid grid) {
		BlockPos at = grid.add(pos).add(-mazeSize + 1, floorRise, -mazeSize + 1);
		genMazeBlock(at, 1, defaultBuildBlockState);
	}

	@Override
	public void buildWall(Grid grid) {
		if (grid.equals(doorGrid)) return;
		if (grid.equals(elevatorGrid)) return;
		BlockPos at = grid.add(pos).add(-mazeSize + 1, floorRise + 1, -mazeSize + 1);
		genMazeBlock(at, 1, defaultBuildBlockState);
		genMazeBlock(at.up(), 2, defaultBuildBlockState);
	}

	public void buildRoof(BlockPos at, boolean needGlow) {
		if (needGlow) {
			boolean checkA = (at.getX() % 9 + at.getZ() % 9) % 5 == 0;
			boolean checkB = (at.getX() % 9 - at.getZ() % 9) % 5 == 0;
			if (checkA || checkB) {
				genMazeBlock(at, 1, defaultBuildBlockState.withProperty(BlockGoatGoldBrick.VARIANT,
						BlockGoatGoldBrick.EnumType.GLOW));
				return;
			}
		}
		genMazeBlock(at, 1, defaultBuildBlockState);
	}

	public void buildMoveBrick(BlockPos at) {
		IBlockState state = defaultBuildBlockState.withProperty(BlockGoatGoldBrick.VARIANT,
				BlockGoatGoldBrick.EnumType.MOVE);
		genMazeBlock(at, 1, state);
	}

	static public final int[] GOAT_STATUE = new int[] { 3, 6, 3, 4, 8, 4, 4, 6, 3, 5, 8, 4, 3, 2, 1, 5, 6, 3, 6, 8, 4,
			4, 2, 1, 6, 6, 3, 7, 8, 4, 6, 4, 2, 8, 6, 3, -8, 7, 3, -8, 5, 2, -8, 3, 1, -7, 3, 1, -4, 3, 1, -3, 3, 1, -2,
			3, 1, -1, 3, 1, 1, 7, 3, 2, 9, 4, 0, 3, 1, 3, 9, 4, 1, 3, 1, 4, 9, 4, 2, 3, 1, 5, 9, 4, 6, 9, 4, 7, 9, 4, 5,
			3, 1, 6, 5, 2, 6, 3, 1, 7, 5, 2, 8, 7, 3, -8, 6, 2, -8, 4, 1, -7, 6, 2, -6, 6, 2, -5, 6, 2, -4, 6, 2, -3, 6,
			2, -2, 6, 2, -1, 6, 2, 0, 6, 2, 1, 8, 3, 2, 10, 4, 1, 6, 2, 3, 10, 4, 2, 6, 2, 4, 10, 4, 3, 6, 2, 5, 10, 4,
			4, 6, 2, 6, 10, 4, 5, 6, 2, 7, 10, 4, 6, 6, 2, 6, 4, 1, 8, 8, 3, 8, 6, 2, -8, 7, 2, -8, 5, 1, -8, 3, 0, -7,
			3, 0, -6, 3, 0, -5, 3, 0, -4, 3, 0, -3, 3, 0, -2, 3, 0, 1, 9, 3, 2, 11, 4, -1, 3, 0, 1, 7, 2, 3, 11, 4, 0,
			3, 0, 4, 11, 4, 1, 3, 0, 5, 11, 4, 2, 3, 0, 6, 11, 4, 3, 3, 0, 7, 11, 4, 4, 3, 0, 5, 3, 0, 6, 5, 1, 8, 9, 3,
			6, 3, 0, 7, 5, 1, 8, 7, 2, -8, 6, 1, -8, 4, 0, -7, 6, 1, -6, 6, 1, -5, 6, 1, -4, 6, 1, -3, 6, 1, -2, 6, 1,
			-1, 6, 1, 1, 10, 3, 0, 6, 1, 1, 8, 2, 1, 6, 1, 2, 6, 1, 3, 6, 1, 4, 6, 1, 5, 6, 1, 6, 6, 1, 8, 10, 3, 6, 4,
			0, 8, 8, 2, 8, 6, 1, -8, 7, 1, -8, 5, 0, 1, 11, 3, 1, 9, 2, 1, 7, 1, 8, 11, 3, 6, 5, 0, 8, 9, 2, 7, 5, 0, 8,
			7, 1, -8, 6, 0, -7, 6, 0, -6, 6, 0, -5, 6, 0, -4, 6, 0, -3, 6, 0, -2, 6, 0, -1, 6, 0, 1, 10, 2, 2, 12, 3, 0,
			6, 0, 1, 8, 1, 3, 12, 3, 1, 6, 0, 4, 12, 3, 2, 6, 0, 5, 12, 3, 3, 6, 0, 6, 12, 3, 4, 6, 0, 7, 12, 3, 5, 6,
			0, 6, 6, 0, 8, 10, 2, 8, 8, 1, 8, 6, 0, -8, 7, 0, 1, 11, 2, 1, 9, 1, 1, 7, 0, 8, 11, 2, 8, 9, 1, 8, 7, 0, 1,
			10, 1, 2, 12, 2, 1, 8, 0, 3, 12, 2, 4, 12, 2, 5, 12, 2, 6, 12, 2, 7, 12, 2, 8, 10, 1, 8, 8, 0, 1, 11, 1, 1,
			9, 0, 8, 11, 1, 8, 9, 0, 1, 10, 0, 2, 12, 1, 3, 12, 1, 4, 12, 1, 5, 12, 1, 6, 12, 1, 7, 12, 1, 8, 10, 0, 1,
			11, 0, 8, 11, 0, 2, 12, 0, 3, 12, 0, 4, 12, 0, 5, 12, 0, 6, 12, 0, 7, 12, 0, 7, 11, -4, 6, 11, -4, 5, 11,
			-4, 4, 11, -4, 3, 11, -4, 2, 11, -4, 6, 8, -5, 7, 10, -4, 5, 8, -5, 6, 10, -4, 7, 12, -3, 4, 8, -5, 5, 10,
			-4, 6, 12, -3, 3, 8, -5, 4, 10, -4, 5, 12, -3, 2, 8, -5, 3, 10, -4, 4, 12, -3, 1, 8, -5, 2, 10, -4, 3, 12,
			-3, 0, 8, -5, 2, 12, -3, -1, 8, -5, -2, 8, -5, -3, 8, -5, -4, 8, -5, -5, 8, -5, 6, 7, -5, 7, 9, -4, 8, 11,
			-3, 5, 7, -5, 6, 9, -4, 4, 7, -5, 5, 9, -4, 3, 7, -5, 4, 9, -4, 2, 7, -5, 3, 9, -4, 1, 7, -5, 2, 9, -4, 0,
			7, -5, -1, 7, -5, 1, 11, -3, -2, 7, -5, -3, 7, -5, -4, 7, -5, -5, 7, -5, -6, 7, -5, -7, 7, -5, -8, 7, -5, 6,
			6, -5, 7, 8, -4, 8, 10, -3, 5, 6, -5, 6, 8, -4, 4, 6, -5, 5, 8, -4, 7, 12, -2, 3, 6, -5, 4, 8, -4, 6, 12,
			-2, 2, 6, -5, 3, 8, -4, 5, 12, -2, 1, 6, -5, 2, 8, -4, 4, 12, -2, 0, 6, -5, 3, 12, -2, -1, 6, -5, 1, 10, -3,
			2, 12, -2, -2, 6, -5, -3, 6, -5, -4, 6, -5, -5, 6, -5, -6, 6, -5, -7, 6, -5, -8, 6, -5, 6, 5, -5, 7, 7, -4,
			8, 9, -3, 5, 5, -5, 6, 7, -4, 8, 11, -2, 4, 5, -5, 5, 7, -4, 3, 5, -5, 4, 7, -4, 2, 5, -5, 3, 7, -4, 1, 5,
			-5, 2, 7, -4, 0, 5, -5, -1, 5, -5, 1, 9, -3, -2, 5, -5, 1, 11, -2, -3, 5, -5, -4, 5, -5, -5, 5, -5, -6, 5,
			-5, -7, 5, -5, -8, 5, -5, -8, 7, -4, 6, 4, -5, 7, 6, -4, 8, 8, -3, 5, 4, -5, 6, 6, -4, 8, 10, -2, 4, 4, -5,
			5, 6, -4, 3, 4, -5, 4, 6, -4, 7, 12, -1, 2, 4, -5, 3, 6, -4, 6, 12, -1, 1, 4, -5, 2, 6, -4, 5, 12, -1, 0, 4,
			-5, 1, 6, -4, 4, 12, -1, -1, 4, -5, 0, 6, -4, 1, 8, -3, 3, 12, -1, -2, 4, -5, -1, 6, -4, 1, 10, -2, 2, 12,
			-1, -3, 4, -5, -2, 6, -4, -4, 4, -5, -3, 6, -4, -5, 4, -5, -4, 6, -4, -6, 4, -5, -5, 6, -4, -7, 4, -5, -6,
			6, -4, -8, 4, -5, -7, 6, -4, -8, 6, -4, 6, 3, -5, 7, 5, -4, 8, 7, -3, 5, 3, -5, 6, 5, -4, 8, 9, -2, 4, 3,
			-5, 8, 11, -1, 3, 3, -5, 2, 3, -5, 1, 3, -5, 0, 3, -5, -1, 3, -5, 1, 7, -3, -2, 3, -5, 1, 9, -2, -3, 3, -5,
			1, 11, -1, -4, 3, -5, -5, 3, -5, -6, 3, -5, -7, 3, -5, -8, 3, -5, -8, 5, -4, -8, 7, -3, 8, 6, -3, 6, 4, -4,
			8, 8, -2, 6, 6, -3, 8, 10, -1, 5, 6, -3, 4, 6, -3, 3, 6, -3, 2, 6, -3, 1, 6, -3, 0, 6, -3, 1, 8, -2, -1, 6,
			-3, 1, 10, -1, -2, 6, -3, -3, 6, -3, -4, 6, -3, -5, 6, -3, -6, 6, -3, -8, 4, -4, -7, 6, -3, -8, 6, -3, 6, 3,
			-4, 7, 5, -3, 8, 7, -2, 5, 3, -4, 6, 5, -3, 8, 9, -1, 2, 3, -4, 1, 3, -4, 0, 3, -4, -1, 3, -4, 1, 7, -2, -2,
			3, -4, 1, 9, -1, -3, 3, -4, -4, 3, -4, -7, 3, -4, -8, 3, -4, -8, 5, -3, -8, 7, -2, 8, 6, -2, 6, 4, -3, 8, 8,
			-1, 4, 2, -4, 6, 6, -2, 3, 2, -4, 5, 6, -2, 4, 6, -2, 3, 6, -2, 2, 6, -2, 1, 6, -2, 0, 6, -2, 1, 8, -1, -1,
			6, -2, -2, 6, -2, -5, 2, -4, -3, 6, -2, -6, 2, -4, -4, 6, -2, -5, 6, -2, -6, 6, -2, -8, 4, -3, -7, 6, -2,
			-8, 6, -2, 6, 3, -3, 7, 5, -2, 8, 7, -1, 4, 1, -4, 6, 5, -2, 3, 1, -4, 1, 3, -3, 0, 3, -3, -1, 3, -3, 1, 7,
			-1, -2, 3, -3, -3, 3, -3, -5, 1, -4, -6, 1, -4, -8, 3, -3, -8, 5, -2, -8, 7, -1, 8, 6, -1, 5, 2, -3, 6, 4,
			-2, 6, 6, -1, 5, 6, -1, 2, 2, -3, 4, 6, -1, 3, 6, -1, 2, 6, -1, 1, 6, -1, 0, 6, -1, -1, 6, -1, -4, 2, -3,
			-2, 6, -1, -3, 6, -1, -4, 6, -1, -7, 2, -3, -5, 6, -1, -6, 6, -1, -8, 4, -2, -7, 6, -1, -8, 6, -1, -8, 3, 5,
			-7, 3, 5, -6, 3, 5, -6, 1, 4, -5, 3, 5, 5, 1, -3, -5, 1, 4, 6, 3, -2, -4, 3, 5, 7, 5, -1, 4, 1, -3, -3, 3,
			5, 6, 5, -1, 3, 1, -3, -2, 3, 5, 2, 1, -3, -1, 3, 5, 0, 3, 5, 1, 3, 5, 1, 3, -2, 2, 3, 5, 0, 3, -2, 3, 3, 5,
			-1, 3, -2, 3, 1, 4, 4, 3, 5, -2, 3, -2, 4, 1, 4, -4, 1, -3, 5, 3, 5, -3, 3, -2, -5, 1, -3, 6, 3, 5, -6, 1,
			-3, -7, 1, -3, -8, 3, -2, -8, 5, -1, -8, 4, 5, -7, 4, 5, -6, 4, 5, -6, 2, 4, -5, 4, 5, -6, 0, 3, -5, 2, 4,
			-4, 4, 5, 4, 0, -3, -5, 0, 3, 5, 2, -2, 6, 4, -1, -3, 4, 5, 3, 0, -3, -2, 4, 5, -1, 4, 5, 2, 2, -2, 0, 4, 5,
			1, 4, 5, 2, 4, 5, 3, 4, 5, 3, 2, 4, 4, 4, 5, 3, 0, 3, 4, 2, 4, 5, 4, 5, 4, 0, 3, -5, 0, -3, -4, 2, -2, 6, 4,
			5, -6, 0, -3, -7, 2, -2, -8, 4, -1, -8, 5, 5, -8, 3, 4, -7, 5, 5, -7, 3, 4, -6, 5, 5, -7, 1, 3, -5, 5, 5,
			-6, 1, 3, -4, 5, 5, 5, 1, -2, -5, 1, 3, 6, 3, -1, -4, 3, 4, -3, 5, 5, 4, 1, -2, -4, 1, 3, 5, 3, -1, -3, 3,
			4, -2, 5, 5, 3, 1, -2, -2, 3, 4, -1, 5, 5, 2, 1, -2, -1, 3, 4, 0, 5, 5, 2, 3, -1, 0, 3, 4, 1, 5, 5, 1, 3, 4,
			1, 3, -1, 2, 5, 5, 2, 3, 4, 0, 3, -1, 3, 5, 5, 2, 1, 3, -1, 3, -1, 4, 5, 5, 3, 1, 3, -2, 3, -1, 5, 5, 5, 4,
			1, 3, -4, 1, -2, 5, 3, 4, -3, 3, -1, 6, 5, 5, 5, 1, 3, -5, 1, -2, 6, 3, 4, -4, 3, -1, -6, 1, -2, -7, 1, -2,
			-7, 3, -1, -8, 3, -1, -8, 6, 5, -8, 4, 4, -7, 6, 5, -6, 6, 5, -7, 2, 3, -5, 6, 5, -6, 2, 3, -4, 6, 5, -6, 0,
			2, -5, 2, 3, -3, 6, 5, 4, 0, -2, -5, 0, 2, -4, 2, 3, -2, 6, 5, 3, 0, -2, 4, 2, -1, -1, 6, 5, 3, 2, -1, 0, 6,
			5, 1, 6, 5, 2, 6, 5, 3, 6, 5, 2, 2, 3, 4, 6, 5, 5, 6, 5, 3, 0, 2, 6, 6, 5, 4, 0, 2, -5, 0, -2, 5, 2, 3, 6,
			4, 4, -6, 0, -2, -5, 2, -1, -6, 2, -1, -8, 7, 5, -8, 5, 4, -7, 7, 5, -8, 3, 3, -6, 7, 5, -5, 7, 5, -7, 1, 2,
			-4, 7, 5, -6, 1, 2, -3, 7, 5, -5, 1, 2, -2, 7, 5, 4, 1, -1, -4, 1, 2, -3, 3, 3, -1, 7, 5, 3, 1, -1, -2, 3,
			3, 0, 7, 5, -1, 3, 3, 1, 7, 5, 0, 3, 3, 2, 7, 5, 1, 3, 3, 3, 7, 5, 4, 7, 5, 2, 1, 2, 5, 7, 5, 3, 1, 2, 6, 7,
			5, 4, 1, 2, 6, 5, 4, 5, 1, 2, -5, 1, -1, 6, 3, 3, 7, 5, 4, -6, 1, -1, -8, 6, 4, -8, 4, 3, -7, 6, 4, -6, 6,
			4, -5, 8, 5, -7, 2, 2, -5, 6, 4, -4, 8, 5, -6, 2, 2, -4, 6, 4, -3, 8, 5, -5, 2, 2, -3, 6, 4, -2, 8, 5, -4,
			2, 2, -2, 6, 4, -1, 8, 5, -1, 6, 4, 0, 8, 5, 0, 6, 4, 1, 8, 5, 1, 6, 4, 2, 8, 5, 2, 6, 4, 3, 8, 5, 3, 6, 4,
			4, 8, 5, 2, 2, 2, 4, 6, 4, 5, 8, 5, 5, 6, 4, 6, 8, 5, 6, 6, 4, 5, 2, 2, 6, 4, 3, 7, 6, 4, -8, 7, 4, -8, 5,
			3, -8, 3, 2, -6, 1, 1, -5, 1, 1, -3, 3, 2, -2, 3, 2, -1, 3, 2, 0, 3, 2, 2, 7, 4, 1, 3, 2, 3, 7, 4, 4, 7, 4,
			5, 7, 4, 3, 1, 1, 6, 7, 4, 4, 1, 1, 6, 5, 3, 7, 7, 4, 6, 3, 2, 7, 5, 3, -8, 6, 3, -8, 4, 2, -7, 6, 3, -6, 6,
			3, -5, 6, 3, -6, 2, 1, -4, 6, 3, -5, 2, 1, -3, 6, 3, -2, 6, 3, -1, 6, 3, 0, 6, 3, 1, 6, 3, 2, 8, 4, 2, 6, 3,
			3, 8, 4 };

}
