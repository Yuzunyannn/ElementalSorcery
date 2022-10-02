package yuzunyannn.elementalsorcery.event;

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
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
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
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import yuzunyannn.elementalsorcery.ESData;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.entity.BehaviorAttack;
import yuzunyannn.elementalsorcery.api.entity.BehaviorBlock;
import yuzunyannn.elementalsorcery.api.entity.BehaviorClick;
import yuzunyannn.elementalsorcery.api.entity.BehaviorInteract;
import yuzunyannn.elementalsorcery.api.mantra.SilentLevel;
import yuzunyannn.elementalsorcery.api.tile.IBlockJumpModify;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.building.BuildingLib;
import yuzunyannn.elementalsorcery.capability.Adventurer;
import yuzunyannn.elementalsorcery.capability.ESPlayerCapabilityProvider;
import yuzunyannn.elementalsorcery.config.ESConfig;
import yuzunyannn.elementalsorcery.elf.ElfPostOffice;
import yuzunyannn.elementalsorcery.elf.quest.IAdventurer;
import yuzunyannn.elementalsorcery.elf.research.Researcher;
import yuzunyannn.elementalsorcery.enchant.EnchantmentES;
import yuzunyannn.elementalsorcery.entity.fcube.EntityFairyCube;
import yuzunyannn.elementalsorcery.entity.fcube.FCMAttack;
import yuzunyannn.elementalsorcery.item.IItemStronger;
import yuzunyannn.elementalsorcery.item.ItemManual;
import yuzunyannn.elementalsorcery.item.prop.ItemBlessingJadePiece;
import yuzunyannn.elementalsorcery.network.ESNetwork;
import yuzunyannn.elementalsorcery.network.MessageSyncConfig;
import yuzunyannn.elementalsorcery.potion.PotionBlessing;
import yuzunyannn.elementalsorcery.potion.PotionCalamity;
import yuzunyannn.elementalsorcery.potion.PotionCombatSkill;
import yuzunyannn.elementalsorcery.potion.PotionDefenseSkill;
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
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.helper.ExceptionHelper;
import yuzunyannn.elementalsorcery.util.helper.SilentWorld;

public class EventServer {

	public static NBTTagCompound getPlayerNBT(EntityLivingBase player) {
		NBTTagCompound data = player.getEntityData();
		if (data.hasKey("ESData", 10)) return data.getCompoundTag("ESData");
		NBTTagCompound nbt = new NBTTagCompound();
		data.setTag("ESData", nbt);
		return nbt;
	}

	static private final List<ITickTask> tickList = new LinkedList<ITickTask>();
	static private final Map<Integer, List<IWorldTickTask>> worldTickMapList = new HashMap<>();
	static private final Map<Integer, List<IWorldTickTask>> worldTickMapListCache = new HashMap<>();
	static private boolean isRunningWorldTick = false;

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
		if (event.player instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.player;

			NBTTagCompound data = EventServer.getPlayerNBT(player);
			if (!data.hasKey("esFirstJoin")) {
				if (player.inventory == null) return;
				data.setBoolean("esFirstJoin", true);
				NBTTagList list = new NBTTagList();
				list.appendTag(new NBTTagString("rite"));
				player.inventory
						.addItemStackToInventory(ItemManual.setIds(new ItemStack(ESObjects.ITEMS.MANUAL), list));
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
		if (event.getObject() instanceof EntityPlayer) {
			event.addCapability(new ResourceLocation(ESAPI.MODID, "capability"), new ESPlayerCapabilityProvider());
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

	// 掉落
	@SubscribeEvent
	public static void onLooting(LootingLevelEvent event) {
		DamageSource ds = event.getDamageSource();
		Entity entity = ds.getImmediateSource();
		if (entity instanceof EntityFairyCube) {
			double plunder = FCMAttack.getPlunder((EntityFairyCube) entity);
			event.setLootingLevel(Math.max((int) plunder, event.getLootingLevel()));
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

		if (event.isCanceled()) return;
		onLivingDeadEnchantmentDeal(deader, source);

		Entity trueSource = source.getTrueSource();
		if (trueSource instanceof EntityLivingBase) {
			EntityLivingBase living = ((EntityLivingBase) trueSource);
			ItemStack held = living.getHeldItemMainhand();
			Item item = held.getItem();
			if (item instanceof IItemStronger) ((IItemStronger) item).onKillEntity(trueSource.world, held, deader,
					(EntityLivingBase) trueSource, source);
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

	@SubscribeEvent
	public static void onEnderTeleport(EnderTeleportEvent event) {
		Entity entity = event.getEntity();
		if (ESAPI.silent.isSilent(entity, SilentLevel.RELEASE)) {
			event.setResult(Result.DENY);
			event.setCanceled(true);
			return;
		}
	}

	@SubscribeEvent
	public static void onLivingHeal(LivingHealEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		PotionEffect frozen = entity.getActivePotionEffect(ESObjects.POTIONS.FROZEN);
		if (frozen != null) {
			int amplifier = frozen.getAmplifier();
			event.setAmount(Math.max(0, event.getAmount() * (1 + (amplifier + 1) * PotionFrozen.FACTOR)));
		}
	}

}
