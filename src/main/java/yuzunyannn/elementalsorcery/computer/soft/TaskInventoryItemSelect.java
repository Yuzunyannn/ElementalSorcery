package yuzunyannn.elementalsorcery.computer.soft;

import java.util.UUID;

import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.computer.DNParams;
import yuzunyannn.elementalsorcery.api.computer.DNResult;
import yuzunyannn.elementalsorcery.api.computer.DNResultCode;
import yuzunyannn.elementalsorcery.api.computer.soft.APP;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGui;
import yuzunyannn.elementalsorcery.api.util.MatchHelper;
import yuzunyannn.elementalsorcery.api.util.detecter.DDByte;
import yuzunyannn.elementalsorcery.api.util.detecter.DDItemHandler;
import yuzunyannn.elementalsorcery.api.util.detecter.DDItemStack;
import yuzunyannn.elementalsorcery.api.util.detecter.DDString;
import yuzunyannn.elementalsorcery.util.item.ItemHandlerVest;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class TaskInventoryItemSelect extends APP {

	public static final String ID = "#PIIS";

	protected UUID itemWriterDevice;
	protected boolean getLocked;
	protected boolean isWriting;
	protected byte code;
	protected int tick = 0;
	protected ItemStack enabledStack = ItemStack.EMPTY;
	protected DDItemHandler ddItemHandler;
	protected IItemHandlerModifiable handler = new ItemStackHandler();
	protected NBTTagCompound writeData = new NBTTagCompound();
	protected String tagTanslateKey = "";

	public TaskInventoryItemSelect(IOS os, int pid) {
		super(os, pid);
		this.setTask(true);
		this.detecter.add("code", new DDByte(c -> code = c, () -> code));
		this.ddItemHandler = new DDItemHandler(n -> handler = new ItemStackHandler(n), () -> handler);
		this.detecter.add("itms", this.ddItemHandler, true);
		this.detecter.add("es", new DDItemStack(e -> enabledStack = e, () -> enabledStack));
		this.detecter.add("tk", new DDString(e -> tagTanslateKey = e, () -> tagTanslateKey));
	}

	@Override
	public void bindDevice(UUID uuid) {
		itemWriterDevice = uuid;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		if (itemWriterDevice != null) nbt.setUniqueId("iwd", itemWriterDevice);
		if (!enabledStack.isEmpty()) nbt.setTag("estk", enabledStack.serializeNBT());
		if (tagTanslateKey != null && !tagTanslateKey.isEmpty()) nbt.setString("tk", tagTanslateKey);
		if (writeData != null) nbt.setTag("write", writeData);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasUniqueId("iwd")) itemWriterDevice = nbt.getUniqueId("iwd");
		enabledStack = new ItemStack(nbt.getCompoundTag("estk"));
		tagTanslateKey = nbt.getString("tk");
		writeData = nbt.getCompoundTag("write");
		super.deserializeNBT(nbt);
	}

	public void setEnabledStack(ItemStack enabledStack) {
		this.enabledStack = enabledStack;
		this.detecter.markDirty("es");
	}

	public ItemStack getEnabledStack() {
		return enabledStack;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (tick++ % 5 != 0) return;

		boolean isRemote = getOS().isRemote();

		if (isRemote) return;

		if (itemWriterDevice != null && !getLocked) {
			getLocked = true;
			getOS().notice(itemWriterDevice, "get-inventory", DNParams.EMPTY).thenAccept(r -> onGetInventory(r));
		}
	}

	protected IItemHandler askItemHandlerFromResult(DNResult result) {
		if (result.code != DNResultCode.SUCCESS) {
			if (result.code == DNResultCode.UNAVAILABLE) return null;
			code = 2;
			detecter.markDirty("code");
			return null;
		}
		IItemHandler handler = result.get("itemHandler", IItemHandler.class);
		if (handler == null) {
			IInventory inventory = result.get("inventory", IInventory.class);
			if (inventory != null) handler = new ItemHandlerVest(inventory);
		}
		if (handler == null) {
			code = 2;
			detecter.markDirty("code");
			return null;
		}
		return handler;
	}

	public void onGetInventory(DNResult result) {
		getLocked = false;
		IItemHandler handler = askItemHandlerFromResult(result);
		ddItemHandler.setCheckHandler(handler);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ISoftGui createGUIRender() {
		return new TaskInventoryItemSelectGui(this);
	}

	public IItemHandlerModifiable getItemHandler() {
		return handler;
	}

	public byte getCode() {
		return code;
	}

	@Override
	public void handleOperation(NBTTagCompound nbt) {
		super.handleOperation(nbt);
		if (nbt.hasKey("slot")) {
			if (isWriting) return;
			isWriting = true;
			int slot = nbt.getShort("slot");
			getOS().notice(itemWriterDevice, "get-inventory", DNParams.EMPTY).thenAccept(r -> {
				isWriting = false;
				writeData(r, slot);
			});
		}
	}

	public void setWriteData(NBTTagCompound writeData) {
		this.writeData = writeData == null ? this.writeData : writeData;
	}

	protected void sendCode(int code) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("code", (byte) code);
		getOS().message(this, nbt);
	}

	protected void writeData(DNResult result, int slot) {
		IItemHandler handler = askItemHandlerFromResult(result);

		if (handler == null) {
			sendCode(-1);
			return;
		}

		ItemStack stack = handler.getStackInSlot(slot);
		if (stack.isEmpty()) {
			sendCode(-2);
			return;
		}

		if (!enabledStack.isEmpty() && !MatchHelper.isItemMatch(enabledStack, stack)) {
			sendCode(-3);
			return;
		}

		NBTTagCompound data = ItemHelper.getOrCreateTagCompound(stack);
		for (String key : this.writeData.getKeySet()) data.setTag(key, writeData.getTag(key));

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("code", (byte) 1);
		getOS().message(this, nbt);
	}

	public void setTagTanslateKey(String tagTanslateKey) {
		this.tagTanslateKey = tagTanslateKey;
	}

	@SideOnly(Side.CLIENT)
	public String getWriteTagDisplayValue() {
		if (tagTanslateKey == null || tagTanslateKey.isEmpty()) return "";
		return I18n.format(tagTanslateKey);
	}

}
