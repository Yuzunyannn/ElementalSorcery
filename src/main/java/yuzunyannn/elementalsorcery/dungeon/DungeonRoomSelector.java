package yuzunyannn.elementalsorcery.dungeon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.registries.IForgeRegistryEntry;
import yuzunyannn.elementalsorcery.item.ItemMemoryFragment.MemoryFragment;
import yuzunyannn.elementalsorcery.util.helper.JavaHelper;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;

public class DungeonRoomSelector extends IForgeRegistryEntry.Impl<DungeonRoomSelector> {

	public static DungeonRoomSelector create(DungeonArea area, Random rand) {
		return new DungeonRoomSelector(rand);
	}

	protected static class BuildRoomNode implements Comparable<BuildRoomNode> {
		final DungeonRoomType roomType;
		final int step;
		final int maxCount;
		int randomStep;
		private boolean isCore;
		int accumulate = 0;
		int buildCount = 0;

		public BuildRoomNode(DungeonRoomType type, int step, int maxCount) {
			this.roomType = type;
			this.step = step;
			this.maxCount = maxCount;
			this.accumulate = this.step;
		}

		protected void onBuild(Random rand) {
			if (this.randomStep > this.step) this.accumulate += RandomHelper.randomRange(step, randomStep, rand);
			else this.accumulate += this.step;
			this.buildCount++;
		}

		public int getBuildCount() {
			return buildCount;
		}

		protected boolean noStock() {
			if (this.maxCount < 0) return false;
			return this.buildCount >= this.maxCount;
		}

		private BuildRoomNode setCore() {
			this.isCore = true;
			return this;
		}

		@Override
		public int compareTo(BuildRoomNode o) {
			return this.accumulate - o.accumulate;
		}

		public DungeonRoomType getRoomType() {
			return roomType;
		}
	}

	// 大 ... 小
	private ArrayList<BuildRoomNode> buildList = new ArrayList<>();
	private IdentityHashMap<DungeonRoomType, BuildRoomNode> buildMap = new IdentityHashMap<>();
	private int coreCount = 0;
	private IdentityHashMap<EnumDyeColor, Integer> keyMap = new IdentityHashMap<>();
	private int buildCount = 0;
	private Random rand;

	public DungeonRoomSelector(Random rand) {
		this.init();
		this.rand = rand;
	}

	protected void init() {
		addBuildRoom(DungeonLib.DUNGEON_CORRIDOR_TOWARD4, 4, -1);
		addBuildRoom(DungeonLib.DUNGEON_SMALL_TOWARD4, 4, 10, -1);
		addBuildRoom(DungeonLib.DUNGEON_SMAL_PRISON_TOWARD2, 4, 6, -1);
		addBuildRoom(DungeonLib.DUNGEON_SMALL_LIBRARY_TOWARD3, 4, 10, -1);
		addBuildRoom(DungeonLib.DUNGEON_SMALL_ROOM_TOWARD1, 4, 6, -1);

		addBuildCoreRoom(DungeonLib.DUNGEON_MANTRA_LAB_TOWARD2, 5); 
		addBuildRoom(DungeonLib.DUNGEON_ROOM_TOWARD2, 4, 6, 10);
		addBuildRoom(DungeonLib.DUNGEON_SMALL_GARDEN_TOWARD3, 6, 12);
		addBuildRoom(DungeonLib.DUNGEON_CHECKPOINT, 6, 12, -1);

		addBuildCoreRoom(DungeonLib.DUNGEON_GREENHOUSE_TOWARD4, 6); 	// t 16
		addBuildCoreRoom(DungeonLib.DUNGEON_STRATEGY_HALL_TOWARD3, 7);
		addBuildRoom(DungeonLib.DUNGEON_SHADY_PATH_TOWARD2, 4, -1);
		addBuildRoom(DungeonLib.DUNGEON_CLINIC_TOWARD2, 6, 10, -1);
		addBuildRoom(DungeonLib.DUNGEON_LOOKOUT_TOWER_TOWARD4, 4, 8, 5);
		addBuildRoom(DungeonLib.DUNGEON_SHADY_PARK_TOWARD4, 4, 10, -1);
		addBuildRoom(DungeonLib.DUNGEON_RESTAURANT_TOWARD2, 8, 12, -1);


		addBuildCoreRoom(DungeonLib.DUNGEON_LABORATORY_TOWARD2, 10);
	}

	protected BuildRoomNode addBuildRoom(DungeonRoomType type, int step, int maxCount) {
		if (buildMap.containsKey(type)) return buildMap.get(type);
		BuildRoomNode node = new BuildRoomNode(type, step, maxCount);
		JavaHelper.orderAdd(buildList, node);
		buildMap.put(type, node);
		return node;
	}

	protected BuildRoomNode addBuildRoom(DungeonRoomType type, int step, int stepMax, int maxCount) {
		BuildRoomNode node = addBuildRoom(type, step, maxCount);
		node.randomStep = stepMax;
		return node;
	}

	protected BuildRoomNode addBuildCoreRoom(DungeonRoomType type, int step) {
		BuildRoomNode node = addBuildRoom(type, step, 1);
		node.setCore();
		this.coreCount++;
		return node;
	}

	public void onBuildRoom(DungeonAreaRoom newRoom) {
		BuildRoomNode node = buildMap.get(newRoom.inst);
		if (node == null) return;
		// 更新钥匙
		DungeonFuncGlobal config = newRoom.getFuncGlobal();
		if (config != null) {
			for (MemoryFragment mf : config.getRequireMemoryFragments()) {
				EnumDyeColor color = mf.getColor();
				int count = keyMap.containsKey(color) ? keyMap.get(color) : 0;
				keyMap.put(color, count - mf.getCount());
			}
			for (MemoryFragment mf : config.getProduceFragments()) {
				EnumDyeColor color = mf.getColor();
				int count = keyMap.containsKey(color) ? keyMap.get(color) : 0;
				keyMap.put(color, count + mf.getCount());
			}
		}
		// 完成更新数据
		this.buildCount++;
		node.onBuild(rand);
		buildList.remove(node);
		// 有库存的话，重新插入
		if (!node.noStock()) {
			JavaHelper.orderAdd(buildList, node);
			return;
		}
		// 没库存
		if (node.isCore) this.coreCount--;
	}

	public DungeonRoomType getFirstRoom() {
		return DungeonLib.DUNGEON_CENTER;
	}

	public boolean hasCore() {
		return this.coreCount > 0;
	}

	protected boolean isCore(DungeonRoomType type) {
		BuildRoomNode node = buildMap.get(type);
		if (node == null) return false;
		return node.isCore;
	}

	public Collection<DungeonRoomType> getAlternateRooms(DungeonAreaRoom currRoom, int doorIndex) {
		if (!hasCore()) return null;
		if (this.buildCount > 256) throw new RuntimeException("to many build! please check is the algorithm correct!");

		List<DungeonRoomType> results = new ArrayList<>(buildList.size());

		// 从后面开始遍历，越小越先
		for (int i = buildList.size() - 1; i >= 0; i--) {
			BuildRoomNode node = buildList.get(i);
			DungeonRoomType currType = node.getRoomType();
			// 第一个房间必须连通多门
			if (currRoom.getType() == getFirstRoom()) {
				List<DungeonRoomDoor> doors = currType.getDoors();
				if (doors.size() <= 2) continue;
			}
			// core房间不能相连
			if (isCore(currRoom.getType()) && node.isCore) continue;
			// 同樣的房間，走人
			if (currRoom.getType() == currType) continue;
			// 钥匙不够，走人
			if (!isKeyEnough(currType.getFuncGlobal())) continue;
			// 都满足，加入待选队列
			results.add(currType);
		}
		return results;
	}

	protected boolean isKeyEnough(DungeonFuncGlobal config) {
		if (config == null) return true;
		List<MemoryFragment> requires = config.getRequireMemoryFragments();
		IdentityHashMap<EnumDyeColor, Integer> myKeyMap = new IdentityHashMap<>();
		for (MemoryFragment mf : requires) {
			if (mf.getCount() <= 0) continue;
			EnumDyeColor color = mf.getColor();
			if (!this.keyMap.containsKey(color)) return false;
			int hasCount = this.keyMap.get(color);
			int needCount = mf.getCount() + (myKeyMap.containsKey(color) ? myKeyMap.get(color) : 0);
			if (hasCount < needCount) return false;
			myKeyMap.put(color, needCount);
		}
		return true;
	}

//	protected boolean isKeyProvideOnDemand(DungeonFuncGlobal config, Map<EnumDyeColor, Integer> fragmentMap) {
//		if (config == null) return false;
//		List<MemoryFragment> produces = config.getProduceFragments();
//		for (MemoryFragment mf : produces) {
//			if (mf.getCount() <= 0) continue;
//			if (fragmentMap.containsKey(mf.getColor())) return true;
//		}
//		return false;
//	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("build ").append(this.buildCount).append(" room:");
		for (BuildRoomNode node : buildMap.values()) {
			if (node.getBuildCount() <= 0) continue;
			builder.append(node.getRoomType().getRegistryName());
			builder.append("x").append(node.getBuildCount()).append(";");
		}
		return builder.toString();
	}

}
