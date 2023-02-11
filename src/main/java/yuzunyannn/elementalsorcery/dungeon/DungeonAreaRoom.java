package yuzunyannn.elementalsorcery.dungeon;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.building.BuildingFace;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class DungeonAreaRoom implements INBTSerializable<NBTTagCompound> {

	protected int areId;
	protected int id;
	protected DungeonRoomType inst;
	protected BlockPos at;
	protected List<DungeonAreaDoor> doorLinks;
	protected EnumFacing facing = EnumFacing.NORTH;

	public DungeonAreaRoom(DungeonRoomType inst) {
		this.inst = inst;
		int n = inst.getDoors().size();
		doorLinks = new ArrayList<>(n);
		while (doorLinks.size() < n) doorLinks.add(new DungeonAreaDoor());
	}

	public DungeonAreaRoom(NBTTagCompound nbt) {
		this.deserializeNBT(nbt);
	}

	public AxisAlignedBB getBox() {
		AxisAlignedBB box = inst.getBuildingBox();
		return BuildingFace.face(box, facing).offset(at);
	}

	public int getId() {
		return id;
	}

	public int getAreId() {
		return areId;
	}

	public List<DungeonAreaDoor> getDoorLinks() {
		return doorLinks;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("id", id);
		nbt.setString("inst", inst.getRegistryName().toString());
		NBTHelper.setBlockPos(nbt, "pos", at);
		nbt.setByte("face", (byte) facing.getIndex());
		NBTHelper.setNBTSerializableList(nbt, "doors", doorLinks);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		id = nbt.getInteger("id");
		inst = DungeonRoomType.REGISTRY.getValue(new ResourceLocation(nbt.getString("inst")));
		at = NBTHelper.getBlockPos(nbt, "pos");
		facing = EnumFacing.byIndex(nbt.getByte("face"));
		doorLinks = NBTHelper.getNBTSerializableList(nbt, "doors", DungeonAreaDoor.class, NBTTagCompound.class);
	}

}
