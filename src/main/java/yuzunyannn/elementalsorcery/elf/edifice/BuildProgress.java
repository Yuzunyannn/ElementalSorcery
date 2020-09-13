
package yuzunyannn.elementalsorcery.elf.edifice;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.building.Building.BuildingBlocks;

public class BuildProgress {

	final IBuilder builder;
	final ElfEdificeFloor floorType;

	private Map<BlockPos, IBlockState> building;
	private Iterator<Entry<BlockPos, IBlockState>> iter;
	private List<Map.Entry<BlockPos, IBlockState>> after;

	int people = 0;

	public BuildProgress(ElfEdificeFloor floorType, IBuilder builder) {
		this.floorType = floorType;
		this.builder = builder;
		floorType.build(builder);
		this.init();
	}

	private void init() {
		building = builder.asBlockMap();
		World world = builder.getWorld();
		BlockPos pos = builder.getFloorBasicPos();
		int treeSize = builder.getEdificeSize();
		int high = builder.getFloorHigh();
		IBlockState elfLog = ESObjects.BLOCKS.ELF_LOG.getDefaultState();
		for (int y = 0; y <= high; y++) {
			for (int i = -treeSize + 1; i < treeSize; i++) {
				int n = GenElfEdifice.getFakeCircleLen(treeSize, i, 2);
				{
					BlockPos at = pos.add(i, y, n);
					if (world.isAirBlock(at) && !building.containsKey(at)) building.put(at, elfLog);
				}
				{
					BlockPos at = pos.add(i, y, -n);
					if (world.isAirBlock(at) && !building.containsKey(at)) building.put(at, elfLog);
				}
				{
					BlockPos at = pos.add(n, y, i);
					if (world.isAirBlock(at) && !building.containsKey(at)) building.put(at, elfLog);
				}
				{
					BlockPos at = pos.add(-n, y, i);
					if (world.isAirBlock(at) && !building.containsKey(at)) building.put(at, elfLog);
				}
			}
		}
		// 天花板
		if (pos.getY() + high < builder.getEdificeCore().getY() - 4) {
			int size = GenElfEdifice.getFakeCircleLen(treeSize, 0, 2);
			for (int i = -size + 1; i < size; i++) {
				int n = GenElfEdifice.getFakeCircleLen(treeSize, i, 2);
				for (int a = 0; a < n; a++) {
					building.put(pos.add(i, high, a), elfLog);
					building.put(pos.add(i, high, -a), elfLog);
				}
			}
		}
		// 地板
		int size = GenElfEdifice.getFakeCircleLen(treeSize, 0, 2);
		for (int i = -size + 1; i < size; i++) {
			int n = GenElfEdifice.getFakeCircleLen(treeSize, i, 2);
			for (int a = 0; a < n; a++) {
				if (!building.containsKey(pos.add(i, -1, a))) building.put(pos.add(i, -1, a), elfLog);
				if (!building.containsKey(pos.add(i, -1, -a))) building.put(pos.add(i, -1, -a), elfLog);
			}
		}

		iter = building.entrySet().iterator();
		after = new LinkedList<>();
	}

	public boolean isFinish() {
		return iter == null;
	}

	public Entry<BlockPos, IBlockState> next() {
		if (iter == null) return null;
		while (iter.hasNext()) {
			Entry<BlockPos, IBlockState> entry = iter.next();
			if (after != null && BuildingBlocks.needToLater(entry.getValue())) {
				after.add(entry);
				continue;
			}
			return entry;
		}
		// 处理after
		if (after != null && !after.isEmpty()) {
			iter = after.iterator();
			after = null;
			return iter.next();
		}
		iter = null;
		return null;
	}

	public void markPeople() {
		people++;
	}

	public void subPeople() {
		people = Math.max(people - 1, 0);
	}

	public int getPeople() {
		return people;
	}

	public boolean needPeople() {
		int c = builder.getBlockCount();
		return people <= c / 512;
	}

}
