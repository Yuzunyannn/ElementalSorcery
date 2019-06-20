package yuzunyan.elementalsorcery.capability;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyan.elementalsorcery.api.ability.IElementInventory;
import yuzunyan.elementalsorcery.network.ESNetwork;
import yuzunyan.elementalsorcery.network.MessageSpellbook;
import yuzunyan.elementalsorcery.network.MessageSyncItemStack;
import yuzunyan.elementalsorcery.render.item.SpellbookRenderInfo;

public class Spellbook {

	@CapabilityInject(Spellbook.class)
	public static Capability<Spellbook> SPELLBOOK_CAPABILITY;

	/** 开始释放 */
	public void beginSpelling(World world, EntityLivingBase player, EnumHand hand) {
		this.spelling = true;
		player.setActiveHand(hand);
		// 发送动画消息
		if (!world.isRemote)
			sendMessageBegin(world, player, hand);
	}

	/**
	 * 结束释放
	 * 
	 * @param sync
	 *            是否进行对客户端的同步，客户端该参数无效
	 */
	public void endSpelling(World world, EntityLivingBase player, ItemStack stack, boolean sync) {
		this.spelling = false;
		if (sync && !world.isRemote)
			sendMessageEnd(world, player, stack);
	}

	/** 释放状态 */
	public boolean spelling = false;

	/** 完成释放，运行该函数，诉客户端动画该结束了，不再运行 spelling函数，但是还会等待合上书时候调用endSpell函数 */
	public void finishSpelling(World world, EntityLivingBase entity) {
		this.spelling = false;
		if (!world.isRemote) {
			if (entity instanceof EntityPlayerMP) {
				MessageSpellbook message = new MessageSpellbook().setSpellFinish(entity);
				for (EntityPlayer player : world.playerEntities)
					ESNetwork.instance.sendTo(message, (EntityPlayerMP) player);
			}
		}
	}

	// 发送打开魔法书的消息
	private void sendMessageBegin(World world, EntityLivingBase playerIn, EnumHand hand) {
		MessageSpellbook message = new MessageSpellbook().setOpen(playerIn, hand);
		for (EntityPlayer player : world.playerEntities)
			ESNetwork.instance.sendTo(message, (EntityPlayerMP) player);
	}

	// 发送结束魔法书时，书内信息的同步
	private void sendMessageEnd(World world, EntityLivingBase playerIn, ItemStack stack) {
		if (playerIn instanceof EntityPlayerMP) {
			ESNetwork.instance.sendTo(new MessageSyncItemStack((EntityPlayer) playerIn, stack),
					(EntityPlayerMP) playerIn);
		}
	}

	// 表明是否当前client玩家使用的
	@SideOnly(Side.CLIENT)
	public EntityLivingBase who = null;
	
	/** 不同的书自使用 */
	public Object obj = null;
	public int cast_time = 0;
	public int flags;

	/** 记录的仓库 */
	private IElementInventory inventory = null;

	/** 渲染信息 */
	public SpellbookRenderInfo render_info = null;
	{
		if (SpellbookRenderInfo.renderInstance != null) {
			render_info = new SpellbookRenderInfo();
		}
	}

	/** 获取仓库 */
	@Nullable
	public IElementInventory getInventory() {
		return inventory;
	}

	// 保存能力
	public static class Storage implements Capability.IStorage<Spellbook> {

		@Override
		public NBTBase writeNBT(Capability<Spellbook> capability, Spellbook instance, EnumFacing side) {
			NBTTagCompound nbt = new NBTTagCompound();
			if (instance.inventory != null) {
				nbt.setTag("inventory", ElementInventory.Provider.storage
						.writeNBT(ElementInventory.ELEMENTINVENTORY_CAPABILITY, instance.inventory, null));
			}
			return nbt;
		}

		@Override
		public void readNBT(Capability<Spellbook> capability, Spellbook instance, EnumFacing side, NBTBase tag) {
			NBTTagCompound nbt = (NBTTagCompound) tag;
			if (nbt.hasKey("inventory") && instance.inventory != null) {
				ElementInventory.Provider.storage.readNBT(ElementInventory.ELEMENTINVENTORY_CAPABILITY,
						instance.inventory, null, nbt.getTag("inventory"));
			}
		}

	}

	// 能力提供者
	public static class Provider implements ICapabilitySerializable<NBTTagCompound> {

		public final static IStorage<Spellbook> storage = SPELLBOOK_CAPABILITY.getStorage();
		private Spellbook instance = new Spellbook();

		public Provider() {
			this(null);
		}

		public Provider(IElementInventory inventory) {
			instance.inventory = inventory;
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return SPELLBOOK_CAPABILITY == capability;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			if (SPELLBOOK_CAPABILITY == capability) {
				return (T) instance;
			}
			return null;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			return (NBTTagCompound) storage.writeNBT(SPELLBOOK_CAPABILITY, instance, null);
		}

		@Override
		public void deserializeNBT(NBTTagCompound compound) {
			storage.readNBT(SPELLBOOK_CAPABILITY, instance, null, compound);
		}
	}

}
