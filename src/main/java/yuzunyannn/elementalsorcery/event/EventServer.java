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
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import yuzunyannn.elementalsorcery.ESData;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.building.BuildingLib;
import yuzunyannn.elementalsorcery.capability.Adventurer;
import yuzunyannn.elementalsorcery.capability.ESPlayerCapabilityProvider;
import yuzunyannn.elementalsorcery.config.ESConfig;
import yuzunyannn.elementalsorcery.elf.ElfPostOffice;
import yuzunyannn.elementalsorcery.elf.quest.IAdventurer;
import yuzunyannn.elementalsorcery.elf.research.Researcher;
import yuzunyannn.elementalsorcery.enchant.EnchantmentES;
import yuzunyannn.elementalsorcery.entity.fcube.BehaviorAttack;
import yuzunyannn.elementalsorcery.entity.fcube.BehaviorBlock;
import yuzunyannn.elementalsorcery.entity.fcube.BehaviorClick;
import yuzunyannn.elementalsorcery.entity.fcube.BehaviorInteract;
import yuzunyannn.elementalsorcery.entity.fcube.EntityFairyCube;
import yuzunyannn.elementalsorcery.entity.fcube.FCMAttack;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.item.IItemStronger;
import yuzunyannn.elementalsorcery.item.ItemScroll;
import yuzunyannn.elementalsorcery.network.ESNetwork;
import yuzunyannn.elementalsorcery.network.MessageSyncConfig;
import yuzunyannn.elementalsorcery.ts.PocketWatch;
import yuzunyannn.elementalsorcery.ts.PocketWatchClient;
import yuzunyannn.elementalsorcery.util.NBTTag;

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

//	@SubscribeEvent
//	public static void entityCanUpdate(EntityEvent.CanUpdate evt) {
//		Entity entity = evt.getEntity();
//		if (entity instanceof EntityFairyCube) {
//			evt.setCanUpdate(true);
//		}
//	}

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
		if (event.phase == Phase.START) {
			PocketWatch.tick();
			return;
		}

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

	@SubscribeEvent
	public static void gameChunkSave(ChunkDataEvent.Save event) {
		World world = event.getWorld();
		if (world.isRemote) return;
		if (PocketWatch.isActive(world)) {
			NBTTagCompound nbt = event.getData().getCompoundTag("Level");
			NBTTagList list = nbt.getTagList("Entities", NBTTag.TAG_COMPOUND);
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound entityData = list.getCompoundTagAt(i);
				entityData.setBoolean("UpdateBlocked", false);
			}
		}

	}

	// 区块卸载
	@SubscribeEvent
	public static void onChunkUnload(ChunkEvent.Unload event) {
		World world = event.getWorld();
		if (world.isRemote) return;
		boolean worldStop = PocketWatch.isActive(world);
		ClassInheritanceMultiMap<Entity>[] entitiess = event.getChunk().getEntityLists();
		for (ClassInheritanceMultiMap<Entity> entities : entitiess) {
			for (Entity entity : entities) {
				if (worldStop) entity.updateBlocked = false;
				if (entity instanceof EntityFairyCube) {
					((EntityFairyCube) entity).onLivingUpdate();
				}
			}
		}
	}

	// 玩家更换维度
	@SubscribeEvent
	public static void onPlayerChangedDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
		EntityPlayer player = event.player;
		if (player.world.isRemote) return;
		if (PocketWatch.isActive(player.world)) PocketWatch.sendPlayStopWord(player);
	}

	@SubscribeEvent
	public static void onNeighborNotify(NeighborNotifyEvent event) {
		World world = event.getWorld();
		if (world.isRemote) {
			if (PocketWatchClient.isActive()) event.setCanceled(true);
		} else {
			if (PocketWatch.isActive(world)) event.setCanceled(true);
		}
	}

	// 破坏方块
	@SubscribeEvent
	public static void onBlockDestory(BlockEvent.BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		if (player.world.isRemote) return;
		EntityFairyCube.addBehavior(player, BehaviorBlock.harvestBlock(event.getPos(), event.getState()));
		Researcher.onDestoryBlock(player, event.getState());
	}

	// 实体交互
	@SubscribeEvent
	public static void onInteractWithEntity(PlayerInteractEvent.EntityInteract event) {
		EntityPlayer player = event.getEntityPlayer();
		if (player.world.isRemote) {
			if (PocketWatchClient.isActive()) event.setCanceled(true);
			return;
		}
		Entity target = event.getTarget();
		EnumHand hand = event.getHand();
		if (hand == EnumHand.MAIN_HAND) EntityFairyCube.addBehavior(player, BehaviorInteract.interact(target, hand));
		Researcher.onInteractWithEntity(player, target, hand);

		if (PocketWatch.isActive(event.getWorld())) event.setCanceled(true);
	}

	// 放置方块
	@SubscribeEvent
	public static void onPlaceBlock(PlaceEvent event) {
		EntityPlayer player = event.getPlayer();
		if (player.world.isRemote) return;
		EntityFairyCube.addBehavior(player, BehaviorBlock.placeBlock(event.getPos(), event.getPlacedBlock()));
	}

	// 右键
	@SubscribeEvent
	public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
		EntityPlayer player = event.getEntityPlayer();
		if (player.world.isRemote) return;
		EntityFairyCube.addBehavior(player, BehaviorClick.rightClick(event.getHand()));
	}

	// 攻击实体
	@SubscribeEvent
	public static void onPlayerAttack(AttackEntityEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		Entity target = event.getTarget();
		EntityFairyCube.addBehavior(player, BehaviorAttack.attack(target));
		Researcher.onAttackWithEntity(player, target);
	}

	// 掉落
	@SubscribeEvent
	public static void onLooting(LootingLevelEvent event) {
		DamageSource ds = event.getDamageSource();
		Entity entity = ds.getImmediateSource();
		if (entity instanceof EntityFairyCube) {
			float plunder = FCMAttack.getPlunder((EntityFairyCube) entity);
			event.setLootingLevel(Math.max((int) plunder, event.getLootingLevel()));
		}
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

	@SubscribeEvent
	public static void onLivingAttack(LivingAttackEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		DamageSource dmg = event.getSource();

//		if (dmg.isFireDamage() && entity.isPotionActive(ESInit.POTIONS.FIRE_WALK)) {
//			event.setCanceled(true);
//			return;
//		}
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
