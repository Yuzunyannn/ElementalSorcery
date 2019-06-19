package yuzunyan.elementalsorcery.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import yuzunyan.elementalsorcery.init.ESInitInstance;
import yuzunyan.elementalsorcery.item.ItemScroll;
import yuzunyan.elementalsorcery.parchment.Pages;

public class EventServer {

	@SubscribeEvent
	public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
		EntityPlayer player = event.player;
		NBTTagCompound data = player.getEntityData();
		if (!data.hasKey("es_first_join")) {
			data.setBoolean("es_first_join", true);
			player.inventory.addItemStackToInventory(ItemScroll.getScroll(Pages.ABOUT_ELEMENT, Pages.ABOUT_STELA));
			player.inventory.addItemStackToInventory(new ItemStack(ESInitInstance.BLOCKS.STELA));
		}
	}
}
