package yuzunyannn.elementalsorcery.ts;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.entity.EntityGrimoire;
import yuzunyannn.elementalsorcery.network.ESNetwork;
import yuzunyannn.elementalsorcery.network.MessagePocketWatch;

public class PocketWatch {

	public static boolean disable = false;

	static class EntityStopData {
		public int ticksExisted;
		public float limbSwingAmount;
	}

	static class WorldData {
		final public WeakReference<World> worldRef;
		public int remain;
		public int totalRemain;
		public WeakReference<EntityLivingBase> caster;
		public HashMap<Integer, EntityStopData> entityMap = new HashMap<>();
		public Set<TileEntity> tickableTileEntities = new HashSet<>();

		public WorldData(World world, int remain) {
			this.worldRef = new WeakReference<>(world);
			this.totalRemain = this.remain = remain;
		}

		public World getWorld() {
			return worldRef.get();
		}

		public WorldData setCaster(@Nullable EntityLivingBase entity) {
			if (entity == null) caster = null;
			caster = new WeakReference<>(entity);
			return this;
		}

		public EntityLivingBase getCaster() {
			EntityLivingBase entity = caster == null ? null : caster.get();
			if (entity == null) return null;
			if (entity.isDead) return null;
			return entity;
		}

	}

	static private Map<Integer, WorldData> map = new TreeMap<>();

	static public boolean isActive(World world) {
		if (PocketWatch.disable) return false;
		return map.containsKey(world.provider.getDimension());
	}

	/** Time Stop! */
	static public void stopWorld(World world, int tick, @Nullable EntityLivingBase caster) {
		if (disable) return;
		if (isActive(world)) return;
		WorldData data = new WorldData(world, tick).setCaster(caster);
		map.put(world.provider.getDimension(), data);
		doStopAll(world, data);
		for (EntityPlayer player : world.playerEntities) sendPlayStopWord(player);
	}

	static public void sendPlayStopWord(EntityPlayer player) {
		if (PocketWatch.disable) return;
		World world = player.world;
		if (world.isRemote) return;
		WorldData data = map.get(world.provider.getDimension());
		if (data == null) return;

		int wordId = world.provider.getDimension();
		ESNetwork.instance.sendTo(new MessagePocketWatch(world, data.remain), (EntityPlayerMP) player);
	}

	static public void resumeWorld(World world) {
		WorldData data = map.get(world.provider.getDimension());
		map.remove(world.provider.getDimension());
		World cworld = data.getWorld();
		if (cworld != world) return;
		doResumeAll(world, data);
	}

	static public void tick() {
		if (PocketWatch.disable) return;
		if (map.isEmpty()) return;
		Iterator<Entry<Integer, WorldData>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Integer, WorldData> entry = iter.next();
			WorldData data = entry.getValue();
			World world = data.getWorld();
			if (world == null) {
				iter.remove();
				continue;
			}
			if (--data.remain <= 0) {
				doResumeAll(world, data);
				iter.remove();
			} else tickStopAll(world, data);

		}
	}

	static public void doStopAll(World world, WorldData data) {
		world.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
		world.getGameRules().setOrCreateGameRule("randomTickSpeed", "0");
		world.getGameRules().setOrCreateGameRule("doFireTick", "false");
		data.tickableTileEntities.clear();
	}

	static public void doResumeAll(World world, WorldData data) {
		world.getGameRules().setOrCreateGameRule("doDaylightCycle", "true");
		world.getGameRules().setOrCreateGameRule("randomTickSpeed", "3");
		world.getGameRules().setOrCreateGameRule("doFireTick", "true");
		blockAllEntities(world, data, false);

		for (TileEntity tile : data.tickableTileEntities) {
			if (world.loadedTileEntityList.contains(tile)) world.tickableTileEntities.add(tile);
		}
		data.tickableTileEntities.clear();
	}

	static public void tickStopAll(World world, WorldData data) {
		blockAllEntities(world, data, true);
		// tile entity
		if (!world.tickableTileEntities.isEmpty()) {
			for (TileEntity tile : world.tickableTileEntities) data.tickableTileEntities.add(tile);
			world.tickableTileEntities.clear();
		}
	}

	static public void blockAllEntities(World world, WorldData data, boolean block) {
//		EntityLivingBase caster = data.getCaster();
		List<Entity> entities = world.loadedEntityList;
		for (Entity entity : entities) {
			entity.timeUntilPortal = 20;
			boolean notBlock = entity instanceof EntityPlayer;
			if (!notBlock && entity instanceof EntityGrimoire) {
				EntityGrimoire grimoire = (EntityGrimoire) entity;
				notBlock = grimoire.getState() != EntityGrimoire.STATE_AFTER_SPELLING;
			}

			EntityStopData stopData;
			if (data.entityMap.containsKey(entity.getEntityId())) stopData = data.entityMap.get(entity.getEntityId());
			else {
				stopData = new EntityStopData();
				stopData.ticksExisted = entity.ticksExisted;
				if (entity instanceof EntityLivingBase) {
					EntityLivingBase living = (EntityLivingBase) entity;
					stopData.limbSwingAmount = living.limbSwingAmount;
				}
				data.entityMap.put(entity.getEntityId(), stopData);
			}

			entity.ticksExisted = stopData.ticksExisted;

			if (notBlock) {
				entity.updateBlocked = false;
				continue;
			}
			entity.updateBlocked = block;
			entity.hurtResistantTime = 0;
			entity.prevRotationPitch = entity.rotationPitch;
			entity.prevRotationYaw = entity.rotationYaw;
			entity.prevPosY = entity.posY;
			entity.prevPosX = entity.posX;
			entity.prevPosZ = entity.posZ;
			entity.prevDistanceWalkedModified = entity.distanceWalkedModified;
			if (entity instanceof EntityLivingBase) {
				EntityLivingBase living = (EntityLivingBase) entity;
				living.prevLimbSwingAmount = living.limbSwingAmount = stopData.limbSwingAmount;
				living.prevCameraPitch = living.cameraPitch;
				living.prevRotationYawHead = living.rotationYawHead;
				living.prevRenderYawOffset = living.renderYawOffset;
				living.prevSwingProgress = living.swingProgress;
			}
		}
	}

}
