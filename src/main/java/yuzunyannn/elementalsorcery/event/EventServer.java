package yuzunyannn.elementalsorcery.event;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import yuzunyannn.elementalsorcery.ESData;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.building.BuildingLib;
import yuzunyannn.elementalsorcery.capability.Adventurer;
import yuzunyannn.elementalsorcery.capability.ESPlayerCapabilityProvider;
import yuzunyannn.elementalsorcery.config.ESConfig;
import yuzunyannn.elementalsorcery.elf.ElfPostOffice;
import yuzunyannn.elementalsorcery.elf.quest.IAdventurer;
import yuzunyannn.elementalsorcery.enchant.EnchantmentES;
import yuzunyannn.elementalsorcery.entity.fcube.Behavior;
import yuzunyannn.elementalsorcery.entity.fcube.EntityFairyCube;
import yuzunyannn.elementalsorcery.item.IItemStronger;
import yuzunyannn.elementalsorcery.item.ItemScroll;
import yuzunyannn.elementalsorcery.network.ESNetwork;
import yuzunyannn.elementalsorcery.network.MessageSyncConfig;

public class EventServer {

	public static NBTTagCompound getPlayerNBT(EntityLivingBase player) {
		NBTTagCompound data = player.getEntityData();
		if (data.hasKey("ESData", 10)) return data.getCompoundTag("ESData");
		NBTTagCompound nbt = new NBTTagCompound();
		data.setTag("ESData", nbt);
		return nbt;
	}

	/** 玩家死亡等，复制玩家数据 */
	@SubscribeEvent
	public static void onClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
		EntityPlayer player = event.getEntityPlayer();
		EntityPlayer origin = event.getOriginal();
		NBTTagCompound oData = origin.getEntityData();
		if (oData.hasKey("ESData", 10)) player.getEntityData().setTag("ESData", oData.getTag("ESData"));

		IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
		if (adventurer != null) {
			IAdventurer oAdventurer = origin.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
			if (oAdventurer != null) {
				IStorage<IAdventurer> storage = Adventurer.ADVENTURER_CAPABILITY.getStorage();
				NBTBase base = storage.writeNBT(Adventurer.ADVENTURER_CAPABILITY, oAdventurer, null);
				storage.readNBT(Adventurer.ADVENTURER_CAPABILITY, adventurer, null, base);
			}
		}
	}

	@SubscribeEvent
	public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
		EntityPlayer player = event.player;
		NBTTagCompound data = EventServer.getPlayerNBT(player);
		if (!data.hasKey("esFirstJoin")) {
			if (player.inventory == null) return;
			data.setBoolean("esFirstJoin", true);
			player.inventory.addItemStackToInventory(ItemScroll.getScroll("rite"));
		}

		if (event.player instanceof EntityPlayerMP) {
			EntityPlayerMP playerMP = (EntityPlayerMP) event.player;
			MinecraftServer mc = playerMP.getServer();
			if (mc.isSinglePlayer()) ESConfig.restore();
			else ESNetwork.instance.sendTo(new MessageSyncConfig(ESConfig.getter.getSyncData()), playerMP);
		}
	}

	@SubscribeEvent
	public static void onLogoff(PlayerEvent.PlayerLoggedOutEvent event) {
		EntityPlayer player = event.player;
		ESData.removeRuntimeData(player);
	}

	@SubscribeEvent
	public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityPlayer) {
			event.addCapability(new ResourceLocation(ElementalSorcery.MODID, "capability"),
					new ESPlayerCapabilityProvider());
		}
	}

	@SubscribeEvent
	public static void entityCanUpdate(EntityEvent.CanUpdate evt) {
		Entity entity = evt.getEntity();
		if (entity instanceof EntityFairyCube) evt.setCanUpdate(true);
	}

	static private final List<ITickTask> tickList = new LinkedList<ITickTask>();

	/** 添加一个服务端的tick任务 */
	static public void addTickTask(ITickTask task) {
		if (task == null) return;
		tickList.add(task);
	}

	static public void addTickTask(ITickTask task, int tickout) {
		if (task == null) return;
		if (tickout <= 0) tickList.add(task);
		else tickList.add(new ITickTask() {
			int tick = 0;

			@Override
			public int onTick() {
				if (tick < tickout) {
					tick++;
					return ITickTask.SUCCESS;
				}
				return task.onTick();
			}
		});
	}

	static public void addTask(ITickTask.ITickTaskOnce task) {
		addTickTask((ITickTask) task);
	}

	static public void addTask(ITickTask.ITickTaskOnce task, int tickout) {
		addTickTask((ITickTask) task, tickout);
	}

	@SubscribeEvent
	public static void serverTick(TickEvent.ServerTickEvent event) {
		Iterator<ITickTask> iter = tickList.iterator();
		while (iter.hasNext()) {
			ITickTask task = iter.next();
			int flags = task.onTick();
			if (flags == ITickTask.END) iter.remove();
		}
	}

	@SubscribeEvent
	public static void gameSave(WorldEvent.Save e) {
		BuildingLib.instance.dealSave();
		ElfPostOffice.GC(e.getWorld());
	}

	// 破坏方块
	@SubscribeEvent
	public static void onBlockDestory(BlockEvent.BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		EntityFairyCube.addBehavior(player, Behavior.harvestBlock(event.getPos(), event.getState()));
	}

	// 任何死亡
	@SubscribeEvent
	public static void onLivingDead(LivingDeathEvent event) {
		if (event.isCanceled()) return;
		EntityLivingBase deader = event.getEntityLiving();
		DamageSource source = event.getSource();
		onLivingDeadEnchantmentDeal(deader, source);

		Entity trueSource = source.getTrueSource();
		if (trueSource instanceof EntityLivingBase) {
			ItemStack held = ((EntityLivingBase) trueSource).getHeldItemMainhand();
			Item item = held.getItem();
			if (item instanceof IItemStronger) ((IItemStronger) item).onKillEntity(trueSource.world, held, deader,
					(EntityLivingBase) trueSource, source);
		}
	}

	private static void onLivingDeadEnchantmentDeal(EntityLivingBase deader, DamageSource source) {
		Entity s = source.getImmediateSource();
		if (!(s instanceof EntityLivingBase)) return;
		EntityLivingBase living = (EntityLivingBase) s;
		ItemStack stack = living.getHeldItemMainhand();
		NBTTagList nbttaglist = stack.getEnchantmentTagList();
		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			int id = nbttaglist.getCompoundTagAt(i).getShort("id");
			int lvl = nbttaglist.getCompoundTagAt(i).getShort("lvl");
			Enchantment enchantment = Enchantment.getEnchantmentByID(id);
			if (enchantment instanceof EnchantmentES) ((EnchantmentES) enchantment).onLivingDead(deader, source, lvl);
		}
	}

}
