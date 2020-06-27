package yuzunyannn.elementalsorcery.event;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import yuzunyannn.elementalsorcery.building.BuildingLib;
import yuzunyannn.elementalsorcery.item.ItemScroll;

public class EventServer {

	public static NBTTagCompound getPlayerNBT(EntityPlayer player) {
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

	static private final List<ITickTask> tickList = new LinkedList<ITickTask>();

	/** 添加一个服务端的tick任务 */
	static public void addTickTask(ITickTask task) {
		if (task == null) return;
		tickList.add(task);
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
