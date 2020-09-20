package yuzunyannn.elementalsorcery.event;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.building.BuildingLib;
import yuzunyannn.elementalsorcery.capability.Adventurer;
import yuzunyannn.elementalsorcery.item.ItemScroll;

public class EventServer {

	public static NBTTagCompound getPlayerNBT(EntityLivingBase player) {
		NBTTagCompound data = player.getEntityData();
		if (data.hasKey("ESData", 10)) return data.getCompoundTag("ESData");
		NBTTagCompound nbt = new NBTTagCompound();
		data.setTag("ESData", nbt);
		return nbt;
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
	}

	@SubscribeEvent
	public static void onLogoff(PlayerEvent.PlayerLoggedOutEvent event) {
		EntityPlayer player = event.player;
		ElementalSorcery.removePlayerData(player);
	}

	@SubscribeEvent
	public static void asAdventurer(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityPlayer) {
			event.addCapability(new ResourceLocation(ElementalSorcery.MODID, "adventurer"), new Adventurer.Provider());
		}
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
	}

}
