package yuzunyannn.elementalsorcery.util.var;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.util.var.IVariableType;
import yuzunyannn.elementalsorcery.elf.trade.TradeCount;

public class VTTradeCount implements IVariableType<TradeCount> {

	@Override
	public TradeCount newInstance(NBTBase base) {
		if (base instanceof NBTTagCompound) return new TradeCount((NBTTagCompound) base);
		return new TradeCount();
	}

	@Override
	public NBTBase serializable(TradeCount obj) {
		return obj.serializeNBT();
	}

}
