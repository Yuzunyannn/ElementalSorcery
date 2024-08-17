package yuzunyannn.elementalsorcery.dungeon;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.computer.soft.IDeviceFile;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncExecuteContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncJsonCreateContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncTimes;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.api.util.target.CapabilityObjectRef;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.computer.Computer;
import yuzunyannn.elementalsorcery.computer.Device;
import yuzunyannn.elementalsorcery.computer.DeviceNetwork;
import yuzunyannn.elementalsorcery.computer.DiskItem;
import yuzunyannn.elementalsorcery.computer.soft.EOS;
import yuzunyannn.elementalsorcery.tile.device.ComputerTile;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class DungeonFuncTileAttach extends GameFuncTimes {

	NBTTagCompound cfg;
	String label;

	@Override
	public void loadFromJson(JsonObject json, GameFuncJsonCreateContext context) {
		super.loadFromJson(json, context);
		cfg = json.getObject("config").asNBT();
		if (json.has("label")) label = json.needString("label");
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		nbt.setTag("cfg", cfg);
		if (label != null) nbt.setString("label", label);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		super.deserializeNBT(nbt);
		cfg = nbt.getCompoundTag("cfg");
		label = nbt.getString("label");
	}

	@Override
	protected void execute(GameFuncExecuteContext context) {
		DungeonAreaRoom room = null;
		if (context instanceof DungeonFuncExecuteContext) room = ((DungeonFuncExecuteContext) context).getRoom();
		else {
			Object obj = context.getExtra().get("room");
			if (obj instanceof DungeonAreaRoom) room = (DungeonAreaRoom) obj;
		}
		if (room == null) return;

		BlockPos pos = context.getBlockPos();
		World world = context.getWorld();
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity == null) return;

		DungeonCategory category = new DungeonCategory(context.getExtra());
		if (label != null) category.setLabelPos(label, pos);

		for (String key : cfg.getKeySet()) {
			try {
				NBTTagCompound data = cfg.getCompoundTag(key);
				key = key.substring(0, 1).toUpperCase() + key.substring(1).toLowerCase();
				Method method = this.getClass().getMethod("attach"
						+ key, DungeonCategory.class, TileEntity.class, NBTTagCompound.class);
				method.invoke(this, category, tileEntity, data);
			} catch (InvocationTargetException e) {
				ESAPI.logger.warn("方法内部异常哦", e.getTargetException());
			} catch (Exception e) {
				if (ESAPI.isDevelop) ESAPI.logger.warn("找不到方法呀", e);
			}
		}

		tileEntity.markDirty();
	}

	@Override
	public String toString() {
		return "<Attach> state:" + cfg;
	}

	public void attachComputer(DungeonCategory category, TileEntity tile, NBTTagCompound data) {
		IComputer computer = tile.getCapability(Computer.COMPUTER_CAPABILITY, null);
		if (computer == null) return;

		if (data.getBoolean("init-disk")) {
			if (computer instanceof ComputerTile) {
				ComputerTile ct = (ComputerTile) computer;
				ct.addDisk(new DiskItem(new ItemStack(ESObjects.ITEMS.DISK)));
			}
		}

		IOS os = computer.getSystem();

		if (data.hasKey("boot")) {
			IDeviceFile file = os.io(EOS.BOOT_PATH);
			IDeviceStorage storage = file.open();
			if (storage != null) {
				storage.set(EOS.BASE, data.getString("boot"));
				storage.close();
			}
		}

	}

	public void attachDevice(DungeonCategory category, TileEntity tile, NBTTagCompound data) {
		final IDevice device = tile.getCapability(Device.DEVICE_CAPABILITY, null);
		if (device == null) return;

		if (data.hasKey("network")) {
			NBTTagCompound ndata = data.getCompoundTag("network");
			if (ndata.hasKey("link-to")) link2: {
				NBTTagList list = ndata.getTagList("link-to", NBTTag.TAG_STRING);
				if (list.isEmpty()) break link2;
				VariableSet params = new VariableSet();
				params.set("labels", list);
				params.set("pos", tile.getPos(), VariableSet.BLOCK_POS);
				category.addOnCompleteTask(DungeonFuncTileAttach::doNetworkLink2, params);
			}
		}
	}

	public static void doNetworkLink2(DungeonCategory category, VariableSet params) {
		NBTTagList list = (NBTTagList) params.get("labels");
		BlockPos pos = params.get("pos", VariableSet.BLOCK_POS);
		World world = category.getWorld();
		TileEntity tile = world.getTileEntity(pos);
		IDevice device = tile != null ? tile.getCapability(Device.DEVICE_CAPABILITY, null) : null;
		if (device == null) return;
		for (int i = 0; i < list.tagCount(); i++) {
			String label = list.getStringTagAt(i);
			TileEntity toDevice = findTileEntity(category, world, label);
			if (toDevice == null) continue;
			DeviceNetwork.doNetworkConnect(device, CapabilityObjectRef.of(toDevice));
		}
	}

	public static TileEntity findTileEntity(DungeonCategory category, World world, String label) {
		BlockPos pos = category.getPosByLabel(label);
		if (pos == null) return null;
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity == null) return null;
		return tileEntity;
	}

	public static IDevice findDevice(DungeonCategory category, World world, String label) {
		TileEntity tileEntity = findTileEntity(category, world, label);
		if (tileEntity == null) return null;
		return tileEntity.getCapability(Device.DEVICE_CAPABILITY, null);
	}
}
