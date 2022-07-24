package yuzunyannn.elementalsorcery.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.network.MessageSyncContainer.IContainerNetwork;
import yuzunyannn.elementalsorcery.tile.TileItemStructureCraftCC;
import yuzunyannn.elementalsorcery.util.NBTTag;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ContainerItemStructureCraftCC extends ContainerNormal<TileItemStructureCraftCC>
		implements IContainerNetwork {

	public static final byte OP_REMOVE_SAME = 0;
	public static final byte OP_REMOVE = 1;
	public static final byte OP_REMOVE_HALF = 2;
	public static final byte OP_ADD = 3;
	public static final byte OP_ADD_HALF = 4;
	public static final byte OP_TSTACK = 10;

	public static final byte SP_OUTPUT = 1;
	public static final byte SP_TYPE_STACK = 2;

	public ContainerItemStructureCraftCC(EntityPlayer player, TileEntity tileEntity) {
		super(player, (TileItemStructureCraftCC) tileEntity, 36, 159);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public void recvData(NBTTagCompound nbt, Side side) {
		if (side == Side.CLIENT) {
			if (nbt.hasKey("cl")) {
				NBTTagList changList = nbt.getTagList("cl", NBTTag.TAG_COMPOUND);
				for (int i = 0; i < changList.tagCount(); i++) {
					NBTTagCompound tag = changList.getCompoundTagAt(i);
					ItemStack stack = NBTHelper.deserializeItemStackFromSend(tag);
					if (tag.hasKey("_Sp")) {
						int sp = tag.getInteger("_Sp");
						if (sp == SP_OUTPUT) this.tileEntity.setOutput(stack);
						else if (sp == SP_TYPE_STACK) this.tileEntity.setTypeStack(stack);
					} else {
						int index = tag.getInteger("_Sl");
						this.tileEntity.setSlotItemStack(index, stack);
					}
				}
				return;
			}
			this.tileEntity.handleUpdateTagFromPacketData(nbt);
			return;
		}

		int slotIndex = nbt.getInteger("si");
		int op = nbt.getInteger("op");
		dealSlotOP(slotIndex, op);
	}

	public void dealSlotOP(int slotIndex, int op) {
		switch (op) {
		case OP_REMOVE_SAME: {
			ItemStack itemStack = tileEntity.getSlotItemStack(slotIndex);
			if (itemStack.isEmpty()) break;
			Map<Integer, ItemStack> slotMap = this.tileEntity.getSlotMap();
			for (Entry<Integer, ItemStack> entry : slotMap.entrySet()) {
				if (ItemHelper.areItemsEqual(entry.getValue(), itemStack))
					tileEntity.setSlotItemStack(entry.getKey(), ItemStack.EMPTY);
			}
			break;
		}
		case OP_REMOVE: {
			ItemStack itemStack = tileEntity.getSlotItemStack(slotIndex);
			if (itemStack.isEmpty()) return;
			tileEntity.setSlotItemStack(slotIndex, ItemStack.EMPTY);
			break;
		}
		case OP_REMOVE_HALF: {
			ItemStack itemStack = tileEntity.getSlotItemStack(slotIndex);
			if (itemStack.isEmpty()) return;
			itemStack.setCount(itemStack.getCount() / 2);
			tileEntity.setSlotItemStack(slotIndex, itemStack);
			break;
		}
		case OP_ADD_HALF:
		case OP_ADD: {
			ItemStack playerHold = TileItemStructureCraftCC.getRealItemStack(player.inventory.getItemStack()).copy();
			if (playerHold.isEmpty()) return;
			if (op == OP_ADD_HALF) playerHold.setCount(Math.max(1, playerHold.getCount() / 2));
			ItemStack itemStack = tileEntity.getSlotItemStack(slotIndex);
			if (itemStack.isEmpty()) tileEntity.setSlotItemStack(slotIndex, playerHold);
			else {
				if (ItemHelper.areItemsEqual(playerHold, itemStack)) {
					int count = itemStack.getCount() + playerHold.getCount();
					itemStack.setCount(Math.min(count, itemStack.getMaxStackSize()));
					tileEntity.setSlotItemStack(slotIndex, itemStack);
				} else tileEntity.setSlotItemStack(slotIndex, playerHold);
			}
			break;
		}
		case OP_TSTACK: {
			ItemStack playerHold = player.inventory.getItemStack().copy();
			tileEntity.updateTypeStackWithItem(playerHold);
			break;
		}
		}
	}

	public String lastSlotMapName = "";
	public Map<Integer, ItemStack> slotMapChange = new HashMap<>();
	public ItemStack outputChange = ItemStack.EMPTY;
	public ItemStack typeStackChange = TileItemStructureCraftCC.defaultTypeStack;

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (!lastSlotMapName.equals(this.tileEntity.getTypeName())) {
			lastSlotMapName = this.tileEntity.getTypeName();
			slotMapChange = new HashMap<>();
			NBTTagCompound dat = this.tileEntity.getUpdateTagForUpdateToClient();
			this.sendToClient(dat, listeners);
			// 全部更新了，所以没有change了
			slotMapChange.clear();
			Map<Integer, ItemStack> slotMap = this.tileEntity.getSlotMap();
			for (Entry<Integer, ItemStack> entry : slotMap.entrySet()) {
				slotMapChange.put(entry.getKey(), entry.getValue().copy());
			}
			return;
		}

		Map<Integer, ItemStack> slotMap = this.tileEntity.getSlotMap();
		NBTTagList changList = new NBTTagList();
		for (Entry<Integer, ItemStack> entry : slotMap.entrySet()) {
			ItemStack itemStack1 = entry.getValue();
			ItemStack itemStack2 = slotMapChange.get(entry.getKey());
			if (itemStack2 == null) itemStack2 = ItemStack.EMPTY;
			if (!ItemStack.areItemStacksEqual(itemStack1, itemStack2)) {
				slotMapChange.put(entry.getKey(), itemStack1.copy());
				NBTTagCompound tag = NBTHelper.serializeItemStackForSend(itemStack1);
				tag.setInteger("_Sl", entry.getKey());
				changList.appendTag(tag);
			}
		}

		if (!ItemStack.areItemStacksEqual(outputChange, this.tileEntity.getOutput())) {
			outputChange = this.tileEntity.getOutput().copy();
			NBTTagCompound tag = NBTHelper.serializeItemStackForSend(outputChange);
			tag.setByte("_Sp", SP_OUTPUT);
			changList.appendTag(tag);
		}

		if (!ItemStack.areItemStacksEqual(typeStackChange, this.tileEntity.getTypeStack())) {
			typeStackChange = this.tileEntity.getTypeStack().copy();
			NBTTagCompound tag = NBTHelper.serializeItemStackForSend(typeStackChange);
			tag.setByte("_Sp", SP_TYPE_STACK);
			changList.appendTag(tag);
		}

		if (!changList.isEmpty()) {
			NBTTagCompound dat = new NBTTagCompound();
			dat.setTag("cl", changList);
			this.sendToClient(dat, listeners);
		}

	}

}
