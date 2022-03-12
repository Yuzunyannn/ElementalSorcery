package yuzunyannn.elementalsorcery.tile.md;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.tile.IGetItemStack;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.tile.altar.TileElementalCube;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;

public class TileMDAbsorbBox extends TileMDBase implements ITickable {

	@Override
	protected ItemStackHandler initItemStackHandler() {
		return new ItemStackHandler(1) {
			@Override
			@Nonnull
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				if (TileMDAbsorbBox.getElementInventory(stack) != null) return super.insertItem(slot, stack, simulate);
				return stack;
			}
		};
	}

	protected MultiBlock structure;
	protected boolean ok;
	private BlockPos scanPos;

	@Override
	public void onLoad() {
		structure = new MultiBlock(Buildings.ABSORB_BOX, this, new BlockPos(0, -2, 0));
	}

	@Override
	public void update() {
		this.autoTransfer();
		ItemStack stack = inventory.getStackInSlot(0);
		IElementInventory inventory = getElementInventory(stack);
		if (!ElementHelper.canInsert(inventory)) return;
		if (inventory == null) return;
		if (tick % 30 == 0) ok = structure.check(EnumFacing.NORTH);
		if (ok == false) return;
		// 消耗
		final int need = 10;
		if (need > this.getCurrentCapacity()) return;
		// 开始扫描
		if (scanPos == null) scanPos = new BlockPos(-2, -1, -2);
		BlockPos curPos = this.pos.add(scanPos);
		if (this.canAbsorb(curPos)) {
			ItemStack itemStack = ((IGetItemStack) world.getTileEntity(curPos)).getStack();
			IElementInventory itemEinv = getElementInventory(itemStack);
			// 寻找一个可以插入的
			for (int i = 0; i < itemEinv.getSlots(); i++) {
				ElementStack estack = itemEinv.getStackInSlot(i);
				if (estack.isEmpty()) continue;
				estack = estack.copy();
				estack.setCount(1);
				// 测试是否可以插入
				if (inventory.insertElement(estack, true)) {
					this.magic.shrink(need);
					if (world.isRemote) {
						// 生成粒子效果
						Vec3d from = new Vec3d(curPos).add(0.5, 0.5, 0.5);
						Vec3d to = new Vec3d(pos).add(0.5, 0.5, 0.5);
						TileElementalCube.giveParticleElementTo(world, estack.getColor(), from, to, 1.0f);
					} else {
						// 记录真实数据
						inventory.insertElement(estack, false);
						inventory.saveState(stack);
						itemEinv.getStackInSlot(i).shrink(1);
						itemEinv.saveState(itemStack);
						this.markDirty();
					}
					break;
				}
			}
		}
		// 移动扫描
		scanPos = scanPos.add(1, 0, 0);
		if (scanPos.getX() > 2) {
			scanPos = new BlockPos(-2, -1, scanPos.getZ() + 1);
			if (scanPos.getZ() > 2) scanPos = null;
		}
	}

	// 某个位置上是否有元素可以吸收
	protected boolean canAbsorb(BlockPos pos) {
		if (world.isAirBlock(pos)) return false;
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof IGetItemStack) {
			ItemStack stack = ((IGetItemStack) tile).getStack();
			if (stack.isEmpty()) return false;
			IElementInventory inventory = getElementInventory(stack);
			return !ElementHelper.isEmpty(inventory);
		}
		return false;
	}

	protected static IElementInventory getElementInventory(ItemStack itemStack) {
		IElementInventory itemEinv = ElementHelper.getElementInventory(itemStack);
		if (itemEinv != null) return itemEinv;
		itemEinv = new ElementInventory();
		if (itemEinv.hasState(itemStack)) {
			itemEinv.loadState(itemStack);
			return itemEinv;
		}
		return null;
	}

}
