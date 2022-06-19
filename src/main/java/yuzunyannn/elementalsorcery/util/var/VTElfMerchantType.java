package yuzunyannn.elementalsorcery.util.var;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import yuzunyannn.elementalsorcery.elf.pro.merchant.ElfMerchantType;

public class VTElfMerchantType implements IVariableType<ElfMerchantType> {

	@Override
	public ElfMerchantType newInstance(NBTBase base) {
		if (base instanceof NBTTagString) return ElfMerchantType.getMerchantType(((NBTTagString) base).getString());
		return ElfMerchantType.getMerchantType(null);
	}

	@Override
	public NBTBase serializable(ElfMerchantType obj) {
		return new NBTTagString(obj.getRegistryName());
	}

}
