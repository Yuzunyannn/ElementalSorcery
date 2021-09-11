package yuzunyannn.elementalsorcery.api.tile;

import net.minecraft.nbt.NBTTagCompound;

public interface ICustomNBTSerialize {
	/** 读取自定义字段 */
	default void readCustomDataFromNBT(NBTTagCompound nbt) {

	}

	/** 写入自定义字段 */
	default void writeCustomDataToNBT(NBTTagCompound nbt) {

	}
}
