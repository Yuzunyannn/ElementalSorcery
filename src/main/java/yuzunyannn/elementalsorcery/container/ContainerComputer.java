package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.api.computer.IComputEnv;
import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.IComputerWatcher;
import yuzunyannn.elementalsorcery.computer.Computer;
import yuzunyannn.elementalsorcery.computer.ComputerEnvItem;
import yuzunyannn.elementalsorcery.computer.ComputerEnvTile;
import yuzunyannn.elementalsorcery.computer.WatcherConatiner;
import yuzunyannn.elementalsorcery.network.MessageSyncContainer.IContainerNetwork;

public class ContainerComputer extends Container implements IContainerNetwork {

	protected IComputer computer;
	public boolean isClosed = false;
	public final World world;
	public final BlockPos pos;
	public final EntityPlayer player;
	public final TileEntity tileEntity;
	public IComputEnv cEnv;
	public ItemStack stack = ItemStack.EMPTY;
	public int slot = -1;
	protected IComputerWatcher watcher;

	public ContainerComputer(EntityPlayer player, BlockPos pos) {
		this.world = player.world;
		this.player = player;
		this.pos = pos;
		this.tileEntity = null;

		stack = player.getHeldItem(EnumHand.MAIN_HAND);
		this.computer = Computer.from(stack);
		if (this.computer == null) {
			stack = player.getHeldItem(EnumHand.OFF_HAND);
			this.computer = Computer.from(stack);
		}
		if (this.computer != null) {
			for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
				if (player.inventory.getStackInSlot(i) == stack) {
					slot = i;
					break;
				}
			}
			cEnv = new ComputerEnvItem(player, stack, slot);
			this.computer.onPlayerInteraction(player, cEnv);
			if (!world.isRemote) watcher = new WatcherConatiner(this);
		}
	}

	public ContainerComputer(EntityPlayer player, TileEntity tile) {
		this.world = player.world;
		this.player = player;
		this.pos = tile.getPos();
		this.tileEntity = tile;
		this.computer = Computer.from(tile);

		if (this.computer != null) {
			cEnv = new ComputerEnvTile(tile);
			this.computer.onPlayerInteraction(player, cEnv);
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (watcher == null) return;
		computer.detectChangesAndSend(watcher, cEnv);
	}

	public IComputer getComputer() {
		return computer;
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		isClosed = true;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		if (this.computer == null) return false;

		if (tileEntity != null) {
			if (world.getTileEntity(this.pos) != this.tileEntity) return false;
		} else if (!stack.isEmpty()) {
			if (stack != player.inventory.getStackInSlot(slot)) return false;
		}

		return playerIn.getDistanceSq(pos) <= 64;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public void recvData(NBTTagCompound nbt, Side side) {
		if (side == Side.SERVER) {
			if (nbt.hasKey("_nt_")) {
				this.computer.notice(cEnv, nbt.getString("_nt_"));
				return;
			}
			if (nbt.hasKey("_op_")) {
				int pid = nbt.getInteger("pid");
				this.computer.notice(cEnv, "op", pid, nbt);
			}
		}
	}

}
