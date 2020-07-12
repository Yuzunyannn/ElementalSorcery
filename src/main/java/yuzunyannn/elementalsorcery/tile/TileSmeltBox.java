package yuzunyannn.elementalsorcery.tile;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.tile.IAcceptBurnPower;
import yuzunyannn.elementalsorcery.block.container.BlockHearth;
import yuzunyannn.elementalsorcery.block.container.BlockSmeltBox;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.item.ItemKyaniteTools;
import yuzunyannn.elementalsorcery.util.IField;
import yuzunyannn.elementalsorcery.util.item.ItemStackLimitHandler;

public class TileSmeltBox extends TileEntity implements IAcceptBurnPower, ITickable, IField {

	public TileSmeltBox() {

	}

	// 下一次更新的能量
	protected int nextPower = 0;
	// 是否允许插入

	// 左边两个炼制位置
	protected ItemStackHandler inventorySmelt = new ItemStackHandler(2);
	// 右边三个完成位置
	protected ItemStackLimitHandler inventoryResult = new ItemStackLimitHandler(3) {
		@Override
		public boolean canInsert(int slot, @Nonnull ItemStack stack) {
			return false;
		}
	};
	// 特殊位置
	protected ItemStackHandler inventoryExtra = new ItemStackHandler(1);

	// 可以被加工
	private boolean canInSmelt = false;

	// 燃烧时间
	protected int burnTime = 0;

	// 上次是否提供过燃烧能量
	private boolean hasNextPower = false;

	// 拥有带魔法的末影之眼个数，没有-1
	protected int hasMagicalEnderEye = -1;

	// 获取材质
	private BlockHearth.EnumMaterial material() {
		try {
			BlockSmeltBox block = (BlockSmeltBox) world.getBlockState(pos).getBlock();
			return block.material;
		} catch (ClassCastException ex) {

		}
		return BlockHearth.EnumMaterial.COBBLESTONE;
	}

	// 加载
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.inventorySmelt.deserializeNBT(compound.getCompoundTag("InventorySmelt"));
		this.inventoryResult.deserializeNBT(compound.getCompoundTag("InventoryResult"));
		this.inventoryExtra.deserializeNBT(compound.getCompoundTag("InventoryExtra"));
		this.burnTime = compound.getInteger("BurnTime");
	}

	// 保存
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("InventorySmelt", this.inventorySmelt.serializeNBT());
		compound.setTag("InventoryResult", this.inventoryResult.serializeNBT());
		compound.setTag("InventoryExtra", this.inventoryExtra.serializeNBT());
		compound.setInteger("BurnTime", this.burnTime);
		return super.writeToNBT(compound);
	}

	// 受到能量
	@Override
	public boolean acceptBurnPower(int amount, int level) {
		nextPower = amount * level;
		if (hasMagicalEnderEye != -1) {
			nextPower = (int) (nextPower * (1.0F + hasMagicalEnderEye / 4.0F));
		}
		return canInSmelt;
	}

	/**
	 * 是否可以存放额外的物品 额外的物品类似于催化剂类的东西=.=
	 */
	public boolean canUseExtraItem() {
		return material().ordinal() > BlockHearth.EnumMaterial.COBBLESTONE.ordinal();
	}

	/** 获取额外物品的ItemStackHandler */
	public ItemStackHandler getExtraItemStackHandler() {
		return inventoryExtra;
	}

	/**
	 * 获得物品烧炼后应该成为的物品 因为该炉子什么都可以放置
	 * 
	 * @param stack 原始物品
	 * @param extra 额外的物品
	 */
	public @Nonnull ItemStack getSmeltResult(@Nonnull ItemStack stack, @Nonnull ItemStack extra) {
		if (stack.isEmpty()) return ItemStack.EMPTY;
		// 特殊
		if (!extra.isEmpty()) {
			// 如果额外物品是魔法末影之眼
			if (extra.getItem() == ESInitInstance.ITEMS.MAGICAL_ENDER_EYE) {
				int power = extra.getCount();
				// 如果有8个以上的魔法末影之眼，就可以直接把珍珠或者眼睛烧成带有魔力的末影之眼
				if ((stack.getItem() == Items.ENDER_EYE || stack.getItem() == Items.ENDER_PEARL) && power >= 8) {
					return new ItemStack(ESInitInstance.ITEMS.MAGICAL_ENDER_EYE, 1);
				}
				// 如果有3个以上的魔法末影之眼，可以将蓝晶石工具变成可以吸收元素的工具
				if (power >= 3 && stack.getItem() instanceof ItemKyaniteTools.toolsCapability
						&& stack.getItemDamage() == 0
						&& !stack.hasCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null)) {
					stack = new ItemStack(stack.getItem(), 1);
					((ItemKyaniteTools.toolsCapability) stack.getItem()).provide(stack);
					return stack;
				}
			}
		}
		// 检查一般的熔炼表
		ItemStack newStack = FurnaceRecipes.instance().getSmeltingResult(stack);
		if (newStack.isEmpty()) return ItemStack.EMPTY;
		return newStack.copy();
	}

	/**
	 * 获得物品烧炼后应的幸运产出物品物品 因为该炉子什么都可以放置
	 * 
	 * @param stackOld 原始物品
	 * @param stackNew 烧制之后的物品
	 * @param extra    额外的物品
	 */
	public @Nonnull ItemStack getAdditionalItem(@Nonnull ItemStack stackOld, @Nonnull ItemStack stackNew,
			@Nonnull ItemStack extra) {
		if (stackNew.getItem() == ESInitInstance.ITEMS.KYANITE) {
			float add = 0.0f;
			// 额外物品是末影之眼+概率
			if (extra.getItem() == Items.ENDER_EYE) add = 0.25f;
			if (extra.getItem() == ESInitInstance.ITEMS.MAGICAL_ENDER_EYE) add = 0.5f;
			switch (material()) {
			case IRON:
				if (Math.random() < 0.10f + add) return new ItemStack(ESInitInstance.ITEMS.MAGIC_PIECE);
				break;
			case KYANITE:
				if (Math.random() < 0.50f + add) return new ItemStack(ESInitInstance.ITEMS.MAGIC_PIECE);
				break;
			default:
				break;
			}
		} else if (extra.getItem() == ESInitInstance.ITEMS.MAGICAL_ENDER_EYE) {

		}
		// 有一定概率会出小颗粒， 蓝晶石箱子一定会出
		if (Math.random() < 0.25f || material() == BlockHearth.EnumMaterial.KYANITE) {
			if (stackNew.getItem() == Items.IRON_INGOT) {
				return new ItemStack(Items.IRON_NUGGET, 1);
			} else if (stackNew.getItem() == Items.GOLD_INGOT) { return new ItemStack(Items.GOLD_NUGGET, 1); }
		}
		return ItemStack.EMPTY;
	}

	/**
	 * 完成一轮烧炼，左边的物品变道右边是一轮，这时候可能就会消耗"额外物品"喽
	 */
	public void finishOnceSmelt(@Nonnull ItemStack extra) {
		if (extra.isEmpty()) return;
		if (extra.getItem() == ESInitInstance.ITEMS.MAGICAL_ENDER_EYE) {
			if (extra.attemptDamageItem(1, world.rand, null)) {
				extra.shrink(1);
			}
		}
	}

	/**
	 * 获取烧炼时间
	 */
	public int getCookTime() {
		// 普通熔炉是200，这个炼制箱可以一下烧两个，所以时间*2-50如果并行的话，比普通熔炉好一些
		return 350;
	}

	// 拥有能力
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) { return true; }
		return super.hasCapability(capability, facing);
	}

	// 获取能力
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			IBlockState state = world.getBlockState(pos);
			EnumFacing wface;
			if (state.getBlock() == ESInitInstance.BLOCKS.SMELT_BOX)
				wface = state.getValue(BlockSmeltBox.FACING).getOpposite();
			else wface = EnumFacing.DOWN;
			if (facing == EnumFacing.DOWN || facing == wface) return (T) inventoryResult;
			return (T) inventorySmelt;
		}
		return super.getCapability(capability, facing);
	}

	// 设置燃烧
	private void brunIt(boolean burn) {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof BlockSmeltBox)
			world.setBlockState(pos, state.withProperty(BlockSmeltBox.BURNING, burn));
	}

	// 是否可以熔炼
	public boolean canSmelt() {
		boolean can_smelt = false;
		for (int i = 0; i < inventorySmelt.getSlots(); i++) {
			ItemStack item_stack = inventorySmelt.extractItem(i, 1, true);
			item_stack = getSmeltResult(item_stack, inventoryExtra.getStackInSlot(0));
			if (item_stack.isEmpty()) continue;
			item_stack = inventoryResult.insertItemForce(i, item_stack, true);
			if (!item_stack.isEmpty()) continue;
			can_smelt = true;
			break;
		}
		return can_smelt;
	}

	// 特效用，没啥用
	@SideOnly(Side.CLIENT)
	public void tick() {

	}

	@Override
	public void update() {
		if (world.isRemote) {
			tick();
			return;
		}
		// 切换燃烧状态
		if (nextPower > 0) {
			if (hasNextPower == false) {
				hasNextPower = true;
				brunIt(hasNextPower);
			}
		} else if (hasNextPower == true) {
			hasNextPower = false;
			brunIt(hasNextPower);
		}
		// 判断是否可以继续烧炼
		canInSmelt = this.canSmelt();
		// 燃烧
		int thisPower = nextPower;
		nextPower = 0;
		if (canInSmelt) {
			if (thisPower > 0) {
				burnTime += thisPower;
				if (burnTime >= this.getCookTime()) {
					burnTime = 0;
					ItemStack extra = inventoryExtra.getStackInSlot(0);
					for (int i = 0; i < inventorySmelt.getSlots(); i++) {
						ItemStack item_stack = inventorySmelt.extractItem(i, 1, true);
						// 根据额外物品，获取烧炼结果
						ItemStack new_stack = getSmeltResult(item_stack, extra);
						if (new_stack.isEmpty()) continue;
						// 检测有没有额外的物品
						ItemStack add_stack = getAdditionalItem(item_stack, new_stack, extra);
						// 处理物品栏
						inventorySmelt.extractItem(i, 1, false);
						inventoryResult.insertItemForce(i, new_stack, false);
						inventoryResult.insertItemForce(2, add_stack, false);
					}
					// 这里就完成一轮烧炼喽
					finishOnceSmelt(extra);
					this.markDirty();
				}
			} else {
				if (burnTime > 0) burnTime--;
			}
		} else {
			burnTime = 0;
		}
		// 检查是否有带有魔力的末影之眼
		ItemStack extra = inventoryExtra.getStackInSlot(0);
		if (!extra.isEmpty() && extra.getItem() == ESInitInstance.ITEMS.MAGICAL_ENDER_EYE)
			hasMagicalEnderEye = extra.getCount();
	}

	public static final int FIELD_BURN_TIME = 0;

	@Override
	public int getField(int id) {
		switch (id) {
		case FIELD_BURN_TIME:
			return burnTime;
		}
		return -1;
	}

	@Override
	public void setField(int id, int value) {
		switch (id) {
		case FIELD_BURN_TIME:
			burnTime = value;
			break;
		}
	}

	@Override
	public int getFieldCount() {
		return 1;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	// 获取对应方块的名字
	public String getBlockUnlocalizedName() {
		return "tile.smeltBox." + material().getName() + ".name";
	}

}
