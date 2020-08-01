package yuzunyannn.elementalsorcery.elf.trade;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.elf.trade.TradeList.TradeInfo;

public class TradeList implements INBTSerializable<NBTTagCompound>, Iterable<TradeInfo> {

	Trade tradePtr;

	public TradeList() {

	}

	public TradeList(NBTTagCompound nbt) {
		this.deserializeNBT(nbt);
	}

	/** 交易的内容 */
	public static class TradeInfo implements INBTSerializable<NBTTagCompound> {

		public TradeInfo(ItemStack commodity, int cost, boolean isReclaim) {
			this.commodity = commodity;
			this.cost = cost;
			this.reclaim = isReclaim;
		}

		public TradeInfo(NBTTagCompound nbt) {
			this.deserializeNBT(nbt);
		}

		ItemStack commodity = ItemStack.EMPTY;
		int cost;
		boolean reclaim;

		public ItemStack getCommodity() {
			return commodity;
		}

		public int getCost() {
			return cost;
		}

		public boolean isReclaim() {
			return reclaim;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setTag("item", commodity.serializeNBT());
			nbt.setInteger("cost", cost);
			nbt.setBoolean("reclaim", reclaim);
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			this.commodity = new ItemStack(nbt.getCompoundTag("item"));
			this.cost = nbt.getInteger("cost");
			this.reclaim = nbt.getBoolean("reclaim");
		}
	}

	protected List<TradeInfo> tradeList = new ArrayList<>();

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for (TradeInfo info : tradeList) list.appendTag(info.serializeNBT());
		nbt.setTag("list", list);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		tradeList.clear();
		NBTTagList list = nbt.getTagList("list", 10);
		for (NBTBase base : list) tradeList.add(new TradeInfo((NBTTagCompound) base));
	}

	public int size() {
		return tradeList.size();
	}

	public boolean isEmpty() {
		return tradeList.isEmpty();
	}

	public TradeInfo get(int i) {
		return tradeList.get(i);
	}

	public void add(TradeInfo info) {
		tradeList.add(info);
		if (tradePtr != null) tradePtr.callback$TradeList$add(info);
	}

	public void add(ItemStack commodity, int cost, boolean isReclaim) {
		add(new TradeInfo(commodity, cost, isReclaim));
	}

	@Override
	public Iterator<TradeInfo> iterator() {
		return tradeList.iterator();
	}

	public TradeList copy() {
		return new TradeList(this.serializeNBT());
	}

}
