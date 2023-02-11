package yuzunyannn.elementalsorcery.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import yuzunyannn.elementalsorcery.dungeon.DungeonAreaRoom;

public class TileDungeonDoor extends TileDungeonBase {

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
	}

	public void onBlockActivated(EntityPlayer player, EnumHand hand, EnumFacing facing, EnumFacing blockFacing) {

	}

	public void initByDungeon(DungeonAreaRoom room, int doorIndex, NBTTagCompound dataset) {
		this.areaId = room.getAreId();
		this.roomId = room.getId();
	}

}
