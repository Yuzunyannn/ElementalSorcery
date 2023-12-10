package yuzunyannn.elementalsorcery.computer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.computer.IComputEnv;
import yuzunyannn.elementalsorcery.api.computer.IComputerWatcher;
import yuzunyannn.elementalsorcery.entity.EntityItemGoods;
import yuzunyannn.elementalsorcery.network.MessageComputerEntity;

public class ComputerEnvItem implements IComputEnv {

	protected final ItemStack stack;
	protected final World world;
	protected Entity entity;
	protected int itemSlot = -1;

	public ComputerEnvItem(World world, ItemStack stack) {
		this.stack = stack;
		this.world = world;
		this.entity = null;
	}

	public ComputerEnvItem(Entity living, ItemStack stack, int itemSlot) {
		this.stack = stack;
		this.world = living.world;
		this.entity = living;
		this.itemSlot = itemSlot;
	}

	public ComputerEnvItem(EntityItem entityItem) {
		this.stack = entityItem.getItem();
		this.world = entityItem.world;
		this.entity = entityItem;
	}

	public ComputerEnvItem(EntityItemGoods entityItem) {
		this.stack = entityItem.getItem();
		this.world = entityItem.world;
		this.entity = entityItem;
	}

	@Override
	public World getWorld() {
		return this.world;
	}

	@Override
	public boolean isRemote() {
		return this.world.isRemote;
	}

	@Override
	public void sendMessageToClient(IComputerWatcher watcher, NBTTagCompound data) {
		if (this.world.isRemote) return;
		MessageComputerEntity msg = new MessageComputerEntity(entity, data, itemSlot);
		watcher.sendMessageToClient(msg);
	}

}
