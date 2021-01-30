package yuzunyannn.elementalsorcery.grimoire;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IMantraData extends INBTSerializable<NBTTagCompound> {

	/** 序列化一个数据，作为发送到客户端使用，客户端仍然调用deserializeNBT */
	NBTTagCompound serializeNBTForSend();
}
