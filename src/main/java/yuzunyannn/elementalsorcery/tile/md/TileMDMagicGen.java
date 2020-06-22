package yuzunyannn.elementalsorcery.tile.md;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.ability.IAcceptBurnPower;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInitInstance;

public class TileMDMagicGen extends TileMDBase implements ITickable, IAcceptBurnPower {

	/** 温度 */
	protected float temperature;

	/** 当前熔炼剩余 */
	protected float meltRate;

	/** 客户端负责绘画的物品 */
	@SideOnly(Side.CLIENT)
	public ItemStack renderItem = new ItemStack(ESInitInstance.ITEMS.MAGIC_STONE);

	/** 初始化仓库 */
	@Override
	protected ItemStackHandler initItemStackHandler() {
		return new ItemStackHandler(1) {
			@Override
			@Nonnull
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				if (stack.getItem() != ESInitInstance.ITEMS.MAGIC_STONE) return stack;
				return super.insertItem(slot, stack, simulate);
			}
		};
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setFloat("T", temperature);
		nbt.setFloat("rate", meltRate);
		return super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.temperature = nbt.getFloat("T");
		this.meltRate = nbt.getFloat("rate");
		super.readFromNBT(nbt);
	}

	protected ItemStack getMagicStone() {
		return this.inventory.getStackInSlot(0);
	}

	protected void setMagicStone(ItemStack stack) {
		this.inventory.setStackInSlot(0, stack);
	}

	@Override
	public int getMaxCapacity() {
		return 2000;
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
		return temperature > 20;
	}

	@Override
	public int getFieldCount() {
		return 4;
	}

	@Override
	public int getField(int id) {
		switch (id) {
		case 0:
		case 1:
			return super.getField(id);
		case 2:
			return Float.floatToIntBits(meltRate);
		case 3:
			return (int) this.temperature;
		default:
			return 0;
		}

	}

	@Override
	public void setField(int id, int value) {
		switch (id) {
		case 0:
		case 1:
			super.setField(id, value);
			break;
		case 2:
			this.meltRate = Float.intBitsToFloat(value);
			break;
		case 3:
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
		if (this.world.isRemote) return true;
		temperature += amount * level;
		return true;
	}

	@Override
	public void update() {
		this.autoTransfer();
		if (this.world.isRemote) return;
		temperature *= 0.995f;
		if (this.temperature < 400) return;
		if (this.meltRate > 0) {
			float dRate = 0.01f;
			float prevMeltRate = this.meltRate;
			this.meltRate -= dRate;
			if (this.meltRate <= 0.0f) {
				this.meltRate = 0.0f;
				dRate = prevMeltRate;
			}
			// 增长
			if (this.magic.isEmpty())
				this.magic = new ElementStack(ESInitInstance.ELEMENTS.MAGIC, (int) (dRate * 100), 25);
			else this.magic.grow(new ElementStack(ESInitInstance.ELEMENTS.MAGIC, (int) (dRate * 100), 25));
			if (this.getCurrentCapacity() >= this.getMaxCapacity()) this.magic.setCount(this.getMaxCapacity());
		} else {
			// 如果没有熔炼，就拿走一个魔石，进行熔炼
			ItemStack stack = this.getMagicStone();
			if (stack.isEmpty()) return;
			if (this.getCurrentCapacity() >= this.getMaxCapacity()) return;
			stack.shrink(1);
			this.meltRate = 1.0f;
		}
	}

}
