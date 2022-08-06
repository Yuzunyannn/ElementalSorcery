package yuzunyannn.elementalsorcery.util.var;

import yuzunyannn.elementalsorcery.api.util.var.IVariableType;
import yuzunyannn.elementalsorcery.elf.pro.merchant.ElfMerchantType;
import yuzunyannn.elementalsorcery.elf.trade.TradeCount;

public class VariableTypes {

	public final static IVariableType<TradeCount> TRADE_COUNT_OBJ = new VTTradeCount();
	public final static IVariableType<ElfMerchantType> ELF_MERCHANT_TYPE = new VTElfMerchantType();

}
