package yuzunyannn.elementalsorcery.util.helper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;

public class NBTSender extends NBTSaver {

	public final static NBTSender SHARE = new NBTSender();

	public NBTSender() {
		super();
	}

	public NBTSender(NBTTagCompound nbt) {
		super(nbt);
	}

	@Override
	protected NBTBase serialize(INBTSerializable<?> serializable) {
		if (serializable instanceof INBTSS) {
			NBTSender sender = new NBTSender();
			((INBTSS) serializable).writeUpdateData(sender);
			return sender.tag();
		} else return super.serialize(serializable);
	}

	@Override
	protected <U extends NBTBase, T extends INBTSerializable<U>> void deserialize(U nbt, T obj) {
		if (obj instanceof INBTSS) {
			NBTSender sender = new NBTSender((NBTTagCompound) nbt);
			((INBTSS) obj).readUpdateData(sender);
		} else super.deserialize(nbt, obj);
	}

	@Override
	public void write(String key, ItemStack stack) {
		nbt.setTag(key, NBTHelper.serializeItemStackForSend(stack));
	}

	@Override
	public ItemStack itemStack(String key) {
		return NBTHelper.deserializeItemStackFromSend(nbt.getCompoundTag(key));
	}

	@Override
	public void write(String key, Mantra mantra) {
		nbt.setInteger(key, mantra == null ? -1 : Mantra.REGISTRY.getId(mantra));
	}

	@Override
	public Mantra mantra(String key) {
		return Mantra.REGISTRY.getValue(nbt.getInteger(key));
	}

}
