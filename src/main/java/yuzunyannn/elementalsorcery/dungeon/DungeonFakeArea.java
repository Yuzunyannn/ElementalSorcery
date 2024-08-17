package yuzunyannn.elementalsorcery.dungeon;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;

public class DungeonFakeArea extends DungeonArea {

	static private DungeonFakeArea FAKE_AREA;

	public static DungeonFakeArea getOrCreate() {
		if (FAKE_AREA != null) return FAKE_AREA;
		FAKE_AREA = new DungeonFakeArea();
		return FAKE_AREA;
	}

	public DungeonFakeArea() {
		super("Fake");
		this.excerpt.id = -1;
	}

	@Override
	public void generate(DungeonWorld dw, BlockPos at) {
		rooms.clear();
	}

	public DungeonFakeArea reset() {
		rooms.clear();
		return this;
	}

	public DungeonFakeAreaRoom fakeRoom(World world, BlockPos pos, EnumFacing facing, String buildKey) {
		DungeonRoomType type = DungeonLib.devRegister(buildKey);
		DungeonFakeAreaRoom room = new DungeonFakeAreaRoom(type);
		room.facing = facing;
		DungeonAreaGenerator generate = new DungeonAreaGenerator(this, world, RandomHelper.rand);
		generate.addRoom(pos, room);
		return room;
	}

	@Override
	public void startBuildRoom(World world, int roomId, EntityPlayer openPlayer) {
		DungeonAreaRoom room = getRoomById(roomId);
		if (room == null) return;
		room.isBuild = false;
		super.startBuildRoom(world, roomId, openPlayer);
	}

	public static class DungeonFakeAreaRoom extends DungeonAreaRoom {
		public DungeonFakeAreaRoom(DungeonRoomType inst) {
			super(inst);
		}
	}

}
