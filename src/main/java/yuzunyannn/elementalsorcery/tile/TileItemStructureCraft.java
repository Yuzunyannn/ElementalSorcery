package yuzunyannn.elementalsorcery.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.tile.IItemStructureCraft;
import yuzunyannn.elementalsorcery.container.ContainerItemStructureCraft;
import yuzunyannn.elementalsorcery.container.gui.GuiItemStructureCraft;
import yuzunyannn.elementalsorcery.util.item.ItemStackHandlerInventory;

public abstract class TileItemStructureCraft extends TileEntityNetwork implements IItemStructureCraft {

	/** 需求 */
	protected ItemStackHandlerInventory inputInv = new ItemStackHandlerInventory(this.initItemStackCount());
	/** 产出 */
	protected ItemStackHandler ouputInv = new ItemStackHandler(1) {
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return ItemStack.EMPTY;
		};

		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			return stack;
		};
	};

	@Override
	public void onLoad() {
		super.onLoad();
		this.onSlotChange(ContainerItemStructureCraft.vest());
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		this.onSlotChange(ContainerItemStructureCraft.vest());
	}

	/** 初始化仓库个数 */
	abstract protected int initItemStackCount();

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		if (inputInv != null) nbt.setTag("inputInv", this.inputInv.serializeNBT());
		if (ouputInv != null) nbt.setTag("outputInv", this.ouputInv.serializeNBT());
		return super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.inputInv.deserializeNBT(nbt.getCompoundTag("inputInv"));
		this.ouputInv.deserializeNBT(nbt.getCompoundTag("outputInv"));
		super.readFromNBT(nbt);
	}

	public ItemStackHandler getInputInv() {
		return inputInv;
	}

	public ItemStackHandler getOuputInv() {
		return ouputInv;
	}

	@Override
	public ItemStack getOutput() {
		return ouputInv.getStackInSlot(0);
	}

	public String getTitle() {
		return this.world.getBlockState(pos).getBlock().getUnlocalizedName() + ".name";
	}

	abstract public void initGui(ContainerItemStructureCraft gui);

	abstract public void onSlotChange(ContainerItemStructureCraft gui);

	@SideOnly(Side.CLIENT)
	public void initGui(GuiItemStructureCraft gui) {
	};

	@SideOnly(Side.CLIENT)
	abstract public void drawGui(GuiItemStructureCraft gui, float partialTicks, int mouseX, int mouseY);

}
