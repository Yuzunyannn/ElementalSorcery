package yuzunyannn.elementalsorcery.tile;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.ability.IGetBurnPower;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;
import yuzunyannn.elementalsorcery.util.world.WorldTime;

public class TileInfusionBox extends TileEntity implements IGetBurnPower {

	// 积攒能量
	private int power = 0;
	// 积攒的魔力能量
	private int magic_power = 0;

	// 魔力碎片进入槽
	protected ItemStackHandler piece_in = new ItemStackHandler(1) {
		@Override
		@Nonnull
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			if (stack.getItem() != ESInitInstance.ITEMS.MAGICAL_PIECE)
				return stack;
			return super.insertItem(slot, stack, simulate);
		}

	};
	// 转化槽
	protected ItemStackHandler translate = new ItemStackHandler(6) {
		@Override
		public int getSlotLimit(int slot) {
			return 1;
		}
	};

	@Override
	public boolean receiveBurnPower(int amount, int level) {
		ItemStack stack = piece_in.getStackInSlot(0);
		if (stack.isEmpty()) {
			power = 0;
			return false;
		}
		if (world.isRemote)
			return true;
		power += amount * level;
		if (power <= this.getMaxPower())
			return true;
		this.power = 0;
		this.infusionOnce();
		return true;
	}

	public int getMaxPower() {
		return 200;
	}

	private void consumeOnce() {
		ItemStack stack = piece_in.getStackInSlot(0);
		if (stack.isEmpty())
			return;
		stack.shrink(1);
		this.magic_power += 1;
	}

	public void infusionOnce() {
		if (world.provider.getDimensionType() != DimensionType.OVERWORLD)
			return;
		WorldTime time = new WorldTime(world);
		Biome biome = world.getBiome(pos);
		if (this.do1(time, biome))
			return;
		if (this.do2(time, biome))
			return;
		this.doLast(time, biome);
	}

	private boolean do1(WorldTime time, Biome biome) {
		if (biome != Biomes.PLAINS)
			return false;
		if (time.at(WorldTime.Period.DAWN) || time.at(WorldTime.Period.DUSK)) {
			this.infusionBecome(10, ESInitInstance.ITEMS.MAGIC_CRYSTAL,
					new ItemStack(ESInitInstance.ITEMS.ELEMENT_CRYSTAL));
			return true;
		}
		return false;
	}

	private boolean do2(WorldTime time, Biome biome) {
		if (time.at(WorldTime.Period.MIDNIGHT)) {
			int count = 0;
			for (int y = -1; y <= 1; y++) {
				for (int x = -2; x <= 2; x++) {
					for (int z = -2; z <= 2; z++) {
						BlockPos pos = this.pos.add(x, y, z);
						if (this.world.getBlockState(pos).getBlock() == Blocks.BOOKSHELF)
							count++;
						if (count >= 14) {
							y = 3;
							x = 3;
							z = 3;
							break;
						}
					}
				}
			}
			if (count >= 14) {
				this.infusionBecome(7, ESInitInstance.ITEMS.MAGIC_CRYSTAL,
						new ItemStack(ESInitInstance.ITEMS.SPELL_CRYSTAL));
				return true;
			}
		}
		return false;
	}

	private void doLast(WorldTime time, Biome biome) {
		if (time.at(WorldTime.Period.DAY)) {
			if (biome.getRainfall() <= 0.5f && !world.isRaining())
				this.infusionBecome(3, ESInitInstance.ITEMS.KYANITE, new ItemStack(ESInitInstance.ITEMS.MAGIC_CRYSTAL));
		}
	}

	private void infusionBecome(int count, Item from, ItemStack tostack) {
		for (int i = 0; i < translate.getSlots(); i++) {
			ItemStack stack = translate.getStackInSlot(i);
			if (stack.isEmpty())
				continue;
			if (stack.getItem() == from) {
				this.consumeOnce();
				if (this.magic_power < count)
					return;
				this.magic_power -= count;
				translate.setStackInSlot(i, tostack);
			}
		}
	}

	// 加载
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		this.translate.deserializeNBT(compound.getCompoundTag("translate"));
		this.piece_in.deserializeNBT(compound.getCompoundTag("piece_in"));
		this.power = compound.getInteger("power");
		this.magic_power = compound.getInteger("magic_power");
		super.readFromNBT(compound);
	}

	// 保存
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("translate", this.translate.serializeNBT());
		compound.setTag("piece_in", this.piece_in.serializeNBT());
		compound.setInteger("power", this.power);
		compound.setInteger("magic_power", this.magic_power);
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
			if (facing == EnumFacing.UP)
				return (T) piece_in;
			return (T) translate;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

}
