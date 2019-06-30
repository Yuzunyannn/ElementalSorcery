package yuzunyan.elementalsorcery.util.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import yuzunyan.elementalsorcery.building.Building;
import yuzunyan.elementalsorcery.building.BuildingLib;

public class ItemArchitectureHelper {
	static public class ArcInfo {
		public BlockPos pos = null;
		public Building building = null;
		public String name = null;

		public boolean isValid() {
			return building != null;
		}

		public boolean isEmpty() {
			return building == null;
		}
	}

	static public void initArcInfoToItem(ItemStack stack, String id) {
		NBTTagCompound nbt = stack.getOrCreateSubCompound("building");
		nbt.setIntArray("pos", new int[] { 0, 0, 0 });
		nbt.setString("key", id);
	}

	static public ArcInfo getArcInfoFromItem(ItemStack stack) {
		NBTTagCompound nbt = stack.getSubCompound("building");
		ArcInfo info = new ArcInfo();
		if (nbt == null)
			return info;
		int[] axis = nbt.getIntArray("pos");
		if (axis == null || axis.length < 3)
			return info;
		String name = nbt.getString("key");
		if (name == null)
			return info;
		info.name = name;
		info.pos = new BlockPos(axis[0], axis[1], axis[2]);
		info.building = BuildingLib.instance.getBuilding(name);
		return info;
	}

	static public boolean isArc(ItemStack stack) {
		NBTTagCompound nbt = stack.getSubCompound("building");
		if (nbt == null)
			return false;
		int[] axis = nbt.getIntArray("pos");
		if (axis == null || axis.length < 3)
			return false;
		String id = nbt.getString("key");
		if (id == null)
			return false;
		return true;
	}
}
