package yuzunyan.elementalsorcery.tile;

import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyan.elementalsorcery.api.ability.IElementInventory;
import yuzunyan.elementalsorcery.api.ability.IGetBurnPower;
import yuzunyan.elementalsorcery.api.element.Element;
import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.api.util.ElementHelper;
import yuzunyan.elementalsorcery.element.ElementMap;
import yuzunyan.elementalsorcery.util.IField;

public class TileDeconstructBox extends TileEntity implements IGetBurnPower, IField {

	static final Random rand = new Random();

	// 能量积攒
	private int power;
	// 任何的物品槽
	protected ItemStackHandler inv_item = new ItemStackHandler(1);
	// 收获能量的槽
	protected ItemStackHandler inv_eleitem = new ItemStackHandler(1) {
		@Override
		@Nonnull
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			IElementInventory einv = ElementHelper.getElementInventoryCanInsert(stack);
			if (einv == null)
				return stack;
			return super.insertItem(slot, stack, simulate);
		}

		@Override
		public int getSlotLimit(int slot) {
			return 1;
		}
	};

	@Override
	public boolean receiveBurnPower(int amount, int level) {
		if (world.isRemote)
			return false;
		ItemStack stack = inv_item.getStackInSlot(0);
		if (stack.isEmpty()) {
			power = 0;
			return false;
		}
		ItemStack stack_ele = inv_eleitem.getStackInSlot(0);
		IElementInventory einv = ElementHelper.getElementInventoryCanInsert(stack_ele);
		if (einv == null) {
			power = 0;
			return false;
		}
		if (this.insertTo(stack, einv, true) == false) {
			power = 0;
			return false;
		}
		this.power += amount * level;
		int max_power = this.getField(TileDeconstructBox.FIELD_TOTAL_POWER);
		if (this.power >= max_power) {
			this.insertTo(stack, einv, false);
			stack.shrink(1);
			this.power = 0;
			this.markDirty();
		}
		return true;
	}

	private boolean insertTo(ItemStack stack, IElementInventory einv, boolean simulate) {
		ElementStack[] estacks = ElementMap.instance.toElement(stack);
		if (estacks == null)
			return false;
		if (simulate) {
			// 寻找一个能插入的
			boolean can_insert = false;
			for (ElementStack estack : estacks) {
				estack = estack.copy().getElementWhenDeconstruct(stack, ElementMap.instance.complex(stack),
						Element.DP_BOX);
				can_insert = einv.insertElement(estack, simulate) || can_insert;
			}
			return can_insert;
		} else {
			// 最终结果，随机插入一个，能不能是有的，看运气喽
			int index = rand.nextInt(estacks.length);
			ElementStack estack = estacks[index];
			estack = estack.copy().getElementWhenDeconstruct(stack, ElementMap.instance.complex(stack), Element.DP_BOX);
			einv.insertElement(estack, simulate);
			// 掉落剩余物品
			ItemStack remain = ElementMap.instance.remain(stack);
			if (!remain.isEmpty()) {
				blockType.spawnAsEntity(world, pos, remain);
			}
			return true;
		}
	}

	// 加载
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		this.inv_item.deserializeNBT(compound.getCompoundTag("inv_item"));
		this.inv_eleitem.deserializeNBT(compound.getCompoundTag("inv_eleitem"));
		this.power = compound.getInteger("power");
		super.readFromNBT(compound);
	}

	// 保存
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("inv_item", this.inv_item.serializeNBT());
		compound.setTag("inv_eleitem", this.inv_eleitem.serializeNBT());
		compound.setInteger("power", this.power);
		return super.writeToNBT(compound);
	}

	// 拥有能力
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	// 获取能力
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			if (facing == EnumFacing.DOWN)
				return (T) inv_eleitem;
			return (T) inv_item;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public static final int FIELD_POWER = 0;
	public static final int FIELD_TOTAL_POWER = 1;

	@Override
	public int getField(int id) {
		switch (id) {
		case FIELD_POWER:
			return this.power;
		case FIELD_TOTAL_POWER:
			return 150;
		}
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		switch (id) {
		case FIELD_POWER:
			this.power = value;
			break;
		}
	}

	@Override
	public int getFieldCount() {
		return 2;
	}

}
