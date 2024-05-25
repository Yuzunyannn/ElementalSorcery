package yuzunyannn.elementalsorcery.logics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import yuzunyannn.elementalsorcery.ESData;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.entity.BehaviorAttack;
import yuzunyannn.elementalsorcery.api.entity.BehaviorBlock;
import yuzunyannn.elementalsorcery.api.entity.BehaviorClick;
import yuzunyannn.elementalsorcery.api.entity.BehaviorInteract;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncCarrier;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncExecuteContext;
import yuzunyannn.elementalsorcery.api.gfunc.IGameFuncCarrier;
import yuzunyannn.elementalsorcery.api.mantra.SilentLevel;
import yuzunyannn.elementalsorcery.api.tile.IBlockJumpModify;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.building.BuildingLib;
import yuzunyannn.elementalsorcery.capability.Adventurer;
import yuzunyannn.elementalsorcery.capability.ESPlayerCapabilityProvider;
import yuzunyannn.elementalsorcery.computer.WideNetwork;
import yuzunyannn.elementalsorcery.config.ESConfig;
import yuzunyannn.elementalsorcery.dungeon.DungeonAreaRoom;
import yuzunyannn.elementalsorcery.dungeon.DungeonWorld;
import yuzunyannn.elementalsorcery.elf.ElfPostOffice;
import yuzunyannn.elementalsorcery.elf.quest.IAdventurer;
import yuzunyannn.elementalsorcery.elf.research.Researcher;
import yuzunyannn.elementalsorcery.enchant.EnchantmentES;
import yuzunyannn.elementalsorcery.entity.fcube.EntityFairyCube;
import yuzunyannn.elementalsorcery.entity.fcube.FCMAttack;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraGoldShield;
import yuzunyannn.elementalsorcery.item.IItemStronger;
import yuzunyannn.elementalsorcery.item.prop.ItemBlessingJadePiece;
import yuzunyannn.elementalsorcery.network.ESNetwork;
import yuzunyannn.elementalsorcery.network.MessageSyncConfig;
import yuzunyannn.elementalsorcery.potion.PotionBlessing;
import yuzunyannn.elementalsorcery.potion.PotionCalamity;
import yuzunyannn.elementalsorcery.potion.PotionCombatSkill;
import yuzunyannn.elementalsorcery.potion.PotionDefenseSkill;
import yuzunyannn.elementalsorcery.potion.PotionDungeonNightmare;
import yuzunyannn.elementalsorcery.potion.PotionEndercorps;
import yuzunyannn.elementalsorcery.potion.PotionEnderization;
import yuzunyannn.elementalsorcery.potion.PotionFrozen;
import yuzunyannn.elementalsorcery.potion.PotionHealthBalance;
import yuzunyannn.elementalsorcery.potion.PotionNaturalMedal;
import yuzunyannn.elementalsorcery.potion.PotionPowerPitcher;
import yuzunyannn.elementalsorcery.potion.PotionRebirthFromFire;
import yuzunyannn.elementalsorcery.potion.PotionWindShield;
import yuzunyannn.elementalsorcery.ts.PocketWatch;
import yuzunyannn.elementalsorcery.ts.PocketWatchClient;
import yuzunyannn.elementalsorcery.util.Stopwatch;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.helper.ExceptionHelper;
import yuzunyannn.elementalsorcery.util.helper.SilentWorld;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class EventServer {

	static private final List<ITickTask> tickList = new LinkedList<ITickTask>();
	static private final Map<Integer, List<IWorldTickTask>> worldTickMapList = new HashMap<>();
	static private final Map<Integer, List<IWorldTickTask>> worldTickMapListCache = new HashMap<>();
	static private boolean isRunningWorldTick = false;
	static public final Stopwatch bigComputeWatch = new Stopwatch();
	// whatever thread safe, just check time;
	static public long chaosTimeStamp;

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

	/** 添加一个世界tick任务 */
	static public void addWorldTickTask(World world, IWorldTickTask task) {
		if (task == null) return;
		int id = world.provider.getDimension();
		Map<Integer, List<IWorldTickTask>> toMap = isRunningWorldTick ? worldTickMapListCache : worldTickMapList;
		List<IWorldTickTask> tasks = toMap.get(id);
		if (tasks == null) toMap.put(id, tasks = new LinkedList<>());
		tasks.add(task);
	}

	/** 添加一个一次性的的世界tick任务，该世界运行完后直接结束 */
	static public void addWorldTask(World world, IWorldTickTask.IWorldTickTaskOnce task) {
		addWorldTickTask(world, (IWorldTickTask) task);
	}

	@SubscribeEvent
	public static void serverTick(TickEvent.ServerTickEvent event) {

		if (event.phase == Phase.START) {
			chaosTimeStamp = System.currentTimeMillis();
			bigComputeWatch.clear();
			PocketWatch.tick();
			return;
		}

		Iterator<ITickTask> iter = tickList.iterator();
		while (iter.hasNext()) {
			ITickTask task = iter.next();
			try {
				int flags = task.onTick();
				if (flags == ITickTask.END) iter.remove();
			} catch (Exception e) {
				ESAPI.logger.warn("Server Tick Error", e);
				iter.remove();
			}
		}

		WideNetwork.instance.update();
	}

	@SubscribeEvent
	public static void worldTick(TickEvent.WorldTickEvent event) {
		if (event.phase == Phase.START) return;

		World world = event.world;
		if (world.isRemote) return;

		int id = world.provider.getDimension();
		if (PocketWatch.isActive(world)) return;

		List<IWorldTickTask> tasks = worldTickMapList.get(id);

		if (tasks != null) {
			isRunningWorldTick = true;
			Iterator<IWorldTickTask> iter = tasks.iterator();
			while (iter.hasNext()) {
				IWorldTickTask task = iter.next();
				try {
					int flags = task.onTick(world);
					if (flags == ITickTask.END) iter.remove();
				} catch (Exception e) {
					ESAPI.logger.warn("Server World Tick Error", e);
					ExceptionHelper.warnSend(world, "Server World Tick Error");
					iter.remove();
				}
			}
			if (tasks.isEmpty()) worldTickMapList.remove(id);
			isRunningWorldTick = false;
			if (!worldTickMapListCache.isEmpty()) {
				for (Entry<Integer, List<IWorldTickTask>> entry : worldTickMapListCache.entrySet()) {
					List<IWorldTickTask> toList = worldTickMapList.get(entry.getKey());
					if (toList == null) worldTickMapList.put(entry.getKey(), entry.getValue());
					else toList.addAll(entry.getValue());
				}
				worldTickMapListCache.clear();
			}
		}
	}

	@SubscribeEvent
	public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.player instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.player;

			if (ESPlayerLogic.checkPlayerFlagAndSet(player, ESPlayerLogic.FIRST_JOIN)) {
				if (player.inventory == null) return;
				ESPlayerLogic.onPlayerFirstJoinInWorld(player);
			}

			MinecraftServer mc = player.getServer();
			if (mc.isSinglePlayer()) ESConfig.restore();
			else ESNetwork.instance.sendTo(new MessageSyncConfig(ESConfig.getter.getSyncData()), player);
		}
	}

	@SubscribeEvent
	public static void onLogoff(PlayerEvent.PlayerLoggedOutEvent event) {
		EntityPlayer player = event.player;
		ESData.removeRuntimeData(player);
	}

	@SubscribeEvent
	public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityPlayer)
			event.addCapability(new ResourceLocation(ESAPI.MODID, "capability"), new ESPlayerCapabilityProvider());
		else event.addCapability(new ResourceLocation(ESAPI.MODID, "fcarrier"), new GameFuncCarrier.Provider());
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
	public static void onPlaceBlock(BlockEvent.PlaceEvent event) {
		EntityPlayer player = event.getPlayer();
		if (player.world.isRemote) return;
		EntityFairyCube.addBehavior(player, BehaviorBlock.placeBlock(event.getPos(), event.getPlacedBlock()));
	}

	// 右键
	@SubscribeEvent
	public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		EntityPlayer player = event.getEntityPlayer();
		if (event.isCanceled()) return;

		ItemStack stack = event.getItemStack();
		if (player.isPotionActive(ESObjects.POTIONS.POWER_PITCHER)) {
			int amplifier = player.getActivePotionEffect(ESObjects.POTIONS.POWER_PITCHER).getAmplifier();
			EnumActionResult result = PotionPowerPitcher.doPowerPitch(player, event.getHand(), stack, amplifier);
			if (result != EnumActionResult.PASS) {
				event.setCanceled(true);
				event.setCancellationResult(result);
				return;
			}
		}

		if (ESAPI.silent.isSilent(player, SilentLevel.SPELL)) {
			Item item = stack.getItem();
			if (item instanceof ItemFood || item == Items.POTIONITEM || item instanceof ItemBucketMilk) {
				SilentWorld.sendSilentMessage(player, SilentLevel.SPELL);
				event.setCanceled(true);
				event.setCancellationResult(EnumActionResult.FAIL);
				return;
			}
		}

		if (player.world.isRemote) return;
		EntityFairyCube.addBehavior(player, BehaviorClick.rightClick(event.getHand()));
	}

	// 右键方块
	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		BlockPos pos = event.getPos();
		World world = event.getWorld();
		if (world.getBlockState(pos).getBlock() == ESObjects.BLOCKS.GOAT_GOLD_BRICK) {
			if (!EntityHelper.isCreative(event.getEntityPlayer())) {
//			ItemStack holdItem = event.getItemStack();
				event.setCanceled(true);
				return;
			}
		}
	}

	// 攻击实体
	@SubscribeEvent
	public static void onPlayerAttack(AttackEntityEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		Entity target = event.getTarget();
		EntityFairyCube.addBehavior(player, BehaviorAttack.attack(target));
		Researcher.onAttackWithEntity(player, target);
	}

	// 掉落等级
	@SubscribeEvent
	public static void onLooting(LootingLevelEvent event) {
		DamageSource ds = event.getDamageSource();
		Entity entity = ds.getImmediateSource();
		if (entity instanceof EntityFairyCube) {
			double plunder = FCMAttack.getPlunder((EntityFairyCube) entity);
			event.setLootingLevel(Math.max((int) plunder, event.getLootingLevel()));
		}
	}

	// 掉落
	@SubscribeEvent
	public static void onLooting(LivingDropsEvent event) {
		Entity entity = event.getEntity();
		IGameFuncCarrier carrier = entity.getCapability(GameFuncCarrier.GAMEFUNCCARRIER_CAPABILITY, null);
		if (carrier != null) {
			GameFuncExecuteContext context = new GameFuncExecuteContext().setByEvent(event).setSrcObj(entity);
			carrier.trigger("onLoot", context);
			if (event.isCanceled()) return;
		}
	}

	// 方快掉落
	@SubscribeEvent
	public static void onHarvestDrops(HarvestDropsEvent event) {
		if (event.isSilkTouching()) return;
		EntityPlayer player = event.getHarvester();
		if (player == null) return;

		if (player.isPotionActive(ESObjects.POTIONS.BLESSING)) {
			int amplifier = player.getActivePotionEffect(ESObjects.POTIONS.BLESSING).getAmplifier();
			PotionBlessing.addOres(amplifier, event.getDropChance(), event.getDrops(), player.getRNG());
		}

		if (player.isPotionActive(ESObjects.POTIONS.CALAMITY)) {
			int amplifier = player.getActivePotionEffect(ESObjects.POTIONS.CALAMITY).getAmplifier();
			PotionCalamity.eliminateOres(amplifier, event.getDrops(), player.getRNG());
		}

	}

	// 设置攻击目标
	@SubscribeEvent
	public static void setAttackTarget(LivingSetAttackTargetEvent event) {
		EntityLivingBase living = event.getEntityLiving();
		EntityLivingBase target = event.getTarget();
		if (target == null) return;
		// 末影化和军团后看末影人不会攻击你
		if (living instanceof EntityEnderman) {
			if (target.isPotionActive(ESObjects.POTIONS.ENDERIZATION)) ((EntityEnderman) living).setAttackTarget(null);
			if (target.isPotionActive(ESObjects.POTIONS.ENDERCORPS) && !PotionEndercorps.isEnd(target.world))
				((EntityEnderman) living).setAttackTarget(null);
		}
	}

	// 任何死亡
	@SubscribeEvent
	public static void onLivingDead(LivingDeathEvent event) {
		EntityLivingBase deader = event.getEntityLiving();
		DamageSource source = event.getSource();

		if (PotionRebirthFromFire.needRebirth(deader, source)) {
			PotionRebirthFromFire.doRebirth(deader);
			event.setCanceled(true);
		}

		IGameFuncCarrier carrier = deader.getCapability(GameFuncCarrier.GAMEFUNCCARRIER_CAPABILITY, null);
		if (carrier != null) {
			GameFuncExecuteContext context = new GameFuncExecuteContext().setByEvent(event).setSrcObj(deader);
			carrier.trigger("onDead", context);
		}

		if (event.isCanceled()) return;

		onLivingDeadEnchantmentDeal(deader, source);

		Entity trueSource = source.getTrueSource();
		if (trueSource instanceof EntityLivingBase) {
			EntityLivingBase living = ((EntityLivingBase) trueSource);
			ItemStack held = living.getHeldItemMainhand();
			IItemStronger stronger = ItemHelper.getItemStronger(held);
			if (stronger != null)
				stronger.onKillEntity(trueSource.world, held, deader, (EntityLivingBase) trueSource, source);
			if (living.isPotionActive(ESObjects.POTIONS.NATURAL_MEDAL)) PotionNaturalMedal.growMedal(living);
		}

	}

	// 攻击
	@SubscribeEvent
	public static void onLivingAttack(LivingAttackEvent event) {
		if (event.isCanceled()) return;

		EntityLivingBase entity = event.getEntityLiving();
		DamageSource source = event.getSource();
		float amount = event.getAmount();

		// 处理三级沉默
		if (source.isMagicDamage()) {
			Entity srcEntity = source.getTrueSource();
			if (srcEntity != null) {
				if (ESAPI.silent.isSilent(srcEntity, SilentLevel.PHENOMENON)) {
					event.setCanceled(true);
					return;
				}
			}
			if (ESAPI.silent.isSilent(entity.world, entity.getPositionEyes(0), SilentLevel.PHENOMENON)) {
				event.setCanceled(true);
				return;
			}
		}

		if (PotionEnderization.tryAttackEntityFrom(entity, source, amount)) {
			event.setCanceled(true);
			return;
		}

		Entity attackerEntity = source.getTrueSource();
		if (attackerEntity instanceof EntityLivingBase) {
			EntityLivingBase attacker = (EntityLivingBase) attackerEntity;
			if (PotionEndercorps.tryAttackEntityFrom(entity, attacker, source, amount)) {
				event.setCanceled(true);
				return;
			}
			PotionWindShield.tryAttackEntityFrom(entity, attacker, source, amount);
		}

		if (entity.isPotionActive(ESObjects.POTIONS.GOLD_SHIELD) && amount > 0) {
			int amplifier = entity.getActivePotionEffect(ESObjects.POTIONS.GOLD_SHIELD).getAmplifier() + 1;
			IInventory inv = null;
			if (entity instanceof EntityPlayer) inv = ((EntityPlayer) entity).inventory;
			double coefficient = MantraGoldShield.getValueCoefficient(amplifier);
			double sDmg = MantraGoldShield.findValue(coefficient, amount, inv, false);
			if (amount < sDmg) {
				event.setCanceled(true);
				double remain = sDmg - amount;
				if (remain > 0.1) MantraGoldShield.setReflect(attackerEntity, entity, remain / 2);
				MantraGoldShield.findValue(coefficient, amount, inv, true);
				return;
			}
		}
	}

	// 受到伤害，修改amount
	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event) {
		if (event.isCanceled()) return;

		EntityLivingBase entity = event.getEntityLiving();
		DamageSource source = event.getSource();
		float amount = event.getAmount();

		Entity attackerEntity = source.getTrueSource();
		if (attackerEntity instanceof EntityLivingBase) {
			EntityLivingBase attacker = (EntityLivingBase) attackerEntity;
			float factor = 1;
			if (PotionCombatSkill.canSkill(entity, attacker, source, amount))
				factor = factor + PotionCombatSkill.doSkill(entity, attacker, source, amount);
			if (PotionDefenseSkill.canSkill(entity, attacker, source, amount))
				factor = Math.max(factor - PotionDefenseSkill.doSkill(entity, attacker, source, amount), 0);
			if (factor != 1) event.setAmount(factor * amount);
		}

		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) entity;

			int nightmare = PotionDungeonNightmare.getNightmareValidLevel(player);
			if (nightmare > 1) {
				event.setAmount(event.getAmount() * nightmare);
				int duration = 20 * 60 * 3;
				if (nightmare >= 3) {
					PotionEffect effectSlow = new PotionEffect(MobEffects.SLOWNESS, duration,
							Math.min(nightmare - 3, 3));
					player.addPotionEffect(effectSlow);
					PotionEffect effectWither = new PotionEffect(MobEffects.WITHER, duration,
							Math.min(nightmare - 3, 3));
					player.addPotionEffect(effectWither);
					PotionEffect effectWeakness = new PotionEffect(MobEffects.WEAKNESS, duration,
							Math.min(nightmare - 3, 3));
					player.addPotionEffect(effectWeakness);
				}
			}
		}

	}

	// 受到伤害，在LivingHurtEvent之后，这里根据amount计算数据
	@SubscribeEvent
	public static void onLivingDamage(LivingDamageEvent event) {
		if (event.isCanceled()) return;

		EntityLivingBase entity = event.getEntityLiving();
		DamageSource source = event.getSource();
		float amount = event.getAmount();

		Entity attackerEntity = source.getTrueSource();
		if (attackerEntity instanceof EntityLivingBase) {
			EntityLivingBase attacker = (EntityLivingBase) attackerEntity;
			PotionHealthBalance.tryBalance(entity, attacker, source, amount);

			PotionEffect effect = entity.getActivePotionEffect(ESObjects.POTIONS.ELEMENT_CRACK_ATTACK);
			if (effect != null) event.setAmount(amount * (1 + effect.getAmplifier() * effect.getAmplifier() * 0.1f));
		}
	}

	// 添加buff
	@SubscribeEvent
	public static void onPotionApplicable(PotionApplicableEvent event) {
		PotionEffect effect = event.getPotionEffect();
		if (effect == null) return;
		EntityLivingBase entity = event.getEntityLiving();

		Potion potion = effect.getPotion();
		float factor = 0;
		if (entity.isPotionActive(ESObjects.POTIONS.CALAMITY)) {
			int amplifier = entity.getActivePotionEffect(ESObjects.POTIONS.CALAMITY).getAmplifier() + 1;
			factor = factor + (potion.isBadEffect() ? (0.4f * amplifier) : (-0.15f * amplifier));
		}
		if (entity.isPotionActive(ESObjects.POTIONS.BLESSING) && potion != ESObjects.POTIONS.BLESSING) {
			int amplifier = entity.getActivePotionEffect(ESObjects.POTIONS.BLESSING).getAmplifier() + 1;
			factor = factor + (potion.isBadEffect() ? (-0.075f * amplifier) : (0.125f * amplifier));
		}
		if (factor != 0) {
			int duration = MathHelper.clamp((int) (effect.getDuration() * (1 + factor)), 1, Short.MAX_VALUE);
			EntityHelper.setPotionEffectDuration(effect, Math.max(duration, effect.getDuration()));
		}
	}

	// 跳跃
	@SubscribeEvent
	public static void onLivingJump(LivingJumpEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (entity instanceof EntityPlayer) {
			BlockPos pos = new BlockPos(entity.posX, entity.posY - 0.1, entity.posZ);
			IBlockState state = entity.world.getBlockState(pos);
			Block block = state.getBlock();
			if (block instanceof IBlockJumpModify)
				((IBlockJumpModify) block).onPlayerJump(entity.world, pos, state, entity);
		}
	}

	// 钓鱼
	@SubscribeEvent
	public static void onFished(ItemFishedEvent event) {
		if (event.isCanceled()) return;
		EntityPlayer player = event.getEntityPlayer();
		ItemBlessingJadePiece.onFished(player, event.getHookEntity());
	}

	@SubscribeEvent
	public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
		World world = event.getWorld();
		if (world.isRemote) return;
		if (PocketWatch.isActive(world)) {
			event.setCanceled(true);
			return;
		}
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent evt) {
		IAdventurer adventurer = evt.player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
		if (adventurer != null) adventurer.onUpdate(evt.player);
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

	@SubscribeEvent
	public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
		EntityLivingBase living = event.getEntityLiving();
		World world = living.world;
		if (ESAPI.silent.isSilent(living, SilentLevel.PHENOMENON)) {
			for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
				ItemStack stack = living.getItemStackFromSlot(slot);
				if (!stack.isEmpty() && stack.getTagCompound() != null) stack.getTagCompound().removeTag("ench");
			}
		}
	}

	public static boolean allowInDungeonTeleport = false;

	@SubscribeEvent
	public static void onEnderTeleport(EnderTeleportEvent event) {
		Entity entity = event.getEntity();
		if (event.isCanceled()) return;

		if (ESAPI.silent.isSilent(entity, SilentLevel.RELEASE)) {
			event.setResult(Result.DENY);
			event.setCanceled(true);
			return;
		}

		if (event.getEntityLiving() instanceof EntityPlayerMP) toNext: {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			DungeonWorld dw = DungeonWorld.getDungeonWorld(player.world);
			Vec3d targetVec = new Vec3d(event.getTargetX() + 0.5, event.getTargetY() + 0.5, event.getTargetZ() + 0.5);
			DungeonAreaRoom room = dw.getAreaRoom(player.getPosition());
			boolean inRoom = room != null && room.isBuild();
			if (inRoom) {
				if (allowInDungeonTeleport) {
					AxisAlignedBB aabb = room.getBox();
					if (aabb.contains(targetVec)) break toNext;
				}
				player.sendMessage(new TextComponentTranslation(
						"info.dungeon.cannot.teleport").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE)));
				event.setResult(Result.DENY);
				event.setCanceled(true);
				return;
			} else {
				room = dw.getAreaRoom(new BlockPos(targetVec));
				inRoom = room != null && room.isBuild();
				if (inRoom) {
					player.sendMessage(new TextComponentTranslation(
							"info.dungeon.cannot.teleport").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE)));
					event.setResult(Result.DENY);
					event.setCanceled(true);
					return;
				}
			}
		}

	}

	@SubscribeEvent
	public static void onLivingHeal(LivingHealEvent event) {
		if (event.isCanceled()) return;
		EntityLivingBase entity = event.getEntityLiving();

		PotionEffect deathWatch = entity.getActivePotionEffect(ESObjects.POTIONS.DEATH_WATCH);
		if (deathWatch != null) {
			event.setCanceled(true);
			event.setResult(Result.DENY);
			return;
		}

		PotionEffect frozen = entity.getActivePotionEffect(ESObjects.POTIONS.FROZEN);
		if (frozen != null) {
			int amplifier = frozen.getAmplifier();
			event.setAmount(Math.max(0, event.getAmount() * (1 + (amplifier + 1) * PotionFrozen.FACTOR)));
		}
	}

	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load event) {
		World world = event.getWorld();
		if (world instanceof WorldServer) world.addEventListener(new ESWorldEventListener());
	}

	@SubscribeEvent
	public static void onPlayerHarvestCheck(
			net.minecraftforge.event.entity.player.PlayerEvent.HarvestCheck harvestCheck) {

	}

	// 玩家死亡等，复制玩家数据
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

		if (!event.isWasDeath()) return;

		PotionEffect effect = origin.getActivePotionEffect(ESObjects.POTIONS.DUNGEON_NIGHTMARE);
		if (effect != null) {
			PotionEffect newEffect = new PotionEffect(effect.getPotion(), effect.getDuration(), effect.getAmplifier());
			player.addPotionEffect(newEffect);
		}
	}

	@SubscribeEvent
	public static void onPlayerRespawn(PlayerRespawnEvent event) {
		EntityPlayer player = event.player;
		World world = player.getEntityWorld();
		if (world.isRemote) return;

		boolean inDungeon = PotionDungeonNightmare.inDungeon(player);

		PotionEffect effect = player.getActivePotionEffect(ESObjects.POTIONS.DUNGEON_NIGHTMARE);
		if (inDungeon) {
			final int defaultDuration = 20 * 60 * 3;
			if (effect != null) {
				int amplifier = Math.min(effect.getAmplifier() + 1, 32);
				int duration = Math.min(0x7fff, effect.getDuration() + defaultDuration);
				effect = new PotionEffect(ESObjects.POTIONS.DUNGEON_NIGHTMARE, duration, amplifier);
			} else effect = new PotionEffect(ESObjects.POTIONS.DUNGEON_NIGHTMARE, defaultDuration, 0);
		}

		if (effect != null) {
			player.removeActivePotionEffect(effect.getPotion());
			final PotionEffect newEffect = effect;
			addWorldTask(player.getEntityWorld(), (w) -> {
				player.addPotionEffect(newEffect);
			});
		}
	}

	@SubscribeEvent
	public static void onPlayerSleepInBedEvent(PlayerSleepInBedEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		World world = player.getEntityWorld();
		if (world.isRemote) return;

		boolean inDungeon = PotionDungeonNightmare.inDungeon(player);
		if (inDungeon) {
			BlockPos currPos = player.getBedLocation();
			BlockPos bedPos = event.getPos();
			if (bedPos == null || !bedPos.equals(currPos)) {
				((EntityPlayerMP) (player)).sendMessage(new TextComponentTranslation(
						"info.dungeon.setSpawn").setStyle(new Style().setColor(TextFormatting.YELLOW)));
			}
		}
	}

	@SubscribeEvent
	public static void onAttachCapabilities(AttachCapabilitiesEvent<?> event) {
//		if (ItemStack.class == event.getGenericType()) {
//			System.out.println("??");
//		}
	}

}
