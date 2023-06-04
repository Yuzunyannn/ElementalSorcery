package yuzunyannn.elementalsorcery.container;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.dungeon.DungeonArea;
import yuzunyannn.elementalsorcery.dungeon.DungeonAreaRoom;
import yuzunyannn.elementalsorcery.dungeon.DungeonAreaRoomSpecialThing;
import yuzunyannn.elementalsorcery.dungeon.DungeonWorld;
import yuzunyannn.elementalsorcery.network.MessageSyncContainer.IContainerNetwork;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class ContainerDungeonMap extends Container implements IContainerNetwork {

	final public EntityPlayer player;
	final public BlockPos pos;
	final public boolean isRemote;
	public Integer dungeonId;
	public DungeonArea areaCache;
	protected DungeonAreaRoom currRoom;
	public Integer currRoomId;
	public int tick = 0;
	public Map<Integer, Integer> roomShowMap = new HashMap<>();

	public ContainerDungeonMap(EntityPlayer player, BlockPos pos) {
		this.player = player;
		this.isRemote = player.world.isRemote;
		this.pos = pos;
		DungeonWorld dw = DungeonWorld.getDungeonWorld(player.world);
		currRoom = dw.getAreaRoom(pos);
	}

	@Nullable
	public DungeonArea getDungeonArea() {
		if (dungeonId == null) return null;
		if (areaCache != null) return areaCache;
		DungeonWorld dw = DungeonWorld.getDungeonWorld(player.world);
		areaCache = dw.getDungeon(dungeonId);
		if (areaCache == null) currRoom = null;
		return areaCache;
	}

	@Override
	public void detectAndSendChanges() {
		if (currRoom == null) return;
		if (dungeonId == null) {
			dungeonId = currRoom.getAreId();
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("dId", dungeonId);
			nbt.setInteger("cId", currRoom.getId());
			this.sendToClient(nbt, this.listeners);
		}

		if (tick++ % 20 != 0) return;
		DungeonArea area = getDungeonArea();
		if (area == null) return;

		NBTTagList list = new NBTTagList();

		Collection<DungeonAreaRoom> rooms = area.getRooms();
		for (DungeonAreaRoom room : rooms) {
			if (room.isBuild() && !Integer.valueOf(room.runtimeChangeFlag).equals(roomShowMap.get(room.getId()))) {
				roomShowMap.put(room.getId(), room.runtimeChangeFlag);
				NBTTagCompound showData = new NBTTagCompound();
				showData.setInteger("id", room.getId());
				showData.setInteger("uflag", room.runtimeChangeFlag);
				list.appendTag(showData);
			}
		}

		if (!list.isEmpty()) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setTag("rs", list);
			this.sendToClient(nbt, this.listeners);
		}
	}

	@Override
	public void recvData(NBTTagCompound nbt, Side side) {
		if (side == Side.CLIENT) {
			if (nbt.hasKey("dId")) dungeonId = nbt.getInteger("dId");
			if (nbt.hasKey("cId")) currRoomId = nbt.getInteger("cId");
			if (nbt.hasKey("rs")) {
				NBTTagList list = nbt.getTagList("rs", NBTTag.TAG_COMPOUND);
				for (int i = 0; i < list.tagCount(); i++) {
					NBTTagCompound showData = list.getCompoundTagAt(i);
					roomShowMap.put(showData.getInteger("id"), showData.getInteger("uflag"));
				}
			}
			return;
		} else {
			if (nbt.hasKey("st_pos")) {
				BlockPos pos = NBTHelper.getBlockPos(nbt, "st_pos");
				DungeonArea area = getDungeonArea();
				if (area == null) return;
				DungeonAreaRoom room = area.findRoom(new Vec3d(pos));
				if (room == null) return;
				DungeonAreaRoomSpecialThing thing = room.getSpecialMap().get(pos);
				if (thing == null) return;
				if (thing.getHandler() == null) return;
				thing.getHandler().executeClick(player.world, pos, player, room, this.pos);
			}
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return isRemote ? true : (currRoom == null ? false : (playerIn.getDistanceSq(pos) <= 64));
	}

	@SideOnly(Side.CLIENT)
	public boolean isOpen(int id) {
		return roomShowMap.get(id) != null;
	}

}
