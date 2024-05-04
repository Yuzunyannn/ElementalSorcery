package yuzunyannn.elementalsorcery.util.helper;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public class NBTSender extends NBTSaver {

	public final static NBTSender SHARE = new NBTSender();

	public NBTSender() {
		super();
	}

	public NBTSender(NBTTagCompound nbt) {
		super(nbt);
	}

	@Override
	public void write(String key, INBTSerializable<?> serializable) {
		if (serializable instanceof INBTSS) {
			NBTSender sender = new NBTSender();
			((INBTSS) serializable).writeUpdateData(sender);
			nbt.setTag(key, sender.tag());
		} else super.write(key, serializable);
	}

	@Override
	public <U extends NBTBase, T extends INBTSerializable<U>> T obj(String key, T obj) {
		try {
			if (obj instanceof INBTSS) {
				NBTSender sender = new NBTSender(nbt.getCompoundTag(key));
				((INBTSS) obj).readUpdateData(sender);
			} else obj.deserializeNBT((U) nbt.getTag(key));
		} catch (Exception e) {}
		return obj;
	}

	@Override
	public <U extends NBTBase, T extends INBTSerializable<U>> T obj(String key, Supplier<T> factory) {
		T obj = factory.get();
		try {
			if (obj instanceof INBTSS) {
				NBTSender sender = new NBTSender(nbt.getCompoundTag(key));
				((INBTSS) obj).readUpdateData(sender);
			} else obj.deserializeNBT((U) nbt.getTag(key));
		} catch (Exception e) {}
		return obj;
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
