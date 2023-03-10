package yuzunyannn.elementalsorcery.dungeon;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.api.gfunc.GameFunc;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncGroup;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.building.BuildingFace;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class DungeonAreaRoom implements INBTSerializable<NBTTagCompound> {

	protected int areId;
	protected int id;
	protected DungeonRoomType inst;
	protected BlockPos at;
	protected List<DungeonAreaDoor> doorLinks;
	protected EnumFacing facing = EnumFacing.NORTH;
	protected boolean isBuild = false;
	protected List<GameFunc> funcs;

	protected int funcGlobalIndex = -1;

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

	public List<GameFunc> getFuncs() {
		return funcs;
	}

	public boolean isBuild() {
		return isBuild;
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

	public DungeonAreaDoor getDoorLink(int index) {
		if (index < 0 || index >= doorLinks.size()) return null;
		return doorLinks.get(index);
	}

	public void setFunc(int index, GameFunc func) {
		if (index < 0 || index >= doorLinks.size()) return;
		funcs.set(index, func);
	}

	public GameFunc getFunc(int index) {
		if (index < 0 || index >= doorLinks.size()) return GameFunc.NOTHING;
		return funcs.get(index);
	}

	@Nullable
	public <T extends GameFunc> T getFunc(int index, Class<T> cls) {
		GameFunc func = getFunc(funcGlobalIndex);
		if (cls.isAssignableFrom(func.getClass())) return (T) func;
		return null;
	}

	@Nullable
	public DungeonFuncGlobal getFuncGlobal() {
		return getFunc(funcGlobalIndex, DungeonFuncGlobal.class);
	}

	public DungeonRoomType getType() {
		return inst;
	}

	public EnumFacing getFacing() {
		return facing;
	}

	public BlockPos getCenterPos() {
		return at;
	}

	protected void refresh() {
		funcGlobalIndex = -1;
		for (int i = 0; i < funcs.size(); i++) {
			GameFunc func = funcs.get(i);
			if (func instanceof DungeonFuncGlobal) {
				funcGlobalIndex = i;
			}
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("id", id);
		nbt.setInteger("areId", areId);
		nbt.setString("inst", inst.getRegistryName().toString());
		NBTHelper.setBlockPos(nbt, "pos", at);
		nbt.setByte("face", (byte) facing.getIndex());
		NBTHelper.setNBTSerializableList(nbt, "doors", doorLinks);
		nbt.setBoolean("isBuild", isBuild);
		nbt.setTag("funcs", GameFuncGroup.serializeNBTList(funcs));
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		id = nbt.getInteger("id");
		areId = nbt.getInteger("areId");
		inst = DungeonRoomType.REGISTRY.getValue(new ResourceLocation(nbt.getString("inst")));
		at = NBTHelper.getBlockPos(nbt, "pos");
		facing = EnumFacing.byIndex(nbt.getByte("face"));
		doorLinks = NBTHelper.getNBTSerializableList(nbt, "doors", DungeonAreaDoor.class, NBTTagCompound.class);
		isBuild = nbt.getBoolean("isBuild");
		funcs = GameFuncGroup.deserializeNBTList(nbt.getTagList("funcs", NBTTag.TAG_COMPOUND));
		this.refresh();
	}

}
