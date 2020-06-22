package yuzunyannn.elementalsorcery.tile.md;

import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.util.world.WorldTime;

public class TileMDInfusion extends TileMDBase implements ITickable {
	@Override
	protected ItemStackHandler initItemStackHandler() {
		return new ItemStackHandler(5) {
			@Override
			public int getSlotLimit(int slot) {
				return 1;
			}
		};
	}

	private int[] infusionPower = new int[5];
	private MultiBlock structure;
	private boolean ok;

	@Override
	public void onLoad() {
		structure = new MultiBlock(Buildings.INFUSION, this, new BlockPos(0, -2, 0));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setIntArray("powers", infusionPower);
		return super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.infusionPower = nbt.getIntArray("powers");
		if (this.infusionPower.length < 5) this.infusionPower = new int[5];
		super.readFromNBT(nbt);
	}

	@Override
	public int getField(int id) {
		switch (id) {
		case 0:
		case 1:
			return super.getField(id);
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
			return infusionPower[id - 2];
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
		case 3:
		case 4:
		case 5:
		case 6:
			infusionPower[id - 2] = value;
			break;
		}
	}

	@Override
	public int getFieldCount() {
		return 7;
	}

	public int getInfusionPower(int index) {
		return infusionPower[index];
	}

	public int getInfusionPowerMax(int index) {
		switch (index) {
		case 0:
		case 4:
		case 1:
		case 3:
		case 2:
			return TileMDInfusion.getOfferMagic(index).getCount() * 4;
		default:
			return 1;
		}
	}

	public final static ElementStack MAGIC20 = new ElementStack.Unchangeable(ESInitInstance.ELEMENTS.MAGIC, 20, 20);
	public final static ElementStack MAGIC40 = new ElementStack.Unchangeable(ESInitInstance.ELEMENTS.MAGIC, 40, 20);
	public final static ElementStack MAGIC60 = new ElementStack.Unchangeable(ESInitInstance.ELEMENTS.MAGIC, 60, 20);

	public static ElementStack getOfferMagic(int index) {
		switch (index) {
		case 0:
		case 4:
			return MAGIC20;
		case 1:
		case 3:
			return MAGIC40;
		case 2:
			return MAGIC60;
		default:
			return ElementStack.EMPTY;
		}
	}

	@Override
	public void update() {
		this.autoTransfer();
		if (world.isRemote) return;
		if (tick % 30 == 0) ok = structure.check(EnumFacing.NORTH);
		if (!ok) {
			this.allPowerDrop();
			return;
		}
		// 对有所物品进行遍历
		for (int i = 0; i < infusionPower.length; i++) {
			if (this.magic.isEmpty()) {
				this.powerDrop(i);
				continue;
			}
			ItemStack stack = this.inventory.getStackInSlot(i);
			ItemStack result = TileMDInfusion.infusionInto(stack, TileMDInfusion.getOfferMagic(i), world, pos);
			if (result.isEmpty()) {
				this.powerDrop(i);
				continue;
			}
			this.infusionPower[i]++;
			if (this.infusionPower[i] % 4 == 0) this.magic.shrink(1);
			if (this.infusionPower[i] >= this.getInfusionPowerMax(i)) {
				this.infusionPower[i] = 0;
				this.inventory.setStackInSlot(i, result.copy());
				this.markDirty();
			}
		}
	}

	private void allPowerDrop() {
		for (int i = 0; i < this.infusionPower.length; i++)
			this.powerDrop(i);
	}

	private final void powerDrop(int index) {
		if (this.infusionPower[index] > 0) this.infusionPower[index]--;
	}

	/**
	 * 某个物品注魔成
	 * 
	 * @param stack      原始物品
	 * @param offerMagic 提供的魔力值
	 * @return 注魔后的物品
	 * 
	 */
	static public ItemStack infusionInto(ItemStack stack, ElementStack offerMagic, World world, BlockPos pos) {
		if (stack.isEmpty()) return ItemStack.EMPTY;
		Biome biome = world.getBiome(pos);
		WorldTime time = new WorldTime(world);
		if (biome == Biomes.PLAINS) {
			if (time.at(WorldTime.Period.DAWN) || time.at(WorldTime.Period.DUSK)) {
				if (stack.getItem() == ESInitInstance.ITEMS.MAGIC_CRYSTAL && offerMagic.getCount() > 40) {
					return new ItemStack(ESInitInstance.ITEMS.ELEMENT_CRYSTAL);
				}
			}
		}
		if (time.at(WorldTime.Period.MIDNIGHT)) {
			if (stack.getItem() == ESInitInstance.ITEMS.MAGIC_CRYSTAL && offerMagic.getCount() > 20) {
				int count = 0;
				for (int y = -1; y <= 1; y++) {
					for (int x = -2; x <= 2; x++) {
						for (int z = -2; z <= 2; z++) {
							BlockPos movePos = pos.add(x, y, z);
							if (world.getBlockState(movePos).getBlock() == Blocks.BOOKSHELF) count++;
							if (count >= 14) {
								y = 3;
								x = 3;
								z = 3;
								break;
							}
						}
					}
				}
				if (count >= 14) return new ItemStack(ESInitInstance.ITEMS.SPELL_CRYSTAL);
			}
		}
		if (time.at(WorldTime.Period.DAY)) {
			if (biome.getRainfall() <= 0.5f && !world.isRaining()) {
				if (stack.getItem() == ESInitInstance.ITEMS.KYANITE && offerMagic.getCount() >= 20) {
					return new ItemStack(ESInitInstance.ITEMS.MAGIC_CRYSTAL);
				}
			}
		}
		// 这里应当加入注魔[事件]
		return ItemStack.EMPTY;
	}
}
