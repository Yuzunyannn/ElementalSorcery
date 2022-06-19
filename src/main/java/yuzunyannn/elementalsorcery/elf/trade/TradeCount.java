package yuzunyannn.elementalsorcery.elf.trade;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import yuzunyannn.elementalsorcery.elf.trade.TradeList.TradeInfo;

//带数值的交易类型
public class TradeCount extends Trade {

	protected ArrayList<Integer> tradeListCount = new ArrayList<>();

	public TradeCount() {

	}

	public TradeCount(NBTTagCompound dat) {
		this.deserializeNBT(dat);
	}

	@Override
	public void setTradeList(TradeList tradeList) {
		super.setTradeList(tradeList);
		tradeListCount.clear();
		for (int i = 0; i < this.tradeList.size(); i++) tradeListCount.add(10);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		nbt.setTag("counts", new NBTTagIntArray(tradeListCount));
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		super.deserializeNBT(nbt);
		int[] cs = nbt.getIntArray("counts");
		tradeListCount.clear();
		for (int i : cs) tradeListCount.add(i);
	}

	@Override
	public void sell(int index, int count) {
		int c = tradeListCount.get(index) - count;
		tradeListCount.set(index, Math.max(0, c));
	}

	@Override
	public void reclaim(int index, int count) {
		int c = tradeListCount.get(index) - count;
		tradeListCount.set(index, Math.max(0, c));
	}

	@Override
	public int stock(int index) {
		if (index < 0 || index >= tradeListCount.size()) return 0;
		return tradeListCount.get(index);
	}

	/** 设置库存 */
	public void setStock(int index, int count) {
		tradeListCount.set(index, count);
	}

	public void addCommodity(ItemStack commodity, int cost, int count, boolean isReclaim) {
		tradeList.add(commodity, cost, isReclaim);
		setStock(tradeListCount.size() - 1, count);
	}

	public void addCommodity(ItemStack commodity, int cost, int count) {
		addCommodity(commodity, cost, count, false);
	}

	@Override
	public void callback$TradeList$add(TradeInfo info) {
		tradeListCount.add(1);
	}

}
