package yuzunyannn.elementalsorcery.api.computer;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IDisk extends ICalculatorObject, INBTSerializable<NBTTagCompound> {

	@Nonnull
	NBTTagCompound getContext();

}
