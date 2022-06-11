package yuzunyannn.elementalsorcery.tile;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.tile.IAliveStatusable;
import yuzunyannn.elementalsorcery.api.tile.IMagicBeamHandler;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class TileEStoneCrock extends TileEntityNetwork implements IMagicBeamHandler {

	public interface ICrockCraft extends IAliveStatusable {
		public void tryConnect(World world, BlockPos pos, IMagicBeamHandler hanlder);

		public double getCraftCost(World world, BlockPos pos, ItemStack stack);

		public ItemStack doCraft(World world, BlockPos pos, ItemStack stack);
	}

	protected ICrockCraft craft;
	protected boolean lastConnectFlag;
	protected double fragmentCollect;
	protected double fragmentRquire;

	public void setCraft(ICrockCraft craft) {
		this.craft = craft;
	}

	public ICrockCraft getCraft() {
		return craft;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return null;
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
	}

	@Override
	public double insertMagicFragment(double count, boolean simulate) {
		lastConnectFlag = true;
		ICrockCraft craft = getCraft();
		if (craft == null) return count;
		if (fragmentCollect >= fragmentRquire) return count;
		double cost = Math.min(fragmentRquire - fragmentCollect, count);
		if (simulate) return count - cost;
		fragmentCollect = fragmentCollect + cost;
		return count - cost;
	}

	@Override
	public double extractMagicFragment(double count, boolean simulate) {
		lastConnectFlag = true;
		return 0;
	}

	public void onItemIn(EntityItem entityItem) {
		ICrockCraft craft = getCraft();
		if (craft == null) return;
		if (!craft.isAlive()) {
			craft = null;
			return;
		}
		if (entityItem.ticksExisted % 20 == 0) {
			if (!lastConnectFlag) craft.tryConnect(world, pos, this);
			lastConnectFlag = false;
		}
		ItemStack stack = entityItem.getItem();
		fragmentRquire = craft.getCraftCost(world, pos, stack);
		if (fragmentRquire <= 0) return;
		if (fragmentCollect >= fragmentRquire) {
			ItemStack newStack = craft.doCraft(world, pos, stack);
			if (!ItemHelper.areItemsEqual(stack, newStack)) entityItem.setItem(stack);
			fragmentCollect -= fragmentRquire;
		}
	}
}
