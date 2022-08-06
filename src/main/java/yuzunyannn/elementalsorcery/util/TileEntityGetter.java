package yuzunyannn.elementalsorcery.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;

public class TileEntityGetter {

	protected AxisAlignedBB checkBox;
	protected Function<TileEntity, Boolean> checker;
	protected List<BlockPos> posList = new ArrayList<>();

	public TileEntityGetter() {
	}

	public TileEntityGetter setChecker(Function<TileEntity, Boolean> checker) {
		this.checker = checker;
		return this;
	}

	public TileEntityGetter setBox(AxisAlignedBB box) {
		this.checkBox = box;
		return this;
	}

	public void doCheck(World world) {
		if (checkBox == null) return;

		ChunkPos pos1 = new ChunkPos((int) checkBox.minX >> 4, (int) checkBox.minZ >> 4);
		ChunkPos pos2 = new ChunkPos((int) checkBox.maxX >> 4, (int) checkBox.maxZ >> 4);

		posList.clear();
		for (int x = pos1.x; x <= pos2.x; x++) {
			for (int z = pos1.z; z <= pos2.z; z++) {
				Chunk chunk = world.getChunk(x, z);
				checkTileToList(chunk);
			}
		}
	}

	protected void checkTileToList(Chunk chunk) {
		if (chunk == null || !chunk.isLoaded()) return;
		Map<BlockPos, TileEntity> tiles = chunk.getTileEntityMap();
		for (TileEntity tile : tiles.values()) {
			if (checker != null && !checker.apply(tile)) continue;
			BlockPos pos = tile.getPos();
			if (checkBox.contains(new Vec3d(pos).add(0.5, 0.5, 0.5))) posList.add(pos);
		}
	}

	public boolean isEmpty() {
		return posList.isEmpty();
	}

	public List<BlockPos> getPosList() {
		return posList;
	}

	public TileEntity checkAndGetTileWithPos(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile == null) {
			posList.remove(pos);
			return null;
		}
		if (checker != null && !checker.apply(tile)) {
			posList.remove(pos);
			return null;
		}
		return tile;
	}

	public TileEntity checkAndGetTileRandom(World world) {
		while (!posList.isEmpty()) {
			BlockPos pos = posList.get(world.rand.nextInt(posList.size()));
			TileEntity tile = checkAndGetTileWithPos(world, pos);
			if (tile == null) continue;
			return tile;
		}
		return null;
	}

	public TileEntity checkAndGetTileWithCondition(World world, int startIndex,
			Function<TileEntity, Boolean> condition) {
		if (posList.isEmpty()) return null;
		for (int i = 0; i < posList.size(); i++) {
			int index = (i + startIndex) % posList.size();
			TileEntity tile = checkAndGetTileWithPos(world, posList.get(index));
			if (tile == null) {
				i--;
				continue;
			}
			if (condition.apply(tile)) return tile;
		}
		return null;
	}

	public TileEntity checkAndGetTileCanInsertElement(World world, int startIndex, ElementStack estack) {
		return checkAndGetTileWithCondition(world, startIndex, tile -> {
			IElementInventory eInv = ElementHelper.getElementInventory(tile);
			if (eInv == null) return false;
			return eInv.insertElement(estack, true);
		});
	}

}
