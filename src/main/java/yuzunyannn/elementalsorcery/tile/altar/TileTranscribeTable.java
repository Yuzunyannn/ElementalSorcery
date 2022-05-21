package yuzunyannn.elementalsorcery.tile.altar;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.tile.IGetItemStack;
import yuzunyannn.elementalsorcery.grimoire.Grimoire;
import yuzunyannn.elementalsorcery.item.prop.ItemMantraGem;
import yuzunyannn.elementalsorcery.tile.TileEntityNetwork;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class TileTranscribeTable extends TileEntityNetwork implements IGetItemStack {

	protected ItemStack grimoire = ItemStack.EMPTY;

	@Override
	public void setStack(ItemStack stack) {
		if (ItemHelper.areItemsEqual(grimoire, stack)) return;
		grimoire = stack;
		this.callInjection();
		this.updateToClient();
		this.markDirty();
	}

	/** 尝试唤醒上方的设备 */
	public void callInjection() {
		if (world.isRemote) return;
		if (grimoire.isEmpty()) return;
		if (grimoire.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null) == null && !ItemMantraGem.isMantraGem(grimoire))
			return;
		TileTranscribeInjection tile = BlockHelper.getTileEntity(world, pos.up(4), TileTranscribeInjection.class);
		if (tile == null) return;
		tile.wake();
	}

	@Override
	public ItemStack getStack() {
		return grimoire;
	}

	@Override
	public boolean canSetStack(ItemStack stack) {
		return stack.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null) != null || ItemMantraGem.isMantraGem(stack);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setTag("stack", grimoire.serializeNBT());
		return super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		grimoire = new ItemStack(nbt.getCompoundTag("stack"));
		super.readFromNBT(nbt);
	}

}
