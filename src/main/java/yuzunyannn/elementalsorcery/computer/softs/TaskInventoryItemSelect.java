package yuzunyannn.elementalsorcery.computer.softs;

import java.util.UUID;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGui;
import yuzunyannn.elementalsorcery.api.util.MatchHelper;
import yuzunyannn.elementalsorcery.api.util.detecter.DDByte;
import yuzunyannn.elementalsorcery.api.util.detecter.DDItemHandler;
import yuzunyannn.elementalsorcery.api.util.detecter.DDItemStack;
import yuzunyannn.elementalsorcery.api.util.detecter.DDString;
import yuzunyannn.elementalsorcery.api.util.target.IObjectGetter;
import yuzunyannn.elementalsorcery.computer.soft.TaskBase;
import yuzunyannn.elementalsorcery.tile.device.DeviceFeature;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class TaskInventoryItemSelect extends TaskBase {

	public static final String ID = "#PIIS";

	protected UUID itemWriterDevice;
//	protected boolean getLocked;
	protected boolean isWriting;
	protected byte code;
	protected int tick = 0;
	protected ItemStack enabledStack = ItemStack.EMPTY;
	protected DDItemHandler ddItemHandler;
	protected IItemHandlerModifiable handler = new ItemStackHandler();
	protected IObjectGetter<IItemHandler> handlerGetter = IObjectGetter.EMPTY;
	protected NBTTagCompound writeData = new NBTTagCompound();
	protected String tagTanslateKey = "";

	public TaskInventoryItemSelect(IOS os, int pid) {
		super(os, pid);
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
	public void writeSaveData(INBTWriter writer) {
		if (itemWriterDevice != null) writer.write("iwd", itemWriterDevice);
		if (!enabledStack.isEmpty()) writer.write("estk", enabledStack);
		if (tagTanslateKey != null && !tagTanslateKey.isEmpty()) writer.write("tk", tagTanslateKey);
		if (writeData != null) writer.write("write", writeData);
	}

	@Override
	public void readSaveData(INBTReader reader) {
		if (reader.has("iwd")) itemWriterDevice = reader.uuid("iwd");
		enabledStack = reader.itemStack("estk");
		tagTanslateKey = reader.string("tk");
		writeData = reader.compoundTag("write");
		handlerGetter = IObjectGetter.EMPTY;
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
		if (itemWriterDevice == null) return;
		if (handlerGetter == IObjectGetter.EMPTY) handlerGetter = getOS().askCapability(itemWriterDevice,
				CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		ddItemHandler.setCheckHandler(handlerGetter.softGet());
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

	public void setWriteData(NBTTagCompound writeData) {
		this.writeData = writeData == null ? this.writeData : writeData;
	}

	protected void sendCode(int code) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("code", (byte) code);
		getOS().message(this, nbt);
	}

	@DeviceFeature(id = "wd")
	public void doWriteData(int slot) {
		if (isWriting) return;
		isWriting = true;
		writeData(slot);
	}

	protected void writeData(int slot) {
		IItemHandler handler = handlerGetter.toughGet();

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
