package yuzunyannn.elementalsorcery.tile.dungeon;

import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.dungeon.DungeonArea;
import yuzunyannn.elementalsorcery.dungeon.DungeonAreaRoom;
import yuzunyannn.elementalsorcery.dungeon.DungeonWorld;
import yuzunyannn.elementalsorcery.tile.TileEntityNetwork;

public abstract class TileDungeonBase extends TileEntityNetwork {

	protected int areaId = 0;
	protected int roomId = 0;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (this.isSending()) return super.writeToNBT(compound);
		compound.setInteger("areaId", areaId);
		compound.setInteger("roomId", roomId);
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		areaId = compound.getInteger("areaId");
		roomId = compound.getInteger("roomId");
	}

	public boolean isRunMode() {
		return areaId > 0;
	}

	public DungeonArea getDungeonArea() {
		if (!isRunMode()) return null;
		return DungeonWorld.getDungeonWorld(world).getDungeon(areaId);
	}

	public DungeonAreaRoom getDungeonRoom() {
		DungeonArea area = getDungeonArea();
		return area == null ? null : area.getRoomById(roomId);
	}

	public void onBreak() {

	}

}
