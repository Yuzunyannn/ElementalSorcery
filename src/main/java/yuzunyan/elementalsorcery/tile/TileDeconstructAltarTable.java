package yuzunyan.elementalsorcery.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import yuzunyan.elementalsorcery.api.ability.IGetItemStack;
import yuzunyan.elementalsorcery.building.Buildings;
import yuzunyan.elementalsorcery.building.MultiBlock;

public class TileDeconstructAltarTable extends TileStaticMultiBlock implements IGetItemStack {

	@Override
	public void initMultiBlock() {
		structure = new MultiBlock(Buildings.DECONSTRUCT_ALTAR, this, new BlockPos(0, -1, 0));
		structure.addSpecialBlock(new BlockPos(2, 1, 2));
		structure.addSpecialBlock(new BlockPos(2, 1, -2));
		structure.addSpecialBlock(new BlockPos(-2, 1, -2));
		structure.addSpecialBlock(new BlockPos(-2, 1, 2));
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("stack"))
			stack = new ItemStack(compound.getCompoundTag("stack"));
		else
			stack = ItemStack.EMPTY;
		super.readFromNBT(compound);

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("stack", stack.serializeNBT());
		return super.writeToNBT(compound);
	}

	private ItemStack stack = ItemStack.EMPTY;

	@Override
	public void setStack(ItemStack stack) {
		this.stack = stack;
		this.updateToClient();
		this.markDirty();
	}

	@Override
	public ItemStack getStack() {
		return stack;
	}
}
