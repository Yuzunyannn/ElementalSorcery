package yuzunyannn.elementalsorcery.container;

import java.util.function.Consumer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.api.computer.DNNBTParams;
import yuzunyannn.elementalsorcery.api.computer.DNRequest;
import yuzunyannn.elementalsorcery.api.computer.DNResult;
import yuzunyannn.elementalsorcery.api.computer.IComputEnv;
import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.IComputerWatcher;
import yuzunyannn.elementalsorcery.api.computer.IDeviceListener;
import yuzunyannn.elementalsorcery.computer.Computer;
import yuzunyannn.elementalsorcery.computer.ComputerEnvItem;
import yuzunyannn.elementalsorcery.computer.ComputerEnvTile;
import yuzunyannn.elementalsorcery.computer.WatcherConatiner;
import yuzunyannn.elementalsorcery.network.MessageSyncContainer.IContainerNetwork;
import yuzunyannn.elementalsorcery.util.black.RecvLimit;

public class ContainerComputer extends Container implements IContainerNetwork, IDeviceListener {

	protected IComputer computer;
	public boolean isClosed = false;
	public final World world;
	public final BlockPos pos;
	public final EntityPlayer player;
	public final TileEntity tileEntity;
	public IComputEnv cEnv;
	public ItemStack stack = ItemStack.EMPTY;
	public int slot = -1;
	public Consumer<DNRequest> msgHook;
	protected IComputerWatcher watcher;
	protected boolean lastComputerIsOpen = false;

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
			cEnv = this.computer.getEnv();
			if (cEnv == null) cEnv = new ComputerEnvItem(player, stack, slot);
			init();
		}
	}

	public ContainerComputer(EntityPlayer player, TileEntity tile) {
		this.world = player.world;
		this.player = player;
		this.pos = tile.getPos();
		this.tileEntity = tile;
		this.computer = Computer.from(tile);

		if (this.computer != null) {
			cEnv = this.computer.getEnv();
			if (cEnv == null) cEnv = new ComputerEnvTile(tile);
			init();
		}
	}

	protected void init() {
		this.computer.onPlayerInteraction(player, cEnv);
		this.computer.addListener(this);
		if (!world.isRemote) watcher = new WatcherConatiner(this);
	}

	public BlockPos getCurrPosition() {
		if (!stack.isEmpty()) return player.getPosition();
		return this.pos;
	}

	@Override
	public boolean isAlive() {
		return !isClosed;
	}

	@Override
	public DNResult notice(String method, DNRequest params) {
		if ("app-message".equals(method)) {
			if (world.isRemote) {
				if (msgHook != null) msgHook.accept(params);
				this.computer.notice(cEnv, "msg", params);
				return DNResult.success();
			} else {
				int pid = params.get("pid", Integer.class);
				NBTTagCompound data = params.get("data");
				data.setInteger("_msg_pid_", pid);
				this.sendToClient(data, player);
				return DNResult.success();
			}
		}
		return DNResult.invalid();
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (watcher == null) return;
		if (watcher.isLeave()) return;

		if (lastComputerIsOpen != computer.isPowerOn()) {
			lastComputerIsOpen = computer.isPowerOn();
			if (computer.isPowerOn()) watcher.clearDetectObjects();
		}

//		if (computer instanceof Computer) ((Computer) computer).detectChangesAndSend(watcher, cEnv);
//		else {
		NBTTagCompound nbt = computer.detectChanges(watcher);
		if (nbt != null) this.sendToClient(nbt, player);
//		}
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

	public final static RecvLimit RL = new RecvLimit(6, 4);

	@Override
	public void recvData(NBTTagCompound nbt, Side side) {
		if (side == Side.SERVER) {
			if (nbt.hasKey("_nt_")) {
				if (RL.peg(player)) return;
				int pid = nbt.hasKey("pid") ? nbt.getInteger("pid") : -1;
				DNRequest params = new DNNBTParams(nbt);
				params.set("pid", pid);
				this.computer.notice(cEnv, nbt.getString("_nt_"), params);
				return;
			}
			if (nbt.hasKey("_op_")) {
				int pid = nbt.getInteger("_op_");
				DNRequest params = new DNRequest();
				params.set("pid", pid);
				params.set("data", nbt);
				this.computer.notice(cEnv, "op", params);
			}
		} else if (side == Side.CLIENT) {
			if (nbt.hasKey("_msg_pid_")) {
				int pid = nbt.getInteger("_msg_pid_");
				DNRequest params = new DNRequest();
				params.set("pid", pid);
				params.set("data", nbt);
				if (msgHook != null) msgHook.accept(params);
				this.computer.notice(cEnv, "msg", params);
				return;
			}
			this.computer.mergeChanges(nbt);
		}
	}

}
