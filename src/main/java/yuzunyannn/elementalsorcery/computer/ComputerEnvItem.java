package yuzunyannn.elementalsorcery.computer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.computer.IComputEnv;
import yuzunyannn.elementalsorcery.api.computer.IComputerWatcher;
import yuzunyannn.elementalsorcery.api.util.target.CapabilityObjectRef;
import yuzunyannn.elementalsorcery.entity.EntityItemGoods;
import yuzunyannn.elementalsorcery.network.MessageComputerEntity;

public class ComputerEnvItem implements IComputEnv {

	protected final ItemStack stack;
	protected final World world;
	protected Entity entity;
	protected BlockPos pos;
	protected int itemSlot = -1;

	public ComputerEnvItem(Entity living, ItemStack stack, int itemSlot) {
		this.stack = stack;
		this.world = living.world;
		this.entity = living;
		this.itemSlot = itemSlot;
		this.pos = living.getPosition();
	}

	public ComputerEnvItem(EntityItem entityItem) {
		this.stack = entityItem.getItem();
		this.world = entityItem.world;
		this.entity = entityItem;
		this.pos = entityItem.getPosition();
	}

	public ComputerEnvItem(EntityItemGoods entityItem) {
		this.stack = entityItem.getItem();
		this.world = entityItem.world;
		this.entity = entityItem;
		this.pos = entityItem.getPosition();
	}

	@Override
	public EntityLivingBase getEntityLiving() {
		if (this.entity instanceof EntityLivingBase) return (EntityLivingBase) this.entity;
		return null;
	}

	@Override
	public CapabilityObjectRef createRef() {
		if (entity instanceof EntityItem) return CapabilityObjectRef.of((EntityItem) entity);
		else if (entity instanceof EntityItemGoods) return CapabilityObjectRef.of((EntityItemGoods) entity);
		else if (entity instanceof EntityPlayer) return CapabilityObjectRef.of((EntityPlayer) entity, itemSlot);
		return CapabilityObjectRef.of();
	}

	@Override
	public BlockPos getBlockPos() {
		return this.pos;
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
