package yuzunyannn.elementalsorcery.dungeon;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public class DungeonAreaDoor implements INBTSerializable<NBTTagCompound> {

	protected Integer linkRoomId, linkDoorIndex;
	protected boolean isOpen;

	public DungeonAreaDoor() {

	}

	protected void setLink(Integer roomId, Integer doorIndex) {
		this.linkRoomId = roomId;
		this.linkDoorIndex = doorIndex;
	}

	public boolean isLink() {
		return linkRoomId != null;
	}

	public int getLinkRoomId() {
		return linkRoomId;
	}

	public int getLinkDoorIndex() {
		return linkDoorIndex;
	}

	public boolean isOpen() {
		return isOpen;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		if (!isLink()) return nbt;
		nbt.setInteger("rId", linkRoomId);
		nbt.setInteger("dId", linkDoorIndex);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		linkRoomId = null;
		linkDoorIndex = null;
		if (nbt.hasKey("rId")) {
			linkRoomId = nbt.getInteger("rId");
			linkDoorIndex = nbt.getInteger("dId");
		}
	}

}
