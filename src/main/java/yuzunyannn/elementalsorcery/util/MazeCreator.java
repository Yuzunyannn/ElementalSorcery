package yuzunyannn.elementalsorcery.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.util.math.BlockPos;

public class MazeCreator {

	public static class Grid {
		public final int x, z;

		public Grid(int x, int z) {
			this.x = x;
			this.z = z;
		}

		@Override
		public boolean equals(Object arg) {
			if (this == arg) return true;
			if (arg instanceof Grid) {
				Grid other = (Grid) arg;
				return this.x == other.x && this.z == other.z;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return z * 31 * 31 + x;
		}

		@Override
		public String toString() {
			return "(" + x + "," + z + ")";
		}

		public BlockPos toBlockPos(int y) {
			return new BlockPos(x, y, z);
		}

		public BlockPos add(BlockPos pos) {
			return new BlockPos(x + pos.getX(), pos.getY(), z + pos.getZ());
		}
	}

	public static final int DIR_UP = 0;
	public static final int DIR_RIGHT = 1;
	public static final int DIR_DOWN = 2;
	public static final int DIR_LEFT = 3;

	public static final int[] xDelta = new int[] { 0, 1, 0, -1 };
	public static final int[] zDelta = new int[] { -1, 0, 1, 0 };

	public final int w, h;

	protected boolean[] walls;

	public MazeCreator(int w, int h) {
		this.w = w;
		this.h = h;

		int wallCount = (w - 1) * h + (h - 1) * w;
		walls = new boolean[wallCount];
	}

	public int getWallIndex(int x, int z, int dir) {
		boolean e = z == h - 1;
		switch (dir) {
		case DIR_UP:
			return (z - 1) * (w - 1 + w) + x * 2;
		case DIR_DOWN:
			return z * (w - 1 + w) + x * 2;
		case DIR_RIGHT:
			return z * (w - 1 + w) + (e ? x : x * 2 + 1);
		case DIR_LEFT:
			return z * (w - 1 + w) + +(e ? x - 1 : x * 2 - 1);
		default:
			return 0;
		}
	}

	public boolean hasWall(int x, int z, int dir) {
		int index = getWallIndex(x, z, dir);
		if (index < 0 || index >= walls.length) return true;
		return walls[index];
	}

	public void generate(Random rand) {
		for (int i = 0; i < walls.length; i++) walls[i] = true;

		List<Grid> grids = new ArrayList<>();
		Set<Grid> gridsInList = new HashSet<>();

		Map<Grid, Boolean> markMap = new HashMap<>();

		Grid startGrid = new Grid(rand.nextInt(w), rand.nextInt(h));
		grids.add(startGrid);

		while (!grids.isEmpty()) {
			int index = rand.nextInt(grids.size());
			Grid currGrid = grids.get(index);
			grids.remove(index);
			markMap.put(currGrid, true);

			List<Integer> markDirs = new ArrayList<>();
			for (int dir = DIR_UP; dir <= DIR_LEFT; dir++) {
				int x = currGrid.x + xDelta[dir];
				int z = currGrid.z + zDelta[dir];
				if (x < 0 || x >= w) continue;
				if (z < 0 || z >= h) continue;

				Grid subGrid = new Grid(x, z);
				if (markMap.containsKey(subGrid)) markDirs.add(dir);
				else {
					if (!gridsInList.contains(subGrid)) {
						grids.add(subGrid);
						gridsInList.add(subGrid);
					}
				}
			}

			if (markDirs.isEmpty()) continue;
			int dir = markDirs.get(rand.nextInt(markDirs.size()));
			walls[getWallIndex(currGrid.x, currGrid.z, dir)] = false;
		}
	}

	public List<Grid> buildWallGridList(int x, int z, int pathSize, int wallSize) {
		List<Grid> list = new ArrayList<>();

		boolean buildSub = true;
		if (x < w - 1) {
			if (hasWall(x, z, DIR_RIGHT)) {
				for (int xoff = 0; xoff < wallSize; xoff++)
					for (int zoff = 0; zoff < pathSize; zoff++) list.add(new Grid(pathSize + xoff, zoff));
			}
		} else buildSub = false;
		if (z < h - 1) {
			if (hasWall(x, z, DIR_DOWN)) {
				for (int xoff = 0; xoff < pathSize; xoff++)
					for (int zoff = 0; zoff < wallSize; zoff++) list.add(new Grid(xoff, pathSize + zoff));
			}
		} else buildSub = false;
		if (!buildSub) return list;
		for (int xoff = 0; xoff < wallSize; xoff++)
			for (int zoff = 0; zoff < wallSize; zoff++) list.add(new Grid(pathSize + xoff, pathSize + zoff));
		return list;
	}
}
