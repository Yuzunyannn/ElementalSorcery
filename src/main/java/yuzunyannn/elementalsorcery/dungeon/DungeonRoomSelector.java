package yuzunyannn.elementalsorcery.dungeon;

import java.util.Arrays;
import java.util.Collection;

import net.minecraftforge.registries.IForgeRegistryEntry;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;

public class DungeonRoomSelector extends IForgeRegistryEntry.Impl<DungeonRoomSelector> {

	public static DungeonRoomSelector create(DungeonArea area) {
		return new DungeonRoomSelector();
	}

	int testCount = 10;

	public DungeonRoomSelector() {

	}

	public DungeonRoomType getFirstRoom() {
		return DungeonRoomLib.DUNGEON_CENTER;
	}

	public Collection<DungeonRoomType> getAlternateRooms(DungeonAreaRoom currRoom, int doorIndex) {
		if (testCount < 0) return null;
		DungeonRoomType list[] = new DungeonRoomType[] { DungeonRoomLib.DUNGEON_SMALL_TOWARD4,
				DungeonRoomLib.DUNGEON_CORRIDOR_TOWARD4, DungeonRoomLib.DUNGEON_SMAL_PRISON_TOWARD2 };
		return Arrays.asList(RandomHelper.randomOrder(list));
	}

	public void onBuildRoom(DungeonAreaRoom newRoom) {
		testCount--;
	}

}
