package yuzunyannn.elementalsorcery.tile.md;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.ability.IAcceptBurnPower;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public class TileMDMagicGen extends TileMDBase implements ITickable, IAcceptBurnPower {

	/** 仓库 */
	protected ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		@Nonnull
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			if (stack.getItem() != ESInitInstance.ITEMS.MAGIC_STONE)
				return stack;
			return super.insertItem(slot, stack, simulate);
		}
	};
	/** 温度 */
	protected float temperature;

	/** 当前熔炼剩余 */
	protected float meltRate;

	/** 客户端负责绘画的物品 */
	@SideOnly(Side.CLIENT)
	public ItemStack renderItem = new ItemStack(ESInitInstance.ITEMS.MAGIC_STONE);

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		if (!this.getMagicStone().isEmpty())
			nbt.setTag("mgStone", this.getMagicStone().serializeNBT());
		nbt.setFloat("T", temperature);
		nbt.setFloat("rate", meltRate);
		return super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("mgStone"))
			this.setMagicStone(new ItemStack(nbt.getCompoundTag("mgStone")));
		this.temperature = nbt.getFloat("T");
		this.meltRate = nbt.getFloat("rate");
		super.readFromNBT(nbt);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			return (T) inventory;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void onBreak() {
		super.onBreak();
		BlockHelper.drop(inventory, world, pos);
	}

	protected ItemStack getMagicStone() {
		return this.inventory.getStackInSlot(0);
	}

	protected void setMagicStone(ItemStack stack) {
		this.inventory.setStackInSlot(0, stack);
	}

	@Override
	public int getMaxCapacity() {
		return 5000;
	}

	@Override
	protected int getOverflow() {
		return 0;
	}

	@Override
	protected int getMaxSendPreSecond() {
		return 100;
	}

	public boolean isFire() {
		return false;
	}

	@Override
	public int getFieldCount() {
		return 3;
	}

	@Override
	public int getField(int id) {
		switch (id) {
		case 0:
			return this.getCurrentCapacity();
		case 1:
			return Float.floatToIntBits(meltRate);
		case 2:
			return (int) this.temperature;
		default:
			return 0;
		}

	}

	@Override
	public void setField(int id, int value) {
		switch (id) {
		case 0:
			this.setCurrentCapacity(value);
			break;
		case 1:
			this.meltRate = Float.intBitsToFloat(value);
			break;
		case 2:
			this.temperature = value;
			break;
		default:
		}

	}

	public float getMeltRate() {
		return meltRate;
	}

	public float getTemperature() {
		return temperature;
	}

	@Override
	public boolean acceptBurnPower(int amount, int level) {
		if (this.meltRate == 0 && (this.getMagicStone().isEmpty() || this.getCurrentCapacity() >= this.getMaxCapacity())
				&& level == 0)
			return false;
		if (this.world.isRemote)
			return true;
		temperature += amount * level;
		return true;
	}

	@Override
	public void update() {
		this.autoTransfer();
		if (this.world.isRemote)
			return;
		temperature *= 0.995f;
		if (this.temperature < 400)
			return;
		if (this.meltRate > 0) {
			float dRate = 0.01f;
			float prevMeltRate = this.meltRate;
			this.meltRate -= dRate;
			if (this.meltRate <= 0.0f) {
				this.meltRate = 0.0f;
				dRate = prevMeltRate;
			}
			// 增长
			this.magic.grow((int) (dRate * 100));
			if (this.getCurrentCapacity() >= this.getMaxCapacity()) {
				this.magic.setCount(this.getMaxCapacity());
			}
		} else {
			// 如果没有熔炼，就拿走一个魔石，进行熔炼
			ItemStack stack = this.getMagicStone();
			if (stack.isEmpty())
				return;
			if (this.getCurrentCapacity() >= this.getMaxCapacity())
				return;
			stack.shrink(1);
			this.meltRate = 1.0f;
		}
	}

}
