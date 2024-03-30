package yuzunyannn.elementalsorcery.api.util.target;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class CORPlayerItem extends CORPlayer {

	protected int slot;

	protected ItemStack stack = ItemStack.EMPTY;

	public CORPlayerItem(EntityPlayer player, int slot) {
		super(player);
		this.slot = slot;
		stack = player.inventory.getStackInSlot(slot);
	}

	@Override
	public void restore(World world) {
		super.restore(world);
		EntityPlayer player = toEntityPlayer();
		if (player == null) return;
		stack = player.inventory.getStackInSlot(slot);
	}

	@Override
	public boolean isValid() {
		if (stack.isEmpty()) return false;
		if (_isValid()) return true;
		stack = ItemStack.EMPTY;
		return false;
	}

	private boolean _isValid() {
		if (!super.isValid()) return false;
		EntityPlayer player = toEntityPlayer();
		if (player.inventory.getStackInSlot(slot) != stack) return false;
		return true;
	}

	@Override
	public int tagId() {
		return TAG_PLAYER_ITEM;
	}

	@Override
	public ItemStack toItemStack() {
		return stack;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return stack.isEmpty() ? false : stack.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return stack.isEmpty() ? null : stack.getCapability(capability, facing);
	}

}
