package yuzunyan.elementalsorcery.tile;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyan.elementalsorcery.api.ability.IElementInventory;
import yuzunyan.elementalsorcery.api.ability.IGetBurnPower;
import yuzunyan.elementalsorcery.api.ability.IGetItemStack;
import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.capability.ElementInventory;
import yuzunyan.elementalsorcery.render.particle.ParticleElement;

public class TileAbsorbBox extends TileEntityNetwork implements IGetBurnPower {

	// 积攒的能量
	private int power = 0;

	// 收获能量的槽
	protected ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		@Nonnull
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			IElementInventory einv = ElementInventory.getOrdinaryElementInventory(stack);
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
		ItemStack stack = inventory.getStackInSlot(0);
		if (stack.isEmpty()) {
			power = 0;
			return false;
		}
		IElementInventory einv = ElementInventory.getOrdinaryElementInventory(stack);
		if (einv == null)
			return false;
		if (posList.isEmpty()) {
			// 每秒计算一次，提升效率
			power--;
			if (power <= 0)
				power = 20;
			else
				return false;
			this.refindAbsorbPosList();
			if (posList.isEmpty()) {
				return false;
			}
		}
		power += amount * level;
		if (power < this.getOnceTime())
			return true;
		power -= this.getOnceTime();
		// 开始抽取
		this.refindAbsorbPosList();
		if (posList.isEmpty()) {
			power = 0;
			return false;
		}
		for (BlockPos pos : posList) {
			IGetItemStack get_stack = (IGetItemStack) world.getTileEntity(pos);
			ItemStack abs = get_stack.getStack();
			IElementInventory stack_einv = abs.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
			ElementStack estack = getFirstNotEmpty(stack_einv, ElementStack.EMPTY);
			ElementStack to_stack = estack.copy();
			to_stack.setCount(1);
			boolean success = einv.insertElement(to_stack, false);
			// 如果失败了就一个一个尝试插入
			if (!success) {
				for (int i = 0; i < einv.getSlots(); i++) {
					estack = getFirstNotEmpty(stack_einv, getFirstNotEmpty(stack_einv, einv.getStackInSlot(i)));
					to_stack = estack.copy();
					to_stack.setCount(1);
					success = einv.insertElement(to_stack, false);
					if (success)
						break;
				}
			}
			// 如果吸收成功
			if (success) {
				estack.shrink(1);
				if (world.isRemote) {
					// 生成粒子效果
					if (estack.getCount() % 3 == 1) {
						ParticleElement effect = new ParticleElement(this.world,
								new Vec3d(pos.getX() + Math.random(), pos.getY() + Math.random(),
										pos.getZ() + Math.random()),
								new Vec3d(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5));
						effect.setColor(estack.getColor());
						Minecraft.getMinecraft().effectRenderer.addEffect(effect);
					}
				} else {
					// 如果没有剩余元素了，就更新下
					if (estack.isEmpty()) {
						TileEntity tile = world.getTileEntity(pos);
						tile.markDirty();
						if (tile instanceof TileEntityNetwork) {
							((TileEntityNetwork) tile).updateToClient();
						}
					}
				}
			}
		}
		this.markDirty();
		return true;
	}

	private List<BlockPos> posList = new LinkedList<BlockPos>();
	static final int dis = 2;

	private void refindAbsorbPosList() {
		posList.clear();
		for (int x = -dis; x <= dis; x++) {
			for (int z = -dis; z <= dis; z++) {
				BlockPos pos = this.pos.add(x, -1, z);
				if (this.canAbsorb(pos)) {
					posList.add(pos);
				}
			}
		}
	}

	private boolean canAbsorb(BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof IGetItemStack) {
			ItemStack stack = ((IGetItemStack) tile).getStack();
			if (stack.isEmpty())
				return false;
			if (!stack.hasCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null))
				return false;
			IElementInventory einv = stack.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
			ElementStack estack = getFirstNotEmpty(einv, ElementStack.EMPTY);
			return !estack.isEmpty();
		}
		return false;
	}

	private ElementStack getFirstNotEmpty(IElementInventory einv, ElementStack cmp) {
		for (int i = 0; i < einv.getSlots(); i++) {
			ElementStack estack = einv.getStackInSlot(i);
			if (!estack.isEmpty() && (cmp.isEmpty() || estack.areSameType(cmp)))
				return estack;
		}
		return ElementStack.EMPTY;
	}

	public int getOnceTime() {
		return 100;
	}

	// 加载
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		this.inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
		this.power = compound.getInteger("power");
		super.readFromNBT(compound);
	}

	// 保存
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("Inventory", this.inventory.serializeNBT());
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
			return (T) inventory;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}
}
