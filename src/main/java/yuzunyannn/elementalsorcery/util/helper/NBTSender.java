package yuzunyannn.elementalsorcery.util.helper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class NBTSender extends NBTSaver {

	public final static NBTSender SHARE = new NBTSender();

	public NBTSender() {
		super();
	}

	public NBTSender(NBTTagCompound nbt) {
		super(nbt);
	}

	@Override
	public void write(String key, ItemStack stack) {
		nbt.setTag(key, NBTHelper.serializeItemStackForSend(stack));
	}

	@Override
	public ItemStack itemStack(String key) {
		return NBTHelper.deserializeItemStackFromSend(nbt.getCompoundTag(key));
	}

}
