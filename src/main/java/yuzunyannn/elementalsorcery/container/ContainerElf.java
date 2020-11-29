package yuzunyannn.elementalsorcery.container;

import java.util.LinkedList;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.event.EventServer;
import yuzunyannn.elementalsorcery.util.NBTHelper;
import yuzunyannn.elementalsorcery.util.NBTTag;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public abstract class ContainerElf extends Container {
	public final BlockPos pos;
	/** 交互的玩家 */
	public final EntityPlayer player;
	/** 交互的精灵，存在null的可能 */
	public EntityElfBase elf;
	/** 是否关闭 */
	protected boolean noEnd = true;

	public ContainerElf(EntityPlayer player) {
		this.player = player;
		NBTTagCompound nbt = ElementalSorcery.getPlayerData(player);
		Entity elf = null;
		if (player.world.isRemote) elf = null;
		else elf = (EntityElfBase) player.world.getEntityByID(nbt.getInteger("elfId"));
		nbt.removeTag("elfId");
		if (elf instanceof EntityElfBase) this.elf = (EntityElfBase) elf;
		this.pos = this.elf == null ? player.getPosition() : elf.getPosition();
		if (this.elf != null) this.elf.setTalker(player);
		if (nbt.hasKey("shiftData", NBTTag.TAG_COMPOUND)) {
			NBTTagCompound shiftData = nbt.getCompoundTag("shiftData");
			this.onShift(shiftData);
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		if (this.elf != null) this.elf.setTalker(null);
		NBTTagCompound playerData = EventServer.getPlayerNBT(player);
		if (playerData.hasKey("elfGuiItemBack", NBTTag.TAG_LIST)) {
			LinkedList<ItemStack> items = NBTHelper.getItemList(playerData, "elfGuiItemBack");
			playerData.removeTag("elfGuiItemBack");
			for (ItemStack stack : items) ItemHelper.addItemStackToPlayer(player, stack);
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		boolean flag;
		if (elf == null) flag = playerIn.getDistanceSq(pos) <= 64;
		else flag = playerIn.getDistanceSq(this.elf) <= 64 && !elf.isDead;
		return noEnd && flag;
	}

	void addPlayerSlot(int xoff, int yoff) {
		// 玩家物品栏
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, xoff + j * 18, yoff + i * 18));
			}
		}
		// 玩家工具栏
		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(player.inventory, i, xoff + i * 18, yoff + 58));
		}
	}

	/** 当切换 */
	public void onShift(NBTTagCompound shiftData) {

	}

	/** gui切换时保存的数据记录 */
	@Nullable
	public NBTTagCompound getShiftData() {
		return null;
	}

	/** 更换ui */
	public void changeUI(int modGuiId) {
		noEnd = false;
		if (elf != null) {
			NBTTagCompound nbt = ElementalSorcery.getPlayerData(player);
			nbt.setInteger("elfId", elf.getEntityId());
			NBTTagCompound shift = this.getShiftData();
			if (nbt != null) nbt.setTag("shiftData", shift);
		}
		player.closeScreen();
		BlockPos pos = player.getPosition();
		player.openGui(ElementalSorcery.instance, modGuiId, player.world, pos.getX(), pos.getY(), pos.getZ());
	}

}
