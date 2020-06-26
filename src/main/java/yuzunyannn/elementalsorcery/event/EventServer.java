package yuzunyannn.elementalsorcery.event;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import yuzunyannn.elementalsorcery.building.BuildingLib;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.item.ItemScroll;
import yuzunyannn.elementalsorcery.parchment.Pages;

public class EventServer {

	@SubscribeEvent
	public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
		EntityPlayer player = event.player;
		NBTTagCompound data = player.getEntityData();
		if (!data.hasKey("esFirstJoin")) {
			data.setBoolean("esFirstJoin", true);
			player.inventory.addItemStackToInventory(ItemScroll.getScroll("rite"));
			//player.inventory.addItemStackToInventory(new ItemStack(ESInitInstance.BLOCKS.STELA));
		}
	}

	static private final List<ITickTask> tickList = new LinkedList<ITickTask>();

	/** 添加一个服务端的tick任务 */
	static public void addTickTask(ITickTask task) {
		if (task == null)
			return;
		tickList.add(task);
	}

	@SubscribeEvent
	public static void serverTick(TickEvent.ServerTickEvent event) {
		Iterator<ITickTask> iter = tickList.iterator();
		while (iter.hasNext()) {
			ITickTask task = iter.next();
			int flags = task.onTick();
			if (flags == ITickTask.END)
				iter.remove();
		}
	}

	@SubscribeEvent
	public static void gameSave(WorldEvent.Save e) {
		BuildingLib.instance.dealSave();
	}

}
