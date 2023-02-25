package yuzunyannn.elementalsorcery.tile.dungeon;

import net.minecraft.block.BlockStainedGlass;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.block.env.BlockDungeonDoor;
import yuzunyannn.elementalsorcery.dungeon.DungeonArea;
import yuzunyannn.elementalsorcery.dungeon.DungeonAreaDoor;
import yuzunyannn.elementalsorcery.dungeon.DungeonAreaRoom;

public class TileDungeonDoor extends TileDungeonBase {

	protected int doorIndex;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (!this.isSending()) compound.setInteger("doorIndex", doorIndex);
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		this.doorIndex = compound.getInteger("doorIndex");
		super.readFromNBT(compound);
	}

	public DungeonAreaDoor getDungeonDoor() {
		DungeonAreaRoom room = this.getDungeonRoom();
		return room == null ? null : room.getDoorLink(this.doorIndex);
	}

	public void onBlockActivated(EntityPlayer player, EnumHand hand, EnumFacing facing, EnumFacing blockFacing) {
		if (!isRunMode()) {

			return;
		}

		DungeonAreaDoor door = this.getDungeonDoor();
		if (door == null) {
			world.setBlockToAir(pos);
			BlockDungeonDoor.onHarvestDoor(world, player, pos);
			return;
		}

		if (!door.isLink()) return;
		if (door.isOpen()) return;

		DungeonArea area = this.getDungeonArea();
		area.startBuildRoom(world, door.getLinkRoomId(), player);

		EnumDyeColor color = EnumDyeColor.BLUE;

		world.destroyBlock(pos, false);
		world.setBlockState(pos, Blocks.STAINED_GLASS.getDefaultState().withProperty(BlockStainedGlass.COLOR, color));
	}

	public void initByDungeon(DungeonAreaRoom room, int doorIndex, NBTTagCompound dataset) {
		this.areaId = room.getAreId();
		this.roomId = room.getId();
		this.doorIndex = doorIndex;
	}

}
