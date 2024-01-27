package yuzunyannn.elementalsorcery.computer.soft;

import java.util.UUID;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import yuzunyannn.elementalsorcery.api.computer.DNParams;
import yuzunyannn.elementalsorcery.api.computer.DNResult;
import yuzunyannn.elementalsorcery.api.computer.DNResultCode;
import yuzunyannn.elementalsorcery.api.computer.soft.APP;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGui;
import yuzunyannn.elementalsorcery.api.util.detecter.DDByte;
import yuzunyannn.elementalsorcery.util.item.ItemHandlerVest;

public class TaskInventoryItemSelect extends APP {

	public static final String ID = "#PIIS";

	protected UUID itemWriterDevice;
	protected boolean getLocked;
	protected byte code;

	public TaskInventoryItemSelect(IOS os, int pid) {
		super(os, pid);
		this.setTask(true);
		this.detecter.add("code", new DDByte(c -> code = c, () -> code));
	}

	@Override
	public void bindDevice(UUID uuid) {
		itemWriterDevice = uuid;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		if (itemWriterDevice != null) nbt.setUniqueId("iwd", itemWriterDevice);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasUniqueId("iwd")) itemWriterDevice = nbt.getUniqueId("iwd");
		super.deserializeNBT(nbt);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		boolean isRemote = getOS().isRemote();

		if (isRemote) return;

		if (itemWriterDevice != null && !getLocked) {
			getLocked = true;
			getOS().notice(itemWriterDevice, "get-inventory", DNParams.EMPTY).thenAccept(r -> onGetInventory(r));
		}
	}

	public void onGetInventory(DNResult result) {

		getLocked = false;
		if (result.code != DNResultCode.SUCCESS) {
			if (result.code == DNResultCode.UNAVAILABLE) return;
			code = 2;
			detecter.markDirty("code");
			return;
		}
		IItemHandler handler = result.get("itemHandler", IItemHandler.class);
		if (handler == null) {
			IInventory inventory = result.get("inventory", IInventory.class);
			if (inventory != null) handler = new ItemHandlerVest(inventory);
		}
		if (handler == null) {
			code = 2;
			detecter.markDirty("code");
			return;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ISoftGui createGUIRender() {
		return new TaskInventoryItemSelectGui(this);
	}

}
