package yuzunyannn.elementalsorcery.summon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.block.BlockElfLeaf;
import yuzunyannn.elementalsorcery.entity.EntityBlockMove;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.MazeCreator;
import yuzunyannn.elementalsorcery.util.MazeCreator.Grid;
import yuzunyannn.elementalsorcery.util.NBTTag;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;

public class SummonMaze extends SummonCommon {

	public IBlockState defaultBuildBlockState = ESInit.BLOCKS.ELF_LEAF.getDefaultState()
			.withProperty(BlockElfLeaf.DECAYABLE, false);

	protected int mazeSize;
	protected int pathSize = 1;

	protected int floorRise = 0;

	protected ArrayList<MazeCreator.Grid> mazeGrids;
	protected int xWall, zWall;
	protected MazeCreator.Grid doorGrid;

	public SummonMaze(World world, BlockPos pos) {
		this(world, pos, Blocks.LEAVES.getDefaultState().getMaterial().getMaterialMapColor().colorValue, 16);
	}

	public SummonMaze(World world, BlockPos pos, int color, int mazeSize) {
		super(world, pos, color);
		this.mazeSize = mazeSize;
		this.initDoorGrid(world.rand);
		this.initMaze(world.rand);

		int gridSize = pathSize + 1;
		int maxSize = mazeSize * gridSize / 2;
		xWall = -maxSize;
		zWall = -maxSize;
	}

	protected MazeCreator.Grid getMazeWalkGrid(int x, int z) {
		int gridSize = pathSize + 1;
		int xoff = x * gridSize;
		int zoff = z * gridSize;
		return new MazeCreator.Grid(xoff, zoff);
	}

	protected MazeCreator.Grid getMazeWalkGrid(int x, int z, int dir) {
		int gridSize = pathSize + 1;
		int xoff = x * gridSize + MazeCreator.xDelta[dir];
		int zoff = z * gridSize + MazeCreator.zDelta[dir];
		return new MazeCreator.Grid(xoff, zoff);
	}

	protected void initDoorGrid(Random rand) {
		int dir = rand.nextInt(4);
		switch (dir) {
		case MazeCreator.DIR_UP:
			doorGrid = getMazeWalkGrid(rand.nextInt(mazeSize), 0, MazeCreator.DIR_UP);
			break;
		case MazeCreator.DIR_DOWN:
			doorGrid = getMazeWalkGrid(rand.nextInt(mazeSize), mazeSize - 1, MazeCreator.DIR_DOWN);
			break;
		case MazeCreator.DIR_LEFT:
			doorGrid = getMazeWalkGrid(0, rand.nextInt(mazeSize), MazeCreator.DIR_LEFT);
			break;
		case MazeCreator.DIR_RIGHT:
			doorGrid = getMazeWalkGrid(mazeSize - 1, rand.nextInt(mazeSize), MazeCreator.DIR_RIGHT);
			break;
		}
	}

	protected void initMaze(Random rand) {
		MazeCreator maze = new MazeCreator(mazeSize, mazeSize);
		maze.generate(rand);
		int gridSize = pathSize + 1;
		maze.generate(RandomHelper.rand);
		this.mazeGrids = new ArrayList<>();
		for (int i = 0; i < maze.w; i++) {
			for (int j = 0; j < maze.h; j++) {
				List<MazeCreator.Grid> list = maze.buildWallGridList(i, j, pathSize, 1);
				int xoff = i * gridSize;
				int zoff = j * gridSize;
				for (Grid g : list) mazeGrids.add(new MazeCreator.Grid(g.x + xoff, g.z + zoff));
			}
		}
		Collections.shuffle(mazeGrids);
	}

	@Override
	public void initData() {
		this.size = (int) (16 * 1.4143f);
		this.height = 10;
	}

	@Override
	public boolean update() {
		if (world.isRemote) return true;
		tick++;
		if (ElementalSorcery.isDevelop) {
			for (int i = 0; i < 12; i++) updateBuild();
		}
		return updateBuild() ? updateBuild() : false;
	}

	public boolean updateBuild() {
		if (updateBuildAroundWall()) {
			updateBuildAroundWall();
			return true;
		}

		boolean needRunning = updateBuildAfterAround();
		if (mazeGrids.isEmpty()) return needRunning || updateBuildAfter();

		MazeCreator.Grid grid = mazeGrids.get(mazeGrids.size() - 1);
		mazeGrids.remove(mazeGrids.size() - 1);

		buildWall(grid);
		return true;
	}

	public boolean updateBuildAfter() {
		return false;
	}

	public boolean updateBuildAfterAround() {
		return false;
	}

	public boolean updateBuildAroundWall() {
		int gridSize = pathSize + 1;
		int maxSize = mazeSize * gridSize / 2;
		if (xWall <= maxSize) {
			MazeCreator.Grid grid = new MazeCreator.Grid(xWall + maxSize - 1, zWall + maxSize - 1);
			buildFloor(grid);
			if (Math.abs(xWall) != maxSize && Math.abs(zWall) != maxSize) {
				xWall++;
				return true;
			}
			buildWall(grid);
			xWall++;
		} else {
			if (zWall < maxSize) {
				zWall++;
				xWall = -maxSize;
			} else return false;
		}
		return true;
	}

	public void buildFloor(MazeCreator.Grid grid) {
		BlockPos at = grid.add(pos).add(-mazeSize + 1, floorRise, -mazeSize + 1);
		genMazeBlock(at, 1, defaultBuildBlockState);
	}

	public void buildWall(MazeCreator.Grid grid) {
		if (grid.equals(doorGrid)) return;
		BlockPos at = grid.add(pos).add(-mazeSize + 1, floorRise + 1, -mazeSize + 1);
		genMazeBlock(at, 1, defaultBuildBlockState);
		genMazeBlock(at.up(), 2, defaultBuildBlockState);
	}

	protected void genMazeBlock(BlockPos pos, int startOff, IBlockState state) {
		Vec3d from = new Vec3d(0.5 + pos.getX(), this.pos.getY() + height - startOff, 0.5 + pos.getZ());
		EntityBlockMove entity = new EntityBlockMove(world, from, pos, state);
		entity.setFlag(EntityBlockMove.FLAG_FORCE_DESTRUCT, true);
		entity.setFlag(EntityBlockMove.FLAG_INTO_CHEST, false);
		entity.setFlag(EntityBlockMove.FLAG_DESTRUCT_DROP, true);
		world.spawnEntity(entity);
		pos = pos.up();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setByte("size", (byte) mazeSize);
		nbt.setInteger("xw", xWall);
		nbt.setInteger("zw", zWall);
		nbt.setInteger("xd", doorGrid.x);
		nbt.setInteger("zd", doorGrid.z);
		if (mazeGrids != null) {
			int[] ints = new int[mazeGrids.size() * 2];
			int i = 0;
			for (MazeCreator.Grid grid : mazeGrids) {
				ints[i++] = grid.x;
				ints[i++] = grid.z;
			}
			nbt.setIntArray("mazeGrids", ints);
		}
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		mazeSize = nbt.getInteger("size");
		xWall = nbt.getInteger("xw");
		zWall = nbt.getInteger("zw");
		doorGrid = new MazeCreator.Grid(nbt.getInteger("xd"), nbt.getInteger("zd"));
		if (nbt.hasKey("mazeGrids", NBTTag.TAG_INT_ARRAY)) {
			int[] ints = nbt.getIntArray("mazeGrids");
			mazeGrids = new ArrayList<>();
			for (int i = 0; i < ints.length; i += 2) mazeGrids.add(new MazeCreator.Grid(ints[i], ints[i + 1]));
		}
	}

}
