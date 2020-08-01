package yuzunyannn.elementalsorcery.elf.trade;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public class Trade implements INBTSerializable<NBTTagCompound> {

	protected TradeList tradeList = new TradeList();

	public Trade() {
		tradeList.tradePtr = this;
	}

	public Trade(NBTTagCompound nbt) {
		tradeList.tradePtr = this;
		this.deserializeNBT(nbt);
	}

	/** 设置交易队列，保证原始数据不变更 */
	public void setTradeList(TradeList tradeList) {
		this.tradeList = tradeList == null ? new TradeList() : tradeList.copy();
		this.tradeList.tradePtr = this;
	}

	public TradeList getTradeList() {
		return tradeList;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return tradeList.serializeNBT();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		tradeList.deserializeNBT(nbt);
	}

	public int getTradeListSize() {
		return tradeList.size();
	}

	public TradeList.TradeInfo getTradeInfo(int index) {
		return tradeList.get(index);
	}

	public void callback$TradeList$add(TradeList.TradeInfo info) {

	}

	/** 当物品出售的时候回调，可以设置剩余数量 */
	public void sell(int index, int count) {

	}

	/** 当玩家出售物品给精灵的时候回调，可以设置剩余数量 */
	public void reclaim(int index, int count) {

	}

	/** 剩余库存，默认trade是可以拿无限个 */
	public int stock(int index) {
		return 1;
	}

	/** 花费 */
	public int cost(int index) {
		return tradeList.get(index).cost;
	}

	/** 商品 */
	public ItemStack commodity(int index) {
		return tradeList.get(index).commodity;
	}
}
