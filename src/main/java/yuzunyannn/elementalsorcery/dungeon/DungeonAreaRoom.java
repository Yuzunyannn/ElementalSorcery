package yuzunyannn.elementalsorcery.dungeon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.gfunc.GameFunc;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncGroup;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.building.BuildingFace;
import yuzunyannn.elementalsorcery.network.ESNetwork;
import yuzunyannn.elementalsorcery.network.MessageDungeonSync;
import yuzunyannn.elementalsorcery.util.helper.JavaHelper;
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
	protected Map<BlockPos, DungeonAreaRoomSpecialThing> specialMap = new TreeMap<>();
	public int canDropHideFuncCount = 0;
	public int runtimeChangeFlag = 0;

	protected int funcGlobalIndex = -1;

	public DungeonAreaRoom() {
	}

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
		if (index < 0 || index >= funcs.size()) return GameFunc.NOTHING;
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

	public Map<BlockPos, DungeonAreaRoomSpecialThing> getSpecialMap() {
		return specialMap;
	}

	public void setSpecialThing(BlockPos pos, DungeonAreaRoomSpecialThing thing) {
		if (thing == null) specialMap.remove(pos);
		else specialMap.put(pos, thing);
	}

	public void visitCoreBlocks(Function<BlockPos, Boolean> visitor) {
		for (Entry<BlockPos, String> entry : inst.funcs) {
			BlockPos pos = entry.getKey();
			BlockPos at = getCenterPos().add(BuildingFace.face(pos, facing));
			Boolean ret = visitor.apply(at);
			if (!JavaHelper.isTrue(ret)) break;
		}
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

	protected void writeSpecialMap(NBTTagCompound nbt) {
		NBTTagList list = new NBTTagList();
		nbt.setTag("spMap", list);
		for (Entry<BlockPos, DungeonAreaRoomSpecialThing> entry : this.specialMap.entrySet()) {
			NBTTagCompound dat = entry.getValue().serializeNBT();
			NBTHelper.setBlockPos(dat, "pos", entry.getKey());
			list.appendTag(dat);
		}
	}

	protected Map<BlockPos, DungeonAreaRoomSpecialThing> readSpecialMap(NBTTagCompound nbt) {
		NBTTagList list = nbt.getTagList("spMap", NBTTag.TAG_COMPOUND);
		Map<BlockPos, DungeonAreaRoomSpecialThing> map = new HashMap<>();
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound dat = list.getCompoundTagAt(i);
			BlockPos pos = NBTHelper.getBlockPos(dat, "pos");
			DungeonAreaRoomSpecialThing thing = new DungeonAreaRoomSpecialThing(dat);
			if (thing.getHandler() != null) map.put(pos, thing);
		}
		return map;
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
		writeSpecialMap(nbt);
		if (canDropHideFuncCount > 0) nbt.setInteger("cdhcc", canDropHideFuncCount);
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
		specialMap = readSpecialMap(nbt);
		canDropHideFuncCount = nbt.getInteger("cdhcc");
		this.refresh();
	}

	public NBTTagCompound serializeToClient() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("id", id);
		nbt.setString("inst", inst.getRegistryName().toString());
		NBTHelper.setBlockPos(nbt, "pos", at);
		nbt.setByte("face", (byte) facing.getIndex());
		NBTHelper.setNBTSerializableList(nbt, "doors", doorLinks);
		writeSpecialMap(nbt);
		nbt.setInteger("_cf_", runtimeChangeFlag);
		return nbt;
	}

	@SideOnly(Side.CLIENT)
	public void deserializeInClient(NBTTagCompound nbt) {
		id = nbt.getInteger("id");
		inst = DungeonRoomType.REGISTRY.getValue(new ResourceLocation(nbt.getString("inst")));
		at = NBTHelper.getBlockPos(nbt, "pos");
		facing = EnumFacing.byIndex(nbt.getByte("face"));
		doorLinks = NBTHelper.getNBTSerializableList(nbt, "doors", DungeonAreaDoor.class, NBTTagCompound.class);
		specialMap = readSpecialMap(nbt);
		runtimeChangeFlag = nbt.getInteger("_cf_");
	}

	@SideOnly(Side.CLIENT)
	public void requireUpdateFromServer() {
//		System.out.println("requireUpdateFromServer");
		ESNetwork.instance.sendToServer(new MessageDungeonSync(areId, id));
	}

	public void nextUpdateFlag() {
		this.runtimeChangeFlag++;
	}

}
