package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.elf.trade.Trade;
import yuzunyannn.elementalsorcery.elf.trade.TradeClient;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.event.EventServer;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.item.ItemElfPurse;
import yuzunyannn.elementalsorcery.network.MessageSyncContainer.IContainerNetwork;
import yuzunyannn.elementalsorcery.util.item.ItemStackHandlerInventory;

public class ContainerElfTrade extends ContainerElf implements IContainerNetwork {

	public Trade trade;
	// 展示仓库
	IInventory shelves = new ItemStackHandlerInventory(18);
	// 销售仓库
	IInventory sell = new ItemStackHandlerInventory(1);

	public ContainerElfTrade(EntityPlayer player) {
		super(player);
		// 物品栏
		int xoff = 8, yoff = 103;
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, xoff + j * 18, yoff + i * 18));
			}
		}
		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(player.inventory, i, xoff + i * 18, yoff + 58));
		}
		// 出售
		this.addSlotToContainer(new SlotSell(sell, 0, 137, 36));
		// 货架
		xoff = 14;
		yoff = 17;
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 6; ++j) {
				this.addSlotToContainer(new SlotShelves(shelves, j + i * 6, xoff + j * 17, yoff + i * 21));
			}
		}
		if (elf == null || player.world.isRemote) return;
		EventServer.addTickTask(() -> {
			setTrade(elf.getProfession().getTrade(elf, player));
		});
	}

	protected class SlotSell extends Slot {

		public SlotSell(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public void putStack(ItemStack stack) {
			if (trade == null) {
				super.putStack(stack);
				return;
			}
			// 寻找可以卖的交易信息
			for (int i = 0; i < trade.getTradeListSize(); i++) {
				if (!trade.getTradeInfo(i).isReclaim()) continue;
				ItemStack c = trade.commodity(i);
				if (ItemStack.areItemsEqual(c, stack)) {
					int count = stack.getCount();
					trade.reclaim(i, count);
					int rest = ItemElfPurse.insert(player.inventory, count * trade.cost(i), false);
					if (rest > 0) player.dropItem(ESInitInstance.ITEMS.ELF_COIN, rest);
					return;
				}
			}
			super.putStack(stack);
		}

	}

	// 货架
	protected class SlotShelves extends Slot {

		public final int slotIndex;

		public SlotShelves(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
			slotIndex = index;
		}

		@Override
		public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
			// 保持数据
			ItemStack temp = stack.copy();
			temp.setCount(1);
			putStack(temp);
			// 处理花钱
			int cost = trade.cost(slotIndex);
			ItemElfPurse.extract(thePlayer.inventory, cost, false);
			trade.sell(slotIndex, 1);
			changeTime = System.currentTimeMillis();
			return super.onTake(thePlayer, stack);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return false;
		}

		@Override
		public boolean canTakeStack(EntityPlayer playerIn) {
			if (slotIndex >= trade.getTradeListSize()) return false;
			if (trade.getTradeInfo(slotIndex).isReclaim() || trade.stock(slotIndex) <= 0) return false;
			int cost = trade.cost(slotIndex);
			if (ItemElfPurse.extract(playerIn.inventory, cost, true) > 0) return false;
			return true;
		}
	}

	/** 设置一种交易 */
	public void setTrade(Trade trade) {
		this.trade = trade;
		if (trade == null) {
			soldOutCheck = null;
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setBoolean("clear", true);
			if (elf != null) nbt.setInteger("elfId", elf.getEntityId());
			this.sendToClient(nbt, player);
			return;
		}
		NBTTagCompound nbt = trade.serializeNBT();
		if (elf != null) nbt.setInteger("elfId", elf.getEntityId());
		this.sendToClient(nbt, player);
		// 设置具体内容
		soldOutCheck = new boolean[trade.getTradeListSize()];
		shelves.clear();
		for (int i = 0; i < trade.getTradeListSize(); i++) {
			ItemStack c = trade.commodity(i);
			shelves.setInventorySlotContents(i, c.copy());
			soldOutCheck[i] = trade.stock(i) <= 0;
		}
	}

	long changeTime = System.currentTimeMillis();
	long lastChangeTime = System.currentTimeMillis();
	boolean[] soldOutCheck;

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (changeTime != lastChangeTime) {
			changeTime = lastChangeTime;
			if (soldOutCheck == null) return;
			// 检查是否要发送售空
			for (int i = 0; i < trade.getTradeListSize(); i++) {
				if (i >= soldOutCheck.length) {
					// 动态变多了！
					return;
				}
				boolean soldOut = trade.stock(i) <= 0;
				// 买完了吗，检查
				if (soldOut != soldOutCheck[i]) {
					soldOutCheck[i] = soldOut;
					NBTTagCompound nbt = new NBTTagCompound();
					if (soldOut) nbt.setShort("soldOut", (short) i);
					else nbt.setShort("filling", (short) i);
					sendToClient(nbt, player);
				}
			}
		}

	}

	@Override
	public void recvData(NBTTagCompound nbt, Side side) {
		if (side.isServer()) return;
		if (nbt.hasKey("elfId")) elf = (EntityElfBase) player.world.getEntityByID(nbt.getInteger("elfId"));
		if (nbt.hasKey("soldOut")) {
			if (this.trade instanceof TradeClient) ((TradeClient) this.trade).setSoldOut(nbt.getInteger("soldOut"));
			return;
		} else if (nbt.hasKey("filling")) {
			if (this.trade instanceof TradeClient) ((TradeClient) this.trade).setFilling(nbt.getInteger("filling"));
			return;
		}
		if (nbt.getBoolean("clear")) {
			this.trade = null;
			return;
		}
		this.trade = new TradeClient(nbt);
	}

	// 转化
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		Slot slot = inventorySlots.get(index);

		if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;

		ItemStack newStack = slot.getStack();
		ItemStack oldStack = newStack.copy();

		boolean isMerged = false;
		final int max = 36 + 1;
		if (index >= max) isMerged = false;
		else if (index < 27)
			isMerged = mergeItemStack(newStack, 36, max, false) || mergeItemStack(newStack, 27, 36, false);
		else if (index >= 27 && index < 36)
			isMerged = mergeItemStack(newStack, 36, max, false) || mergeItemStack(newStack, 0, 27, false);
		else isMerged = mergeItemStack(newStack, 0, 36, false);

		if (!isMerged) return ItemStack.EMPTY;

		if (newStack.isEmpty()) slot.putStack(ItemStack.EMPTY);
		slot.onTake(playerIn, newStack);

		return oldStack;
	}
}
