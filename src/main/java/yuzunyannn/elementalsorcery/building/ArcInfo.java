package yuzunyannn.elementalsorcery.building;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class ArcInfo {
	public static final int MISS = 2;
	public static final int WAITING = 1;
	public static final int SUCCESS = 0;

	public BlockPos pos = null;
	public String keyName = null;
	public Building building = null;
	public EnumFacing facing = EnumFacing.NORTH;
	public int flags = SUCCESS;

	public ArcInfo(ItemStack stack, Side side) {
		if (!ArcInfo.isArc(stack)) return;
		NBTTagCompound nbt = stack.getSubCompound("building");
		this.flags = nbt.getInteger("flags");
		if (this.flags == MISS) return;
		this.pos = NBTHelper.getBlockPos(nbt, "pos");
		this.keyName = nbt.getString("key");
		if (this.keyName.isEmpty()) return;
		if (side.isClient() && !Minecraft.getMinecraft().isSingleplayer()) {
			this.building = BuildingLib.instance.giveBuilding(this.keyName);
			long mtime = nbt.getLong("mtime");
			if (this.building.mtime != mtime) {
				BuildingLib.wantBuildingDatasFormServer(this.keyName);
				this.building.mtime = mtime;
			}
		} else {
			this.building = BuildingLib.instance.getBuilding(this.keyName);
			if (this.building == null) {
				nbt.setInteger("flags", MISS);
				this.flags = MISS;
				return;
			}
			nbt.setLong("mtime", this.building.mtime);
			BuildingLib.instance.use(this.keyName);
		}
		if (nbt.hasKey("face")) this.facing = EnumFacing.values()[nbt.getByte("face")];
	}

	public boolean isValid() {
		return this.building != null;
	}

	public boolean isMiss() {
		return this.flags == MISS;
	}

	static public void initArcInfoToItem(ItemStack stack, String key) {
		NBTTagCompound nbt = stack.getOrCreateSubCompound("building");
		nbt.setInteger("flags", SUCCESS);
		nbt.setString("key", key);
		NBTHelper.setBlockPos(nbt, "pos", BlockPos.ORIGIN);
		Building building = BuildingLib.instance.getBuilding(key);
		if (building != null) nbt.setLong("mtime", building.mtime);
		else nbt.setLong("mtime", 0);
	}

	static public ItemStack createArcInfoItem(String key) {
		ItemStack ar = new ItemStack(ESInit.ITEMS.ARCHITECTURE_CRYSTAL);
		ArcInfo.initArcInfoToItem(ar, key);
		return ar;
	}

	static public boolean isArc(ItemStack stack) {
		NBTTagCompound nbt = stack.getSubCompound("building");
		if (nbt == null) return false;
		if (!NBTHelper.hasBlockPos(nbt, "pos")) return false;
		if (!nbt.hasKey("key")) return false;
		return true;
	}
}
