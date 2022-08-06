package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.item.ItemRiteManual;
import yuzunyannn.elementalsorcery.summon.recipe.SummonRecipe;
import yuzunyannn.elementalsorcery.tile.TileRiteTable;
import yuzunyannn.elementalsorcery.util.item.ItemStackHandlerInventory;

public class ContainerRiteManual extends Container {

	public IInventory slot = new ItemStackHandlerInventory(1);
	public final World world;
	public final BlockPos pos;
	public final EntityPlayer player;

	private int lPower = 0;
	private int lLevel = 0;
	public int power = 0;
	public int level = 0;

	private boolean lSummonShow = false;
	public boolean summonShow = false;

	private boolean needCheckSummon = false;

	public ContainerRiteManual(EntityPlayer player) {
		this.world = player.world;
		this.pos = player.getPosition();
		this.player = player;

		final int xoff = 48;
		final int yoff = 90;
		// 玩家背包
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, xoff + j * 18, yoff + i * 18));
			}
		}
		// 玩家工具栏
		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(player.inventory, i, xoff + i * 18, 58 + yoff));
		}

		// 查询槽
		this.addSlotToContainer(new Slot(slot, 0, 70, 21) {
			@Override
			public void onSlotChanged() {
				super.onSlotChanged();
				if (player.world.isRemote) return;
				ItemStack stack = this.inventory.getStackInSlot(0);
				if (stack.isEmpty()) {
					power = 0;
					summonShow = false;
					return;
				}
				if (needCheckSummon) summonShow = SummonRecipe.findRecipeWithKeepsake(stack, world, pos) != null;
				int power = TileRiteTable.sacrifice.getPower(stack);
				ContainerRiteManual.this.power = power;
				if (power == 0) return;
				ContainerRiteManual.this.level = TileRiteTable.sacrifice.getLevel(stack);

				ItemStack quill = findQuill();
				if (quill.isEmpty()) return;
				ItemStack manual = findRiteManual();
				ItemRiteManual.addRecord(manual, stack, ContainerRiteManual.this.level, ContainerRiteManual.this.power);
			}
		});

		if (!world.isRemote) this.checkAdvancement();
	}

	protected ItemStack findQuill() {
		ItemStack stack = this.player.getHeldItem(EnumHand.OFF_HAND);
		if (stack.getItem() == ESObjects.ITEMS.QUILL) return stack;
		stack = this.player.getHeldItem(EnumHand.MAIN_HAND);
		if (stack.getItem() == ESObjects.ITEMS.QUILL) return stack;
		return ItemStack.EMPTY;
	}

	protected ItemStack findRiteManual() {
		return ItemRiteManual.findRiteManual(player);
	}

	public void checkAdvancement() {
//		WorldServer worldServer = (WorldServer) world;
//		AdvancementManager am = worldServer.getAdvancementManager();
//		Advancement adv = am.getAdvancement(new ResourceLocation(ElementalSorcery.MODID, "elf/hurt_by_crazy"));
//		EntityPlayerMP playerMP = (EntityPlayerMP) player;
//		AdvancementProgress ap = playerMP.getAdvancements().getProgress(adv);
		needCheckSummon = true;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (lPower != power) {
			lPower = power;
			for (int j = 0; j < listeners.size(); ++j) listeners.get(j).sendWindowProperty(this, 0, power);
		}
		if (lLevel != level) {
			lLevel = level;
			for (int j = 0; j < listeners.size(); ++j) listeners.get(j).sendWindowProperty(this, 1, level);
		}
		if (lSummonShow != summonShow) {
			lSummonShow = summonShow;
			for (int j = 0; j < listeners.size(); ++j) listeners.get(j).sendWindowProperty(this, 2, summonShow ? 1 : 0);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		if (id == 0) power = data;
		else if (id == 1) level = data;
		else if (id == 2) summonShow = data == 0 ? false : true;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return playerIn.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D,
				(double) this.pos.getZ() + 0.5D) <= 64.0D;
	}

	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		if (!this.world.isRemote) this.clearContainer(playerIn, this.world, this.slot);
	}

	public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
		return slotIn.inventory != this.slot;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		Slot slot = inventorySlots.get(index);

		if (slot == null || !slot.getHasStack()) { return ItemStack.EMPTY; }

		ItemStack new_stack = slot.getStack();
		ItemStack old_stack = new_stack.copy();

		boolean isMerged = false;
		final int max = 36 + 1;
		if (index < 27)
			isMerged = mergeItemStack(new_stack, 36, max, false) || mergeItemStack(new_stack, 27, 36, false);
		else if (index >= 27 && index < 36)
			isMerged = mergeItemStack(new_stack, 36, max, false) || mergeItemStack(new_stack, 0, 27, false);
		else isMerged = mergeItemStack(new_stack, 0, 36, false);

		if (!isMerged) { return ItemStack.EMPTY; }

		if (new_stack.isEmpty()) slot.putStack(ItemStack.EMPTY);
		slot.onTake(playerIn, new_stack);

		return old_stack;
	}

}
